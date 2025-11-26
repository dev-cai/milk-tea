<template>
	<view class="container">
		<!-- 顶部背景 -->
		<view class="header-bg">
			<view class="header-content">
				<text class="header-title">邀请好友</text>
				<text class="header-subtitle">邀请好友注册，双方都有奖励</text>
			</view>
		</view>

		<!-- 填写邀请码提示 -->
		<view class="bind-invite-tip" v-if="!userInfo.inviterId" @click="openBindDialog">
			<view class="tip-icon-box">
				<u-icon name="gift" size="24" color="#ff6b35"></u-icon>
			</view>
			<view class="tip-content">
				<text class="tip-title">填写邀请码</text>
				<text class="tip-desc">填写好友邀请码，立得50积分</text>
			</view>
			<u-icon name="arrow-right" size="18" color="#ff8f00"></u-icon>
		</view>

		<!-- 邀请码卡片 -->
		<view class="invite-card">
			<view class="card-header">
				<text class="card-title">我的邀请码</text>
				<view class="reward-badge">
					<u-icon name="integral" size="16" color="#fff"></u-icon>
					<text class="reward-text">双方各得50积分</text>
				</view>
			</view>
			
			<view class="invite-code-section">
				<view class="code-display">
					<text class="code-text">{{ inviteCode }}</text>
				</view>
				<view class="code-actions">
					<view class="action-btn" @click="copyInviteCode">
						<u-icon name="file-text" size="20" color="#666"></u-icon>
						<text class="btn-text">复制</text>
					</view>
					<view class="action-btn primary" @click="shareInvite">
						<u-icon name="share" size="20" color="#fff"></u-icon>
						<text class="btn-text">分享</text>
					</view>
				</view>
			</view>


		</view>

		<!-- 邀请统计 -->
		<view class="stats-section">
			<view class="section-header">
				<text class="section-title">邀请统计</text>
			</view>
			<view class="stats-grid">
				<view class="stat-item">
					<text class="stat-number">{{ inviteStats.totalInvites }}</text>
					<text class="stat-label">累计邀请</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{ inviteStats.successInvites }}</text>
					<text class="stat-label">成功注册</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{ inviteStats.totalRewards }}</text>
					<text class="stat-label">获得积分</text>
				</view>
			</view>
		</view>

		<!-- 邀请奖励规则 -->
		<view class="rules-section">
			<view class="section-header">
				<text class="section-title">奖励规则</text>
			</view>
			<view class="rules-list">
				<view class="rule-item">
					<view class="rule-number">1</view>
					<view class="rule-content">
						<text class="rule-title">邀请注册</text>
						<text class="rule-desc">好友通过您的邀请码注册，双方各得50积分</text>
					</view>
				</view>
				<view class="rule-item">
					<view class="rule-number">2</view>
					<view class="rule-content">
						<text class="rule-title">首单奖励</text>
						<text class="rule-desc">好友完成首单，您额外获得100积分</text>
					</view>
				</view>
				<view class="rule-item">
					<view class="rule-number">3</view>
					<view class="rule-content">
						<text class="rule-title">持续奖励</text>
						<text class="rule-desc">好友每次消费，您可获得其消费金额5%的积分</text>
					</view>
				</view>
				<view class="rule-item">
					<view class="rule-number">4</view>
					<view class="rule-content">
						<text class="rule-title">优惠券奖励</text>
						<text class="rule-desc">邀请满5人，赠送20元优惠券</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 邀请记录 -->
		<view class="records-section">
			<view class="section-header">
				<text class="section-title">邀请记录</text>
				<text class="section-more" @click="viewAllRecords">查看全部 ›</text>
			</view>
			<view class="records-list" v-if="inviteRecords.length > 0">
				<view class="record-item" v-for="record in inviteRecords" :key="record.id">
					<view class="record-avatar">
						<image class="avatar" :src="record.avatar || 'https://via.placeholder.com/100x100/CCCCCC/666666?text=User'" mode="aspectFill"></image>
					</view>
					<view class="record-info">
						<text class="record-name">{{ record.nickname }}</text>
						<text class="record-time">{{ formatTime(record.registerTime) }}</text>
					</view>
					<view class="record-status" :class="record.status">
						<text>{{ getStatusText(record.status) }}</text>
					</view>
					<view class="record-reward" v-if="record.reward > 0">
						<text class="reward-amount">+{{ record.reward }}</text>
						<text class="reward-unit">积分</text>
					</view>
				</view>
			</view>
			<view class="records-empty" v-else>
				<u-icon name="account" size="80" color="#ddd"></u-icon>
				<text class="empty-text">还没有邀请记录</text>
				<text class="empty-desc">快去邀请好友吧</text>
			</view>
		</view>

		<!-- 填写邀请码弹窗 -->
		<u-popup :show="showBindDialog" @close="showBindDialog = false" mode="center" :round="20">
			<view class="bind-dialog">
				<view class="dialog-header">
					<text class="dialog-title">填写邀请码</text>
					<u-icon name="close" @click="showBindDialog = false"></u-icon>
				</view>
				<view class="dialog-content">
					<text class="dialog-desc">填写好友的邀请码，双方各得50积分</text>
					<input 
						class="invite-input" 
						v-model="inputInviteCode" 
						placeholder="请输入6位邀请码"
						maxlength="6"
						:adjust-position="false"
					/>
					<view class="dialog-tip">
						<text>💡 每个账号只能填写一次邀请码</text>
					</view>
				</view>
				<view class="dialog-actions">
					<view class="dialog-btn cancel" @click="showBindDialog = false">
						<text>取消</text>
					</view>
					<view class="dialog-btn confirm" @click="bindInviteCode">
						<text>确认</text>
					</view>
				</view>
			</view>
		</u-popup>

		<!-- 分享海报弹窗 -->
		<view class="poster-modal" v-if="showPoster" @click="closePoster">
			<view class="poster-content" @click.stop>
				<view class="poster-header">
					<text class="poster-title">分享海报</text>
					<text class="poster-close" @click="closePoster">✕</text>
				</view>
				<view class="poster-canvas">
					<canvas canvas-id="invitePoster" class="canvas"></canvas>
				</view>
				<view class="poster-actions">
					<view class="poster-btn" @click="savePoster">
						<text>保存图片</text>
					</view>
					<view class="poster-btn primary" @click="sharePoster">
						<text>分享好友</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { formatTime } from '@/utils/common.js'

export default {
	data() {
		return {
			userInfo: {},
			inviteCode: '',
			inviteStats: {
				totalInvites: 0,
				successInvites: 0,
				totalRewards: 0
			},
			inviteRecords: [],
			showPoster: false,
			showBindDialog: false,
			inputInviteCode: ''
		}
	},
	
	onLoad() {
		this.loadUserInfo()
		this.loadInviteData()
	},
	
	methods: {
		formatTime,
		
		// 加载用户信息
		async loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo) {
				this.userInfo = userInfo
				// 从后端获取完整用户信息（包含inviterId）
				await this.loadFullUserInfo()
				// 从后端获取邀请码
				await this.loadInviteCode()
			} else {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			}
		},
		
		// 获取完整用户信息
		async loadFullUserInfo() {
			try {
				const res = await api.user.getInfo(this.userInfo.id)
				if (res.code === 200 && res.data) {
					// 更新userInfo，特别是inviterId字段
					this.userInfo = {
						...this.userInfo,
						...res.data
					}
					// 同时更新本地存储
					uni.setStorageSync('userInfo', this.userInfo)
				}
			} catch (error) {
				console.error('获取用户信息失败:', error)
			}
		},
		
		// 获取邀请码
		async loadInviteCode() {
			try {
				const res = await api.invite.getCode(this.userInfo.id)
				if (res.code === 200) {
					this.inviteCode = res.data
				}
			} catch (error) {
				console.error('获取邀请码失败:', error)
				// 使用临时邀请码
				this.inviteCode = this.generateInviteCode(this.userInfo.id)
			}
		},
		
		// 打开填写邀请码对话框
		openBindDialog() {
			console.log('点击了填写邀请码提示')
			console.log('userInfo.inviterId:', this.userInfo.inviterId)
			console.log('showBindDialog 设置为 true')
			this.showBindDialog = true
			console.log('showBindDialog 当前值:', this.showBindDialog)
		},
		
		// 绑定邀请码
		async bindInviteCode() {
			if (!this.inputInviteCode) {
				uni.showToast({
					title: '请输入邀请码',
					icon: 'none'
				})
				return
			}
			
			if (this.inputInviteCode.length !== 6) {
				uni.showToast({
					title: '邀请码格式不正确',
					icon: 'none'
				})
				return
			}
			
			try {
				const res = await api.invite.bind(this.userInfo.id, this.inputInviteCode)
				if (res.code === 200) {
					uni.showToast({
						title: '绑定成功',
						icon: 'success'
					})
					this.showBindDialog = false
					this.inputInviteCode = ''
					// 重新加载用户信息
					await this.loadFullUserInfo()
					// 刷新数据
					this.loadInviteData()
				} else {
					uni.showToast({
						title: res.message || '绑定失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('绑定邀请码失败:', error)
				uni.showToast({
					title: error.message || '绑定失败',
					icon: 'none'
				})
			}
		},
		
		// 生成邀请码
		generateInviteCode(userId) {
			// 简单的邀请码生成逻辑：用户ID转36进制 + 随机字符
			const base = userId.toString(36).toUpperCase()
			const random = Math.random().toString(36).substring(2, 6).toUpperCase()
			return base + random
		},
		
		// 加载邀请数据
		async loadInviteData() {
			try {
				// 调用后端API获取邀请统计
				const res = await api.member.getInfo(this.userInfo.id)
				if (res.code === 200 && res.data) {
					this.inviteStats = {
						totalInvites: res.data.inviteCount || 0,
						successInvites: res.data.successInvites || 0,
						totalRewards: res.data.inviteRewards || 0
					}
				}
			} catch (error) {
				console.error('加载邀请数据失败:', error)
				// 使用模拟数据
				this.inviteStats = {
					totalInvites: 3,
					successInvites: 2,
					totalRewards: 150
				}
			}
			
			// 加载邀请记录
			this.loadInviteRecords()
		},
		
		// 加载邀请记录
		async loadInviteRecords() {
			try {
				const res = await api.invite.getMyInvites(this.userInfo.id)
				if (res.code === 200) {
					this.inviteRecords = (res.data || []).map(record => ({
						id: record.id,
						nickname: `用户${record.inviteeId}`,
						avatar: '',
						registerTime: record.createTime,
						status: record.status === 2 ? 'completed' : (record.status === 1 ? 'registered' : 'pending'),
						reward: record.points || 0
					}))
					
					// 更新统计数据
					this.inviteStats.totalInvites = this.inviteRecords.length
					this.inviteStats.successInvites = this.inviteRecords.filter(r => r.status === 'completed').length
					this.inviteStats.totalRewards = this.inviteRecords.reduce((sum, r) => sum + r.reward, 0)
				}
			} catch (error) {
				console.error('加载邀请记录失败:', error)
				this.inviteRecords = []
			}
		},
		
		// 复制邀请码
		copyInviteCode() {
			uni.setClipboardData({
				data: this.inviteCode,
				success: () => {
					uni.showToast({
						title: '邀请码已复制',
						icon: 'success'
					})
				}
			})
		},
		

		
		// 分享邀请
		shareInvite() {
			// 微信小程序使用转发
			uni.showShareMenu({
				withShareTicket: true,
				success: () => {
					uni.showToast({
						title: '请点击右上角分享',
						icon: 'none'
					})
				}
			})
			
			// 或者显示海报
			// this.showPoster = true
			// this.generatePoster()
		},
		
		// 生成分享海报
		generatePoster() {
			// 这里可以使用canvas生成分享海报
			// 包含邀请码、二维码等信息
			console.log('生成分享海报')
		},
		
		// 关闭海报
		closePoster() {
			this.showPoster = false
		},
		
		// 保存海报
		savePoster() {
			uni.showToast({
				title: '保存成功',
				icon: 'success'
			})
			this.closePoster()
		},
		
		// 分享海报
		sharePoster() {
			uni.showToast({
				title: '请点击右上角分享',
				icon: 'none'
			})
		},
		
		// 查看全部记录
		viewAllRecords() {
			uni.showToast({
				title: '查看全部记录',
				icon: 'none'
			})
		},
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				'pending': '待注册',
				'registered': '已注册',
				'completed': '已完成',
				'rewarded': '已奖励'
			}
			return statusMap[status] || '未知'
		}
	},
	
	// 分享配置
	onShareAppMessage() {
		return {
			title: `我在奶茶小程序发现了好喝的奶茶，邀请你一起来！`,
			path: `/pages/login/login?inviteCode=${this.inviteCode}`,
			imageUrl: '/static/share-image.jpg'
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #ff6b35 0%, #f5f5f5 300rpx);
	padding-bottom: 40rpx;
}

/* 顶部背景 */
.header-bg {
	padding: 60rpx 40rpx 40rpx;
	color: white;
}

.header-content {
	text-align: center;
}

.header-title {
	display: block;
	font-size: 48rpx;
	font-weight: 700;
	margin-bottom: 16rpx;
}

.header-subtitle {
	display: block;
	font-size: 28rpx;
	opacity: 0.9;
}

/* 填写邀请码提示 */
.bind-invite-tip {
	margin: 0 30rpx 20rpx;
	background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
	border-radius: 16rpx;
	padding: 24rpx 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 4rpx 16rpx rgba(255, 152, 0, 0.15);
	position: relative;
	z-index: 10;
	cursor: pointer;
}

.tip-icon-box {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.tip-content {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.tip-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #ff6b00;
	margin-bottom: 4rpx;
}

.tip-desc {
	font-size: 24rpx;
	color: #ff8f00;
}



/* 填写邀请码弹窗 */
.bind-dialog {
	width: 600rpx;
	background: white;
	border-radius: 24rpx;
	overflow: hidden;
}

.dialog-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 40rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.dialog-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.dialog-content {
	padding: 40rpx;
}

.dialog-desc {
	display: block;
	font-size: 26rpx;
	color: #666;
	margin-bottom: 30rpx;
	text-align: center;
}

.invite-input {
	width: 100%;
	height: 88rpx;
	background: #f5f5f5;
	border-radius: 12rpx;
	padding: 0 30rpx;
	font-size: 32rpx;
	font-weight: 600;
	text-align: center;
	letter-spacing: 8rpx;
	color: #333;
}

.dialog-tip {
	margin-top: 20rpx;
	text-align: center;
	font-size: 22rpx;
	color: #999;
}

.dialog-actions {
	display: flex;
	border-top: 1rpx solid #f0f0f0;
}

.dialog-btn {
	flex: 1;
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 30rpx;
	font-weight: 600;
}

.dialog-btn.cancel {
	color: #999;
	border-right: 1rpx solid #f0f0f0;
}

.dialog-btn.confirm {
	color: #ff6b35;
}

/* 邀请码卡片 */
.invite-card {
	margin: 0 30rpx 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.reward-badge {
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
}



.reward-text {
	font-size: 22rpx;
	color: white;
	font-weight: 600;
}

/* 邀请码显示 */
.invite-code-section {
	margin-bottom: 30rpx;
}

.code-display {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 40rpx;
	border-radius: 16rpx;
	text-align: center;
	margin-bottom: 20rpx;
}

.code-text {
	font-size: 56rpx;
	font-weight: 700;
	color: white;
	letter-spacing: 8rpx;
}

.code-actions {
	display: flex;
	gap: 20rpx;
}

.action-btn {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 24rpx;
	background: #f5f5f5;
	border-radius: 12rpx;
	transition: all 0.3s;
}

.action-btn.primary {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
}

.action-btn.primary .btn-text {
	color: white;
}



.btn-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #666;
}

/* 统计区域 */
.stats-section {
	margin: 0 30rpx 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.section-more {
	font-size: 26rpx;
	color: #999;
}

.stats-grid {
	display: flex;
	justify-content: space-around;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-number {
	font-size: 48rpx;
	font-weight: 700;
	color: #ff6b35;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #999;
}

/* 规则列表 */
.rules-section {
	margin: 0 30rpx 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.rules-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

.rule-item {
	display: flex;
	align-items: flex-start;
}

.rule-number {
	width: 48rpx;
	height: 48rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 28rpx;
	font-weight: 600;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.rule-content {
	flex: 1;
}

.rule-title {
	display: block;
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
}

.rule-desc {
	display: block;
	font-size: 24rpx;
	color: #999;
	line-height: 1.6;
}

/* 邀请记录 */
.records-section {
	margin: 0 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.records-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

.record-item {
	display: flex;
	align-items: center;
}

.record-avatar {
	width: 80rpx;
	height: 80rpx;
	margin-right: 20rpx;
}

.avatar {
	width: 100%;
	height: 100%;
	border-radius: 50%;
}

.record-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.record-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
}

.record-time {
	font-size: 22rpx;
	color: #999;
}

.record-status {
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	font-size: 22rpx;
	margin-right: 20rpx;
}

.record-status.pending {
	background: #f0f0f0;
	color: #999;
}

.record-status.completed {
	background: #e6f7ff;
	color: #1890ff;
}

.record-reward {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
}

.reward-amount {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
}

.reward-unit {
	font-size: 20rpx;
	color: #999;
}

/* 空状态 */
.records-empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 80rpx 0;
}



.empty-text {
	font-size: 28rpx;
	color: #999;
	margin-bottom: 8rpx;
}

.empty-desc {
	font-size: 24rpx;
	color: #ccc;
}

/* 海报弹窗 */
.poster-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.7);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 1000;
}

.poster-content {
	width: 600rpx;
	background: white;
	border-radius: 24rpx;
	overflow: hidden;
}

.poster-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 40rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.poster-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.poster-close {
	font-size: 40rpx;
	color: #999;
}

.poster-canvas {
	padding: 40rpx;
}

.canvas {
	width: 520rpx;
	height: 800rpx;
	background: #f5f5f5;
}

.poster-actions {
	display: flex;
	gap: 20rpx;
	padding: 30rpx 40rpx;
	border-top: 1rpx solid #f0f0f0;
}

.poster-btn {
	flex: 1;
	padding: 24rpx;
	text-align: center;
	background: #f5f5f5;
	border-radius: 12rpx;
	font-size: 28rpx;
	font-weight: 600;
	color: #666;
}

.poster-btn.primary {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
}
</style>
