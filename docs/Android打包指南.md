# UniApp Android 打包指南

## 方式一：使用 HBuilderX 云打包（推荐）

### 1. 准备工作

#### 1.1 安装 HBuilderX
- 下载地址：https://www.dcloud.io/hbuilderx.html
- 选择"App开发版"
- 安装完成后打开 HBuilderX

#### 1.2 导入项目
1. 打开 HBuilderX
2. 文件 → 导入 → 从本地目录导入
3. 选择 `milk-tea-uniapp` 目录
4. 点击"选择"完成导入

### 2. 配置 manifest.json

#### 2.1 基础配置
1. 在项目中找到 `src/manifest.json`
2. 点击"可视化界面"标签
3. 配置以下信息：

**应用标识**
- 应用名称：奶茶小程序
- 应用版本名称：1.0.0
- 应用版本号：100

**App图标配置**
- 准备不同尺寸的图标（建议1024x1024）
- 点击"自动生成所有图标"

#### 2.2 Android配置
1. 点击左侧"App常用其他设置"
2. 配置以下内容：

**包名（重要）**
```
com.milktea.app
```

**应用图标**
- 上传应用图标（1024x1024 PNG）

**启动图**
- 上传启动图（建议2208x2208）

**权限配置**（已在manifest.json中配置）
- ✅ 网络访问
- ✅ 相机
- ✅ 位置信息
- ✅ 读写存储

### 3. 云端打包

#### 3.1 开始打包
1. 点击菜单：发行 → 原生App-云打包
2. 选择打包平台：Android
3. 配置打包参数：

**打包类型**
- 测试版：使用公共证书（快速测试）
- 正式版：使用自有证书（上架应用商店）

**选择打包方式**
- ✅ 使用DCloud公共证书（测试用）
- 或 使用自有证书（正式发布）

#### 3.2 打包配置
```
打包类型：Android
应用名称：奶茶小程序
包名：com.milktea.app
版本号：1.0.0
版本Code：100
```

#### 3.3 等待打包
- 点击"打包"按钮
- 等待云端打包完成（通常5-10分钟）
- 打包完成后会显示下载链接

#### 3.4 下载APK
- 点击下载链接
- 保存APK文件到本地
- 文件名：`奶茶小程序_1.0.0.apk`

### 4. 安装测试

#### 4.1 安装到手机
1. 将APK文件传输到Android手机
2. 在手机上找到APK文件
3. 点击安装（需要允许安装未知来源应用）
4. 安装完成后打开应用

#### 4.2 测试功能
- ✅ 登录注册
- ✅ 浏览商品
- ✅ 下单支付
- ✅ 订单管理
- ✅ 投诉反馈
- ✅ 个人中心

## 方式二：本地打包（高级）

### 1. 环境准备

#### 1.1 安装 Android Studio
- 下载地址：https://developer.android.com/studio
- 安装 Android SDK
- 配置环境变量

#### 1.2 安装 Gradle
- 下载地址：https://gradle.org/releases/
- 配置环境变量

### 2. 生成本地打包资源

#### 2.1 使用 HBuilderX
1. 打开项目
2. 发行 → 原生App-本地打包 → 生成本地打包App资源
3. 等待资源生成完成
4. 资源位置：`unpackage/resources`

#### 2.2 下载离线SDK
1. 访问：https://nativesupport.dcloud.net.cn/AppDocs/download/android
2. 下载最新版本的Android离线SDK
3. 解压到本地目录

### 3. 配置Android项目

#### 3.1 导入项目
1. 打开 Android Studio
2. 打开离线SDK中的 `HBuilder-Integrate-AS` 项目
3. 等待Gradle同步完成

#### 3.2 替换资源
1. 将生成的资源复制到项目中
2. 路径：`app/src/main/assets/apps/__UNI__MILKTEA/`
3. 替换 `www` 目录

#### 3.3 配置应用信息
编辑 `app/build.gradle`：
```gradle
android {
    defaultConfig {
        applicationId "com.milktea.app"
        versionCode 100
        versionName "1.0.0"
    }
}
```

### 4. 生成签名证书

#### 4.1 创建密钥库
```bash
keytool -genkey -v -keystore milktea.keystore -alias milktea -keyalg RSA -keysize 2048 -validity 10000
```

参数说明：
- 密钥库名称：milktea.keystore
- 别名：milktea
- 密码：设置一个安全的密码
- 有效期：10000天

#### 4.2 配置签名
编辑 `app/build.gradle`：
```gradle
android {
    signingConfigs {
        release {
            storeFile file("milktea.keystore")
            storePassword "your_password"
            keyAlias "milktea"
            keyPassword "your_password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### 5. 打包APK

#### 5.1 构建Release版本
```bash
./gradlew assembleRelease
```

#### 5.2 输出位置
```
app/build/outputs/apk/release/app-release.apk
```

## 方式三：使用命令行打包

### 1. 构建App资源

```bash
cd milk-tea-uniapp
npm run build:app
```

### 2. 输出位置
```
dist/build/app/
```

### 3. 后续步骤
- 将构建产物导入 HBuilderX
- 使用云打包或本地打包

## 常见问题

### Q1: 打包失败怎么办？
**A:** 检查以下几点：
1. manifest.json 配置是否正确
2. 包名是否符合规范（小写字母+点）
3. 版本号是否正确
4. 网络连接是否正常

### Q2: 安装时提示"解析包时出现问题"
**A:** 可能原因：
1. APK文件损坏，重新下载
2. 手机系统版本过低
3. 签名证书问题

### Q3: 应用闪退怎么办？
**A:** 检查：
1. API地址配置是否正确
2. 权限是否授予
3. 查看日志定位问题

### Q4: 如何修改API地址？
**A:** 在 `milk-tea-uniapp` 根目录创建 `.env.production`，配置正式 HTTPS API 地址。构建工具会通过 `VITE_API_BASE_URL` 注入，无需修改源码：
```javascript
VITE_API_BASE_URL=https://api.example.com/api
```
开发环境可在 `.env.development` 中使用本地地址，例如 `http://localhost:8080/api`。

### Q5: 如何上架应用商店？
**A:** 需要：
1. 使用自有证书打包
2. 准备应用截图和描述
3. 注册开发者账号
4. 提交审核

## 打包清单

### 打包前检查
- [ ] 修改API地址为正式服务器地址
- [ ] 配置应用图标和启动图
- [ ] 设置正确的包名和版本号
- [ ] 测试所有功能正常
- [ ] 准备签名证书（正式版）

### 打包后检查
- [ ] 安装测试
- [ ] 功能测试
- [ ] 性能测试
- [ ] 兼容性测试

## 推荐配置

### 应用信息
```
应用名称：奶茶小程序
包名：com.milktea.app
版本名称：1.0.0
版本号：100
```

### 图标尺寸
- 应用图标：1024x1024 PNG
- 启动图：2208x2208 PNG
- 通知图标：96x96 PNG

### 最低支持版本
- Android 5.0 (API Level 21)
- 推荐 Android 8.0 及以上

## 相关链接

- HBuilderX 官网：https://www.dcloud.io/hbuilderx.html
- UniApp 文档：https://uniapp.dcloud.net.cn/
- Android 打包文档：https://uniapp.dcloud.net.cn/tutorial/app-android.html
- 离线打包文档：https://nativesupport.dcloud.net.cn/AppDocs/README

## 技术支持

如遇到问题，可以：
1. 查看 UniApp 官方文档
2. 访问 DCloud 社区论坛
3. 查看项目 README.md
4. 联系技术支持
