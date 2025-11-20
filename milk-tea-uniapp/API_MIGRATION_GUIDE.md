# UniApp 模拟数据迁移指南

## 概述
本文档列出了所有使用模拟数据的页面，以及如何将它们迁移到真实API。

## API配置
- **Base URL**: `http://localhost:8080/api`
- **配置文件**: `src/utils/api.js`
- **Token存储**: `uni.getStorageSync('token')`

## 需要迁移的页面

### 1. 首页 (src/pages/index/index.vue)
**当前状态**: 个性化推荐使用模拟数据

**需要修改的方法**:
```javascript
// 第265行
async loadPersonalizedProducts() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      return
    }
    
    const res = await api.product.getPersonalized(userInfo.id, 10)
    if (res.code === 200) {
      this.personalizedProducts = res.data || []
    }
  } catch (error) {
    console.error('加载个性化推荐失败:', error)
    // 移除模拟数据，显示空状态
    this.personalizedProducts = []
  }
}
```

### 2. 商品详情 (src/pages/product/product.vue)
**当前状态**: 加载失败时使用模拟数据

**需要修改的方法**:
```javascript
// 第308行 - 移除catch中的模拟数据
async loadProductDetail() {
  try {
    const res = await api.product.getDetail(this.productId)
    if (res.code === 200) {
      this.product = res.data
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    uni.showToast({
      title: '商品不存在',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
}
```

### 3. 分类页 (src/pages/category/category.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第249行 - 移除模拟数据逻辑
async loadProducts(reset = false) {
  if (this.loading) return
  
  if (reset) {
    this.page = 1
    this.products = []
    this.hasMore = true
  }
  
  if (!this.hasMore) return
  
  this.loading = true
  
  try {
    const res = await api.category.getProducts(this.currentCategory.id, {
      page: this.page,
      size: this.pageSize
    })
    
    if (res.code === 200) {
      const newProducts = res.data.records || []
      
      if (reset) {
        this.products = newProducts
      } else {
        this.products = [...this.products, ...newProducts]
      }
      
      this.hasMore = newProducts.length === this.pageSize
      this.page++
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    this.loading = false
  }
}
```

### 4. 搜索页 (src/pages/search/search.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第273行 - 移除模拟数据
async performSearch() {
  if (!this.keyword.trim()) {
    return
  }
  
  this.loading = true
  this.searchResults = []
  
  try {
    const res = await api.product.search(this.keyword, {
      page: 1,
      size: 20
    })
    
    if (res.code === 200) {
      this.searchResults = res.data.records || []
      
      // 保存搜索历史
      this.saveSearchHistory(this.keyword)
    }
  } catch (error) {
    console.error('搜索失败:', error)
    uni.showToast({
      title: '搜索失败',
      icon: 'none'
    })
  } finally {
    this.loading = false
  }
}
```

### 5. 订单列表 (src/pages/order-list/order-list.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第182行 - 移除loadMockData方法
async loadOrders(refresh = false) {
  if (this.loading) return
  
  if (refresh) {
    this.page = 1
    this.orderList = []
    this.hasMore = true
  }
  
  if (!this.hasMore) return
  
  this.loading = true
  
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      uni.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    const res = await api.order.getList({
      userId: userInfo.id,
      status: this.currentTab === 0 ? '' : this.currentTab,
      page: this.page,
      size: this.pageSize
    })
    
    if (res.code === 200) {
      const newOrders = res.data.records || []
      
      if (refresh) {
        this.orderList = newOrders
      } else {
        this.orderList = [...this.orderList, ...newOrders]
      }
      
      this.hasMore = newOrders.length === this.pageSize
      this.page++
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    this.loading = false
  }
}
```

### 6. 订单详情 (src/pages/order-detail/order-detail.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第178行 - 移除模拟数据
async loadOrderDetail() {
  try {
    const res = await api.order.getDetail(this.orderId)
    if (res.code === 200) {
      this.orderInfo = res.data
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    uni.showToast({
      title: '订单不存在',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
}
```

### 7. 用户中心 (src/pages/user/user.vue)
**当前状态**: 订单数量使用模拟数据

**需要修改的方法**:
```javascript
// 第200行 - 添加获取订单统计的API
async loadUserInfo() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      return
    }
    
    const res = await api.user.getInfo(userInfo.id)
    if (res.code === 200) {
      this.userInfo = res.data
      uni.setStorageSync('userInfo', res.data)
      
      // 获取订单统计
      await this.loadOrderCounts()
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
},

async loadOrderCounts() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    // 需要后端提供订单统计接口
    const res = await api.order.getStats(userInfo.id)
    if (res.code === 200) {
      this.orderCounts = res.data
    }
  } catch (error) {
    console.error('加载订单统计失败:', error)
    // 设置默认值
    this.orderCounts = {
      unpaid: 0,
      processing: 0,
      shipping: 0,
      completed: 0
    }
  }
}
```

### 8. 个人资料 (src/pages/profile/profile.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第200行 - 移除模拟数据
async loadUserInfo() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      uni.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    const res = await api.user.getInfo(userInfo.id)
    if (res.code === 200) {
      this.userInfo = res.data
      uni.setStorageSync('userInfo', res.data)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}
```

### 9. 会员中心 (src/pages/member/member.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第267行 - 移除模拟数据
async loadUserStats() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      return
    }
    
    const res = await api.member.getInfo(userInfo.id)
    if (res.code === 200) {
      this.userStats = res.data
    }
  } catch (error) {
    console.error('加载用户统计失败:', error)
    this.userStats = {
      totalOrders: 0,
      totalSpent: 0,
      totalPoints: 0,
      totalCoupons: 0
    }
  }
}
```

### 10. 优惠券 (src/pages/coupon/coupon.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第313行 - 移除loadMockData方法
async loadCoupons() {
  this.loading = true
  
  try {
    const userInfo = uni.getStorageSync('userInfo')
    
    if (this.currentTab === 0) {
      // 可领取的优惠券
      const res = await api.coupon.getAvailable()
      if (res.code === 200) {
        this.availableCoupons = res.data || []
      }
    } else {
      // 我的优惠券
      if (!userInfo || !userInfo.id) {
        this.myCoupons = []
        return
      }
      
      const status = this.currentTab === 1 ? 'unused' : 'used'
      const res = await api.coupon.getMy(userInfo.id, status)
      if (res.code === 200) {
        this.myCoupons = res.data || []
      }
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    this.loading = false
  }
}
```

### 11. 地址管理 (src/pages/address/address.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第97行 - 移除模拟数据
async loadAddressList() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      return
    }
    
    const res = await api.user.getAddresses(userInfo.id)
    if (res.code === 200) {
      this.addressList = res.data || []
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
    this.addressList = []
  }
}
```

### 12. 反馈页面 (src/pages/feedback/feedback.vue)
**当前状态**: 使用模拟数据

**需要修改的方法**:
```javascript
// 第192行 - 需要添加反馈历史API
async loadFeedbackHistory() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      return
    }
    
    // 需要后端提供反馈历史接口
    const res = await api.feedback.getHistory(userInfo.id)
    if (res.code === 200) {
      this.feedbackHistory = res.data || []
    }
  } catch (error) {
    console.error('加载反馈历史失败:', error)
    this.feedbackHistory = []
  }
}
```

### 13. 设置页面 (src/pages/settings/settings.vue)
**当前状态**: 缓存大小使用模拟数据

**需要修改的方法**:
```javascript
// 第191行 - 计算真实缓存大小
calculateCacheSize() {
  try {
    const res = uni.getStorageInfoSync()
    const sizeKB = res.currentSize
    const sizeMB = (sizeKB / 1024).toFixed(2)
    this.cacheSize = sizeMB + 'MB'
  } catch (error) {
    console.error('计算缓存大小失败:', error)
    this.cacheSize = '0MB'
  }
}
```

## 需要后端添加的API接口

以下接口在前端已调用，但可能后端还未实现：

1. **订单统计接口**
   - URL: `GET /order/stats?userId={userId}`
   - 返回: `{ unpaid, processing, shipping, completed }`

2. **反馈历史接口**
   - URL: `GET /feedback/history?userId={userId}`
   - 返回: 反馈记录列表

3. **会员统计接口**
   - URL: `GET /member/info?userId={userId}`
   - 返回: `{ totalOrders, totalSpent, totalPoints, totalCoupons }`

## 迁移步骤

1. **备份代码**: 在修改前先备份当前代码
2. **逐页迁移**: 按照上述指南逐个页面修改
3. **测试验证**: 每修改一个页面就测试一次
4. **错误处理**: 确保所有API调用都有适当的错误处理
5. **空状态**: 当数据为空时显示友好的空状态提示

## 注意事项

1. **Token验证**: 确保所有需要登录的接口都携带token
2. **错误提示**: 移除模拟数据后，要有友好的错误提示
3. **加载状态**: 保持loading状态的显示
4. **空数据处理**: 当API返回空数据时，显示空状态而不是模拟数据
5. **向后兼容**: 如果后端接口还未完成，可以暂时保留模拟数据作为降级方案

## 测试清单

- [ ] 首页加载
- [ ] 商品详情
- [ ] 分类浏览
- [ ] 商品搜索
- [ ] 购物车
- [ ] 下单流程
- [ ] 订单列表
- [ ] 订单详情
- [ ] 用户信息
- [ ] 地址管理
- [ ] 优惠券
- [ ] 会员中心
- [ ] 积分系统
- [ ] 反馈功能

## 完成后的验证

1. 关闭后端服务，确认前端有适当的错误提示
2. 清空数据库，确认空状态显示正常
3. 测试所有用户流程，确保功能完整
4. 检查控制台，确保没有模拟数据相关的日志

---

**最后更新**: 2025-11-20
**维护者**: 开发团队
