#!/usr/bin/env python3
"""
自进化 AI - 网络学习模块
能够自主从网络学习新知识并存储到记忆系统
"""

import asyncio
import aiohttp
from bs4 import BeautifulSoup
from pathlib import Path
import json
import hashlib
from datetime import datetime
import re

class NetworkLearner:
    """网络学习器 - 自主从网络获取知识"""
    
    def __init__(self, memory_dir: str = "memory/network_knowledge"):
        self.memory_dir = Path(memory_dir)
        self.memory_dir.mkdir(parents=True, exist_ok=True)
        self.session = None
        self.learning_log = []
        
        # 学习源配置
        self.learning_sources = {
            'python': [
                'https://docs.python.org/3/tutorial/',
                'https://realpython.com/',
            ],
            'rust': [
                'https://doc.rust-lang.org/book/',
                'https://rust-lang.github.io/rust-by-example/',
            ],
            'go': [
                'https://go.dev/tour/',
                'https://gobyexample.com/',
            ],
            'android': [
                'https://developer.android.com/guide',
                'https://android.googlesource.com/',
            ],
            'ai': [
                'https://huggingface.co/docs',
                'https://pytorch.org/docs/',
            ]
        }
    
    async def start(self):
        """启动 HTTP 会话"""
        self.session = aiohttp.ClientSession(
            headers={
                'User-Agent': 'Mozilla/5.0 (SelfEvolvingAI/1.0; +https://github.com/your-username/self-evolving-ai)'
            }
        )
    
    async def stop(self):
        """停止 HTTP 会话"""
        if self.session:
            await self.session.close()
    
    async def fetch_page(self, url: str) -> str:
        """获取网页内容"""
        try:
            async with self.session.get(url, timeout=30) as response:
                if response.status == 200:
                    return await response.text()
                else:
                    print(f"❌ 获取失败：{url} (状态码：{response.status})")
                    return None
        except Exception as e:
            print(f"❌ 请求错误：{url} - {e}")
            return None
    
    def parse_html(self, html: str, url: str) -> dict:
        """解析 HTML 提取知识"""
        soup = BeautifulSoup(html, 'html.parser')
        
        # 提取标题
        title = soup.find('title')
        title = title.text.strip() if title else "未知标题"
        
        # 提取主要内容（移除导航、广告等）
        content_tags = ['article', 'main', 'section']
        content = ""
        for tag in content_tags:
            element = soup.find(tag)
            if element:
                content += element.get_text(separator='\n', strip=True)
        
        # 如果没有找到主要内容，提取所有段落
        if not content:
            paragraphs = soup.find_all('p')
            content = '\n'.join([p.get_text(strip=True) for p in paragraphs[:50]])
        
        # 提取代码块
        code_blocks = []
        for code in soup.find_all(['code', 'pre']):
            code_text = code.get_text(strip=True)
            if len(code_text) > 20:  # 过滤太短的代码
                code_blocks.append(code_text)
        
        # 提取链接（用于后续学习）
        links = []
        for a in soup.find_all('a', href=True):
            href = a['href']
            if href.startswith('http') and len(links) < 20:
                links.append(href)
        
        return {
            'title': title,
            'url': url,
            'content': content[:10000],  # 限制长度
            'code_blocks': code_blocks[:10],
            'related_links': links,
            'learned_at': datetime.now().isoformat(),
            'content_hash': hashlib.md5(content.encode()).hexdigest()
        }
    
    def save_knowledge(self, knowledge: dict):
        """保存知识到记忆系统"""
        # 使用 URL 的 hash 作为文件名
        url_hash = hashlib.md5(knowledge['url'].encode()).hexdigest()[:12]
        filename = f"{url_hash}_{knowledge['title'][:30]}.json"
        filename = "".join(c for c in filename if c not in '<>:"/\\|？*')
        
        filepath = self.memory_dir / filename
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(knowledge, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 已保存：{filepath.name}")
        self.learning_log.append({
            'url': knowledge['url'],
            'title': knowledge['title'],
            'time': knowledge['learned_at']
        })
        
        return filepath
    
    async def learn_topic(self, topic: str, max_pages: int = 5):
        """学习一个主题"""
        print(f"\n🔍 开始学习：{topic}")
        print("=" * 50)
        
        if topic not in self.learning_sources:
            print(f"⚠️  未知主题：{topic}")
            print(f"可用主题：{', '.join(self.learning_sources.keys())}")
            return
        
        sources = self.learning_sources[topic]
        
        for source_url in sources[:max_pages]:
            print(f"\n📖 学习：{source_url}")
            
            html = await self.fetch_page(source_url)
            if not html:
                continue
            
            knowledge = self.parse_html(html, source_url)
            
            # 检查是否已学习过
            existing = list(self.memory_dir.glob(f"*{knowledge['content_hash']}*"))
            if existing:
                print(f"⏭️  已学习过，跳过")
                continue
            
            self.save_knowledge(knowledge)
            
            # 延迟避免请求过快
            await asyncio.sleep(2)
        
        print(f"\n✅ {topic} 学习完成！共学习 {len(self.learning_log)} 个页面")
    
    async def autonomous_learn(self, duration_minutes: int = 30):
        """自主学习的核心循环"""
        print("🧠 启动自主学习模式...")
        print(f"预计运行时间：{duration_minutes} 分钟")
        
        start_time = datetime.now()
        
        while True:
            # 检查是否超时
            elapsed = (datetime.now() - start_time).total_seconds() / 60
            if elapsed >= duration_minutes:
                print(f"\n⏰ 学习时间到！已运行 {elapsed:.1f} 分钟")
                break
            
            # 随机选择一个主题学习
            import random
            topic = random.choice(list(self.learning_sources.keys()))
            
            try:
                await self.learn_topic(topic, max_pages=3)
            except Exception as e:
                print(f"❌ 学习出错：{e}")
            
            # 休息时间
            rest_time = random.randint(60, 300)  # 1-5 分钟
            print(f" 休息 {rest_time} 秒...")
            await asyncio.sleep(rest_time)
        
        # 生成学习报告
        self.generate_learning_report()
    
    def generate_learning_report(self):
        """生成学习报告"""
        report = {
            'total_sessions': len(self.learning_log),
            'topics_learned': list(set([l['url'] for l in self.learning_log])),
            'log': self.learning_log,
            'generated_at': datetime.now().isoformat()
        }
        
        report_path = self.memory_dir / "learning_report.json"
        with open(report_path, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        print(f"\n📊 学习报告已保存：{report_path}")
        print(f"   总学习页面：{len(self.learning_log)}")
    
    def get_learned_knowledge(self, keyword: str = None):
        """检索已学习的知识"""
        results = []
        
        for filepath in self.memory_dir.glob("*.json"):
            if filepath.name == "learning_report.json":
                continue
            
            with open(filepath, 'r', encoding='utf-8') as f:
                knowledge = json.load(f)
            
            if keyword:
                if keyword.lower() in knowledge['content'].lower():
                    results.append(knowledge)
            else:
                results.append(knowledge)
        
        return results


# ==================== 命令行入口 ====================

async def main():
    learner = NetworkLearner()
    
    await learner.start()
    
    try:
        # 学习指定主题
        topic = input("要学习的主题 (python/rust/go/android/ai): ").strip()
        if topic:
            await learner.learn_topic(topic, max_pages=5)
        else:
            # 自主学习模式
            duration = int(input("学习时长（分钟，默认 30）: ").strip() or "30")
            await learner.autonomous_learn(duration)
        
        # 显示学习成果
        print("\n📚 已学习的知识:")
        for item in learner.learning_log:
            print(f"  • {item['title']}")
    
    finally:
        await learner.stop()


if __name__ == '__main__':
    asyncio.run(main())
