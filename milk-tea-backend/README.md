# 奶茶小程序后端系统

基于 Spring Boot 3 + MyBatis Plus + JWT 的奶茶小程序后端系统。密码使用 BCrypt 存储，历史 MD5 密文在登录成功后惰性迁移；JWT 使用 30 分钟 access token + 7 天 refresh token。

## 技术栈

- **Spring Boot 3.1.5** - 主框架
- **MyBatis Plus 3.5.4.1** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **JWT** - 认证授权
- **微信支付开发测试流程** - 当前唯一支付渠道，需配置商户号、证书和回调后启用真实微信支付下单
- **Spring Security** - 安全框架
- **Lombok** - 简化代码
- **Hutool** - 工具类库

## 项目结构

```
src/main/java/com/milktea/
├── common/              # 通用类
│   ├── Result.java      # 统一响应结果
│   └── PageResult.java  # 分页结果
├── config/              # 配置类
│   ├── CorsConfig.java  # 跨域配置
│   ├── MybatisPlusConfig.java # MyBatis Plus配置
│   ├── SecurityConfig.java    # Spring Security配置
│   └── MetaObjectHandlerConfig.java # 自动填充配置
├── controller/          # 控制器
│   ├── AuthController.java    # 认证控制器
│   ├── UserController.java    # 用户控制器
│   ├── ProductController.java # 商品控制器
│   ├── OrderController.java   # 订单控制器
│   ├── CouponController.java  # 优惠券控制器
│   └── admin/              # 管理端控制器
│       ├── AdminProductController.java
│       ├── AdminOrderController.java
│       ├── AdminUserController.java
│       └── AdminCouponController.java
├── dto/                 # 数据传输对象
│   └── OrderCreateRequest.java
├── entity/              # 实体类
│   ├── User.java        # 用户实体
│   ├── UserAddress.java # 用户地址实体
│   ├── Category.java    # 商品分类实体
│   ├── Product.java     # 商品实体
│   ├── Order.java       # 订单实体
│   ├── OrderItem.java   # 订单项实体
│   ├── Coupon.java      # 优惠券实体
│   └── UserCoupon.java  # 用户优惠券实体
├── exception/           # 异常处理
│   ├── BusinessException.java      # 业务异常
│   └── GlobalExceptionHandler.java # 全局异常处理器
├── filter/              # 过滤器
│   └── JwtAuthenticationFilter.java # JWT认证过滤器
├── mapper/              # 数据访问层
│   ├── UserMapper.java
│   ├── UserAddressMapper.java
│   ├── CategoryMapper.java
│   ├── ProductMapper.java
│   ├── OrderMapper.java
│   ├── OrderItemMapper.java
│   ├── CouponMapper.java
│   └── UserCouponMapper.java
├── service/             # 服务层
│   ├── AuthService.java     # 认证服务
│   ├── UserService.java     # 用户服务
│   ├── ProductService.java  # 商品服务
│   ├── OrderService.java    # 订单服务
│   ├── CouponService.java   # 优惠券服务
│   └── AdminService.java    # 管理端服务
├── utils/               # 工具类
│   └── JwtUtils.java    # JWT工具类
└── MilkTeaApplication.java # 启动类
```

## 功能模块

### 用户端功能
- **用户认证**: 登录、注册、JWT Token认证
- **用户管理**: 个人信息管理、地址管理
- **商品浏览**: 分类查询、商品搜索、推荐商品、热销商品
- **订单管理**: 下单、订单查询、取消订单、申请退款
- **优惠券**: 优惠券领取、我的优惠券

### 管理端功能
- **商品管理**: 商品增删改查、上下架管理、批量操作
- **订单管理**: 订单查询、状态更新、退款处理、统计数据
- **用户管理**: 用户查询、状态管理、会员等级调整、积分管理
- **优惠券管理**: 优惠券发布、编辑、删除

## 数据库设计

### 主要数据表
- `user` - 用户表
- `user_address` - 用户地址表
- `category` - 商品分类表
- `product` - 商品表
- `orders` - 订单表
- `order_item` - 订单项表
- `coupon` - 优惠券表
- `user_coupon` - 用户优惠券表

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库初始化
```sql
-- 执行数据库初始化脚本
source src/main/resources/sql/init.sql

-- 插入示例数据
source src/main/resources/sql/data.sql
```

### 3. 配置文件
修改 `application.yml` 中的数据库和Redis连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/milk_tea
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动应用
```bash
mvn spring-boot:run
```

### 5. 访问接口
- 应用启动后访问: http://localhost:8080/api
- 测试接口: http://localhost:8080/api/test/hello
- 数据库测试: http://localhost:8080/api/database/test

## API文档

详细的API文档请参考项目根目录下的 `API文档.md` 文件。

### 主要接口

#### 认证接口
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册

#### 用户接口
- `GET /user/info` - 获取用户信息
- `PUT /user/info` - 更新用户信息
- `GET /user/addresses` - 获取用户地址列表
- `POST /user/address` - 添加用户地址

#### 商品接口
- `GET /product/page` - 分页查询商品
- `GET /product/{id}` - 获取商品详情
- `GET /product/categories` - 获取商品分类
- `GET /product/recommend` - 获取推荐商品

#### 订单接口
- `POST /order/create` - 创建订单
- `GET /order/list` - 获取用户订单列表
- `PUT /order/{id}/cancel` - 取消订单
- `PUT /order/{id}/refund` - 申请退款

## 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

### 普通用户账号
- 用户名: `user1`
- 密码: `123456`

## 开发说明

### 代码规范
- 使用Lombok简化代码
- 统一使用Result包装返回结果
- 异常统一处理
- 数据库字段自动填充

### 安全配置
- JWT Token认证
- 密码BCrypt加密
- 跨域配置
- 请求权限控制

## 项目特色

1. **完整的业务功能**: 涵盖奶茶小程序的所有核心功能
2. **规范的代码结构**: 分层清晰，职责明确
3. **完善的异常处理**: 统一的异常处理和响应格式
4. **安全的认证机制**: JWT + Spring Security
5. **灵活的权限控制**: 用户端和管理端分离
6. **详细的API文档**: 完整的接口说明和示例

## 联系方式

- 项目作者: MilkTea Team
- 邮箱: milktea@example.com
- 项目地址: https://github.com/milktea/backend
