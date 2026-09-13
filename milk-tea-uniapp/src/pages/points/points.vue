<template>
	<view class="container">
		<!-- 积分头部 -->
		<view class="points-header">
			<view class="points-info">
				<text class="points-title">我的积分</text>
				<text class="points-amount">{{ userInfo.points || 0 }}</text>
				<text class="points-desc">积分可兑换商品和优惠券</text>
			</view>
			<view class="points-icon">
				<text>🎯</text>
			</view>
		</view>

		<!-- 积分规则 -->
		<view class="rules-section">
			<view class="section-header">
				<text class="section-title">积分规则</text>
			</view>
			<view class="rules-list">
				<view class="rule-item">
					<text class="rule-icon">💰</text>
					<text class="rule-text">消费1元获得1积分</text>
				</view>
				<view class="rule-item">
					<text class="rule-icon">👥</text>
					<text class="rule-text">邀请好友获得50积分</text>
				</view>
				<view class="rule-item">
					<text class="rule-icon">📅</text>
					<text class="rule-text">每日签到获得5积分</text>
				</view>
			</view>
		</view>

		<!-- 兑换商品 -->
		<view class="exchange-section">
			<view class="section-header">
				<text class="section-title">积分兑换</text>
			</view>
			<view class="exchange-tabs">
				<view class="tab-item" :class="{ active: activeTab === 'products' }" @click="switchTab('products')">
					<text>商品兑换</text>
				</view>
				<view class="tab-item" :class="{ active: activeTab === 'coupons' }" @click="switchTab('coupons')">
					<text>优惠券</text>
				</view>
			</view>
			
			<!-- 商品兑换 -->
			<view class="products-grid" v-if="activeTab === 'products'">
				<view class="product-item" v-for="product in exchangeProducts" :key="product.id" @click="exchangeProduct(product)">
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<view class="product-points">
							<text class="points-need">{{ product.points }}积分</text>
							<text class="product-stock">库存{{ product.stock }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 优惠券兑换 -->
			<view class="coupons-list" v-if="activeTab === 'coupons'">
				<view class="coupon-item" v-for="coupon in exchangeCoupons" :key="coupon.id" @click="exchangeCoupon(coupon)">
					<view class="coupon-info">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-desc">{{ coupon.desc }}</text>
					</view>
					<view class="coupon-points">
						<text class="points-need">{{ coupon.points }}积分</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 积分明细 -->
		<view class="history-section">
			<view class="section-header">
				<text class="section-title">积分明细</text>
			</view>
			<view class="history-list">
				<view class="history-item" v-for="record in pointsHistory" :key="record.id">
					<view class="history-info">
						<text class="history-desc">{{ record.desc }}</text>
						<text class="history-time">{{ record.time }}</text>
					</view>
					<text class="history-points" :class="{ positive: record.points > 0 }">
						{{ record.points > 0 ? '+' : '' }}{{ record.points }}
					</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			userInfo: {},
			activeTab: 'products',
			exchangeProducts: [],
			exchangeCoupons: [],
			pointsHistory: []
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadPointsData()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo) {
				this.userInfo = userInfo
			}
		},

		// 加载积分数据
		async loadPointsData() {
			const userInfo = this.userInfo
			if (!userInfo || !userInfo.id) return
			try {
				const [products, history] = await Promise.all([
					api.points.getProducts({ page: 1, size: 50 }),
					api.points.getHistory(userInfo.id, { page: 1, size: 50 })
				])
				this.exchangeProducts = products.data?.records || products.data || []
				const records = history.data?.records || history.data || []
				this.pointsHistory = records.map(record => ({ ...record, desc: record.description || record.reason, time: record.createTime }))
			} catch (error) {
				console.error('加载积分数据失败:', error)
			}
		},

		// 切换标签
		switchTab(tab) {
			this.activeTab = tab
		},

		// 兑换商品
		exchangeProduct(product) {
			if (this.userInfo.points < product.points) {
				uni.showToast({
					title: '积分不足',
					icon: 'none'
				})
				return
			}

			uni.showModal({
				title: '确认兑换',
				content: `确定用${product.points}积分兑换${product.name}吗？`,
				success: (res) => {
					if (res.confirm) {
						api.points.exchange({ userId: this.userInfo.id, productId: product.id, quantity: 1 }).then(() => {
							uni.showToast({ title: '兑换成功', icon: 'success' })
							this.loadPointsData()
						}).catch(error => uni.showToast({ title: error.message || '兑换失败', icon: 'none' }))
					}
				}
			})
		},

		// 兑换优惠券
		exchangeCoupon(coupon) {
			if (this.userInfo.points < coupon.points) {
				uni.showToast({
					title: '积分不足',
					icon: 'none'
				})
				return
			}

			uni.showModal({
				title: '确认兑换',
				content: `确定用${coupon.points}积分兑换${coupon.name}吗？`,
				success: (res) => {
					if (res.confirm) {
						uni.showToast({ title: '该券暂不支持积分兑换', icon: 'none' })
					}
				}
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
}

.points-header {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 40rpx 30rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.points-info {
	flex: 1;
}

.points-title {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
	display: block;
	margin-bottom: 8rpx;
}

.points-amount {
	font-size: 48rpx;
	font-weight: 700;
	color: white;
	display: block;
	margin-bottom: 8rpx;
}

.points-desc {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.points-icon {
	font-size: 80rpx;
}

.rules-section,
.exchange-section,
.history-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.rules-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.rule-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 16rpx;
}

.rule-icon {
	font-size: 32rpx;
	margin-right: 16rpx;
}

.rule-text {
	font-size: 26rpx;
	color: #666;
}

.exchange-tabs {
	display: flex;
	background: #f0f0f0;
	border-radius: 12rpx;
	padding: 6rpx;
	margin-bottom: 30rpx;
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 16rpx;
	border-radius: 8rpx;
	font-size: 26rpx;
	color: #666;
	transition: all 0.3s ease;
}

.tab-item.active {
	background: white;
	color: #ff6b35;
	font-weight: 600;
}

.products-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.product-item {
	width: calc(50% - 10rpx);
	border-radius: 16rpx;
	overflow: hidden;
	background: #f8f9fa;
}

.product-image {
	width: 100%;
	height: 200rpx;
}

.product-info {
	padding: 20rpx;
}

.product-name {
	font-size: 26rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 12rpx;
}

.product-points {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.points-need {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.product-stock {
	font-size: 22rpx;
	color: #999;
}

.coupons-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.coupon-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: #f8f9fa;
	border-radius: 16rpx;
}

.coupon-info {
	flex: 1;
}

.coupon-name {
	font-size: 26rpx;
	color: #333;
	font-weight: 600;
	display: block;
	margin-bottom: 6rpx;
}

.coupon-desc {
	font-size: 22rpx;
	color: #666;
}

.coupon-points .points-need {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.history-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.history-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
	border-bottom: none;
}

.history-info {
	flex: 1;
}

.history-desc {
	font-size: 26rpx;
	color: #333;
	display: block;
	margin-bottom: 6rpx;
}

.history-time {
	font-size: 22rpx;
	color: #999;
}

.history-points {
	font-size: 28rpx;
	font-weight: 600;
	color: #ff4d4f;
}

.history-points.positive {
	color: #52c41a;
}
</style>
