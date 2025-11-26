<template>
	<view class="container">
		<!-- 会员卡片 -->
		<view class="member-card">
			<view class="card-bg" :class="getMemberCardClass(userInfo.memberLevel)">
				<view class="card-header">
					<view class="user-info">
						<image class="avatar" :src="userInfo.avatar" mode="aspectFill" v-if="userInfo.avatar"></image>
						<view class="avatar-placeholder" v-else>
							<text>{{ (userInfo.nickname || '会员').charAt(0) }}</text>
						</view>
						<view class="info-text">
							<text class="nickname">{{ userInfo.nickname || '会员' }}</text>
							<text class="level-text">{{ getMemberLevelText(userInfo.memberLevel) }}</text>
						</view>
					</view>
					<text class="member-no">NO.{{ userInfo.memberNo || '-' }}</text>
				</view>
				
				<view class="card-balance">
					<view class="balance-item">
						<text class="balance-label">余额</text>
						<text class="balance-value">¥{{ formatPrice(userInfo.balance || 0) }}</text>
					</view>
					<view class="balance-item">
						<text class="balance-label">积分</text>
						<text class="balance-value">{{ userInfo.points || 0 }}</text>
					</view>
				</view>
				
				<view class="card-actions">
					<view class="action-btn" @click="goToRecharge">
						<text class="btn-icon">💰</text>
						<text class="btn-text">充值</text>
					</view>
					<view class="action-btn" @click="goToPoints">
						<text class="btn-icon">⭐</text>
						<text class="btn-text">积分明细</text>
					</view>
					<view class="action-btn" @click="goToInvite">
						<text class="btn-icon">🎁</text>
						<text class="btn-text">邀请好友</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 会员等级 -->
		<view class="level-card">
			<view class="level-header">
				<text class="level-title">会员等级</text>
				<text class="level-desc">{{ getLevelProgressDesc() }}</text>
			</view>
			<view class="level-progress">
				<view class="progress-bar">
					<view class="progress-fill" :style="{ width: getLevelProgress() + '%' }"></view>
				</view>
				<view class="level-marks">
					<view class="mark-item" :class="{ active: userInfo.memberLevel >= 0 }">
						<text>普通</text>
					</view>
					<view class="mark-item" :class="{ active: userInfo.memberLevel >= 1 }">
						<text>黄金</text>
					</view>
					<view class="mark-item" :class="{ active: userInfo.memberLevel >= 2 }">
						<text>钻石</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 会员特权 -->
		<view class="privileges-card">
			<view class="card-title">会员特权</view>
			<view class="privileges-grid">
				<view class="privilege-item" v-for="privilege in privileges" :key="privilege.id" :class="{ locked: !isPrivilegeAvailable(privilege.level) }">
					<text class="privilege-icon">{{ privilege.icon }}</text>
					<text class="privilege-name">{{ privilege.name }}</text>
					<text class="privilege-desc">{{ privilege.desc }}</text>
					<view class="privilege-lock" v-if="!isPrivilegeAvailable(privilege.level)">
						<text>{{ getMemberLevelText(privilege.level) }}可用</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 消费统计 -->
		<view class="stats-card">
			<view class="card-title">消费统计</view>
			<view class="stats-grid">
				<view class="stat-item">
					<text class="stat-value">{{ userStats.totalOrders || 0 }}</text>
					<text class="stat-label">订单数</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">¥{{ formatPrice(userStats.totalAmount || 0) }}</text>
					<text class="stat-label">消费金额</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ userStats.totalPoints || 0 }}</text>
					<text class="stat-label">累计积分</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ userStats.inviteCount || 0 }}</text>
					<text class="stat-label">邀请好友</text>
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
			userStats: {},
			privileges: [
				{ id: 1, name: '专属折扣', desc: '会员专享价', icon: '💰', level: 0 },
				{ id: 2, name: '积分翻倍', desc: '双倍积分', icon: '⭐', level: 1 },
				{ id: 3, name: '免配送费', desc: '全场包邮', icon: '🚚', level: 1 },
				{ id: 4, name: '生日特权', desc: '生日礼遇', icon: '🎂', level: 0 },
				{ id: 5, name: '专属客服', desc: '优先服务', icon: '👨‍💼', level: 2 },
				{ id: 6, name: '新品试喝', desc: '优先体验', icon: '🆕', level: 2 }
			]
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadUserStats()
	},
	onShow() {
		this.loadUserInfo()
	},
	methods: {
		async loadUserInfo() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo && userInfo.id) {
					this.userInfo = userInfo
					const res = await api.user.getInfo(userInfo.id)
					if (res.code === 200) {
						this.userInfo = res.data
						uni.setStorageSync('userInfo', this.userInfo)
					}
				}
			} catch (error) {
				console.error('加载用户信息失败:', error)
			}
		},
		async loadUserStats() {
			try {
				if (!this.userInfo.id) return
				const res = await api.member.getStats(this.userInfo.id)
				if (res.code === 200) {
					this.userStats = res.data
				}
			} catch (error) {
				console.error('加载统计数据失败:', error)
			}
		},
		getMemberCardClass(level) {
			return ['normal-card', 'gold-card', 'diamond-card'][level] || 'normal-card'
		},
		getLevelProgressDesc() {
			const points = this.userInfo.points || 0
			const thresholds = [0, 1000, 5000]
			
			// 根据积分判断当前等级
			let currentLevel = 0
			if (points >= 5000) {
				currentLevel = 2
			} else if (points >= 1000) {
				currentLevel = 1
			}
			
			if (currentLevel >= 2) return '已达最高等级'
			
			const remaining = thresholds[currentLevel + 1] - points
			return `还需${remaining}积分升级到${getMemberLevelText(currentLevel + 1)}`
		},
		getLevelProgress() {
			const points = this.userInfo.points || 0
			const thresholds = [0, 1000, 5000]
			
			// 根据积分判断当前等级
			let currentLevel = 0
			if (points >= 5000) {
				currentLevel = 2
			} else if (points >= 1000) {
				currentLevel = 1
			}
			
			if (currentLevel >= 2) return 100
			
			const current = thresholds[currentLevel]
			const next = thresholds[currentLevel + 1]
			return Math.min(Math.max(((points - current) / (next - current)) * 100, 0), 100)
		},
		isPrivilegeAvailable(requiredLevel) {
			// 根据积分实时计算当前等级
			const points = this.userInfo.points || 0
			let currentLevel = 0
			if (points >= 5000) {
				currentLevel = 2 // 钻石会员
			} else if (points >= 1000) {
				currentLevel = 1 // 黄金会员
			}
			return currentLevel >= requiredLevel
		},
		goToRecharge() {
			uni.navigateTo({ url: '/pages/recharge/recharge' })
		},
		goToPoints() {
			uni.navigateTo({ url: '/pages/points/points' })
		},
		goToInvite() {
			uni.navigateTo({ url: '/pages/invite/invite' })
		},
		formatPrice,
		getMemberLevelText
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding: 20rpx;
	padding-bottom: 40rpx;
}

/* 会员卡片 */
.member-card {
	margin-bottom: 20rpx;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
}

.card-bg {
	padding: 40rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
}

.gold-card {
	background: linear-gradient(135deg, #f7931e 0%, #ffd200 100%);
}

.diamond-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	border: 3rpx solid rgba(255, 255, 255, 0.3);
}

.avatar-placeholder {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.2);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	color: white;
	font-weight: 700;
}

.info-text {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.nickname {
	font-size: 32rpx;
	font-weight: 700;
	color: white;
}

.level-text {
	font-size: 22rpx;
	color: rgba(255, 255, 255, 0.9);
	padding: 4rpx 12rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 12rpx;
	align-self: flex-start;
}

.member-no {
	font-size: 20rpx;
	color: rgba(255, 255, 255, 0.7);
	letter-spacing: 2rpx;
}

.card-balance {
	display: flex;
	justify-content: space-around;
	padding: 30rpx 0;
	margin-bottom: 30rpx;
}

.balance-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
}

.balance-label {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.balance-value {
	font-size: 36rpx;
	font-weight: 700;
	color: white;
}

.card-actions {
	display: flex;
	justify-content: space-around;
}

.action-btn {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
	padding: 16rpx 24rpx;
	border-radius: 16rpx;
	background: rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
}

.action-btn:active {
	background: rgba(255, 255, 255, 0.3);
	transform: scale(0.95);
}

.btn-icon {
	font-size: 28rpx;
}

.btn-text {
	font-size: 22rpx;
	color: white;
	font-weight: 500;
}

/* 通用卡片 */
.level-card,
.privileges-card,
.stats-card {
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.card-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 24rpx;
}

/* 等级进度 */
.level-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.level-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.level-desc {
	font-size: 24rpx;
	color: #666;
}

.progress-bar {
	height: 8rpx;
	background: #f0f0f0;
	border-radius: 4rpx;
	overflow: hidden;
	margin-bottom: 20rpx;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #ff6b35, #f7931e);
	border-radius: 4rpx;
	transition: width 0.3s ease;
}

.level-marks {
	display: flex;
	justify-content: space-between;
}

.mark-item {
	font-size: 22rpx;
	color: #999;
	font-weight: 500;
}

.mark-item.active {
	color: #ff6b35;
	font-weight: 600;
}

/* 特权网格 */
.privileges-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
}

.privilege-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 24rpx 16rpx;
	border-radius: 16rpx;
	background: #f8f9fa;
	position: relative;
}

.privilege-item.locked {
	opacity: 0.5;
}

.privilege-icon {
	font-size: 40rpx;
	margin-bottom: 12rpx;
}

.privilege-name {
	font-size: 24rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 6rpx;
}

.privilege-desc {
	font-size: 20rpx;
	color: #999;
	text-align: center;
}

.privilege-lock {
	position: absolute;
	top: 8rpx;
	right: 8rpx;
	font-size: 18rpx;
	color: white;
	background: rgba(0, 0, 0, 0.6);
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

/* 统计网格 */
.stats-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	border-radius: 16rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
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
</style>
