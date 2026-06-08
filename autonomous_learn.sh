#!/bin/bash
# ============================================
# 自进化 AI - 自主学习启动脚本
# ============================================

set -e

echo "========================================"
echo "   🧠 自进化 AI - 自主学习系统"
echo "========================================"
echo ""

# 检查 Python
if ! command -v python3 &> /dev/null; then
    echo "❌ 错误：需要安装 Python 3"
    exit 1
fi
echo "✓ Python 已安装"

# 创建虚拟环境
if [ ! -d ".venv" ]; then
    echo " 创建虚拟环境..."
    python3 -m venv .venv
fi

# 激活虚拟环境
source .venv/bin/activate

# 安装依赖
echo " 安装依赖..."
pip install -q aiohttp beautifulsoup4

# 创建记忆目录
mkdir -p memory/network_knowledge

echo ""
echo "========================================"
echo "   启动选项"
echo "========================================"
echo ""
echo "1. 学习指定主题 (python/rust/go/android/ai)"
echo "2. 自主学习模式 (指定时长)"
echo "3. 搜索已学知识"
echo "4. 查看学习统计"
echo ""

read -p "选择 (1-4): " choice

case $choice in
    1)
        read -p "输入主题: " topic
        python3 network_learning.py <<< "$topic"
        ;;
    2)
        read -p "学习时长 (分钟，默认 30): " duration
        duration=${duration:-30}
        echo ""
        echo "🚀 开始自主学习，时长：$duration 分钟"
        echo "   按 Ctrl+C 随时停止"
        echo ""
        python3 network_learning.py <<< ""$duration""
        ;;
    3)
        python3 memory_search.py
        ;;
    4)
        python3 -c "
from memory_search import MemorySearch
s = MemorySearch()
stats = s.get_stats()
print(f'总知识：{stats[\"total_knowledge\"]} 条')
print(f'热门标签：{list(stats[\"tags\"].keys())[:5]}')
"
        ;;
    *)
        echo "无效选择"
        ;;
esac

echo ""
echo "========================================"
echo "   完成!"
echo "========================================"
