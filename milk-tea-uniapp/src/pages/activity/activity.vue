<template>
	<view class="container">
		<!-- 活动头部 -->
		<view class="activity-header">
			<image class="header-bg" :src="activity.banner || '/static/images/activity-bg.jpg'" mode="aspectFill"></image>
			<view class="header-overlay">
				<view class="activity-icon">{{ activity.icon }}</view>
				<text class="activity-title">{{ activity.name }}</text>
				<text class="activity-time">{{ formatTime(activity.startTime) }} - {{ formatTime(activity.endTime) }}</text>
			</view>
		</view>

		<!-- 活动说明 -->
		<view class="activity-section">
			<view class="section-title">活动说明</view>
			<text class="section-content">{{ activity.description }}</text>
		</view>

		<!-- 活动规则 -->
		<view class="activity-section">
			<view class="section-title">活动规则</view>
			<text class="section-content">{{ activity.rules }}</text>
		</view>

		<!-- 参与商品 -->
		<view class="activity-section">
			<view class="section-title">参与商品</view>
			<view class="product-list">
				<view class="product-item" v-for="product in products" :key="product.id" @click="goToProduct(product.id)">
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-price">
							<text class="price-current">¥{{ formatPrice(product.price) }}</text>
							<text class="price-original" v-if="activityType === 'buy_one_get_one'">买一送一</text>
							<text class="price-tag" v-if="activityType === 'new_product'">新品</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 参与按钮 -->
		<view class="bottom-bar">
			<view class="participants-info">
				<text class="participants-count">{{ activity.participants || 0 }}人已参与</text>
			</view>
			<button class="join-btn" @click="joinActivity">立即参与</button>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			activityId: null,
			activityType: '',
			activity: {
				name: '',
				icon: '',
				description: '',
				rules: '',
				banner: '',
				startTime: '',
				endTime: '',
				participants: 0
			},
			products: []
		}
	},
	onLoad(options) {
		if (options.id) {
			this.activityId = options.id
		}
		if (options.type) {
			this.activityType = options.type
		}
		this.loadActivityDetail()
		this.loadActivityProducts()
	},
	methods: {
		// 加载活动详情
		async loadActivityDetail() {
			try {
				const res = await api.marketing.getActivityDetail(this.activityId)
				if (res.code === 200) {
					this.activity = res.data
				}
			} catch (error) {
				console.error('加载活动详情失败:', error)
			}
		},

		// 加载活动商品
		async loadActivityProducts() {
			try {
				let res
				if (this.activityType === 'new_product') {
					// 加载新品
					res = await api.product.getPage({
						page: 1,
						size: 20,
						sortType: 'default',
						sortOrder: 'desc'
					})
				} else if (this.activityType === 'buy_one_get_one') {
					// 加载促销商品（这里简化为热销商品）
					res = await api.product.getHot(20)
				}
				
				if (res && res.code === 200) {
					this.products = res.data.records || res.data || []
				}
			} catch (error) {
				console.error('加载活动商品失败:', error)
			}
		},

		// 参与活动
		async joinActivity() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/login/login'
						})
					}, 1500)
					return
				}

				const res = await api.marketing.joinActivity(this.activityId, {
					userId: userInfo.id
				})

				if (res.code === 200) {
					uni.showToast({
						title: '参与成功',
						icon: 'success'
					})
					// 刷新活动详情
					this.loadActivityDetail()
				} else {
					uni.showToast({
						title: res.message || '参与失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('参与活动失败:', error)
				uni.showToast({
					title: '参与失败',
					icon: 'none'
				})
			}
		},

		// 跳转商品详情
		goToProduct(id) {
			uni.navigateTo({
				url: `/pages/product/product?id=${id}`
			})
		},

		// 格式化时间
		formatTime(time) {
			if (!time) return ''
			const date = new Date(time)
			const month = date.getMonth() + 1
			const day = date.getDate()
			return `${month}月${day}日`
		},

		// 格式化价格
		formatPrice(price) {
			return Number(price).toFixed(2)
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

/* 活动头部 */
.activity-header {
	position: relative;
	height: 400rpx;
	overflow: hidden;
}

.header-bg {
	width: 100%;
	height: 100%;
}

.header-overlay {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: linear-gradient(180deg, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.6) 100%);
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	color: white;
}

.activity-icon {
	font-size: 80rpx;
	margin-bottom: 20rpx;
}

.activity-title {
	font-size: 48rpx;
	font-weight: bold;
	margin-bottom: 12rpx;
}

.activity-time {
	font-size: 24rpx;
	opacity: 0.9;
}

/* 活动区块 */
.activity-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 20rpx;
	padding-left: 20rpx;
	border-left: 6rpx solid #ff6b35;
}

.section-content {
	font-size: 28rpx;
	color: #666;
	line-height: 1.8;
}

/* 商品列表 */
.product-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.product-item {
	display: flex;
	background: #f8f9fa;
	border-radius: 16rpx;
	overflow: hidden;
	transition: all 0.3s ease;
}

.product-item:active {
	transform: scale(0.98);
	background: #f0f0f0;
}

.product-image {
	width: 180rpx;
	height: 180rpx;
	flex-shrink: 0;
}

.product-info {
	flex: 1;
	padding: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.product-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
}

.product-desc {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 12rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.product-price {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.price-current {
	font-size: 32rpx;
	font-weight: bold;
	color: #ff6b35;
}

.price-original {
	font-size: 24rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.price-tag {
	font-size: 24rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

/* 底部栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx 30rpx;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.08);
	display: flex;
	align-items: center;
	justify-content: space-between;
	z-index: 100;
}

.participants-info {
	flex: 1;
}

.participants-count {
	font-size: 24rpx;
	color: #999;
}

.join-btn {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	border: none;
	border-radius: 48rpx;
	padding: 24rpx 60rpx;
	font-size: 28rpx;
	font-weight: 600;
	box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.3);
}

.join-btn:active {
	opacity: 0.8;
	transform: scale(0.98);
}
</style>
