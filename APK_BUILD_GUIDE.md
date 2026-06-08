# 📱 自进化 AI - 安卓手机 App 打包指南

## 快速打包成 APK

### 方法 1: 使用 Buildozer（推荐）

#### 步骤 1: 安装 Buildozer

```bash
# 在 Linux/Mac 上
pip install buildozer

# 在 Windows 上（需要 WSL）
# 先安装 WSL，然后在 WSL 中执行上述命令
```

#### 步骤 2: 安装依赖

```bash
cd /workspace/self-evolving-ai

# 安装 Python 依赖
pip install kivy requests aiohttp aiofiles

# 安装 Buildozer 依赖（Ubuntu/Debian）
sudo apt-get update
sudo apt-get install -y \
    git zip unzip openjdk-17-jdk \
    python3-pip autoconf libtool pkg-config \
    zlib1g-dev libncurses5-dev libncursesw5-dev \
    libtinfo5 cmake libffi-dev libssl-dev
```

#### 步骤 3: 初始化 Buildozer

```bash
# 如果还没有 buildozer.spec 文件
buildozer init

# 使用我们创建好的配置文件（已提供）
```

#### 步骤 4: 构建 APK

```bash
# 清理之前的构建（可选）
buildozer android clean

# 构建调试版 APK
buildozer android debug

# 或构建发布版 APK（需要签名）
buildozer android release
```

#### 步骤 5: 获取 APK

构建完成后，APK 文件位于：
```
./bin/selfevolvingai-1.0.0-debug.apk
```

---

### 方法 2: 使用 Google Colab（最简单）

如果本地环境复杂，可以用 Google Colab 云端构建：

```python
# 在 Colab 中运行以下代码

# 1. 克隆项目
!git clone https://github.com/your-username/self-evolving-ai.git
%cd self-evolving-ai

# 2. 安装 Buildozer
!pip install buildozer

# 3. 安装依赖
!sudo apt-get update
!sudo apt-get install -y git zip unzip openjdk-17-jdk python3-pip autoconf libtool pkg-config zlib1g-dev libncurses5-dev libncursesw5-dev libtinfo5 cmake libffi-dev libssl-dev

# 4. 构建
!buildozer android debug

# 5. 下载 APK
from google.colab import files
files.download('./bin/selfevolvingai-1.0.0-debug.apk')
```

---

### 方法 3: 使用 Docker（跨平台）

```bash
# 使用 Buildozer Docker 镜像
docker run --rm -v $(pwd):/app -it kivy/buildozer android debug

# APK 会输出到当前目录的 bin/ 文件夹
```

---

## 📲 安装到手机

### 方式 1: USB 传输

1. 用 USB 线连接手机和电脑
2. 将 APK 文件复制到手机
3. 在手机上打开 APK 安装

### 方式 2: 云存储

1. 上传 APK 到 Google Drive / 百度网盘
2. 在手机上下载
3. 打开安装

### 方式 3: 直接构建到手机

```bash
# 连接手机后直接安装
buildozer android debug deploy run
```

---

## 🎨 自定义应用

### 修改应用名称

编辑 `buildozer.spec`:
```ini
[app]
title = 你的应用名称
package.name = yourappname
package.domain = com.yourdomain
```

### 修改应用图标

1. 准备 512x512 PNG 图标
2. 命名为 `icon.png`
3. 放到项目根目录
4. 在 `buildozer.spec` 中取消注释：
```ini
icon.filename = %(source.dir)s/icon.png
```

### 修改界面

编辑 `mobile_app.py` 中的 UI 代码

---

## 🐛 常见问题

### 问题 1: Buildozer 安装失败

```bash
# 使用 pipx 安装
pip install pipx
pipx install buildozer
```

### 问题 2: Android SDK 缺失

```bash
# Buildozer 会自动下载，但也可以手动设置
export ANDROID_HOME=$HOME/.buildozer/android/platform/android-sdk
export ANDROID_NDK_HOME=$HOME/.buildozer/android/platform/android-ndk
```

### 问题 3: 构建超时

```bash
# 增加超时时间
export BUILDODEZER_TIMEOUT=3600
buildozer android debug
```

### 问题 4: 内存不足

```bash
# 限制 Gradle 内存
export GRADLE_OPTS="-Xmx2g"
```

---

## 📋 APK 构建检查清单

- [ ] 已安装 Java JDK 17
- [ ] 已安装 Python 3.8+
- [ ] 已安装 Kivy
- [ ] 已安装 Buildozer
- [ ] 已配置 `buildozer.spec`
- [ ] 有足够的磁盘空间（至少 5GB）
- [ ] 网络连接正常（需要下载 SDK）

---

## 🚀 快速命令参考

```bash
# 初始化
buildozer init

# 构建调试版
buildozer android debug

# 构建发布版
buildozer android release

# 部署到手机
buildozer android debug deploy run

# 清理构建
buildozer android clean

# 查看日志
buildozer android debug logcat

# 更新依赖
buildozer android update
```

---

## 📱 应用界面预览

```
┌─────────────────────────┐
│   🧠 自进化 AI 系统      │
├─────────────────────────┤
│ 状态：就绪              │
├─────────────────────────┤
│ [AI] 欢迎使用自进化 AI  │
│ 系统！我可以帮你：      │
│ • 创建新功能模块        │
│ • 学习编程语言          │
│ • 分析并改进代码        │
│                         │
│ [你] 创建一个日志模块   │
│                         │
│ [AI] 收到任务...        │
│ 正在执行:               │
│ 1. ✓ 分析需求           │
│ 2. ⏳ 检索知识          │
│ ...                     │
├─────────────────────────┤
│ [输入任务...]    [发送] │
├─────────────────────────┤
│ [📊 状态] [🔍 评估]    │
│ [📚 学习]               │
└─────────────────────────┘
```

---

## 🎯 下一步

1. **选择构建方法** - 本地/Colab/Docker
2. **执行构建命令** - `buildozer android debug`
3. **等待构建完成** - 首次构建约 20-30 分钟
4. **安装到手机** - 传输 APK 并安装
5. **开始使用** - 输入你的需求！

---

**构建完成后，你就有一个真正的安卓 App 了！** 📱✨
