#!/usr/bin/env python3
"""
自进化 AI 系统 - Web 手机版
使用 Flask 提供 Web 界面，手机浏览器访问
"""

from flask import Flask, render_template_string, request, jsonify
import asyncio
import sys
from pathlib import Path

app = Flask(__name__)

# 存储对话历史
chat_history = []

HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🧠 自进化 AI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .header {
            background: rgba(255,255,255,0.1);
            backdrop-filter: blur(10px);
            padding: 20px;
            text-align: center;
            color: white;
        }
        .header h1 { font-size: 24px; margin-bottom: 5px; }
        .status { font-size: 14px; opacity: 0.8; }
        .chat-container {
            flex: 1;
            overflow-y: auto;
            padding: 20px;
            max-width: 600px;
            margin: 0 auto;
            width: 100%;
        }
        .message {
            margin-bottom: 15px;
            padding: 12px 16px;
            border-radius: 18px;
            max-width: 80%;
            word-wrap: break-word;
        }
        .message.ai {
            background: white;
            color: #333;
            margin-right: auto;
            border-bottom-left-radius: 4px;
        }
        .message.user {
            background: rgba(255,255,255,0.2);
            color: white;
            margin-left: auto;
            border-bottom-right-radius: 4px;
        }
        .input-area {
            background: white;
            padding: 15px;
            display: flex;
            gap: 10px;
            max-width: 600px;
            margin: 0 auto;
            width: 100%;
        }
        #userInput {
            flex: 1;
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 25px;
            font-size: 16px;
            outline: none;
        }
        #userInput:focus { border-color: #667eea; }
        #sendBtn {
            padding: 12px 24px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 25px;
            font-size: 16px;
            cursor: pointer;
        }
        #sendBtn:disabled { opacity: 0.5; }
        .quick-actions {
            display: flex;
            gap: 10px;
            padding: 10px 20px;
            overflow-x: auto;
            max-width: 600px;
            margin: 0 auto;
            width: 100%;
        }
        .action-btn {
            padding: 8px 16px;
            background: rgba(255,255,255,0.2);
            color: white;
            border: 1px solid rgba(255,255,255,0.3);
            border-radius: 20px;
            font-size: 14px;
            cursor: pointer;
            white-space: nowrap;
        }
        .action-btn:hover { background: rgba(255,255,255,0.3); }
        .typing { opacity: 0.6; font-style: italic; }
    </style>
</head>
<body>
    <div class="header">
        <h1> 自进化 AI 系统</h1>
        <p class="status">状态：就绪 | 版本：1.0.0</p>
    </div>
    
    <div class="chat-container" id="chatContainer">
        <div class="message ai">
            欢迎使用自进化 AI！我可以帮你：<br>
            • 创建功能模块<br>
            • 学习编程语言<br>
            • 分析代码<br>
            • 自我评估优化<br><br>
            请输入你的任务~
        </div>
    </div>
    
    <div class="quick-actions">
        <button class="action-btn" onclick="quickAction('创建一个日志模块')">📝 创建模块</button>
        <button class="action-btn" onclick="quickAction('学习 Go 语言')"> 学习语言</button>
        <button class="action-btn" onclick="quickAction('查看系统状态')">📊 系统状态</button>
        <button class="action-btn" onclick="quickAction('自我评估')">🔍 自我评估</button>
    </div>
    
    <div class="input-area">
        <input type="text" id="userInput" placeholder="输入任务..." onkeypress="handleKeyPress(event)">
        <button id="sendBtn" onclick="sendMessage()">发送</button>
    </div>
    
    <script>
        function quickAction(text) {
            document.getElementById('userInput').value = text;
            sendMessage();
        }
        
        function handleKeyPress(event) {
            if (event.key === 'Enter') sendMessage();
        }
        
        function appendMessage(content, isUser) {
            const container = document.getElementById('chatContainer');
            const div = document.createElement('div');
            div.className = 'message ' + (isUser ? 'user' : 'ai');
            div.innerHTML = content;
            container.appendChild(div);
            container.scrollTop = container.scrollHeight;
        }
        
        async function sendMessage() {
            const input = document.getElementById('userInput');
            const btn = document.getElementById('sendBtn');
            const message = input.value.trim();
            
            if (!message) return;
            
            // 显示用户消息
            appendMessage(message, true);
            input.value = '';
            btn.disabled = true;
            
            // 显示正在输入
            const container = document.getElementById('chatContainer');
            const typing = document.createElement('div');
            typing.className = 'message ai typing';
            typing.id = 'typing';
            typing.innerHTML = '正在思考...';
            container.appendChild(typing);
            container.scrollTop = container.scrollHeight;
            
            try {
                const response = await fetch('/api/chat', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({message: message})
                });
                
                const data = await response.json();
                
                // 移除正在输入
                document.getElementById('typing').remove();
                
                // 显示 AI 回复
                appendMessage(data.response.replace(/\\n/g, '<br>'), false);
            } catch (error) {
                document.getElementById('typing').remove();
                appendMessage('❌ 错误：' + error.message, false);
            }
            
            btn.disabled = false;
            input.focus();
        }
    </script>
</body>
</html>
"""

@app.route('/')
def index():
    return render_template_string(HTML_TEMPLATE)

@app.route('/api/chat', methods=['POST'])
def chat():
    data = request.json
    message = data.get('message', '')
    
    # 简单的响应逻辑（可以连接到自进化 AI 核心）
    response = generate_response(message)
    
    return jsonify({'response': response})

def generate_response(message):
    """生成响应（简化版）"""
    message_lower = message.lower()
    
    if '创建' in message and '模块' in message:
        return "✅ 收到任务！正在创建模块...\n\n1. ✓ 分析需求\n2. ⏳ 生成代码结构\n3. ⏳ 编写实现\n4. ⏳ 添加测试\n\n预计完成时间：2 分钟"
    elif '学习' in message:
        lang = message.replace('学习', '').strip()
        return f"📚 开始学习 {lang}！\n\n我将：\n1. 分析语法特性\n2. 创建示例代码\n3. 生成学习路径\n4. 实践项目\n\n让我们开始吧！"
    elif '状态' in message:
        return "📊 系统状态\n\n• 感知层：就绪\n• 认知层：就绪\n• 执行层：就绪\n• 记忆中心：正常\n• 元认知：活跃\n\n内存使用：正常\n任务队列：空闲"
    elif '评估' in message:
        return "🔍 自我评估报告\n\n优势：\n✓ 代码生成准确率高\n✓ 学习速度快\n✓ 记忆持久化\n\n改进点：\n• 网络学习优化\n• 多语言支持扩展\n\n下次迭代计划：优化推理速度"
    else:
        return f"收到任务：{message}\n\n正在处理中... 我会尽快完成！"

if __name__ == '__main__':
    print("=" * 50)
    print("  🧠 自进化 AI - Web 版")
    print("=" * 50)
    print("\n  启动服务器...")
    print("  访问地址：http://0.0.0.0:5000")
    print("  手机访问：http://<你的IP>:5000")
    print("\n  按 Ctrl+C 停止")
    print("=" * 50)
    
    app.run(host='0.0.0.0', port=5000, debug=False)
