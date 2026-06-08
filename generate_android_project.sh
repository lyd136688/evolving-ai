#!/bin/bash
# ============================================
# 自进化 AI - Android 项目生成器
# 基于 NowInAndroid 架构
# ============================================

set -e

PROJECT_NAME="SelfEvolvingAI"
PACKAGE_NAME="org.omnibot.selfevolvingai"

echo "========================================"
echo "   🧠 自进化 AI - Android 项目生成"
echo "========================================"
echo ""

# 创建项目目录
mkdir -p $PROJECT_NAME
cd $PROJECT_NAME

echo "📁 创建项目结构..."

# 创建 Gradle 配置
mkdir -p gradle/wrapper
mkdir -p app/src/main/java/org/omnibot/selfevolvingai
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/kotlin/org/omnibot/selfevolvingai

# 创建 settings.gradle.kts
cat > settings.gradle.kts << 'EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SelfEvolvingAI"
include(":app")
EOF

# 创建 build.gradle.kts (项目级)
cat > build.gradle.kts << 'EOF'
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
EOF

echo "✅ Gradle 配置完成"
echo "📂 项目位置：$(pwd)"
