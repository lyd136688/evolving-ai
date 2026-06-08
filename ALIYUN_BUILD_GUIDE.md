# ☁️ 阿里云 Cloud IDE 构建 Android APK 完整指南

##  前提条件

- 阿里云账号（已注册）
- 基础 Git 知识
- 自进化 AI 项目文件

---

##  步骤 1: 登录阿里云 Cloud IDE

1. 访问：https://cloud-ide.aliyun.com/
2. 使用阿里云账号登录
3. 点击 **创建工作空间**

---

## 📝 步骤 2: 配置工作空间

### 选择模板

| 选项 | 推荐设置 |
|------|----------|
| **模板类型** | Android 开发 |
| **运行时** | Ubuntu 20.04 |
| **CPU** | 2 核 (最低) |
| **内存** | 4GB (推荐 8GB) |
| **存储** | 20GB |

### 环境变量

```bash
ANDROID_HOME=/opt/android-sdk
JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

---

##  步骤 3: 上传项目

### 方式 A: 使用 Git（推荐）

```bash
# 在 Cloud IDE 终端中
cd /workspace
git clone https://github.com/your-username/self-evolving-ai.git
cd self-evolving-ai

# 运行项目生成脚本
chmod +x generate_android_project.sh
./generate_android_project.sh
```

### 方式 B: 直接上传文件

1. 点击左侧 **文件** 图标
2. 右键 → **上传文件**
3. 选择所有项目文件
4. 上传完成后解压

---

## 📝 步骤 4: 配置 Android SDK

在 Cloud IDE 终端中运行：

```bash
# 检查 SDK 是否已安装
echo $ANDROID_HOME

# 如果没有，手动安装
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# 下载 Android 命令行工具
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mkdir -p $ANDROID_HOME/cmdline-tools
mv cmdline-tools $ANDROID_HOME/cmdline-tools/latest

# 安装 SDK 组件
yes | sdkmanager --platforms;android-34
yes | sdkmanager --build-tools;34.0.0
yes | sdkmanager --platform-tools
```

---

##  步骤 5: 构建 APK

### Debug 版本

```bash
cd SelfEvolvingAI

# 设置执行权限
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 输出位置
ls -lh app/build/outputs/apk/debug/
```

### Release 版本

```bash
# 需要先配置签名
# 创建 keystore.properties
cat > keystore.properties << 'EOF'
storePassword=your_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=../your-keystore.jks
EOF

# 构建 Release APK
./gradlew assembleRelease

# 输出位置
ls -lh app/build/outputs/apk/release/
```

---

## 📝 步骤 6: 下载 APK

### 方式 A: 通过文件浏览器

1. 在 Cloud IDE 左侧点击 **文件**
2. 导航到 `SelfEvolvingAI/app/build/outputs/apk/debug/`
3. 右键 `app-debug.apk`
4. 点击 **下载**

### 方式 B: 使用命令行

```bash
# 创建下载链接（如果有公网访问）
# 或使用阿里云 OSS 上传

# 安装 OSS 工具
pip install oss2

# 上传到 OSS（需要配置）
python upload_to_oss.py app/build/outputs/apk/debug/app-debug.apk
```

---

## 📝 步骤 7: 安装到手机

### 方式 A: 直接传输

1. 下载 APK 到电脑
2. 通过 USB/微信/QQ 传到手机
3. 在手机上点击安装

### 方式 B: 使用 ADB（需要连接设备）

```bash
# 连接手机
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚠️ 常见问题

### 问题 1: Gradle 同步失败

```bash
# 解决方案
./gradlew clean
./gradlew build --refresh-dependencies
```

### 问题 2: SDK 许可证未接受

```bash
# 接受所有许可证
yes | sdkmanager --licenses
```

### 问题 3: 内存不足

```bash
# 修改 gradle.properties
org.gradle.jvmargs=-Xmx1024m
```

### 问题 4: 构建超时

```bash
# 增加超时时间
./gradlew assembleDebug --max-workers 1
```

---

## 🔧 优化建议

### 1. 使用阿里云镜像

```bash
# 在 build.gradle.kts 中添加
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
}
```

### 2. 启用 Gradle 缓存

```bash
# 在 gradle.properties 中
org.gradle.caching=true
org.gradle.parallel=true
```

### 3. 使用构建缓存

```bash
# 首次构建后
./gradlew assembleDebug --build-cache
```

---

## 📊 构建时间参考

| 项目 | 时间 |
|------|------|
| 首次完整构建 | 15-25 分钟 |
| 增量构建 | 2-5 分钟 |
| 仅测试 | 1-3 分钟 |

---

## 🎯 完整命令清单

```bash
# 1. 克隆项目
git clone https://github.com/your-username/self-evolving-ai.git
cd self-evolving-ai

# 2. 生成 Android 项目
./generate_android_project.sh
cd SelfEvolvingAI

# 3. 配置环境
chmod +x gradlew
yes | sdkmanager --licenses

# 4. 构建
./gradlew assembleDebug

# 5. 查看输出
ls -lh app/build/outputs/apk/debug/
```

---

## 📱 安装后的应用

打开应用后，你将看到：

```
┌─────────────────────────┐
│    自进化 AI 系统      │
│   版本：1.0.0           │
├─────────────────────────┤
│                         │
│  [开始学习]  [搜索知识] │
│                         │
│  [自我评估]  [系统设置] │
│                         │
└─────────────────────────┘
```

---

## 🚀 现在开始

1. 登录阿里云 Cloud IDE
2. 上传项目
3. 运行构建命令
4. 下载 APK 安装

**祝你构建成功！** 
