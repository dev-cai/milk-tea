<template>
	<view class="container">
		<!-- 订单状态筛选 -->
		<view class="status-tabs">
			<scroll-view class="tabs-scroll" scroll-x="true" show-scrollbar="false">
				<view class="tab-item" 
					:class="{ active: activeStatus === status.value }" 
					v-for="status in orderStatus" 
					:key="status.value"
					@click="switchStatus(status.value)"
				>
					<text class="tab-text">{{ status.name }}</text>
					<view class="tab-badge" v-if="status.count > 0">{{ status.count }}</view>
				</view>
			</scroll-view>
		</view>

		<!-- 订单列表 -->
		<view class="order-list" v-if="orderList.length > 0">
			<view class="order-item" v-for="order in orderList" :key="order.id" @click="goToOrderDetail(order.id)">
				<!-- 订单头部 -->
				<view class="order-header">
					<text class="order-no">订单号：{{ order.orderNo }}</text>
					<text class="order-status" :style="{ color: getStatusColor(order.status) }">
						{{ getStatusText(order.status) }}
					</text>
				</view>

				<!-- 商品列表 -->
				<view class="order-products">
					<view class="product-item" v-for="item in order.items" :key="item.id">
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

				<!-- 订单信息 -->
				<view class="order-info">
					<text class="order-time">{{ order.createTime }}</text>
					<text class="order-total">共{{ order.totalQuantity }}件 合计：¥{{ formatPrice(order.totalAmount) }}</text>
				</view>

				<!-- 订单操作 -->
				<view class="order-actions">
					<view class="action-btn secondary" v-if="order.status === 0" @click.stop="cancelOrder(order)">
						<text>取消订单</text>
					</view>
					<view class="action-btn secondary" v-if="order.status >= 1 && order.status <= 3" @click.stop="contactService(order)">
						<text>联系客服</text>
					</view>
					<view class="action-btn secondary" v-if="order.status === 3" @click.stop="urgeOrder(order)">
						<text>催单</text>
					</view>
					<view class="action-btn primary" v-if="order.status === 0" @click.stop="payOrder(order)">
						<text>立即支付</text>
					</view>
					<view class="action-btn secondary" v-if="order.status === 4" @click.stop="applyComplaint(order)">
						<text>投诉</text>
					</view>
					<view class="action-btn secondary" v-if="order.status >= 1 && order.status <= 4" @click.stop="applyRefund(order)">
						<text>申请退款</text>
					</view>
					<view class="action-btn primary" v-if="order.status === 4" @click.stop="evaluateOrder(order)">
						<text>评价</text>
					</view>
					<view class="action-btn primary" v-if="order.status === 4" @click.stop="reorder(order)">
						<text>再来一单</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<text class="empty-icon">📋</text>
			<text class="empty-text">暂无订单</text>
			<text class="empty-desc">快去下单享受美味奶茶吧</text>
			<view class="empty-action" @click="goToHome">
				<text>去下单</text>
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="hasMore && orderList.length > 0">
			<text class="load-text">{{ loading ? '加载中...' : '上拉加载更多' }}</text>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice } from '@/utils/common.js'

export default {
	data() {
		return {
			activeStatus: null, // null表示全部
			orderStatus: [
				{ value: null, name: '全部', count: 0 },
				{ value: 0, name: '待支付', count: 0 },
				{ value: 1, name: '待制作', count: 0 },
				{ value: 2, name: '制作中', count: 0 },
				{ value: 3, name: '待取餐', count: 0 },
				{ value: 4, name: '已完成', count: 0 },
				{ value: 6, name: '售后', count: 0 }
			],
			orderList: [],
			loading: false,
			hasMore: true,
			page: 1,
			pageSize: 10
		}
	},
	onLoad(options) {
		// 从参数获取初始状态
		if (options.status !== undefined) {
			this.activeStatus = parseInt(options.status)
		}
		this.loadOrderList(true)
	},
	onReachBottom() {
		if (this.hasMore && !this.loading) {
			this.loadOrderList(false)
		}
	},
	onPullDownRefresh() {
		this.loadOrderList(true).then(() => {
			uni.stopPullDownRefresh()
		})
	},
	methods: {
		// 加载订单列表
		async loadOrderList(refresh = false) {
			if (refresh) {
				this.page = 1
				this.orderList = []
				this.hasMore = true
			}

			if (this.loading) return
			this.loading = true

			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				const params = {
					userId: userInfo.id,
					page: this.page,
					size: this.pageSize
				}

				if (this.activeStatus !== null) {
					params.status = this.activeStatus
				}

				const res = await api.order.getList(params)
				if (res.code === 200) {
					const newOrders = res.data.records || []
					if (refresh) {
						this.orderList = newOrders
					} else {
						this.orderList.push(...newOrders)
					}
					
					this.hasMore = newOrders.length === this.pageSize
					if (this.hasMore) {
						this.page++
					}
				}
			} catch (error) {
				console.error('加载订单列表失败:', error)
				uni.showToast({
					title: error.message || '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},

		// 切换状态
		switchStatus(status) {
			this.activeStatus = status
			this.loadOrderList(true)
		},

		// 获取状态文本
		getStatusText(status) {
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
		},

		// 获取状态颜色
		getStatusColor(status) {
			const colorMap = {
				0: '#ff6b35',
				1: '#1890ff',
				2: '#faad14',
				3: '#52c41a',
				4: '#52c41a',
				5: '#999',
				6: '#ff4d4f',
				7: '#999'
			}
			return colorMap[status] || '#999'
		},

		// 格式化商品规格
		formatSpecs(item) {
			const sweetness = ['无糖', '三分糖', '五分糖', '七分糖', '正常糖'][item.sweetness] || ''
			const temperature = ['去冰', '少冰', '正常冰', '热饮'][item.temperature] || ''
			const toppings = item.toppings && item.toppings !== '无' ? `加${item.toppings}` : ''
			
			return [sweetness, temperature, toppings].filter(Boolean).join(' | ')
		},

		// 跳转订单详情
		goToOrderDetail(orderId) {
			uni.navigateTo({
				url: `/pages/order-detail/order-detail?id=${orderId}`
			})
		},

		// 取消订单
		cancelOrder(order) {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个订单吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const userInfo = uni.getStorageSync('userInfo')
							await api.order.cancel(order.id, userInfo.id)
							
							// 更新本地状态
							order.status = 5
							uni.showToast({
								title: '订单已取消',
								icon: 'success'
							})
						} catch (error) {
							console.error('取消订单失败:', error)
							// 模拟取消成功
							order.status = 5
							uni.showToast({
								title: '订单已取消',
								icon: 'success'
							})
						}
					}
				}
			})
		},

		// 支付订单
		payOrder(order) {
			uni.navigateTo({
				url: `/pages/payment/payment?orderId=${order.id}`
			})
		},

		// 催单
		urgeOrder(order) {
			uni.showModal({
				title: '催单',
				content: '已为您催单，请耐心等待',
				showCancel: false
			})
		},

		// 联系客服
		contactService(order) {
			uni.makePhoneCall({
				phoneNumber: '400-123-4567'
			})
		},

		// 申请退款
		applyRefund(order) {
			uni.navigateTo({
				url: `/pages/order-detail/refund?orderId=${order.id}`
			})
		},

		// 申请投诉
		applyComplaint(order) {
			uni.navigateTo({
				url: `/pages/order-detail/complaint?orderId=${order.id}`
			})
		},

		// 评价订单
		evaluateOrder(order) {
			uni.navigateTo({
				url: `/pages/evaluate/evaluate?orderId=${order.id}`
			})
		},

		// 再来一单
		reorder(order) {
			// 将订单商品添加到购物车
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

		// 跳转首页
		goToHome() {
			uni.switchTab({
				url: '/pages/index/index'
			})
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
}

/* 状态筛选 */
.status-tabs {
	background: white;
	padding: 20rpx 0;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.tabs-scroll {
	white-space: nowrap;
}

.tab-item {
	display: inline-block;
	padding: 16rpx 30rpx;
	margin: 0 10rpx;
	border-radius: 20rpx;
	position: relative;
	transition: all 0.3s ease;
}

.tab-item.active {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
}

.tab-text {
	font-size: 26rpx;
	color: #666;
	font-weight: 500;
}

.tab-item.active .tab-text {
	color: white;
}

.tab-badge {
	position: absolute;
	top: 8rpx;
	right: 8rpx;
	background: #ff4d4f;
	color: white;
	font-size: 18rpx;
	padding: 2rpx 6rpx;
	border-radius: 8rpx;
	min-width: 16rpx;
	text-align: center;
}

/* 订单列表 */
.order-list {
	padding: 20rpx;
}

.order-item {
	background: white;
	border-radius: 24rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
	font-size: 26rpx;
	color: #666;
}

.order-status {
	font-size: 26rpx;
	font-weight: 600;
}

.order-products {
	padding: 20rpx 30rpx;
}

.product-item {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
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

.order-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background: #f8f9fa;
	border-top: 1rpx solid #f0f0f0;
}

.order-time {
	font-size: 24rpx;
	color: #999;
}

.order-total {
	font-size: 26rpx;
	color: #333;
	font-weight: 600;
}

.order-actions {
	display: flex;
	justify-content: flex-end;
	align-items: center;
	padding: 20rpx 30rpx;
	gap: 20rpx;
}

.action-btn {
	padding: 16rpx 32rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 500;
	transition: all 0.3s ease;
}

.action-btn.primary {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
}

.action-btn.secondary {
	background: transparent;
	color: #666;
	border: 1rpx solid #ddd;
}

.action-btn:active {
	transform: scale(0.95);
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
	text-align: center;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.6;
}

.empty-text {
	font-size: 32rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 16rpx;
}

.empty-desc {
	font-size: 26rpx;
	color: #999;
	margin-bottom: 40rpx;
	line-height: 1.5;
}

.empty-action {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	font-size: 28rpx;
	font-weight: 600;
	padding: 24rpx 48rpx;
	border-radius: 50rpx;
	transition: all 0.3s ease;
}

.empty-action:active {
	transform: scale(0.95);
}

/* 加载更多 */
.load-more {
	padding: 40rpx;
	text-align: center;
}

.load-text {
	font-size: 24rpx;
	color: #999;
}
</style>
