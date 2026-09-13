/**
 * 通用工具函数库
 */

// 时间格式化
export const formatTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
	if (!date) return ''
	
	const d = new Date(date)
	if (isNaN(d.getTime())) return ''
	
	const formatMap = {
		YYYY: d.getFullYear(),
		MM: String(d.getMonth() + 1).padStart(2, '0'),
		DD: String(d.getDate()).padStart(2, '0'),
		HH: String(d.getHours()).padStart(2, '0'),
		mm: String(d.getMinutes()).padStart(2, '0'),
		ss: String(d.getSeconds()).padStart(2, '0')
	}
	
	return format.replace(/YYYY|MM|DD|HH|mm|ss/g, match => formatMap[match])
}

// 价格格式化
export const formatPrice = (price) => {
	if (price === null || price === undefined || isNaN(price)) return '0.00'
	return Number(price).toFixed(2)
}

// 价格显示（带￥符号）
export const formatPriceDisplay = (price) => {
	return `¥${formatPrice(price)}`
}

// 订单状态文本
export const getOrderStatusText = (status) => {
	const statusMap = {
		0: '待支付',
		1: '待制作',
		2: '制作中',
		3: '待取餐',
		4: '已完成',
		5: '已取消',
		6: '申请退款',
		7: '已退款'
	}
	return statusMap[status] || '未知状态'
}

// 订单状态颜色
export const getOrderStatusColor = (status) => {
	const colorMap = {
		0: '#ff6b35',  // 待支付 - 橙色
		1: '#1890ff',  // 待制作 - 蓝色
		2: '#52c41a',  // 制作中 - 绿色
		3: '#faad14',  // 待取餐 - 黄色
		4: '#52c41a',  // 已完成 - 绿色
		5: '#999999',  // 已取消 - 灰色
		6: '#ff4d4f',  // 申请退款 - 红色
		7: '#999999'   // 已退款 - 灰色
	}
	return colorMap[status] || '#999999'
}

// 会员等级文本
export const getMemberLevelText = (level) => {
	const levelMap = {
		0: '普通会员',
		1: '黄金会员',
		2: '钻石会员'
	}
	return levelMap[level] || '普通会员'
}

// 会员等级颜色
export const getMemberLevelColor = (level) => {
	const colorMap = {
		0: '#999999',  // 普通会员 - 灰色
		1: '#faad14',  // 黄金会员 - 金色
		2: '#722ed1'   // 钻石会员 - 紫色
	}
	return colorMap[level] || '#999999'
}

// 甜度文本
export const getSweetnessText = (sweetness) => {
	const sweetnessMap = {
		0: '无糖',
		1: '三分糖',
		2: '五分糖',
		3: '七分糖',
		4: '正常糖'
	}
	return sweetnessMap[sweetness] || '正常糖'
}

// 温度文本
export const getTemperatureText = (temperature) => {
	const temperatureMap = {
		0: '去冰',
		1: '少冰',
		2: '正常冰',
		3: '热饮'
	}
	return temperatureMap[temperature] || '正常冰'
}

// 地址标签文本
export const getAddressTagText = (tag) => {
	const tagMap = {
		1: '家',
		2: '公司',
		3: '学校',
		4: '其他'
	}
	return tagMap[tag] || '其他'
}

// 优惠券类型文本
export const getCouponTypeText = (type) => {
	const typeMap = {
		1: '满减券',
		2: '折扣券',
		3: '兑换券'
	}
	return typeMap[type] || '优惠券'
}

// 优惠券状态文本
export const getCouponStatusText = (status) => {
	const statusMap = {
		0: '未使用',
		1: '已使用',
		2: '已过期'
	}
	return statusMap[status] || '未知'
}

// 防抖函数（优化版本）
export const debounce = (func, wait, immediate = false) => {
	let timeout
	return function executedFunction(...args) {
		const callNow = immediate && !timeout
		clearTimeout(timeout)
		timeout = setTimeout(() => {
			timeout = null
			if (!immediate) func.apply(this, args)
		}, wait)
		if (callNow) func.apply(this, args)
	}
}

// 节流函数
export const throttle = (func, limit) => {
	let inThrottle
	return function() {
		const args = arguments
		const context = this
		if (!inThrottle) {
			func.apply(context, args)
			inThrottle = true
			setTimeout(() => inThrottle = false, limit)
		}
	}
}

// 生成唯一ID
export const generateId = () => {
	if (typeof crypto !== 'undefined' && crypto.randomUUID) return crypto.randomUUID()
	return `${Date.now().toString(36)}-${Date.now().toString(36)}`
}

// 深拷贝（优化版本）
export const deepClone = (obj) => {
	if (obj === null || typeof obj !== 'object') return obj
	if (obj instanceof Date) return new Date(obj.getTime())
	if (obj instanceof RegExp) return new RegExp(obj)
	if (obj instanceof Array) return obj.map(item => deepClone(item))
	
	const clonedObj = {}
	for (const key in obj) {
		if (Object.prototype.hasOwnProperty.call(obj, key)) {
			clonedObj[key] = deepClone(obj[key])
		}
	}
	return clonedObj
}

// 验证手机号
export const validatePhone = (phone) => {
	const phoneReg = /^1[3-9]\d{9}$/
	return phoneReg.test(phone)
}

// 验证邮箱
export const validateEmail = (email) => {
	const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
	return emailReg.test(email)
}

// 数字千分位格式化
export const formatNumber = (num) => {
	if (!num && num !== 0) return ''
	return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 获取位置信息
export const getLocation = () => {
	return new Promise((resolve, reject) => {
		uni.getLocation({
			type: 'gcj02',
			success: resolve,
			fail: reject
		})
	})
}

// 选择位置
export const chooseLocation = () => {
	return new Promise((resolve, reject) => {
		uni.chooseLocation({
			success: resolve,
			fail: reject
		})
	})
}

// 复制到剪贴板
export const copyToClipboard = (text) => {
	return new Promise((resolve, reject) => {
		uni.setClipboardData({
			data: text,
			success: () => {
				uni.showToast({
					title: '复制成功',
					icon: 'success'
				})
				resolve()
			},
			fail: reject
		})
	})
}

// 拨打电话
export const makePhoneCall = (phoneNumber) => {
	return new Promise((resolve, reject) => {
		uni.makePhoneCall({
			phoneNumber,
			success: resolve,
			fail: reject
		})
	})
}

// 预览图片
export const previewImage = (urls, current = 0) => {
	uni.previewImage({
		urls,
		current: typeof current === 'number' ? urls[current] : current
	})
}

export default {
	formatTime,
	formatPrice,
	formatPriceDisplay,
	getOrderStatusText,
	getOrderStatusColor,
	getMemberLevelText,
	getMemberLevelColor,
	getSweetnessText,
	getTemperatureText,
	getAddressTagText,
	getCouponTypeText,
	getCouponStatusText,
	debounce,
	throttle,
	generateId,
	deepClone,
	validatePhone,
	validateEmail,
	formatNumber,
	getLocation,
	chooseLocation,
	copyToClipboard,
	makePhoneCall,
	previewImage
}
