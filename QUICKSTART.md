# 自进化 AI 系统 - 快速开始指南

## 🚀 5 分钟快速上手

### 1. 安装依赖

```bash
cd /workspace/self-evolving-ai
pip install -r requirements.txt
```

### 2. 启动系统

```bash
# 交互模式（推荐）
python main.py --interactive

# 或执行单个任务
python main.py --task "添加一个 JSON 分析功能"

# 或学习新语言
python main.py --learn Rust

# 查看系统状态
python main.py --status
```

### 3. 交互模式命令

```
> task <任务描述>     - 执行任务
> learn <语言名>      - 学习编程语言
> status             - 查看系统状态
> evaluate           - 自我评估
> help               - 查看帮助
> quit               - 退出
```

## 📋 使用示例

### 示例 1: 创建新功能

```
> task 添加一个能分析 JSON 文件并生成统计报告的功能

系统会自动:
1. 理解需求
2. 检索相关知识
3. 如需则上网学习
4. 生成代码模块
5. 测试验证
6. 保存经验
```

### 示例 2: 学习新语言

```
> learn Go

系统会自动:
1. 搜索 Go 语言资源
2. 学习核心概念
3. 收集代码示例
4. 创建 Go 支持模块
5. 将 Go 加入技能库
```

### 示例 3: 自我评估

```
> evaluate

系统会评估:
- 知识储备
- 技能水平
- 执行能力
- 学习能力
- 安全性
```

## 📁 目录结构

```
self-evolving-ai/
├── main.py                    # 主入口
├── requirements.txt           # 依赖
├── README.md                  # 详细文档
├── QUICKSTART.md              # 本文件
├── perception/                # 感知层
│   └── perception_manager.py  # 网络学习、信息获取
├── cognition/                 # 认知层
│   └── cognition_engine.py    # 理解、推理、规划
├── execution/                 # 执行层
│   └── execution_manager.py   # 代码生成、模块执行
├── memory/                    # 记忆中心
│   └── memory_center.py       # 知识、技能、经验存储
├── meta/                      # 元认知
│   └── meta_cognition.py      # 自我评估、优化
├── memory/                    # 记忆数据 (运行时生成)
│   ├── knowledge_graph.json   # 知识图谱
│   ├── skill_library.json     # 技能库
│   └── experience_memory/     # 经验记忆
└── modules/                   # 功能模块 (运行时生成)
```

## 🔧 配置选项

创建 `config.json` 自定义系统行为:

```json
{
  "learning": {
    "auto_web_search": true,
    "max_search_depth": 3,
    "trusted_sources": [
      "github.com",
      "stackoverflow.com",
      "python.org"
    ]
  },
  "safety": {
    "require_confirmation_for": ["system_modify", "file_delete"],
    "max_code_change_lines": 500,
    "sandbox_enabled": true
  },
  "memory": {
    "max_experience_entries": 10000,
    "auto_cleanup_days": 30
  }
}
```

启动时指定配置:

```bash
python main.py --config config.json --interactive
```

## 🛡️ 安全机制

系统内置多层安全保护:

| 机制 | 说明 |
|------|------|
| 沙箱执行 | 新代码在隔离环境中测试 |
| 变更限制 | 单次修改不超过 500 行 |
| 人工确认 | 敏感操作需人工确认 |
| 版本控制 | 所有修改可追溯回滚 |
| 可信源 | 仅从可信网站学习 |

## 📊 系统能力

### 已支持

- ✅ 理解自然语言需求
- ✅ 自主网络学习
- ✅ 代码自动生成
- ✅ 模块动态加载
- ✅ 经验记忆沉淀
- ✅ 自我评估优化

### 规划中

- 🔄 真实 API 集成 (GitHub, StackOverflow)
- 🔄 多语言代码生成
- 🔄 自动化测试生成
- 🔄 分布式学习
- 🔄 可视化仪表板

## 🐛 故障排除

### 问题：依赖安装失败

```bash
# 使用 pip 国内镜像
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
```

### 问题：模块导入错误

确保所有 `__init__.py` 文件存在:

```bash
touch perception/__init__.py
touch cognition/__init__.py
touch execution/__init__.py
touch memory/__init__.py
touch meta/__init__.py
```

### 问题：系统状态为空

首次运行需要初始化，执行一次任务后会有数据:

```bash
python main.py --task "测试功能"
```

## 📚 进阶阅读

- [README.md](README.md) - 完整架构文档
- [perception/perception_manager.py](perception/perception_manager.py) - 感知层实现
- [cognition/cognition_engine.py](cognition/cognition_engine.py) - 认知层实现
- [execution/execution_manager.py](execution/execution_manager.py) - 执行层实现
- [memory/memory_center.py](memory/memory_center.py) - 记忆中心实现
- [meta/meta_cognition.py](meta/meta_cognition.py) - 元认知实现

## 💡 提示

1. **首次使用** 建议先用 `--status` 查看系统状态
2. **学习任务** 从简单开始，逐步增加复杂度
3. **定期检查** 使用 `--evaluate` 了解系统进化情况
4. **保存配置** 将常用配置保存到 `config.json`

---

**开始你的自进化之旅！** 🚀

```bash
python main.py --interactive
```
