#  Android 开发 Agent 配置

> 以后开发安卓应用自动使用以下两个 Agent

---

## 📦 Agent 1: NowInAndroid Agent Kit

**GitHub**: https://github.com/warrenth/nowinandroid-agent-kit

### 核心能力
| 类型 | 数量 | 说明 |
|------|------|------|
| Rules | 8 | 始终加载，保持 AI 遵循最佳实践 |
| Skills | 8 | 按需调用，深度知识 |
| Agents | 4 | 自主执行不同角色任务 |

### 技术栈
- **架构**: Clean Architecture + MVVM + Hilt
- **UI**: Jetpack Compose + Material 3
- **数据**: Room + DataStore + Retrofit
- **版本**: AGP 9.0, Kotlin 2.3, Compose BOM 2025.09, Hilt 2.59, Room 2.8.3, Navigation3

### Rules（规则）
| 规则 | 作用 |
|------|------|
| `architecture` | Clean Architecture 分层，ViewModel/UseCase/Repository |
| `module-boundary` | 模块 API/Impl 分离，依赖方向控制 |
| `compose-rules` | 状态提升、重组优化、副作用处理 |
| `coroutines` | Scope 管理、Flow 收集、CancellationException |
| `kotlin-style` | 格式化、命名、空安全 |
| `naming-convention` | NIA 类/函数/资源命名规范 |
| `testing` | Fakes 优于 Mocks、Turbine、AAA 模式 |
| `performance` | 重组优化、图片加载、启动速度 |

### Skills（技能）
| 技能 | 教学内容 |
|------|----------|
| `compose-animation` | 7 种动画模式（可见性、旋转、关键帧、交错、状态机、画布、手势） |
| `compose-performance` | 稳定性、graphicsLayer、derivedStateOf |
| `compose-navigation` | 类型安全路由、自适应导航 |
| `compose-testing` | ViewModel/UI/截图测试 |
| `compose-migration` | XML → Compose 迁移策略 |
| `hilt-di` | @Binds/@Provides、约定插件 |
| `coroutines-flow` | StateFlow、并行同步、变更列表 |
| `room-offline` | 离线优先、Entity/DTO/Domain 映射 |

### Agents（角色）
| Agent | 职责 |
|-------|------|
| `architect` | 模块设计、依赖图 |
| `coder` | 功能实现 |
| `code-reviewer` | 架构合规审查 |
| `tester` | 单元/UI/截图测试 |

### 快速使用
```bash
# 克隆项目
git clone https://github.com/warrenth/nowinandroid-agent-kit.git
cd nowinandroid-agent-kit

# 生成项目
./generate.sh
# 输入：项目名称、包名、输出目录

# 或单独复制技能
cp -r skills/compose-animation/ your-project/.claude/skills/
cp -r rules/ your-project/.claude/rules/
```

---

## 📦 Agent 2: Google Android Skills (官方)

**GitHub**: https://github.com/android/skills

### 核心能力
- 谷歌官方 Android 技能集
- LLM 专用精准指令
- 遵循 developer.android.com 最佳实践
- 专注 LLM 薄弱领域

### 技能目录
```
android/skills/
├── build/agp/          # AGP 构建配置
├── camera/             # CameraX 迁移
├── device-ai/          # 设备 AI 功能
├── devtools/           # 开发工具
── identity/           # 身份验证
── jetpack-compose/    # Compose 最佳实践
├── navigation/         # Navigation 3
├── performance/        # R8 分析器
└── play/               # Google Play
```

### 快速使用
```bash
# 使用 Android CLI 安装技能
# 安装特定技能
android skills add --skill=r8-analyzer --project=.

# 安装所有技能
android skills add --all

# 指定 Agent
android skills add --all --agent=gemini,claude
```

### 安装 Android CLI
```bash
# macOS
brew install android-cli

# 或手动安装
git clone https://github.com/android/cli.git
cd cli && ./install.sh
```

---

## 🎯 自动使用策略

以后创建 Android 项目时，自动执行：

1. **项目初始化**
   ```bash
   git clone https://github.com/warrenth/nowinandroid-agent-kit.git
   cd nowinandroid-agent-kit
   ./generate.sh
   ```

2. **安装官方技能**
   ```bash
   android skills add --all --project=.
   ```

3. **配置 Agent 规则**
   - 在 `.claude/rules/` 中启用所有 8 个规则
   - 在 `.claude/skills/` 中按需选择技能

---

## 📝 许可证

| 项目 | 许可证 |
|------|--------|
| NowInAndroid Agent Kit | MIT |
| Google Android Skills | Apache License 2.0 |

---

## 📚 相关资源

- [NowInAndroid 官方项目](https://github.com/android/nowinandroid)
- [Android 开发者文档](https://developer.android.com)
- [Android Skills 文档](https://github.com/android/skills)
- [Android CLI 文档](https://github.com/android/cli)

---

*最后更新：2026-06-08*
