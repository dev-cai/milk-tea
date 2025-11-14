<template>
	<view class="container">
		<!-- 头像区域 -->
		<view class="avatar-section">
			<view class="avatar-wrapper" @click="changeAvatar">
				<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill"></image>
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
					<text class="info-arrow">></text>
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
				<view class="info-item" @click="editBirthday">
					<text class="info-label">生日</text>
					<text class="info-value">{{ userInfo.birthday || '未设置' }}</text>
					<text class="info-arrow">></text>
				</view>
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
					<view class="contact-status" :class="{ verified: userInfo.phoneVerified }">
						<text>{{ userInfo.phoneVerified ? '已验证' : '未验证' }}</text>
					</view>
					<text class="contact-action" @click="bindPhone">
						{{ userInfo.phone ? '更换' : '绑定' }}
					</text>
				</view>
				<view class="contact-item">
					<text class="contact-label">微信</text>
					<text class="contact-value">{{ userInfo.wechatNickname || '未绑定' }}</text>
					<view class="contact-status" :class="{ verified: userInfo.wechatBound }">
						<text>{{ userInfo.wechatBound ? '已绑定' : '未绑定' }}</text>
					</view>
					<text class="contact-action" @click="bindWechat">
						{{ userInfo.wechatBound ? '解绑' : '绑定' }}
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
				<view class="security-item" @click="changePassword">
					<text class="security-icon">🔒</text>
					<text class="security-text">修改密码</text>
					<text class="security-desc">定期更换密码保护账户安全</text>
					<text class="security-arrow">></text>
				</view>
				<view class="security-item" @click="showLoginHistory">
					<text class="security-icon">📱</text>
					<text class="security-text">登录记录</text>
					<text class="security-desc">查看最近登录设备和时间</text>
					<text class="security-arrow">></text>
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

		<!-- 生日选择器 -->
		<picker 
			mode="date" 
			:value="userInfo.birthday" 
			@change="onBirthdayChange"
			:disabled="false"
			ref="birthdayPicker"
		>
		</picker>
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
			showBirthdayPicker: false
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
				if (userInfo) {
					this.userInfo = userInfo
				}

				// 从服务器获取最新信息
				if (userInfo && userInfo.id) {
					const res = await api.user.getInfo(userInfo.id)
					if (res.code === 200) {
						this.userInfo = res.data
						uni.setStorageSync('userInfo', this.userInfo)
					}
				}
			} catch (error) {
				console.error('加载用户信息失败:', error)
				// 使用模拟数据
				this.userInfo = {
					id: 1,
					nickname: '奶茶爱好者',
					avatar: '/static/default-avatar.png',
					gender: 1,
					birthday: '1990-01-01',
					phone: '13800138000',
					phoneVerified: true,
					wechatNickname: '微信用户',
					wechatBound: true,
					memberLevel: 1,
					memberNo: 'MT202411130001',
					points: 1250,
					balance: 68.50
				}
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
				
				// 这里应该调用上传API
				// const res = await api.user.uploadAvatar(filePath)
				// this.userInfo.avatar = res.data.url
				
				// 模拟上传成功
				this.userInfo.avatar = filePath
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
				// 这里应该调用API更新昵称
				// await api.user.updateNickname(nickname)
				
				this.userInfo.nickname = nickname
				uni.setStorageSync('userInfo', this.userInfo)
				
				this.hideNicknameModal()
				uni.showToast({
					title: '昵称更新成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('更新昵称失败:', error)
				// 模拟更新成功
				this.userInfo.nickname = nickname
				uni.setStorageSync('userInfo', this.userInfo)
				this.hideNicknameModal()
				uni.showToast({
					title: '昵称更新成功',
					icon: 'success'
				})
			}
		},

		// 选择性别
		async selectGender(gender) {
			if (this.userInfo.gender === gender) return

			try {
				// 这里应该调用API更新性别
				// await api.user.updateGender(gender)
				
				this.userInfo.gender = gender
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.showToast({
					title: '性别更新成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('更新性别失败:', error)
				// 模拟更新成功
				this.userInfo.gender = gender
				uni.setStorageSync('userInfo', this.userInfo)
				uni.showToast({
					title: '性别更新成功',
					icon: 'success'
				})
			}
		},

		// 编辑生日
		editBirthday() {
			uni.showActionSheet({
				itemList: ['选择生日'],
				success: () => {
					// 触发日期选择器
					this.$refs.birthdayPicker.$el.click()
				}
			})
		},

		// 生日改变
		async onBirthdayChange(e) {
			const birthday = e.detail.value
			
			try {
				// 这里应该调用API更新生日
				// await api.user.updateBirthday(birthday)
				
				this.userInfo.birthday = birthday
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.showToast({
					title: '生日更新成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('更新生日失败:', error)
				// 模拟更新成功
				this.userInfo.birthday = birthday
				uni.setStorageSync('userInfo', this.userInfo)
				uni.showToast({
					title: '生日更新成功',
					icon: 'success'
				})
			}
		},

		// 绑定手机号
		bindPhone() {
			uni.navigateTo({
				url: '/pages/bind-phone/bind-phone'
			})
		},

		// 绑定微信
		bindWechat() {
			if (this.userInfo.wechatBound) {
				// 解绑微信
				uni.showModal({
					title: '解绑微信',
					content: '确定要解绑微信吗？解绑后将无法使用微信登录。',
					success: (res) => {
						if (res.confirm) {
							this.unbindWechat()
						}
					}
				})
			} else {
				// 绑定微信
				this.performWechatBind()
			}
		},

		// 执行微信绑定
		async performWechatBind() {
			try {
				uni.showLoading({ title: '绑定中...' })
				
				// 这里应该调用微信授权API
				// const res = await api.user.bindWechat()
				
				// 模拟绑定成功
				this.userInfo.wechatBound = true
				this.userInfo.wechatNickname = '微信用户'
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.showToast({
					title: '微信绑定成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('微信绑定失败:', error)
				uni.showToast({
					title: '绑定失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 解绑微信
		async unbindWechat() {
			try {
				uni.showLoading({ title: '解绑中...' })
				
				// 这里应该调用解绑API
				// await api.user.unbindWechat()
				
				// 模拟解绑成功
				this.userInfo.wechatBound = false
				this.userInfo.wechatNickname = ''
				uni.setStorageSync('userInfo', this.userInfo)
				
				uni.showToast({
					title: '微信解绑成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('微信解绑失败:', error)
				uni.showToast({
					title: '解绑失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 修改密码
		changePassword() {
			uni.showToast({
				title: '功能开发中',
				icon: 'none'
			})
		},

		// 显示登录记录
		showLoginHistory() {
			uni.showModal({
				title: '登录记录',
				content: '最近登录：\n2024-11-13 18:30 iPhone\n2024-11-12 09:15 Android\n2024-11-11 20:45 小程序',
				showCancel: false,
				confirmText: '确定'
			})
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

.security-text {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 6rpx;
}

.security-desc {
	flex: 1;
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
</style>
