# 奶茶小程序系统设计文档

## 1. 系统概述

### 1.1 项目背景
本项目是一个完整的奶茶小程序系统，包含用户端小程序和商家管理后台，采用前后端分离架构设计。

### 1.2 系统目标
- 为用户提供便捷的奶茶订购体验
- 为商家提供完善的订单和商品管理功能
- 支持会员体系和营销活动管理

### 1.3 技术架构
- 后端：Spring Boot 3 + MyBatis Plus + JWT + Redis
- 前端(小程序)：uni-app+vue3
- 前端(管理后台)：Vue 3 + Element Plus + Vite
- 数据库：MySQL 8.0

## 2. 系统架构设计

### 2.1 整体架构
```
┌─────────────────┐    ┌─────────────────┐
│   小程序前端     │    │   管理后台       │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────┬───────────┘
                     │
         ┌─────────────────┐
         │   后端API服务    │
         └─────────────────┘
                     │
         ┌─────────────────┐
         │   MySQL数据库    │
         └─────────────────┘
```

### 2.2 技术选型说明
- **Spring Boot**: 简化配置，快速开发
- **MyBatis Plus**: 提高开发效率，减少SQL编写
- **JWT**: 无状态认证，支持分布式
- **Redis**: 缓存热点数据，提升性能
- **Vue 3**: 现代化前端框架
- **Element Plus**: 丰富的UI组件库



## 3. 部署流程

### 3.1 环境要求

#### 开发环境
- **JDK**: 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (可选)
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA / VS Code


### 3.2 本地开发部署

#### 3.2.1 克隆项目
```bash
git clone https://github.com/pahhcn/milk-tea.git
cd milk-tea-system
```

#### 3.2.2 数据库初始化

1. **创建数据库**
```sql
CREATE DATABASE milk_tea CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **导入数据库脚本**（按顺序执行）
```bash
# 导入数据库结构和基础数据
mysql -u root -p milk_tea < database/milk_tea.sql

```

#### 3.2.3 后端部署

1. **配置数据库连接**

编辑 `milk-tea-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/milk_tea?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的数据库密码
```


2. **启动后端服务**

```bash
cd milk-tea-backend

# 使用Maven启动
mvn clean spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/milk-tea-backend-1.0.0.jar
```

后端服务将在 `http://localhost:8080/api` 启动

#### 3.2.4 管理后台部署

1. **安装依赖**
```bash
cd milk-tea-admin
npm install
npm install -D sass
```

2. **配置API地址**（如果需要）

编辑 `milk-tea-admin/src/utils/request.js`：
```javascript
const request = axios.create({
  baseURL: '/api',  // 开发环境使用代理
  timeout: 10000
})
```

3. **启动开发服务器**
```bash
npm run dev
```

管理后台将在 `http://localhost:5173` 启动

默认管理员账号：
- 用户名：`admin`
- 密码：`123456`

#### 3.2.5 小程序端部署

1. **安装依赖**
```bash
cd milk-tea-uniapp
npm install
```

2. **配置API地址**

编辑 `milk-tea-uniapp/src/config/index.js`（如果存在）或相关配置文件

3. **启动开发服务器**
```bash
npm run dev:mp-weixin  # 微信小程序
npm run dev:h5         # H5
```

4. **使用微信开发者工具**
- 打开微信开发者工具
- 导入项目：选择 `milk-tea-uniapp/dist/dev/mp-weixin` 目录
- 开始调试



