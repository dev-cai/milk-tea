<template>
	<view class="container">

		<!-- 反馈类型 -->
		<view class="type-section">
			<view class="section-header">
				<text class="section-title">反馈类型</text>
			</view>
			<view class="type-list">
				<view class="type-item" 
					:class="{ active: feedbackType === type.value }"
					v-for="type in feedbackTypes" 
					:key="type.value"
					@click="selectType(type.value)"
				>
					<text class="type-emoji">{{ type.emoji }}</text>
					<text class="type-name">{{ type.name }}</text>
				</view>
			</view>
		</view>

		<!-- 反馈内容 -->
		<view class="content-section">
			<view class="section-header">
				<text class="section-title">问题描述</text>
				<text class="char-count">{{ feedbackContent.length }}/500</text>
			</view>
			<view class="textarea-wrapper">
				<textarea 
					class="feedback-textarea" 
					placeholder="请详细描述您遇到的问题或建议"
					v-model="feedbackContent"
					maxlength="500"
					:auto-height="true"
				/>
			</view>
		</view>

		<!-- 图片上传 -->
		<view class="images-section">
			<view class="section-header">
				<text class="section-title">上传图片</text>
				<text class="image-tip">最多3张</text>
			</view>
			<view class="image-list">
				<view class="image-item" v-for="(image, index) in uploadedImages" :key="index">
					<image class="uploaded-image" :src="image" mode="aspectFill"></image>
					<view class="image-delete" @click="deleteImage(index)">
						<text class="delete-icon">×</text>
					</view>
				</view>
				<view class="image-upload" v-if="uploadedImages.length < 3" @click="chooseImage">
					<text class="upload-icon">+</text>
					<text class="upload-text">添加图片</text>
				</view>
			</view>
		</view>

		<!-- 联系方式 -->
		<view class="contact-section">
			<view class="section-header">
				<text class="section-title">联系方式</text>
				<text class="contact-tip">选填</text>
			</view>
			<view class="contact-form">
				<view class="form-item">
					<text class="form-label">手机号</text>
					<input 
						class="form-input" 
						type="number" 
						placeholder="请输入手机号" 
						v-model="contactPhone"
						maxlength="11"
					/>
				</view>
				<view class="form-item">
					<text class="form-label">邮箱</text>
					<input 
						class="form-input" 
						type="text" 
						placeholder="请输入邮箱" 
						v-model="contactEmail"
					/>
				</view>
			</view>
		</view>

		<!-- 历史反馈 -->
		<view class="history-section" v-if="feedbackHistory.length > 0">
			<view class="section-header">
				<text class="section-title">我的反馈</text>
			</view>
			<view class="history-list">
				<view class="history-item" v-for="item in feedbackHistory" :key="item.id" @click="viewFeedback(item)">
					<view class="history-content">
						<view class="history-top">
							<text class="history-type">{{ getFeedbackTypeName(item.type) }}</text>
							<view class="history-status" :class="getStatusClass(item.status)">
								<text>{{ getStatusText(item.status) }}</text>
							</view>
						</view>
						<text class="history-desc">{{ item.content }}</text>
						<text class="history-time">{{ item.createTime }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" @click="submitFeedback">提交反馈</button>
		</view>

		<!-- 反馈详情弹窗 -->
		<view class="feedback-modal" :class="{ show: showFeedbackModal }" @click="hideFeedbackModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">反馈详情</text>
					<view class="modal-close" @click="hideFeedbackModal">
						<text class="close-icon">×</text>
					</view>
				</view>
				<view class="modal-body" v-if="selectedFeedback">
					<view class="feedback-card">
						<view class="feedback-info">
							<view class="info-left">
								<text class="feedback-emoji">{{ getFeedbackTypeEmoji(selectedFeedback.type) }}</text>
								<text class="feedback-type">{{ getFeedbackTypeName(selectedFeedback.type) }}</text>
							</view>
							<view class="feedback-status" :class="getStatusClass(selectedFeedback.status)">
								<text class="status-dot"></text>
								<text class="status-text">{{ getStatusText(selectedFeedback.status) }}</text>
							</view>
						</view>
						<text class="feedback-time">🕐 {{ selectedFeedback.createTime }}</text>
						<view class="divider"></view>
						<text class="feedback-content">{{ selectedFeedback.content }}</text>
						<view class="feedback-images" v-if="selectedFeedback.images && selectedFeedback.images.length > 0">
							<image class="feedback-image" 
								v-for="(image, index) in selectedFeedback.images" 
								:key="index"
								:src="image" 
								mode="aspectFill"
								@click="previewImage(image, selectedFeedback.images)"
							></image>
						</view>
					</view>
					<view class="feedback-reply" v-if="selectedFeedback.reply">
						<view class="reply-header">
							<text class="reply-icon">💬</text>
							<text class="reply-title">客服回复</text>
						</view>
						<text class="reply-content">{{ selectedFeedback.reply }}</text>
						<text class="reply-time">🕐 {{ selectedFeedback.replyTime }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { validatePhone, validateEmail } from '@/utils/common.js'

export default {
	data() {
		return {
			feedbackType: 1,
			feedbackTypes: [
				{ value: 1, name: '产品问题', emoji: '🧋' },
				{ value: 2, name: '服务问题', emoji: '👨‍💼' },
				{ value: 3, name: '配送问题', emoji: '🚚' },
				{ value: 4, name: '支付问题', emoji: '💳' },
				{ value: 5, name: '功能建议', emoji: '💡' },
				{ value: 6, name: '其他问题', emoji: '💬' }
			],
			feedbackContent: '',
			uploadedImages: [],
			contactPhone: '',
			contactEmail: '',
			feedbackHistory: [],
			showFeedbackModal: false,
			selectedFeedback: null
		}
	},
	computed: {
		canSubmit() {
			return this.feedbackContent.trim().length >= 10
		}
	},
	onLoad() {
		this.loadUserInfo()
		this.loadFeedbackHistory()
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo && userInfo.phone) {
				this.contactPhone = userInfo.phone
			}
		},

		// 加载反馈历史
		async loadFeedbackHistory() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) return

				const res = await api.feedback.getList(userInfo.id, { page: 1, size: 20 })
				if (res && res.data && res.data.records) {
					this.feedbackHistory = res.data.records.map(item => ({
						...item,
						images: item.images ? item.images.split(',') : []
					}))
				}
			} catch (error) {
				console.error('加载反馈历史失败:', error)
			}
		},

		// 选择反馈类型
		selectType(type) {
			this.feedbackType = type
		},

		// 选择图片
		chooseImage() {
			const remainingCount = 3 - this.uploadedImages.length
			uni.chooseImage({
				count: remainingCount,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.uploadedImages.push(...res.tempFilePaths)
				}
			})
		},

		// 删除图片
		deleteImage(index) {
			this.uploadedImages.splice(index, 1)
		},

		// 预览图片
		previewImage(current, urls) {
			uni.previewImage({
				current: current,
				urls: urls
			})
		},

		// 验证表单
		validateForm() {
			if (!this.feedbackContent.trim()) {
				uni.showToast({
					title: '请输入问题描述',
					icon: 'none'
				})
				return false
			}

			if (this.feedbackContent.length < 10) {
				uni.showToast({
					title: '问题描述至少10个字符',
					icon: 'none'
				})
				return false
			}

			if (this.contactPhone && !validatePhone(this.contactPhone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return false
			}

			if (this.contactEmail && !validateEmail(this.contactEmail)) {
				uni.showToast({
					title: '请输入正确的邮箱',
					icon: 'none'
				})
				return false
			}

			return true
		},

		// 提交反馈
		async submitFeedback() {
			if (!this.validateForm()) return

			try {
				uni.showLoading({ title: '提交中...' })

				const feedbackData = {
					type: this.feedbackType,
					content: this.feedbackContent,
					images: this.uploadedImages.join(','),
					contactPhone: this.contactPhone,
					contactEmail: this.contactEmail
				}

				await api.feedback.submit(feedbackData)

				// 清空表单
				this.feedbackContent = ''
				this.uploadedImages = []
				this.feedbackType = 1

				uni.showToast({
					title: '反馈提交成功',
					icon: 'success'
				})

				// 重新加载反馈历史
				setTimeout(() => {
					this.loadFeedbackHistory()
				}, 500)
			} catch (error) {
				console.error('提交反馈失败:', error)
				uni.showToast({
					title: error.message || '提交失败，请重试',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		// 查看反馈详情
		viewFeedback(feedback) {
			this.selectedFeedback = feedback
			this.showFeedbackModal = true
		},

		// 隐藏反馈详情弹窗
		hideFeedbackModal() {
			this.showFeedbackModal = false
			this.selectedFeedback = null
		},

		// 获取反馈类型名称
		getFeedbackTypeName(type) {
			const typeItem = this.feedbackTypes.find(item => item.value === type)
			return typeItem ? typeItem.name : '未知类型'
		},

		// 获取反馈类型emoji
		getFeedbackTypeEmoji(type) {
			const typeItem = this.feedbackTypes.find(item => item.value === type)
			return typeItem ? typeItem.emoji : '💬'
		},

		// 获取状态样式类
		getStatusClass(status) {
			const classes = {
				0: 'pending',
				1: 'processing',
				2: 'completed'
			}
			return classes[status] || 'pending'
		},

		// 获取状态文本
		getStatusText(status) {
			const texts = {
				0: '待处理',
				1: '处理中',
				2: '已回复'
			}
			return texts[status] || '未知'
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding: 20rpx;
	padding-bottom: 120rpx;
}

/* 通用区块样式 */
.type-section,
.content-section,
.images-section,
.contact-section,
.history-section {
	margin-bottom: 20rpx;
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx 24rpx 16rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.char-count,
.image-tip,
.contact-tip {
	font-size: 24rpx;
	color: #999;
}

/* 反馈类型 */
.type-list {
	display: flex;
	flex-wrap: wrap;
	padding: 20rpx 16rpx;
	gap: 12rpx;
}

.type-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	padding: 20rpx 16rpx;
	border-radius: 12rpx;
	background: #f8f8f8;
	border: 2rpx solid #f8f8f8;
	transition: all 0.2s ease;
	flex: 1;
	min-width: 140rpx;
}

.type-item.active {
	background: #fff5f0;
	border-color: #ff6b35;
}

.type-emoji {
	font-size: 32rpx;
}

.type-name {
	font-size: 24rpx;
	color: #666;
}

.type-item.active .type-name {
	color: #ff6b35;
	font-weight: 600;
}

/* 反馈内容 */
.textarea-wrapper {
	padding: 20rpx 24rpx;
}

.feedback-textarea {
	width: 100%;
	min-height: 200rpx;
	padding: 0;
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	background: white;
	border: none;
}

.feedback-textarea::placeholder {
	color: #999;
}

/* 图片上传 */
.image-list {
	display: flex;
	flex-wrap: wrap;
	padding: 20rpx 24rpx;
	gap: 16rpx;
}

.image-item {
	position: relative;
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	overflow: hidden;
}

.uploaded-image {
	width: 100%;
	height: 100%;
}

.image-delete {
	position: absolute;
	top: 8rpx;
	right: 8rpx;
	width: 40rpx;
	height: 40rpx;
	background: rgba(0, 0, 0, 0.6);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.delete-icon {
	font-size: 28rpx;
	color: white;
	line-height: 1;
}

.image-upload {
	width: 160rpx;
	height: 160rpx;
	border: 2rpx dashed #ddd;
	border-radius: 12rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	background: #fafafa;
}

.upload-icon {
	font-size: 48rpx;
	color: #999;
	line-height: 1;
}

.upload-text {
	font-size: 22rpx;
	color: #999;
}

/* 联系方式 */
.contact-form {
	padding: 20rpx 24rpx;
}

.form-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
	border-bottom: none;
}

.form-label {
	font-size: 28rpx;
	color: #333;
	width: 120rpx;
}

.form-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.form-input::placeholder {
	color: #999;
}

/* 历史反馈 */
.history-list {
	padding: 20rpx 24rpx;
}

.history-item {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
	border-bottom: none;
}

.history-content {
	flex: 1;
}

.history-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.history-type {
	font-size: 26rpx;
	color: #ff6b35;
	font-weight: 600;
}

.history-desc {
	font-size: 26rpx;
	color: #333;
	margin-bottom: 12rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.history-time {
	font-size: 22rpx;
	color: #999;
}

.history-status {
	font-size: 22rpx;
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
}

.history-status.pending {
	background: #fff7e6;
	color: #faad14;
}

.history-status.processing {
	background: #e6f7ff;
	color: #1890ff;
}

.history-status.completed {
	background: #f6ffed;
	color: #52c41a;
}

/* 提交按钮 */
.submit-section {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	background: white;
	border-top: 1rpx solid #f0f0f0;
}

.submit-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 44rpx;
	border: none;
	font-size: 32rpx;
	color: white;
	font-weight: 600;
	line-height: 88rpx;
}

.submit-btn::after {
	border: none;
}

/* 反馈详情弹窗 */
.feedback-modal {
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

.feedback-modal.show {
	opacity: 1;
	visibility: visible;
}

.modal-content {
	background: white;
	border-radius: 16rpx;
	width: 90%;
	max-height: 80vh;
	overflow: hidden;
	transform: scale(0.9);
	transition: all 0.3s ease;
}

.feedback-modal.show .modal-content {
	transform: scale(1);
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.modal-close {
	padding: 8rpx;
}

.close-icon {
	font-size: 32rpx;
	color: #999;
	line-height: 1;
}

.modal-body {
	padding: 24rpx;
	max-height: 60vh;
	overflow-y: auto;
}

.feedback-card {
	margin-bottom: 20rpx;
}

.feedback-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.info-left {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.feedback-emoji {
	font-size: 28rpx;
}

.feedback-type {
	font-size: 26rpx;
	color: #333;
	font-weight: 600;
}

.feedback-status {
	font-size: 22rpx;
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
}

.feedback-time {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 12rpx;
	display: block;
}

.divider {
	height: 1rpx;
	background: #f0f0f0;
	margin: 16rpx 0;
}

.feedback-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	margin-bottom: 16rpx;
	display: block;
}

.feedback-images {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
}

.feedback-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 8rpx;
}

.feedback-reply {
	background: #fff5f0;
	border-radius: 12rpx;
	padding: 20rpx;
	border-left: 4rpx solid #ff6b35;
}

.reply-header {
	display: flex;
	align-items: center;
	gap: 8rpx;
	margin-bottom: 12rpx;
}

.reply-icon {
	font-size: 24rpx;
}

.reply-title {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.reply-content {
	font-size: 26rpx;
	color: #333;
	line-height: 1.6;
	display: block;
	margin-bottom: 12rpx;
}

.reply-time {
	font-size: 22rpx;
	color: #999;
	display: block;
}

</style>
