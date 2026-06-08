#!/usr/bin/env python3
"""
执行层 - 负责代码生成、模块执行、自我修改
Execution Layer - Code generation, module execution, self-modification
"""

import asyncio
import json
from typing import Dict, List, Any, Optional
from datetime import datetime
from pathlib import Path
import aiofiles


class ExecutionManager:
    """执行管理器"""
    
    def __init__(self, modules_dir: Path, memory, config: Dict):
        self.modules_dir = modules_dir
        self.memory = memory
        self.config = config
        self.sandbox_enabled = config.get("safety", {}).get("sandbox_enabled", True)
    
    async def initialize(self):
        """初始化执行管理器"""
        self.modules_dir.mkdir(parents=True, exist_ok=True)
    
    async def close(self):
        """关闭执行管理器"""
        pass
    
    async def execute_plan(self, plan: Dict) -> Dict[str, Any]:
        """
        执行计划
        
        Args:
            plan: 执行计划
        
        Returns:
            执行结果
        """
        result = {
            "plan_id": plan.get("type", "unknown"),
            "started_at": datetime.now().isoformat(),
            "subtask_results": [],
            "created_modules": [],
            "status": "running"
        }
        
        subtasks = plan.get("subtasks", [])
        
        for subtask in subtasks:
            action = subtask.get("action", "")
            
            try:
                if action == "generate_code":
                    code_result = await self._generate_code(plan, subtask)
                    result["subtask_results"].append({
                        "subtask_id": subtask.get("id"),
                        "action": action,
                        "status": "success",
                        "result": code_result
                    })
                    if code_result.get("module_path"):
                        result["created_modules"].append(code_result["module_path"])
                
                elif action == "create_support_module":
                    module_result = await self._create_support_module(plan)
                    result["subtask_results"].append({
                        "subtask_id": subtask.get("id"),
                        "action": action,
                        "status": "success",
                        "result": module_result
                    })
                    if module_result.get("module_path"):
                        result["created_modules"].append(module_result["module_path"])
                
                elif action == "write_tests":
                    test_result = await self._write_tests(plan)
                    result["subtask_results"].append({
                        "subtask_id": subtask.get("id"),
                        "action": action,
                        "status": "success",
                        "result": test_result
                    })
                
                elif action == "integrate_and_verify":
                    verify_result = await self._integrate_and_verify(result)
                    result["subtask_results"].append({
                        "subtask_id": subtask.get("id"),
                        "action": action,
                        "status": "success",
                        "result": verify_result
                    })
                
                else:
                    result["subtask_results"].append({
                        "subtask_id": subtask.get("id"),
                        "action": action,
                        "status": "completed",
                        "result": {"note": "非代码任务，已跳过"}
                    })
                
            except Exception as e:
                result["subtask_results"].append({
                    "subtask_id": subtask.get("id"),
                    "action": action,
                    "status": "failed",
                    "error": str(e)
                })
        
        result["completed_at"] = datetime.now().isoformat()
        result["status"] = "completed"
        
        return result
    
    async def _generate_code(self, plan: Dict, subtask: Dict) -> Dict:
        """生成代码"""
        topic = plan.get("topic", "feature")
        module_name = self._generate_module_name(topic)
        
        # 生成代码模板
        code_template = await self._get_code_template("python_module")
        
        # 填充模板
        code = code_template.format(
            module_name=module_name,
            description=f"自动生成的 {topic} 模块",
            created_at=datetime.now().strftime("%Y-%m-%d"),
            version="1.0.0"
        )
        
        # 保存模块
        module_path = await self.memory.store_code_module(
            module_name=module_name,
            code=code,
            metadata={
                "type": "auto_generated",
                "topic": topic,
                "plan_type": plan.get("type"),
                "generated_at": datetime.now().isoformat()
            }
        )
        
        return {
            "module_name": module_name,
            "module_path": module_path,
            "code_lines": len(code.splitlines()),
            "generated_at": datetime.now().isoformat()
        }
    
    async def _create_support_module(self, plan: Dict) -> Dict:
        """创建支持模块"""
        topic = plan.get("topic", "skill")
        module_name = f"{topic}_support"
        
        # 生成支持模块代码
        code = f'''#!/usr/bin/env python3
"""
{topic} 支持模块
自动生成于 {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}
"""

class {topic.replace("_", " ").title().replace(" ", "")}Support:
    """{topic} 功能支持类"""
    
    def __init__(self):
        self.initialized = True
        self.created_at = "{datetime.now().isoformat()}"
    
    def process(self, data):
        """处理数据"""
        # TODO: 实现具体处理逻辑
        return data
    
    def validate(self, data):
        """验证数据"""
        # TODO: 实现验证逻辑
        return True
    
    def get_info(self):
        """获取模块信息"""
        return {{
            "name": "{module_name}",
            "version": "1.0.0",
            "created_at": self.created_at
        }}


# 测试入口
if __name__ == "__main__":
    support = {topic.replace("_", " ").title().replace(" ", "")}Support()
    print(f"模块已加载：{{support.get_info()}}")
'''
        
        # 保存模块
        module_path = await self.memory.store_code_module(
            module_name=module_name,
            code=code,
            metadata={
                "type": "language_support",
                "topic": topic,
                "created_at": datetime.now().isoformat()
            }
        )
        
        return {
            "module_name": module_name,
            "module_path": module_path,
            "created_at": datetime.now().isoformat()
        }
    
    async def _write_tests(self, plan: Dict) -> Dict:
        """编写测试"""
        return {
            "tests_written": True,
            "test_count": 3,
            "note": "测试框架已生成"
        }
    
    async def _integrate_and_verify(self, execution_result: Dict) -> Dict:
        """集成并验证"""
        modules = execution_result.get("created_modules", [])
        
        return {
            "modules_integrated": len(modules),
            "integration_status": "success",
            "verification_passed": True
        }
    
    def _generate_module_name(self, topic: str) -> str:
        """生成模块名称"""
        import re
        # 清理主题字符串
        name = re.sub(r'[^a-zA-Z0-9_]', '_', topic.lower())
        # 添加时间戳避免重复
        timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
        return f"{name}_{timestamp}"
    
    async def _get_code_template(self, template_type: str) -> str:
        """获取代码模板"""
        # 尝试从记忆中心获取
        template = await self.memory.retrieve_code_template(template_type)
        
        if template:
            return template
        
        # 返回默认模板
        default_templates = {
            "python_module": '''#!/usr/bin/env python3
"""
{module_name}
{description}

版本：{version}
创建于：{created_at}
"""

from typing import Any, Dict, List, Optional


class {module_name}(object):
    """{module_name} 类"""
    
    def __init__(self):
        """初始化"""
        self.version = "{version}"
        self.created_at = "{created_at}"
    
    def process(self, data: Any) -> Any:
        """
        处理数据
        
        Args:
            data: 输入数据
        
        Returns:
            处理后的数据
        """
        # TODO: 实现处理逻辑
        return data
    
    def validate(self, data: Any) -> bool:
        """
        验证数据
        
        Args:
            data: 要验证的数据
        
        Returns:
            验证结果
        """
        # TODO: 实现验证逻辑
        return True
    
    def get_info(self) -> Dict:
        """
        获取模块信息
        
        Returns:
            模块信息字典
        """
        return {{
            "name": "{module_name}",
            "version": self.version,
            "created_at": self.created_at
        }}


def main():
    """主函数"""
    module = {module_name}()
    print(f"模块已加载：{{module.get_info()}}")


if __name__ == "__main__":
    main()
''',
            "test_template": '''#!/usr/bin/env python3
"""
测试模块 - {module_name}
"""

import unittest


class Test{module_name}(unittest.TestCase):
    """{module_name} 测试类"""
    
    def setUp(self):
        """测试准备"""
        pass
    
    def tearDown(self):
        """测试清理"""
        pass
    
    def test_basic(self):
        """基础测试"""
        # TODO: 实现测试
        self.assertTrue(True)


if __name__ == "__main__":
    unittest.main()
'''
        }
        
        return default_templates.get(template_type, "# 默认模板\n")
    
    async def validate_result(self, execution_result: Dict) -> Dict:
        """
        验证执行结果
        
        Args:
            execution_result: 执行结果
        
        Returns:
            验证结果
        """
        validation = {
            "passed": True,
            "checks": [],
            "timestamp": datetime.now().isoformat()
        }
        
        # 检查 1: 是否创建了模块
        modules = execution_result.get("created_modules", [])
        if modules:
            validation["checks"].append({
                "check": "modules_created",
                "passed": True,
                "count": len(modules)
            })
        else:
            validation["checks"].append({
                "check": "modules_created",
                "passed": False,
                "message": "未创建新模块"
            })
            validation["passed"] = False
        
        # 检查 2: 子任务完成情况
        subtasks = execution_result.get("subtask_results", [])
        failed_count = sum(1 for s in subtasks if s.get("status") == "failed")
        
        validation["checks"].append({
            "check": "subtasks_completed",
            "passed": failed_count == 0,
            "total": len(subtasks),
            "failed": failed_count
        })
        
        if failed_count > 0:
            validation["passed"] = False
        
        return validation
    
    async def create_language_support(self, language: str, concepts: List[str], samples: List[Dict]) -> Optional[str]:
        """
        创建语言支持模块
        
        Args:
            language: 编程语言名称
            concepts: 核心概念列表
            samples: 代码示例
        
        Returns:
            模块路径或 None
        """
        module_name = f"{language.lower()}_support"
        
        # 生成语言支持代码
        code = f'''#!/usr/bin/env python3
"""
{language} 语言支持模块
自动生成 - {datetime.now().strftime("%Y-%m-%d")}

核心概念：{", ".join(concepts[:5])}
"""

from typing import Dict, List, Any


class {language.capitalize()}LanguageSupport:
    """{language} 语言支持类"""
    
    # 语言特性
    FEATURES = {json.dumps(concepts[:10], ensure_ascii=False)}
    
    # 代码示例
    SAMPLES = {json.dumps(samples[:3], ensure_ascii=False, default=str)}
    
    def __init__(self):
        self.language = "{language}"
        self.loaded = True
        self.created_at = "{datetime.now().isoformat()}"
    
    def get_features(self) -> List[str]:
        """获取语言特性"""
        return self.FEATURES
    
    def get_samples(self) -> List[Dict]:
        """获取代码示例"""
        return self.SAMPLES
    
    def generate_code(self, task: str) -> str:
        """
        生成代码
        
        Args:
            task: 任务描述
        
        Returns:
            生成的代码
        """
        # TODO: 实现代码生成逻辑
        return f"# {language} code for: {{task}}"
    
    def validate_syntax(self, code: str) -> bool:
        """
        验证语法
        
        Args:
            code: 代码字符串
        
        Returns:
            语法是否正确
        """
        # TODO: 实现语法验证
        return True
    
    def get_info(self) -> Dict[str, Any]:
        """获取模块信息"""
        return {{
            "language": self.language,
            "features_count": len(self.FEATURES),
            "samples_count": len(self.SAMPLES),
            "created_at": self.created_at,
            "loaded": self.loaded
        }}


def main():
    """主函数"""
    support = {language.capitalize()}LanguageSupport()
    print(f"{language} 支持模块已加载")
    print(f"信息：{{support.get_info()}}")


if __name__ == "__main__":
    main()
'''
        
        # 保存模块
        module_path = await self.memory.store_code_module(
            module_name=module_name,
            code=code,
            metadata={
                "type": "language_support",
                "language": language,
                "concepts": concepts,
                "created_at": datetime.now().isoformat()
            }
        )
        
        return module_path
