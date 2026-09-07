# MilkTea Commerce Platform｜奶茶点单与门店运营平台

面向茶饮门店的全链路数字化运营平台。本人主要负责 Spring Boot 3 后端开发，围绕用户端和运营后台提供 REST API，覆盖订单履约、库存与会员营销、售后反馈和经营分析等核心业务域，实践 JWT 鉴权、事务化库存扣减、规则推荐和统一 API 契约。

## 后端开发重点

- 为用户端小程序和商家后台提供商品、订单、会员、营销和数据分析 API，维护统一响应模型与分页协议。
- 采用分层架构和 MyBatis-Plus 数据访问，完成 JWT 认证、角色访问控制、全局异常处理和参数边界校验。
- 订单流程包含金额计算、库存校验、事务控制、原子扣库存、取消恢复库存、退款申请和订单评价落库。
- 推荐模块基于用户历史购买分类进行规则推荐，并以销量商品处理冷启动和结果补齐。
- 与 Vue 3 管理后台、uni-app 用户端进行接口联调，完成从商品浏览到订单履约、售后反馈和运营分析的业务闭环。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 用户端 | uni-app、Vue 3、uView Plus、SCSS、Vite |
| 管理端 | Vue 3、Vue Router、Vuex、Element Plus、ECharts、Axios、Vite |
| 后端 | Java 17、Spring Boot 3.1、Spring Security、JWT、MyBatis-Plus、HikariCP、Hutool |
| 数据库 | MySQL 8.0 |
| 可选基础设施 | Redis 6+（配置预留） |

## 系统结构

```text
milk-tea/
├── milk-tea-backend/     # Spring Boot 后端服务，默认端口 8080，接口前缀 /api
├── milk-tea-admin/       # Vue 3 + Element Plus 商家管理后台
├── milk-tea-uniapp/      # uni-app 用户端，可构建微信小程序、H5 和 App
├── database/              # MySQL 初始化脚本和数据说明
├── docs/                  # API、系统设计和 Android 打包文档
└── milk-tea.apk           # Android 测试安装包
```

## 本地运行

### 1. 准备环境

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- Redis 6+（当前配置为可选）

### 2. 初始化数据库

```bash
mysql -u root -p -e "CREATE DATABASE milk_tea CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p milk_tea < database/init.sql
```

### 3. 配置后端

项目通过环境变量读取敏感配置，至少设置数据库密码和 JWT 密钥：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=replace-with-a-long-random-secret
```

如需使用 Redis，可设置 `REDIS_HOST`、`REDIS_PORT` 和 `REDIS_PASSWORD`，再启动后端：

```bash
cd milk-tea-backend
mvn spring-boot:run
```

服务地址：`http://localhost:8080/api`

### 4. 启动管理后台

```bash
cd milk-tea-admin
npm install
npm run dev
```

默认访问地址：`http://localhost:5173`

### 5. 启动用户端

```bash
cd milk-tea-uniapp
npm install
npm run dev:h5
```

微信小程序构建：

```bash
npm run build:mp-weixin
```

更多 App 打包说明见 [`docs/Android打包指南.md`](docs/Android打包指南.md)。

## 接口与文档

- [API 接口文档](docs/API文档.md)
- [系统设计文档](docs/系统设计文档.md)
- [后端说明](milk-tea-backend/README.md)
- [管理后台说明](milk-tea-admin/README.md)
- [uni-app 用户端说明](milk-tea-uniapp/README.md)

## 验证命令

```bash
# 后端
cd milk-tea-backend && mvn test

# 管理后台
cd milk-tea-admin && npm run build

# uni-app H5
cd milk-tea-uniapp && npm run build:h5
```

## 说明

项目中的支付、短信验证码、部分打印和地图能力包含测试环境实现，生产部署前需要接入真实服务并补充密钥管理、支付回调、库存锁定和自动化集成测试。仓库中的 `milk-tea.apk` 仅用于开发验证。

## 作者

- GitHub: [dev-cai](https://github.com/dev-cai)
- 技术博客: [CSDN](https://blog.csdn.net/weixin_45167912)
