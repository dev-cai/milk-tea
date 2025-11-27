# 奶茶小程序系统 - 答辩PPT大纲

## 封面页
**标题**: 基于Spring Boot + Vue3 + uni-app的奶茶小程序系统设计与实现
**副标题**: 三端分离架构的O2O电商平台
**答辩人**: [姓名]
**指导老师**: [教师姓名]
**答辩时间**: [日期]

---

## 第一部分：项目概述 (3分钟)

### 1.1 项目背景与意义
- **行业背景**: 新式茶饮市场规模突破3000亿，移动点单成为主流
- **项目意义**: 提升用户体验、帮助商家数字化转型、实现精准营销

### 1.2 项目目标
- 用户端: 便捷、个性化的点单体验
- 商家端: 高效的订单和商品管理
- 技术端: 可扩展、高性能的系统架构

### 1.3 系统特色
1. **三端分离架构**: 小程序端、管理后台、后端API完全解耦
2. **个性化定制**: 甜度、温度、加料等多维度定制
3. **会员积分体系**: 完整的会员等级和积分兑换系统
4. **智能营销**: 优惠券自动匹配，精准推送

---

## 第二部分：需求分析 (4分钟)

### 2.1 功能需求

#### 小程序端核心功能
1. **用户中心**: 微信登录、会员等级、积分系统、地址管理
2. **商品订购**: 个性化推荐、多级分类、定制点单、购物车
3. **订单管理**: 订单流程、状态追踪、售后功能、评价晒图
4. **营销活动**: 优惠券、会员特权、促销活动、积分商城

#### 管理后台核心功能
1. **仪表盘**: 数据概览、销售趋势、经营分析
2. **商品管理**: 上下架、库存管理、分类管理
3. **订单管理**: 订单处理、售后处理、打印管理
4. **会员管理**: 会员信息、消费分析、消息推送
5. **营销管理**: 活动管理、优惠券、广告管理
6. **系统管理**: 员工管理、门店设置、系统配置

### 2.2 非功能性需求
- **安全**: JWT认证、数据加密、权限控制、防护措施
- **可扩展**: 模块化设计，支持多门店、多地区扩展

---

## 第三部分：系统设计 (8分钟)

### 3.1 系统架构设计

#### 总体架构（三层架构）
```
表现层: 小程序端(uni-app) + 管理后台(Vue3 + Element Plus)
         ↓ HTTP/HTTPS
业务层: Spring Boot后端服务 (Controller → Service → Mapper)
         ↓ JDBC
数据层: MySQL 8.0 + Redis(可选)
```

**架构特点**:
- 前后端完全分离，独立开发部署
- RESTful API设计，接口标准化
- 无状态服务，支持水平扩展

#### 技术选型

**后端技术栈**:
| 技术 | 版本 | 作用 |
|------|------|------|
| Spring Boot | 3.x | 基础框架 |
| MyBatis Plus | 3.5+ | ORM框架 |
| MySQL | 8.0 | 关系数据库 |
| JWT | - | 认证授权 |
| Redis | 6.0+ | 缓存(可选) |

**前端技术栈**:
| 技术 | 版本 | 作用 |
|------|------|------|
| Vue 3 | 3.x | 前端框架 |
| uni-app | 最新 | 跨平台框架 |
| Element Plus | 最新 | UI组件库 |
| Vite | 4.x | 构建工具 |
| Pinia | 最新 | 状态管理 |


### 3.2 数据库设计

#### 核心实体关系
- 用户 1:N 订单
- 用户 1:N 地址、优惠券
- 订单 1:N 订单明细
- 商品 1:N 订单明细
- 商品 N:1 分类

#### 核心数据表（15张表）
1. **user**: 用户信息、会员等级、积分余额
2. **product**: 商品信息、价格、库存、分类
3. **orders**: 订单主表、金额、状态、支付方式
4. **order_item**: 订单明细、商品详情、定制选项
5. **category**: 商品分类
6. **user_address**: 收货地址
7. **coupon**: 优惠券信息
8. **user_coupon**: 用户优惠券关系
9. **marketing_activity**: 营销活动
10. **points_product**: 积分商品
11. **points_history**: 积分历史
12. **product_collection**: 商品收藏
13. **review**: 商品评价
14. **banner**: 轮播图
15. **admin_user**: 管理员


### 3.3 接口设计

#### RESTful API规范
- 基础路径: `/api`
- 资源命名: 使用名词复数 `/products`, `/orders`
- HTTP方法: GET查询、POST创建、PUT更新、DELETE删除
- 响应格式: `{code, message, data}`

#### 核心接口（50+个）
**认证**: login, register, phone-login
**商品**: page, detail, recommend, hot, categories
**订单**: create, list, detail, cancel, confirm, evaluate
**用户**: info, update, addresses, address-add
**营销**: banners, activities, coupons
**积分**: history, products, exchange, checkin


### 3.4 核心业务流程

#### 用户下单流程
```
浏览商品 → 定制选项 → 加入购物车 → 选择地址 
→ 选择优惠券 → 确认订单 → 支付 → 状态追踪 → 评价
```

#### 订单处理流程
```
接收订单(语音提醒) → 确认接单 → 制作中 
→ 待取餐 → 用户取餐 → 订单完成
```

#### JWT认证流程
```
用户登录 → 验证密码 → 生成JWT Token → 返回Token 
→ 客户端存储 → 请求携带Token → 后端验证 → 执行业务
```

---

## 第四部分：系统实现 (10分钟)

### 4.1 后端实现

#### 项目结构
```
milk-tea-backend/src/main/java/com/milktea/
├── controller/          # 控制器层（API接口）
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── OrderController.java
│   ├── UserController.java
│   ├── CouponController.java
│   ├── MarketingController.java
│   ├── PointsController.java
│   └── admin/           # 管理端控制器
├── service/             # 业务逻辑层
│   ├── impl/
│   ├── ProductService.java
│   ├── OrderService.java
│   └── PointsService.java
├── mapper/              # 数据访问层
│   ├── UserMapper.java
│   ├── ProductMapper.java
│   └── OrderMapper.java
├── entity/              # 实体类
├── dto/                 # 数据传输对象
├── common/              # 通用类
│   ├── Result.java      # 统一响应
│   └── PageResult.java  # 分页结果
├── config/              # 配置类
│   ├── CorsConfig.java
│   └── WebConfig.java
├── filter/              # 过滤器
│   └── JwtAuthenticationFilter.java
├── exception/           # 异常处理
│   └── GlobalExceptionHandler.java
└── utils/               # 工具类
    ├── JwtUtils.java
    └── MD5Utils.java
```

#### 核心代码实现

**1. 统一响应封装**
```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
}
```

**2. JWT认证过滤器**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = JwtUtils.parseToken(token);
            // 设置用户信息到上下文
        }
        filterChain.doFilter(request, response);
    }
}
```

**3. 订单创建核心逻辑**
```java
@Transactional
public Result<Map<String, Object>> createOrder(OrderCreateRequest request) {
    // 1. 生成订单号
    String orderNo = generateOrderNo();
    
    // 2. 计算订单金额
    BigDecimal totalAmount = calculateAmount(request.getItems());
    
    // 3. 创建订单
    Order order = new Order();
    order.setOrderNo(orderNo);
    order.setUserId(request.getUserId());
    order.setTotalAmount(totalAmount);
    orderMapper.insert(order);
    
    // 4. 创建订单明细并减库存
    for (OrderItem item : request.getItems()) {
        item.setOrderId(order.getId());
        orderItemMapper.insert(item);
        productService.decreaseStock(item.getProductId(), item.getQuantity());
    }
    
    return Result.success(orderInfo);
}
```

**4. 全局异常处理**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常，请稍后重试");
    }
}
```


### 4.2 前端实现

#### 小程序端 (uni-app)

**项目结构**
```
milk-tea-uniapp/src/
├── pages/              # 页面
│   ├── index/          # 首页
│   ├── category/       # 分类
│   ├── cart/           # 购物车
│   ├── order/          # 订单
│   ├── user/           # 个人中心
│   ├── product/        # 商品详情
│   ├── address/        # 地址管理
│   ├── coupon/         # 优惠券
│   └── points/         # 积分
├── utils/              # 工具类
│   ├── api.js          # API封装
│   ├── cart.js         # 购物车逻辑
│   └── common.js       # 通用工具
├── static/             # 静态资源
├── App.vue
├── main.js
└── pages.json          # 页面配置
```

**核心功能实现**

**1. API请求封装**
```javascript
const request = (options) => {
  const token = uni.getStorageSync('token')
  if (token) {
    options.header = options.header || {}
    options.header.Authorization = `Bearer ${token}`
  }
  
  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          uni.showToast({ title: res.data.message, icon: 'none' })
          reject(res.data)
        }
      },
      fail: reject
    })
  })
}
```

**2. 购物车管理**
```javascript
export const addToCart = (product) => {
  let cart = uni.getStorageSync('cart') || []
  const index = cart.findIndex(item => 
    item.id === product.id && 
    item.options === product.options
  )
  
  if (index > -1) {
    cart[index].quantity++
  } else {
    cart.push({ ...product, quantity: 1 })
  }
  
  uni.setStorageSync('cart', cart)
  uni.showToast({ title: '已加入购物车' })
}
```

**3. 订单提交**
```javascript
async submitOrder() {
  const orderData = {
    items: this.cartItems,
    addressId: this.selectedAddress.id,
    couponId: this.selectedCoupon?.id,
    remark: this.remark
  }
  
  const res = await request({
    url: '/api/order/create',
    method: 'POST',
    data: orderData
  })
  
  // 跳转支付页面
  uni.navigateTo({
    url: `/pages/payment/payment?orderId=${res.data.orderId}`
  })
}
```


#### 管理后台 (Vue3 + Element Plus)

**项目结构**
```
milk-tea-admin/src/
├── views/              # 页面视图
│   ├── dashboard/      # 仪表盘
│   ├── product/        # 商品管理
│   ├── order/          # 订单管理
│   ├── user/           # 用户管理
│   ├── marketing/      # 营销管理
│   └── system/         # 系统管理
├── components/         # 公共组件
├── api/                # API接口
├── router/             # 路由配置
├── store/              # 状态管理(Pinia)
├── utils/              # 工具类
├── layout/             # 布局组件
└── main.js
```

**核心功能实现**

**1. 路由守卫**
```javascript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})
```

**2. Axios请求拦截**
```javascript
// 请求拦截器
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
axios.interceptors.response.use(
  response => {
    if (response.data.code === 200) {
      return response.data
    } else {
      ElMessage.error(response.data.message)
      return Promise.reject(response.data)
    }
  },
  error => {
    ElMessage.error('网络错误')
    return Promise.reject(error)
  }
)
```

**3. 数据可视化（ECharts）**
```javascript
const chartOption = {
  title: { text: '销售趋势' },
  tooltip: { trigger: 'axis' },
  xAxis: { 
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [{
    name: '销售额',
    type: 'line',
    data: [1200, 1500, 1800, 2100, 2400, 3000, 2800],
    smooth: true
  }]
}
```

**4. 订单实时更新（轮询）**
```javascript
onMounted(() => {
  // 每30秒刷新订单列表
  const timer = setInterval(() => {
    loadOrders()
  }, 30000)
  
  onUnmounted(() => {
    clearInterval(timer)
  })
})
```


### 4.3 数据库实现

#### 数据库初始化
- 15张核心业务表
- 管理员账号: admin/123456
- 20+款测试商品数据
- 商品分类: 奶茶、果茶、奶盖、小吃

#### 数据库优化
**索引设计**:
- 主键索引: 所有表的id字段
- 唯一索引: username, order_no, phone
- 普通索引: user_id, product_id, category_id
- 复合索引: (user_id, status), (create_time, status)

**查询优化**:
- 分页查询使用LIMIT
- 避免SELECT *
- 使用JOIN代替子查询
- 合理使用缓存


### 4.4 系统测试

#### 功能测试
- 用户端: 登录注册、商品浏览、下单支付、订单管理
- 管理端: 商品管理、订单处理、数据统计、营销管理
- 异常测试: 网络异常、数据校验、权限验证

---

## 第五部分：系统演示 (5分钟)

### 5.1 小程序端演示
1. **用户登录**: 微信授权登录
2. **商品浏览**: 首页推荐、分类查看、商品详情
3. **定制下单**: 选择甜度/温度/加料 → 加入购物车
4. **订单提交**: 选择地址 → 选择优惠券 → 提交订单 → 模拟支付
5. **订单追踪**: 查看订单状态、订单详情
6. **会员中心**: 积分查看、优惠券管理、地址管理

### 5.2 管理后台演示
1. **数据概览**: 今日销售数据、销售趋势图表
2. **订单管理**: 接收新订单 → 更新状态 → 订单完成
3. **商品管理**: 添加商品、编辑信息、上下架
4. **营销管理**: 创建优惠券、设置活动

---

## 第六部分：系统特色与创新 (3分钟)

### 6.1 技术创新
1. **三端分离架构**: 独立开发、独立部署、易于扩展
2. **RESTful API设计**: 接口标准化、易于维护
3. **JWT无状态认证**: 支持分布式、安全可靠
4. **响应式设计**: 适配多种屏幕尺寸

### 6.2 业务创新
1. **个性化定制**: 甜度/温度/加料多维度选择
2. **智能营销**: 优惠券自动匹配最优方案
3. **会员体系**: 三级会员、积分兑换、专享价格
4. **数据分析**: 实时销售统计、可视化展示

### 6.3 用户体验创新
1. **极简操作**: 3步完成下单
2. **智能推荐**: 基于历史订单推荐
3. **实时追踪**: 订单状态实时更新
4. **贴心功能**: 商品收藏、一键复购、催单功能

---

## 第七部分：项目总结 (3分钟)

### 7.1 项目成果
**功能完成度**:
- 用户端: 10+核心功能模块
- 管理端: 8+管理功能模块
- 后端: 50+个API接口
- 数据库: 15张核心业务表

**技术实现**:
- 前后端完全分离架构
- RESTful API标准化设计
- JWT无状态认证机制
- 完整的异常处理体系
- 数据库索引优化

### 7.2 技术能力提升
**后端**: Spring Boot、MyBatis Plus、JWT认证、RESTful API
**前端**: Vue3、uni-app、Element Plus、状态管理
**全栈**: 前后端分离、项目开发流程、问题解决能力

### 7.3 不足与改进
**当前不足**:
- 支付功能为模拟实现
- 未使用Redis缓存
- 缺少实时通信(WebSocket)
- 缺少消息推送

**优化方向**:
- 对接微信支付
- 引入Redis提升性能
- 实现WebSocket实时通信
- 完善数据分析功能
- 支持多门店管理

### 7.4 商业价值
**对商家**: 降低成本、提升营业额、数据化运营
**对用户**: 节省时间、个性化服务、优惠实惠
**市场前景**: 新式茶饮市场持续增长，数字化转型需求旺盛

---

## 致谢
感谢指导老师的悉心指导
感谢学校提供的学习资源
感谢开源社区的技术支持

---

## 常见问题准备

**技术问题**:
1. **为什么选择Spring Boot?** 简化配置、生态完善、适合快速开发
2. **如何保证订单一致性?** 数据库事务、乐观锁、状态机设计
3. **JWT如何保证安全?** HTTPS传输、过期时间、签名验证
4. **前后端如何交互?** RESTful API、JSON格式、统一响应
5. **如何实现权限控制?** JWT Token解析、角色权限验证、接口拦截

**业务问题**:
1. **如何提升用户体验?** 简化流程、智能推荐、个性化定制
2. **如何实现精准营销?** 用户分析、会员体系、优惠券匹配
3. **如何支持多门店?** 数据库预留门店字段、独立库存、分门店统计

**项目问题**:
1. **最大困难?** 描述具体问题、解决方法、经验总结
2. **如果重做?** 更详细设计、引入更多技术、加强测试
3. **创新点?** 三端分离、智能推荐、完整会员体系
