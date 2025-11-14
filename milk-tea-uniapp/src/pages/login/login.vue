<template>
	<view class="container">
		<!-- 背景装饰 -->
		<view class="bg-decoration">
			<view class="circle circle-1"></view>
			<view class="circle circle-2"></view>
			<view class="circle circle-3"></view>
		</view>

		<!-- 登录表单 -->
		<view class="login-form">
			<!-- Logo和标题 -->
			<view class="header">
				<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
				<text class="title">奶茶小程序</text>
				<text class="subtitle">享受美味，享受生活</text>
			</view>

			<!-- 登录方式切换 -->
			<view class="login-tabs">
				<view class="tab-item" :class="{ active: loginType === 'phone' }" @click="switchLoginType('phone')">
					<text>手机登录</text>
				</view>
				<view class="tab-item" :class="{ active: loginType === 'wechat' }" @click="switchLoginType('wechat')">
					<text>微信登录</text>
				</view>
			</view>

			<!-- 手机号登录 -->
			<view class="phone-login" v-if="loginType === 'phone'">
				<view class="input-group">
					<view class="input-item">
						<text class="input-icon">📱</text>
						<input 
							class="input-field" 
							type="number" 
							placeholder="请输入手机号" 
							v-model="phoneForm.phone"
							maxlength="11"
						/>
					</view>
					<view class="input-item">
						<text class="input-icon">🔐</text>
						<input 
							class="input-field" 
							type="number" 
							placeholder="请输入验证码" 
							v-model="phoneForm.code"
							maxlength="6"
						/>
						<view class="code-btn" :class="{ disabled: codeCountdown > 0 }" @click="sendCode">
							<text v-if="codeCountdown > 0">{{ codeCountdown }}s</text>
							<text v-else>获取验证码</text>
						</view>
					</view>
				</view>

				<view class="login-btn" @click="phoneLogin">
					<text>登录</text>
				</view>
			</view>

			<!-- 微信登录 -->
			<view class="wechat-login" v-if="loginType === 'wechat'">
				<view class="wechat-info">
					<text class="wechat-icon">💚</text>
					<text class="wechat-text">使用微信账号快速登录</text>
					<text class="wechat-desc">安全便捷，一键登录</text>
				</view>

				<view class="wechat-btn" @click="wechatLogin">
					<text class="wechat-btn-icon">💚</text>
					<text class="wechat-btn-text">微信一键登录</text>
				</view>
			</view>

			<!-- 协议条款 -->
			<view class="agreement">
				<view class="agreement-check" @click="toggleAgreement">
					<text class="checkbox" :class="{ checked: agreed }">{{ agreed ? '✓' : '' }}</text>
					<text class="agreement-text">
						我已阅读并同意
						<text class="link" @click.stop="showAgreement('privacy')">《隐私政策》</text>
						和
						<text class="link" @click.stop="showAgreement('terms')">《用户协议》</text>
					</text>
				</view>
			</view>

			<!-- 其他登录方式 -->
			<view class="other-login">
				<text class="other-text">其他登录方式</text>
				<view class="other-methods">
					<view class="method-item" @click="guestLogin">
						<text class="method-icon">👤</text>
						<text class="method-text">游客登录</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { validatePhone } from '@/utils/common.js'

export default {
	data() {
		return {
			loginType: 'wechat', // 'phone' | 'wechat'
			phoneForm: {
				phone: '',
				code: ''
			},
			codeCountdown: 0,
			agreed: false
		}
	},
	onLoad(options) {
		// 检查是否已登录
		const token = uni.getStorageSync('token')
		if (token) {
			this.goBack()
		}
	},
	methods: {
		// 切换登录方式
		switchLoginType(type) {
			this.loginType = type
		},

		// 手机号登录
		async phoneLogin() {
			if (!this.validatePhoneForm()) return
			if (!this.agreed) {
				uni.showToast({
					title: '请先同意用户协议',
					icon: 'none'
				})
				return
			}

			try {
				uni.showLoading({ title: '登录中...' })
				
				const res = await api.auth.login({
					phone: this.phoneForm.phone,
					code: this.phoneForm.code
				})

				if (res.code === 200) {
					await this.handleLoginSuccess(res.data)
				}
			} catch (error) {
				console.error('登录失败:', error)
				uni.showToast({
					title: error.message || '登录失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 微信登录
		async wechatLogin() {
			if (!this.agreed) {
				uni.showToast({
					title: '请先同意用户协议',
					icon: 'none'
				})
				return
			}

			try {
				uni.showLoading({ title: '登录中...' })

				// 获取微信登录凭证
				const loginRes = await new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: resolve,
						fail: reject
					})
				})

				// 获取用户信息
				const userInfoRes = await new Promise((resolve, reject) => {
					uni.getUserInfo({
						provider: 'weixin',
						success: resolve,
						fail: reject
					})
				})

				// 调用后端登录接口
				const res = await api.auth.wxLogin({
					code: loginRes.code,
					userInfo: userInfoRes.userInfo,
					rawData: userInfoRes.rawData,
					signature: userInfoRes.signature,
					encryptedData: userInfoRes.encryptedData,
					iv: userInfoRes.iv
				})

				if (res.code === 200) {
					await this.handleLoginSuccess(res.data)
				}
			} catch (error) {
				console.error('微信登录失败:', error)
				uni.showToast({
					title: error.message || '登录失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 游客登录
		guestLogin() {
			if (!this.agreed) {
				uni.showToast({
					title: '请先同意用户协议',
					icon: 'none'
				})
				return
			}

			// 设置游客标识
			uni.setStorageSync('isGuest', true)
			uni.showToast({
				title: '游客登录成功',
				icon: 'success'
			})
			
			setTimeout(() => {
				this.goBack()
			}, 1500)
		},

		// 发送验证码
		async sendCode() {
			if (this.codeCountdown > 0) return
			
			if (!validatePhone(this.phoneForm.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}

			try {
				// 这里应该调用发送验证码接口
				// const res = await api.auth.sendCode({ phone: this.phoneForm.phone })
				
				// 模拟发送成功
				uni.showToast({
					title: '验证码已发送',
					icon: 'success'
				})
				
				this.startCountdown()
			} catch (error) {
				uni.showToast({
					title: error.message || '发送失败',
					icon: 'none'
				})
			}
		},

		// 开始倒计时
		startCountdown() {
			this.codeCountdown = 60
			const timer = setInterval(() => {
				this.codeCountdown--
				if (this.codeCountdown <= 0) {
					clearInterval(timer)
				}
			}, 1000)
		},

		// 验证手机号表单
		validatePhoneForm() {
			if (!validatePhone(this.phoneForm.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return false
			}

			if (!this.phoneForm.code || this.phoneForm.code.length !== 6) {
				uni.showToast({
					title: '请输入6位验证码',
					icon: 'none'
				})
				return false
			}

			return true
		},

		// 处理登录成功
		async handleLoginSuccess(data) {
			// 保存token和用户信息
			uni.setStorageSync('token', data.token)
			uni.setStorageSync('userInfo', data.userInfo)
			uni.removeStorageSync('isGuest')

			uni.showToast({
				title: '登录成功',
				icon: 'success'
			})

			setTimeout(() => {
				this.goBack()
			}, 1500)
		},

		// 切换协议同意状态
		toggleAgreement() {
			this.agreed = !this.agreed
		},

		// 显示协议内容
		showAgreement(type) {
			const title = type === 'privacy' ? '隐私政策' : '用户协议'
			const content = type === 'privacy' 
				? '我们重视您的隐私，会严格保护您的个人信息...'
				: '欢迎使用奶茶小程序，请仔细阅读以下条款...'

			uni.showModal({
				title,
				content,
				showCancel: false,
				confirmText: '我知道了'
			})
		},

		// 返回上一页
		goBack() {
			const pages = getCurrentPages()
			if (pages.length > 1) {
				uni.navigateBack()
			} else {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 50%, #ff8c42 100%);
	position: relative;
	overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	pointer-events: none;
}

.circle {
	position: absolute;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.1);
	backdrop-filter: blur(10rpx);
}

.circle-1 {
	width: 400rpx;
	height: 400rpx;
	top: -200rpx;
	right: -200rpx;
	animation: float 6s ease-in-out infinite;
}

.circle-2 {
	width: 300rpx;
	height: 300rpx;
	bottom: -150rpx;
	left: -150rpx;
	animation: float 8s ease-in-out infinite reverse;
}

.circle-3 {
	width: 200rpx;
	height: 200rpx;
	top: 50%;
	left: -100rpx;
	animation: float 10s ease-in-out infinite;
}

@keyframes float {
	0%, 100% { transform: translateY(0px) rotate(0deg); }
	50% { transform: translateY(-20px) rotate(180deg); }
}

/* 登录表单 */
.login-form {
	position: relative;
	z-index: 1;
	padding: 80rpx 40rpx 40rpx;
	min-height: 100vh;
	display: flex;
	flex-direction: column;
}

/* 头部 */
.header {
	text-align: center;
	margin-bottom: 80rpx;
}

.logo {
	width: 120rpx;
	height: 120rpx;
	margin-bottom: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
}

.title {
	display: block;
	font-size: 48rpx;
	font-weight: 700;
	color: white;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
	margin-bottom: 16rpx;
}

.subtitle {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
	font-weight: 500;
}

/* 登录方式切换 */
.login-tabs {
	display: flex;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 50rpx;
	padding: 8rpx;
	margin-bottom: 60rpx;
	backdrop-filter: blur(20rpx);
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 20rpx;
	border-radius: 42rpx;
	transition: all 0.3s ease;
}

.tab-item.active {
	background: rgba(255, 255, 255, 0.9);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.tab-item text {
	font-size: 28rpx;
	font-weight: 600;
	color: rgba(255, 255, 255, 0.8);
}

.tab-item.active text {
	color: #ff6b35;
}

/* 手机号登录 */
.phone-login {
	flex: 1;
}

.input-group {
	margin-bottom: 60rpx;
}

.input-item {
	display: flex;
	align-items: center;
	background: rgba(255, 255, 255, 0.95);
	border-radius: 50rpx;
	padding: 24rpx 32rpx;
	margin-bottom: 30rpx;
	backdrop-filter: blur(20rpx);
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.3);
}

.input-icon {
	font-size: 32rpx;
	margin-right: 20rpx;
	color: #ff6b35;
}

.input-field {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	background: transparent;
	border: none;
	outline: none;
}

.input-field::placeholder {
	color: #999;
}

.code-btn {
	padding: 12rpx 24rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	border-radius: 24rpx;
	font-size: 24rpx;
	font-weight: 600;
	transition: all 0.3s ease;
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.3);
}

.code-btn.disabled {
	background: #ccc;
	box-shadow: none;
}

.code-btn:active:not(.disabled) {
	transform: scale(0.95);
}

.login-btn {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50rpx;
	padding: 32rpx;
	text-align: center;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.4);
	transition: all 0.3s ease;
	border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.login-btn:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.5);
}

.login-btn text {
	font-size: 32rpx;
	font-weight: 700;
	color: white;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

/* 微信登录 */
.wechat-login {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.wechat-info {
	text-align: center;
	margin-bottom: 80rpx;
}

.wechat-icon {
	font-size: 120rpx;
	display: block;
	margin-bottom: 30rpx;
	filter: drop-shadow(0 4rpx 16rpx rgba(0, 0, 0, 0.2));
}

.wechat-text {
	display: block;
	font-size: 32rpx;
	font-weight: 600;
	color: white;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
	margin-bottom: 16rpx;
}

.wechat-desc {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.wechat-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(255, 255, 255, 0.95);
	border-radius: 50rpx;
	padding: 32rpx 60rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	backdrop-filter: blur(20rpx);
	border: 2rpx solid rgba(255, 255, 255, 0.3);
	transition: all 0.3s ease;
}

.wechat-btn:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.2);
}

.wechat-btn-icon {
	font-size: 40rpx;
	margin-right: 16rpx;
}

.wechat-btn-text {
	font-size: 32rpx;
	font-weight: 700;
	color: #07c160;
}

/* 协议条款 */
.agreement {
	margin: 40rpx 0;
}

.agreement-check {
	display: flex;
	align-items: flex-start;
}

.checkbox {
	width: 32rpx;
	height: 32rpx;
	border: 2rpx solid rgba(255, 255, 255, 0.6);
	border-radius: 6rpx;
	margin-right: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	color: white;
	font-weight: bold;
	transition: all 0.3s ease;
	flex-shrink: 0;
	margin-top: 4rpx;
}

.checkbox.checked {
	background: rgba(255, 255, 255, 0.9);
	color: #ff6b35;
	border-color: rgba(255, 255, 255, 0.9);
}

.agreement-text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	line-height: 1.5;
}

.link {
	color: white;
	text-decoration: underline;
	font-weight: 600;
}

/* 其他登录方式 */
.other-login {
	margin-top: auto;
	padding-top: 40rpx;
	text-align: center;
}

.other-text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.6);
	margin-bottom: 30rpx;
	display: block;
}

.other-methods {
	display: flex;
	justify-content: center;
}

.method-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 16rpx;
	background: rgba(255, 255, 255, 0.1);
	backdrop-filter: blur(10rpx);
	transition: all 0.3s ease;
}

.method-item:active {
	background: rgba(255, 255, 255, 0.2);
	transform: scale(0.95);
}

.method-icon {
	font-size: 40rpx;
	margin-bottom: 8rpx;
}

.method-text {
	font-size: 22rpx;
	color: rgba(255, 255, 255, 0.8);
	font-weight: 500;
}
</style>
