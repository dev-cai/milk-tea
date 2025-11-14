# 奶茶小程序 API 文档

## 基本信息

- **项目名称**: 奶茶小程序
- **API版本**: v1.0
- **基础URL**: http://localhost:8080/api
- **认证方式**: JWT Token
- **数据格式**: JSON

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 认证接口

### 1.1 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "mock-token-1699876543210",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "userType": 1
    }
  }
}
```

### 1.2 用户注册

**接口地址**: `POST /auth/register`

**请求参数**:
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "新用户"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功"
}
```

---

## 2. 用户接口

### 2.1 获取用户信息

**接口地址**: `GET /user/info`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "avatar": null,
    "phone": null,
    "memberLevel": 0,
    "points": 0,
    "balance": 0.00
  }
}
```

### 2.2 更新用户信息

**接口地址**: `PUT /user/info`

**请求参数**:
```json
{
  "id": 1,
  "nickname": "新昵称",
  "avatar": "头像URL",
  "phone": "13800138000"
}
```

### 2.3 获取用户地址列表

**接口地址**: `GET /user/addresses`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detail": "科技园南区",
      "tag": 1,
      "isDefault": 1
    }
  ]
}
```

### 2.4 添加用户地址

**接口地址**: `POST /user/address`

**请求参数**:
```json
{
  "userId": 1,
  "name": "张三",
  "phone": "13800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detail": "科技园南区",
  "tag": 1,
  "isDefault": 1
}
```

---

## 3. 商品接口

### 3.1 分页查询商品

**接口地址**: `GET /product/page`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| categoryId | Long | 否 | - | 分类ID |
| keyword | String | 否 | - | 搜索关键词 |

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "珍珠奶茶",
        "categoryId": 1,
        "description": "经典珍珠奶茶",
        "image": "product1.jpg",
        "price": 12.00,
        "memberPrice": 10.80,
        "stock": 100,
        "sales": 156,
        "nutrition": "热量：200kcal",
        "isRecommend": 1
      }
    ],
    "total": 10,
    "current": 1,
    "size": 10
  }
}
```

### 3.2 获取商品详情

**接口地址**: `GET /product/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |

### 3.3 获取推荐商品

**接口地址**: `GET /product/recommend`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| limit | Integer | 否 | 10 | 数量限制 |

### 3.4 获取热销商品

**接口地址**: `GET /product/hot`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| limit | Integer | 否 | 10 | 数量限制 |

### 3.5 获取商品分类

**接口地址**: `GET /admin/category/list`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "茶饮",
      "parentId": 0,
      "icon": "tea-icon",
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 4. 订单接口

### 4.1 创建订单

**接口地址**: `POST /order/create`

**请求参数**:
```json
{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "sweetness": 3,
      "temperature": 1,
      "toppings": "珍珠,椰果"
    }
  ],
  "remark": "少冰",
  "payType": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "orderId": 1,
    "orderNo": "MT1699876543210",
    "totalAmount": 24.00
  }
}
```

### 4.2 获取用户订单列表

**接口地址**: `GET /order/list`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| userId | Long | 是 | - | 用户ID |
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| status | Integer | 否 | - | 订单状态 |

**订单状态说明**:
| 状态值 | 说明 |
|--------|------|
| 0 | 待支付 |
| 1 | 待制作 |
| 2 | 制作中 |
| 3 | 待取餐 |
| 4 | 已完成 |
| 5 | 已取消 |
| 6 | 申请退款 |
| 7 | 已退款 |

### 4.3 取消订单

**接口地址**: `PUT /order/{id}/cancel`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

### 4.4 申请退款

**接口地址**: `PUT /order/{id}/refund`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| reason | String | 是 | 退款原因 |

---

## 5. 优惠券接口

### 5.1 获取可用优惠券列表

**接口地址**: `GET /coupon/available`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "新用户专享券",
      "type": 1,
      "discount": 5.00,
      "minAmount": 20.00,
      "totalCount": 1000,
      "receivedCount": 156,
      "validStart": "2024-01-01 00:00:00",
      "validEnd": "2024-12-31 23:59:59"
    }
  ]
}
```

**优惠券类型说明**:
| 类型值 | 说明 |
|--------|------|
| 1 | 满减券 |
| 2 | 折扣券 |
| 3 | 兑换券 |

### 5.2 领取优惠券

**接口地址**: `POST /coupon/receive/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 优惠券ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

### 5.3 获取用户优惠券列表

**接口地址**: `GET /coupon/my`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| status | Integer | 否 | 优惠券状态 |

**优惠券状态说明**:
| 状态值 | 说明 |
|--------|------|
| 0 | 未使用 |
| 1 | 已使用 |
| 2 | 已过期 |

---

## 6. 管理端商品接口

### 6.1 分页查询商品

**接口地址**: `GET /admin/product/page`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| categoryId | Long | 否 | - | 分类ID |
| keyword | String | 否 | - | 搜索关键词 |

### 6.2 添加商品

**接口地址**: `POST /admin/product`

**请求参数**:
```json
{
  "name": "新商品",
  "categoryId": 1,
  "description": "商品描述",
  "image": "product.jpg",
  "price": 15.00,
  "memberPrice": 13.50,
  "cost": 8.00,
  "stock": 100,
  "nutrition": "热量：180kcal",
  "status": 1,
  "isRecommend": 0,
  "sort": 1
}
```

### 6.3 更新商品

**接口地址**: `PUT /admin/product/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |

### 6.4 删除商品

**接口地址**: `DELETE /admin/product/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |

### 6.5 批量删除商品

**接口地址**: `DELETE /admin/product/batch`

**请求参数**:
```json
[1, 2, 3, 4, 5]
```

### 6.6 更新商品状态

**接口地址**: `PUT /admin/product/{id}/status`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 商品状态 (0:下架 1:上架) |

---

## 7. 管理端分类接口

### 7.1 获取分类列表

**接口地址**: `GET /admin/category/list`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "茶饮",
      "parentId": 0,
      "icon": "tea-icon",
      "sort": 1,
      "status": 1,
      "createTime": "2024-11-13 10:00:00",
      "updateTime": "2024-11-13 10:00:00"
    }
  ]
}
```

### 7.2 根据ID获取分类

**接口地址**: `GET /admin/category/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |

### 7.3 添加分类

**接口地址**: `POST /admin/category`

**请求参数**:
```json
{
  "name": "新分类",
  "parentId": 0,
  "icon": "category-icon",
  "sort": 1,
  "status": 1
}
```

### 7.4 更新分类

**接口地址**: `PUT /admin/category/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |

**请求参数**:
```json
{
  "name": "更新分类名",
  "parentId": 0,
  "icon": "new-icon",
  "sort": 2,
  "status": 1
}
```

### 7.5 删除分类

**接口地址**: `DELETE /admin/category/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |

---

## 8. 管理端订单接口

### 8.1 分页查询订单

**接口地址**: `GET /admin/order/page`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| status | Integer | 否 | - | 订单状态 |
| orderNo | String | 否 | - | 订单号 |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

### 7.2 获取订单详情

**接口地址**: `GET /admin/order/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |

### 7.3 更新订单状态

**接口地址**: `PUT /admin/order/{id}/status`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 订单状态 |

### 7.4 处理退款

**接口地址**: `PUT /admin/order/{id}/refund`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| approve | Boolean | 是 | 是否同意退款 |

### 7.5 获取订单统计数据

**接口地址**: `GET /admin/order/statistics`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "todayOrders": 25,
    "todaySales": 680.50,
    "pendingOrders": 8,
    "totalUsers": 1256
  }
}
```

---

## 9. 管理端用户接口

### 9.1 分页查询用户

**接口地址**: `GET /admin/user/page`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |
| memberLevel | Integer | 否 | - | 会员等级 |

### 9.2 获取用户详情

**接口地址**: `GET /admin/user/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |

### 9.3 更新用户状态

**接口地址**: `PUT /admin/user/{id}/status`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 用户状态 (0:禁用 1:启用) |

### 9.4 调整用户会员等级

**接口地址**: `PUT /admin/user/{id}/member-level`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| memberLevel | Integer | 是 | 会员等级 (0:普通 1:黄金 2:钻石) |

### 9.5 调整用户积分

**接口地址**: `PUT /admin/user/{id}/points`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| points | Integer | 是 | 积分数量 |
| type | String | 是 | 操作类型 (add:增加 subtract:减少 set:设置) |

### 9.6 获取用户消费统计

**接口地址**: `GET /admin/user/{id}/statistics`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalOrders": 15,
    "totalAmount": 380.50,
    "avgAmount": 25.37,
    "lastOrderTime": "2024-11-13 09:30:00"
  }
}
```

---

## 10. 管理端优惠券接口

### 10.1 分页查询优惠券

**接口地址**: `GET /admin/coupon/page`

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |

### 10.2 添加优惠券

**接口地址**: `POST /admin/coupon`

**请求参数**:
```json
{
  "name": "双11特惠券",
  "type": 1,
  "discount": 10.00,
  "minAmount": 50.00,
  "totalCount": 500,
  "validStart": "2024-11-01 00:00:00",
  "validEnd": "2024-11-30 23:59:59",
  "status": 1
}
```

### 10.3 更新优惠券

**接口地址**: `PUT /admin/coupon/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 优惠券ID |

### 10.4 删除优惠券

**接口地址**: `DELETE /admin/coupon/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 优惠券ID |

### 10.5 获取优惠券使用记录

**接口地址**: `GET /admin/coupon/{id}/usage`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 优惠券ID |

---

## 11. 测试接口

### 11.1 基础测试

**接口地址**: `GET /test/hello`

**响应示例**:
```json
{
  "code": 200,
  "data": "Hello, MilkTea!",
  "message": "奶茶小程序后端启动成功！"
}
```

### 11.2 健康检查

**接口地址**: `GET /test/health`

**响应示例**:
```json
{
  "status": "UP",
  "timestamp": 1699876543210
}
```

### 11.3 数据库连接测试

**接口地址**: `GET /database/test`

**响应示例**:
```json
{
  "code": 200,
  "message": "数据库连接成功",
  "userCount": 2
}
```

### 11.4 获取用户数据

**接口地址**: `GET /database/users`

### 11.5 获取商品数据

**接口地址**: `GET /database/products`

---

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式和必填项 |
| 401 | 未授权或登录已过期 | 重新登录获取Token |
| 404 | 资源不存在 | 检查请求的资源ID是否正确 |
| 500 | 服务器内部错误 | 联系技术支持 |

## 认证说明

除了公开接口（如登录、注册、测试接口）外，其他接口都需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

## 数据字典

### 会员等级
| 值 | 说明 |
|----|------|
| 0 | 普通会员 |
| 1 | 黄金会员 |
| 2 | 钻石会员 |

### 用户类型
| 值 | 说明 |
|----|------|
| 0 | 普通用户 |
| 1 | 管理员 |

### 支付方式
| 值 | 说明 |
|----|------|
| 1 | 微信支付 |
| 2 | 余额支付 |
| 3 | 组合支付 |

### 甜度等级
| 值 | 说明 |
|----|------|
| 0 | 无糖 |
| 1 | 三分糖 |
| 2 | 五分糖 |
| 3 | 七分糖 |
| 4 | 正常糖 |

### 温度选择
| 值 | 说明 |
|----|------|
| 0 | 去冰 |
| 1 | 少冰 |
| 2 | 正常冰 |
| 3 | 热饮 |

### 地址标签
| 值 | 说明 |
|----|------|
| 1 | 家 |
| 2 | 公司 |
| 3 | 学校 |
| 4 | 其他 |

---

**文档更新时间**: 2024-11-13
**API版本**: v1.0
**联系方式**: milktea@example.com
