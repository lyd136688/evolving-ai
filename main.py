#!/usr/bin/env python3
"""
自进化 AI 系统 - 主入口
Self-Evolving AI System - Main Entry Point

这是一个能够自主学习、自我衍化、持续进化的 AI 系统框架
"""

import asyncio
import json
import argparse
from datetime import datetime
from pathlib import Path
from typing import Optional, Dict, Any

# 系统版本
VERSION = "2.0.0-alpha"
SYSTEM_NAME = "自进化 AI 系统"


class SelfEvolvingAI:
    """自进化 AI 系统主类"""
    
    def __init__(self, config_path: Optional[str] = None):
        self.config = self._load_config(config_path)
        self.system_root = Path(__file__).parent
        self.memory_dir = self.system_root / "memory"
        self.modules_dir = self.system_root / "modules"
        self.logs_dir = self.system_root / "logs"
        
        # 确保目录存在
        self.memory_dir.mkdir(parents=True, exist_ok=True)
        self.modules_dir.mkdir(parents=True, exist_ok=True)
        self.logs_dir.mkdir(parents=True, exist_ok=True)
        
        # 初始化核心组件
        self.perception = None
        self.cognition = None
        self.execution = None
        self.memory = None
        self.meta = None
        
        # 系统状态
        self.state = {
            "initialized_at": datetime.now().isoformat(),
            "version": VERSION,
            "mode": "idle",
            "current_task": None,
            "evolution_count": 0,
            "skills": []
        }
        
        self._log(f"{SYSTEM_NAME} v{VERSION} 初始化中...")
    
    def _load_config(self, config_path: Optional[str]) -> Dict:
        """加载配置"""
        default_config = {
            "system": {
                "name": SYSTEM_NAME,
                "version": VERSION,
                "language": "zh-CN"
            },
            "learning": {
                "auto_web_search": True,
                "max_search_depth": 3,
                "trusted_sources": [
                    "github.com",
                    "stackoverflow.com",
                    "python.org",
                    "rust-lang.org",
                    "developer.mozilla.org"
                ]
            },
            "safety": {
                "require_confirmation_for": ["system_modify", "external_api", "file_delete"],
                "max_code_change_lines": 500,
                "sandbox_enabled": True
            },
            "memory": {
                "max_experience_entries": 10000,
                "auto_cleanup_days": 30
            }
        }
        
        if config_path and Path(config_path).exists():
            with open(config_path, 'r', encoding='utf-8') as f:
                user_config = json.load(f)
                # 合并配置
                self._merge_config(default_config, user_config)
        
        return default_config
    
    def _merge_config(self, base: Dict, override: Dict) -> Dict:
        """递归合并配置"""
        for key, value in override.items():
            if key in base and isinstance(base[key], dict) and isinstance(value, dict):
                self._merge_config(base[key], value)
            else:
                base[key] = value
        return base
    
    def _log(self, message: str, level: str = "INFO"):
        """系统日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_entry = f"[{timestamp}] [{level}] {message}"
        print(log_entry)
        
        # 写入日志文件
        log_file = self.logs_dir / f"system_{datetime.now().strftime('%Y%m%d')}.log"
        with open(log_file, 'a', encoding='utf-8') as f:
            f.write(log_entry + "\n")
    
    async def initialize(self):
        """初始化系统组件"""
        self._log("正在初始化核心组件...")
        
        # 延迟导入以避免循环依赖
        from perception.perception_manager import PerceptionManager
        from cognition.cognition_engine import CognitionEngine
        from execution.execution_manager import ExecutionManager
        from memory.memory_center import MemoryCenter
        from meta.meta_cognition import MetaCognition
        
        # 初始化各层
        self.memory = MemoryCenter(self.memory_dir, self.config)
        await self.memory.initialize()
        self._log("✓ 记忆中心已初始化")
        
        self.perception = PerceptionManager(self.config)
        await self.perception.initialize()
        self._log("✓ 感知层已初始化")
        
        self.cognition = CognitionEngine(self.memory, self.config)
        await self.cognition.initialize()
        self._log("✓ 认知层已初始化")
        
        self.execution = ExecutionManager(self.modules_dir, self.memory, self.config)
        await self.execution.initialize()
        self._log("✓ 执行层已初始化")
        
        self.meta = MetaCognition(self, self.config)
        await self.meta.initialize()
        self._log("✓ 元认知已初始化")
        
        # 加载已有技能
        await self._load_skills()
        
        self.state["mode"] = "ready"
        self._log(f"✓ 系统初始化完成，已加载 {len(self.state['skills'])} 个技能")
    
    async def _load_skills(self):
        """加载已有技能"""
        skills_file = self.memory_dir / "skill_library.json"
        if skills_file.exists():
            with open(skills_file, 'r', encoding='utf-8') as f:
                skills_data = json.load(f)
                self.state["skills"] = skills_data.get("skills", [])
    
    async def process_request(self, request: str) -> Dict[str, Any]:
        """
        处理用户请求
        
        Args:
            request: 用户需求描述，如"添加一个 JSON 分析功能"
        
        Returns:
            执行结果字典
        """
        self._log(f"收到请求：{request}")
        self.state["mode"] = "processing"
        self.state["current_task"] = request
        
        result = {
            "request": request,
            "timestamp": datetime.now().isoformat(),
            "steps": [],
            "outcome": "pending",
            "new_modules": [],
            "learned_knowledge": []
        }
        
        try:
            # 步骤 1: 理解需求
            self._log("步骤 1/6: 理解需求...")
            understanding = await self.cognition.understand_request(request)
            result["steps"].append({
                "step": 1,
                "action": "understand",
                "result": understanding
            })
            
            # 步骤 2: 检索知识
            self._log("步骤 2/6: 检索已有知识...")
            knowledge = await self.memory.search_knowledge(understanding["keywords"])
            result["steps"].append({
                "step": 2,
                "action": "retrieve",
                "result": {"found_items": len(knowledge)}
            })
            
            # 步骤 3: 判断是否需要网络学习
            if understanding["complexity"] > 0.7 or len(knowledge) < 2:
                self._log("步骤 3/6: 启动网络学习...")
                web_knowledge = await self.perception.learn_from_web(understanding["topic"])
                result["steps"].append({
                    "step": 3,
                    "action": "web_learning",
                    "result": {"sources": len(web_knowledge)}
                })
                result["learned_knowledge"] = web_knowledge
            else:
                result["steps"].append({
                    "step": 3,
                    "action": "web_learning",
                    "result": {"skipped": "已有足够知识"}
                })
            
            # 步骤 4: 规划任务
            self._log("步骤 4/6: 规划实现方案...")
            plan = await self.cognition.create_plan(understanding, knowledge)
            result["steps"].append({
                "step": 4,
                "action": "plan",
                "result": {"subtasks": len(plan["subtasks"])}
            })
            
            # 步骤 5: 执行实现
            self._log("步骤 5/6: 执行代码生成与集成...")
            execution_result = await self.execution.execute_plan(plan)
            result["steps"].append({
                "step": 5,
                "action": "execute",
                "result": execution_result
            })
            result["new_modules"] = execution_result.get("created_modules", [])
            
            # 步骤 6: 验证与沉淀
            self._log("步骤 6/6: 验证功能并沉淀经验...")
            validation = await self.execution.validate_result(execution_result)
            await self.memory.record_experience({
                "task": request,
                "plan": plan,
                "result": execution_result,
                "validation": validation,
                "timestamp": datetime.now().isoformat()
            })
            result["steps"].append({
                "step": 6,
                "action": "validate",
                "result": validation
            })
            
            result["outcome"] = "success" if validation["passed"] else "partial"
            self.state["evolution_count"] += 1
            
        except Exception as e:
            self._log(f"处理请求时出错：{e}", "ERROR")
            result["outcome"] = "failed"
            result["error"] = str(e)
        
        self.state["mode"] = "ready"
        self.state["current_task"] = None
        
        return result
    
    async def learn_language(self, language: str) -> Dict[str, Any]:
        """
        学习新的编程语言
        
        Args:
            language: 编程语言名称，如 "Rust", "Go", "TypeScript"
        
        Returns:
            学习结果
        """
        self._log(f"开始学习编程语言：{language}")
        
        result = {
            "language": language,
            "timestamp": datetime.now().isoformat(),
            "resources_found": 0,
            "concepts_learned": [],
            "code_samples": 0,
            "capability_added": False
        }
        
        try:
            # 搜索学习资源
            resources = await self.perception.search_language_resources(language)
            result["resources_found"] = len(resources)
            
            # 解析核心概念
            concepts = await self.cognition.extract_language_concepts(resources)
            result["concepts_learned"] = concepts
            
            # 学习代码示例
            samples = await self.perception.collect_code_samples(language)
            result["code_samples"] = len(samples)
            
            # 生成语言支持模块
            module_path = await self.execution.create_language_support(language, concepts, samples)
            result["capability_added"] = module_path is not None
            result["module_path"] = module_path
            
            # 记录到技能库
            await self.memory.add_skill({
                "type": "language",
                "name": language,
                "concepts": concepts,
                "learned_at": datetime.now().isoformat()
            })
            
            self.state["skills"].append({
                "type": "language",
                "name": language,
                "learned_at": datetime.now().isoformat()
            })
            
        except Exception as e:
            self._log(f"学习 {language} 时出错：{e}", "ERROR")
            result["error"] = str(e)
        
        return result
    
    async def self_evaluate(self) -> Dict[str, Any]:
        """自我评估系统状态"""
        return await self.meta.evaluate_system()
    
    def get_status(self) -> Dict[str, Any]:
        """获取系统状态"""
        return {
            **self.state,
            "memory_path": str(self.memory_dir),
            "modules_path": str(self.modules_dir),
            "uptime": datetime.now().isoformat()
        }
    
    async def shutdown(self):
        """优雅关闭系统"""
        self._log("系统正在关闭...")
        self.state["mode"] = "shutting_down"
        
        # 保存状态
        await self.memory.save_state()
        
        # 关闭各组件
        if self.perception:
            await self.perception.close()
        if self.cognition:
            await self.cognition.close()
        if self.execution:
            await self.execution.close()
        
        self._log("系统已关闭")


async def main():
    """主入口函数"""
    parser = argparse.ArgumentParser(
        description=f"{SYSTEM_NAME} v{VERSION}",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
使用示例:
  python main.py --task "添加一个日志分析功能"
  python main.py --learn Rust
  python main.py --status
  python main.py --interactive
        """
    )
    
    parser.add_argument("--task", type=str, help="要执行的任务描述")
    parser.add_argument("--learn", type=str, help="学习的编程语言")
    parser.add_argument("--status", action="store_true", help="显示系统状态")
    parser.add_argument("--interactive", action="store_true", help="交互模式")
    parser.add_argument("--config", type=str, help="配置文件路径")
    parser.add_argument("--evaluate", action="store_true", help="执行自我评估")
    
    args = parser.parse_args()
    
    # 创建系统实例
    ai = SelfEvolvingAI(config_path=args.config)
    
    try:
        # 初始化
        await ai.initialize()
        
        # 处理命令
        if args.status:
            status = ai.get_status()
            print("\n" + "="*60)
            print(f"{SYSTEM_NAME} 状态")
            print("="*60)
            for key, value in status.items():
                print(f"  {key}: {value}")
            print("="*60)
        
        elif args.task:
            result = await ai.process_request(args.task)
            print("\n" + "="*60)
            print("任务执行结果")
            print("="*60)
            print(json.dumps(result, ensure_ascii=False, indent=2))
            print("="*60)
        
        elif args.learn:
            result = await ai.learn_language(args.learn)
            print("\n" + "="*60)
            print(f"学习 {args.learn} 结果")
            print("="*60)
            print(json.dumps(result, ensure_ascii=False, indent=2))
            print("="*60)
        
        elif args.evaluate:
            result = await ai.self_evaluate()
            print("\n" + "="*60)
            print("自我评估结果")
            print("="*60)
            print(json.dumps(result, ensure_ascii=False, indent=2))
            print("="*60)
        
        elif args.interactive:
            print("\n" + "="*60)
            print(f"欢迎使用 {SYSTEM_NAME} v{VERSION}")
            print("输入 'help' 查看帮助，'quit' 退出")
            print("="*60)
            
            while True:
                try:
                    user_input = input("\n> ").strip()
                    if not user_input:
                        continue
                    if user_input.lower() in ['quit', 'exit', 'q']:
                        break
                    if user_input.lower() == 'help':
                        print("""
可用命令:
  task <描述>     - 执行任务
  learn <语言>    - 学习编程语言
  status         - 查看系统状态
  evaluate       - 自我评估
  quit           - 退出
                        """)
                        continue
                    
                    # 解析命令
                    parts = user_input.split(maxsplit=1)
                    command = parts[0].lower()
                    arg = parts[1] if len(parts) > 1 else ""
                    
                    if command == "task" and arg:
                        result = await ai.process_request(arg)
                        print(json.dumps(result, ensure_ascii=False, indent=2))
                    elif command == "learn" and arg:
                        result = await ai.learn_language(arg)
                        print(json.dumps(result, ensure_ascii=False, indent=2))
                    elif command == "status":
                        print(json.dumps(ai.get_status(), ensure_ascii=False, indent=2))
                    elif command == "evaluate":
                        result = await ai.self_evaluate()
                        print(json.dumps(result, ensure_ascii=False, indent=2))
                    else:
                        print("未知命令，输入 'help' 查看帮助")
                
                except KeyboardInterrupt:
                    break
                except Exception as e:
                    print(f"错误：{e}")
        
        else:
            parser.print_help()
    
    finally:
        await ai.shutdown()


if __name__ == "__main__":
    asyncio.run(main())
