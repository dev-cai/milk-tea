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
		<scroll-view class="coupon-list" scroll-y="true" @scrolltolower="loadMoreCoupons">
			<!-- 可领取优惠券 -->
			<view v-if="currentTab === 0">
				<view class="coupon-item available" v-for="coupon in availableCoupons" :key="coupon.id">
					<view class="coupon-left">
						<view class="coupon-amount">
							<text class="amount-symbol">¥</text>
							<text class="amount-value">{{ getCouponValue(coupon) }}</text>
						</view>
						<text class="coupon-condition">{{ getCouponCondition(coupon) }}</text>
					</view>
					
					<view class="coupon-center">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-desc">{{ coupon.description }}</text>
						<view class="coupon-time">
							<u-icon name="clock" size="12" color="#999"></u-icon>
							<text class="time-text">{{ formatCouponTime(coupon) }}</text>
						</view>
						<view class="coupon-tags" v-if="coupon.tags && coupon.tags.length > 0">
							<u-tag 
								v-for="tag in coupon.tags" 
								:key="tag"
								:text="tag" 
								size="mini" 
								type="warning"
								plain
							></u-tag>
						</view>
					</view>
					
					<view class="coupon-right">
						<u-button 
							type="warning" 
							size="mini"
							shape="circle"
							:loading="coupon.receiving"
							@click="receiveCoupon(coupon)"
						>
							{{ coupon.receiving ? '领取中' : '立即领取' }}
						</u-button>
					</view>
				</view>
				
				<!-- 空状态 -->
				<u-empty 
					v-if="!loading && availableCoupons.length === 0"
					text="暂无可领取的优惠券"
					icon="/static/empty-coupon.png"
					:icon-size="200"
				></u-empty>
			</view>

			<!-- 我的优惠券 -->
			<view v-if="currentTab === 1">
				<view 
					class="coupon-item my-coupon" 
					:class="{ expired: coupon.status === 2, used: coupon.status === 1 }"
					v-for="coupon in myCoupons" 
					:key="coupon.id"
					@click="selectCoupon(coupon)"
				>
					<view class="coupon-left">
						<view class="coupon-amount">
							<text class="amount-symbol">¥</text>
							<text class="amount-value">{{ getCouponValue(coupon) }}</text>
						</view>
						<text class="coupon-condition">{{ getCouponCondition(coupon) }}</text>
					</view>
					
					<view class="coupon-center">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-desc">{{ coupon.description }}</text>
						<view class="coupon-time">
							<u-icon name="clock" size="12" color="#999"></u-icon>
							<text class="time-text">{{ formatCouponTime(coupon) }}</text>
						</view>
					</view>
					
					<view class="coupon-right">
						<view class="coupon-status">
							<text v-if="coupon.status === 0" class="status-available">可使用</text>
							<text v-else-if="coupon.status === 1" class="status-used">已使用</text>
							<text v-else class="status-expired">已过期</text>
						</view>
						<u-icon 
							name="arrow-right" 
							size="16" 
							color="#999"
							v-if="isSelectMode && coupon.status === 0"
						></u-icon>
					</view>
				</view>
				
				<!-- 空状态 -->
				<u-empty 
					v-if="!loading && myCoupons.length === 0"
					text="暂无优惠券"
					icon="/static/empty-coupon.png"
					:icon-size="200"
				></u-empty>
			</view>

			<!-- 加载更多 -->
			<u-loadmore 
				v-if="hasMore"
				:status="loading ? 'loading' : 'loadmore'"
				@loadmore="loadMoreCoupons"
			></u-loadmore>
		</scroll-view>

		<!-- 优惠券使用说明 -->
		<view class="help-section" v-if="currentTab === 0">
			<u-cell-group>
				<u-cell title="优惠券使用说明" @click="showHelpPopup = true" is-link>
					<template #icon>
						<u-icon name="question-circle" size="20" color="#ff6b35"></u-icon>
					</template>
				</u-cell>
			</u-cell-group>
		</view>

		<!-- 使用说明弹窗 -->
		<u-popup v-model="showHelpPopup" mode="bottom" border-radius="20">
			<view class="popup-content">
				<view class="popup-header">
					<text class="popup-title">优惠券使用说明</text>
					<u-icon name="close" @click="showHelpPopup = false"></u-icon>
				</view>
				<view class="help-content">
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

		<!-- 优惠券详情弹窗 -->
		<u-popup v-model="showDetailPopup" mode="center" border-radius="20" width="80%">
			<view class="detail-popup" v-if="selectedCoupon">
				<view class="detail-header">
					<text class="detail-title">{{ selectedCoupon.name }}</text>
					<u-icon name="close" @click="showDetailPopup = false"></u-icon>
				</view>
				
				<view class="detail-coupon">
					<view class="detail-amount">
						<text class="amount-symbol">¥</text>
						<text class="amount-value">{{ getCouponValue(selectedCoupon) }}</text>
					</view>
					<text class="detail-condition">{{ getCouponCondition(selectedCoupon) }}</text>
				</view>
				
				<view class="detail-info">
					<view class="info-item">
						<text class="info-label">优惠券名称：</text>
						<text class="info-value">{{ selectedCoupon.name }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">使用条件：</text>
						<text class="info-value">{{ getCouponCondition(selectedCoupon) }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">有效期：</text>
						<text class="info-value">{{ formatCouponTime(selectedCoupon) }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">使用说明：</text>
						<text class="info-value">{{ selectedCoupon.description }}</text>
					</view>
				</view>
				
				<u-button 
					type="warning" 
					shape="circle"
					:custom-style="{ marginTop: '30rpx' }"
					@click="confirmSelectCoupon"
					v-if="isSelectMode && selectedCoupon.status === 0"
				>
					选择此优惠券
				</u-button>
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
			hasMore: true,
			page: 1,
			pageSize: 20,
			showHelpPopup: false,
			showDetailPopup: false,
			selectedCoupon: null,
			isSelectMode: false, // 是否为选择模式（从订单页面跳转）
			userInfo: {}
		}
	},
	onLoad(options) {
		// 检查是否为选择模式
		if (options.select === 'true') {
			this.isSelectMode = true
			this.currentTab = 1 // 直接显示我的优惠券
		}
		
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
			this.currentTab = index
			this.page = 1
			this.hasMore = true
			this.loadCoupons()
		},

		// 加载优惠券数据
		async loadCoupons(loadMore = false) {
			if (this.loading) return
			
			if (!loadMore) {
				this.page = 1
				this.hasMore = true
			}

			this.loading = true

			try {
				let res
				if (this.currentTab === 0) {
					// 加载可领取优惠券
					res = await api.coupon.getAvailable()
				} else {
					// 加载我的优惠券
					if (!this.userInfo.id) {
						this.myCoupons = []
						return
					}
					res = await api.coupon.getMy(this.userInfo.id)
				}

				if (res.code === 200) {
					const newData = res.data || []
					
					if (this.currentTab === 0) {
						if (loadMore) {
							this.availableCoupons.push(...newData)
						} else {
							this.availableCoupons = newData
						}
					} else {
						if (loadMore) {
							this.myCoupons.push(...newData)
						} else {
							this.myCoupons = newData
						}
					}

					this.hasMore = newData.length === this.pageSize
					if (this.hasMore) {
						this.page++
					}
				} else {
					// 使用模拟数据
					this.loadMockData()
				}
			} catch (error) {
				console.error('加载优惠券失败:', error)
				this.loadMockData()
			} finally {
				this.loading = false
			}
		},

		// 加载模拟数据
		loadMockData() {
			if (this.currentTab === 0) {
				this.availableCoupons = [
					{
						id: 1,
						name: '新用户专享券',
						description: '仅限新用户首次下单使用',
						type: 1, // 1: 满减券, 2: 折扣券
						discount: 10,
						minAmount: 20,
						startTime: '2024-11-01 00:00:00',
						endTime: '2024-12-31 23:59:59',
						tags: ['新用户', '限时']
					},
					{
						id: 2,
						name: '满30减8券',
						description: '全场通用，满30元可用',
						type: 1,
						discount: 8,
						minAmount: 30,
						startTime: '2024-11-01 00:00:00',
						endTime: '2024-11-30 23:59:59',
						tags: ['全场通用']
					},
					{
						id: 3,
						name: '9折优惠券',
						description: '全场9折，无门槛使用',
						type: 2,
						discount: 9,
						minAmount: 0,
						startTime: '2024-11-01 00:00:00',
						endTime: '2024-11-25 23:59:59',
						tags: ['无门槛']
					}
				]
			} else {
				this.myCoupons = [
					{
						id: 11,
						name: '满20减5券',
						description: '全场通用，满20元可用',
						type: 1,
						discount: 5,
						minAmount: 20,
						startTime: '2024-11-01 00:00:00',
						endTime: '2024-11-30 23:59:59',
						status: 0, // 0: 未使用, 1: 已使用, 2: 已过期
						receiveTime: '2024-11-10 15:30:00'
					},
					{
						id: 12,
						name: '8折优惠券',
						description: '全场8折，无门槛使用',
						type: 2,
						discount: 8,
						minAmount: 0,
						startTime: '2024-11-01 00:00:00',
						endTime: '2024-11-20 23:59:59',
						status: 2,
						receiveTime: '2024-11-05 10:20:00'
					}
				]
			}
		},

		// 加载更多
		loadMoreCoupons() {
			if (this.hasMore && !this.loading) {
				this.loadCoupons(true)
			}
		},

		// 领取优惠券
		async receiveCoupon(coupon) {
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
				coupon.receiving = true
				
				const res = await api.coupon.receive(coupon.id, this.userInfo.id)
				
				if (res.code === 200) {
					uni.showToast({
						title: '领取成功',
						icon: 'success'
					})
					
					// 从可领取列表中移除
					const index = this.availableCoupons.findIndex(c => c.id === coupon.id)
					if (index > -1) {
						this.availableCoupons.splice(index, 1)
					}
				}
			} catch (error) {
				console.error('领取优惠券失败:', error)
				uni.showToast({
					title: error.message || '领取失败',
					icon: 'none'
				})
			} finally {
				coupon.receiving = false
			}
		},

		// 选择优惠券（选择模式）
		selectCoupon(coupon) {
			if (this.isSelectMode && coupon.status === 0) {
				this.selectedCoupon = coupon
				this.showDetailPopup = true
			}
		},

		// 确认选择优惠券
		confirmSelectCoupon() {
			// 通过事件总线或其他方式传递选中的优惠券
			uni.$emit('couponSelected', this.selectedCoupon)
			
			this.showDetailPopup = false
			
			// 返回上一页
			uni.navigateBack()
		},

		// 获取优惠券面值
		getCouponValue(coupon) {
			if (coupon.type === 1) {
				// 满减券
				return coupon.discount
			} else if (coupon.type === 2) {
				// 折扣券
				return `${coupon.discount}折`
			}
			return coupon.discount
		},

		// 获取优惠券使用条件
		getCouponCondition(coupon) {
			if (coupon.type === 1) {
				// 满减券
				return coupon.minAmount > 0 ? `满${coupon.minAmount}元可用` : '无门槛使用'
			} else if (coupon.type === 2) {
				// 折扣券
				return coupon.minAmount > 0 ? `满${coupon.minAmount}元可用` : '无门槛使用'
			}
			return '查看使用条件'
		},

		// 格式化优惠券时间
		formatCouponTime(coupon) {
			const startTime = formatTime(coupon.startTime, 'YYYY.MM.DD')
			const endTime = formatTime(coupon.endTime, 'YYYY.MM.DD')
			return `${startTime} - ${endTime}`
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
	height: calc(100vh - 88rpx); // 减去标签栏高度
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
}

.coupon-item.available {
	border-left: 8rpx solid #ff6b35;
}

.coupon-item.my-coupon {
	border-left: 8rpx solid #52c41a;
}

.coupon-item.used {
	opacity: 0.6;
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

.coupon-item.used .coupon-left,
.coupon-item.expired .coupon-left {
	background: #999;
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
	margin-bottom: 12rpx;
}

.time-text {
	font-size: 20rpx;
	color: #999;
	margin-left: 8rpx;
}

.coupon-tags {
	display: flex;
	gap: 8rpx;
}

.coupon-right {
	width: 160rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 20rpx;
}

.coupon-status {
	margin-bottom: 16rpx;
}

.status-available {
	font-size: 24rpx;
	color: #52c41a;
	font-weight: 600;
}

.status-used {
	font-size: 24rpx;
	color: #999;
}

.status-expired {
	font-size: 24rpx;
	color: #ff4d4f;
}

/* 帮助说明 */
.help-section {
	margin: 20rpx;
	background: white;
	border-radius: 20rpx;
	overflow: hidden;
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

.help-content {
	// 帮助内容样式
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

/* 优惠券详情弹窗 */
.detail-popup {
	padding: 40rpx 30rpx;
}

.detail-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.detail-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.detail-coupon {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 40rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 20rpx;
	margin-bottom: 30rpx;
}

.detail-amount {
	display: flex;
	align-items: baseline;
	color: white;
	margin-bottom: 12rpx;
}

.detail-amount .amount-symbol {
	font-size: 28rpx;
}

.detail-amount .amount-value {
	font-size: 56rpx;
}

.detail-condition {
	color: rgba(255, 255, 255, 0.9);
	font-size: 24rpx;
}

.detail-info {
	// 详情信息样式
}

.info-item {
	display: flex;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f8f8f8;
}

.info-item:last-child {
	border-bottom: none;
}

.info-label {
	width: 160rpx;
	font-size: 26rpx;
	color: #666;
	flex-shrink: 0;
}

.info-value {
	flex: 1;
	font-size: 26rpx;
	color: #333;
	line-height: 1.4;
}
</style>
