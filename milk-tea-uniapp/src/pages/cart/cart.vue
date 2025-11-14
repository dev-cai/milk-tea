<template>
	<view class="container">
		<!-- 购物车列表 -->
		<scroll-view class="cart-list" scroll-y="true" v-if="cartItems.length > 0">
			<view class="cart-item" v-for="item in cartItems" :key="item.cartId">
				<!-- 选择框 -->
				<view class="item-checkbox" @click="toggleItemSelected(item.cartId)">
					<text class="checkbox" :class="{ checked: item.selected }">{{ item.selected ? '✓' : '' }}</text>
				</view>

				<!-- 商品信息 -->
				<view class="item-content" @click="goToProduct(item.id)">
					<image class="item-image" :src="item.image" mode="aspectFill"></image>
					<view class="item-info">
						<text class="item-name">{{ item.name }}</text>
						<text class="item-desc">{{ item.description }}</text>
						
						<!-- 定制选项 -->
						<view class="item-options">
							<text class="option-text">{{ getSweetnessText(item.sweetness) }}</text>
							<text class="option-text">{{ getTemperatureText(item.temperature) }}</text>
							<text class="option-text" v-if="item.toppings && item.toppings.length > 0">
								加料: {{ item.toppings.join(', ') }}
							</text>
						</view>

						<!-- 备注 -->
						<text class="item-remark" v-if="item.remark">备注: {{ item.remark }}</text>

						<!-- 价格和数量 -->
						<view class="item-bottom">
							<view class="price-section">
								<text class="item-price">¥{{ formatPrice(getCurrentPrice(item)) }}</text>
								<text class="original-price" v-if="isMember && item.memberPrice < item.price">
									¥{{ formatPrice(item.price) }}
								</text>
							</view>
							<view class="quantity-control">
								<view class="quantity-btn" @click.stop="updateQuantity(item.cartId, item.quantity - 1)">
									<text>-</text>
								</view>
								<text class="quantity-text">{{ item.quantity }}</text>
								<view class="quantity-btn" @click.stop="updateQuantity(item.cartId, item.quantity + 1)">
									<text>+</text>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 删除按钮 -->
				<view class="item-delete" @click="removeItem(item.cartId)">
					<text class="delete-icon">🗑️</text>
				</view>
			</view>
		</scroll-view>

		<!-- 空购物车 -->
		<view class="empty-cart" v-else>
			<text class="empty-icon">🛒</text>
			<text class="empty-text">购物车空空如也</text>
			<text class="empty-desc">快去选购心仪的商品吧</text>
			<view class="empty-btn" @click="goShopping">
				<text>去逛逛</text>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="cart-footer" v-if="cartItems.length > 0">
			<view class="footer-left">
				<view class="select-all" @click="toggleAllSelected">
					<text class="checkbox" :class="{ checked: isAllSelected }">{{ isAllSelected ? '✓' : '' }}</text>
					<text class="select-text">全选</text>
				</view>
				<view class="total-info">
					<text class="total-text">
						合计: <text class="total-price">¥{{ formatPrice(selectedTotalPrice) }}</text>
					</text>
					<text class="member-tip" v-if="isMember && memberSavings > 0">
						会员已省¥{{ formatPrice(memberSavings) }}
					</text>
				</view>
			</view>
			<view class="footer-right">
				<view class="checkout-btn" :class="{ disabled: selectedCount === 0 }" @click="checkout">
					<text>结算({{ selectedCount }})</text>
				</view>
			</view>
		</view>

		<!-- 推荐商品 -->
		<view class="recommend-section" v-if="cartItems.length > 0 && recommendProducts.length > 0">
			<view class="section-header">
				<text class="section-title">🔥 为你推荐</text>
			</view>
			<scroll-view class="recommend-scroll" scroll-x="true" show-scrollbar="false">
				<view class="recommend-item" v-for="product in recommendProducts" :key="product.id" @click="goToProduct(product.id)">
					<image class="recommend-image" :src="product.image" mode="aspectFill"></image>
					<view class="recommend-info">
						<text class="recommend-name">{{ product.name }}</text>
						<text class="recommend-price">¥{{ formatPrice(product.price) }}</text>
					</view>
					<view class="recommend-add" @click.stop="addToCart(product)">
						<text>+</text>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
import { 
	getCart, 
	updateCartItemQuantity, 
	removeFromCart, 
	toggleCartItemSelected, 
	toggleAllSelected as toggleAllCartSelected,
	getSelectedTotalCount,
	getSelectedTotalPrice,
	isAllSelected as checkAllSelected,
	clearCart
} from '@/utils/cart.js'
import { formatPrice, getSweetnessText, getTemperatureText } from '@/utils/common.js'
import { addToCart } from '@/utils/cart.js'
import api from '@/utils/api.js'

export default {
	data() {
		return {
			cartItems: [],
			recommendProducts: [],
			userInfo: {}
		}
	},
	computed: {
		// 是否为会员
		isMember() {
			return this.userInfo.memberLevel > 0
		},

		// 选中商品数量
		selectedCount() {
			return getSelectedTotalCount()
		},

		// 选中商品总价
		selectedTotalPrice() {
			return getSelectedTotalPrice(this.isMember)
		},

		// 会员节省金额
		memberSavings() {
			if (!this.isMember) return 0
			const normalPrice = getSelectedTotalPrice(false)
			const memberPrice = getSelectedTotalPrice(true)
			return normalPrice - memberPrice
		},

		// 是否全选
		isAllSelected() {
			return checkAllSelected()
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadCartItems()
		this.loadRecommendProducts()
	},
	onShow() {
		// 每次显示时刷新购物车
		this.loadCartItems()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			this.userInfo = userInfo || {}
		},

		// 加载购物车商品
		loadCartItems() {
			this.cartItems = getCart()
		},

		// 加载推荐商品
		async loadRecommendProducts() {
			try {
				const res = await api.product.getRecommend(5)
				if (res.code === 200) {
					this.recommendProducts = res.data || []
				} else {
					// 使用默认数据
					this.recommendProducts = [
						{
							id: 101,
							name: '柠檬蜂蜜茶',
							image: '/static/recommend1.jpg',
							price: 15.00
						},
						{
							id: 102,
							name: '红豆奶茶',
							image: '/static/recommend2.jpg',
							price: 16.00
						}
					]
				}
			} catch (error) {
				console.error('加载推荐商品失败:', error)
			}
		},

		// 切换商品选中状态
		toggleItemSelected(cartId) {
			toggleCartItemSelected(cartId)
			this.loadCartItems()
		},

		// 全选/取消全选
		toggleAllSelected() {
			const newState = !this.isAllSelected
			toggleAllCartSelected(newState)
			this.loadCartItems()
		},

		// 更新商品数量
		updateQuantity(cartId, newQuantity) {
			if (newQuantity < 1) {
				this.removeItem(cartId)
				return
			}

			updateCartItemQuantity(cartId, newQuantity)
			this.loadCartItems()
		},

		// 删除商品
		removeItem(cartId) {
			uni.showModal({
				title: '提示',
				content: '确定要删除这个商品吗？',
				success: (res) => {
					if (res.confirm) {
						removeFromCart(cartId)
						this.loadCartItems()
					}
				}
			})
		},

		// 获取当前价格（考虑会员价）
		getCurrentPrice(item) {
			return this.isMember && item.memberPrice ? item.memberPrice : item.price
		},

		// 结算
		checkout() {
			if (this.selectedCount === 0) {
				uni.showToast({
					title: '请选择要结算的商品',
					icon: 'none'
				})
				return
			}

			// 检查登录状态
			const token = uni.getStorageSync('token')
			if (!token) {
				uni.showModal({
					title: '提示',
					content: '请先登录后再结算',
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

			// 跳转到订单确认页面
			uni.navigateTo({
				url: '/pages/order/order'
			})
		},

		// 跳转商品详情
		goToProduct(productId) {
			uni.navigateTo({
				url: `/pages/product/product?id=${productId}`
			})
		},

		// 去购物
		goShopping() {
			uni.switchTab({
				url: '/pages/index/index'
			})
		},

		// 添加推荐商品到购物车
		addToCart(product) {
			addToCart(product)
			this.loadCartItems()
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
	height: 100vh;
	display: flex;
	flex-direction: column;
	background: #f8f9fa;
}

/* 购物车列表 */
.cart-list {
	flex: 1;
	padding: 20rpx;
}

.cart-item {
	display: flex;
	align-items: flex-start;
	background: white;
	border-radius: 20rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.item-checkbox {
	margin-right: 20rpx;
	padding-top: 10rpx;
}

.checkbox {
	width: 36rpx;
	height: 36rpx;
	border: 2rpx solid #ddd;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	color: white;
	font-weight: bold;
	transition: all 0.3s ease;
}

.checkbox.checked {
	background: #ff6b35;
	border-color: #ff6b35;
}

.item-content {
	flex: 1;
	display: flex;
}

.item-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.item-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.item-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
	line-height: 1.3;
}

.item-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 12rpx;
	display: block;
	line-height: 1.4;
}

.item-options {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
	margin-bottom: 8rpx;
}

.option-text {
	font-size: 20rpx;
	color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.item-remark {
	font-size: 20rpx;
	color: #999;
	margin-bottom: 16rpx;
	display: block;
}

.item-bottom {
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
	margin-top: auto;
}

.price-section {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.item-price {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
}

.original-price {
	font-size: 22rpx;
	color: #999;
	text-decoration: line-through;
}

.quantity-control {
	display: flex;
	align-items: center;
	background: #f8f8f8;
	border-radius: 20rpx;
	overflow: hidden;
}

.quantity-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f0f0f0;
	color: #666;
	font-size: 32rpx;
	font-weight: bold;
	transition: all 0.3s ease;
}

.quantity-btn:active {
	background: #e0e0e0;
}

.quantity-text {
	min-width: 80rpx;
	text-align: center;
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.item-delete {
	margin-left: 20rpx;
	padding: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.delete-icon {
	font-size: 32rpx;
	color: #ff4d4f;
}

/* 空购物车 */
.empty-cart {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
	text-align: center;
}

.empty-icon {
	font-size: 160rpx;
	margin-bottom: 40rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 36rpx;
	color: #666;
	margin-bottom: 16rpx;
	font-weight: 600;
}

.empty-desc {
	font-size: 26rpx;
	color: #999;
	margin-bottom: 60rpx;
}

.empty-btn {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	padding: 24rpx 60rpx;
	border-radius: 50rpx;
	font-size: 28rpx;
	font-weight: 600;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
}

.empty-btn:active {
	transform: scale(0.98);
}

/* 底部结算栏 */
.cart-footer {
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: white;
	padding: 24rpx 30rpx;
	border-top: 1rpx solid #f0f0f0;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.footer-left {
	display: flex;
	align-items: center;
	flex: 1;
}

.select-all {
	display: flex;
	align-items: center;
	margin-right: 30rpx;
}

.select-text {
	font-size: 26rpx;
	color: #333;
	margin-left: 12rpx;
}

.total-info {
	display: flex;
	flex-direction: column;
	gap: 4rpx;
}

.total-text {
	font-size: 26rpx;
	color: #333;
}

.total-price {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
}

.member-tip {
	font-size: 20rpx;
	color: #52c41a;
}

.footer-right {
	margin-left: 20rpx;
}

.checkout-btn {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	padding: 24rpx 40rpx;
	border-radius: 50rpx;
	font-size: 28rpx;
	font-weight: 600;
	box-shadow: 0 6rpx 20rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
	min-width: 160rpx;
	text-align: center;
}

.checkout-btn.disabled {
	background: #ccc;
	box-shadow: none;
}

.checkout-btn:active:not(.disabled) {
	transform: scale(0.98);
}

/* 推荐商品 */
.recommend-section {
	background: white;
	padding: 30rpx;
	border-top: 1rpx solid #f0f0f0;
}

.section-header {
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 700;
	color: #333;
}

.recommend-scroll {
	white-space: nowrap;
}

.recommend-item {
	display: inline-block;
	width: 200rpx;
	margin-right: 20rpx;
	background: #f8f8f8;
	border-radius: 16rpx;
	overflow: hidden;
	vertical-align: top;
	position: relative;
}

.recommend-image {
	width: 100%;
	height: 120rpx;
}

.recommend-info {
	padding: 16rpx;
}

.recommend-name {
	font-size: 22rpx;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
	white-space: normal;
	line-height: 1.3;
}

.recommend-price {
	font-size: 24rpx;
	font-weight: 600;
	color: #ff6b35;
}

.recommend-add {
	position: absolute;
	bottom: 16rpx;
	right: 16rpx;
	width: 40rpx;
	height: 40rpx;
	background: #ff6b35;
	color: white;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24rpx;
	font-weight: bold;
	box-shadow: 0 2rpx 8rpx rgba(255, 107, 53, 0.3);
}

.recommend-add:active {
	transform: scale(0.9);
}
</style>
