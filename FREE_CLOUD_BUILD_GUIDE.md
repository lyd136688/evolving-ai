# 🆓 免费云端构建 Android APK 完整指南

##  方案对比

| 平台 | 免费额度 | 构建时间 | 难度 | 推荐 |
|------|----------|----------|------|------|
| **GitHub Actions** | 2000 分钟/月 | 15-25 分钟 | ⭐⭐ | ✅ 最推荐 |
| **GitLab CI** | 400 分钟/月 | 15-25 分钟 | ⭐⭐⭐ | ✅ |
| **腾讯云 Cloud Studio** | 有限 | 20-30 分钟 | ⭐⭐ | ✅ |
| **Replit** | 有限 | 30+ 分钟 | ⭐⭐⭐⭐ | ⚠️ |

---

##  方案 1: GitHub Actions（最推荐）

### 优势
- ✅ 每月 2000 分钟免费
- ✅ 自动构建，无需手动操作
- ✅ APK 自动存储到 Release
- ✅ 支持定时构建
- ✅ 集成 Git 版本控制

### 步骤 1: 创建 GitHub 仓库

```bash
# 在本地初始化 Git
cd /workspace/self-evolving-ai
git init
git add .
git commit -m "Initial commit: Self-Evolving AI"

# 在 GitHub 创建新仓库（不要初始化）
# https://github.com/new
# 仓库名：self-evolving-ai
```

### 步骤 2: 推送代码到 GitHub

```bash
# 关联远程仓库（替换为你的仓库）
git remote add origin https://github.com/YOUR_USERNAME/self-evolving-ai.git

# 推送代码
git branch -M main
git push -u origin main
```

### 步骤 3: 自动构建

推送代码后，GitHub Actions 会**自动触发构建**！

查看构建进度：
```
https://github.com/YOUR_USERNAME/self-evolving-ai/actions
```

### 步骤 4: 下载 APK

构建完成后：

**方式 A: 从 Actions 下载**
1. 进入 **Actions** 标签
2. 点击最近的构建
3. 滚动到底部 **Artifacts**
4. 点击 `app-debug` 下载

**方式 B: 从 Release 下载**（需要配置）
```yaml
# 修改 .github/workflows/android-build.yml
# 添加发布到 Release 的步骤
```

### 步骤 5: 手动触发构建

在仓库页面：
1. 点击 **Actions**
2. 选择 **Android APK Build**
3. 点击 **Run workflow**
4. 选择分支，点击 **Run workflow**

---

## 🥈 方案 2: GitLab CI

### 优势
- ✅ 每月 400 分钟免费
- ✅ 私有仓库免费
- ✅ 集成 Docker

### 步骤 1: 创建 GitLab 仓库

```
https://gitlab.com/
→ New Project → Create blank project
```

### 步骤 2: 创建 .gitlab-ci.yml

```yaml
image: openjdk:17-jdk

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"

before_script:
  - chmod +x gradlew
  - mkdir -p $ANDROID_HOME/licenses
  - echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > $ANDROID_HOME/licenses/android-sdk-license

build:
  stage: build
  script:
    - ./gradlew assembleDebug
  artifacts:
    paths:
      - app/build/outputs/apk/debug/app-debug.apk
    expire_in: 1 week
```

### 步骤 3: 推送代码

```bash
git remote add gitlab https://gitlab.com/YOUR_USERNAME/self-evolving-ai.git
git push -u gitlab main
```

### 步骤 4: 查看构建

```
https://gitlab.com/YOUR_USERNAME/self-evolving-ai/-/pipelines
```

---

## 🥉 方案 3: 腾讯云 Cloud Studio

### 优势
- ✅ 国内访问快
- ✅ 微信登录
- ✅ 图形界面

### 步骤 1: 登录

```
https://cloudstudio.net/
→ 微信/QQ 登录
```

### 步骤 2: 创建工作空间

1. 点击 **创建工作空间**
2. 选择 **Android 开发** 模板
3. 配置：CPU 2 核，内存 4GB

### 步骤 3: 导入项目

```bash
# 方式 A: Git 克隆
git clone https://github.com/YOUR_USERNAME/self-evolving-ai.git

# 方式 B: 上传文件
# 点击文件 → 上传 → 选择所有文件
```

### 步骤 4: 构建

```bash
cd self-evolving-ai
./generate_android_project.sh
cd SelfEvolvingAI
chmod +x gradlew
./gradlew assembleDebug
```

### 步骤 5: 下载

1. 左侧文件浏览器
2. 导航到 `app/build/outputs/apk/debug/`
3. 右键 `app-debug.apk` → 下载

---

## 📊 GitHub Actions 详细配置

### 完整配置文件

[.github/workflows/android-build.yml](omnibot://workspace/self-evolving-ai/.github/workflows/android-build.yml)

### 自定义选项

```yaml
# 修改构建触发条件
on:
  push:
    branches: [ main ]           # 仅 main 分支
  schedule:
    - cron: '0 2 * * *'          # 每天 2AM 自动构建
  workflow_dispatch:             # 手动触发

# 修改 Java 版本
- uses: actions/setup-java@v4
  with:
    java-version: '17'

# 修改缓存策略
- uses: gradle/gradle-build-action@v3
  with:
    cache-read-only: false
```

### 添加签名构建（可选）

```yaml
- name: 解码签名文件
  run: |
    echo "${{ secrets.KEYSTORE }}" | base64 -d > keystore.jks

- name: 构建签名 APK
  run: ./gradlew assembleRelease
  env:
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

---

## ⚡ 快速开始脚本

### 一键初始化 GitHub 仓库

```bash
#!/bin/bash
# init_github.sh

echo "🔧 初始化 GitHub 仓库..."

# 初始化 Git
git init
git add .
git commit -m "Initial commit: Self-Evolving AI"

# 输入仓库信息
read -p "GitHub 用户名：" GITHUB_USER
read -p "仓库名：" REPO_NAME

# 关联远程
git remote add origin https://github.com/$GITHUB_USER/$REPO_NAME.git

# 推送
git branch -M main
git push -u origin main

echo "✅ 完成！"
echo "查看构建：https://github.com/$GITHUB_USER/$REPO_NAME/actions"
```

---

## 📱 构建时间参考

| 阶段 | 时间 |
|------|------|
| 检出代码 | 30 秒 |
| 设置 Java | 1 分钟 |
| Gradle 缓存 | 2-5 分钟 |
| 下载依赖 | 5-10 分钟 |
| 构建 APK | 5-10 分钟 |
| **总计** | **15-25 分钟** |

---

## ⚠️ 常见问题

### 问题 1: 构建失败 - SDK 许可证

```bash
# 解决方案：在 workflow 中添加
- name: 接受许可证
  run: |
    mkdir -p $ANDROID_HOME/licenses
    echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > $ANDROID_HOME/licenses/android-sdk-license
```

### 问题 2: 内存不足

```yaml
# 修改 gradle.properties
org.gradle.jvmargs=-Xmx2048m
```

### 问题 3: 构建超时

```yaml
# 增加超时
- name: 构建 APK
  timeout-minutes: 60
  run: ./gradlew assembleDebug
```

### 问题 4: 网络慢（国内）

```yaml
# 使用国内镜像
- name: 配置镜像
  run: |
    echo "repositories {
      maven { url 'https://maven.aliyun.com/repository/google' }
      maven { url 'https://maven.aliyun.com/repository/public' }
    }" >> build.gradle.kts
```

---

## 🎯 推荐流程

```
1. 创建 GitHub 账号
   └→ https://github.com/signup

2. 创建新仓库
   └→ https://github.com/new

3. 推送代码
   └→ git push -u origin main

4. 等待自动构建
   └→ 15-25 分钟

5. 下载 APK
   └→ Actions → Artifacts → 下载
```

---

## 💡 进阶技巧

### 1. 自动发布到 Release

```yaml
- name: 创建 Release
  uses: softprops/action-gh-release@v1
  with:
    files: app/build/outputs/apk/debug/app-debug.apk
    tag_name: v${{ github.run_number }}
```

### 2. 定时构建

```yaml
on:
  schedule:
    - cron: '0 2 * * *'  # 每天 2AM UTC
```

### 3. 发送通知

```yaml
- name: 发送通知
  uses: 8398a7/action-slack@v3
  with:
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---

## 🚀 现在开始

### 最快方式

```bash
cd /workspace/self-evolving-ai

# 1. 初始化 Git
git init
git add .
git commit -m "Initial commit"

# 2. 在 GitHub 创建仓库
# https://github.com/new

# 3. 关联并推送
git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git
git push -u origin main

# 4. 等待构建完成
# https://github.com/YOUR_USERNAME/REPO_NAME/actions
```

**完全免费，自动构建，无需任何云服务！** 🎉
