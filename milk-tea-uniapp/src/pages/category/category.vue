<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goToSearch">
				<text class="search-placeholder">搜索商品...</text>
				<text class="search-icon">🔍</text>
			</view>
		</view>

		<view class="content">
			<!-- 左侧分类列表 -->
			<scroll-view class="category-sidebar" scroll-y="true">
				<view 
					class="category-item" 
					:class="{ active: currentCategoryId === category.id }"
					v-for="category in categories" 
					:key="category.id"
					@click="selectCategory(category.id)"
				>
					<text class="category-icon">{{ category.icon }}</text>
					<text class="category-name">{{ category.name }}</text>
					<view class="category-badge" v-if="category.productCount > 0">{{ category.productCount }}</view>
				</view>
			</scroll-view>

			<!-- 右侧商品列表 -->
			<scroll-view class="product-content" scroll-y="true" @scrolltolower="loadMoreProducts">
				<!-- 分类信息 -->
				<view class="category-header" v-if="currentCategory">
					<view class="category-info">
						<text class="category-title">{{ currentCategory.name }}</text>
						<text class="category-desc">{{ currentCategory.description || '精选优质商品' }}</text>
					</view>
					<view class="category-stats">
						<text class="stats-text">共{{ products.length }}款商品</text>
					</view>
				</view>

				<!-- 筛选栏 -->
				<view class="filter-bar">
					<view class="filter-list">
						<view class="filter-item" :class="{ active: sortType === 'default' }" @tap="setSortType('default')">
							<text>综合</text>
						</view>
						<view class="filter-item" :class="{ active: sortType === 'sales' }" @tap="setSortType('sales')">
							<text>销量</text>
						</view>
						<view class="filter-item" :class="{ active: sortType === 'price' }" @tap="setSortType('price')">
							<text>价格</text>
							<text class="sort-arrow" v-if="sortType === 'price'">{{ priceOrder === 'asc' ? '↑' : '↓' }}</text>
						</view>
						<view class="filter-item" :class="{ active: sortType === 'rating' }" @tap="setSortType('rating')">
							<text>评分</text>
						</view>
					</view>
				</view>

				<!-- 商品列表 -->
				<view class="product-list">
					<view class="product-item" v-for="product in products" :key="product.id" @click="goToProduct(product.id)">
						<image class="product-image" :src="product.image" mode="aspectFill"></image>
						<view class="product-info">
							<text class="product-name">{{ product.name }}</text>
							<text class="product-desc">{{ product.description }}</text>
							<view class="product-tags" v-if="product.tags && product.tags.length > 0">
								<text class="tag" v-for="tag in product.tags" :key="tag">{{ tag }}</text>
							</view>
							<view class="product-rating" v-if="product.rating">
								<view class="stars">
									<text class="star" v-for="i in 5" :key="i" :class="{ filled: i <= product.rating }">★</text>
								</view>
								<text class="rating-text">({{ product.reviewCount || 0 }})</text>
							</view>
							<view class="product-bottom">
								<view class="price-section">
									<text class="price-current">¥{{ formatPrice(product.price) }}</text>
									<text class="price-member" v-if="product.memberPrice && product.memberPrice < product.price">
										会员¥{{ formatPrice(product.memberPrice) }}
									</text>
								</view>
								<view class="product-actions">
									<text class="sales-count">月销{{ product.sales || 0 }}</text>
									<view class="add-cart-btn" @click.stop="addToCart(product)">
										<text>+</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 加载更多 -->
				<view class="load-more" v-if="hasMore">
					<text v-if="loading">加载中...</text>
					<text v-else>上拉加载更多</text>
				</view>

				<!-- 没有更多数据 -->
				<view class="no-more" v-if="!hasMore && products.length > 0">
					<text>没有更多商品了</text>
				</view>

				<!-- 空状态 -->
				<view class="empty-state" v-if="!loading && products.length === 0">
					<text class="empty-icon">🛍️</text>
					<text class="empty-text">暂无商品</text>
					<text class="empty-desc">该分类下还没有商品哦</text>
				</view>
			</scroll-view>
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
			categories: [],
			currentCategoryId: null,
			currentCategory: null,
			products: [],
			sortType: 'default', // 'default', 'sales', 'price', 'rating'
			priceOrder: 'asc', // 'asc', 'desc'
			loading: false,
			hasMore: true,
			page: 1,
			pageSize: 20
		}
	},
	onLoad(options) {
		// 从参数获取分类ID
		if (options.categoryId) {
			this.currentCategoryId = parseInt(options.categoryId)
		}
		
		this.loadCategories()
	},
	onShow() {
		// 每次显示时刷新数据
		if (this.currentCategoryId) {
			this.loadProducts(true)
		}
	},
	methods: {
		// 加载分类列表
		async loadCategories() {
			try {
				const res = await api.category.getList()
				this.categories = res.data || []

				// 设置默认选中第一个分类
				if (!this.currentCategoryId && this.categories.length > 0) {
					this.currentCategoryId = this.categories[0].id
				}

				// 设置当前分类信息
				this.updateCurrentCategory()
				
				// 加载商品
				if (this.currentCategoryId) {
					this.loadProducts(true)
				}
			} catch (error) {
				console.error('加载分类失败:', error)
				this.categories = []
			}
		},

		// 更新当前分类信息
		updateCurrentCategory() {
			this.currentCategory = this.categories.find(cat => cat.id === this.currentCategoryId)
		},

		// 选择分类
		selectCategory(categoryId) {
			if (this.currentCategoryId === categoryId) return
			
			this.currentCategoryId = categoryId
			this.updateCurrentCategory()
			this.loadProducts(true)
		},

		// 加载商品列表
		async loadProducts(reset = false) {
			if (this.loading) return
			
			if (reset) {
				this.page = 1
				this.products = []
				this.hasMore = true
			}

			if (!this.hasMore) return

			this.loading = true

			try {
				const params = {
					categoryId: this.currentCategoryId,
					page: this.page,
					pageSize: this.pageSize,
					sortType: this.sortType,
					sortOrder: this.sortType === 'price' ? this.priceOrder : 'desc'
				}

				const res = await api.category.getProducts(this.currentCategoryId, params)
				
				const newProducts = res.data?.records || []
				
				if (reset) {
					this.products = newProducts
				} else {
					this.products.push(...newProducts)
				}

				this.hasMore = newProducts.length === this.pageSize
				if (this.hasMore) {
					this.page++
				}
			} catch (error) {
				console.error('加载商品失败:', error)
				
				if (reset) {
					this.products = []
					this.hasMore = false
				}
			} finally {
				this.loading = false
			}
		},

		// 设置排序方式
		setSortType(type) {
			console.log('点击排序按钮:', type)
			console.log('当前sortType:', this.sortType)
			
			if (type === 'price' && this.sortType === 'price') {
				// 切换价格排序顺序
				this.priceOrder = this.priceOrder === 'asc' ? 'desc' : 'asc'
				console.log('切换价格排序:', this.priceOrder)
			} else {
				this.sortType = type
				if (type === 'price') {
					this.priceOrder = 'asc'
				}
			}
			
			console.log('开始重新加载商品...')
			this.loadProducts(true)
		},

		// 加载更多商品
		loadMoreProducts() {
			if (!this.loading && this.hasMore) {
				this.loadProducts(false)
			}
		},

		// 跳转搜索页面
		goToSearch() {
			uni.navigateTo({
				url: '/pages/search/search'
			})
		},

		// 跳转商品详情
		goToProduct(productId) {
			uni.navigateTo({
				url: `/pages/product/product?id=${productId}`
			})
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
	height: 100vh;
	display: flex;
	flex-direction: column;
	background: #f8f9fa;
}

/* 搜索栏 */
.search-bar {
	padding: 20rpx 30rpx;
	background: #ff6b35;
}

.search-input {
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 50rpx;
	padding: 20rpx 32rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.search-placeholder {
	color: #666;
	font-size: 28rpx;
}

.search-icon {
	font-size: 28rpx;
	color: #ff6b35;
}

/* 内容区域 */
.content {
	flex: 1;
	display: flex;
	overflow: hidden;
}

/* 左侧分类 */
.category-sidebar {
	width: 200rpx;
	background: white;
	border-right: 1rpx solid #f0f0f0;
}

.category-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	border-bottom: 1rpx solid #f8f8f8;
	transition: all 0.3s ease;
	position: relative;
}

.category-item.active {
	background: #fff7f0;
	border-right: 6rpx solid #ff6b35;
}

.category-item:active {
	background: #f8f8f8;
}

.category-icon {
	font-size: 40rpx;
	margin-bottom: 8rpx;
}

.category-name {
	font-size: 24rpx;
	color: #333;
	text-align: center;
	font-weight: 500;
}

.category-item.active .category-name {
	color: #ff6b35;
	font-weight: 600;
}

.category-badge {
	position: absolute;
	top: 20rpx;
	right: 10rpx;
	background: #ff6b35;
	color: white;
	font-size: 18rpx;
	padding: 2rpx 8rpx;
	border-radius: 10rpx;
	min-width: 20rpx;
	text-align: center;
}

/* 右侧商品内容 */
.product-content {
	flex: 1;
	background: #f8f9fa;
}

/* 分类头部 */
.category-header {
	background: white;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.category-info {
	margin-bottom: 16rpx;
}

.category-title {
	font-size: 32rpx;
	font-weight: 700;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}

.category-desc {
	font-size: 24rpx;
	color: #666;
	display: block;
}

.category-stats {
	text-align: right;
}

.stats-text {
	font-size: 22rpx;
	color: #999;
}

/* 筛选栏 */
.filter-bar {
	background: white;
	padding: 20rpx 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	position: relative;
	z-index: 10;
}

.filter-list {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.filter-item {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 16rpx 24rpx;
	border-radius: 20rpx;
	background: #f8f8f8;
	font-size: 24rpx;
	color: #666;
	transition: all 0.3s ease;
	white-space: nowrap;
	flex-shrink: 0;
}

.filter-item:active {
	opacity: 0.7;
	transform: scale(0.98);
}

.filter-item.active {
	background: #ff6b35;
	color: white;
}

.sort-arrow {
	margin-left: 4rpx;
	font-size: 20rpx;
}

/* 商品列表 */
.product-list {
	padding: 20rpx 30rpx;
}

.product-item {
	display: flex;
	background: white;
	border-radius: 20rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	transition: all 0.3s ease;
}

.product-item:active {
	transform: translateY(-2rpx);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.product-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	margin-right: 24rpx;
	flex-shrink: 0;
}

.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.product-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
	line-height: 1.3;
}

.product-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 12rpx;
	display: block;
	line-height: 1.4;
}

.product-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 8rpx;
	margin-bottom: 12rpx;
}

.tag {
	font-size: 18rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.product-rating {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.stars {
	display: flex;
	margin-right: 8rpx;
}

.star {
	font-size: 20rpx;
	color: #ddd;
}

.star.filled {
	color: #ffa500;
}

.rating-text {
	font-size: 20rpx;
	color: #999;
}

.product-bottom {
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
	margin-top: auto;
}

.price-section {
	display: flex;
	flex-direction: column;
	gap: 4rpx;
}

.price-current {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
}

.price-member {
	font-size: 20rpx;
	color: #8b4513;
	background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
	font-weight: 500;
}

.product-actions {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 8rpx;
}

.sales-count {
	font-size: 20rpx;
	color: #999;
}

.add-cart-btn {
	width: 56rpx;
	height: 56rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 28rpx;
	font-weight: bold;
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
}

.add-cart-btn:active {
	transform: scale(0.9);
}

/* 加载状态 */
.load-more,
.no-more {
	text-align: center;
	padding: 40rpx;
	color: #999;
	font-size: 24rpx;
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
	opacity: 0.5;
}

.empty-text {
	font-size: 32rpx;
	color: #666;
	margin-bottom: 16rpx;
	font-weight: 600;
}

.empty-desc {
	font-size: 24rpx;
	color: #999;
}
</style>
