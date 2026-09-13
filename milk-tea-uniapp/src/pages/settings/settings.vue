<template>
	<view class="container">
		<!-- 账户设置 -->
		<view class="settings-section">
			<view class="section-header">
				<text class="section-title">账户设置</text>
			</view>
			<view class="settings-list">
				<view class="setting-item" @click="goToProfile">
					<text class="setting-icon">👤</text>
					<text class="setting-text">个人信息</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="goToAddress">
					<text class="setting-icon">📍</text>
					<text class="setting-text">地址管理</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="changePassword">
					<text class="setting-icon">🔒</text>
					<text class="setting-text">修改密码</text>
					<text class="setting-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 消息设置 -->
		<view class="settings-section">
			<view class="section-header">
				<text class="section-title">消息设置</text>
			</view>
			<view class="settings-list">
				<view class="setting-item">
					<text class="setting-icon">🔔</text>
					<text class="setting-text">订单通知</text>
					<switch 
						class="setting-switch" 
						:checked="settings.orderNotification" 
						@change="onOrderNotificationChange"
						color="#ff6b35"
					/>
				</view>
				<view class="setting-item">
					<text class="setting-icon">📢</text>
					<text class="setting-text">活动推送</text>
					<switch 
						class="setting-switch" 
						:checked="settings.activityPush" 
						@change="onActivityPushChange"
						color="#ff6b35"
					/>
				</view>
				<view class="setting-item">
					<text class="setting-icon">🎁</text>
					<text class="setting-text">优惠提醒</text>
					<switch 
						class="setting-switch" 
						:checked="settings.couponReminder" 
						@change="onCouponReminderChange"
						color="#ff6b35"
					/>
				</view>
			</view>
		</view>

		<!-- 隐私设置 -->
		<view class="settings-section">
			<view class="section-header">
				<text class="section-title">隐私设置</text>
			</view>
			<view class="settings-list">
				<view class="setting-item" @click="showPrivacyPolicy">
					<text class="setting-icon">🛡️</text>
					<text class="setting-text">隐私政策</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="showUserAgreement">
					<text class="setting-icon">📄</text>
					<text class="setting-text">用户协议</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item">
					<text class="setting-icon">👁️</text>
					<text class="setting-text">个性化推荐</text>
					<switch 
						class="setting-switch" 
						:checked="settings.personalizedRecommend" 
						@change="onPersonalizedRecommendChange"
						color="#ff6b35"
					/>
				</view>
			</view>
		</view>

		<!-- 通用设置 -->
		<view class="settings-section">
			<view class="section-header">
				<text class="section-title">通用设置</text>
			</view>
			<view class="settings-list">
				<view class="setting-item" @click="clearCache">
					<text class="setting-icon">🗑️</text>
					<text class="setting-text">清理缓存</text>
					<text class="setting-value">{{ cacheSize }}</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="checkUpdate">
					<text class="setting-icon">🔄</text>
					<text class="setting-text">检查更新</text>
					<text class="setting-value">v{{ appVersion }}</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="goToFeedback">
					<text class="setting-icon">💬</text>
					<text class="setting-text">意见反馈</text>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="goToAbout">
					<text class="setting-icon">ℹ️</text>
					<text class="setting-text">关于我们</text>
					<text class="setting-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 危险操作 -->
		<view class="settings-section">
			<view class="section-header">
				<text class="section-title">账户管理</text>
			</view>
			<view class="settings-list">
				<view class="setting-item danger" @click="deleteAccount">
					<text class="setting-icon">⚠️</text>
					<text class="setting-text">注销账号</text>
					<text class="setting-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 退出登录 -->
		<view class="logout-section" v-if="isLoggedIn">
			<view class="logout-btn" @click="logout">
				<text>退出登录</text>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			settings: {
				orderNotification: true,
				activityPush: true,
				couponReminder: true,
				personalizedRecommend: true
			},
			cacheSize: '12.5MB',
			appVersion: '1.0.0',
			isLoggedIn: false
		}
	},
	onLoad() {
		this.checkLoginStatus()
		this.loadSettings()
		this.calculateCacheSize()
	},
	methods: {
		// 加载设置
		async loadSettings() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo && userInfo.id) {
					// 从服务器加载设置
					const res = await api.settings.get(userInfo.id)
					if (res.code === 200 && res.data) {
						this.settings = {
							orderNotification: res.data.orderNotification === 1,
							activityPush: res.data.activityPush === 1,
							couponReminder: res.data.couponReminder === 1,
							personalizedRecommend: res.data.personalizedRecommend === 1
						}
					}
				} else {
					// 未登录，从本地加载
					const savedSettings = uni.getStorageSync('appSettings')
					if (savedSettings) {
						this.settings = { ...this.settings, ...savedSettings }
					}
				}
			} catch (error) {
				console.error('加载设置失败:', error)
				// 加载失败，使用本地设置
				const savedSettings = uni.getStorageSync('appSettings')
				if (savedSettings) {
					this.settings = { ...this.settings, ...savedSettings }
				}
			}
		},

		// 保存设置
		async saveSettings() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo && userInfo.id) {
					// 保存到服务器
					await api.settings.update({
						userId: userInfo.id,
						orderNotification: this.settings.orderNotification ? 1 : 0,
						activityPush: this.settings.activityPush ? 1 : 0,
						couponReminder: this.settings.couponReminder ? 1 : 0,
						personalizedRecommend: this.settings.personalizedRecommend ? 1 : 0
					})
				}
				// 同时保存到本地
				uni.setStorageSync('appSettings', this.settings)
			} catch (error) {
				console.error('保存设置失败:', error)
				// 保存失败，至少保存到本地
				uni.setStorageSync('appSettings', this.settings)
			}
		},

		// 检查登录状态
		checkLoginStatus() {
			const token = uni.getStorageSync('token')
			this.isLoggedIn = !!token
		},

		// 计算缓存大小
		calculateCacheSize() {
			try {
				const info = uni.getStorageInfoSync()
				this.cacheSize = `${(info.currentSize || 0).toFixed(1)}KB`
			} catch (error) {
				this.cacheSize = '0KB'
			}
		},

		// 跳转个人信息
		goToProfile() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/profile/profile'
			})
		},

		// 跳转地址管理
		goToAddress() {
			if (!this.checkLogin()) return
			uni.navigateTo({
				url: '/pages/address/address'
			})
		},

		// 修改密码
		changePassword() {
			if (!this.checkLogin()) return
			uni.showToast({
				title: '功能开发中',
				icon: 'none'
			})
		},

		// 订单通知开关
		onOrderNotificationChange(e) {
			this.settings.orderNotification = e.detail.value
			this.saveSettings()
		},

		// 活动推送开关
		onActivityPushChange(e) {
			this.settings.activityPush = e.detail.value
			this.saveSettings()
		},

		// 优惠提醒开关
		onCouponReminderChange(e) {
			this.settings.couponReminder = e.detail.value
			this.saveSettings()
		},

		// 个性化推荐开关
		onPersonalizedRecommendChange(e) {
			this.settings.personalizedRecommend = e.detail.value
			this.saveSettings()
		},

		// 显示隐私政策
		showPrivacyPolicy() {
			uni.showModal({
				title: '隐私政策',
				content: '我们重视您的隐私权益，会严格保护您的个人信息安全。详细内容请访问我们的官方网站查看完整的隐私政策。',
				showCancel: false,
				confirmText: '我知道了'
			})
		},

		// 显示用户协议
		showUserAgreement() {
			uni.showModal({
				title: '用户协议',
				content: '欢迎使用奶茶小程序！请仔细阅读本用户协议，使用本应用即表示您同意遵守相关条款。',
				showCancel: false,
				confirmText: '我知道了'
			})
		},

		// 清理缓存
		clearCache() {
			uni.showModal({
				title: '清理缓存',
				content: '确定要清理应用缓存吗？这将删除临时文件和图片缓存。',
				success: (res) => {
					if (res.confirm) {
						uni.showLoading({ title: '清理中...' })
						
						try {
							uni.removeStorageSync('appSettings')
							this.calculateCacheSize()
							uni.showToast({ title: '缓存清理完成', icon: 'success' })
						} finally {
							uni.hideLoading()
						}
					}
				}
			})
		},

		// 检查更新
		checkUpdate() {
			uni.showLoading({ title: '检查中...' })
			
			const updateManager = typeof uni.getUpdateManager === 'function' ? uni.getUpdateManager() : null
			if (!updateManager) {
				uni.hideLoading()
				uni.showToast({ title: '当前平台不支持在线更新', icon: 'none' })
				return
			}
			updateManager.onCheckForUpdate((result) => {
				uni.hideLoading()
				if (!result.hasUpdate) uni.showToast({ title: '当前已是最新版本', icon: 'none' })
			})
			updateManager.onUpdateReady(() => updateManager.applyUpdate())
			updateManager.onUpdateFailed(() => {
				uni.hideLoading()
				uni.showToast({ title: '更新检查失败', icon: 'none' })
			})
		},

		// 跳转意见反馈
		goToFeedback() {
			uni.navigateTo({
				url: '/pages/feedback/feedback'
			})
		},

		// 跳转关于我们
		goToAbout() {
			uni.showModal({
				title: '关于我们',
				content: '奶茶小程序 v1.0.0\n\n一款专注于提供优质奶茶服务的移动应用，致力于为用户带来便捷的点餐体验。\n\n客服电话：400-123-4567',
				showCancel: false,
				confirmText: '确定'
			})
		},

		// 注销账号
		deleteAccount() {
			if (!this.checkLogin()) return
			
			uni.showModal({
				title: '注销账号',
				content: '注销后将删除所有个人数据，且无法恢复。确定要注销账号吗？',
				confirmColor: '#ff4d4f',
				success: (res) => {
					if (res.confirm) {
						uni.showModal({
							title: '确认注销',
							content: '请再次确认，注销操作不可撤销！',
							confirmColor: '#ff4d4f',
							success: (res2) => {
								if (res2.confirm) {
									this.performDeleteAccount()
								}
							}
						})
					}
				}
			})
		},

		// 执行注销账号
		async performDeleteAccount() {
			try {
				uni.showLoading({ title: '注销中...' })
				
				// 这里应该调用注销API
				// await api.user.deleteAccount()
				
				// 清除本地数据
				uni.clearStorageSync()
				
				uni.hideLoading()
				uni.showToast({
					title: '账号已注销',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '注销失败',
					icon: 'none'
				})
			}
		},

		// 退出登录
		logout() {
			uni.showModal({
				title: '退出登录',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('token')
						uni.removeStorageSync('userInfo')
						this.isLoggedIn = false
						
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						})
						
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/index/index'
							})
						}, 1500)
					}
				}
			})
		},

		// 检查登录状态
		checkLogin() {
			if (!this.isLoggedIn) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/login'
					})
				}, 1500)
				return false
			}
			return true
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

/* 设置区块 */
.settings-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.settings-list {
	background: white;
}

.setting-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f8f9fa;
	transition: all 0.3s ease;
}

.setting-item:last-child {
	border-bottom: none;
}

.setting-item:active {
	background: rgba(255, 107, 53, 0.05);
}

.setting-item.danger:active {
	background: rgba(255, 77, 79, 0.05);
}

.setting-icon {
	font-size: 32rpx;
	margin-right: 20rpx;
	width: 40rpx;
	text-align: center;
}

.setting-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.setting-item.danger .setting-text {
	color: #ff4d4f;
}

.setting-value {
	font-size: 24rpx;
	color: #999;
	margin-right: 12rpx;
}

.setting-arrow {
	font-size: 24rpx;
	color: #999;
}

.setting-switch {
	transform: scale(0.8);
}

/* 退出登录 */
.logout-section {
	margin: 40rpx 20rpx 20rpx;
}

.logout-btn {
	background: white;
	border-radius: 24rpx;
	padding: 32rpx;
	text-align: center;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	transition: all 0.3s ease;
}

.logout-btn:active {
	background: rgba(255, 77, 79, 0.05);
	transform: scale(0.98);
}

.logout-btn text {
	font-size: 28rpx;
	color: #ff4d4f;
	font-weight: 600;
}
</style>
