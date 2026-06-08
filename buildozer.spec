[app]

# (str) 应用标题
title = 自进化 AI

# (str) 应用包名
package.name = selfevolvingai

# (str) 应用域名（反向域名表示）
package.domain = org.omnibot

# (str) 应用源码目录
source.dir = .

# (list) 包含的源码文件
source.include_exts = py,png,jpg,kv,atlas,json,md

# (str) 应用版本
version = 1.0.0

# (list) 应用依赖的 Python 模块
requirements = python3,kivy==2.3.0,requests,aiohttp,aiofiles

# (str) 应用图标
# icon.filename = %(source.dir)s/icon.png

# (str) 应用方向
orientation = portrait

# (bool) 是否全屏
fullscreen = 0

# (list) Android 权限
android.permissions = INTERNET,ACCESS_NETWORK_STATE,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE

# (int) Android API 级别
android.api = 31

# (int) Android 最小 API 级别
android.minapi = 21

# (str) Android NDK 版本
android.ndk = 25b

# (bool) 是否使用 Android 支持库
android.support_lib = android.support.v4

# (list) 额外的 Android 库
android.extra_libs =

# (str) Android 应用入口
android.entrypoint = org.kivy.android.PythonActivity

# (str) Android 应用主题
android.theme = @android:style/Theme.NoTitleBar

# (bool) 是否接受 Android 许可证
android.accept_sdk_license = True

# (list) 构建时排除的模块
# android.blacklist_modules =

# (str) 应用输出目录
output.dir = ./bin

# (bool) 是否备份应用数据
android.backup_rules =

# (str) 应用版本代码
android.version_code = 1

# (str) 应用签名密钥（可选）
# android.keystore = /path/to/keystore
# android.keyalias = mykey
# android.keypw = password

# (list) 额外的 Android 功能
android.features = android.hardware.usb.host

# (bool) 是否允许备份
android.allow_backup = True

# (str) 应用图标自适应背景色
# android.icon_background_color = #FFFFFF

[buildozer]

# (list) 构建目标平台
platforms = android

# (int) 日志级别
log_level = 2

# (bool) 是否警告未使用的配置
warn_on_root = 1

# (str) 构建目录
build_dir = ./.buildozer

# (str) 缓存目录
cache_dir = ./.buildozer/cache

# (str) 构建配置文件
# build_profile = default
