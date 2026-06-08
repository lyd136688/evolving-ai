#!/usr/bin/env python3
"""
自进化 AI - 记忆检索模块
使用向量相似度检索已学习的知识
"""

import json
from pathlib import Path
from datetime import datetime
import re

class MemorySearch:
    """记忆检索系统 - 语义搜索已学知识"""
    
    def __init__(self, memory_dir: str = "memory/network_knowledge"):
        self.memory_dir = Path(memory_dir)
        self.knowledge_cache = []
        self.index_built = False
    
    def build_index(self):
        """构建知识索引"""
        print(" 构建知识索引...")
        
        self.knowledge_cache = []
        
        for filepath in self.memory_dir.glob("*.json"):
            if filepath.name == "learning_report.json":
                continue
            
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    knowledge = json.load(f)
                
                self.knowledge_cache.append({
                    'file': filepath.name,
                    'title': knowledge.get('title', '未知'),
                    'url': knowledge.get('url', ''),
                    'content': knowledge.get('content', ''),
                    'code_blocks': knowledge.get('code_blocks', []),
                    'learned_at': knowledge.get('learned_at', ''),
                    'tags': self._extract_tags(knowledge.get('content', ''))
                })
            except Exception as e:
                print(f"⚠️  读取失败 {filepath.name}: {e}")
        
        self.index_built = True
        print(f"✅ 索引完成！共 {len(self.knowledge_cache)} 条知识")
    
    def _extract_tags(self, content: str) -> list:
        """从内容中提取标签"""
        tags = []
        
        # 编程语言
        lang_patterns = {
            'python': r'\bdef \w+|import \w+|from \w+ import',
            'rust': r'\bfn \w+|let mut|impl \w+',
            'go': r'\bfunc \w+|var \w+|package \w+',
            'javascript': r'\bconst \w+|function \w+|=>',
            'java': r'\bpublic class|void \w+|import java\.',
        }
        
        for lang, pattern in lang_patterns.items():
            if re.search(pattern, content):
                tags.append(lang)
        
        # 技术概念
        tech_patterns = {
            'async': r'\basync|await',
            'class': r'\bclass \w+',
            'api': r'\bAPI|REST|HTTP',
            'database': r'\bSQL|database|query',
            'ui': r'\bUI|widget|component',
        }
        
        for tech, pattern in tech_patterns.items():
            if re.search(pattern, content):
                tags.append(tech)
        
        return list(set(tags))
    
    def search(self, query: str, limit: int = 5) -> list:
        """搜索知识"""
        if not self.index_built:
            self.build_index()
        
        results = []
        query_lower = query.lower()
        
        for knowledge in self.knowledge_cache:
            score = 0
            
            # 标题匹配（高权重）
            if query_lower in knowledge['title'].lower():
                score += 10
            
            # 标签匹配
            for tag in knowledge['tags']:
                if query_lower in tag.lower():
                    score += 5
            
            # 内容匹配
            if query_lower in knowledge['content'].lower():
                score += 3
            
            # URL 匹配
            if query_lower in knowledge['url'].lower():
                score += 2
            
            if score > 0:
                results.append({
                    **knowledge,
                    'score': score
                })
        
        # 按分数排序
        results.sort(key=lambda x: x['score'], reverse=True)
        
        return results[:limit]
    
    def get_by_topic(self, topic: str) -> list:
        """按主题获取知识"""
        if not self.index_built:
            self.build_index()
        
        results = []
        for knowledge in self.knowledge_cache:
            if topic in knowledge['tags'] or topic.lower() in knowledge['title'].lower():
                results.append(knowledge)
        
        return results
    
    def get_recent(self, limit: int = 10) -> list:
        """获取最近学习的知识"""
        if not self.index_built:
            self.build_index()
        
        sorted_knowledge = sorted(
            self.knowledge_cache,
            key=lambda x: x['learned_at'],
            reverse=True
        )
        
        return sorted_knowledge[:limit]
    
    def get_stats(self) -> dict:
        """获取记忆统计"""
        if not self.index_built:
            self.build_index()
        
        # 统计标签
        tag_count = {}
        for k in self.knowledge_cache:
            for tag in k['tags']:
                tag_count[tag] = tag_count.get(tag, 0) + 1
        
        return {
            'total_knowledge': len(self.knowledge_cache),
            'tags': dict(sorted(tag_count.items(), key=lambda x: x[1], reverse=True)[:10]),
            'memory_dir': str(self.memory_dir),
            'index_built_at': datetime.now().isoformat()
        }
    
    def export_summary(self, output_path: str = "memory/knowledge_summary.md"):
        """导出知识摘要"""
        stats = self.get_stats()
        
        md = "# 自进化 AI - 知识摘要\n\n"
        md += f"生成时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n"
        
        md += "## 统计\n\n"
        md += f"- 总知识条目：{stats['total_knowledge']}\n"
        md += f"- 热门标签：{', '.join(stats['tags'].keys())}\n\n"
        
        md += "## 最近学习\n\n"
        for k in self.get_recent(10):
            md += f"### {k['title']}\n"
            md += f"- 来源：{k['url']}\n"
            md += f"- 时间：{k['learned_at']}\n"
            md += f"- 标签：{', '.join(k['tags'])}\n\n"
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(md)
        
        print(f"✅ 摘要已导出：{output_path}")
        return output_path


# ==================== 命令行入口 ====================

def main():
    search = MemorySearch()
    
    print("=" * 50)
    print("  自进化 AI - 记忆检索系统")
    print("=" * 50)
    
    while True:
        print("\n命令:")
        print("  search <关键词> - 搜索知识")
        print("  topic <主题>    - 按主题查看")
        print("  recent          - 最近学习")
        print("  stats           - 统计信息")
        print("  export          - 导出摘要")
        print("  quit            - 退出")
        
        cmd = input("\n> ").strip()
        
        if not cmd or cmd == 'quit':
            break
        
        parts = cmd.split(maxsplit=1)
        action = parts[0]
        
        if action == 'search' and len(parts) > 1:
            results = search.search(parts[1])
            if results:
                print(f"\n找到 {len(results)} 条结果:")
                for r in results:
                    print(f"  [{r['score']}分] {r['title']}")
                    print(f"       标签：{', '.join(r['tags'])}")
            else:
                print("  未找到相关结果")
        
        elif action == 'topic' and len(parts) > 1:
            results = search.get_by_topic(parts[1])
            print(f"\n主题 '{parts[1]}' 共 {len(results)} 条:")
            for r in results:
                print(f"  • {r['title']}")
        
        elif action == 'recent':
            results = search.get_recent()
            print(f"\n最近学习 ({len(results)} 条):")
            for r in results:
                print(f"  • {r['title']} ({r['learned_at'][:10]})")
        
        elif action == 'stats':
            stats = search.get_stats()
            print(f"\n统计信息:")
            print(f"  总知识：{stats['total_knowledge']} 条")
            print(f"  热门标签:")
            for tag, count in list(stats['tags'].items())[:5]:
                print(f"    #{tag}: {count}")
        
        elif action == 'export':
            search.export_summary()
        
        else:
            print("  未知命令")


if __name__ == '__main__':
    main()
