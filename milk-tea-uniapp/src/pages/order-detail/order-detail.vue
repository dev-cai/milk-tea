<template>
	<view class="container">
		<!-- 订单状态 -->
		<view class="status-section">
			<view class="status-icon" :class="getStatusClass(orderInfo.status)">
				<text>{{ getStatusIcon(orderInfo.status) }}</text>
			</view>
			<view class="status-info">
				<text class="status-text">{{ getStatusText(orderInfo.status) }}</text>
				<text class="status-desc">{{ getStatusDesc(orderInfo.status) }}</text>
			</view>
		</view>

		<!-- 制作进度 -->
		<view class="progress-section" v-if="orderInfo.status >= 1 && orderInfo.status <= 3">
			<view class="progress-header">
				<text class="progress-title">制作进度</text>
			</view>
			<view class="progress-steps">
				<view class="step-item" :class="{ active: orderInfo.status >= 1, completed: orderInfo.status > 1 }">
					<view class="step-dot"></view>
					<text class="step-text">订单确认</text>
				</view>
				<view class="step-item" :class="{ active: orderInfo.status >= 2, completed: orderInfo.status > 2 }">
					<view class="step-dot"></view>
					<text class="step-text">开始制作</text>
				</view>
				<view class="step-item" :class="{ active: orderInfo.status >= 3 }">
					<view class="step-dot"></view>
					<text class="step-text">制作完成</text>
				</view>
			</view>
		</view>

		<!-- 商品信息 -->
		<view class="products-section">
			<view class="section-header">
				<text class="section-title">商品信息</text>
			</view>
			<view class="product-list">
				<view class="product-item" v-for="item in orderInfo.items" :key="item.id">
					<image class="product-image" :src="item.productImage" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ item.productName }}</text>
						<text class="product-specs">{{ formatSpecs(item) }}</text>
						<view class="product-price">
							<text class="price">¥{{ formatPrice(item.price) }}</text>
							<text class="quantity">x{{ item.quantity }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="order-info-section">
			<view class="section-header">
				<text class="section-title">订单信息</text>
			</view>
			<view class="info-list">
				<view class="info-item">
					<text class="info-label">订单号</text>
					<text class="info-value">{{ orderInfo.orderNo }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">下单时间</text>
					<text class="info-value">{{ orderInfo.createTime }}</text>
				</view>
				<view class="info-item" v-if="orderInfo.payTime">
					<text class="info-label">支付时间</text>
					<text class="info-value">{{ orderInfo.payTime }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">支付方式</text>
					<text class="info-value">{{ getPaymentMethodText(orderInfo.paymentMethod) }}</text>
				</view>
				<view class="info-item" v-if="orderInfo.remark">
					<text class="info-label">订单备注</text>
					<text class="info-value">{{ orderInfo.remark }}</text>
				</view>
			</view>
		</view>

		<!-- 配送信息 -->
		<view class="delivery-section" v-if="orderInfo.deliveryAddress">
			<view class="section-header">
				<text class="section-title">配送信息</text>
			</view>
			<view class="delivery-info">
				<view class="delivery-address">
					<text class="address-name">{{ orderInfo.deliveryAddress.name }}</text>
					<text class="address-phone">{{ orderInfo.deliveryAddress.phone }}</text>
					<text class="address-detail">{{ orderInfo.deliveryAddress.fullAddress }}</text>
				</view>
			</view>
		</view>

		<!-- 费用明细 -->
		<view class="cost-section">
			<view class="section-header">
				<text class="section-title">费用明细</text>
			</view>
			<view class="cost-list">
				<view class="cost-item">
					<text class="cost-label">商品金额</text>
					<text class="cost-value">¥{{ formatPrice(orderInfo.productAmount) }}</text>
				</view>
				<view class="cost-item" v-if="orderInfo.deliveryFee > 0">
					<text class="cost-label">配送费</text>
					<text class="cost-value">¥{{ formatPrice(orderInfo.deliveryFee) }}</text>
				</view>
				<view class="cost-item" v-if="orderInfo.discountAmount > 0">
					<text class="cost-label">优惠金额</text>
					<text class="cost-value discount">-¥{{ formatPrice(orderInfo.discountAmount) }}</text>
				</view>
				<view class="cost-item total">
					<text class="cost-label">实付金额</text>
					<text class="cost-value">¥{{ formatPrice(orderInfo.totalAmount) }}</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="actions-section">
			<view class="action-btn secondary" v-if="orderInfo.status === 0" @click="cancelOrder">
				<text>取消订单</text>
			</view>
			<view class="action-btn secondary" v-if="orderInfo.status >= 1 && orderInfo.status <= 3" @click="contactService">
				<text>联系客服</text>
			</view>
			<view class="action-btn secondary" v-if="orderInfo.status === 3" @click="urgeOrder">
				<text>催单</text>
			</view>
			<view class="action-btn primary" v-if="orderInfo.status === 0" @click="payOrder">
				<text>立即支付</text>
			</view>
			<view class="action-btn secondary" v-if="orderInfo.status === 4" @click="applyComplaint">
				<text>投诉</text>
			</view>
			<view class="action-btn secondary" v-if="orderInfo.status >= 1 && orderInfo.status <= 4" @click="applyRefund">
				<text>申请退款</text>
			</view>
			<view class="action-btn primary" v-if="orderInfo.status === 4" @click="evaluateOrder">
				<text>评价订单</text>
			</view>
			<view class="action-btn primary" v-if="orderInfo.status === 4" @click="reorder">
				<text>再来一单</text>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice } from '@/utils/common.js'

export default {
	data() {
		return {
			orderId: null,
			orderInfo: {}
		}
	},
	onLoad(options) {
		this.orderId = options.id
		this.loadOrderDetail()
	},
	methods: {
		// 加载订单详情
		async loadOrderDetail() {
			try {
				const res = await api.order.getDetail(this.orderId)
				if (res.code === 200) {
					// 后端返回的数据结构是 { order: {...}, items: [...] }
					const order = res.data.order
					const items = res.data.items || []
					
					// 合并订单信息和订单项
					this.orderInfo = {
						id: order.id,
						orderNo: order.orderNo,
						status: order.status,
						createTime: order.createTime,
						payTime: order.payTime,
						paymentMethod: order.payType,
						productAmount: order.totalAmount,
						deliveryFee: 0, // 当前版本没有配送费
						discountAmount: order.discountAmount || 0,
						totalAmount: order.actualAmount || order.totalAmount,
						remark: order.remark,
						items: items
					}
					
					console.log('订单详情加载成功:', this.orderInfo)
				}
			} catch (error) {
				console.error('加载订单详情失败:', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			}
		},

		// 获取状态样式类
		getStatusClass(status) {
			const classes = {
				0: 'pending',
				1: 'confirmed',
				2: 'preparing',
				3: 'ready',
				4: 'completed',
				5: 'cancelled',
				6: 'refunding',
				7: 'refunded'
			}
			return classes[status] || 'pending'
		},

		// 获取状态图标
		getStatusIcon(status) {
			const icons = {
				0: '💰',
				1: '✅',
				2: '👨‍🍳',
				3: '🥤',
				4: '✨',
				5: '❌',
				6: '🔄',
				7: '💸'
			}
			return icons[status] || '❓'
		},

		// 获取状态文本
		getStatusText(status) {
			const texts = {
				0: '待支付',
				1: '待制作',
				2: '制作中',
				3: '待取餐',
				4: '已完成',
				5: '已取消',
				6: '申请退款',
				7: '已退款'
			}
			return texts[status] || '未知状态'
		},

		// 获取状态描述
		getStatusDesc(status) {
			const descs = {
				0: '请尽快完成支付',
				1: '订单已确认，等待制作',
				2: '正在为您精心制作',
				3: '制作完成，请及时取餐',
				4: '感谢您的光临',
				5: '订单已取消',
				6: '退款申请处理中',
				7: '退款已完成'
			}
			return descs[status] || ''
		},

		// 获取支付方式文本
		getPaymentMethodText(method) {
			const methods = {
				1: '微信支付（开发测试）',
				2: '微信支付（历史订单）',
				3: '微信支付（历史订单）',
				4: '微信支付（历史订单）'
			}
			return methods[method] || '未知'
		},

		// 格式化商品规格
		formatSpecs(item) {
			const specs = []
			
			// 甜度
			if (item.sweetness !== null && item.sweetness !== undefined) {
				const sweetnessMap = ['无糖', '三分糖', '五分糖', '七分糖', '正常糖']
				specs.push(sweetnessMap[item.sweetness] || '')
			}
			
			// 温度
			if (item.temperature !== null && item.temperature !== undefined) {
				const temperatureMap = ['去冰', '少冰', '正常冰', '热饮']
				specs.push(temperatureMap[item.temperature] || '')
			}
			
			// 加料
			if (item.toppings && item.toppings !== '无' && item.toppings !== '') {
				specs.push(`加${item.toppings}`)
			}
			
			return specs.filter(Boolean).join(' | ') || '标准'
		},

		// 取消订单
		cancelOrder() {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个订单吗？',
				success: (res) => {
					if (res.confirm) {
						// 调用取消订单API
						uni.showToast({
							title: '订单已取消',
							icon: 'success'
						})
						this.orderInfo.status = 5
					}
				}
			})
		},

		// 支付订单
		payOrder() {
			uni.navigateTo({
				url: `/pages/payment/payment?orderId=${this.orderId}`
			})
		},

		// 催单
		urgeOrder() {
			uni.showModal({
				title: '催单',
				content: '已为您催单，请耐心等待',
				showCancel: false
			})
		},

		// 联系客服
		contactService() {
			uni.makePhoneCall({
				phoneNumber: '400-123-4567'
			})
		},

		// 申请退款
		applyRefund() {
			uni.navigateTo({
				url: `/pages/order-detail/refund?orderId=${this.orderId}`
			})
		},

		// 申请投诉
		applyComplaint() {
			uni.navigateTo({
				url: `/pages/order-detail/complaint?orderId=${this.orderId}`
			})
		},

		// 评价订单
		evaluateOrder() {
			uni.navigateTo({
				url: `/pages/evaluate/evaluate?orderId=${this.orderId}`
			})
		},

		// 再来一单
		reorder() {
			uni.showToast({
				title: '已添加到购物车',
				icon: 'success'
			})
			setTimeout(() => {
				uni.switchTab({
					url: '/pages/cart/cart'
				})
			}, 1500)
		},

		// 工具方法
		formatPrice
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

/* 订单状态 */
.status-section {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 60rpx 30rpx;
	display: flex;
	align-items: center;
}

.status-icon {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 60rpx;
	margin-right: 30rpx;
	background: rgba(255, 255, 255, 0.2);
	backdrop-filter: blur(10rpx);
}

.status-info {
	flex: 1;
}

.status-text {
	font-size: 36rpx;
	font-weight: 700;
	color: white;
	display: block;
	margin-bottom: 8rpx;
}

.status-desc {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 制作进度 */
.progress-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.progress-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
}

.progress-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.progress-time {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 500;
}

.progress-steps {
	position: relative;
}

.progress-steps::before {
	content: '';
	position: absolute;
	left: 24rpx;
	top: 24rpx;
	bottom: 24rpx;
	width: 4rpx;
	background: #f0f0f0;
	z-index: 1;
}

.step-item {
	display: flex;
	align-items: center;
	margin-bottom: 40rpx;
	position: relative;
	z-index: 2;
}

.step-item:last-child {
	margin-bottom: 0;
}

.step-dot {
	width: 48rpx;
	height: 48rpx;
	border-radius: 50%;
	background: #f0f0f0;
	margin-right: 24rpx;
	position: relative;
	transition: all 0.3s ease;
}

.step-item.active .step-dot {
	background: #ff6b35;
}

.step-item.completed .step-dot {
	background: #52c41a;
}

.step-item.completed .step-dot::after {
	content: '✓';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	color: white;
	font-size: 24rpx;
	font-weight: bold;
}

.step-text {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
	flex: 1;
}

.step-time {
	font-size: 22rpx;
	color: #999;
}

/* 通用区块样式 */
.products-section,
.order-info-section,
.delivery-section,
.cost-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

/* 商品列表 */
.product-list {
	padding: 20rpx 30rpx;
}

.product-item {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.product-item:last-child {
	margin-bottom: 0;
}

.product-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 16rpx;
	margin-right: 20rpx;
}

.product-info {
	flex: 1;
}

.product-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.product-specs {
	font-size: 22rpx;
	color: #999;
	margin-bottom: 12rpx;
	display: block;
}

.product-price {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.price {
	font-size: 28rpx;
	font-weight: 600;
	color: #ff6b35;
}

.quantity {
	font-size: 24rpx;
	color: #666;
}

/* 信息列表 */
.info-list,
.cost-list {
	padding: 20rpx 30rpx;
}

.info-item,
.cost-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f8f9fa;
}

.info-item:last-child,
.cost-item:last-child {
	border-bottom: none;
}

.info-label,
.cost-label {
	font-size: 26rpx;
	color: #666;
}

.info-value,
.cost-value {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
}

.cost-value.discount {
	color: #52c41a;
}

.cost-item.total {
	padding-top: 30rpx;
	border-top: 2rpx solid #f0f0f0;
	margin-top: 20rpx;
}

.cost-item.total .cost-label,
.cost-item.total .cost-value {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
}

/* 配送信息 */
.delivery-info {
	padding: 30rpx;
}

.delivery-address {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.address-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.address-phone {
	font-size: 26rpx;
	color: #666;
}

.address-detail {
	font-size: 24rpx;
	color: #999;
	line-height: 1.5;
}

/* 操作按钮 */
.actions-section {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx 30rpx;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
	display: flex;
	gap: 20rpx;
}

.action-btn {
	flex: 1;
	padding: 24rpx;
	border-radius: 50rpx;
	text-align: center;
	font-size: 28rpx;
	font-weight: 600;
	transition: all 0.3s ease;
}

.action-btn.primary {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.3);
}

.action-btn.secondary {
	background: transparent;
	color: #666;
	border: 2rpx solid #ddd;
}

.action-btn:active {
	transform: scale(0.95);
}
</style>
