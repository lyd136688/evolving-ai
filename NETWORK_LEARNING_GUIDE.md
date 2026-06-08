#  自进化 AI - 网络学习使用指南

## ✅ 已实现的功能

| 模块 | 文件 | 功能 |
|------|------|------|
| **网络学习** | `network_learning.py` | 自主爬取、解析、存储网络知识 |
| **记忆检索** | `memory_search.py` | 语义搜索、标签分类、统计导出 |
| **启动脚本** | `autonomous_learn.sh` | 一键启动学习流程 |

---

##  快速开始

### 方式 1: 使用启动脚本（推荐）

```bash
cd /workspace/self-evolving-ai
chmod +x autonomous_learn.sh
./autonomous_learn.sh
```

### 方式 2: 直接运行 Python

```bash
cd /workspace/self-evolving-ai

# 安装依赖
pip install aiohttp beautifulsoup4

# 学习指定主题
python3 network_learning.py

# 搜索知识
python3 memory_search.py
```

---

## 📚 支持的学习主题

| 主题 | 学习源 |
|------|--------|
| `python` | Python 官方教程、Real Python |
| `rust` | Rust Book、Rust By Example |
| `go` | Go Tour、Go By Example |
| `android` | Android Developer、AOSP |
| `ai` | Hugging Face、PyTorch 文档 |

---

## 📖 使用示例

### 学习 Python

```bash
$ ./autonomous_learn.sh

选择 (1-4): 1
输入主题：python

 开始学习：python
==================================================

📖 学习：https://docs.python.org/3/tutorial/
✅ 已保存：a3f2b1_Python 官方教程.json

📖 学习：https://realpython.com/
✅ 已保存：c7d9e4_Real Python 教程.json

✅ python 学习完成！共学习 2 个页面
```

### 自主学习模式

```bash
$ ./autonomous_learn.sh

选择 (1-4): 2
学习时长 (分钟，默认 30): 60

 启动自主学习模式...
预计运行时间：60 分钟

🔍 开始学习：rust
...
  休息 180 秒...

🔍 开始学习：go
...

⏰ 学习时间到！已运行 60.2 分钟
📊 学习报告已保存：memory/network_knowledge/learning_report.json
```

### 搜索知识

```bash
$ python3 memory_search.py

==================================================
  自进化 AI - 记忆检索系统
==================================================

命令:
  search <关键词> - 搜索知识
  topic <主题>    - 按主题查看
  recent          - 最近学习
  stats           - 统计信息
  export          - 导出摘要
  quit            - 退出

> search async

找到 3 条结果:
  [10 分] Python 异步编程
       标签：python, async
  [5 分] Rust async/await
       标签：rust, async
  [3 分] Go 协程
       标签：go, async
```

---

## 📁 文件结构

```
/workspace/self-evolving-ai/
├── network_learning.py      # 网络学习模块
├── memory_search.py         # 记忆检索模块
── autonomous_learn.sh      # 启动脚本
├── memory/
│   └── network_knowledge/   # 学习的知识存储
│       ├── *.json           # 知识条目
│       └── learning_report.json  # 学习报告
└── NETWORK_LEARNING_GUIDE.md  # 本指南
```

---

## 🎯 学习流程

```
─────────────┐
│  选择主题   │
└──────┬──────┘
       │
       ▼
─────────────┐
│  爬取网页   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  解析内容   │
──────┬──────┘
       │
       ▼
┌─────────────
│  提取代码   │
──────┬──────┘
       │
       ▼
┌─────────────┐
│  存储记忆   │
──────┬──────┘
       │
       ▼
┌─────────────┐
│  休息等待   │───→ 继续学习下一个
└─────────────┘
```

---

## ️ 配置选项

在 `network_learning.py` 中可以修改：

```python
# 添加新的学习源
self.learning_sources = {
    'your_topic': [
        'https://example.com/tutorial1',
        'https://example.com/tutorial2',
    ]
}

# 修改自主学习时长
await learner.autonomous_learn(duration_minutes=60)

# 修改每次学习的页面数
await learner.learn_topic('python', max_pages=10)
```

---

## 📊 学习报告

学习完成后会生成报告：

```json
{
  "total_sessions": 15,
  "topics_learned": [...],
  "log": [...],
  "generated_at": "2026-06-08T12:00:00"
}
```

---

## 🔍 知识检索

支持多种检索方式：

| 命令 | 说明 |
|------|------|
| `search <关键词>` | 全文搜索 |
| `topic <主题>` | 按主题筛选 |
| `recent` | 最近学习 |
| `stats` | 统计信息 |
| `export` | 导出 Markdown 摘要 |

---

## ⚠️ 注意事项

1. **网络请求**：学习过程需要联网
2. **请求频率**：自动延迟避免被封禁
3. **存储空间**：知识会保存到本地
4. **学习时长**：建议 30-60 分钟/次

---

## 🚧 未来计划

- [ ] 向量数据库支持（更精准的语义搜索）
- [ ] 多语言内容解析
- [ ] 知识图谱构建
- [ ] 自动代码实践
- [ ] 学习进度可视化

---

## 💡 现在就开始学习吧！

```bash
cd /workspace/self-evolving-ai
./autonomous_learn.sh
```

**让 AI 自己在网络上学习成长！** 
