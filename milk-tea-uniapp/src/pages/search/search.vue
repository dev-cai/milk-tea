<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input-wrapper">
				<text class="search-icon">🔍</text>
				<input 
					class="search-input" 
					type="text" 
					placeholder="搜索你想要的奶茶..." 
					v-model="searchKeyword"
					@input="onSearchInput"
					@confirm="onSearchConfirm"
					focus
				/>
				<text class="clear-btn" v-if="searchKeyword" @click="clearSearch">✕</text>
			</view>
			<text class="cancel-btn" @click="goBack">取消</text>
		</view>

		<!-- 搜索建议 -->
		<view class="search-suggestions" v-if="showSuggestions && suggestions.length > 0">
			<view class="suggestion-item" 
				v-for="suggestion in suggestions" 
				:key="suggestion.id"
				@click="selectSuggestion(suggestion.keyword)"
			>
				<text class="suggestion-icon">🔍</text>
				<text class="suggestion-text">{{ suggestion.keyword }}</text>
				<text class="suggestion-count">约{{ suggestion.count }}个结果</text>
			</view>
		</view>

		<!-- 搜索历史 -->
		<view class="search-history" v-if="!searchKeyword && searchHistory.length > 0">
			<view class="history-header">
				<text class="history-title">搜索历史</text>
				<text class="clear-history" @click="clearHistory">清空</text>
			</view>
			<view class="history-tags">
				<view class="history-tag" 
					v-for="(keyword, index) in searchHistory" 
					:key="index"
					@click="selectSuggestion(keyword)"
				>
					<text>{{ keyword }}</text>
				</view>
			</view>
		</view>

		<!-- 热门搜索 -->
		<view class="hot-search" v-if="!searchKeyword">
			<view class="hot-header">
				<text class="hot-title">🔥 热门搜索</text>
			</view>
			<view class="hot-tags">
				<view class="hot-tag" 
					v-for="(keyword, index) in hotKeywords" 
					:key="index"
					@click="selectSuggestion(keyword)"
				>
					<text>{{ keyword }}</text>
				</view>
			</view>
		</view>

		<!-- 搜索结果 -->
		<view class="search-results" v-if="searchKeyword && searchResults.length > 0">
			<view class="results-header">
				<text class="results-count">找到{{ searchResults.length }}个相关商品</text>
				<view class="sort-options">
					<text class="sort-item" 
						:class="{ active: sortType === 'default' }"
						@click="changeSortType('default')"
					>综合</text>
					<text class="sort-item" 
						:class="{ active: sortType === 'sales' }"
						@click="changeSortType('sales')"
					>销量</text>
					<text class="sort-item" 
						:class="{ active: sortType === 'price' }"
						@click="changeSortType('price')"
					>价格</text>
				</view>
			</view>
			
			<view class="product-list">
				<view class="product-item" 
					v-for="product in sortedResults" 
					:key="product.id"
					@click="goToProduct(product.id)"
				>
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name" v-html="highlightKeyword(product.name)"></text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-tags" v-if="product.tags">
							<text class="product-tag" v-for="tag in product.tags" :key="tag">{{ tag }}</text>
						</view>
						<view class="product-bottom">
							<view class="product-price">
								<text class="price-current">¥{{ formatPrice(product.price) }}</text>
								<text class="price-member" v-if="product.memberPrice < product.price">
									会员¥{{ formatPrice(product.memberPrice) }}
								</text>
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
		</view>

		<!-- 无搜索结果 -->
		<view class="no-results" v-if="searchKeyword && searchResults.length === 0 && !searching">
			<text class="no-results-icon">🔍</text>
			<text class="no-results-text">没有找到相关商品</text>
			<text class="no-results-desc">试试其他关键词吧</text>
		</view>

		<!-- 加载状态 -->
		<view class="loading" v-if="searching">
			<text class="loading-text">搜索中...</text>
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
			searchKeyword: '',
			searchHistory: [],
			hotKeywords: ['珍珠奶茶', '芝士奶盖', '水果茶', '咖啡', '布丁', '椰果', '红豆', '抹茶'],
			suggestions: [],
			searchResults: [],
			showSuggestions: false,
			searching: false,
			sortType: 'default', // default, sales, price
			searchTimer: null
		}
	},
	computed: {
		// 排序后的搜索结果
		sortedResults() {
			const results = [...this.searchResults]
			switch (this.sortType) {
				case 'sales':
					return results.sort((a, b) => b.sales - a.sales)
				case 'price':
					return results.sort((a, b) => a.price - b.price)
				default:
					return results
			}
		}
	},
	onLoad() {
		this.loadSearchHistory()
		this.loadHotKeywords()
	},
	methods: {
		// 加载搜索历史
		loadSearchHistory() {
			const history = uni.getStorageSync('searchHistory') || []
			this.searchHistory = history.slice(0, 10) // 最多显示10条
		},

		// 保存搜索历史
		saveSearchHistory(keyword) {
			if (!keyword.trim()) return
			
			let history = uni.getStorageSync('searchHistory') || []
			
			// 移除重复项
			history = history.filter(item => item !== keyword)
			
			// 添加到开头
			history.unshift(keyword)
			
			// 限制数量
			history = history.slice(0, 20)
			
			uni.setStorageSync('searchHistory', history)
			this.searchHistory = history.slice(0, 10)
		},

		// 加载热门关键词
		async loadHotKeywords() {
			try {
				// 这里应该从API获取热门搜索词
				// const res = await api.search.getHotKeywords()
				// this.hotKeywords = res.data || []
			} catch (error) {
				console.error('加载热门搜索失败:', error)
			}
		},

		// 搜索输入
		onSearchInput(e) {
			const keyword = e.detail.value
			this.searchKeyword = keyword
			
			// 清除之前的定时器
			if (this.searchTimer) {
				clearTimeout(this.searchTimer)
			}
			
			if (keyword.trim()) {
				this.showSuggestions = true
				// 防抖搜索建议
				this.searchTimer = setTimeout(() => {
					this.loadSearchSuggestions(keyword)
				}, 300)
			} else {
				this.showSuggestions = false
				this.suggestions = []
				this.searchResults = []
			}
		},

		// 搜索确认
		onSearchConfirm(e) {
			const keyword = e.detail.value.trim()
			if (keyword) {
				this.performSearch(keyword)
			}
		},

		// 加载搜索建议
		async loadSearchSuggestions(keyword) {
			try {
				// 这里应该调用API获取搜索建议
				// const res = await api.search.getSuggestions(keyword)
				// this.suggestions = res.data || []
				
				// 模拟搜索建议
				const mockSuggestions = [
					{ id: 1, keyword: keyword + '奶茶', count: 15 },
					{ id: 2, keyword: keyword + '咖啡', count: 8 },
					{ id: 3, keyword: keyword + '果茶', count: 12 }
				].filter(item => item.keyword !== keyword)
				
				this.suggestions = mockSuggestions
			} catch (error) {
				console.error('加载搜索建议失败:', error)
			}
		},

		// 执行搜索
		async performSearch(keyword) {
			this.searchKeyword = keyword
			this.showSuggestions = false
			this.searching = true
			
			// 保存搜索历史
			this.saveSearchHistory(keyword)
			
			try {
				const res = await api.product.search(keyword)
				if (res.code === 200) {
					this.searchResults = res.data.records || []
				}
			} catch (error) {
				console.error('搜索失败:', error)
				// 使用模拟数据
				this.searchResults = [
					{
						id: 1,
						name: '珍珠奶茶',
						description: '经典珍珠奶茶，香甜可口',
						image: '/static/product1.jpg',
						price: 18.00,
						memberPrice: 16.00,
						sales: 999,
						tags: ['热销', '经典']
					},
					{
						id: 2,
						name: '芝士奶盖珍珠奶茶',
						description: '浓郁芝士配珍珠，层次丰富',
						image: '/static/product2.jpg',
						price: 22.00,
						memberPrice: 20.00,
						sales: 888,
						tags: ['新品', '芝士']
					}
				].filter(item => item.name.includes(keyword))
			} finally {
				this.searching = false
			}
		},

		// 选择搜索建议
		selectSuggestion(keyword) {
			this.performSearch(keyword)
		},

		// 清空搜索
		clearSearch() {
			this.searchKeyword = ''
			this.showSuggestions = false
			this.searchResults = []
		},

		// 清空历史
		clearHistory() {
			uni.showModal({
				title: '确认清空',
				content: '确定要清空搜索历史吗？',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('searchHistory')
						this.searchHistory = []
					}
				}
			})
		},

		// 改变排序方式
		changeSortType(type) {
			this.sortType = type
		},

		// 高亮关键词
		highlightKeyword(text) {
			if (!this.searchKeyword) return text
			const regex = new RegExp(`(${this.searchKeyword})`, 'gi')
			return text.replace(regex, '<span style="color: #ff6b35; font-weight: 600;">$1</span>')
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

		// 返回
		goBack() {
			uni.navigateBack()
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

/* 搜索栏 */
.search-bar {
	display: flex;
	align-items: center;
	padding: 20rpx 30rpx;
	background: white;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.search-input-wrapper {
	flex: 1;
	display: flex;
	align-items: center;
	background: #f8f9fa;
	border-radius: 50rpx;
	padding: 20rpx 30rpx;
	margin-right: 20rpx;
}

.search-icon {
	font-size: 28rpx;
	color: #999;
	margin-right: 16rpx;
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	background: transparent;
}

.search-input::placeholder {
	color: #999;
}

.clear-btn {
	font-size: 24rpx;
	color: #999;
	padding: 8rpx;
	margin-left: 16rpx;
}

.cancel-btn {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
}

/* 搜索建议 */
.search-suggestions {
	background: white;
	border-top: 1rpx solid #f0f0f0;
}

.suggestion-item {
	display: flex;
	align-items: center;
	padding: 24rpx 30rpx;
	border-bottom: 1rpx solid #f8f9fa;
}

.suggestion-item:last-child {
	border-bottom: none;
}

.suggestion-icon {
	font-size: 24rpx;
	color: #999;
	margin-right: 20rpx;
}

.suggestion-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.suggestion-count {
	font-size: 22rpx;
	color: #999;
}

/* 搜索历史 */
.search-history {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.history-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.history-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.clear-history {
	font-size: 24rpx;
	color: #999;
}

.history-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.history-tag {
	padding: 16rpx 24rpx;
	background: #f8f9fa;
	border-radius: 20rpx;
	font-size: 24rpx;
	color: #666;
	transition: all 0.3s ease;
}

.history-tag:active {
	background: rgba(255, 107, 53, 0.1);
	color: #ff6b35;
}

/* 热门搜索 */
.hot-search {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.hot-header {
	margin-bottom: 30rpx;
}

.hot-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.hot-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.hot-tag {
	padding: 16rpx 24rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.1) 0%, rgba(247, 147, 30, 0.1) 100%);
	border-radius: 20rpx;
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 500;
	transition: all 0.3s ease;
}

.hot-tag:active {
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.2) 0%, rgba(247, 147, 30, 0.2) 100%);
}

/* 搜索结果 */
.search-results {
	padding: 20rpx;
}

.results-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.results-count {
	font-size: 24rpx;
	color: #666;
}

.sort-options {
	display: flex;
	gap: 30rpx;
}

.sort-item {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
	position: relative;
}

.sort-item.active {
	color: #ff6b35;
}

.sort-item.active::after {
	content: '';
	position: absolute;
	bottom: -8rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 20rpx;
	height: 4rpx;
	background: #ff6b35;
	border-radius: 2rpx;
}

.product-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.product-item {
	display: flex;
	background: white;
	border-radius: 20rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	transition: all 0.3s ease;
}

.product-item:active {
	transform: translateY(-2rpx);
	box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.1);
}

.product-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	margin-right: 24rpx;
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
	line-height: 1.3;
}

.product-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 12rpx;
	line-height: 1.4;
}

.product-tags {
	display: flex;
	gap: 12rpx;
	margin-bottom: 16rpx;
}

.product-tag {
	font-size: 18rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.product-bottom {
	margin-top: auto;
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
}

.product-price {
	display: flex;
	align-items: center;
	gap: 12rpx;
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
}

.product-stats {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.product-sales {
	font-size: 22rpx;
	color: #999;
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
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
}

.add-cart-btn:active {
	transform: scale(0.9);
}

/* 无结果状态 */
.no-results {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
	text-align: center;
}

.no-results-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.6;
}

.no-results-text {
	font-size: 32rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 16rpx;
}

.no-results-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.5;
}

/* 加载状态 */
.loading {
	padding: 80rpx;
	text-align: center;
}

.loading-text {
	font-size: 26rpx;
	color: #999;
}
</style>
