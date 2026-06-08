#!/usr/bin/env python3
"""
记忆中心 - 负责存储和管理知识、技能、经验
Memory Center - Store and manage knowledge, skills, experiences
"""

import json
import asyncio
from typing import Dict, List, Any, Optional
from datetime import datetime, timedelta
from pathlib import Path
import aiofiles


class MemoryCenter:
    """记忆中心"""
    
    def __init__(self, memory_dir: Path, config: Dict):
        self.memory_dir = memory_dir
        self.config = config
        
        # 记忆文件路径
        self.knowledge_db_path = memory_dir / "knowledge_graph.json"
        self.skill_library_path = memory_dir / "skill_library.json"
        self.experience_dir = memory_dir / "experience_memory"
        self.code_repo_dir = memory_dir / "code_repository"
        self.state_path = memory_dir / "system_state.json"
        
        # 内存缓存
        self.knowledge_db = {"nodes": [], "edges": []}
        self.skill_library = {"skills": []}
        self.system_state = {}
    
    async def initialize(self):
        """初始化记忆中心"""
        # 确保目录存在
        self.experience_dir.mkdir(parents=True, exist_ok=True)
        (self.experience_dir / "successes").mkdir(exist_ok=True)
        (self.experience_dir / "failures").mkdir(exist_ok=True)
        (self.experience_dir / "insights").mkdir(exist_ok=True)
        
        self.code_repo_dir.mkdir(parents=True, exist_ok=True)
        (self.code_repo_dir / "templates").mkdir(exist_ok=True)
        (self.code_repo_dir / "snippets").mkdir(exist_ok=True)
        (self.code_repo_dir / "modules").mkdir(exist_ok=True)
        
        # 加载已有数据
        await self._load_knowledge_db()
        await self._load_skill_library()
        await self._load_system_state()
    
    async def _load_knowledge_db(self):
        """加载知识图谱"""
        if self.knowledge_db_path.exists():
            async with aiofiles.open(self.knowledge_db_path, 'r', encoding='utf-8') as f:
                content = await f.read()
                self.knowledge_db = json.loads(content)
    
    async def _load_skill_library(self):
        """加载技能库"""
        if self.skill_library_path.exists():
            async with aiofiles.open(self.skill_library_path, 'r', encoding='utf-8') as f:
                content = await f.read()
                self.skill_library = json.loads(content)
    
    async def _load_system_state(self):
        """加载系统状态"""
        if self.state_path.exists():
            async with aiofiles.open(self.state_path, 'r', encoding='utf-8') as f:
                content = await f.read()
                self.system_state = json.loads(content)
    
    async def save_state(self):
        """保存系统状态"""
        async with aiofiles.open(self.state_path, 'w', encoding='utf-8') as f:
            await f.write(json.dumps({
                "last_saved": datetime.now().isoformat(),
                "knowledge_count": len(self.knowledge_db["nodes"]),
                "skill_count": len(self.skill_library["skills"])
            }, indent=2, ensure_ascii=False))
    
    async def search_knowledge(self, keywords: List[str]) -> List[Dict]:
        """
        搜索知识
        
        Args:
            keywords: 搜索关键词
        
        Returns:
            匹配的知识列表
        """
        results = []
        
        # 在知识图谱中搜索
        for node in self.knowledge_db.get("nodes", []):
            match_score = 0
            for keyword in keywords:
                if keyword.lower() in node.get("content", "").lower():
                    match_score += 1
                if keyword.lower() in node.get("tags", []):
                    match_score += 2
            
            if match_score > 0:
                results.append({
                    **node,
                    "relevance": match_score / len(keywords)
                })
        
        # 按相关性排序
        results.sort(key=lambda x: x.get("relevance", 0), reverse=True)
        
        return results[:10]  # 返回前 10 个结果
    
    async def add_knowledge(self, knowledge: Dict) -> str:
        """
        添加新知识
        
        Args:
            knowledge: 知识条目
        
        Returns:
            知识 ID
        """
        knowledge_id = f"KNW-{datetime.now().strftime('%Y%m%d')}-{len(self.knowledge_db['nodes']) + 1:04d}"
        
        knowledge_entry = {
            "id": knowledge_id,
            "created_at": datetime.now().isoformat(),
            "type": knowledge.get("type", "general"),
            "topic": knowledge.get("topic", ""),
            "content": knowledge.get("content", ""),
            "tags": knowledge.get("tags", []),
            "source": knowledge.get("source", ""),
            "confidence": knowledge.get("confidence", 0.8)
        }
        
        self.knowledge_db["nodes"].append(knowledge_entry)
        
        # 定期保存
        if len(self.knowledge_db["nodes"]) % 10 == 0:
            await self._save_knowledge_db()
        
        return knowledge_id
    
    async def _save_knowledge_db(self):
        """保存知识图谱"""
        async with aiofiles.open(self.knowledge_db_path, 'w', encoding='utf-8') as f:
            await f.write(json.dumps(self.knowledge_db, indent=2, ensure_ascii=False))
    
    async def add_skill(self, skill: Dict) -> str:
        """
        添加新技能
        
        Args:
            skill: 技能信息
        
        Returns:
            技能 ID
        """
        skill_id = f"SKL-{datetime.now().strftime('%Y%m%d')}-{len(self.skill_library['skills']) + 1:04d}"
        
        skill_entry = {
            "id": skill_id,
            "created_at": datetime.now().isoformat(),
            "type": skill.get("type", "general"),
            "name": skill.get("name", ""),
            "description": skill.get("description", ""),
            "proficiency": skill.get("proficiency", 0.5),
            "last_used": datetime.now().isoformat()
        }
        
        self.skill_library["skills"].append(skill_entry)
        
        # 保存技能库
        async with aiofiles.open(self.skill_library_path, 'w', encoding='utf-8') as f:
            await f.write(json.dumps(self.skill_library, indent=2, ensure_ascii=False))
        
        return skill_id
    
    async def record_experience(self, experience: Dict) -> str:
        """
        记录经验
        
        Args:
            experience: 经验记录
        
        Returns:
            经验 ID
        """
        experience_id = f"EXP-{datetime.now().strftime('%Y%m%d%H%M%S')}"
        
        # 根据结果决定存储位置
        outcome = experience.get("validation", {}).get("passed", True)
        if outcome:
            category = "successes"
        else:
            category = "failures"
        
        experience_entry = {
            "id": experience_id,
            "timestamp": datetime.now().isoformat(),
            "task": experience.get("task", ""),
            "plan": experience.get("plan", {}),
            "result": experience.get("result", {}),
            "validation": experience.get("validation", {}),
            "lessons_learned": [],
            "category": category
        }
        
        # 提取经验教训
        experience_entry["lessons_learned"] = self._extract_lessons(experience)
        
        # 保存到文件
        experience_file = self.experience_dir / category / f"{experience_id}.json"
        async with aiofiles.open(experience_file, 'w', encoding='utf-8') as f:
            await f.write(json.dumps(experience_entry, indent=2, ensure_ascii=False))
        
        return experience_id
    
    def _extract_lessons(self, experience: Dict) -> List[str]:
        """从经验中提取教训"""
        lessons = []
        
        task = experience.get("task", "")
        result = experience.get("result", {})
        validation = experience.get("validation", {})
        
        if validation.get("passed", False):
            lessons.append(f"成功完成任务：{task}")
            if result.get("new_modules"):
                lessons.append(f"创建了 {len(result['new_modules'])} 个新模块")
        else:
            lessons.append(f"任务失败：{task}")
            lessons.append("需要分析失败原因并改进")
        
        return lessons
    
    async def get_similar_experiences(self, task_description: str) -> List[Dict]:
        """获取相似经验"""
        similar = []
        
        for category in ["successes", "failures"]:
            category_dir = self.experience_dir / category
            if not category_dir.exists():
                continue
            
            for exp_file in category_dir.glob("*.json"):
                async with aiofiles.open(exp_file, 'r', encoding='utf-8') as f:
                    content = await f.read()
                    experience = json.loads(content)
                    
                    # 简单相似度匹配
                    task = experience.get("task", "")
                    if any(word in task for word in task_description.split()):
                        similar.append(experience)
        
        return similar[:5]
    
    async def store_code_module(self, module_name: str, code: str, metadata: Dict) -> str:
        """
        存储代码模块
        
        Args:
            module_name: 模块名称
            code: 代码内容
            metadata: 元数据
        
        Returns:
            模块路径
        """
        module_file = self.code_repo_dir / "modules" / f"{module_name}.py"
        
        async with aiofiles.open(module_file, 'w', encoding='utf-8') as f:
            await f.write(code)
        
        # 保存元数据
        metadata_file = self.code_repo_dir / "modules" / f"{module_name}.meta.json"
        metadata["stored_at"] = datetime.now().isoformat()
        metadata["module_file"] = str(module_file)
        
        async with aiofiles.open(metadata_file, 'w', encoding='utf-8') as f:
            await f.write(json.dumps(metadata, indent=2, ensure_ascii=False))
        
        return str(module_file)
    
    async def retrieve_code_template(self, template_type: str) -> Optional[str]:
        """获取代码模板"""
        template_file = self.code_repo_dir / "templates" / f"{template_type}.template"
        
        if template_file.exists():
            async with aiofiles.open(template_file, 'r', encoding='utf-8') as f:
                return await f.read()
        
        return None
    
    async def cleanup_old_memories(self, days: int = 30):
        """清理旧记忆"""
        cutoff_date = datetime.now() - timedelta(days=days)
        cleaned_count = 0
        
        for category in ["successes", "failures", "insights"]:
            category_dir = self.experience_dir / category
            if not category_dir.exists():
                continue
            
            for exp_file in category_dir.glob("*.json"):
                async with aiofiles.open(exp_file, 'r', encoding='utf-8') as f:
                    content = await f.read()
                    experience = json.loads(content)
                    
                    exp_date = datetime.fromisoformat(experience.get("timestamp", ""))
                    if exp_date < cutoff_date:
                        exp_file.unlink()
                        cleaned_count += 1
        
        return cleaned_count
