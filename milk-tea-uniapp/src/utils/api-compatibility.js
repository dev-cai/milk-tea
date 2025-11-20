// API兼容性处理
// 处理前端和后端接口不匹配的情况

// 模拟接口 - 当后端接口不存在时使用
const mockApi = {
  // 微信登录模拟
  wxLogin: (data) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '微信登录成功',
          data: {
            token: 'mock-wx-token-' + Date.now(),
            user: {
              id: 1,
              username: 'wx_user_' + Math.random().toString(36).substr(2, 9),
              nickname: '微信用户',
              userType: 0
            }
          }
        })
      }, 1000)
    })
  },

  // 发送验证码模拟
  sendCode: (data) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '验证码发送成功',
          data: null
        })
      }, 500)
    })
  },

  // 营销活动模拟
  getBanners: () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '获取成功',
          data: [
            { id: 1, image: '/static/banner1.jpg', url: '', title: '新品上市' },
            { id: 2, image: '/static/banner2.jpg', url: '', title: '限时优惠' },
            { id: 3, image: '/static/banner3.jpg', url: '', title: '会员专享' }
          ]
        })
      }, 300)
    })
  },

  // 营销活动列表模拟
  getActivities: () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '获取成功',
          data: [
            {
              id: 1,
              name: '优惠券中心',
              desc: '领券享优惠',
              icon: '🎫',
              type: 'coupon'
            },
            {
              id: 2,
              name: '会员特权',
              desc: '专享折扣',
              icon: '👑',
              type: 'member'
            },
            {
              id: 3,
              name: '积分商城',
              desc: '积分兑好礼',
              icon: '🎯',
              type: 'points'
            }
          ]
        })
      }, 300)
    })
  },

  // 个性化推荐模拟
  getPersonalized: (userId, limit) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '获取成功',
          data: [
            {
              id: 1,
              name: '珍珠奶茶',
              description: '经典珍珠奶茶，香甜可口',
              image: '/static/product1.jpg',
              price: 18.00,
              memberPrice: 16.00,
              sales: 999,
              reason: '经常购买'
            },
            {
              id: 2,
              name: '芝士奶盖',
              description: '浓郁芝士，层次丰富',
              image: '/static/product2.jpg',
              price: 22.00,
              memberPrice: 20.00,
              sales: 888,
              reason: '相似口味'
            }
          ]
        })
      }, 400)
    })
  },

  // 积分相关模拟
  getPointsHistory: (userId) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '获取成功',
          data: [
            {
              id: 1,
              type: 'earn',
              points: 100,
              reason: '下单奖励',
              createTime: '2024-11-19 10:00:00'
            },
            {
              id: 2,
              type: 'use',
              points: -50,
              reason: '积分兑换',
              createTime: '2024-11-18 15:30:00'
            }
          ]
        })
      }, 300)
    })
  },

  // 会员信息模拟
  getMemberInfo: (userId) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '获取成功',
          data: {
            id: userId,
            memberLevel: 1,
            levelName: '黄金会员',
            points: 1250,
            nextLevelPoints: 2000,
            privileges: [
              '专享折扣',
              '生日特权',
              '积分翻倍',
              '免费配送'
            ]
          }
        })
      }, 300)
    })
  }
}

// 接口适配器
const apiAdapter = {
  // 处理用户信息接口的不同参数传递方式
  adaptUserInfo: (originalApi) => {
    return (userId) => {
      // 如果后端支持JWT自动识别用户，可以不传userId
      if (uni.getStorageSync('token')) {
        return originalApi()
      } else {
        return originalApi(userId)
      }
    }
  },

  // 处理分页参数的不同格式
  adaptPagination: (params) => {
    return {
      page: params.page || 1,
      size: params.size || params.limit || 10,
      ...params
    }
  },

  // 处理响应数据格式
  adaptResponse: (response) => {
    // 确保响应格式统一
    if (response && typeof response === 'object') {
      return {
        code: response.code || 200,
        message: response.message || '操作成功',
        data: response.data || response
      }
    }
    return response
  }
}

// 接口降级策略
const fallbackStrategy = {
  // 当接口不存在时的降级处理
  handleMissingApi: (apiName, params) => {
    console.warn(`API ${apiName} 不存在，使用模拟数据`)
    
    switch (apiName) {
      case 'wxLogin':
        return mockApi.wxLogin(params)
      case 'sendCode':
        return mockApi.sendCode(params)
      case 'getBanners':
        return mockApi.getBanners()
      case 'getActivities':
        return mockApi.getActivities()
      case 'getPersonalized':
        return mockApi.getPersonalized(params.userId, params.limit)
      case 'getPointsHistory':
        return mockApi.getPointsHistory(params.userId)
      case 'getMemberInfo':
        return mockApi.getMemberInfo(params.userId)
      default:
        return Promise.reject({
          code: 404,
          message: `接口 ${apiName} 暂未实现`
        })
    }
  }
}

export { mockApi, apiAdapter, fallbackStrategy }
