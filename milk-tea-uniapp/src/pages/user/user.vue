<template>
	<view class="container">
		<!-- 用户信息头部 -->
		<view class="user-header">
			<view class="user-info">
				<view class="avatar-section" @click="goToProfile">
					<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill"></image>
					<view class="avatar-edit">👤</view>
				</view>
				<view class="info-section">
					<text class="nickname">{{ userInfo.nickname || '点击登录' }}</text>
					<view class="member-info" v-if="userInfo.id">
						<text class="member-level" :style="{ color: getMemberLevelColor(userInfo.memberLevel) }">
							{{ getMemberLevelText(userInfo.memberLevel) }}
						</text>
						<text class="member-points">积分: {{ userInfo.points || 0 }}</text>
					</view>
					<view class="login-tip" v-else @click="goToLogin">
						<text>登录享受更多优惠</text>
					</view>
				</view>
			</view>
			
			<!-- 会员卡片 -->
			<view class="member-card" v-if="userInfo.id">
				<view class="card-header">
					<text class="card-title">会员卡</text>
					<text class="card-balance">余额: ¥{{ formatPrice(userInfo.balance || 0) }}</text>
				</view>
				<view class="card-actions">
					<view class="action-btn" @click="goToRecharge">
						<text class="action-icon">💰</text>
						<text class="action-text">充值</text>
					</view>
					<view class="action-btn" @click="goToPoints">
						<text class="action-icon">🎯</text>
						<text class="action-text">积分</text>
					</view>
					<view class="action-btn" @click="goToCoupon">
						<text class="action-icon">🎫</text>
						<text class="action-text">优惠券</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单管理 -->
		<view class="order-section">
			<view class="section-header">
				<text class="section-title">我的订单</text>
				<text class="section-more" @click="goToOrderList()">全部订单 ></text>
			</view>
			<view class="order-types">
				<view class="order-type" @click="goToOrderList(0)">
					<text class="type-icon">💰</text>
					<text class="type-name">待支付</text>
					<view class="type-badge" v-if="orderCounts.unpaid > 0">{{ orderCounts.unpaid }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(1)">
					<text class="type-icon">⏰</text>
					<text class="type-name">待制作</text>
					<view class="type-badge" v-if="orderCounts.preparing > 0">{{ orderCounts.preparing }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(3)">
					<text class="type-icon">🥤</text>
					<text class="type-name">待取餐</text>
					<view class="type-badge" v-if="orderCounts.ready > 0">{{ orderCounts.ready }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(4)">
					<text class="type-icon">✅</text>
					<text class="type-name">已完成</text>
				</view>
				<view class="order-type" @click="goToOrderList(6)">
					<text class="type-icon">🔄</text>
					<text class="type-name">售后</text>
					<view class="type-badge" v-if="orderCounts.refund > 0">{{ orderCounts.refund }}</view>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-group">
				<view class="menu-item" @click="goToAddress">
					<text class="menu-icon">📍</text>
					<text class="menu-text">地址管理</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToMember">
					<text class="menu-icon">👑</text>
					<text class="menu-text">会员中心</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToCoupon">
					<text class="menu-icon">🎫</text>
					<text class="menu-text">我的优惠券</text>
					<text class="menu-arrow">></text>
				</view>
			</view>

			<view class="menu-group">
				<view class="menu-item" @click="shareApp">
					<text class="menu-icon">📤</text>
					<text class="menu-text">分享给好友</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToFeedback">
					<text class="menu-icon">💬</text>
					<text class="menu-text">意见反馈</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="contactService">
					<text class="menu-icon">📞</text>
					<text class="menu-text">联系客服</text>
					<text class="menu-arrow">></text>
				</view>
			</view>

			<view class="menu-group">
				<view class="menu-item" @click="goToSettings">
					<text class="menu-icon">⚙️</text>
					<text class="menu-text">设置</text>
					<text class="menu-arrow">></text>
				</view>
			</view>
		</view>

		<!-- 退出登录 -->
		<view class="logout-section" v-if="userInfo.id">
			<view class="logout-btn" @click="logout">
				<text>退出登录</text>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice, getMemberLevelText, getMemberLevelColor } from '@/utils/common.js'

export default {
	data() {
		return {
			userInfo: {},
			orderCounts: {
				unpaid: 0,
				preparing: 0,
				ready: 0,
				refund: 0
			}
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadOrderCounts()
	},
	onShow() {
		// 每次显示页面时刷新数据
		this.loadUserInfo()
		this.loadOrderCounts()
	},
	methods: {
		// 加载用户信息
		async loadUserInfo() {
			try {
				const token = uni.getStorageSync('token')
				if (!token) {
					this.userInfo = {}
					return
				}

				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo) {
					this.userInfo = userInfo
				}

				// 从服务器获取最新用户信息
				const res = await api.user.getInfo(this.userInfo.id)
				if (res.code === 200) {
					this.userInfo = res.data
					uni.setStorageSync('userInfo', this.userInfo)
				}
			} catch (error) {
				console.error('加载用户信息失败:', error)
			}
		},

		// 加载订单统计
		async loadOrderCounts() {
			try {
				if (!this.userInfo.id) return

				const res = await api.order.getList({ 
					userId: this.userInfo.id,
					pageSize: 1,
					current: 1
				})
				
				if (res.code === 200) {
					// 这里应该从后端获取各状态订单数量
					// 暂时使用模拟数据
					this.orderCounts = {
						unpaid: 2,
						preparing: 1,
						ready: 0,
						refund: 0
					}
				}
			} catch (error) {
				console.error('加载订单统计失败:', error)
			}
		},

		// 选择头像
		chooseAvatar() {
			if (!this.userInfo.id) {
				this.goToLogin()
				return
			}

			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempFilePath = res.tempFilePaths[0]
					this.uploadAvatar(tempFilePath)
				}
			})
		},

		// 上传头像
		async uploadAvatar(filePath) {
			try {
				uni.showLoading({ title: '上传中...' })
				
				// 这里应该调用文件上传接口
				// 暂时使用本地路径
				this.userInfo.avatar = filePath
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.hideLoading()
				uni.showToast({
					title: '头像更新成功',
					icon: 'success'
				})
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '上传失败',
					icon: 'none'
				})
			}
		},

		// 跳转登录
		goToLogin() {
			uni.navigateTo({
				url: '/pages/login/login'
			})
		},

		// 跳转订单列表
		goToOrderList(status) {
			const url = status !== undefined 
				? `/pages/order-list/order-list?status=${status}`
				: '/pages/order-list/order-list'
			uni.navigateTo({ url })
		},

		// 跳转地址管理
		goToAddress() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/address/address'
			})
		},

		// 跳转会员中心
		goToMember() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/member/member'
			})
		},

		// 跳转优惠券
		goToCoupon() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/coupon/coupon'
			})
		},

		// 跳转积分商城
		goToPoints() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/points/points'
			})
		},

		// 跳转充值
		goToRecharge() {
			if (!this.checkLogin()) return
			uni.showToast({
				title: '充值功能开发中',
				icon: 'none'
			})
		},

		// 跳转设置
		goToSettings() {
			uni.navigateTo({
				url: '/pages/settings/settings'
			})
		},

		// 跳转个人信息
		goToProfile() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/profile/profile'
			})
		},

		// 跳转意见反馈
		goToFeedback() {
			uni.navigateTo({
				url: '/pages/feedback/feedback'
			})
		},

		// 分享应用
		shareApp() {
			uni.share({
				provider: 'weixin',
				scene: 'WXSceneSession',
				type: 0,
				href: '',
				title: '奶茶小程序',
				summary: '好喝的奶茶，优惠多多！',
				imageUrl: '/static/logo.png',
				success: () => {
					uni.showToast({
						title: '分享成功',
						icon: 'success'
					})
				},
				fail: () => {
					// 微信小程序使用转发
					uni.showShareMenu({
						withShareTicket: true
					})
				}
			})
		},

		// 联系客服
		contactService() {
			uni.makePhoneCall({
				phoneNumber: '400-123-4567'
			})
		},

		// 检查登录状态
		checkLogin() {
			if (!this.userInfo.id) {
				this.goToLogin()
				return false
			}
			return true
		},

		// 退出登录
		logout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('token')
						uni.removeStorageSync('userInfo')
						this.userInfo = {}
						this.orderCounts = {
							unpaid: 0,
							preparing: 0,
							ready: 0,
							refund: 0
						}
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						})
					}
				}
			})
		},

		// 工具方法
		formatPrice,
		getMemberLevelText,
		getMemberLevelColor
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #ff6b35 0%, #f8f9fa 40%);
}

/* 用户信息头部 */
.user-header {
	padding: 40rpx 30rpx;
	background: transparent;
}

.user-info {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.avatar-section {
	position: relative;
	margin-right: 30rpx;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	border: 6rpx solid rgba(255, 255, 255, 0.3);
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
}

.avatar-edit {
	position: absolute;
	bottom: 0;
	right: 0;
	width: 40rpx;
	height: 40rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.info-section {
	flex: 1;
}

.nickname {
	font-size: 36rpx;
	font-weight: 700;
	color: white;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
	display: block;
	margin-bottom: 8rpx;
}

.member-info {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.member-level {
	font-size: 24rpx;
	font-weight: 600;
	padding: 6rpx 12rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 12rpx;
	backdrop-filter: blur(10rpx);
}

.member-points {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

.login-tip {
	padding: 8rpx 16rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 20rpx;
	backdrop-filter: blur(10rpx);
}

.login-tip text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 会员卡片 */
.member-card {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.3);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.card-balance {
	font-size: 32rpx;
	font-weight: 700;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.card-actions {
	display: flex;
	justify-content: space-around;
}

.action-btn {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 16rpx;
	transition: all 0.3s ease;
}

.action-btn:active {
	background: rgba(255, 107, 53, 0.1);
	transform: scale(0.95);
}

.action-icon {
	font-size: 40rpx;
	margin-bottom: 8rpx;
}

.action-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

/* 订单管理 */
.order-section {
	margin: 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: 700;
	color: #333;
}

.section-more {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 500;
}

.order-types {
	display: flex;
	justify-content: space-around;
}

.order-type {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 16rpx;
	transition: all 0.3s ease;
	position: relative;
}

.order-type:active {
	background: rgba(255, 107, 53, 0.1);
	transform: scale(0.95);
}

.type-icon {
	font-size: 40rpx;
	margin-bottom: 8rpx;
}

.type-name {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

.type-badge {
	position: absolute;
	top: 10rpx;
	right: 10rpx;
	background: #ff4d4f;
	color: white;
	font-size: 18rpx;
	padding: 4rpx 8rpx;
	border-radius: 10rpx;
	min-width: 20rpx;
	text-align: center;
	font-weight: 600;
}

/* 功能菜单 */
.menu-section {
	margin: 30rpx;
}

.menu-group {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	overflow: hidden;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid rgba(240, 240, 240, 0.5);
	transition: all 0.3s ease;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-item:active {
	background: rgba(255, 107, 53, 0.05);
}

.menu-icon {
	font-size: 40rpx;
	margin-right: 24rpx;
}

.menu-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.menu-arrow {
	font-size: 24rpx;
	color: #999;
}

/* 退出登录 */
.logout-section {
	margin: 30rpx;
	padding-bottom: 40rpx;
}

.logout-btn {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 30rpx;
	text-align: center;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
}

.logout-btn:active {
	background: rgba(255, 77, 79, 0.1);
	transform: scale(0.98);
}

.logout-btn text {
	font-size: 28rpx;
	color: #ff4d4f;
	font-weight: 600;
}
</style>
