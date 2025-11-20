<template>
	<view class="container">
		<!-- 配送方式选择 -->
		<view class="delivery-mode-section">
			<view class="mode-tabs">
				<view 
					class="mode-tab" 
					:class="{ active: deliveryMode === 'pickup' }"
					@click="switchDeliveryMode('pickup')"
				>
					<text class="tab-icon">🛍️</text>
					<text class="tab-text">到店自取</text>
				</view>
				<view 
					class="mode-tab" 
					:class="{ active: deliveryMode === 'delivery' }"
					@click="switchDeliveryMode('delivery')"
				>
					<text class="tab-icon">🚚</text>
					<text class="tab-text">外卖配送</text>
				</view>
			</view>
		</view>

		<!-- 店铺信息 - 自取和配送都显示 -->
		<view class="store-section" @click="selectStore">
			<view class="section-title">
				<text class="title-icon">🏪</text>
				<text class="title-text">店铺信息</text>
			</view>
			<view class="store-header">
				<view class="store-icon">🏪</view>
				<view class="store-info">
					<text class="store-name">{{ selectedStore.name }}</text>
					<text class="store-address">{{ selectedStore.address }}</text>
					<text class="store-distance">距离您 {{ selectedStore.distance }}</text>
				</view>
			</view>
			<!-- 自取模式显示电话和导航 -->
			<view class="store-actions" v-if="deliveryMode === 'pickup'">
				<view class="store-action" @click.stop="callStore">
					<text class="action-icon">📞</text>
					<text class="action-text">电话</text>
				</view>
				<view class="store-action" @click.stop="navigateToStore">
					<text class="action-icon">🧭</text>
					<text class="action-text">导航</text>
				</view>
			</view>
			<text class="change-store-hint">点击切换店铺</text>
		</view>

		<!-- 配送地址 - 仅配送模式显示 -->
		<view class="address-section" v-if="deliveryMode === 'delivery'" @click="selectAddress">
			<view class="section-title">
				<text class="title-icon">📍</text>
				<text class="title-text">配送地址</text>
			</view>
			<view class="address-card" v-if="selectedAddress">
				<view class="address-header">
					<text class="address-name">{{ selectedAddress.name }}</text>
					<text class="address-phone">{{ selectedAddress.phone }}</text>
				</view>
				<text class="address-detail">
					{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}
				</text>
			</view>
			<view class="address-empty" v-else>
				<text class="empty-icon">➕</text>
				<text class="empty-text">请添加配送地址</text>
			</view>
			<text class="arrow-icon">›</text>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="section-header">
				<text class="section-icon">🛍️</text>
				<text class="section-title">商品清单</text>
			</view>
			
			<view class="goods-list">
				<view class="goods-item" v-for="item in orderItems" :key="item.cartId">
					<image 
						:src="item.image" 
						class="goods-image"
						mode="aspectFill"
					></image>
					
					<view class="goods-info">
						<text class="goods-name">{{ item.name }}</text>
						<view class="goods-options">
							<text 
								class="option-tag"
								v-for="option in getItemOptions(item)" 
								:key="option"
							>{{ option }}</text>
						</view>
						<text class="goods-remark" v-if="item.remark">备注: {{ item.remark }}</text>
					</view>
					
					<view class="goods-price">
						<text class="price-text">¥{{ formatPrice(getCurrentPrice(item)) }}</text>
						<text class="quantity-text">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 预计时间 - 自取模式始终显示，配送模式需要选择地址后显示 -->
		<view class="time-section" v-if="deliveryMode === 'pickup' || (deliveryMode === 'delivery' && selectedAddress)">
			<view class="time-item">
				<view class="time-left">
					<text class="time-icon">🕰️</text>
					<text class="time-title">{{ deliveryMode === 'pickup' ? '预计取餐时间' : '预计送达时间' }}</text>
				</view>
				<text class="time-value">{{ deliveryMode === 'pickup' ? pickupTime : estimatedTime }}</text>
			</view>
		</view>

		<!-- 优惠券 -->
		<view class="coupon-section" @click="selectCoupon">
			<view class="coupon-item">
				<view class="coupon-left">
					<text class="coupon-icon">🎟️</text>
					<text class="coupon-title">优惠券</text>
				</view>
				<view class="coupon-right">
					<text class="coupon-value">{{ selectedCoupon ? selectedCoupon.name : '请选择优惠券' }}</text>
					<text class="coupon-discount" v-if="selectedCoupon">-¥{{ formatPrice(couponDiscount) }}</text>
					<text class="arrow-icon">›</text>
				</view>
			</view>
		</view>

		<!-- 订单备注 -->
		<view class="remark-section">
			<view class="remark-item" @click="showRemarkPopup = true">
				<view class="remark-left">
					<text class="remark-icon">✏️</text>
					<text class="remark-title">订单备注</text>
				</view>
				<view class="remark-right">
					<text class="remark-value">{{ orderRemark || '无' }}</text>
					<text class="arrow-icon">›</text>
				</view>
			</view>
		</view>

		<!-- 费用明细 -->
		<view class="cost-section">
			<view class="cost-list">
				<view class="cost-item">
					<text class="cost-title">商品金额</text>
					<text class="cost-value">¥{{ formatPrice(goodsAmount) }}</text>
				</view>

				<view class="cost-item" v-if="couponDiscount > 0">
					<text class="cost-title">优惠券</text>
					<text class="cost-value discount">-¥{{ formatPrice(couponDiscount) }}</text>
				</view>
				<view class="cost-item" v-if="memberDiscount > 0">
					<text class="cost-title">会员优惠</text>
					<text class="cost-value discount">-¥{{ formatPrice(memberDiscount) }}</text>
				</view>
			</view>
		</view>

		<!-- 底部结算 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">实付款:</text>
				<text class="total-amount">¥{{ formatPrice(totalAmount) }}</text>
			</view>
			<button 
				class="submit-btn"
				:disabled="submitting"
				@click="submitOrder"
			>
				{{ submitting ? '提交中...' : '提交订单' }}
			</button>
		</view>

		<!-- 店铺选择弹窗 -->
		<view class="popup-mask" v-if="showStorePopup" @click="showStorePopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">选择店铺</text>
					<text class="close-btn" @click="showStorePopup = false">×</text>
				</view>
				<view class="store-list">
					<view 
						class="store-option" 
						v-for="store in storeList" 
						:key="store.id"
						:class="{ selected: selectedStore.id === store.id }"
						@click="onStoreSelect(store)"
					>
						<view class="store-option-info">
							<text class="store-option-name">{{ store.name }}</text>
							<text class="store-option-address">{{ store.address }}</text>
							<text class="store-option-distance">距离您 {{ store.distance }}</text>
						</view>
						<text class="check-icon" v-if="selectedStore.id === store.id">✓</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单备注弹窗 -->
		<view class="popup-mask" v-if="showRemarkPopup" @click="showRemarkPopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">订单备注</text>
					<text class="close-btn" @click="showRemarkPopup = false">×</text>
				</view>
				<textarea 
					v-model="orderRemark" 
					class="remark-textarea"
					placeholder="请输入订单备注（选填）"
					maxlength="200"
				></textarea>
				<button 
					class="confirm-btn"
					@click="confirmRemark"
				>
					确定
				</button>
			</view>
		</view>

		<!-- 支付方式选择弹窗 -->
		<view class="popup-mask" v-if="showPaymentSheet" @click="showPaymentSheet = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">选择支付方式</text>
					<text class="close-btn" @click="showPaymentSheet = false">×</text>
				</view>
				<view class="payment-list">
					<view class="payment-item" 
						v-for="(method, index) in paymentMethods" 
						:key="index"
						@click="onPaymentSelect(index)"
					>
						<text class="payment-icon">{{ method.icon }}</text>
						<text class="payment-name">{{ method.name }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice, getSweetnessText, getTemperatureText } from '@/utils/common.js'
import { getSelectedItems, clearCart } from '@/utils/cart.js'

export default {
	data() {
		return {
			deliveryMode: 'pickup', // 配送方式：pickup-自取, delivery-配送
			orderItems: [],
			selectedCoupon: null,
			selectedAddress: null,
			orderRemark: '',
			submitting: false,
			showStorePopup: false,
			showRemarkPopup: false,
			showPaymentSheet: false,
			userLocation: null, // 用户位置
			selectedStore: {
				id: 1,
				name: '奶茶小铺（科技园店）',
				address: '深圳市南山区科技园南区',
				phone: '0755-12345678',
				latitude: 22.5431,
				longitude: 114.0579,
				distance: '1.2km'
			},
			storeList: [
				{
					id: 1,
					name: '奶茶小铺（科技园店）',
					address: '深圳市南山区科技园南区',
					phone: '0755-12345678',
					latitude: 22.5431,
					longitude: 114.0579,
					distance: '1.2km'
				},
				{
					id: 2,
					name: '奶茶小铺（华强北店）',
					address: '深圳市福田区华强北路',
					phone: '0755-23456789',
					latitude: 22.5461,
					longitude: 114.0889,
					distance: '3.5km'
				},
				{
					id: 3,
					name: '奶茶小铺（海岸城店）',
					address: '深圳市南山区文心五路海岸城',
					phone: '0755-34567890',
					latitude: 22.5201,
					longitude: 113.9301,
					distance: '5.8km'
				}
			],
			paymentMethods: [
				{ name: '微信支付', value: 'wechat' },
				{ name: '余额支付', value: 'balance' },
				{ name: '支付宝支付', value: 'alipay' }
			],
			userInfo: {},
			availableCoupons: []
		}
	},
	computed: {
		// 是否为会员
		isMember() {
			return this.userInfo.memberLevel > 0
		},

		// 商品金额
		goodsAmount() {
			return this.orderItems.reduce((total, item) => {
				return total + (this.getCurrentPrice(item) * item.quantity)
			}, 0)
		},

		// 配送费（奶茶店不需要配送费）
		deliveryFee() {
			return 0
		},

		// 优惠券折扣
		couponDiscount() {
			if (!this.selectedCoupon) return 0
			
			if (this.selectedCoupon.type === 1) {
				// 满减券
				return this.goodsAmount >= this.selectedCoupon.minAmount ? this.selectedCoupon.discount : 0
			} else if (this.selectedCoupon.type === 2) {
				// 折扣券
				return this.goodsAmount * (1 - this.selectedCoupon.discount / 10)
			}
			
			return 0
		},

		// 会员优惠
		memberDiscount() {
			if (!this.isMember) return 0
			
			return this.orderItems.reduce((total, item) => {
				if (item.memberPrice && item.memberPrice < item.price) {
					return total + ((item.price - item.memberPrice) * item.quantity)
				}
				return total
			}, 0)
		},

		// 总金额
		totalAmount() {
			return Math.max(0, this.goodsAmount + this.deliveryFee - this.couponDiscount - this.memberDiscount)
		},

		// 预计送达时间
		estimatedTime() {
			const now = new Date()
			now.setMinutes(now.getMinutes() + 35) // 35分钟后
			return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
		},

		// 取餐时间
		pickupTime() {
			const now = new Date()
			now.setMinutes(now.getMinutes() + 18) // 18分钟后
			return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadOrderItems()
		this.getUserLocation()
		this.loadDefaultAddress()
		this.loadAvailableCoupons()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			this.userInfo = userInfo || {}
		},

		// 加载订单商品
		loadOrderItems() {
			this.orderItems = getSelectedItems()
			
			if (this.orderItems.length === 0) {
				uni.showModal({
					title: '提示',
					content: '购物车为空，请先添加商品',
					success: (res) => {
						if (res.confirm) {
							uni.switchTab({
								url: '/pages/index/index'
							})
						}
					}
				})
			}
		},

		// 加载默认地址
		async loadDefaultAddress() {
			try {
				if (!this.userInfo.id) return
				
				const res = await api.user.getAddresses(this.userInfo.id)
				if (res.code === 200) {
					const addresses = res.data || []
					this.selectedAddress = addresses.find(addr => addr.isDefault) || addresses[0]
				}
			} catch (error) {
				console.error('加载地址失败:', error)
			}
		},

		// 加载可用优惠券
		async loadAvailableCoupons() {
			try {
				if (!this.userInfo.id) return
				
				const res = await api.coupon.getMy(this.userInfo.id, 0) // 0: 未使用
				if (res.code === 200) {
					this.availableCoupons = res.data || []
				}
			} catch (error) {
				console.error('加载优惠券失败:', error)
			}
		},

		// 获取商品当前价格
		getCurrentPrice(item) {
			let basePrice = this.isMember && item.memberPrice ? item.memberPrice : item.price
			
			// 加上加料价格
			if (item.toppings && item.toppings.length > 0) {
				const toppingsPrice = item.toppings.length * 3 // 假设每个加料3元
				basePrice += toppingsPrice
			}
			
			return basePrice
		},

		// 获取商品选项文本
		getItemOptions(item) {
			const options = []
			
			if (item.sweetness !== undefined) {
				options.push(getSweetnessText(item.sweetness))
			}
			
			if (item.temperature !== undefined) {
				options.push(getTemperatureText(item.temperature))
			}
			
			if (item.toppings && item.toppings.length > 0) {
				options.push(`加料: ${item.toppings.join('、')}`)
			}
			
			return options
		},

		// 选择收货地址
		selectAddress() {
			if (!this.userInfo.id) {
				uni.showModal({
					title: '提示',
					content: '请先登录',
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: '/pages/login/login'
							})
						}
					}
				})
				return
			}
			
			uni.navigateTo({
				url: '/pages/address/address?select=true'
			})
		},

		// 选择优惠券
		selectCoupon() {
			if (!this.userInfo.id) {
				uni.showModal({
					title: '提示',
					content: '请先登录',
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: '/pages/login/login'
							})
						}
					}
				})
				return
			}
			
			uni.navigateTo({
				url: '/pages/coupon/coupon?select=true'
			})
		},

		// 切换配送方式
		switchDeliveryMode(mode) {
			this.deliveryMode = mode
			
			// 切换到配送模式时，检查是否有地址
			if (mode === 'delivery' && !this.selectedAddress) {
				uni.showToast({
					title: '请先添加配送地址',
					icon: 'none'
				})
			}
		},

		// 选择店铺
		selectStore() {
			this.showStorePopup = true
		},

		// 店铺选择
		onStoreSelect(store) {
			this.selectedStore = store
			this.showStorePopup = false
		},

		// 切换配送方式
		switchDeliveryMode(mode) {
			this.deliveryMode = mode
			
			// 切换到配送模式时，检查是否有地址
			if (mode === 'delivery' && !this.selectedAddress) {
				uni.showToast({
					title: '请先添加配送地址',
					icon: 'none'
				})
			}
		},

		// 获取用户位置
		getUserLocation() {
			uni.getLocation({
				type: 'gcj02',
				success: (res) => {
					this.userLocation = {
						latitude: res.latitude,
						longitude: res.longitude
					}
					
					// 根据位置计算最近的店铺
					this.calculateNearestStore()
				},
				fail: (err) => {
					console.error('获取位置失败:', err)
					uni.showToast({
						title: '获取位置失败，使用默认店铺',
						icon: 'none'
					})
				}
			})
		},

		// 计算最近的店铺
		calculateNearestStore() {
			if (!this.userLocation) return
			
			// 简单的距离计算（实际应该用更精确的算法）
			const distances = this.storeList.map(store => {
				const distance = this.getDistance(
					this.userLocation.latitude,
					this.userLocation.longitude,
					store.latitude,
					store.longitude
				)
				return {
					...store,
					calculatedDistance: distance,
					distance: distance < 1 ? `${(distance * 1000).toFixed(0)}m` : `${distance.toFixed(1)}km`
				}
			})
			
			// 按距离排序
			distances.sort((a, b) => a.calculatedDistance - b.calculatedDistance)
			
			// 更新店铺列表和选中店铺
			this.storeList = distances
			this.selectedStore = distances[0]
		},

		// 计算两点间距离（单位：km）
		getDistance(lat1, lng1, lat2, lng2) {
			const radLat1 = lat1 * Math.PI / 180.0
			const radLat2 = lat2 * Math.PI / 180.0
			const a = radLat1 - radLat2
			const b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0
			let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)))
			s = s * 6378.137
			s = Math.round(s * 10000) / 10000
			return s
		},

		// 选择店铺
		selectStore() {
			this.showStorePopup = true
		},

		// 店铺选择
		onStoreSelect(store) {
			this.selectedStore = store
			this.showStorePopup = false
		},

		// 确认备注
		confirmRemark() {
			this.showRemarkPopup = false
		},

		// 提交订单
		async submitOrder() {
			// 验证必填信息
			if (!this.userInfo.id) {
				uni.showModal({
					title: '提示',
					content: '请先登录',
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: '/pages/login/login'
							})
						}
					}
				})
				return
			}

			// 配送模式需要验证地址
			if (this.deliveryMode === 'delivery' && !this.selectedAddress) {
				uni.showToast({
					title: '请选择配送地址',
					icon: 'none'
				})
				return
			}

			// 显示支付方式选择
			this.showPaymentSheet = true
		},

		// 选择支付方式
		async onPaymentSelect(index) {
			const paymentMethod = this.paymentMethods[index].value
			
			try {
				this.submitting = true
				
				// 构建订单数据（匹配后端OrderCreateRequest）
				const orderData = {
					userId: this.userInfo.id,
					storeId: this.selectedStore.id,
					deliveryMode: this.deliveryMode, // pickup 或 delivery
					addressId: this.deliveryMode === 'delivery' ? this.selectedAddress?.id : null,
					items: this.orderItems.map(item => ({
						productId: item.id,
						quantity: item.quantity,
						sweetness: item.sweetness || 4,
						temperature: item.temperature || 2,
						toppings: Array.isArray(item.toppings) ? item.toppings.join(',') : ''
					})),
					remark: this.orderRemark || '',
					payType: paymentMethod === 'wechat' ? 1 : (paymentMethod === 'alipay' ? 2 : 3)
				}

				console.log('提交订单数据:', orderData)
				console.log('订单商品详情:', this.orderItems)
				console.log('前端计算总金额:', this.totalAmount)

				// 创建订单
				const res = await api.order.create(orderData)
				console.log('订单创建响应:', res)
				
				if (res.code === 200) {
					const order = res.data
					console.log('订单创建成功，订单数据:', order)
					console.log('准备调用支付，支付方式:', paymentMethod)
					
					// 清空购物车
					clearCart()
					
					// 发起支付
					await this.processPayment(order, paymentMethod)
				} else {
					console.error('订单创建失败:', res)
					uni.showToast({
						title: res.message || '订单创建失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('提交订单失败:', error)
				uni.showToast({
					title: error.message || '提交失败',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		},

		// 处理支付
		async processPayment(order, paymentMethod) {
			try {
				console.log('开始处理支付，订单:', order, '支付方式:', paymentMethod)
				
				// 检查余额是否足够（余额支付时）
				if (paymentMethod === 'balance') {
					const currentBalance = this.userInfo.balance || 0
					if (currentBalance < order.totalAmount) {
						uni.showModal({
							title: '余额不足',
							content: `当前余额：¥${currentBalance.toFixed(2)}\n订单金额：¥${order.totalAmount}\n\n余额不足，是否前往充值？`,
							success: (res) => {
								if (res.confirm) {
									uni.navigateTo({
										url: '/pages/recharge/recharge'
									})
								}
							}
						})
						return
					}
				}
				
				// 调用后端支付接口
				let payRes
				console.log('支付方式:', paymentMethod)
				console.log('订单信息:', order)
				
				if (paymentMethod === 'wechat') {
					// 微信支付（测试环境）
					console.log('调用微信支付接口')
					payRes = await api.payment.wxPay({
						orderId: order.orderId,
						orderNo: order.orderNo,
						amount: order.totalAmount,
						userId: this.userInfo.id
					})
				} else if (paymentMethod === 'balance') {
					// 余额支付
					console.log('调用余额支付接口')
					payRes = await api.payment.balancePay({
						orderId: order.orderId,
						orderNo: order.orderNo,
						amount: order.totalAmount,
						userId: this.userInfo.id
					})
				} else if (paymentMethod === 'alipay') {
					// 支付宝支付（测试环境）
					console.log('调用支付宝支付接口')
					payRes = await api.payment.alipay({
						orderId: order.orderId,
						orderNo: order.orderNo,
						amount: order.totalAmount,
						userId: this.userInfo.id
					})
				} else {
					console.error('未知的支付方式:', paymentMethod)
					uni.showToast({
						title: '不支持的支付方式',
						icon: 'none'
					})
					return
				}
				
				console.log('支付接口返回:', payRes)
				
				if (payRes && payRes.code === 200) {
					// 支付成功
					this.paymentSuccess(order, paymentMethod)
				} else {
					uni.showToast({
						title: payRes?.message || '支付失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('支付失败:', error)
				uni.showToast({
					title: error.message || '支付失败',
					icon: 'none'
				})
			}
		},
		
		// 获取支付方式名称
		getPaymentMethodName(method) {
			const names = {
				'wechat': '微信支付',
				'alipay': '支付宝',
				'balance': '余额支付'
			}
			return names[method] || '未知'
		},
		
		// 拨打店铺电话
		callStore() {
			uni.makePhoneCall({
				phoneNumber: this.selectedStore.phone
			})
		},
		
		// 导航到店铺
		navigateToStore() {
			uni.openLocation({
				latitude: this.selectedStore.latitude,
				longitude: this.selectedStore.longitude,
				name: this.selectedStore.name,
				address: this.selectedStore.address
			})
		},

		// 支付成功
		async paymentSuccess(order, paymentMethod) {
			console.log('支付成功，订单:', order, '支付方式:', paymentMethod)
			
			// 重新获取用户信息（更新余额）
			try {
				const userRes = await api.user.getInfo(this.userInfo.id)
				if (userRes.code === 200) {
					this.userInfo = userRes.data
					uni.setStorageSync('userInfo', userRes.data)
				}
			} catch (error) {
				console.error('获取用户信息失败:', error)
			}
			
			uni.showToast({
				title: '支付成功',
				icon: 'success',
				duration: 2000
			})
			
			setTimeout(() => {
				// 跳转到订单列表
				uni.redirectTo({
					url: '/pages/order-list/order-list'
				})
			}, 2000)
		},

		// 工具方法
		formatPrice,
		getSweetnessText,
		getTemperatureText
	}
}
</script>

<style lang="scss" scoped>
.container {
	background: #f8f9fa;
	min-height: 100vh;
	padding-bottom: 120rpx;
}

/* 地址选择 */
.address-section {
	background: white;
	margin-bottom: 20rpx;
	padding: 30rpx;
}

.address-content {
	display: flex;
	align-items: center;
}

.address-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.address-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-left: 12rpx;
}

.address-info {
	flex: 1;
	margin-right: 20rpx;
}

.address-line1 {
	display: flex;
	align-items: center;
	margin-bottom: 8rpx;
}

.address-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-right: 20rpx;
}

.address-phone {
	font-size: 24rpx;
	color: #666;
}

.address-detail {
	font-size: 24rpx;
	color: #666;
	line-height: 1.4;
}

.address-icon, .plus-icon {
	font-size: 32rpx;
	color: #ff6b35;
	margin-right: 16rpx;
}

.arrow-icon {
	font-size: 24rpx;
	color: #999;
}

/* 店铺信息 - 现代化设计 */
.store-section {
	background: white;
	margin-bottom: 20rpx;
	padding: 30rpx;
	border-radius: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.section-title {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.title-icon {
	font-size: 32rpx;
	margin-right: 12rpx;
}

.title-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.store-header {
	display: flex;
	align-items: center;
	margin-bottom: 24rpx;
	padding: 20rpx;
	background: linear-gradient(135deg, #fff5f0 0%, #ffe8dc 100%);
	border-radius: 16rpx;
}

.store-icon {
	width: 80rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 40rpx;
	margin-right: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.store-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 6rpx;
}

.store-name {
	font-size: 30rpx;
	font-weight: 700;
	color: #333;
}

.store-address {
	font-size: 24rpx;
	color: #666;
}

.store-distance {
	font-size: 22rpx;
	color: #ff6b35;
	font-weight: 600;
}

.store-info-card {
	padding: 24rpx;
	background: linear-gradient(135deg, #f8f9fa 0%, #f0f1f3 100%);
	border-radius: 12rpx;
	border: 1rpx solid #e8e9eb;
}

.store-info-card .store-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.store-info-card .store-address {
	font-size: 24rpx;
	color: #666;
	display: block;
}

.store-actions {
	display: flex;
	gap: 12rpx;
}

.store-action {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 16rpx;
	background: white;
	border-radius: 12rpx;
	border: 1rpx solid #f0f0f0;
	transition: all 0.3s;
}

.store-action:active {
	background: #f8f9fa;
	transform: scale(0.95);
}

.action-icon {
	font-size: 32rpx;
	margin-bottom: 6rpx;
}

.action-text {
	font-size: 22rpx;
	color: #666;
	font-weight: 500;
}

.change-store-hint {
	display: block;
	text-align: center;
	font-size: 24rpx;
	color: #999;
	margin-top: 16rpx;
	padding-top: 16rpx;
	border-top: 1rpx dashed #e8e9eb;
}

/* 配送地址 */
.address-section {
	background: white;
	margin-bottom: 20rpx;
	padding: 30rpx;
	border-radius: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	position: relative;
}

.address-card {
	padding: 24rpx;
	background: linear-gradient(135deg, #f8f9fa 0%, #f0f1f3 100%);
	border-radius: 12rpx;
	border: 1rpx solid #e8e9eb;
}

.address-empty {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	border: 2rpx dashed #ddd;
}

.empty-icon {
	font-size: 32rpx;
	color: #ff6b35;
	margin-right: 16rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.address-section .arrow-icon {
	position: absolute;
	right: 30rpx;
	top: 50%;
	transform: translateY(-50%);
	font-size: 32rpx;
	color: #999;
}

.section-icon {
	font-size: 32rpx;
	margin-right: 12rpx;
}

.goods-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.option-tag {
	font-size: 20rpx;
	color: #666;
	background: #f0f0f0;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
	margin-right: 12rpx;
}

/* 配送方式样式 */
.delivery-section,
.coupon-section,
.remark-section,
.cost-section {
	background: white;
	margin-bottom: 20rpx;
	padding: 30rpx;
}

.delivery-list,
.cost-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.delivery-item,
.coupon-item,
.remark-item,
.cost-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.delivery-item:last-child,
.cost-item:last-child {
	border-bottom: none;
}

.delivery-left,
.coupon-left,
.remark-left {
	display: flex;
	align-items: center;
}

.delivery-icon,
.coupon-icon,
.remark-icon {
	font-size: 32rpx;
	margin-right: 16rpx;
}

.delivery-title,
.coupon-title,
.remark-title,
.cost-title {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.delivery-right,
.coupon-right,
.remark-right {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.delivery-value,
.coupon-value,
.remark-value,
.cost-value {
	font-size: 26rpx;
	color: #666;
}

.cost-value.discount {
	color: #ff6b35;
}

.coupon-discount {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

/* 提交按钮样式 */
.submit-btn {
	flex: 1;
	height: 88rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	font-size: 32rpx;
	font-weight: 600;
	border-radius: 44rpx;
	border: none;
	transition: all 0.3s ease;
}

.submit-btn:disabled {
	opacity: 0.6;
}

/* 弹窗样式 */
.popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 9999;
	display: flex;
	align-items: flex-end;
	justify-content: center;
}

.popup-content {
	width: 100%;
	max-height: 80vh;
	background: white;
	border-radius: 20rpx 20rpx 0 0;
	padding: 40rpx 30rpx;
	overflow-y: auto;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.close-btn {
	font-size: 40rpx;
	color: #999;
	line-height: 1;
}

.delivery-option {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.delivery-option:last-child {
	border-bottom: none;
}

.delivery-info {
	flex: 1;
	margin-left: 20rpx;
}

.delivery-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	display: block;
	margin-bottom: 8rpx;
}

.delivery-desc {
	font-size: 24rpx;
	color: #666;
	display: block;
	margin-bottom: 8rpx;
}

.delivery-fee {
	font-size: 26rpx;
	color: #ff6b35;
	font-weight: 600;
}

.remark-textarea {
	width: 100%;
	height: 200rpx;
	padding: 20rpx;
	border: 1rpx solid #e0e0e0;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #333;
	background: #f8f9fa;
	margin-bottom: 30rpx;
}

.confirm-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	font-size: 32rpx;
	font-weight: 600;
	border-radius: 44rpx;
	border: none;
}

.payment-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.payment-item {
	display: flex;
	align-items: center;
	padding: 30rpx 20rpx;
	background: #f8f9fa;
	border-radius: 16rpx;
	transition: all 0.3s ease;
}

.payment-item:active {
	background: rgba(255, 107, 53, 0.1);
}

.payment-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.payment-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.address-empty {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
}

.empty-text {
	flex: 1;
	font-size: 28rpx;
	color: #999;
	margin-left: 12rpx;
}

/* 商品列表 */
.goods-section {
	background: white;
	margin-bottom: 20rpx;
	padding: 30rpx;
}

.section-header {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-left: 12rpx;
}

.goods-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.goods-item {
	display: flex;
	align-items: flex-start;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f8f8f8;
}

.goods-item:last-child {
	border-bottom: none;
}

.goods-info {
	flex: 1;
	margin: 0 20rpx;
}

.goods-name {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 12rpx;
	display: block;
}

.goods-options {
	display: flex;
	flex-wrap: wrap;
	gap: 8rpx;
	margin-bottom: 8rpx;
}

.goods-remark {
	font-size: 22rpx;
	color: #999;
	display: block;
}

.goods-price {
	text-align: right;
}

.price-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #ff6b35;
	display: block;
	margin-bottom: 4rpx;
}

.quantity-text {
	font-size: 22rpx;
	color: #999;
}

/* 配送方式、优惠券、备注 */
.delivery-section,
.coupon-section,
.remark-section,
.cost-section {
	background: white;
	margin-bottom: 20rpx;
}

.coupon-discount {
	font-size: 24rpx;
	color: #52c41a;
	font-weight: 600;
}

/* 底部结算 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx 30rpx;
	border-top: 1rpx solid #f0f0f0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.total-info {
	flex: 1;
}

.total-label {
	font-size: 26rpx;
	color: #666;
	margin-right: 8rpx;
}

.total-amount {
	font-size: 36rpx;
	font-weight: 700;
	color: #ff6b35;
}

/* 弹窗样式 */
.popup-content {
	padding: 40rpx 30rpx;
	max-height: 80vh;
	overflow-y: auto;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.delivery-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f8f8f8;
}

.delivery-item:last-child {
	border-bottom: none;
}

.delivery-info {
	flex: 1;
	margin-left: 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.delivery-desc {
	font-size: 24rpx;
	color: #666;
}

.delivery-fee {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

/* 店铺选择弹窗 */
.store-list {
	max-height: 60vh;
	overflow-y: auto;
}

.store-option {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx;
	border-bottom: 1rpx solid #f0f0f0;
	transition: all 0.3s;
}

.store-option:last-child {
	border-bottom: none;
}

.store-option.selected {
	background: linear-gradient(135deg, #fff5f0 0%, #ffe8dc 100%);
}

.store-option:active {
	background: #f8f9fa;
}

.store-option-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 6rpx;
}

.store-option-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.store-option-address {
	font-size: 24rpx;
	color: #666;
}

.store-option-distance {
	font-size: 22rpx;
	color: #ff6b35;
	font-weight: 600;
}

.check-icon {
	font-size: 40rpx;
	color: #ff6b35;
	font-weight: 700;
}

/* 配送方式选择 - 现代化设计 */
.delivery-mode-section {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	margin-bottom: 20rpx;
	padding: 40rpx 30rpx 30rpx;
}

.mode-tabs {
	display: flex;
	gap: 16rpx;
	background: rgba(255, 255, 255, 0.2);
	padding: 6rpx;
	border-radius: 50rpx;
}

.mode-tab {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	padding: 20rpx 24rpx;
	background: transparent;
	border-radius: 44rpx;
	transition: all 0.3s ease;
}

.mode-tab.active {
	background: white;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.tab-icon {
	font-size: 36rpx;
}

.tab-text {
	font-size: 28rpx;
	font-weight: 600;
	color: rgba(255, 255, 255, 0.8);
}

.mode-tab.active .tab-text {
	color: #ff6b35;
}
</style>
