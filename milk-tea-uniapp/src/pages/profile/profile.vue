<template>
	<view class="container">
		<!-- 头像区域 -->
		<view class="avatar-section">
			<view class="avatar-wrapper" @click="changeAvatar">
				<image class="avatar" :src="userInfo.avatar || 'https://via.placeholder.com/200x200/CCCCCC/666666?text=Avatar'" mode="aspectFill"></image>
				<view class="avatar-overlay">
					<text class="camera-icon">📷</text>
					<text class="change-text">更换头像</text>
				</view>
			</view>
		</view>

		<!-- 基本信息 -->
		<view class="info-section">
			<view class="section-header">
				<text class="section-title">基本信息</text>
			</view>
			<view class="info-list">
				<view class="info-item" @click="editNickname">
					<text class="info-label">昵称</text>
					<text class="info-value">{{ userInfo.nickname || '未设置' }}</text>
					<text class="info-arrow">›</text>
				</view>
				<view class="info-item">
					<text class="info-label">性别</text>
					<view class="gender-options">
						<view class="gender-option" 
							:class="{ active: userInfo.gender === 1 }"
							@click="selectGender(1)"
						>
							<text>👨 男</text>
						</view>
						<view class="gender-option" 
							:class="{ active: userInfo.gender === 2 }"
							@click="selectGender(2)"
						>
							<text>👩 女</text>
						</view>
					</view>
				</view>
				<picker mode="date" :value="userInfo.birthday || '2000-01-01'" @change="onBirthdayChange">
					<view class="info-item">
						<text class="info-label">生日</text>
						<text class="info-value">{{ userInfo.birthday || '未设置' }}</text>
						<text class="info-arrow">›</text>
					</view>
				</picker>
			</view>
		</view>

		<!-- 联系方式 -->
		<view class="contact-section">
			<view class="section-header">
				<text class="section-title">联系方式</text>
			</view>
			<view class="contact-list">
				<view class="contact-item">
					<text class="contact-label">手机号</text>
					<text class="contact-value">{{ formatPhone(userInfo.phone) }}</text>
					<view class="contact-status verified">
						<text>已验证</text>
					</view>
				</view>
				<view class="contact-item" @click="handleWechatBind">
					<text class="contact-label">微信</text>
					<text class="contact-value">{{ userInfo.openid ? '已绑定' : '未绑定' }}</text>
					<view class="contact-status" :class="{ verified: userInfo.openid }">
						<text>{{ userInfo.openid ? '已绑定' : '未绑定' }}</text>
					</view>
					<text class="contact-action">
						{{ userInfo.openid ? '解绑' : '绑定' }}
					</text>
				</view>
			</view>
		</view>

		<!-- 会员信息 -->
		<view class="member-section">
			<view class="section-header">
				<text class="section-title">会员信息</text>
			</view>
			<view class="member-card">
				<view class="member-info">
					<text class="member-level">{{ getMemberLevelText(userInfo.memberLevel) }}</text>
					<text class="member-id">会员号：{{ userInfo.memberNo || '未生成' }}</text>
				</view>
				<view class="member-stats">
					<view class="stat-item">
						<text class="stat-value">{{ userInfo.points || 0 }}</text>
						<text class="stat-label">积分</text>
					</view>
					<view class="stat-item">
						<text class="stat-value">¥{{ formatPrice(userInfo.balance || 0) }}</text>
						<text class="stat-label">余额</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 账户安全 -->
		<view class="security-section">
			<view class="section-header">
				<text class="section-title">账户安全</text>
			</view>
			<view class="security-list">
				<view class="security-item" @click="showChangePasswordModal">
					<text class="security-icon">🔒</text>
					<view class="security-content">
						<text class="security-text">修改密码</text>
						<text class="security-desc">定期更换密码保护账户安全</text>
					</view>
					<text class="security-arrow">›</text>
				</view>
				<view class="security-item" @click="showLoginHistory">
					<text class="security-icon">📱</text>
					<view class="security-content">
						<text class="security-text">登录记录</text>
						<text class="security-desc">查看最近登录设备和时间</text>
					</view>
					<text class="security-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 昵称编辑弹窗 -->
		<view class="nickname-modal" :class="{ show: showNicknameModal }" @click="hideNicknameModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">修改昵称</text>
					<text class="modal-close" @click="hideNicknameModal">✕</text>
				</view>
				<view class="modal-body">
					<input 
						class="nickname-input" 
						type="text" 
						placeholder="请输入昵称" 
						v-model="tempNickname"
						maxlength="20"
					/>
					<text class="input-tip">昵称长度为2-20个字符</text>
				</view>
				<view class="modal-footer">
					<view class="modal-btn cancel" @click="hideNicknameModal">
						<text>取消</text>
					</view>
					<view class="modal-btn confirm" @click="saveNickname">
						<text>确定</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 修改密码弹窗 -->
		<view class="password-modal" :class="{ show: showPasswordModal }" @click="hidePasswordModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">修改密码</text>
					<text class="modal-close" @click="hidePasswordModal">✕</text>
				</view>
				<view class="modal-body">
					<input 
						class="password-input" 
						type="password" 
						placeholder="请输入旧密码" 
						v-model="oldPassword"
					/>
					<input 
						class="password-input" 
						type="password" 
						placeholder="请输入新密码" 
						v-model="newPassword"
					/>
					<input 
						class="password-input" 
						type="password" 
						placeholder="请确认新密码" 
						v-model="confirmPassword"
					/>
					<text class="input-tip">密码长度为6-20个字符</text>
				</view>
				<view class="modal-footer">
					<view class="modal-btn cancel" @click="hidePasswordModal">
						<text>取消</text>
					</view>
					<view class="modal-btn confirm" @click="savePassword">
						<text>确定</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 登录记录弹窗 -->
		<view class="login-history-modal" :class="{ show: showLoginHistoryModal }" @click="hideLoginHistoryModal">
			<view class="modal-content history-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">登录记录</text>
					<text class="modal-close" @click="hideLoginHistoryModal">✕</text>
				</view>
				<view class="modal-body">
					<view v-if="loginHistory.length === 0" class="empty-history">
						<text class="empty-icon">📝</text>
						<text class="empty-text">暂无登录记录</text>
					</view>
					<view v-else class="history-list">
						<view class="history-item" v-for="(item, index) in loginHistory" :key="index">
							<view class="history-info">
								<text class="history-device">{{ item.device }}</text>
								<text class="history-time">{{ item.loginTime }}</text>
							</view>
							<view class="history-location">
								<text>{{ item.location || '未知位置' }}</text>
							</view>
						</view>
					</view>
				</view>
				<view class="modal-footer single">
					<view class="modal-btn confirm full" @click="hideLoginHistoryModal">
						<text>关闭</text>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatPrice, getMemberLevelText } from '@/utils/common.js'

export default {
	data() {
		return {
			userInfo: {},
			showNicknameModal: false,
			tempNickname: '',
			showBirthdayPicker: false,
			showPasswordModal: false,
			oldPassword: '',
			newPassword: '',
			confirmPassword: '',
			showLoginHistoryModal: false,
			loginHistory: []
		}
	},
	onLoad() {
		this.loadUserInfo()
	},
	onShow() {
		this.loadUserInfo()
	},
	methods: {
		// 加载用户信息
		async loadUserInfo() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo && userInfo.id) {
					// 先显示本地数据
					this.userInfo = { ...userInfo }
					
					// 从服务器获取最新信息
					try {
						const res = await api.user.getInfo(userInfo.id)
						if (res.code === 200 && res.data) {
							this.userInfo = { ...res.data }
							uni.setStorageSync('userInfo', this.userInfo)
							console.log('个人信息页 - 更新用户信息:', this.userInfo)
						}
					} catch (apiError) {
						console.error('从服务器获取用户信息失败，使用本地数据:', apiError)
					}
				}
			} catch (error) {
				console.error('加载用户信息失败:', error)
			}
		},

		// 更换头像
		changeAvatar() {
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
				const res = await api.file.uploadAvatar(filePath)
				const avatarUrl = res.data.url
				await api.user.updateInfo({ avatar: avatarUrl })
				this.userInfo = { ...this.userInfo, avatar: avatarUrl }
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.showToast({
					title: '头像更新成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('上传头像失败:', error)
				uni.showToast({
					title: '上传失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 编辑昵称
		editNickname() {
			this.tempNickname = this.userInfo.nickname || ''
			this.showNicknameModal = true
		},

		// 隐藏昵称弹窗
		hideNicknameModal() {
			this.showNicknameModal = false
		},

		// 保存昵称
		async saveNickname() {
			const nickname = this.tempNickname.trim()
			if (!nickname) {
				uni.showToast({
					title: '请输入昵称',
					icon: 'none'
				})
				return
			}

			if (nickname.length < 2 || nickname.length > 20) {
				uni.showToast({
					title: '昵称长度为2-20个字符',
					icon: 'none'
				})
				return
			}

			try {
				uni.showLoading({ title: '保存中...' })
				
				// 调用API更新昵称
				const res = await api.user.updateInfo({
					id: this.userInfo.id,
					nickname: nickname
				})
				
				if (res.code === 200) {
					this.userInfo.nickname = nickname
					uni.setStorageSync('userInfo', this.userInfo)
					
					this.hideNicknameModal()
					uni.hideLoading()
					uni.showToast({
						title: '昵称更新成功',
						icon: 'success'
					})
				} else {
					throw new Error(res.message || '更新失败')
				}
			} catch (error) {
				console.error('更新昵称失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: '更新失败',
					icon: 'none'
				})
			}
		},

		// 选择性别
		async selectGender(gender) {
			if (this.userInfo.gender === gender) return

			try {
				uni.showLoading({ title: '保存中...' })
				
				// 调用API更新性别
				const res = await api.user.updateInfo({
					id: this.userInfo.id,
					gender: gender
				})
				
				if (res.code === 200) {
					this.userInfo.gender = gender
					uni.setStorageSync('userInfo', this.userInfo)
					
					uni.hideLoading()
					uni.showToast({
						title: '性别更新成功',
						icon: 'success'
					})
				} else {
					throw new Error(res.message || '更新失败')
				}
			} catch (error) {
				console.error('更新性别失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: '更新失败',
					icon: 'none'
				})
			}
		},

		// 编辑生日 - 直接触发picker
		editBirthday() {
			// 不需要额外操作，点击会触发picker
		},

		// 生日改变
		async onBirthdayChange(e) {
			const birthday = e.detail.value
			
			try {
				uni.showLoading({ title: '保存中...' })
				
				// 调用API更新生日
				const res = await api.user.updateInfo({
					id: this.userInfo.id,
					birthday: birthday
				})
				
				if (res.code === 200) {
					this.userInfo.birthday = birthday
					uni.setStorageSync('userInfo', this.userInfo)
					
					uni.hideLoading()
					uni.showToast({
						title: '生日更新成功',
						icon: 'success'
					})
				} else {
					throw new Error(res.message || '更新失败')
				}
			} catch (error) {
				console.error('更新生日失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: '更新失败',
					icon: 'none'
				})
			}
		},



		// 处理微信绑定/解绑
		handleWechatBind() {
			if (this.userInfo.openid) {
				// 已绑定，执行解绑
				uni.showModal({
					title: '解绑微信',
					content: '确定要解绑微信吗？',
					success: (res) => {
						if (res.confirm) {
							this.unbindWechat()
						}
					}
				})
			} else {
				// 未绑定，执行绑定
				this.bindWechat()
			}
		},

		// 绑定微信
		async bindWechat() {
			try {
				uni.showLoading({ title: '绑定中...' })
				
				// 调用微信登录获取code
				const loginRes = await uni.login({
					provider: 'weixin'
				})
				
				if (!loginRes[1] || !loginRes[1].code) {
					throw new Error('获取微信授权失败')
				}
				
				const code = loginRes[1].code
				
				// 调用后端API绑定微信
				const res = await api.user.bindWechat({
					userId: this.userInfo.id,
					code: code
				})
				
				if (res.code === 200) {
					this.userInfo.openid = res.data.openid
					uni.setStorageSync('userInfo', this.userInfo)
					
					uni.hideLoading()
					uni.showToast({
						title: '微信绑定成功',
						icon: 'success'
					})
					
					// 刷新用户信息
					this.loadUserInfo()
				} else {
					throw new Error(res.message || '绑定失败')
				}
			} catch (error) {
				console.error('微信绑定失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: error.message || '绑定失败',
					icon: 'none'
				})
			}
		},

		// 解绑微信
		async unbindWechat() {
			try {
				uni.showLoading({ title: '解绑中...' })
				
				// 调用后端API解绑微信
				const res = await api.user.unbindWechat({
					userId: this.userInfo.id
				})
				
				if (res.code === 200) {
					this.userInfo.openid = null
					uni.setStorageSync('userInfo', this.userInfo)
					
					uni.hideLoading()
					uni.showToast({
						title: '微信解绑成功',
						icon: 'success'
					})
					
					// 刷新用户信息
					this.loadUserInfo()
				} else {
					throw new Error(res.message || '解绑失败')
				}
			} catch (error) {
				console.error('微信解绑失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: error.message || '解绑失败',
					icon: 'none'
				})
			}
		},

		// 显示修改密码弹窗
		showChangePasswordModal() {
			this.oldPassword = ''
			this.newPassword = ''
			this.confirmPassword = ''
			this.showPasswordModal = true
		},

		// 隐藏修改密码弹窗
		hidePasswordModal() {
			this.showPasswordModal = false
		},

		// 显示登录记录
		async showLoginHistory() {
			try {
				uni.showLoading({ title: '加载中...' })
				
				// 调用后端API获取登录记录
				const res = await api.user.getLoginHistory(this.userInfo.id)
				
				if (res.code === 200) {
					this.loginHistory = res.data || []
					this.showLoginHistoryModal = true
				} else {
					throw new Error(res.message || '获取失败')
				}
			} catch (error) {
				console.error('获取登录记录失败:', error)
				uni.showToast({
					title: error.message || '获取失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 隐藏登录记录弹窗
		hideLoginHistoryModal() {
			this.showLoginHistoryModal = false
		},

		// 保存新密码
		async savePassword() {
			// 验证输入
			if (!this.oldPassword) {
				uni.showToast({
					title: '请输入旧密码',
					icon: 'none'
				})
				return
			}

			if (!this.newPassword) {
				uni.showToast({
					title: '请输入新密码',
					icon: 'none'
				})
				return
			}

			if (this.newPassword.length < 6 || this.newPassword.length > 20) {
				uni.showToast({
					title: '密码长度为6-20个字符',
					icon: 'none'
				})
				return
			}

			if (this.newPassword !== this.confirmPassword) {
				uni.showToast({
					title: '两次输入的密码不一致',
					icon: 'none'
				})
				return
			}

			if (this.oldPassword === this.newPassword) {
				uni.showToast({
					title: '新密码不能与旧密码相同',
					icon: 'none'
				})
				return
			}

			try {
				uni.showLoading({ title: '修改中...' })
				
				// 调用后端API修改密码
				const res = await api.user.changePassword({
					userId: this.userInfo.id,
					oldPassword: this.oldPassword,
					newPassword: this.newPassword
				})
				
				if (res.code === 200) {
					this.hidePasswordModal()
					uni.hideLoading()
					
					uni.showModal({
						title: '修改成功',
						content: '密码修改成功，请重新登录',
						showCancel: false,
						success: () => {
							// 清除登录信息
							uni.removeStorageSync('token')
							uni.removeStorageSync('userInfo')
							
							// 跳转到登录页
							uni.reLaunch({
								url: '/pages/login/login'
							})
						}
					})
				} else {
					throw new Error(res.message || '修改失败')
				}
			} catch (error) {
				console.error('修改密码失败:', error)
				uni.hideLoading()
				uni.showToast({
					title: error.message || '修改失败',
					icon: 'none'
				})
			}
		},

		// 格式化手机号
		formatPhone(phone) {
			if (!phone) return '未绑定'
			return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
		},

		// 工具方法
		formatPrice,
		getMemberLevelText
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 40rpx;
}

/* 头像区域 */
.avatar-section {
	display: flex;
	justify-content: center;
	padding: 60rpx 0;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
}

.avatar-wrapper {
	position: relative;
	width: 200rpx;
	height: 200rpx;
	border-radius: 50%;
	overflow: hidden;
	border: 6rpx solid rgba(255, 255, 255, 0.3);
	transition: all 0.3s ease;
}

.avatar-wrapper:active {
	transform: scale(0.95);
}

.avatar {
	width: 100%;
	height: 100%;
}

.avatar-overlay {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	opacity: 0;
	transition: all 0.3s ease;
}

.avatar-wrapper:active .avatar-overlay {
	opacity: 1;
}

.camera-icon {
	font-size: 48rpx;
	color: white;
	margin-bottom: 8rpx;
}

.change-text {
	font-size: 22rpx;
	color: white;
	font-weight: 500;
}

/* 通用区块样式 */
.info-section,
.contact-section,
.member-section,
.security-section {
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

/* 基本信息 */
.info-list {
	background: white;
}

.info-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f8f9fa;
	transition: all 0.3s ease;
}

.info-item:last-child {
	border-bottom: none;
}

.info-item:active {
	background: rgba(255, 107, 53, 0.05);
}

.info-label {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	width: 120rpx;
	flex-shrink: 0;
}

.info-value {
	flex: 1;
	font-size: 28rpx;
	color: #666;
	text-align: right;
	margin-right: 12rpx;
}

.info-arrow {
	font-size: 24rpx;
	color: #999;
}

.gender-options {
	flex: 1;
	display: flex;
	justify-content: flex-end;
	gap: 20rpx;
}

.gender-option {
	padding: 12rpx 24rpx;
	border-radius: 20rpx;
	border: 2rpx solid #f0f0f0;
	font-size: 24rpx;
	color: #666;
	transition: all 0.3s ease;
}

.gender-option.active {
	border-color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
	color: #ff6b35;
}

/* 联系方式 */
.contact-list {
	background: white;
}

.contact-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f8f9fa;
}

.contact-item:last-child {
	border-bottom: none;
}

.contact-label {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	width: 120rpx;
	flex-shrink: 0;
}

.contact-value {
	flex: 1;
	font-size: 26rpx;
	color: #666;
}

.contact-status {
	font-size: 22rpx;
	color: #999;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	background: #f0f0f0;
	margin-right: 20rpx;
}

.contact-status.verified {
	background: rgba(82, 196, 26, 0.1);
	color: #52c41a;
}

.contact-action {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 500;
}

/* 会员信息 */
.member-card {
	padding: 30rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.1) 0%, rgba(247, 147, 30, 0.1) 100%);
}

.member-info {
	margin-bottom: 30rpx;
}

.member-level {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
	display: block;
	margin-bottom: 8rpx;
}

.member-id {
	font-size: 24rpx;
	color: #666;
}

.member-stats {
	display: flex;
	gap: 60rpx;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-value {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #666;
}

/* 账户安全 */
.security-list {
	background: white;
}

.security-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f8f9fa;
	transition: all 0.3s ease;
}

.security-item:last-child {
	border-bottom: none;
}

.security-item:active {
	background: rgba(255, 107, 53, 0.05);
}

.security-icon {
	font-size: 32rpx;
	margin-right: 20rpx;
	width: 40rpx;
	text-align: center;
}

.security-content {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.security-text {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 6rpx;
}

.security-desc {
	font-size: 22rpx;
	color: #999;
	line-height: 1.4;
}

.security-arrow {
	font-size: 24rpx;
	color: #999;
	margin-left: 20rpx;
}

/* 昵称编辑弹窗 */
.nickname-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	opacity: 0;
	visibility: hidden;
	transition: all 0.3s ease;
	z-index: 999;
}

.nickname-modal.show {
	opacity: 1;
	visibility: visible;
}

.modal-content {
	background: white;
	border-radius: 24rpx;
	width: 600rpx;
	max-width: 90vw;
	overflow: hidden;
	transform: scale(0.9);
	transition: all 0.3s ease;
}

.nickname-modal.show .modal-content {
	transform: scale(1);
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.modal-close {
	font-size: 32rpx;
	color: #999;
	padding: 8rpx;
}

.modal-body {
	padding: 40rpx 30rpx;
}

.nickname-input {
	width: 100%;
	padding: 24rpx;
	border: 2rpx solid #f0f0f0;
	border-radius: 16rpx;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 16rpx;
}

.nickname-input:focus {
	border-color: #ff6b35;
}

.input-tip {
	font-size: 22rpx;
	color: #999;
}

.modal-footer {
	display: flex;
	border-top: 1rpx solid #f0f0f0;
}

.modal-btn {
	flex: 1;
	padding: 32rpx;
	text-align: center;
	font-size: 28rpx;
	font-weight: 600;
	transition: all 0.3s ease;
}

.modal-btn.cancel {
	color: #666;
	border-right: 1rpx solid #f0f0f0;
}

.modal-btn.confirm {
	color: #ff6b35;
}

.modal-btn:active {
	background: rgba(255, 107, 53, 0.05);
}

/* 修改密码弹窗 */
.password-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	opacity: 0;
	visibility: hidden;
	transition: all 0.3s ease;
	z-index: 999;
}

.password-modal.show {
	opacity: 1;
	visibility: visible;
}

.password-modal .modal-content {
	transform: scale(0.9);
}

.password-modal.show .modal-content {
	transform: scale(1);
}

.password-input {
	width: 100%;
	padding: 24rpx;
	border: 2rpx solid #f0f0f0;
	border-radius: 16rpx;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 20rpx;
}

.password-input:focus {
	border-color: #ff6b35;
}

/* 登录记录弹窗 */
.login-history-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	opacity: 0;
	visibility: hidden;
	transition: all 0.3s ease;
	z-index: 999;
}

.login-history-modal.show {
	opacity: 1;
	visibility: visible;
}

.history-content {
	max-height: 80vh;
}

.history-content .modal-body {
	max-height: 60vh;
	overflow-y: auto;
}

.empty-history {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 80rpx 0;
}

.empty-icon {
	font-size: 80rpx;
	margin-bottom: 20rpx;
	opacity: 0.3;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.history-list {
	padding: 0;
}

.history-item {
	padding: 30rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
	border-bottom: none;
}

.history-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.history-device {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.history-time {
	font-size: 24rpx;
	color: #999;
}

.history-location {
	font-size: 24rpx;
	color: #666;
}

.modal-footer.single {
	border-top: 1rpx solid #f0f0f0;
}

.modal-btn.full {
	width: 100%;
	border-right: none;
}
</style>
