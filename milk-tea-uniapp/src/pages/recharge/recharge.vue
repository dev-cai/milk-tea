<template>
	<view class="container">
		<!-- 余额卡片 -->
		<view class="balance-card">
			<view class="balance-header">
				<text class="balance-label">当前余额</text>
				<view class="balance-actions">
					<text class="action-link" @click="viewBalanceHistory">明细 ›</text>
				</view>
			</view>
			<view class="balance-amount">
				<text class="currency">¥</text>
				<text class="amount">{{ formatPrice(userInfo.balance || 0) }}</text>
			</view>
			<view class="balance-tips">
				<text class="tip-icon">💡</text>
				<text class="tip-text">充值享优惠，充值越多优惠越多</text>
			</view>
		</view>

		<!-- 充值金额选择 -->
		<view class="recharge-section">
			<view class="section-header">
				<text class="section-title">选择充值金额</text>
			</view>
			<view class="amount-grid">
				<view 
					class="amount-item" 
					v-for="item in rechargeOptions" 
					:key="item.id"
					:class="{ active: selectedAmount === item.amount, recommended: item.recommended }"
					@click="selectAmount(item)"
				>
					<view class="amount-badge" v-if="item.recommended">
						<text>推荐</text>
					</view>
					<view class="amount-value">
						<text class="amount-number">{{ item.amount }}</text>
						<text class="amount-unit">元</text>
					</view>
					<view class="amount-bonus" v-if="item.bonus > 0">
						<text>送{{ item.bonus }}元</text>
					</view>
					<view class="amount-discount" v-if="item.discount">
						<text>{{ item.discount }}</text>
					</view>
				</view>
			</view>

			<!-- 自定义金额 -->
			<view class="custom-amount">
				<view class="custom-label">
					<text>自定义金额</text>
				</view>
				<view class="custom-input">
					<text class="input-prefix">¥</text>
					<input 
						class="input-field" 
						type="digit" 
						placeholder="请输入充值金额"
						v-model="customAmount"
						@input="onCustomAmountInput"
						@focus="onCustomAmountFocus"
					/>
				</view>
				<view class="custom-tips">
					<text>最低充值10元，最高充值10000元</text>
				</view>
			</view>
		</view>

		<!-- 充值优惠说明 -->
		<view class="discount-section">
			<view class="section-header">
				<text class="section-title">充值优惠</text>
			</view>
			<view class="discount-list">
				<view class="discount-item" v-for="(discount, index) in discountRules" :key="index">
					<view class="discount-icon">
						<text>{{ discount.icon }}</text>
					</view>
					<view class="discount-content">
						<text class="discount-title">{{ discount.title }}</text>
						<text class="discount-desc">{{ discount.desc }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 支付方式 -->
		<view class="payment-section">
			<view class="section-header">
				<text class="section-title">支付方式</text>
			</view>
			<view class="payment-list">
				<view 
					class="payment-item" 
					v-for="method in paymentMethods" 
					:key="method.id"
					:class="{ active: selectedPayment === method.id }"
					@click="selectPayment(method.id)"
				>
					<view class="payment-icon">
						<text>{{ method.icon }}</text>
					</view>
					<view class="payment-info">
						<text class="payment-name">{{ method.name }}</text>
						<text class="payment-desc">{{ method.desc }}</text>
					</view>
					<view class="payment-radio">
						<view class="radio-dot" v-if="selectedPayment === method.id"></view>
					</view>
				</view>
			</view>
		</view>

		<!-- 充值说明 -->
		<view class="tips-section">
			<text class="tips-text">💡 充值金额实时到账，赠送金额有效期30天</text>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">实付金额</text>
				<view class="total-amount">
					<text class="total-currency">¥</text>
					<text class="total-number">{{ getFinalAmount() }}</text>
				</view>
				<text class="total-bonus" v-if="getBonusAmount() > 0">
					到账{{ getFinalAmount() + getBonusAmount() }}元
				</text>
			</view>
			<view class="submit-btn" :class="{ disabled: !canSubmit() }" @click="submitRecharge">
				<text>立即充值</text>
			</view>
		</view>
	</view>
</template>

<script>
import { formatPrice } from '@/utils/common.js'

export default {
	data() {
		return {
			userInfo: {},
			selectedAmount: 0,
			customAmount: '',
			selectedPayment: 'wechat',
			rechargeOptions: [
				{ id: 1, amount: 10, bonus: 0, discount: '', recommended: false },
				{ id: 2, amount: 50, bonus: 5, discount: '送5元', recommended: false },
				{ id: 3, amount: 100, bonus: 15, discount: '送15元', recommended: true },
				{ id: 4, amount: 200, bonus: 40, discount: '送40元', recommended: false },
				{ id: 5, amount: 500, bonus: 120, discount: '送120元', recommended: false },
				{ id: 6, amount: 1000, bonus: 300, discount: '送300元', recommended: false }
			],
			paymentMethods: [
				{ id: 'wechat', name: '微信支付（开发测试）', desc: '仅用于测试', icon: '💚' }
			],
			discountRules: [
				{ icon: '🎁', title: '充值送好礼', desc: '充值满100元送15元，充值越多送越多' },
				{ icon: '⭐', title: '会员专享', desc: '会员充值额外享受5%积分返还' },
				{ icon: '🎫', title: '优惠券奖励', desc: '首次充值赠送20元优惠券' },
				{ icon: '💰', title: '无手续费', desc: '充值无任何手续费，实时到账' }
			]
		}
	},
	
	onLoad() {
		this.loadUserInfo()
	},
	
	methods: {
		formatPrice,
		
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo) {
				this.userInfo = userInfo
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
		
		// 选择充值金额
		selectAmount(item) {
			this.selectedAmount = item.amount
			this.customAmount = ''
		},
		
		// 自定义金额输入
		onCustomAmountInput(e) {
			const value = e.detail.value
			// 限制只能输入数字和小数点
			const numValue = parseFloat(value) || 0
			
			if (numValue > 0) {
				this.selectedAmount = 0 // 清除预设金额选择
			}
		},
		
		// 自定义金额获得焦点
		onCustomAmountFocus() {
			this.selectedAmount = 0
		},
		
		// 选择支付方式
		selectPayment(id) {
			this.selectedPayment = id
		},
		
		
		// 查看余额明细
		viewBalanceHistory() {
			uni.showToast({
				title: '余额明细功能开发中',
				icon: 'none'
			})
		},
		
		// 获取最终支付金额
		getFinalAmount() {
			if (this.customAmount) {
				return parseFloat(this.customAmount) || 0
			}
			return this.selectedAmount
		},
		
		// 获取赠送金额
		getBonusAmount() {
			if (this.customAmount) {
				// 自定义金额按比例计算赠送
				const amount = parseFloat(this.customAmount) || 0
				if (amount >= 1000) return Math.floor(amount * 0.3)
				if (amount >= 500) return Math.floor(amount * 0.24)
				if (amount >= 200) return Math.floor(amount * 0.2)
				if (amount >= 100) return Math.floor(amount * 0.15)
				if (amount >= 50) return Math.floor(amount * 0.1)
				return 0
			}
			
			const selected = this.rechargeOptions.find(item => item.amount === this.selectedAmount)
			return selected ? selected.bonus : 0
		},
		
		// 检查是否可以提交
		canSubmit() {
			const amount = this.getFinalAmount()
			return amount >= 10 && amount <= 10000
		},
		
		// 提交充值
		async submitRecharge() {
			if (!this.canSubmit()) {
				uni.showToast({
					title: '请选择充值金额（10-10000元）',
					icon: 'none'
				})
				return
			}
			
			const amount = this.getFinalAmount()
			const bonus = this.getBonusAmount()
			
			uni.showModal({
				title: '确认充值',
				content: `充值${amount}元${bonus > 0 ? '，赠送' + bonus + '元' : ''}，实际到账${amount + bonus}元`,
				success: async (res) => {
					if (res.confirm) {
						await this.processRecharge(amount, bonus)
					}
				}
			})
		},
		
		// 处理充值
		async processRecharge(amount, bonus) {
			uni.showModal({
				title: '微信沙箱充值',
				content: '当前仅接入订单微信支付开发测试流程，余额充值需配置商户号、证书和支付回调后开放。',
				showCancel: false
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 200rpx;
}

/* 余额卡片 */
.balance-card {
	margin: 30rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.3);
}

.balance-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.balance-label {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
}

.balance-actions {
	display: flex;
	gap: 20rpx;
}

.action-link {
	font-size: 26rpx;
	color: white;
	opacity: 0.9;
}

.balance-amount {
	display: flex;
	align-items: baseline;
	margin-bottom: 20rpx;
}

.currency {
	font-size: 40rpx;
	color: white;
	font-weight: 600;
	margin-right: 8rpx;
}

.amount {
	font-size: 72rpx;
	color: white;
	font-weight: 700;
}

.balance-tips {
	display: flex;
	align-items: center;
	padding: 16rpx 20rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 12rpx;
}

.tip-icon {
	font-size: 28rpx;
	margin-right: 12rpx;
}

.tip-text {
	font-size: 24rpx;
	color: white;
}

/* 充值金额选择 */
.recharge-section {
	margin: 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
}

.section-header {
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.amount-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
	margin-bottom: 40rpx;
}

.amount-item {
	position: relative;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 30rpx 20rpx;
	background: #f5f5f5;
	border-radius: 16rpx;
	border: 2rpx solid transparent;
	transition: all 0.3s;
}

.amount-item.active {
	background: #fff5f0;
	border-color: #ff6b35;
}

.amount-item.recommended {
	background: linear-gradient(135deg, #fff5f0 0%, #ffe8e0 100%);
}

.amount-badge {
	position: absolute;
	top: 0;
	right: 0;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	font-size: 20rpx;
	padding: 4rpx 12rpx;
	border-radius: 0 16rpx 0 16rpx;
}

.amount-value {
	display: flex;
	align-items: baseline;
	margin-bottom: 8rpx;
}

.amount-number {
	font-size: 40rpx;
	font-weight: 700;
	color: #333;
}

.amount-unit {
	font-size: 24rpx;
	color: #666;
	margin-left: 4rpx;
}

.amount-bonus {
	font-size: 22rpx;
	color: #ff6b35;
	font-weight: 600;
}

.amount-discount {
	font-size: 20rpx;
	color: #999;
}

/* 自定义金额 */
.custom-amount {
	padding-top: 30rpx;
	border-top: 1rpx solid #f0f0f0;
}

.custom-label {
	margin-bottom: 20rpx;
}

.custom-label text {
	font-size: 28rpx;
	color: #333;
	font-weight: 600;
}

.custom-input {
	display: flex;
	align-items: center;
	background: #f5f5f5;
	border-radius: 12rpx;
	padding: 24rpx 30rpx;
	margin-bottom: 16rpx;
}

.input-prefix {
	font-size: 32rpx;
	color: #666;
	margin-right: 12rpx;
}

.input-field {
	flex: 1;
	font-size: 32rpx;
	color: #333;
}

.custom-tips {
	font-size: 22rpx;
	color: #999;
}

/* 充值优惠 */
.discount-section {
	margin: 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
}

.discount-list {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
}

.discount-item {
	display: flex;
	align-items: flex-start;
}

.discount-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.discount-content {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.discount-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
}

.discount-desc {
	font-size: 24rpx;
	color: #999;
	line-height: 1.6;
}

/* 支付方式 */
.payment-section {
	margin: 30rpx;
	background: white;
	border-radius: 24rpx;
	padding: 40rpx;
}

.payment-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.payment-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: #f5f5f5;
	border-radius: 16rpx;
	border: 2rpx solid transparent;
	transition: all 0.3s;
}

.payment-item.active {
	background: #fff5f0;
	border-color: #ff6b35;
}

.payment-icon {
	font-size: 48rpx;
	margin-right: 20rpx;
}

.payment-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.payment-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
}

.payment-desc {
	font-size: 22rpx;
	color: #999;
}

.payment-radio {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #ddd;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.payment-item.active .payment-radio {
	border-color: #ff6b35;
}

.radio-dot {
	width: 24rpx;
	height: 24rpx;
	background: #ff6b35;
	border-radius: 50%;
}

/* 充值说明 */
.tips-section {
	margin: 30rpx;
	padding: 24rpx 30rpx;
	background: #fff9f0;
	border-radius: 16rpx;
	border-left: 4rpx solid #ff6b35;
}

.tips-text {
	font-size: 24rpx;
	color: #666;
	line-height: 1.6;
}

/* 底部操作栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
	display: flex;
	align-items: center;
	gap: 20rpx;
	z-index: 100;
}

.total-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.total-label {
	font-size: 22rpx;
	color: #999;
	margin-bottom: 8rpx;
}

.total-amount {
	display: flex;
	align-items: baseline;
}

.total-currency {
	font-size: 28rpx;
	color: #ff6b35;
	font-weight: 600;
	margin-right: 4rpx;
}

.total-number {
	font-size: 48rpx;
	color: #ff6b35;
	font-weight: 700;
}

.total-bonus {
	font-size: 20rpx;
	color: #999;
	margin-top: 4rpx;
}

.submit-btn {
	padding: 28rpx 60rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50rpx;
	box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.4);
}

.submit-btn.disabled {
	background: #ccc;
	box-shadow: none;
}

.submit-btn text {
	font-size: 32rpx;
	font-weight: 700;
	color: white;
}
</style>
