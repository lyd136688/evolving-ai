#!/bin/bash
# ============================================
# Android 开发 Agent 一键配置脚本
# ============================================
# 自动克隆 NowInAndroid Agent Kit 并配置 Android Skills

set -e

echo "========================================"
echo "   Android 开发 Agent 配置工具"
echo "========================================"
echo ""

# 检查 Git
if ! command -v git &> /dev/null; then
    echo "❌ 错误：需要安装 Git"
    exit 1
fi
echo "✓ Git 已安装"

# 检查 Android CLI（可选）
if command -v android &> /dev/null; then
    echo "✓ Android CLI 已安装"
    HAS_ANDROID_CLI=true
else
    echo "️  Android CLI 未安装（可选）"
    HAS_ANDROID_CLI=false
fi

# 询问项目目录
echo ""
read -p "请输入项目目录（默认：当前目录）: " PROJECT_DIR
PROJECT_DIR=${PROJECT_DIR:-.}

# 创建目录
mkdir -p "$PROJECT_DIR"
cd "$PROJECT_DIR"

echo ""
echo "📦 正在克隆 NowInAndroid Agent Kit..."
if [ -d "nowinandroid-agent-kit" ]; then
    echo "⚠️  nowinandroid-agent-kit 已存在，跳过克隆"
else
    git clone https://github.com/warrenth/nowinandroid-agent-kit.git
fi

echo ""
echo "⚙️  配置 AI Agent Harness..."

# 创建 .claude 目录结构
mkdir -p .claude/rules
mkdir -p .claude/skills
mkdir -p .claude/agents

# 复制 Rules
if [ -d "nowinandroid-agent-kit/rules" ]; then
    cp -r nowinandroid-agent-kit/rules/* .claude/rules/ 2>/dev/null || true
    echo "✓ 已复制 8 个 Rules"
fi

# 复制 Skills
if [ -d "nowinandroid-agent-kit/skills" ]; then
    cp -r nowinandroid-agent-kit/skills/* .claude/skills/ 2>/dev/null || true
    echo "✓ 已复制 8 个 Skills"
fi

# 复制 Agents
if [ -d "nowinandroid-agent-kit/agents" ]; then
    cp -r nowinandroid-agent-kit/agents/* .claude/agents/ 2>/dev/null || true
    echo "✓ 已复制 4 个 Agents"
fi

# 使用 Android CLI 安装官方 Skills（如果已安装）
if [ "$HAS_ANDROID_CLI" = true ]; then
    echo ""
    echo "📚 正在安装 Google Android Skills..."
    android skills add --all --project=. 2>/dev/null || echo "⚠️  Android Skills 安装失败（可手动安装）"
fi

# 创建 AGENTS.md 配置文件
cat > .claude/AGENTS.md << 'EOF'
# Android 开发 Agent 配置

## 已加载的 Agent

1. **NowInAndroid Agent Kit**
   - 8 Rules（始终加载）
   - 8 Skills（按需调用）
   - 4 Agents（自主执行）

2. **Google Android Skills**
   - 官方 Android 最佳实践
   - LLM 专用精准指令

## 架构规范

- Clean Architecture + MVVM
- Jetpack Compose + Material 3
- Hilt 依赖注入
- Room 数据库
- Navigation 3

## 代码规范

- Kotlin 2.3
- AGP 9.0
- Compose BOM 2025.09
- 状态提升
- 重组优化
- Fakes 优于 Mocks
EOF

echo ""
echo "========================================"
echo "  ✅ 配置完成!"
echo "========================================"
echo ""
echo "📁 项目结构:"
echo "  $PROJECT_DIR/"
echo "  ├── .claude/"
echo "  │   ├── rules/     (8 个规则)"
echo "  │   ├── skills/    (8 个技能)"
echo "  │   ├── agents/    (4 个角色)"
echo "  │   └── AGENTS.md  (配置说明)"
echo "  └── nowinandroid-agent-kit/  (原始项目)"
echo ""
echo " 下一步:"
echo "  1. 在 IDE 中打开项目"
echo "  2. 运行 ./generate.sh 生成完整项目"
echo "  3. 开始 Android 开发!"
echo ""
