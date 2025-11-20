// API配置和封装
const config = {
	baseUrl: 'http://localhost:8080/api',
	timeout: 10000,
	header: {
		'Content-Type': 'application/json'
	}
}

// 请求拦截器
const requestInterceptor = (options) => {
	// 添加token
	const token = uni.getStorageSync('token')
	if (token) {
		options.header.Authorization = `Bearer ${token}`
	}
	
	// 显示加载提示
	if (options.showLoading !== false) {
		uni.showLoading({
			title: '加载中...',
			mask: true
		})
	}
	
	return options
}

// 响应拦截器
const responseInterceptor = (response, options) => {
	// 隐藏加载提示
	if (options.showLoading !== false) {
		uni.hideLoading()
	}
	
	const { data, statusCode } = response
	
	// HTTP状态码处理
	if (statusCode !== 200) {
		uni.showToast({
			title: '网络错误',
			icon: 'none'
		})
		return Promise.reject(response)
	}
	
	// 业务状态码处理
	if (data.code === 401) {
		// token过期，跳转登录
		uni.removeStorageSync('token')
		uni.removeStorageSync('userInfo')
		uni.showToast({
			title: '登录已过期',
			icon: 'none'
		})
		setTimeout(() => {
			uni.navigateTo({
				url: '/pages/login/login'
			})
		}, 1500)
		return Promise.reject(data)
	}
	
	if (data.code !== 200) {
		// 不在这里显示toast，让调用方自己处理
		// uni.showToast({
		// 	title: data.msg || data.message || '请求失败',
		// 	icon: 'none'
		// })
		return Promise.reject(data)
	}
	
	return data
}

// 封装请求方法
const request = (options) => {
	// 处理URL
	if (!options.url.startsWith('http')) {
		options.url = config.baseUrl + options.url
	}
	
	// 合并配置
	options = {
		...config,
		...options,
		header: {
			...config.header,
			...options.header
		}
	}
	
	// 请求拦截
	options = requestInterceptor(options)
	
	return new Promise((resolve, reject) => {
		uni.request({
			...options,
			success: (response) => {
				responseInterceptor(response, options)
					.then(resolve)
					.catch(reject)
			},
			fail: (error) => {
				// 隐藏加载提示
				if (options.showLoading !== false) {
					uni.hideLoading()
				}
				
				uni.showToast({
					title: '网络连接失败',
					icon: 'none'
				})
				reject(error)
			}
		})
	})
}

// API接口定义
const api = {
	// 认证相关
	auth: {
		// 微信登录
		wxLogin: (data) => request({
			url: '/auth/wx-login',
			method: 'POST',
			data
		}),
		
		// 手机号登录
		login: (data) => request({
			url: '/auth/login',
			method: 'POST',
			data
		}),
		
		// 手机号+验证码登录
		phoneLogin: (data) => request({
			url: '/auth/phone-login',
			method: 'POST',
			data
		}),
		
		// 注册
		register: (data) => request({
			url: '/auth/register',
			method: 'POST',
			data
		}),
		
		// 发送验证码
		sendCode: (data) => request({
			url: '/auth/send-code',
			method: 'POST',
			data
		}),
		
		// 验证token
		validateToken: () => request({
			url: '/auth/validate',
			method: 'GET',
			showLoading: false
		}),
		
		// 退出登录
		logout: () => request({
			url: '/auth/logout',
			method: 'POST'
		})
	},
	
	// 用户相关
	user: {
		// 获取用户信息
		getInfo: (userId) => request({
			url: `/user/info`,
			method: 'GET',
			data: { userId }
		}),
		
		// 更新用户信息
		updateInfo: (data) => request({
			url: '/user/info',
			method: 'PUT',
			data
		}),
		
		// 获取用户地址列表
		getAddresses: (userId) => request({
			url: `/user/addresses`,
			method: 'GET',
			data: { userId }
		}),
		
		// 添加地址
		addAddress: (data) => request({
			url: '/user/address',
			method: 'POST',
			data
		}),
		
		// 更新地址
		updateAddress: (id, data) => request({
			url: `/user/address/${id}`,
			method: 'PUT',
			data
		}),
		
		// 删除地址
		deleteAddress: (id) => request({
			url: `/user/address/${id}`,
			method: 'DELETE'
		}),
		
		// 设置默认地址
		setDefaultAddress: (id) => request({
			url: `/user/address/${id}/default`,
			method: 'PUT'
		})
	},
	
	// 商品相关
	product: {
		// 获取商品列表
		getList: (params) => request({
			url: '/product/page',
			method: 'GET',
			data: params
		}),
		
		// 获取商品详情
		getDetail: (id) => request({
			url: `/product/${id}`,
			method: 'GET'
		}),
		
		// 获取推荐商品
		getRecommend: (limit = 10) => request({
			url: `/product/recommend?limit=${limit}`,
			method: 'GET'
		}),
		
		// 获取热门商品
		getHot: (limit = 10) => request({
			url: `/product/hot?limit=${limit}`,
			method: 'GET'
		}),
		
		// 获取个性化推荐
		getPersonalized: (userId, limit = 10) => request({
			url: `/product/personalized?userId=${userId}&limit=${limit}`,
			method: 'GET'
		}),
		
		// 搜索商品
		search: (keyword, params = {}) => request({
			url: `/product/search?keyword=${keyword}`,
			method: 'GET',
			data: params
		}),
		
		// 收藏商品
		collect: (productId) => request({
			url: `/product/${productId}/collect`,
			method: 'POST'
		}),
		
		// 取消收藏
		uncollect: (productId) => request({
			url: `/product/${productId}/uncollect`,
			method: 'DELETE'
		})
	},
	
	// 分类相关
	category: {
		// 获取分类列表
		getList: () => request({
			url: '/admin/category/list',
			method: 'GET'
		}),
		
		// 获取分类商品
		getProducts: (categoryId, params = {}) => request({
			url: `/category/${categoryId}/products`,
			method: 'GET',
			data: params
		})
	},
	
	// 订单相关
	order: {
		// 创建订单
		create: (data) => request({
			url: '/order/create',
			method: 'POST',
			data
		}),
		
		// 获取订单列表
		getList: (params) => request({
			url: '/order/list',
			method: 'GET',
			data: params
		}),
		
		// 获取订单详情
		getDetail: (id) => request({
			url: `/order/${id}`,
			method: 'GET'
		}),
		
		// 取消订单
		cancel: (id, userId) => request({
			url: `/order/${id}/cancel`,
			method: 'PUT',
			data: { userId }
		}),
		
		// 申请退款
		refund: (id, data) => request({
			url: `/order/${id}/refund`,
			method: 'PUT',
			data
		}),
		
		// 确认收货
		confirm: (id) => request({
			url: `/order/${id}/confirm`,
			method: 'PUT'
		}),
		
		// 订单评价
		evaluate: (id, data) => request({
			url: `/order/${id}/evaluate`,
			method: 'POST',
			data
		})
	},
	
	// 优惠券相关
	coupon: {
		// 获取可用优惠券
		getAvailable: () => request({
			url: '/coupon/available',
			method: 'GET'
		}),
		
		// 领取优惠券
		receive: (id, userId) => request({
			url: `/coupon/receive/${id}`,
			method: 'POST',
			data: { userId }
		}),
		
		// 获取我的优惠券
		getMy: (userId, status) => request({
			url: `/coupon/my?userId=${userId}&status=${status || ''}`,
			method: 'GET'
		}),
		
		// 使用优惠券
		use: (id, orderId) => request({
			url: `/coupon/use/${id}`,
			method: 'PUT',
			data: { orderId }
		})
	},
	
	// 支付相关
	payment: {
		// 微信支付
		wxPay: (data) => request({
			url: '/payment/wx-pay',
			method: 'POST',
			data
		}),
		
		// 余额支付
		balancePay: (data) => request({
			url: '/payment/balance-pay',
			method: 'POST',
			data
		}),
		
		// 查询支付状态
		queryStatus: (orderNo) => request({
			url: `/payment/status/${orderNo}`,
			method: 'GET'
		})
	},
	
	// 营销活动
	marketing: {
		// 获取轮播图
		getBanners: () => request({
			url: '/marketing/banners',
			method: 'GET'
		}),
		
		// 获取活动列表
		getActivities: () => request({
			url: '/marketing/activities',
			method: 'GET'
		}),
		
		// 参与活动
		joinActivity: (id, data) => request({
			url: `/marketing/activity/${id}/join`,
			method: 'POST',
			data
		})
	},
	
	// 积分相关
	points: {
		// 获取积分明细
		getHistory: (userId, params = {}) => request({
			url: `/points/history?userId=${userId}`,
			method: 'GET',
			data: params
		}),
		
		// 获取积分商品
		getProducts: (params = {}) => request({
			url: '/points/products',
			method: 'GET',
			data: params
		}),
		
		// 积分兑换
		exchange: (data) => request({
			url: '/points/exchange',
			method: 'POST',
			data
		}),
		
		// 签到
		checkin: (userId) => request({
			url: '/points/checkin',
			method: 'POST',
			data: { userId }
		})
	},
	
	// 会员相关
	member: {
		// 获取会员信息
		getInfo: (userId) => request({
			url: `/member/info?userId=${userId}`,
			method: 'GET'
		}),
		
		// 获取会员特权
		getPrivileges: (level) => request({
			url: `/member/privileges?level=${level}`,
			method: 'GET'
		}),
		
		// 邀请好友
		invite: (data) => request({
			url: '/member/invite',
			method: 'POST',
			data
		})
	}
}

// 导出
export default api
