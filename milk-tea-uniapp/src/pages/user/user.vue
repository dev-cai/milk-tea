<template>
	<view class="container">
		<!-- 用户信息头部 -->
		<view class="user-header">
			<view class="user-info">
				<view class="avatar-section" @click="userInfo.id ? chooseAvatar() : goToLogin()">
					<image v-if="userInfo.avatar" class="avatar" :src="userInfo.avatar" mode="aspectFill" @error="onAvatarError"></image>
					<view v-else class="avatar-placeholder">
						<text class="avatar-text">{{ userInfo.nickname ? userInfo.nickname.charAt(0) : '?' }}</text>
					</view>
					<view class="avatar-edit" v-if="userInfo.id">
						<text class="edit-icon">✎</text>
					</view>
				</view>
				<view class="info-section">
					<text class="nickname" @click="userInfo.id ? goToProfile() : goToLogin()">{{ userInfo.nickname || '点击登录' }}</text>
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
				<text class="section-more" @click="goToOrderList()">全部订单 ›</text>
			</view>
			<view class="order-types">
				<view class="order-type" @click="goToOrderList(0)">
					<view class="type-icon-box">
						<text class="type-icon">¥</text>
					</view>
					<text class="type-name">待支付</text>
					<view class="type-badge" v-if="orderCounts.unpaid > 0">{{ orderCounts.unpaid }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(1)">
					<view class="type-icon-box">
						<text class="type-icon">···</text>
					</view>
					<text class="type-name">待制作</text>
					<view class="type-badge" v-if="orderCounts.preparing > 0">{{ orderCounts.preparing }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(3)">
					<view class="type-icon-box">
						<text class="type-icon">取</text>
					</view>
					<text class="type-name">待取餐</text>
					<view class="type-badge" v-if="orderCounts.ready > 0">{{ orderCounts.ready }}</view>
				</view>
				<view class="order-type" @click="goToOrderList(4)">
					<view class="type-icon-box">
						<text class="type-icon">✓</text>
					</view>
					<text class="type-name">已完成</text>
				</view>
				<view class="order-type" @click="goToOrderList(6)">
					<view class="type-icon-box">
						<text class="type-icon">↻</text>
					</view>
					<text class="type-name">售后</text>
					<view class="type-badge" v-if="orderCounts.refund > 0">{{ orderCounts.refund }}</view>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-group">
				<view class="menu-item" @click="goToAddress">
					<view class="menu-icon-box">
						<text class="menu-icon">📍</text>
					</view>
					<text class="menu-text">地址管理</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goToMember">
					<view class="menu-icon-box">
						<text class="menu-icon">VIP</text>
					</view>
					<text class="menu-text">会员中心</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goToCoupon">
					<view class="menu-icon-box">
						<text class="menu-icon">券</text>
					</view>
					<text class="menu-text">我的优惠券</text>
					<text class="menu-arrow">›</text>
				</view>
			</view>

			<view class="menu-group">
				<view class="menu-item" @click="shareApp">
					<view class="menu-icon-box">
						<text class="menu-icon">↗</text>
					</view>
					<text class="menu-text">邀请好友</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goToComplaintList">
					<view class="menu-icon-box">
						<text class="menu-icon">📢</text>
					</view>
					<text class="menu-text">我的投诉</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goToFeedback">
					<view class="menu-icon-box">
						<text class="menu-icon">💬</text>
					</view>
					<text class="menu-text">意见反馈</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="contactService">
					<view class="menu-icon-box">
						<text class="menu-icon">服</text>
					</view>
					<text class="menu-text">联系客服</text>
					<text class="menu-arrow">›</text>
				</view>
			</view>

			<view class="menu-group">
				<view class="menu-item" @click="goToSettings">
					<view class="menu-icon-box">
						<text class="menu-icon">⚙</text>
					</view>
					<text class="menu-text">设置</text>
					<text class="menu-arrow">›</text>
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
				if (userInfo && userInfo.id) {
					// 先显示本地数据
					this.userInfo = { ...userInfo }
					console.log('【个人中心】从本地加载用户信息:', this.userInfo)
					console.log('【个人中心】本地会员等级:', this.userInfo.memberLevel, '类型:', typeof this.userInfo.memberLevel)
					
					// 从服务器获取最新用户信息
					try {
						const res = await api.user.getInfo(userInfo.id)
						console.log('【个人中心】服务器返回完整数据:', res)
						if (res.code === 200 && res.data) {
							console.log('【个人中心】服务器返回用户数据:', res.data)
							console.log('【个人中心】服务器会员等级:', res.data.memberLevel, '类型:', typeof res.data.memberLevel)
							this.userInfo = { ...res.data }
							uni.setStorageSync('userInfo', this.userInfo)
							console.log('【个人中心】更新后的用户信息:', this.userInfo)
							console.log('【个人中心】更新后的会员等级:', this.userInfo.memberLevel)
						}
					} catch (apiError) {
						console.error('从服务器获取用户信息失败，使用本地数据:', apiError)
					}
				}
			} catch (error) {
				console.error('加载用户信息失败:', error)
			}
		},
		
		// 头像加载失败处理
		onAvatarError(e) {
			console.error('头像加载失败:', e)
			// 头像加载失败时，清除头像显示占位符
			this.userInfo.avatar = ''
		},

		// 加载订单统计
		async loadOrderCounts() {
			try {
				if (!this.userInfo.id) {
					console.log('用户未登录，跳过订单统计')
					return
				}

				console.log('开始加载订单统计，用户ID:', this.userInfo.id)

				// 获取各状态的订单数量
				const [unpaidRes, preparingRes, readyRes, refundRes] = await Promise.all([
					api.order.getList({ userId: this.userInfo.id, status: 0, page: 1, size: 1 }), // 待支付
					api.order.getList({ userId: this.userInfo.id, status: 1, page: 1, size: 1 }), // 待制作
					api.order.getList({ userId: this.userInfo.id, status: 3, page: 1, size: 1 }), // 待取餐
					api.order.getList({ userId: this.userInfo.id, status: 6, page: 1, size: 1 })  // 售后
				])
				
				console.log('订单统计响应:', {
					unpaid: unpaidRes,
					preparing: preparingRes,
					ready: readyRes,
					refund: refundRes
				})
				
				this.orderCounts = {
					unpaid: unpaidRes.data?.total || 0,
					preparing: preparingRes.data?.total || 0,
					ready: readyRes.data?.total || 0,
					refund: refundRes.data?.total || 0
				}
				
				console.log('订单统计结果:', this.orderCounts)
			} catch (error) {
				console.error('加载订单统计失败:', error)
				// 出错时使用默认值
				this.orderCounts = {
					unpaid: 0,
					preparing: 0,
					ready: 0,
					refund: 0
				}
			}
		},

		// 选择头像
		chooseAvatar() {
			if (!this.userInfo.id) {
				this.goToLogin()
				return
			}

			uni.showActionSheet({
				itemList: ['从相册选择', '拍照'],
				success: (res) => {
					if (res.tapIndex === 0) {
						this.selectImageFromAlbum()
					} else if (res.tapIndex === 1) {
						this.selectImageFromCamera()
					}
				}
			})
		},

		// 从相册选择
		selectImageFromAlbum() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album'],
				success: (res) => {
					const tempFilePath = res.tempFilePaths[0]
					this.uploadAvatar(tempFilePath)
				}
			})
		},

		// 拍照
		selectImageFromCamera() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['camera'],
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
				
				console.log('选择的图片路径:', filePath)
				
				const token = uni.getStorageSync('token')
				if (!token) {
					uni.hideLoading()
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}
				
				// 上传文件到服务器
				uni.uploadFile({
					url: 'http://localhost:8080/api/file/upload-avatar',
					filePath: filePath,
					name: 'file',
					header: {
						'Authorization': `Bearer ${token}`
					},
					success: async (uploadRes) => {
						console.log('上传响应:', uploadRes)
						
						try {
							const data = JSON.parse(uploadRes.data)
							console.log('解析后的数据:', data)
							
							if (data.code === 200) {
								const avatarUrl = data.data.url
								console.log('头像URL:', avatarUrl)
								
								// 更新用户信息
								const updateRes = await api.user.updateInfo({
									id: this.userInfo.id,
									avatar: avatarUrl
								})
								
								if (updateRes.code === 200) {
									// 更新本地数据 - 创建新对象触发响应式更新
									const newUserInfo = {
										...this.userInfo,
										avatar: avatarUrl
									}
									this.userInfo = newUserInfo
									uni.setStorageSync('userInfo', newUserInfo)
									
									console.log('头像更新成功，新的userInfo:', this.userInfo)
									
									uni.hideLoading()
									uni.showToast({
										title: '头像更新成功',
										icon: 'success'
									})
									
									// 延迟刷新页面确保显示
									setTimeout(() => {
										this.loadUserInfo()
									}, 500)
								} else {
									throw new Error(updateRes.message || '更新用户信息失败')
								}
							} else {
								throw new Error(data.message || '上传失败')
							}
						} catch (parseError) {
							console.error('处理上传结果失败:', parseError)
							uni.hideLoading()
							uni.showToast({
								title: '上传失败: ' + parseError.message,
								icon: 'none'
							})
						}
					},
					fail: (error) => {
						console.error('上传文件失败:', error)
						uni.hideLoading()
						uni.showToast({
							title: '上传失败',
							icon: 'none'
						})
					}
				})
			} catch (error) {
				console.error('上传头像失败:', error)
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
			
			uni.navigateTo({
				url: '/pages/recharge/recharge'
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

		// 跳转投诉列表
		goToComplaintList() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/complaint/complaint-list'
			})
		},

		// 跳转意见反馈
		goToFeedback() {
			uni.navigateTo({
				url: '/pages/feedback/feedback'
			})
		},

		// 分享应用 - 跳转到邀请页面
		shareApp() {
			console.log('点击分享按钮')
			console.log('用户信息:', this.userInfo)
			
			if (!this.checkLogin()) {
				console.log('未登录，跳转登录页面')
				return
			}
			
			console.log('跳转到邀请页面')
			uni.navigateTo({
				url: '/pages/invite/invite'
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
			const token = uni.getStorageSync('token')
			const userInfo = uni.getStorageSync('userInfo')
			
			console.log('检查登录状态 - token:', token)
			console.log('检查登录状态 - userInfo:', userInfo)
			
			if (!token || !userInfo || !userInfo.id) {
				console.log('未登录，跳转登录页')
				this.goToLogin()
				return false
			}
			
			// 确保当前页面的userInfo是最新的
			if (!this.userInfo.id) {
				this.userInfo = userInfo
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

.avatar-placeholder {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border: 6rpx solid rgba(255, 255, 255, 0.3);
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.3);
	display: flex;
	align-items: center;
	justify-content: center;
}

.avatar-text {
	font-size: 48rpx;
	color: white;
	font-weight: 700;
	text-transform: uppercase;
}

.avatar-edit {
	position: absolute;
	bottom: 0;
	right: 0;
	width: 40rpx;
	height: 40rpx;
	background: linear-gradient(135deg, #ff6b35, #f7931e);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 2rpx 8rpx rgba(255, 107, 53, 0.3);
	border: 2rpx solid white;
}

.edit-icon {
	font-size: 18rpx;
	color: white;
	font-weight: 600;
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

.action-icon-box {
	width: 80rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #ff6b35, #f7931e);
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12rpx;
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.action-icon-box.points {
	background: linear-gradient(135deg, #667eea, #764ba2);
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.action-icon-box.coupon {
	background: linear-gradient(135deg, #f093fb, #f5576c);
	box-shadow: 0 4rpx 12rpx rgba(240, 147, 251, 0.3);
}

.action-icon {
	font-size: 32rpx;
	color: white;
	font-weight: 700;
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

.type-icon-box {
	width: 72rpx;
	height: 72rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.1), rgba(247, 147, 30, 0.1));
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12rpx;
	border: 2rpx solid rgba(255, 107, 53, 0.2);
}

.type-icon {
	font-size: 26rpx;
	color: #ff6b35;
	font-weight: 700;
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

.menu-icon-box {
	width: 64rpx;
	height: 64rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.08), rgba(247, 147, 30, 0.08));
	border-radius: 14rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 24rpx;
	border: 1rpx solid rgba(255, 107, 53, 0.15);
}

.menu-icon {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
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
