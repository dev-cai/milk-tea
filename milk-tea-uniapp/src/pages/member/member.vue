<template>
	<view class="container">
		<!-- 会员卡片 -->
		<view class="member-card">
			<view class="card-bg" :class="getMemberCardClass(userInfo.memberLevel)">
				<view class="card-header">
					<view class="member-info">
						<text class="member-level">{{ getMemberLevelText(userInfo.memberLevel) }}</text>
						<text class="member-name">{{ userInfo.nickname || '会员' }}</text>
					</view>
					<view class="member-avatar">
						<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill"></image>
					</view>
				</view>
				
				<view class="card-content">
					<view class="balance-info">
						<text class="balance-label">账户余额</text>
						<text class="balance-amount">¥{{ formatPrice(userInfo.balance || 0) }}</text>
					</view>
					<view class="points-info">
						<text class="points-label">积分</text>
						<text class="points-amount">{{ userInfo.points || 0 }}</text>
					</view>
				</view>
				
				<view class="card-actions">
					<view class="action-item" @click="goToRecharge">
						<text class="action-icon">💰</text>
						<text class="action-text">充值</text>
					</view>
					<view class="action-item" @click="goToPoints">
						<text class="action-icon">🎯</text>
						<text class="action-text">积分明细</text>
					</view>
					<view class="action-item" @click="goToInvite">
						<text class="action-icon">👥</text>
						<text class="action-text">邀请好友</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 会员等级进度 -->
		<view class="level-progress">
			<view class="progress-header">
				<text class="progress-title">会员等级</text>
				<text class="progress-desc">{{ getLevelProgressDesc(userInfo.memberLevel, userInfo.totalAmount || 0) }}</text>
			</view>
			<view class="progress-bar">
				<view class="progress-track">
					<view class="progress-fill" :style="{ width: getLevelProgress(userInfo.memberLevel, userInfo.totalAmount || 0) + '%' }"></view>
				</view>
				<view class="level-markers">
					<view class="level-marker" :class="{ active: userInfo.memberLevel >= 0 }">
						<text class="marker-text">普通</text>
					</view>
					<view class="level-marker" :class="{ active: userInfo.memberLevel >= 1 }">
						<text class="marker-text">黄金</text>
					</view>
					<view class="level-marker" :class="{ active: userInfo.memberLevel >= 2 }">
						<text class="marker-text">钻石</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 会员特权 -->
		<view class="privileges-section">
			<view class="section-header">
				<text class="section-title">会员特权</text>
			</view>
			<view class="privileges-grid">
				<view class="privilege-item" v-for="privilege in memberPrivileges" :key="privilege.id">
					<view class="privilege-icon" :class="{ available: isPrivilegeAvailable(privilege.level) }">
						<text>{{ privilege.icon }}</text>
					</view>
					<text class="privilege-name">{{ privilege.name }}</text>
					<text class="privilege-desc">{{ privilege.desc }}</text>
					<view class="privilege-level" v-if="privilege.level > userInfo.memberLevel">
						<text>{{ getMemberLevelText(privilege.level) }}专享</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 会员福利 -->
		<view class="benefits-section">
			<view class="section-header">
				<text class="section-title">会员福利</text>
				<text class="section-more" @click="goToCoupon">查看更多 ></text>
			</view>
			<view class="benefits-list">
				<view class="benefit-item" v-for="benefit in memberBenefits" :key="benefit.id" @click="claimBenefit(benefit)">
					<view class="benefit-icon">
						<text>{{ benefit.icon }}</text>
					</view>
					<view class="benefit-info">
						<text class="benefit-name">{{ benefit.name }}</text>
						<text class="benefit-desc">{{ benefit.desc }}</text>
					</view>
					<view class="benefit-action">
						<text class="action-btn" :class="{ claimed: benefit.claimed }">
							{{ benefit.claimed ? '已领取' : '立即领取' }}
						</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 消费统计 -->
		<view class="statistics-section">
			<view class="section-header">
				<text class="section-title">消费统计</text>
			</view>
			<view class="stats-grid">
				<view class="stat-item">
					<text class="stat-number">{{ userStats.totalOrders || 0 }}</text>
					<text class="stat-label">总订单数</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">¥{{ formatPrice(userStats.totalAmount || 0) }}</text>
					<text class="stat-label">总消费金额</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{ userStats.totalPoints || 0 }}</text>
					<text class="stat-label">累计积分</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{ userStats.inviteCount || 0 }}</text>
					<text class="stat-label">邀请好友</text>
				</view>
			</view>
		</view>

		<!-- 生日特权提醒 -->
		<view class="birthday-reminder" v-if="isBirthdayMonth">
			<view class="reminder-content">
				<text class="reminder-icon">🎂</text>
				<view class="reminder-text">
					<text class="reminder-title">生日月特权</text>
					<text class="reminder-desc">本月享受生日专属优惠</text>
				</view>
				<view class="reminder-action" @click="claimBirthdayGift">
					<text>领取</text>
				</view>
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
			userStats: {},
			memberPrivileges: [
				{
					id: 1,
					name: '专属折扣',
					desc: '享受会员专属价格',
					icon: '💰',
					level: 0
				},
				{
					id: 2,
					name: '积分翻倍',
					desc: '消费积分双倍奖励',
					icon: '⭐',
					level: 1
				},
				{
					id: 3,
					name: '免费配送',
					desc: '全场免配送费',
					icon: '🚚',
					level: 1
				},
				{
					id: 4,
					name: '生日特权',
					desc: '生日月专属优惠',
					icon: '🎂',
					level: 0
				},
				{
					id: 5,
					name: '专属客服',
					desc: '优先客服支持',
					icon: '👨‍💼',
					level: 2
				},
				{
					id: 6,
					name: '新品试喝',
					desc: '新品优先体验',
					icon: '🆕',
					level: 2
				}
			],
			memberBenefits: [
				{
					id: 1,
					name: '新会员礼包',
					desc: '注册即送5元优惠券',
					icon: '🎁',
					claimed: false
				},
				{
					id: 2,
					name: '每日签到',
					desc: '连续签到获得积分',
					icon: '📅',
					claimed: false
				},
				{
					id: 3,
					name: '分享奖励',
					desc: '分享好友获得优惠券',
					icon: '📤',
					claimed: false
				}
			],
			isBirthdayMonth: false
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadUserStats()
		this.checkBirthdayMonth()
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
			}
		},

		// 加载用户统计
		async loadUserStats() {
			try {
				if (!this.userInfo.id) return

				// 这里应该调用API获取用户统计数据
				// 暂时使用模拟数据
				this.userStats = {
					totalOrders: 25,
					totalAmount: 680.50,
					totalPoints: 1250,
					inviteCount: 3
				}
			} catch (error) {
				console.error('加载用户统计失败:', error)
			}
		},

		// 检查是否为生日月
		checkBirthdayMonth() {
			// 这里应该根据用户生日判断
			// 暂时随机设置
			this.isBirthdayMonth = Math.random() > 0.7
		},

		// 获取会员卡样式类
		getMemberCardClass(level) {
			const classes = ['normal-card', 'gold-card', 'diamond-card']
			return classes[level] || classes[0]
		},

		// 获取等级进度描述
		getLevelProgressDesc(level, totalAmount) {
			const thresholds = [0, 500, 2000] // 升级门槛
			if (level >= 2) {
				return '已达到最高等级'
			}
			const nextThreshold = thresholds[level + 1]
			const remaining = nextThreshold - totalAmount
			return `再消费¥${remaining.toFixed(2)}升级到${getMemberLevelText(level + 1)}`
		},

		// 获取等级进度百分比
		getLevelProgress(level, totalAmount) {
			const thresholds = [0, 500, 2000]
			if (level >= 2) return 100
			
			const currentThreshold = thresholds[level]
			const nextThreshold = thresholds[level + 1]
			const progress = ((totalAmount - currentThreshold) / (nextThreshold - currentThreshold)) * 100
			return Math.min(Math.max(progress, 0), 100)
		},

		// 判断特权是否可用
		isPrivilegeAvailable(requiredLevel) {
			return this.userInfo.memberLevel >= requiredLevel
		},

		// 领取福利
		async claimBenefit(benefit) {
			if (benefit.claimed) return

			try {
				// 这里应该调用API领取福利
				benefit.claimed = true
				uni.showToast({
					title: '领取成功',
					icon: 'success'
				})
			} catch (error) {
				uni.showToast({
					title: '领取失败',
					icon: 'none'
				})
			}
		},

		// 领取生日礼品
		claimBirthdayGift() {
			uni.showModal({
				title: '生日快乐',
				content: '恭喜您获得生日专属优惠券！',
				showCancel: false,
				success: () => {
					this.isBirthdayMonth = false
				}
			})
		},

		// 跳转充值
		goToRecharge() {
			uni.showToast({
				title: '充值功能开发中',
				icon: 'none'
			})
		},

		// 跳转积分页面
		goToPoints() {
			uni.navigateTo({
				url: '/pages/points/points'
			})
		},

		// 跳转邀请页面
		goToInvite() {
			uni.navigateTo({
				url: '/pages/invite/invite'
			})
		},

		// 跳转优惠券
		goToCoupon() {
			uni.navigateTo({
				url: '/pages/coupon/coupon'
			})
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

/* 会员卡片 */
.member-card {
	margin: 20rpx;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
}

.card-bg {
	padding: 40rpx;
	position: relative;
	overflow: hidden;
}

.card-bg::before {
	content: '';
	position: absolute;
	top: -50%;
	right: -50%;
	width: 200%;
	height: 200%;
	background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
	transform: rotate(45deg);
}

.normal-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.gold-card {
	background: linear-gradient(135deg, #f7971e 0%, #ffd200 100%);
}

.diamond-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
	position: relative;
	z-index: 1;
}

.member-info {
	flex: 1;
}

.member-level {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-bottom: 8rpx;
}

.member-name {
	display: block;
	font-size: 36rpx;
	font-weight: 700;
	color: white;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.member-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	overflow: hidden;
	border: 3rpx solid rgba(255, 255, 255, 0.3);
}

.avatar {
	width: 100%;
	height: 100%;
}

.card-content {
	display: flex;
	justify-content: space-between;
	margin-bottom: 40rpx;
	position: relative;
	z-index: 1;
}

.balance-info,
.points-info {
	text-align: center;
}

.balance-label,
.points-label {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-bottom: 8rpx;
}

.balance-amount,
.points-amount {
	display: block;
	font-size: 32rpx;
	font-weight: 700;
	color: white;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.card-actions {
	display: flex;
	justify-content: space-around;
	position: relative;
	z-index: 1;
}

.action-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 16rpx;
	background: rgba(255, 255, 255, 0.2);
	backdrop-filter: blur(10rpx);
	transition: all 0.3s ease;
}

.action-item:active {
	background: rgba(255, 255, 255, 0.3);
	transform: scale(0.95);
}

.action-icon {
	font-size: 32rpx;
	margin-bottom: 8rpx;
}

.action-text {
	font-size: 22rpx;
	color: white;
	font-weight: 500;
}

/* 等级进度 */
.level-progress {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.progress-header {
	margin-bottom: 30rpx;
}

.progress-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}

.progress-desc {
	font-size: 24rpx;
	color: #666;
}

.progress-bar {
	position: relative;
}

.progress-track {
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

.level-markers {
	display: flex;
	justify-content: space-between;
}

.level-marker {
	display: flex;
	flex-direction: column;
	align-items: center;
	opacity: 0.5;
	transition: all 0.3s ease;
}

.level-marker.active {
	opacity: 1;
}

.marker-text {
	font-size: 22rpx;
	color: #666;
	font-weight: 500;
}

.level-marker.active .marker-text {
	color: #ff6b35;
}

/* 通用区块样式 */
.privileges-section,
.benefits-section,
.statistics-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.section-more {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 500;
}

/* 特权网格 */
.privileges-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.privilege-item {
	width: calc(50% - 10rpx);
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	border-radius: 16rpx;
	border: 2rpx solid #f0f0f0;
	position: relative;
}

.privilege-icon {
	font-size: 48rpx;
	margin-bottom: 16rpx;
	opacity: 0.5;
	transition: all 0.3s ease;
}

.privilege-icon.available {
	opacity: 1;
}

.privilege-name {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	text-align: center;
}

.privilege-desc {
	font-size: 22rpx;
	color: #666;
	text-align: center;
	line-height: 1.4;
}

.privilege-level {
	position: absolute;
	top: 10rpx;
	right: 10rpx;
	background: #ff6b35;
	color: white;
	font-size: 18rpx;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

/* 福利列表 */
.benefits-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.benefit-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	border-radius: 16rpx;
	background: #f8f9fa;
	transition: all 0.3s ease;
}

.benefit-item:active {
	background: rgba(255, 107, 53, 0.1);
}

.benefit-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.benefit-info {
	flex: 1;
}

.benefit-name {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	display: block;
	margin-bottom: 6rpx;
}

.benefit-desc {
	font-size: 22rpx;
	color: #666;
}

.benefit-action {
	margin-left: 20rpx;
}

.action-btn {
	font-size: 24rpx;
	color: #ff6b35;
	padding: 12rpx 20rpx;
	border-radius: 20rpx;
	border: 1rpx solid #ff6b35;
	font-weight: 500;
}

.action-btn.claimed {
	color: #999;
	border-color: #ddd;
}

/* 统计网格 */
.stats-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.stat-item {
	width: calc(50% - 10rpx);
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	border-radius: 16rpx;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
}

.stat-number {
	font-size: 32rpx;
	font-weight: 700;
	color: #ff6b35;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

/* 生日提醒 */
.birthday-reminder {
	margin: 20rpx;
	background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(255, 154, 158, 0.3);
}

.reminder-content {
	display: flex;
	align-items: center;
}

.reminder-icon {
	font-size: 48rpx;
	margin-right: 20rpx;
}

.reminder-text {
	flex: 1;
}

.reminder-title {
	font-size: 28rpx;
	font-weight: 600;
	color: white;
	display: block;
	margin-bottom: 6rpx;
}

.reminder-desc {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

.reminder-action {
	background: rgba(255, 255, 255, 0.9);
	color: #ff6b35;
	font-size: 24rpx;
	font-weight: 600;
	padding: 16rpx 32rpx;
	border-radius: 20rpx;
}
</style>
