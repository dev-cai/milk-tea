<template>
	<view class="container">
		<!-- 顶部标签切换 -->
		<u-tabs 
			:list="tabList" 
			@change="onTabChange" 
			:current="currentTab"
			:bar-width="60"
			active-color="#ff6b35"
			inactive-color="#666"
		></u-tabs>

		<!-- 优惠券列表 -->
		<scroll-view class="coupon-list" scroll-y="true">
			<!-- 可领取优惠券 -->
			<view v-if="currentTab === 0">
				<view 
					class="coupon-item" 
					:class="{ 'received': coupon.isReceived, 'available': !coupon.isReceived }"
					v-for="coupon in availableCoupons" 
					:key="coupon.id"
				>
					<view class="coupon-left" :class="{ 'gray': coupon.isReceived }">
						<view class="coupon-amount">
							<text class="amount-symbol" v-if="coupon.type === 1">¥</text>
							<text class="amount-value">{{ getCouponValue(coupon) }}</text>
						</view>
						<text class="coupon-condition">{{ getCouponCondition(coupon) }}</text>
					</view>
					
					<view class="coupon-center">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-desc">{{ getCouponDescription(coupon) }}</text>
						<view class="coupon-time">
							<u-icon name="clock" size="12" :color="coupon.isReceived ? '#ccc' : '#999'"></u-icon>
							<text class="time-text" :class="{ 'gray-text': coupon.isReceived }">{{ formatCouponTime(coupon) }}</text>
						</view>
					</view>
					
					<view class="coupon-right">
						<!-- 未领取：显示领取按钮 -->
						<u-button 
							v-if="!coupon.isReceived"
							type="warning" 
							size="mini"
							shape="circle"
							:loading="coupon.receiving"
							@click="receiveCoupon(coupon)"
						>
							{{ coupon.receiving ? '领取中' : '立即领取' }}
						</u-button>
						
						<!-- 已领取：显示"已领取"文字 -->
						<view v-else class="status-badge received-badge">
							<text>已领取</text>
						</view>
					</view>
				</view>
				
				<!-- 空状态 -->
				<view v-if="!loading && availableCoupons.length === 0" class="empty-state">
					<u-icon name="coupon" size="100" color="#ddd"></u-icon>
					<text class="empty-text">暂无可领取的优惠券</text>
					<text class="empty-tip">敬请期待更多优惠</text>
				</view>
			</view>

			<!-- 我的优惠券 -->
			<view v-if="currentTab === 1">
				<view 
					class="coupon-item my-coupon" 
					:class="{ 'expired': coupon.status === 2, 'used': coupon.status === 1 }"
					v-for="coupon in myCoupons" 
					:key="coupon.id"
					@click="onCouponClick(coupon)"
				>
					<view class="coupon-left" :class="{ 'gray': coupon.status !== 0 }">
						<view class="coupon-amount">
							<text class="amount-symbol" v-if="coupon.type === 1">¥</text>
							<text class="amount-value">{{ getCouponValue(coupon) }}</text>
						</view>
						<text class="coupon-condition">{{ getCouponCondition(coupon) }}</text>
					</view>
					
					<view class="coupon-center">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-desc">{{ getCouponDescription(coupon) }}</text>
						<view class="coupon-time">
							<u-icon name="clock" size="12" :color="coupon.status !== 0 ? '#ccc' : '#999'"></u-icon>
							<text class="time-text" :class="{ 'gray-text': coupon.status !== 0 }">{{ formatCouponTime(coupon) }}</text>
						</view>
					</view>
					
					<view class="coupon-right">
						<!-- 可使用状态 -->
						<u-button 
							v-if="coupon.status === 0"
							type="warning" 
							size="mini"
							shape="circle"
							@click.stop="useCoupon(coupon)"
						>
							去使用
						</u-button>
						
						<!-- 已使用状态 -->
						<view v-else-if="coupon.status === 1" class="status-badge used-badge">
							<text>已使用</text>
						</view>
						
						<!-- 已过期状态 -->
						<view v-else class="status-badge expired-badge">
							<text>已过期</text>
						</view>
					</view>
				</view>
				
				<!-- 空状态 -->
				<view v-if="!loading && myCoupons.length === 0" class="empty-state">
					<u-icon name="coupon" size="100" color="#ddd"></u-icon>
					<text class="empty-text">暂无优惠券</text>
					<text class="empty-tip">快去领取优惠券吧</text>
				</view>
			</view>
		</scroll-view>

		<!-- 优惠券使用说明 -->
		<view class="help-section">
			<view class="help-btn" @click="showHelpPopup = true">
				<u-icon name="question-circle" size="18" color="#ff6b35"></u-icon>
				<text class="help-text">使用说明</text>
			</view>
		</view>

		<!-- 使用说明弹窗 -->
		<u-popup v-model="showHelpPopup" mode="bottom" border-radius="20">
			<view class="popup-content">
				<view class="popup-header">
					<text class="popup-title">优惠券使用说明</text>
					<u-icon name="close" @click="showHelpPopup = false"></u-icon>
				</view>
				<view class="help-list">
					<view class="help-item">
						<text class="help-title">1. 如何领取优惠券？</text>
						<text class="help-text">在"可领取"页面点击"立即领取"按钮即可获得优惠券。</text>
					</view>
					<view class="help-item">
						<text class="help-title">2. 如何使用优惠券？</text>
						<text class="help-text">在结算页面选择可用的优惠券，系统会自动计算优惠金额。</text>
					</view>
					<view class="help-item">
						<text class="help-title">3. 优惠券使用条件</text>
						<text class="help-text">每张优惠券都有使用条件和有效期，请在有效期内使用。</text>
					</view>
					<view class="help-item">
						<text class="help-title">4. 优惠券叠加规则</text>
						<text class="help-text">每笔订单只能使用一张优惠券，不可叠加使用。</text>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatTime } from '@/utils/common.js'

export default {
	data() {
		return {
			currentTab: 0,
			tabList: [
				{ name: '可领取' },
				{ name: '我的优惠券' }
			],
			availableCoupons: [],
			myCoupons: [],
			loading: false,
			showHelpPopup: false,
			userInfo: {},
			selectMode: false
		}
	},
	onLoad(options) {
		this.selectMode = options && options.select === 'true'
		this.loadUserInfo()
		this.loadCoupons()
	},
	onShow() {
		// 每次显示时刷新数据
		this.loadCoupons()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			this.userInfo = userInfo || {}
		},

		// 标签切换
		onTabChange(index) {
			console.log('标签切换 - 原始index:', index, '类型:', typeof index)
			// u-tabs组件传递的是对象，需要提取index属性
			if (typeof index === 'object' && index.index !== undefined) {
				this.currentTab = index.index
			} else if (typeof index === 'number') {
				this.currentTab = index
			} else {
				this.currentTab = parseInt(index) || 0
			}
			console.log('标签切换后 - currentTab:', this.currentTab)
			this.loadCoupons()
		},

		// 加载优惠券数据
		async loadCoupons() {
			if (this.loading) return
			this.loading = true

			console.log('loadCoupons - currentTab:', this.currentTab)

			try {
				if (this.currentTab === 0) {
					console.log('加载可领取优惠券')
					// 加载可领取优惠券
					await this.loadAvailableCoupons()
				} else {
					console.log('加载我的优惠券')
					// 加载我的优惠券
					await this.loadMyCoupons()
				}
			} catch (error) {
				console.error('加载优惠券失败:', error)
				uni.showToast({
					title: error.message || '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},

		// 加载可领取优惠券
		async loadAvailableCoupons() {
			const res = await api.coupon.getAvailable(this.userInfo.id)
			
			if (res.code === 200) {
				const coupons = res.data || []
				
				// 处理优惠券数据，标记已领取状态
				this.availableCoupons = coupons.map(coupon => ({
					...coupon,
					isReceived: coupon.isReceived || false,
					receiving: false
				}))
			} else {
				throw new Error(res.message || '加载失败')
			}
		},

		// 加载我的优惠券
		async loadMyCoupons() {
			if (!this.userInfo.id) {
				this.myCoupons = []
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				return
			}

			const res = await api.coupon.getMy(this.userInfo.id)
			
			if (res.code === 200) {
				this.myCoupons = res.data || []
				// 调试：打印优惠券数据
				console.log('我的优惠券数据:', this.myCoupons)
				this.myCoupons.forEach(coupon => {
					console.log(`优惠券 ${coupon.name} - status: ${coupon.status}, type: ${typeof coupon.status}`)
				})
			} else {
				throw new Error(res.message || '加载失败')
			}
		},

		// 领取优惠券
		async receiveCoupon(coupon) {
			// 检查是否已领取
			if (coupon.isReceived) {
				uni.showToast({
					title: '您已领取过该优惠券',
					icon: 'none'
				})
				return
			}

			// 检查登录状态
			if (!this.userInfo.id) {
				uni.showModal({
					title: '提示',
					content: '请先登录后再领取优惠券',
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

			try {
				// 设置领取中状态
				this.$set(coupon, 'receiving', true)
				
				const res = await api.coupon.receive(coupon.id, this.userInfo.id)
				
				if (res.code === 200) {
					uni.showToast({
						title: '领取成功',
						icon: 'success'
					})
					
					// 标记为已领取，变灰并显示"去使用"按钮
					this.$set(coupon, 'isReceived', true)
					this.$set(coupon, 'receiving', false)
				} else {
					throw new Error(res.message || '领取失败')
				}
			} catch (error) {
				console.error('领取优惠券失败:', error)
				this.$set(coupon, 'receiving', false)
				uni.showToast({
					title: error.message || '领取失败',
					icon: 'none'
				})
			}
		},

		// 使用优惠券（跳转到商品列表）
		useCoupon(coupon) {
			if (this.selectMode) {
				uni.setStorageSync('selectedCoupon', coupon)
				uni.navigateBack()
				return
			}
			uni.switchTab({
				url: '/pages/index/index'
			})
		},

		// 点击优惠券卡片
		onCouponClick(coupon) {
			// 可以在这里添加查看详情的逻辑
			console.log('点击优惠券:', coupon)
		},

		// 获取优惠券面值
		getCouponValue(coupon) {
			if (!coupon || !coupon.discount) {
				return '0'
			}
			
			if (coupon.type === 1) {
				// 满减券 - 显示金额
				return parseFloat(coupon.discount).toFixed(0)
			} else if (coupon.type === 2) {
				// 折扣券 - 显示折扣
				const discountValue = parseFloat(coupon.discount)
				if (discountValue < 1) {
					// 如果是0.8这样的小数，转换为8折
					return `${(discountValue * 10).toFixed(1)}折`
				}
				// 如果已经是折扣数字（如8），直接显示
				return `${discountValue}折`
			}
			return coupon.discount
		},

		// 获取优惠券使用条件
		getCouponCondition(coupon) {
			if (!coupon) {
				return '查看使用条件'
			}
			
			const minAmount = parseFloat(coupon.minAmount || coupon.min_amount || 0)
			
			if (coupon.type === 1) {
				// 满减券
				return minAmount > 0 ? `满${minAmount.toFixed(0)}元可用` : '无门槛使用'
			} else if (coupon.type === 2) {
				// 折扣券
				return minAmount > 0 ? `满${minAmount.toFixed(0)}元可用` : '无门槛使用'
			}
			return '查看使用条件'
		},

		// 获取优惠券描述
		getCouponDescription(coupon) {
			if (!coupon) {
				return '优惠券'
			}
			
			const minAmount = parseFloat(coupon.minAmount || coupon.min_amount || 0)
			const discount = parseFloat(coupon.discount || 0)
			
			if (coupon.type === 1) {
				// 满减券
				if (minAmount > 0) {
					return `满${minAmount.toFixed(0)}元减${discount.toFixed(0)}元`
				} else {
					return `立减${discount.toFixed(0)}元，无门槛使用`
				}
			} else if (coupon.type === 2) {
				// 折扣券
				const discountValue = parseFloat(discount)
				const discountPercent = discountValue < 1 ? (discountValue * 10).toFixed(1) : discountValue
				
				if (minAmount > 0) {
					return `满${minAmount.toFixed(0)}元享${discountPercent}折优惠`
				} else {
					return `全场${discountPercent}折，无门槛使用`
				}
			}
			
			return '优惠券'
		},

		// 格式化优惠券时间
		formatCouponTime(coupon) {
			if (!coupon) {
				return '有效期未知'
			}
			
			// 兼容不同的字段名
			const startTime = coupon.startTime || coupon.validStart || coupon.valid_start
			const endTime = coupon.endTime || coupon.validEnd || coupon.valid_end
			
			if (!startTime || !endTime) {
				return '有效期未知'
			}
			
			try {
				const start = formatTime(startTime, 'YYYY.MM.DD')
				const end = formatTime(endTime, 'YYYY.MM.DD')
				return `${start} - ${end}`
			} catch (error) {
				console.error('格式化时间失败:', error)
				return '有效期未知'
			}
		},

		// 工具方法
		formatTime
	}
}
</script>

<style lang="scss" scoped>
.container {
	background: #f8f9fa;
	min-height: 100vh;
}

/* 优惠券列表 */
.coupon-list {
	padding: 20rpx;
	height: calc(100vh - 88rpx - 100rpx);
}

/* 优惠券卡片 */
.coupon-item {
	display: flex;
	background: white;
	border-radius: 20rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	position: relative;
	transition: all 0.3s ease;
}

.coupon-item.available {
	border-left: 8rpx solid #ff6b35;
}

.coupon-item.received {
	border-left: 8rpx solid #ccc;
	opacity: 0.7;
}

.coupon-item.my-coupon {
	border-left: 8rpx solid #52c41a;
}

.coupon-item.used {
	opacity: 0.5;
	border-left-color: #999;
}

.coupon-item.expired {
	opacity: 0.4;
	border-left-color: #ff4d4f;
}

.coupon-left {
	width: 200rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	position: relative;
}

.coupon-left.gray {
	background: linear-gradient(135deg, #bbb 0%, #999 100%);
}

.coupon-left::after {
	content: '';
	position: absolute;
	right: -10rpx;
	top: 50%;
	transform: translateY(-50%);
	width: 20rpx;
	height: 20rpx;
	background: #f8f9fa;
	border-radius: 50%;
}

.coupon-amount {
	display: flex;
	align-items: baseline;
	margin-bottom: 8rpx;
}

.amount-symbol {
	font-size: 24rpx;
	font-weight: 600;
}

.amount-value {
	font-size: 48rpx;
	font-weight: 700;
	line-height: 1;
}

.coupon-condition {
	font-size: 20rpx;
	opacity: 0.9;
	text-align: center;
}

.coupon-center {
	flex: 1;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.coupon-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.coupon-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 12rpx;
	display: block;
	line-height: 1.4;
}

.coupon-time {
	display: flex;
	align-items: center;
}

.time-text {
	font-size: 20rpx;
	color: #999;
	margin-left: 8rpx;
}

.time-text.gray-text {
	color: #ccc;
}

.coupon-right {
	width: 160rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 20rpx;
}

.status-badge {
	padding: 8rpx 20rpx;
	border-radius: 30rpx;
	font-size: 22rpx;
	text-align: center;
}

.received-badge {
	background: #f0f0f0;
	color: #999;
}

.used-badge {
	background: #f0f0f0;
	color: #999;
}

.expired-badge {
	background: #fff1f0;
	color: #ff4d4f;
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
	margin-top: 30rpx;
	display: block;
}

.empty-tip {
	font-size: 24rpx;
	color: #ccc;
	margin-top: 10rpx;
	display: block;
}

/* 帮助说明 */
.help-section {
	position: fixed;
	bottom: 20rpx;
	right: 20rpx;
	z-index: 100;
}

.help-btn {
	display: flex;
	align-items: center;
	background: white;
	padding: 16rpx 24rpx;
	border-radius: 50rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.help-text {
	font-size: 24rpx;
	color: #333;
	margin-left: 8rpx;
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

.help-list {
	padding-bottom: 20rpx;
}

.help-item {
	margin-bottom: 30rpx;
}

.help-title {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 12rpx;
	display: block;
}

.help-text {
	font-size: 24rpx;
	color: #666;
	line-height: 1.5;
	display: block;
}
</style>
