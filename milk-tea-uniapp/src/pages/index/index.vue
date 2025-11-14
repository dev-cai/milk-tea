<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goToSearch">
				<text class="search-placeholder">搜索你想要的奶茶...</text>
				<text class="search-icon">🔍</text>
			</view>
		</view>

		<!-- 轮播图 -->
		<view class="banner-section">
			<swiper class="banner-swiper" indicator-dots="true" autoplay="true" interval="3000" duration="500">
				<swiper-item v-for="(banner, index) in banners" :key="index">
					<image class="banner-image" :src="banner.image" mode="aspectFill" @click="onBannerTap(banner)"></image>
				</swiper-item>
			</swiper>
		</view>

		<!-- 分类导航 -->
		<view class="category-nav">
			<view class="category-item" v-for="(category, index) in categories" :key="category.id" @click="goToCategory(category.id)">
				<text class="category-icon">{{ category.icon }}</text>
				<text class="category-name">{{ category.name }}</text>
			</view>
		</view>

		<!-- 营销活动入口 -->
		<view class="activity-section" v-if="activities.length > 0">
			<view class="activity-list">
				<view class="activity-item" v-for="activity in activities" :key="activity.id" @click="goToActivity(activity)">
					<view class="activity-icon">{{ activity.icon }}</view>
					<text class="activity-name">{{ activity.name }}</text>
					<text class="activity-desc">{{ activity.desc }}</text>
				</view>
			</view>
		</view>

		<!-- 个性化推荐 -->
		<view class="personalized-section" v-if="personalizedProducts.length > 0">
			<view class="section-header">
				<text class="section-title">🎯 为你推荐</text>
				<text class="section-subtitle">基于你的购买历史</text>
			</view>
			<view class="product-grid">
				<view class="product-item" v-for="product in personalizedProducts" :key="product.id" @click="goToProduct(product.id)">
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-badge" v-if="product.reason">{{ product.reason }}</view>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-price">
							<text class="price-current">¥{{ formatPrice(product.price) }}</text>
							<text class="price-member" v-if="product.memberPrice < product.price">会员¥{{ formatPrice(product.memberPrice) }}</text>
						</view>
						<view class="product-stats">
							<text class="product-sales">月销{{ product.sales }}</text>
							<view class="add-cart-btn" @click.stop="addToCart(product)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 推荐商品 -->
		<view class="recommend-section">
			<view class="section-header">
				<text class="section-title">🔥 热门推荐</text>
				<text class="section-more" @click="goToCategory()">更多 ></text>
			</view>
			<view class="product-grid">
				<view class="product-item" v-for="product in recommendProducts" :key="product.id" @click="goToProduct(product.id)">
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-price">
							<text class="price-current">¥{{ formatPrice(product.price) }}</text>
							<text class="price-member" v-if="product.memberPrice < product.price">会员¥{{ formatPrice(product.memberPrice) }}</text>
						</view>
						<view class="product-stats">
							<text class="product-sales">月销{{ product.sales }}</text>
							<view class="add-cart-btn" @click.stop="addToCart(product)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 今日特惠 -->
		<view class="special-section">
			<view class="section-header">
				<text class="section-title">💰 今日特惠</text>
				<text class="section-more" @click="goToCategory()">更多 ></text>
			</view>
			<scroll-view class="special-scroll" scroll-x="true" show-scrollbar="false">
				<view class="special-item" v-for="product in specialProducts" :key="product.id" @click="goToProduct(product.id)">
					<image class="special-image" :src="product.image" mode="aspectFill"></image>
					<view class="special-info">
						<text class="special-name">{{ product.name }}</text>
						<view class="special-price">
							<text class="price-special">¥{{ formatPrice(product.price) }}</text>
							<text class="price-original">¥{{ formatPrice(product.originalPrice) }}</text>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 新品推荐 -->
		<view class="new-section">
			<view class="section-header">
				<text class="section-title">✨ 新品推荐</text>
				<text class="section-more" @click="goToCategory()">更多 ></text>
			</view>
			<view class="new-list">
				<view class="new-item" v-for="product in newProducts" :key="product.id" @click="goToProduct(product.id)">
					<image class="new-image" :src="product.image" mode="aspectFill"></image>
					<view class="new-info">
						<text class="new-name">{{ product.name }}</text>
						<text class="new-desc">{{ product.description }}</text>
						<view class="new-price">
							<text class="price-current">¥{{ formatPrice(product.price) }}</text>
							<text class="new-tag">新品</text>
						</view>
					</view>
					<view class="new-action">
						<view class="add-cart-btn" @click.stop="addToCart(product)">
							<text>+</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice } from '@/utils/common.js'
import { addToCart } from '@/utils/cart.js'

export default {
	data() {
		return {
			banners: [],
			categories: [],
			activities: [],
			personalizedProducts: [],
			recommendProducts: [],
			specialProducts: [],
			newProducts: []
		}
	},
	onLoad() {
		this.loadData()
	},
	onPullDownRefresh() {
		this.loadData().then(() => {
			uni.stopPullDownRefresh()
		})
	},
	methods: {
		// 加载数据
		async loadData() {
			try {
				await Promise.all([
					this.loadBanners(),
					this.loadCategories(),
					this.loadActivities(),
					this.loadPersonalizedProducts(),
					this.loadRecommendProducts(),
					this.loadSpecialProducts(),
					this.loadNewProducts()
				])
			} catch (error) {
				console.error('加载数据失败:', error)
			}
		},

		// 加载轮播图
		async loadBanners() {
			try {
				const res = await api.marketing.getBanners()
				this.banners = res.data || [
					{ id: 1, image: '/static/banner1.jpg', url: '' },
					{ id: 2, image: '/static/banner2.jpg', url: '' },
					{ id: 3, image: '/static/banner3.jpg', url: '' }
				]
			} catch (error) {
				// 使用默认数据
				this.banners = [
					{ id: 1, image: '/static/banner1.jpg', url: '' },
					{ id: 2, image: '/static/banner2.jpg', url: '' },
					{ id: 3, image: '/static/banner3.jpg', url: '' }
				]
			}
		},

		// 加载分类
		async loadCategories() {
			try {
				const res = await api.category.getList()
				this.categories = res.data || []
			} catch (error) {
				// 使用默认数据
				this.categories = [
					{ id: 1, name: '奶茶', icon: '🧋' },
					{ id: 2, name: '咖啡', icon: '☕' },
					{ id: 3, name: '果茶', icon: '🍹' },
					{ id: 4, name: '小食', icon: '🍰' }
				]
			}
		},

		// 加载营销活动
		async loadActivities() {
			try {
				const res = await api.marketing.getActivities()
				this.activities = res.data || []
			} catch (error) {
				// 使用默认数据
				this.activities = [
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
			}
		},

		// 加载个性化推荐
		async loadPersonalizedProducts() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					this.personalizedProducts = []
					return
				}

				const res = await api.product.getPersonalized(userInfo.id, 4)
				this.personalizedProducts = res.data || []
			} catch (error) {
				// 使用模拟数据
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo && userInfo.id) {
					this.personalizedProducts = [
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
				}
			}
		},

		// 加载推荐商品
		async loadRecommendProducts() {
			try {
				const res = await api.product.getRecommend(6)
				this.recommendProducts = res.data || []
			} catch (error) {
				// 使用默认数据
				this.recommendProducts = [
					{
						id: 1,
						name: '珍珠奶茶',
						description: '经典珍珠奶茶，香甜可口',
						image: '/static/product1.jpg',
						price: 18.00,
						memberPrice: 16.00,
						sales: 999
					},
					{
						id: 2,
						name: '芝士奶盖',
						description: '浓郁芝士，层次丰富',
						image: '/static/product2.jpg',
						price: 22.00,
						memberPrice: 20.00,
						sales: 888
					}
				]
			}
		},

		// 加载特惠商品
		async loadSpecialProducts() {
			try {
				const res = await api.product.getList({ special: true, limit: 10 })
				this.specialProducts = res.data?.records || []
			} catch (error) {
				// 使用默认数据
				this.specialProducts = [
					{
						id: 3,
						name: '柠檬蜂蜜茶',
						image: '/static/product3.jpg',
						price: 15.00,
						originalPrice: 20.00
					},
					{
						id: 4,
						name: '红豆奶茶',
						image: '/static/product4.jpg',
						price: 16.00,
						originalPrice: 22.00
					}
				]
			}
		},

		// 加载新品
		async loadNewProducts() {
			try {
				const res = await api.product.getList({ isNew: true, limit: 5 })
				this.newProducts = res.data?.records || []
			} catch (error) {
				// 使用默认数据
				this.newProducts = [
					{
						id: 5,
						name: '抹茶拿铁',
						description: '日式抹茶，香醇浓郁',
						image: '/static/product5.jpg',
						price: 25.00
					},
					{
						id: 6,
						name: '草莓奶昔',
						description: '新鲜草莓，清甜可口',
						image: '/static/product6.jpg',
						price: 20.00
					}
				]
			}
		},

		// 轮播图点击
		onBannerTap(banner) {
			if (banner.url) {
				// 处理跳转逻辑
				console.log('点击轮播图:', banner)
			}
		},

		// 跳转搜索页面
		goToSearch() {
			uni.navigateTo({
				url: '/pages/search/search'
			})
		},

		// 跳转分类页面
		goToCategory(categoryId) {
			uni.switchTab({
				url: `/pages/category/category${categoryId ? '?categoryId=' + categoryId : ''}`
			})
		},

		// 跳转商品详情
		goToProduct(productId) {
			uni.navigateTo({
				url: `/pages/product/product?id=${productId}`
			})
		},

		// 跳转营销活动
		goToActivity(activity) {
			switch (activity.type) {
				case 'coupon':
					uni.navigateTo({
						url: '/pages/coupon/coupon'
					})
					break
				case 'member':
					uni.navigateTo({
						url: '/pages/member/member'
					})
					break
				case 'points':
					uni.navigateTo({
						url: '/pages/points/points'
					})
					break
				default:
					uni.showToast({
						title: '功能开发中',
						icon: 'none'
					})
			}
		},

		// 添加到购物车
		addToCart(product) {
			addToCart(product)
		},

		// 格式化价格
		formatPrice
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #ff6b35 0%, #f8f9fa 30%);
}

/* 搜索栏 */
.search-bar {
	padding: 30rpx;
	background: transparent;
}

.search-input {
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 50rpx;
	padding: 24rpx 32rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
}

.search-input:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
}

.search-placeholder {
	color: #666;
	font-size: 28rpx;
	font-weight: 500;
}

.search-icon {
	font-size: 32rpx;
	color: #ff6b35;
}

/* 轮播图 */
.banner-section {
	margin: 20rpx 30rpx;
	position: relative;
}

.banner-swiper {
	height: 320rpx;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 12rpx 48rpx rgba(0, 0, 0, 0.15);
	border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.banner-image {
	width: 100%;
	height: 100%;
	transition: transform 0.3s ease;
}

.banner-image:active {
	transform: scale(1.02);
}

/* 分类导航 */
.category-nav {
	display: flex;
	justify-content: space-around;
	padding: 40rpx 20rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	margin: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.category-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 20rpx;
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
}

.category-item::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: linear-gradient(135deg, #ff6b35, #f7931e);
	opacity: 0;
	transition: opacity 0.3s ease;
	border-radius: 20rpx;
}

.category-item:active::before {
	opacity: 0.1;
}

.category-item:active {
	transform: scale(0.95);
}

.category-icon {
	font-size: 64rpx;
	margin-bottom: 12rpx;
	position: relative;
	z-index: 1;
	filter: drop-shadow(0 2rpx 8rpx rgba(0, 0, 0, 0.1));
}

.category-name {
	font-size: 24rpx;
	color: #333;
	font-weight: 500;
	position: relative;
	z-index: 1;
}

/* 营销活动 */
.activity-section {
	margin: 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.activity-list {
	display: flex;
	justify-content: space-around;
	gap: 20rpx;
}

.activity-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	border-radius: 20rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.1) 0%, rgba(247, 147, 30, 0.1) 100%);
	border: 2rpx solid rgba(255, 107, 53, 0.2);
	transition: all 0.3s ease;
}

.activity-item:active {
	transform: scale(0.95);
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.2) 0%, rgba(247, 147, 30, 0.2) 100%);
}

.activity-icon {
	font-size: 48rpx;
	margin-bottom: 12rpx;
}

.activity-name {
	font-size: 26rpx;
	font-weight: 600;
	color: #ff6b35;
	margin-bottom: 6rpx;
}

.activity-desc {
	font-size: 20rpx;
	color: #999;
}

/* 个性化推荐 */
.personalized-section {
	margin: 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	position: relative;
	overflow: hidden;
}

.personalized-section::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 6rpx;
	background: linear-gradient(90deg, #667eea, #764ba2);
	border-radius: 24rpx 24rpx 0 0;
}

.section-subtitle {
	font-size: 22rpx;
	color: #999;
	margin-left: 8rpx;
}

.product-badge {
	position: absolute;
	top: 12rpx;
	left: 12rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	font-size: 18rpx;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	font-weight: 500;
	z-index: 2;
}

/* 通用区块样式 */
.recommend-section,
.special-section,
.new-section {
	margin: 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	position: relative;
	overflow: hidden;
}

.recommend-section::before,
.special-section::before,
.new-section::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 6rpx;
	background: linear-gradient(90deg, #ff6b35, #f7931e, #ff8c42);
	border-radius: 24rpx 24rpx 0 0;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
	position: relative;
}

.section-title {
	font-size: 36rpx;
	font-weight: 700;
	color: #1a1a1a;
	position: relative;
}

.section-more {
	font-size: 26rpx;
	color: #ff6b35;
	font-weight: 500;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	background: rgba(255, 107, 53, 0.1);
	transition: all 0.3s ease;
}

.section-more:active {
	background: rgba(255, 107, 53, 0.2);
	transform: scale(0.95);
}

/* 推荐商品网格 */
.product-grid {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
	gap: 20rpx;
}

.product-item {
	width: calc(50% - 10rpx);
	background: rgba(255, 255, 255, 0.9);
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	border: 1rpx solid rgba(255, 255, 255, 0.3);
	transition: all 0.3s ease;
	position: relative;
}

.product-item:active {
	transform: translateY(-4rpx);
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
}

.product-image {
	width: 100%;
	height: 220rpx;
	transition: transform 0.3s ease;
}

.product-item:active .product-image {
	transform: scale(1.05);
}

.product-info {
	padding: 24rpx;
}

.product-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #1a1a1a;
	margin-bottom: 8rpx;
	display: block;
	line-height: 1.3;
}

.product-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 16rpx;
	display: block;
	line-height: 1.4;
	opacity: 0.8;
}

.product-price {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.price-current {
	font-size: 32rpx;
	font-weight: 700;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	margin-right: 12rpx;
}

.price-member {
	font-size: 20rpx;
	color: #8b4513;
	background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	font-weight: 500;
	box-shadow: 0 2rpx 8rpx rgba(255, 215, 0, 0.2);
}

.product-stats {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-sales {
	font-size: 22rpx;
	color: #999;
	font-weight: 500;
}

.add-cart-btn {
	width: 60rpx;
	height: 60rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 32rpx;
	font-weight: bold;
	box-shadow: 0 6rpx 20rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
	border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.add-cart-btn:active {
	transform: scale(0.9);
	box-shadow: 0 3rpx 10rpx rgba(255, 107, 53, 0.4);
}

/* 特惠商品横向滚动 */
.special-scroll {
	white-space: nowrap;
	padding: 10rpx 0;
}

.special-item {
	display: inline-block;
	width: 220rpx;
	margin-right: 24rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 20rpx;
	overflow: hidden;
	vertical-align: top;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	border: 1rpx solid rgba(255, 255, 255, 0.3);
	transition: all 0.3s ease;
	position: relative;
}

.special-item:active {
	transform: translateY(-2rpx);
	box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.1);
}

.special-item::before {
	content: '特惠';
	position: absolute;
	top: 12rpx;
	right: 12rpx;
	background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
	color: white;
	font-size: 18rpx;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
	font-weight: 500;
	z-index: 2;
	box-shadow: 0 2rpx 8rpx rgba(255, 77, 79, 0.3);
}

.special-image {
	width: 100%;
	height: 160rpx;
	transition: transform 0.3s ease;
}

.special-item:active .special-image {
	transform: scale(1.05);
}

.special-info {
	padding: 20rpx;
}

.special-name {
	font-size: 24rpx;
	color: #1a1a1a;
	margin-bottom: 12rpx;
	display: block;
	white-space: normal;
	font-weight: 600;
	line-height: 1.3;
}

.special-price {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.price-special {
	font-size: 28rpx;
	font-weight: 700;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.price-original {
	font-size: 20rpx;
	color: #999;
	text-decoration: line-through;
	opacity: 0.7;
}

/* 新品推荐列表 */
.new-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.new-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: rgba(255, 255, 255, 0.8);
	border-radius: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	border: 1rpx solid rgba(255, 255, 255, 0.3);
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
}

.new-item::before {
	content: '';
	position: absolute;
	left: 0;
	top: 0;
	bottom: 0;
	width: 6rpx;
	background: linear-gradient(180deg, #ff6b35, #f7931e);
	border-radius: 0 6rpx 6rpx 0;
}

.new-item:active {
	transform: translateX(4rpx);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.new-image {
	width: 140rpx;
	height: 140rpx;
	border-radius: 20rpx;
	margin-right: 24rpx;
	transition: transform 0.3s ease;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.new-item:active .new-image {
	transform: scale(1.05);
}

.new-info {
	flex: 1;
}

.new-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #1a1a1a;
	margin-bottom: 8rpx;
	display: block;
	line-height: 1.3;
}

.new-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 16rpx;
	display: block;
	line-height: 1.4;
	opacity: 0.8;
}

.new-price {
	display: flex;
	align-items: center;
}

.new-tag {
	background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
	color: white;
	font-size: 18rpx;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	margin-left: 12rpx;
	font-weight: 500;
	box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.3);
}

.new-action {
	display: flex;
	align-items: center;
	margin-left: 20rpx;
}
</style>
