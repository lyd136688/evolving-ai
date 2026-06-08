#!/bin/bash
# 一键打包 APK 脚本
# Quick APK Build Script

set -e

echo "========================================"
echo "  自进化 AI - APK 打包脚本"
echo "========================================"
echo ""

# 检查 Python
if ! command -v python3 &> /dev/null; then
    echo "❌ 错误：需要 Python 3"
    exit 1
fi
echo "✓ Python 版本：$(python3 --version)"

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "⚠️  警告：Java 未安装，Buildozer 会自动下载"
fi

# 安装依赖
echo ""
echo "正在安装 Python 依赖..."
pip3 install kivy requests aiohttp aiofiles buildozer

# 检查 buildozer.spec
if [ ! -f "buildozer.spec" ]; then
    echo "❌ 错误：找不到 buildozer.spec"
    exit 1
fi
echo "✓ buildozer.spec 已找到"

# 清理旧构建
echo ""
echo "清理旧构建..."
buildozer android clean 2>/dev/null || true

# 构建 APK
echo ""
echo "========================================"
echo "  开始构建 APK..."
echo "  首次构建可能需要 20-30 分钟"
echo "========================================"
echo ""

buildozer android debug

# 检查结果
if [ -f "./bin/"*"-debug.apk" ]; then
    echo ""
    echo "========================================"
    echo "  ✅ 构建成功!"
    echo "========================================"
    echo ""
    echo "APK 文件位置:"
    ls -lh ./bin/*-debug.apk
    echo ""
    echo "安装到手机:"
    echo "  1. 将 APK 传输到手机"
    echo "  2. 在手机上打开安装"
    echo ""
else
    echo ""
    echo "❌ 构建失败，请检查日志"
    exit 1
fi
