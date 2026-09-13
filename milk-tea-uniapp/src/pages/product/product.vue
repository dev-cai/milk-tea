<template>
	<view class="container" v-if="product">
		<!-- 商品图片轮播 -->
		<view class="product-images">
			<swiper class="image-swiper" indicator-dots="true" autoplay="false" interval="3000" duration="500">
				<swiper-item v-for="(image, index) in productImages" :key="index">
					<image class="product-image" :src="image" mode="aspectFill" @click="previewImage(image)"></image>
				</swiper-item>
			</swiper>
			<view class="image-indicator">
				<text>{{ currentImageIndex + 1 }}/{{ productImages.length }}</text>
			</view>
		</view>

		<!-- 商品基本信息 -->
		<view class="product-info">
			<view class="product-header">
				<view class="product-title">
					<text class="product-name">{{ product.name }}</text>
					<view class="product-tags" v-if="product.tags && product.tags.length > 0">
						<view class="product-tag" v-for="tag in product.tags" :key="tag">
							<text>{{ tag }}</text>
						</view>
					</view>
				</view>
				<view class="product-actions">
					<view class="action-btn" @click="collectProduct">
						<text class="action-icon">{{ product.isCollected ? '❤️' : '🤍' }}</text>
					</view>
					<view class="action-btn" @click="shareProduct">
						<text class="action-icon">📤</text>
					</view>
				</view>
			</view>

			<text class="product-desc">{{ product.description }}</text>

			<!-- 价格信息 -->
			<view class="price-section">
				<view class="price-main">
					<text class="price-symbol">¥</text>
					<text class="price-value">{{ formatPrice(calculateTotalPrice()) }}</text>
					<text class="price-original" v-if="product.originalPrice > product.price">
						¥{{ formatPrice(product.originalPrice) }}
					</text>
					<text class="price-member" v-if="userInfo.memberLevel > 0">
						会员价¥{{ formatPrice(product.memberPrice || product.price) }}
					</text>
				</view>
				<view class="price-stats">
					<text class="product-sales">月销{{ product.sales || 0 }}</text>
					<text class="product-rating" v-if="product.rating">
						⭐{{ product.rating }}分
					</text>
				</view>
			</view>
		</view>

		<!-- 定制化选项 -->
		<view class="customize-section">
			<!-- 甜度选择 -->
			<view class="customize-item">
				<view class="customize-header">
					<text class="customize-title">甜度</text>
					<text class="customize-required">必选</text>
				</view>
				<view class="option-list">
					<view class="option-item" 
						:class="{ active: selectedOptions.sweetness === option.value }"
						v-for="option in sweetnessOptions" 
						:key="option.value"
						@click="selectSweetness(option.value)"
					>
						<text class="option-name">{{ option.name }}</text>
						<text class="option-desc" v-if="option.desc">{{ option.desc }}</text>
						<text class="option-price" v-if="option.price > 0">+¥{{ formatPrice(option.price) }}</text>
					</view>
				</view>
			</view>

			<!-- 温度选择 -->
			<view class="customize-item">
				<view class="customize-header">
					<text class="customize-title">温度</text>
					<text class="customize-required">必选</text>
				</view>
				<view class="option-list">
					<view class="option-item" 
						:class="{ active: selectedOptions.temperature === option.value }"
						v-for="option in temperatureOptions" 
						:key="option.value"
						@click="selectTemperature(option.value)"
					>
						<text class="option-name">{{ option.name }}</text>
						<text class="option-desc" v-if="option.desc">{{ option.desc }}</text>
						<text class="option-price" v-if="option.price > 0">+¥{{ formatPrice(option.price) }}</text>
					</view>
				</view>
			</view>

			<!-- 加料选择 -->
			<view class="customize-item">
				<view class="customize-header">
					<text class="customize-title">加料</text>
					<text class="customize-optional">可选</text>
				</view>
				<view class="option-list">
					<view class="option-item multiple" 
						:class="{ active: selectedOptions.toppings.includes(option.value) }"
						v-for="option in toppingOptions" 
						:key="option.value"
						@click="toggleTopping(option.value)"
					>
						<view class="option-info">
							<text class="option-name">{{ option.name }}</text>
							<text class="option-desc" v-if="option.desc">{{ option.desc }}</text>
						</view>
						<text class="option-price">+¥{{ formatPrice(option.price) }}</text>
						<view class="option-check">
							<text v-if="selectedOptions.toppings.includes(option.value)">✓</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 规格选择 -->
			<view class="customize-item" v-if="product.sizes && product.sizes.length > 1">
				<view class="customize-header">
					<text class="customize-title">规格</text>
					<text class="customize-required">必选</text>
				</view>
				<view class="option-list">
					<view class="option-item" 
						:class="{ active: selectedOptions.size === option.value }"
						v-for="option in product.sizes" 
						:key="option.value"
						@click="selectSize(option.value)"
					>
						<text class="option-name">{{ option.name }}</text>
						<text class="option-desc" v-if="option.desc">{{ option.desc }}</text>
						<text class="option-price" v-if="option.price !== product.price">
							{{ option.price > product.price ? '+' : '' }}¥{{ formatPrice(Math.abs(option.price - product.price)) }}
						</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 商品详情 -->
		<view class="detail-section">
			<view class="detail-tabs">
				<view class="tab-item" :class="{ active: activeTab === 'detail' }" @click="switchTab('detail')">
					<text>商品详情</text>
				</view>
				<view class="tab-item" :class="{ active: activeTab === 'nutrition' }" @click="switchTab('nutrition')">
					<text>营养成分</text>
				</view>
				<view class="tab-item" :class="{ active: activeTab === 'reviews' }" @click="switchTab('reviews')">
					<text>用户评价</text>
				</view>
			</view>
			
			<view class="tab-content">
				<!-- 商品详情 -->
				<view class="detail-content" v-if="activeTab === 'detail'">
					<text class="detail-text">{{ product.detailDescription || '暂无详细描述' }}</text>
					<view class="detail-images" v-if="product.detailImages && product.detailImages.length > 0">
						<image class="detail-image" 
							v-for="(image, index) in product.detailImages" 
							:key="index"
							:src="image" 
							mode="widthFix"
							@click="previewImage(image, product.detailImages)"
						></image>
					</view>
				</view>
				
				<!-- 营养成分 -->
				<view class="nutrition-content" v-if="activeTab === 'nutrition'">
					<view class="nutrition-item" v-for="item in product.nutrition" :key="item.name" v-if="product.nutrition">
						<text class="nutrition-name">{{ item.name }}</text>
						<text class="nutrition-value">{{ item.value }}</text>
					</view>
					<text class="nutrition-empty" v-else>暂无营养成分信息</text>
				</view>
				
				<!-- 用户评价 -->
				<view class="reviews-content" v-if="activeTab === 'reviews'">
					<view class="reviews-summary" v-if="product.reviewSummary">
						<text class="reviews-rating">{{ product.reviewSummary.rating }}分</text>
						<text class="reviews-count">{{ product.reviewSummary.count }}条评价</text>
					</view>
					<view class="review-item" v-for="review in product.reviews" :key="review.id" v-if="product.reviews">
						<view class="review-header">
							<image class="review-avatar" :src="review.userAvatar" mode="aspectFill"></image>
							<view class="review-info">
								<text class="review-name">{{ review.userName }}</text>
								<text class="review-rating">{{ '⭐'.repeat(review.rating) }}</text>
							</view>
							<text class="review-time">{{ review.createTime }}</text>
						</view>
						<text class="review-content">{{ review.content }}</text>
						<view class="review-images" v-if="review.images && review.images.length > 0">
							<image class="review-image" 
								v-for="(image, index) in review.images" 
								:key="index"
								:src="image" 
								mode="aspectFill"
								@click="previewImage(image, review.images)"
							></image>
						</view>
					</view>
					<text class="reviews-empty" v-if="!product.reviews || product.reviews.length === 0">暂无评价</text>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-actions">
			<view class="quantity-control">
				<view class="quantity-btn" @click="decreaseQuantity">
					<text>-</text>
				</view>
				<text class="quantity-value">{{ quantity }}</text>
				<view class="quantity-btn" @click="increaseQuantity">
					<text>+</text>
				</view>
			</view>
			<view class="action-buttons">
				<view class="action-btn secondary" @click="addToCart">
					<text>加入购物车</text>
				</view>
				<view class="action-btn primary" @click="buyNow">
					<text>立即购买</text>
				</view>
			</view>
		</view>
	</view>
	<view v-else class="empty-state"><text>商品暂不可用</text></view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice } from '@/utils/common.js'
import { addToCart } from '@/utils/cart.js'

export default {
	data() {
		return {
			productId: null,
			product: {},
			productImages: [],
			currentImageIndex: 0,
			userInfo: {},
			activeTab: 'detail',
			quantity: 1,
			selectedOptions: {
				sweetness: 2, // 默认五分糖
				temperature: 2, // 默认正常冰
				toppings: [],
				size: 'medium'
			},
			sweetnessOptions: [
				{ value: 0, name: '无糖', desc: '0%糖分', price: 0 },
				{ value: 1, name: '三分糖', desc: '30%糖分', price: 0 },
				{ value: 2, name: '五分糖', desc: '50%糖分', price: 0 },
				{ value: 3, name: '七分糖', desc: '70%糖分', price: 0 },
				{ value: 4, name: '正常糖', desc: '100%糖分', price: 0 }
			],
			temperatureOptions: [
				{ value: 0, name: '去冰', desc: '0°C', price: 0 },
				{ value: 1, name: '少冰', desc: '5°C', price: 0 },
				{ value: 2, name: '正常冰', desc: '10°C', price: 0 },
				{ value: 3, name: '热饮', desc: '65°C', price: 0 }
			],
			toppingOptions: [
				{ value: 'pearl', name: '珍珠', desc: '经典Q弹珍珠', price: 3 },
				{ value: 'coconut', name: '椰果', desc: '爽脆椰果', price: 3 },
				{ value: 'pudding', name: '布丁', desc: '香滑布丁', price: 4 },
				{ value: 'redbean', name: '红豆', desc: '香甜红豆', price: 3 },
				{ value: 'grass_jelly', name: '仙草', desc: '清香仙草', price: 3 },
				{ value: 'taro_ball', name: '芋圆', desc: '软糯芋圆', price: 4 }
			]
		}
	},
	onLoad(options) {
		this.productId = options.id
		this.loadUserInfo()
		this.loadProductDetail()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo) {
				this.userInfo = userInfo
			}
		},

		// 加载商品详情
		async loadProductDetail() {
			try {
				const res = await api.product.getDetail(this.productId)
				if (res.code === 200) {
					this.product = res.data
					this.productImages = res.data.images || [res.data.image]
				}
			} catch (error) {
				console.error('加载商品详情失败:', error)
				this.product = null
				uni.showToast({ title: error.message || '商品加载失败', icon: 'none' })
			}
		},

		// 计算总价格
		calculateTotalPrice() {
			let totalPrice = this.product.price || 0
			
			// 会员价格
			if (this.userInfo.memberLevel > 0 && this.product.memberPrice) {
				totalPrice = this.product.memberPrice
			}
			
			// 加料价格
			this.selectedOptions.toppings.forEach(toppingValue => {
				const topping = this.toppingOptions.find(t => t.value === toppingValue)
				if (topping) {
					totalPrice += topping.price
				}
			})
			
			return totalPrice * this.quantity
		},

		// 选择甜度
		selectSweetness(value) {
			this.selectedOptions.sweetness = value
		},

		// 选择温度
		selectTemperature(value) {
			this.selectedOptions.temperature = value
		},

		// 切换加料
		toggleTopping(value) {
			const index = this.selectedOptions.toppings.indexOf(value)
			if (index > -1) {
				this.selectedOptions.toppings.splice(index, 1)
			} else {
				this.selectedOptions.toppings.push(value)
			}
		},

		// 选择规格
		selectSize(value) {
			this.selectedOptions.size = value
		},

		// 切换详情标签
		switchTab(tab) {
			this.activeTab = tab
		},

		// 增加数量
		increaseQuantity() {
			this.quantity++
		},

		// 减少数量
		decreaseQuantity() {
			if (this.quantity > 1) {
				this.quantity--
			}
		},

		// 预览图片
		previewImage(current, urls = null) {
			uni.previewImage({
				current: current,
				urls: urls || this.productImages
			})
		},

		// 收藏商品
		async collectProduct() {
			try {
				if (this.product.isCollected) {
					await api.product.uncollect(this.productId)
					this.product.isCollected = false
					uni.showToast({
						title: '已取消收藏',
						icon: 'success'
					})
				} else {
					await api.product.collect(this.productId)
					this.product.isCollected = true
					uni.showToast({
						title: '收藏成功',
						icon: 'success'
					})
				}
			} catch (error) {
				uni.showToast({ title: error.message || '收藏操作失败', icon: 'none' })
			}
		},

		// 分享商品
		shareProduct() {
			uni.share({
				provider: 'weixin',
				scene: 'WXSceneSession',
				type: 0,
				href: '',
				title: this.product.name,
				summary: this.product.description,
				imageUrl: this.productImages[0],
				success: () => {
					uni.showToast({
						title: '分享成功',
						icon: 'success'
					})
				},
				fail: () => {
					uni.showShareMenu({
						withShareTicket: true
					})
				}
			})
		},

		// 加入购物车
		addToCart() {
			const cartItem = {
				...this.product,
				quantity: this.quantity,
				selectedOptions: { ...this.selectedOptions },
				totalPrice: this.calculateTotalPrice()
			}
			
			addToCart(cartItem)
			uni.showToast({
				title: '已加入购物车',
				icon: 'success'
			})
		},

		// 立即购买
		buyNow() {
			const cartItem = {
				...this.product,
				quantity: this.quantity,
				selectedOptions: { ...this.selectedOptions },
				totalPrice: this.calculateTotalPrice()
			}
			
			// 跳转到订单确认页面
			uni.navigateTo({
				url: `/pages/order/order?type=buy&data=${encodeURIComponent(JSON.stringify([cartItem]))}`
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
	padding-bottom: 120rpx;
}

/* 商品图片 */
.product-images {
	position: relative;
	height: 600rpx;
}

.image-swiper {
	height: 100%;
}

.product-image {
	width: 100%;
	height: 100%;
}

.image-indicator {
	position: absolute;
	bottom: 20rpx;
	right: 20rpx;
	background: rgba(0, 0, 0, 0.6);
	color: white;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
}

/* 商品信息 */
.product-info {
	background: white;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.product-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 20rpx;
}

.product-title {
	flex: 1;
}

.product-name {
	font-size: 36rpx;
	font-weight: 700;
	color: #333;
	line-height: 1.3;
	margin-bottom: 12rpx;
}

.product-tags {
	display: flex;
	gap: 12rpx;
	flex-wrap: wrap;
}

.product-tag {
	background: rgba(255, 107, 53, 0.1);
	color: #ff6b35;
	font-size: 20rpx;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	font-weight: 500;
}

.product-actions {
	display: flex;
	gap: 20rpx;
}

.action-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 50%;
	background: #f8f9fa;
	transition: all 0.3s ease;
}

.action-btn:active {
	transform: scale(0.9);
}

.action-icon {
	font-size: 32rpx;
}

.product-desc {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 24rpx;
}

.price-section {
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
}

.price-main {
	display: flex;
	align-items: baseline;
	gap: 12rpx;
}

.price-symbol {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.price-value {
	font-size: 48rpx;
	color: #ff6b35;
	font-weight: 700;
}

.price-original {
	font-size: 24rpx;
	color: #999;
	text-decoration: line-through;
}

.price-member {
	font-size: 20rpx;
	color: #8b4513;
	background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.price-stats {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 8rpx;
}

.product-sales,
.product-rating {
	font-size: 22rpx;
	color: #999;
}

/* 定制化选项 */
.customize-section {
	background: white;
	margin-bottom: 20rpx;
}

.customize-item {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.customize-item:last-child {
	border-bottom: none;
}

.customize-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.customize-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.customize-required {
	font-size: 20rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.customize-optional {
	font-size: 20rpx;
	color: #999;
	background: #f0f0f0;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.option-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.option-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	border: 2rpx solid #f0f0f0;
	border-radius: 16rpx;
	transition: all 0.3s ease;
}

.option-item.active {
	border-color: #ff6b35;
	background: rgba(255, 107, 53, 0.05);
}

.option-item.multiple {
	justify-content: space-between;
}

.option-info {
	flex: 1;
}

.option-name {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 4rpx;
}

.option-desc {
	font-size: 22rpx;
	color: #999;
}

.option-price {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
	margin-left: 20rpx;
}

.option-check {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #ddd;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-left: 20rpx;
	font-size: 24rpx;
	color: #ff6b35;
}

.option-item.active .option-check {
	border-color: #ff6b35;
	background: #ff6b35;
	color: white;
}

/* 商品详情 */
.detail-section {
	background: white;
	margin-bottom: 20rpx;
}

.detail-tabs {
	display: flex;
	border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
	flex: 1;
	padding: 30rpx 20rpx;
	text-align: center;
	font-size: 26rpx;
	color: #666;
	position: relative;
	transition: all 0.3s ease;
}

.tab-item.active {
	color: #ff6b35;
	font-weight: 600;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 60rpx;
	height: 4rpx;
	background: #ff6b35;
	border-radius: 2rpx;
}

.tab-content {
	padding: 30rpx;
}

.detail-text {
	font-size: 26rpx;
	color: #333;
	line-height: 1.6;
	margin-bottom: 30rpx;
}

.detail-images {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.detail-image {
	width: 100%;
	border-radius: 12rpx;
}

.nutrition-item {
	display: flex;
	justify-content: space-between;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f8f9fa;
}

.nutrition-item:last-child {
	border-bottom: none;
}

.nutrition-name {
	font-size: 26rpx;
	color: #333;
}

.nutrition-value {
	font-size: 26rpx;
	color: #666;
	font-weight: 600;
}

.nutrition-empty,
.reviews-empty {
	text-align: center;
	color: #999;
	font-size: 26rpx;
	padding: 60rpx 0;
}

.reviews-summary {
	display: flex;
	align-items: center;
	gap: 20rpx;
	margin-bottom: 30rpx;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
}

.reviews-rating {
	font-size: 32rpx;
	color: #ff6b35;
	font-weight: 700;
}

.reviews-count {
	font-size: 24rpx;
	color: #666;
}

.review-item {
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f8f9fa;
}

.review-item:last-child {
	border-bottom: none;
}

.review-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.review-avatar {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.review-info {
	flex: 1;
}

.review-name {
	font-size: 24rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 4rpx;
}

.review-rating {
	font-size: 20rpx;
}

.review-time {
	font-size: 22rpx;
	color: #999;
}

.review-content {
	font-size: 26rpx;
	color: #333;
	line-height: 1.5;
	margin-bottom: 16rpx;
}

.review-images {
	display: flex;
	gap: 12rpx;
	flex-wrap: wrap;
}

.review-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 8rpx;
}

/* 底部操作栏 */
.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx 30rpx;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.quantity-control {
	display: flex;
	align-items: center;
	border: 2rpx solid #f0f0f0;
	border-radius: 50rpx;
	overflow: hidden;
}

.quantity-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f8f9fa;
	font-size: 32rpx;
	font-weight: bold;
	color: #666;
	transition: all 0.3s ease;
}

.quantity-btn:active {
	background: #e0e0e0;
}

.quantity-value {
	width: 80rpx;
	text-align: center;
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.action-buttons {
	flex: 1;
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

.action-btn.secondary {
	background: transparent;
	color: #ff6b35;
	border: 2rpx solid #ff6b35;
}

.action-btn.primary {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.3);
}

.action-btn:active {
	transform: scale(0.95);
}

.price-current {
	font-size: 48rpx;
	color: #ff6b35;
	font-weight: 700;
	margin-right: 16rpx;
}

.price-original {
	font-size: 24rpx;
	color: #999;
	text-decoration: line-through;
}

.member-price {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.member-text {
	font-size: 24rpx;
	color: #52c41a;
	font-weight: 600;
}

.product-stats {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.rating-text {
	font-size: 24rpx;
	color: #666;
}

.sales-text {
	font-size: 24rpx;
	color: #999;
}

/* 定制选项 */
.customize-section {
	background: white;
	margin-bottom: 20rpx;
}

.option-icon {
	font-size: 32rpx;
	margin-right: 16rpx;
}

/* 商品详情 */
.detail-section {
	background: white;
	margin-bottom: 20rpx;
}

.tab-content {
	padding: 30rpx;
}

.detail-content {
	line-height: 1.6;
}

/* 营养成分样式 - 待实现 */
/* .nutrition-content {
	
} */

/* 评价内容样式 - 待实现 */
/* .review-content {
	
} */

.review-item {
	padding: 30rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
	border-bottom: none;
}

.review-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.review-info {
	flex: 1;
	margin-left: 16rpx;
}

.review-name {
	font-size: 26rpx;
	color: #333;
	font-weight: 600;
	display: block;
	margin-bottom: 8rpx;
}

.review-time {
	font-size: 22rpx;
	color: #999;
}

.review-text {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 16rpx;
	display: block;
}

.review-images {
	display: flex;
	gap: 12rpx;
}

.review-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
}

/* 底部操作栏 */
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

.bar-left {
	display: flex;
	gap: 40rpx;
}

.bar-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
}

.bar-text {
	font-size: 20rpx;
	color: #666;
	margin-top: 4rpx;
}

.bar-right {
	display: flex;
	align-items: center;
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
</style>
