#!/usr/bin/env python3
"""
感知层 - 负责从外部世界获取信息
Perception Layer - Acquire information from external world
"""

import aiohttp
import asyncio
from typing import Dict, List, Any, Optional
from datetime import datetime
from pathlib import Path


class PerceptionManager:
    """感知层管理器"""
    
    def __init__(self, config: Dict):
        self.config = config
        self.session: Optional[aiohttp.ClientSession] = None
        self.trusted_sources = config.get("learning", {}).get("trusted_sources", [])
    
    async def initialize(self):
        """初始化 HTTP 会话"""
        headers = {
            "User-Agent": "SelfEvolvingAI/2.0 (Autonomous Learning System)"
        }
        self.session = aiohttp.ClientSession(headers=headers)
    
    async def close(self):
        """关闭 HTTP 会话"""
        if self.session:
            await self.session.close()
    
    async def learn_from_web(self, topic: str) -> List[Dict]:
        """
        从网络学习指定主题
        
        Args:
            topic: 学习主题
        
        Returns:
            学习到的知识列表
        """
        knowledge = []
        
        # 搜索策略
        search_queries = [
            f"{topic} tutorial",
            f"{topic} best practices",
            f"{topic} documentation",
            f"{topic} examples"
        ]
        
        for query in search_queries:
            try:
                results = await self._search_web(query)
                for result in results[:3]:  # 每个查询取前 3 个结果
                    content = await self._fetch_content(result["url"])
                    if content:
                        knowledge.append({
                            "source": result["url"],
                            "title": result.get("title", ""),
                            "content": content[:5000],  # 限制长度
                            "relevance": result.get("relevance", 0.5),
                            "learned_at": datetime.now().isoformat()
                        })
            except Exception as e:
                print(f"搜索 {query} 时出错：{e}")
        
        return knowledge
    
    async def _search_web(self, query: str) -> List[Dict]:
        """
        执行网络搜索
        
        注意：实际实现需要集成搜索 API（如 Google Custom Search、Bing API 等）
        这里使用模拟实现
        """
        # TODO: 集成真实搜索 API
        # 目前返回模拟结果
        return [
            {
                "url": f"https://example.com/{query.replace(' ', '-')}",
                "title": f"关于 {query} 的教程",
                "relevance": 0.8
            }
        ]
    
    async def _fetch_content(self, url: str) -> Optional[str]:
        """获取网页内容"""
        # 检查是否是可信源
        is_trusted = any(source in url for source in self.trusted_sources)
        if not is_trusted:
            print(f"跳过非可信源：{url}")
            return None
        
        try:
            async with self.session.get(url, timeout=30) as response:
                if response.status == 200:
                    html = await response.text()
                    # 简单的 HTML 清理
                    content = self._extract_text_from_html(html)
                    return content
        except Exception as e:
            print(f"获取 {url} 失败：{e}")
        
        return None
    
    def _extract_text_from_html(self, html: str) -> str:
        """从 HTML 提取文本内容"""
        # 简化实现，实际应使用 BeautifulSoup 等库
        import re
        # 移除 script 和 style 标签
        text = re.sub(r'<script[^>]*>.*?</script>', '', html, flags=re.DOTALL)
        text = re.sub(r'<style[^>]*>.*?</style>', '', text, flags=re.DOTALL)
        # 移除 HTML 标签
        text = re.sub(r'<[^>]+>', ' ', text)
        # 清理空白
        text = ' '.join(text.split())
        return text
    
    async def search_language_resources(self, language: str) -> List[Dict]:
        """搜索编程语言学习资源"""
        resources = []
        
        # 官方文档
        official_docs = await self._find_official_docs(language)
        if official_docs:
            resources.append({
                "type": "official_documentation",
                "url": official_docs,
                "priority": 1
            })
        
        # 教程资源
        tutorials = await self._search_web(f"{language} programming tutorial")
        for t in tutorials[:5]:
            resources.append({
                "type": "tutorial",
                "url": t["url"],
                "title": t["title"],
                "priority": 2
            })
        
        # 代码示例
        examples = await self._search_web(f"{language} code examples github")
        for e in examples[:5]:
            resources.append({
                "type": "code_example",
                "url": e["url"],
                "title": e["title"],
                "priority": 3
            })
        
        return resources
    
    async def _find_official_docs(self, language: str) -> Optional[str]:
        """查找官方文档 URL"""
        official_urls = {
            "python": "https://docs.python.org/3/",
            "rust": "https://doc.rust-lang.org/book/",
            "javascript": "https://developer.mozilla.org/en-US/docs/Web/JavaScript",
            "typescript": "https://www.typescriptlang.org/docs/",
            "go": "https://go.dev/doc/",
            "java": "https://docs.oracle.com/en/java/",
            "cpp": "https://en.cppreference.com/",
            "c": "https://en.cppreference.com/w/c",
        }
        return official_urls.get(language.lower())
    
    async def collect_code_samples(self, language: str) -> List[Dict]:
        """收集代码示例"""
        samples = []
        
        # 搜索 GitHub 代码
        github_samples = await self._search_github_code(language)
        samples.extend(github_samples)
        
        # 搜索 StackOverflow 示例
        so_samples = await self._search_stackoverflow(language)
        samples.extend(so_samples)
        
        return samples
    
    async def _search_github_code(self, language: str) -> List[Dict]:
        """搜索 GitHub 代码"""
        # TODO: 集成 GitHub API
        return [
            {
                "source": "github",
                "language": language,
                "code": f"# {language} 示例代码\nprint('Hello, World!')",
                "description": f"基础 {language} 示例",
                "url": "https://github.com/example"
            }
        ]
    
    async def _search_stackoverflow(self, language: str) -> List[Dict]:
        """搜索 StackOverflow 示例"""
        # TODO: 集成 StackOverflow API
        return []
    
    async def fetch_api_documentation(self, api_name: str) -> Optional[Dict]:
        """获取 API 文档"""
        # 常见 API 文档 URL
        api_docs = {
            "github": "https://docs.github.com/en/rest",
            "twitter": "https://developer.twitter.com/en/docs",
            "google": "https://developers.google.com/apis-explorer",
        }
        
        doc_url = api_docs.get(api_name.lower())
        if doc_url:
            content = await self._fetch_content(doc_url)
            if content:
                return {
                    "api": api_name,
                    "doc_url": doc_url,
                    "content": content,
                    "fetched_at": datetime.now().isoformat()
                }
        
        return None
    
    async def monitor_system(self) -> Dict[str, Any]:
        """监控系统状态"""
        import psutil
        
        return {
            "cpu_percent": psutil.cpu_percent(),
            "memory_percent": psutil.virtual_memory().percent,
            "disk_usage": psutil.disk_usage('/').percent,
            "timestamp": datetime.now().isoformat()
        }
