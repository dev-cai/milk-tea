<template>
	<view class="container">
		<view class="header">
			<text class="title">申请退款</text>
			<text class="subtitle">我们会尽快处理您的退款申请</text>
		</view>

		<!-- 订单信息 -->
		<view class="order-info" v-if="!loading">
			<view class="info-row">
				<text class="label">订单号</text>
				<text class="value">{{ orderInfo.orderNo || '加载中...' }}</text>
			</view>
			<view class="info-row">
				<text class="label">订单金额</text>
				<text class="value price">¥{{ orderInfo.payAmount || 0 }}</text>
			</view>
			<view class="info-row">
				<text class="label">退款金额</text>
				<text class="value refund-amount">¥{{ orderInfo.payAmount || 0 }}</text>
			</view>
		</view>
		
		<!-- 加载状态 -->
		<view class="order-info loading-state" v-else>
			<text class="loading-text">加载订单信息中...</text>
		</view>

		<!-- 退款原因 -->
		<view class="section">
			<view class="section-title">退款原因</view>
			<view class="reason-list">
				<view 
					class="reason-item" 
					:class="{ active: refundReason === reason }"
					v-for="(reason, index) in refundReasons" 
					:key="index"
					@click="selectReason(reason)"
				>
					<text class="reason-text">{{ reason }}</text>
					<text class="reason-check" v-if="refundReason === reason">✓</text>
				</view>
			</view>
		</view>

		<!-- 详细说明 -->
		<view class="section">
			<view class="section-title">详细说明（选填）</view>
			<textarea 
				class="refund-textarea" 
				placeholder="请详细说明退款原因，有助于我们更快处理"
				v-model="refundDescription"
				maxlength="200"
			/>
			<view class="char-count">{{ refundDescription.length }}/200</view>
		</view>

		<!-- 温馨提示 -->
		<view class="tips-section">
			<view class="tips-title">
				<text class="tips-icon">💡</text>
				<text class="tips-text">温馨提示</text>
			</view>
			<view class="tips-content">
				<text class="tip-item">• 退款将原路返回至您的支付账户</text>
				<text class="tip-item">• 退款预计1-3个工作日到账</text>
				<text class="tip-item">• 如有疑问请联系客服</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" @click="submitRefund">提交退款申请</button>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			orderId: null,
			orderInfo: {
				orderNo: '',
				payAmount: 0
			},
			refundReason: '',
			refundReasons: [
				'不想要了',
				'商品质量问题',
				'配送时间太长',
				'下错单了',
				'商家原因',
				'其他原因'
			],
			refundDescription: '',
			loading: true
		}
	},
	onLoad(options) {
		console.log('退款页面参数:', options)
		if (options.orderId) {
			this.orderId = options.orderId
			this.loadOrderInfo()
		} else {
			uni.showToast({
				title: '订单ID不存在',
				icon: 'none'
			})
		}
	},
	methods: {
		// 加载订单信息
		async loadOrderInfo() {
			this.loading = true
			uni.showLoading({ title: '加载中...' })
			
			try {
				console.log('开始加载订单信息，订单ID:', this.orderId)
				const res = await api.order.getDetail(this.orderId)
				console.log('订单信息响应:', res)
				
				if (res && res.data) {
					// 后端返回的数据结构是 { items: [...], order: {...} }
					// 我们需要使用 order 对象
					if (res.data.order) {
						this.orderInfo = res.data.order
					} else {
						// 如果直接是订单对象
						this.orderInfo = res.data
					}
					console.log('订单信息已设置:', this.orderInfo)
				} else {
					uni.showToast({
						title: '订单信息加载失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('加载订单信息失败:', error)
				uni.showToast({
					title: '加载失败，请重试',
					icon: 'none'
				})
			} finally {
				this.loading = false
				uni.hideLoading()
			}
		},

		// 选择退款原因
		selectReason(reason) {
			this.refundReason = reason
		},

		// 验证表单
		validateForm() {
			if (!this.refundReason) {
				uni.showToast({
					title: '请选择退款原因',
					icon: 'none'
				})
				return false
			}

			return true
		},

		// 提交退款申请
		async submitRefund() {
			if (!this.validateForm()) return

			try {
				await uni.showModal({
					title: '确认退款',
					content: `确定要申请退款¥${this.orderInfo.payAmount}吗？`,
					confirmText: '确定',
					cancelText: '取消'
				})

				uni.showLoading({ title: '提交中...' })

				const userInfo = uni.getStorageSync('userInfo')
				const refundData = {
					userId: userInfo.id,
					reason: this.refundReason + (this.refundDescription ? ': ' + this.refundDescription : '')
				}

				await api.order.refund(this.orderId, refundData)

				uni.showToast({
					title: '退款申请已提交',
					icon: 'success'
				})

				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (error) {
				if (error !== 'cancel') {
					console.error('提交退款失败:', error)
					uni.showToast({
						title: error.message || '提交失败，请重试',
						icon: 'none'
					})
				}
			} finally {
				uni.hideLoading()
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 120rpx;
}

.header {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 60rpx 40rpx 40rpx;
	color: white;
}

.title {
	font-size: 40rpx;
	font-weight: 600;
	display: block;
	margin-bottom: 12rpx;
}

.subtitle {
	font-size: 24rpx;
	opacity: 0.9;
}

.order-info {
	background: white;
	margin: 20rpx;
	padding: 24rpx;
	border-radius: 16rpx;
}

.loading-state {
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 200rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.info-row:last-child {
	border-bottom: none;
}

.label {
	font-size: 28rpx;
	color: #666;
}

.value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.price {
	color: #ff6b35;
	font-weight: 600;
}

.refund-amount {
	color: #52c41a;
	font-weight: 600;
	font-size: 32rpx;
}

.section {
	background: white;
	margin: 20rpx;
	padding: 24rpx;
	border-radius: 16rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 20rpx;
}

.reason-list {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.reason-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx 20rpx;
	border-radius: 12rpx;
	background: #f8f8f8;
	border: 2rpx solid #f8f8f8;
	transition: all 0.2s ease;
}

.reason-item.active {
	background: #fff5f0;
	border-color: #ff6b35;
}

.reason-text {
	font-size: 28rpx;
	color: #333;
}

.reason-item.active .reason-text {
	color: #ff6b35;
	font-weight: 600;
}

.reason-check {
	font-size: 28rpx;
	color: #ff6b35;
	font-weight: 600;
}

.refund-textarea {
	width: 100%;
	min-height: 160rpx;
	padding: 20rpx;
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	background: #f8f8f8;
	border-radius: 12rpx;
	border: none;
}

.char-count {
	text-align: right;
	font-size: 24rpx;
	color: #999;
	margin-top: 12rpx;
}

.tips-section {
	background: #fff5f0;
	margin: 20rpx;
	padding: 24rpx;
	border-radius: 16rpx;
	border-left: 4rpx solid #ff6b35;
}

.tips-title {
	display: flex;
	align-items: center;
	gap: 8rpx;
	margin-bottom: 16rpx;
}

.tips-icon {
	font-size: 28rpx;
}

.tips-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #ff6b35;
}

.tips-content {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.tip-item {
	font-size: 24rpx;
	color: #666;
	line-height: 1.6;
}

.submit-section {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	background: white;
	border-top: 1rpx solid #f0f0f0;
}

.submit-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 44rpx;
	border: none;
	font-size: 32rpx;
	color: white;
	font-weight: 600;
	line-height: 88rpx;
}

.submit-btn::after {
	border: none;
}
</style>
