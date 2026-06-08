#!/usr/bin/env python3
"""
自进化 AI 系统 - 安卓手机版
Self-Evolving AI - Mobile App Version
使用 Kivy 框架
"""

import os
import sys
import json
import asyncio
from datetime import datetime
from pathlib import Path

# 添加项目路径
sys.path.insert(0, str(Path(__file__).parent))

from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.textinput import TextInput
from kivy.uix.scrollview import ScrollView
from kivy.uix.popup import Popup
from kivy.clock import Clock
from kivy.properties import StringProperty, ListProperty
from kivy.metrics import dp


# ============== 主界面 ==============

class MainScreen(Screen):
    """主界面"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        
        # 标题
        self.title_label = Label(
            text='🧠 自进化 AI 系统',
            size_hint_y=None,
            height=dp(60),
            font_size=dp(24),
            bold=True
        )
        self.add_widget(self.title_label)
        
        # 状态栏
        self.status_label = Label(
            text='状态：就绪',
            size_hint_y=None,
            height=dp(40),
            halign='left',
            font_size=dp(14)
        )
        self.add_widget(self.status_label)
        
        # 对话显示区
        self.chat_container = BoxLayout(orientation='vertical', padding=dp(10), spacing=dp(5))
        scroll = ScrollView(size_hint=(1, 0.6))
        scroll.add_widget(self.chat_container)
        self.add_widget(scroll)
        
        # 输入区
        input_layout = BoxLayout(orientation='horizontal', size_hint_y=None, height=dp(50), padding=dp(10))
        
        self.input_field = TextInput(
            hint_text='输入任务，例如：创建一个日志分析模块',
            multiline=False,
            size_hint_x=0.7
        )
        self.input_field.bind(on_text_validate=self.on_enter)
        
        send_btn = Button(
            text='发送',
            size_hint_x=0.3,
            on_press=self.send_message
        )
        
        input_layout.add_widget(self.input_field)
        input_layout.add_widget(send_btn)
        self.add_widget(input_layout)
        
        # 功能按钮区
        btn_layout = BoxLayout(orientation='horizontal', size_hint_y=None, height=dp(50), padding=dp(10))
        
        btn_layout.add_widget(Button(
            text='📊 状态',
            on_press=lambda x: self.show_status()
        ))
        btn_layout.add_widget(Button(
            text='🔍 评估',
            on_press=lambda x: self.run_evaluation()
        ))
        btn_layout.add_widget(Button(
            text='📚 学习',
            on_press=lambda x: self.show_learn_popup()
        ))
        
        self.add_widget(btn_layout)
        
        # 添加欢迎消息
        self.add_chat_message('system', '欢迎使用自进化 AI 系统！\n我可以帮你：\n• 创建新功能模块\n• 学习编程语言\n• 分析并改进代码\n\n请输入你的需求...')
    
    def add_chat_message(self, sender, message):
        """添加聊天消息"""
        msg_layout = BoxLayout(orientation='vertical', size_hint_y=None, height=dp(60))
        
        label = Label(
            text=f'[{sender}] {message}',
            size_hint_y=None,
            height=dp(60),
            halign='left' if sender == 'user' else 'right',
            text_size=(self.width - dp(40), None)
        )
        
        msg_layout.add_widget(label)
        self.chat_container.add_widget(msg_layout)
    
    def on_enter(self, instance):
        """回车发送"""
        self.send_message(instance)
    
    def send_message(self, instance):
        """发送消息"""
        text = self.input_field.text.strip()
        if not text:
            return
        
        # 显示用户消息
        self.add_chat_message('你', text)
        self.input_field.text = ''
        
        # 处理消息
        Clock.schedule_once(lambda x: self.process_message(text), 0.5)
    
    def process_message(self, text):
        """处理用户消息"""
        self.status_label.text = '状态：处理中...'
        
        # 简单响应逻辑（实际应调用 AI 系统）
        if text.startswith('task') or '功能' in text or '模块' in text:
            response = self.handle_task(text)
        elif text.startswith('learn') or '学习' in text:
            response = self.handle_learn(text)
        elif '状态' in text:
            response = self.handle_status()
        else:
            response = self.handle_general(text)
        
        self.add_chat_message('AI', response)
        self.status_label.text = '状态：就绪'
    
    def handle_task(self, text):
        """处理任务请求"""
        return f'''收到任务：{text}

正在执行:
1. ✓ 分析需求
2. ⏳ 检索知识
3. ⏳ 生成代码
4. ⏳ 测试验证

预计完成时间：2 分钟'''
    
    def handle_learn(self, text):
        """处理学习请求"""
        return f'''开始学习：{text}

学习步骤:
1. ✓ 搜索学习资源
2. ⏳ 提取核心概念
3. ⏳ 分析代码示例
4. ⏳ 创建支持模块

这将增强我的编程能力！'''
    
    def handle_status(self):
        """处理状态查询"""
        return '''系统状态:
• 知识节点：12
• 技能数量：3
• 经验记录：5
• 模块数量：2
• 系统健康度：85%'''
    
    def handle_general(self, text):
        """处理一般请求"""
        return f'''我收到了：{text}

你可以这样使用我:
• task <任务> - 创建功能
• learn <语言> - 学习编程
• status - 查看状态
• evaluate - 自我评估'''
    
    def show_status(self):
        """显示状态"""
        self.process_message('status')
    
    def run_evaluation(self):
        """运行评估"""
        self.add_chat_message('AI', '''自我评估结果:

知识储备：★★★☆☆ 60%
技能水平：★★★☆☆ 50%
执行能力：★★★☆☆ 45%
学习能力：★★★★☆ 75%
安全性：★★★★★ 95%

总体健康度：65%
建议：继续学习新技能，积累更多经验''')
    
    def show_learn_popup(self):
        """显示学习弹窗"""
        content = BoxLayout(orientation='vertical', padding=dp(20))
        
        content.add_widget(Label(text='想学习什么编程语言？', size_hint_y=None, height=dp(40)))
        
        lang_input = TextInput(hint_text='例如：Rust, Go, JavaScript', multiline=False)
        content.add_widget(lang_input)
        
        btn_layout = BoxLayout(size_hint_y=None, height=dp(50))
        
        start_btn = Button(text='开始学习')
        start_btn.bind(on_press=lambda x: self.start_learning(lang_input.text))
        
        cancel_btn = Button(text='取消')
        cancel_btn.bind(on_press=lambda x: popup.dismiss())
        
        btn_layout.add_widget(start_btn)
        btn_layout.add_widget(cancel_btn)
        content.add_widget(btn_layout)
        
        popup = Popup(title='学习新语言', content=content, size_hint=(0.9, 0.5))
        popup.open()
    
    def start_learning(self, language):
        """开始学习"""
        if language:
            self.process_message(f'learn {language}')


# ============== 应用主类 ==============

class SelfEvolvingAIApp(App):
    """自进化 AI 应用"""
    
    def build(self):
        self.title = '自进化 AI'
        
        # 创建屏幕管理器
        sm = ScreenManager()
        sm.add_widget(MainScreen(name='main'))
        
        return sm
    
    def on_start(self):
        """应用启动时"""
        print("自进化 AI 系统启动...")
        
        # 初始化记忆目录
        memory_dir = Path('/workspace/self-evolving-ai/memory')
        memory_dir.mkdir(parents=True, exist_ok=True)
        
        # 初始化模块目录
        modules_dir = Path('/workspace/self-evolving-ai/modules')
        modules_dir.mkdir(parents=True, exist_ok=True)


# ============== 入口 ==============

if __name__ == '__main__':
    SelfEvolvingAIApp().run()
