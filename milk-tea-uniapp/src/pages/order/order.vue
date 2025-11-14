<template>
	<view class="container">
		<!-- 收货地址 -->
		<view class="address-section" @click="selectAddress">
			<view class="address-content" v-if="selectedAddress">
				<view class="address-header">
					<text class="address-icon">📍</text>
					<text class="address-title">收货地址</text>
				</view>
				<view class="address-info">
					<view class="address-line1">
						<text class="address-name">{{ selectedAddress.name }}</text>
						<text class="address-phone">{{ selectedAddress.phone }}</text>
					</view>
					<text class="address-detail">{{ selectedAddress.fullAddress }}</text>
				</view>
				<text class="arrow-icon">></text>
			</view>
			<view class="address-empty" v-else>
				<text class="plus-icon">+</text>
				<text class="empty-text">请选择收货地址</text>
				<text class="arrow-icon">></text>
			</view>
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

		<!-- 配送方式 -->
		<view class="delivery-section">
			<view class="delivery-list">
				<view class="delivery-item" @click="showDeliveryPopup = true">
					<view class="delivery-left">
						<text class="delivery-icon">🚚</text>
						<text class="delivery-title">配送方式</text>
					</view>
					<view class="delivery-right">
						<text class="delivery-value">{{ deliveryMethods[selectedDelivery].name }}</text>
						<text class="arrow-icon">></text>
					</view>
				</view>
				
				<view class="delivery-item" v-if="selectedDelivery === 0">
					<view class="delivery-left">
						<text class="delivery-icon">🕰️</text>
						<text class="delivery-title">预计送达</text>
					</view>
					<text class="delivery-value">{{ estimatedTime }}</text>
				</view>
				
				<view class="delivery-item" v-if="selectedDelivery === 1">
					<view class="delivery-left">
						<text class="delivery-icon">🕰️</text>
						<text class="delivery-title">取餐时间</text>
					</view>
					<text class="delivery-value">{{ pickupTime }}</text>
				</view>
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
					<text class="arrow-icon">></text>
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
					<text class="arrow-icon">></text>
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
				<view class="cost-item">
					<text class="cost-title">配送费</text>
					<text class="cost-value">¥{{ formatPrice(deliveryFee) }}</text>
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

		<!-- 配送方式选择弹窗 -->
		<view class="popup-mask" v-if="showDeliveryPopup" @click="showDeliveryPopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">选择配送方式</text>
					<text class="close-btn" @click="showDeliveryPopup = false">×</text>
				</view>
				<radio-group @change="onDeliveryChange">
					<view class="delivery-option" v-for="(method, index) in deliveryMethods" :key="index">
						<radio :value="index" :checked="selectedDelivery === index" />
						<view class="delivery-info">
							<text class="delivery-name">{{ method.name }}</text>
							<text class="delivery-desc">{{ method.desc }}</text>
							<text class="delivery-fee">{{ method.fee > 0 ? `¥${formatPrice(method.fee)}` : '免费' }}</text>
						</view>
					</view>
				</radio-group>
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
			orderItems: [],
			selectedAddress: null,
			selectedDelivery: 0, // 0: 外卖配送, 1: 到店自取
			selectedCoupon: null,
			orderRemark: '',
			submitting: false,
			showDeliveryPopup: false,
			showRemarkPopup: false,
			showPaymentSheet: false,
			deliveryMethods: [
				{
					name: '外卖配送',
					desc: '30-45分钟送达',
					fee: 3
				},
				{
					name: '到店自取',
					desc: '15-20分钟制作完成',
					fee: 0
				}
			],
			paymentMethods: [
				{ name: '微信支付', value: 'wechat' },
				{ name: '余额支付', value: 'balance' },
				{ name: '组合支付', value: 'mixed' }
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

		// 配送费
		deliveryFee() {
			return this.deliveryMethods[this.selectedDelivery].fee
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

		// 配送方式改变
		onDeliveryChange(value) {
			this.selectedDelivery = value
			this.showDeliveryPopup = false
		},

		// 确认备注
		confirmRemark() {
			this.showRemarkPopup = false
		},

		// 提交订单
		async submitOrder() {
			// 验证必填信息
			if (this.selectedDelivery === 0 && !this.selectedAddress) {
				uni.showToast({
					title: '请选择收货地址',
					icon: 'none'
				})
				return
			}

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

			// 显示支付方式选择
			this.showPaymentSheet = true
		},

		// 选择支付方式
		async onPaymentSelect(index) {
			const paymentMethod = this.paymentMethods[index].value
			
			try {
				this.submitting = true
				
				// 构建订单数据
				const orderData = {
					userId: this.userInfo.id,
					items: this.orderItems.map(item => ({
						productId: item.id,
						quantity: item.quantity,
						price: this.getCurrentPrice(item),
						sweetness: item.sweetness,
						temperature: item.temperature,
						toppings: item.toppings,
						remark: item.remark
					})),
					deliveryType: this.selectedDelivery,
					addressId: this.selectedAddress?.id,
					couponId: this.selectedCoupon?.id,
					remark: this.orderRemark,
					goodsAmount: this.goodsAmount,
					deliveryFee: this.deliveryFee,
					couponDiscount: this.couponDiscount,
					memberDiscount: this.memberDiscount,
					totalAmount: this.totalAmount,
					paymentMethod
				}

				// 创建订单
				const res = await api.order.create(orderData)
				
				if (res.code === 200) {
					const order = res.data
					
					// 清空购物车
					clearCart()
					
					// 发起支付
					await this.processPayment(order, paymentMethod)
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
				if (paymentMethod === 'wechat') {
					// 微信支付
					const payRes = await api.payment.wxPay({
						orderNo: order.orderNo,
						amount: order.totalAmount
					})
					
					if (payRes.code === 200) {
						// 调用微信支付
						await uni.requestPayment({
							provider: 'wxpay',
							...payRes.data
						})
						
						this.paymentSuccess(order)
					}
				} else if (paymentMethod === 'balance') {
					// 余额支付
					const payRes = await api.payment.balancePay({
						orderNo: order.orderNo,
						amount: order.totalAmount
					})
					
					if (payRes.code === 200) {
						this.paymentSuccess(order)
					}
				}
			} catch (error) {
				console.error('支付失败:', error)
				uni.showToast({
					title: '支付失败',
					icon: 'none'
				})
			}
		},

		// 支付成功
		paymentSuccess(order) {
			uni.showToast({
				title: '支付成功',
				icon: 'success'
			})
			
			setTimeout(() => {
				uni.redirectTo({
					url: `/pages/order-detail/order-detail?id=${order.id}`
				})
			}, 1500)
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
</style>
