#!/usr/bin/env python3
"""
元认知 - 负责自我评估、目标追踪、策略优化
Meta-Cognition - Self-evaluation, goal tracking, strategy optimization
"""

import json
from typing import Dict, List, Any, Optional
from datetime import datetime
from pathlib import Path


class MetaCognition:
    """元认知模块"""
    
    def __init__(self, ai_system, config: Dict):
        self.ai = ai_system
        self.config = config
        self.evaluation_history = []
    
    async def initialize(self):
        """初始化元认知"""
        pass
    
    async def evaluate_system(self) -> Dict[str, Any]:
        """
        自我评估系统状态
        
        Returns:
            评估结果
        """
        evaluation = {
            "timestamp": datetime.now().isoformat(),
            "overall_health": 0.0,
            "dimensions": {},
            "recommendations": []
        }
        
        # 维度 1: 知识储备
        knowledge_score = await self._evaluate_knowledge()
        evaluation["dimensions"]["knowledge"] = knowledge_score
        
        # 维度 2: 技能水平
        skills_score = await self._evaluate_skills()
        evaluation["dimensions"]["skills"] = skills_score
        
        # 维度 3: 执行能力
        execution_score = await self._evaluate_execution()
        evaluation["dimensions"]["execution"] = execution_score
        
        # 维度 4: 学习能力
        learning_score = await self._evaluate_learning()
        evaluation["dimensions"]["learning"] = learning_score
        
        # 维度 5: 安全性
        safety_score = await self._evaluate_safety()
        evaluation["dimensions"]["safety"] = safety_score
        
        # 计算总体健康度
        scores = [
            knowledge_score["score"],
            skills_score["score"],
            execution_score["score"],
            learning_score["score"],
            safety_score["score"]
        ]
        evaluation["overall_health"] = sum(scores) / len(scores)
        
        # 生成建议
        evaluation["recommendations"] = self._generate_recommendations(evaluation["dimensions"])
        
        # 保存评估历史
        self.evaluation_history.append(evaluation)
        
        return evaluation
    
    async def _evaluate_knowledge(self) -> Dict:
        """评估知识储备"""
        knowledge_db = self.ai.memory.knowledge_db
        node_count = len(knowledge_db.get("nodes", []))
        
        score = min(1.0, node_count / 100)  # 100 个知识点为满分
        
        return {
            "score": score,
            "metrics": {
                "knowledge_nodes": node_count,
                "target": 100
            },
            "status": "good" if score > 0.5 else "needs_improvement"
        }
    
    async def _evaluate_skills(self) -> Dict:
        """评估技能水平"""
        skill_library = self.ai.memory.skill_library
        skill_count = len(skill_library.get("skills", []))
        
        score = min(1.0, skill_count / 10)  # 10 个技能为满分
        
        return {
            "score": score,
            "metrics": {
                "skill_count": skill_count,
                "target": 10
            },
            "status": "good" if score > 0.5 else "needs_improvement"
        }
    
    async def _evaluate_execution(self) -> Dict:
        """评估执行能力"""
        # 检查模块目录
        modules_dir = self.ai.modules_dir
        module_count = len(list(modules_dir.glob("*.py"))) if modules_dir.exists() else 0
        
        score = min(1.0, module_count / 20)  # 20 个模块为满分
        
        return {
            "score": score,
            "metrics": {
                "module_count": module_count,
                "target": 20
            },
            "status": "good" if score > 0.5 else "needs_improvement"
        }
    
    async def _evaluate_learning(self) -> Dict:
        """评估学习能力"""
        # 检查经验记录
        experience_dir = self.ai.memory.experience_dir
        experience_count = 0
        
        if experience_dir.exists():
            for category in ["successes", "failures", "insights"]:
                category_dir = experience_dir / category
                if category_dir.exists():
                    experience_count += len(list(category_dir.glob("*.json")))
        
        score = min(1.0, experience_count / 50)  # 50 条经验为满分
        
        return {
            "score": score,
            "metrics": {
                "experience_count": experience_count,
                "target": 50
            },
            "status": "good" if score > 0.5 else "needs_improvement"
        }
    
    async def _evaluate_safety(self) -> Dict:
        """评估安全性"""
        safety_config = self.config.get("safety", {})
        
        # 检查安全配置
        safety_features = {
            "sandbox_enabled": safety_config.get("sandbox_enabled", False),
            "confirmation_required": len(safety_config.get("require_confirmation_for", [])) > 0,
            "change_limit": safety_config.get("max_code_change_lines", 0) > 0
        }
        
        enabled_count = sum(1 for v in safety_features.values() if v)
        score = enabled_count / len(safety_features)
        
        return {
            "score": score,
            "metrics": {
                "safety_features": safety_features,
                "enabled_count": enabled_count
            },
            "status": "secure" if score == 1.0 else "review_needed"
        }
    
    def _generate_recommendations(self, dimensions: Dict) -> List[Dict]:
        """生成改进建议"""
        recommendations = []
        
        for dimension, data in dimensions.items():
            if data.get("status") in ["needs_improvement", "review_needed"]:
                recommendations.append({
                    "dimension": dimension,
                    "current_score": data.get("score", 0),
                    "suggestion": self._get_suggestion_for_dimension(dimension),
                    "priority": "high" if data.get("score", 0) < 0.3 else "medium"
                })
        
        return recommendations
    
    def _get_suggestion_for_dimension(self, dimension: str) -> str:
        """获取维度改进建议"""
        suggestions = {
            "knowledge": "增加知识储备，多学习新领域的知识",
            "skills": "学习新技能，扩展系统能力边界",
            "execution": "创建更多功能模块，提高执行能力",
            "learning": "积累更多经验，从成功和失败中学习",
            "safety": "检查并完善安全配置"
        }
        return suggestions.get(dimension, "继续改进")
    
    async def track_goal(self, goal: Dict) -> Dict:
        """
        追踪目标进度
        
        Args:
            goal: 目标定义
        
        Returns:
            进度报告
        """
        progress = {
            "goal": goal,
            "current_progress": 0.0,
            "milestones": [],
            "estimated_completion": None,
            "timestamp": datetime.now().isoformat()
        }
        
        # 根据目标类型计算进度
        goal_type = goal.get("type", "general")
        
        if goal_type == "learn_language":
            progress = await self._track_language_learning(goal, progress)
        elif goal_type == "create_feature":
            progress = await self._track_feature_creation(goal, progress)
        
        return progress
    
    async def _track_language_learning(self, goal: Dict, progress: Dict) -> Dict:
        """追踪语言学习进度"""
        language = goal.get("language", "")
        
        # 检查是否已学习该语言
        skill_library = self.ai.memory.skill_library
        learned_languages = [
            s.get("name") for s in skill_library.get("skills", [])
            if s.get("type") == "language"
        ]
        
        if language in learned_languages:
            progress["current_progress"] = 1.0
            progress["milestones"].append({
                "name": f"学习 {language}",
                "completed": True,
                "completed_at": datetime.now().isoformat()
            })
        else:
            progress["current_progress"] = 0.0
        
        return progress
    
    async def _track_feature_creation(self, goal: Dict, progress: Dict) -> Dict:
        """追踪功能创建进度"""
        # 检查模块目录
        modules_dir = self.ai.modules_dir
        if modules_dir.exists():
            module_count = len(list(modules_dir.glob("*.py")))
            progress["current_progress"] = min(1.0, module_count / 10)
        
        return progress
    
    async def optimize_strategy(self, current_strategy: str, results: List[Dict]) -> Dict:
        """
        优化策略
        
        Args:
            current_strategy: 当前策略
            results: 执行结果历史
        
        Returns:
            优化后的策略
        """
        optimization = {
            "original_strategy": current_strategy,
            "analysis": [],
            "suggested_changes": [],
            "confidence": 0.5,
            "timestamp": datetime.now().isoformat()
        }
        
        # 分析结果
        success_count = sum(1 for r in results if r.get("outcome") == "success")
        total = len(results)
        
        if total > 0:
            success_rate = success_count / total
            optimization["analysis"].append({
                "metric": "success_rate",
                "value": success_rate,
                "interpretation": "良好" if success_rate > 0.7 else "需要改进"
            })
            
            if success_rate < 0.5:
                optimization["suggested_changes"].append(
                    "考虑调整任务分解策略，将任务拆分成更小的步骤"
                )
                optimization["confidence"] = 0.7
        
        return optimization
