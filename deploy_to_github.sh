#!/bin/bash
# ============================================
# 自进化 AI - GitHub 一键部署脚本
# ============================================

set -e

echo "========================================"
echo "    GitHub 一键部署"
echo "========================================"
echo ""

# 检查 Git
if ! command -v git &> /dev/null; then
    echo "❌ 错误：需要安装 Git"
    echo "   安装：sudo apt-get install git"
    exit 1
fi
echo "✓ Git 已安装"

# 检查是否已初始化
if [ -d ".git" ]; then
    echo "⚠️  Git 已初始化"
    read -p "是否重新初始化？(y/N): " reinit
    if [ "$reinit" = "y" ]; then
        rm -rf .git
        echo "  已删除 .git 目录"
    else
        echo "  跳过 Git 初始化"
    fi
fi

# 初始化 Git
if [ ! -d ".git" ]; then
    echo "  初始化 Git..."
    git init
    echo "✅ Git 初始化完成"
fi

# 添加文件
echo ""
echo "  添加文件..."
git add .
echo "✅ 文件已添加"

# 提交
echo ""
read -p "输入提交信息 (默认：Initial commit): " commit_msg
commit_msg=${commit_msg:-"Initial commit: Self-Evolving AI"}
git commit -m "$commit_msg"
echo "✅ 提交完成"

# 切换分支
git branch -M main

# 输入 GitHub 信息
echo ""
echo "========================================"
echo "   GitHub 仓库信息"
echo "========================================"
echo ""
read -p "GitHub 用户名：" GITHUB_USER
read -p "仓库名：" REPO_NAME

# 验证输入
if [ -z "$GITHUB_USER" ] || [ -z "$REPO_NAME" ]; then
    echo "❌ 错误：用户名和仓库名不能为空"
    exit 1
fi

REMOTE_URL="https://github.com/$GITHUB_USER/$REPO_NAME.git"

echo ""
echo "仓库 URL: $REMOTE_URL"
echo ""

# 检查远程仓库
if git remote | grep -q "origin"; then
    echo "⚠️  origin 远程已存在"
    read -p "是否覆盖？(y/N): " overwrite
    if [ "$overwrite" = "y" ]; then
        git remote remove origin
    else
        echo "  跳过远程配置"
        REMOTE_URL=""
    fi
fi

# 添加远程仓库
if [ -n "$REMOTE_URL" ]; then
    git remote add origin $REMOTE_URL
    echo "✅ 远程仓库已添加"
fi

# 推送
echo ""
echo "========================================"
echo "   推送到 GitHub"
echo "========================================"
echo ""
echo "⚠️  请确保已在 GitHub 创建仓库："
echo "   https://github.com/new"
echo ""
read -p "是否继续推送？(y/N): " confirm_push

if [ "$confirm_push" = "y" ]; then
    echo "  推送中..."
    
    # 尝试推送
    if git push -u origin main; then
        echo ""
        echo "========================================"
        echo "   ✅ 部署成功！"
        echo "========================================"
        echo ""
        echo "📦 仓库：https://github.com/$GITHUB_USER/$REPO_NAME"
        echo "🔧 构建：https://github.com/$GITHUB_USER/$REPO_NAME/actions"
        echo ""
        echo "⏱️  构建时间：15-25 分钟"
        echo ""
        echo "📱 下载 APK:"
        echo "   1. 访问 Actions 页面"
        echo "   2. 点击最近的构建"
        echo "   3. 滚动到底部 Artifacts"
        echo "   4. 点击 app-debug 下载"
        echo ""
    else
        echo ""
        echo "❌ 推送失败"
        echo ""
        echo "可能原因:"
        echo "  1. 仓库不存在 - 请先在 GitHub 创建"
        echo "  2. 认证失败 - 检查 GitHub 登录"
        echo "  3. 网络问题 - 重试或使用代理"
        echo ""
        echo "手动推送命令:"
        echo "  git push -u origin main"
        echo ""
        exit 1
    fi
else
    echo ""
    echo "⏭️  跳过推送"
    echo ""
    echo "手动推送命令:"
    echo "  git remote add origin https://github.com/$GITHUB_USER/$REPO_NAME.git"
    echo "  git push -u origin main"
    echo ""
fi

echo "========================================"
echo "   完成!"
echo "========================================"
