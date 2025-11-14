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
					<text class="type-icon">{{ type.icon }}</text>
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
			<textarea 
				class="feedback-textarea" 
				placeholder="请详细描述您遇到的问题或建议，我们会认真处理每一条反馈"
				v-model="feedbackContent"
				maxlength="500"
				auto-height
			/>
		</view>

		<!-- 图片上传 -->
		<view class="images-section">
			<view class="section-header">
				<text class="section-title">上传图片</text>
				<text class="image-tip">最多上传3张图片</text>
			</view>
			<view class="image-list">
				<view class="image-item" v-for="(image, index) in uploadedImages" :key="index">
					<image class="uploaded-image" :src="image" mode="aspectFill"></image>
					<view class="image-delete" @click="deleteImage(index)">
						<text>✕</text>
					</view>
				</view>
				<view class="image-upload" v-if="uploadedImages.length < 3" @click="chooseImage">
					<text class="upload-icon">📷</text>
					<text class="upload-text">添加图片</text>
				</view>
			</view>
		</view>

		<!-- 联系方式 -->
		<view class="contact-section">
			<view class="section-header">
				<text class="section-title">联系方式</text>
				<text class="contact-tip">便于我们及时回复您</text>
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
						placeholder="请输入邮箱（选填）" 
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
						<text class="history-type">{{ getFeedbackTypeName(item.type) }}</text>
						<text class="history-desc">{{ item.content }}</text>
						<text class="history-time">{{ item.createTime }}</text>
					</view>
					<view class="history-status" :class="getStatusClass(item.status)">
						<text>{{ getStatusText(item.status) }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view class="submit-btn" @click="submitFeedback">
				<text>提交反馈</text>
			</view>
		</view>

		<!-- 反馈详情弹窗 -->
		<view class="feedback-modal" :class="{ show: showFeedbackModal }" @click="hideFeedbackModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">反馈详情</text>
					<text class="modal-close" @click="hideFeedbackModal">✕</text>
				</view>
				<view class="modal-body" v-if="selectedFeedback">
					<view class="feedback-info">
						<text class="feedback-type">{{ getFeedbackTypeName(selectedFeedback.type) }}</text>
						<text class="feedback-time">{{ selectedFeedback.createTime }}</text>
					</view>
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
					<view class="feedback-reply" v-if="selectedFeedback.reply">
						<text class="reply-title">客服回复：</text>
						<text class="reply-content">{{ selectedFeedback.reply }}</text>
						<text class="reply-time">{{ selectedFeedback.replyTime }}</text>
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
				{ value: 1, name: '产品问题', icon: '🥤' },
				{ value: 2, name: '服务问题', icon: '👨‍💼' },
				{ value: 3, name: '配送问题', icon: '🚚' },
				{ value: 4, name: '支付问题', icon: '💳' },
				{ value: 5, name: '功能建议', icon: '💡' },
				{ value: 6, name: '其他问题', icon: '❓' }
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

				// 这里应该调用API获取反馈历史
				// const res = await api.feedback.getHistory(userInfo.id)
				// this.feedbackHistory = res.data || []

				// 使用模拟数据
				this.feedbackHistory = [
					{
						id: 1,
						type: 1,
						content: '奶茶味道太甜了，希望能调整一下甜度',
						status: 2,
						createTime: '2024-11-12 14:30',
						reply: '感谢您的反馈，我们已经调整了甜度配比，请您再次尝试。',
						replyTime: '2024-11-12 16:45',
						images: []
					},
					{
						id: 2,
						type: 5,
						content: '建议增加更多口味的奶茶',
						status: 1,
						createTime: '2024-11-10 09:15',
						reply: '',
						replyTime: '',
						images: []
					}
				]
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

				const userInfo = uni.getStorageSync('userInfo')
				const feedbackData = {
					type: this.feedbackType,
					content: this.feedbackContent,
					images: this.uploadedImages,
					contactPhone: this.contactPhone,
					contactEmail: this.contactEmail,
					userId: userInfo ? userInfo.id : null
				}

				// 这里应该调用API提交反馈
				// await api.feedback.submit(feedbackData)

				// 模拟提交成功
				const newFeedback = {
					id: Date.now(),
					...feedbackData,
					status: 0,
					createTime: new Date().toLocaleString(),
					reply: '',
					replyTime: ''
				}
				this.feedbackHistory.unshift(newFeedback)

				// 清空表单
				this.feedbackContent = ''
				this.uploadedImages = []
				this.feedbackType = 1

				uni.showToast({
					title: '反馈提交成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('提交反馈失败:', error)
				uni.showToast({
					title: '提交失败，请重试',
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
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

/* 通用区块样式 */
.type-section,
.content-section,
.images-section,
.contact-section,
.history-section {
	margin: 20rpx;
	background: white;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.char-count,
.image-tip,
.contact-tip {
	font-size: 22rpx;
	color: #999;
}

/* 反馈类型 */
.type-list {
	display: flex;
	flex-wrap: wrap;
	padding: 20rpx 30rpx 30rpx;
	gap: 20rpx;
}

.type-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 24rpx 20rpx;
	border-radius: 16rpx;
	border: 2rpx solid #f0f0f0;
	transition: all 0.3s ease;
	min-width: 140rpx;
}

.type-item.active {
	border-color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
}

.type-icon {
	font-size: 40rpx;
	margin-bottom: 12rpx;
}

.type-name {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

.type-item.active .type-name {
	color: #ff6b35;
}

/* 反馈内容 */
.feedback-textarea {
	width: 100%;
	min-height: 200rpx;
	padding: 30rpx;
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	background: white;
}

.feedback-textarea::placeholder {
	color: #999;
}

/* 图片上传 */
.image-list {
	display: flex;
	flex-wrap: wrap;
	padding: 20rpx 30rpx 30rpx;
	gap: 20rpx;
}

.image-item {
	position: relative;
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
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
	color: white;
	font-size: 24rpx;
}

.image-upload {
	width: 160rpx;
	height: 160rpx;
	border: 2rpx dashed #ddd;
	border-radius: 16rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
}

.image-upload:active {
	border-color: #ff6b35;
	background: rgba(255, 107, 53, 0.05);
}

.upload-icon {
	font-size: 40rpx;
	color: #999;
	margin-bottom: 8rpx;
}

.upload-text {
	font-size: 22rpx;
	color: #999;
}

/* 联系方式 */
.contact-form {
	padding: 20rpx 30rpx 30rpx;
}

.form-item {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.form-label {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	width: 120rpx;
	flex-shrink: 0;
}

.form-input {
	flex: 1;
	padding: 20rpx;
	border: 2rpx solid #f0f0f0;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #333;
	margin-left: 20rpx;
}

.form-input:focus {
	border-color: #ff6b35;
}

.form-input::placeholder {
	color: #999;
}

/* 历史反馈 */
.history-list {
	padding: 20rpx 30rpx 30rpx;
}

.history-item {
	display: flex;
	align-items: center;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f8f9fa;
	transition: all 0.3s ease;
}

.history-item:last-child {
	border-bottom: none;
}

.history-item:active {
	background: rgba(255, 107, 53, 0.05);
}

.history-content {
	flex: 1;
}

.history-type {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
	display: block;
	margin-bottom: 8rpx;
}

.history-desc {
	font-size: 26rpx;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
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
	padding: 8rpx 16rpx;
	border-radius: 12rpx;
	font-weight: 500;
}

.history-status.pending {
	background: rgba(250, 173, 20, 0.1);
	color: #faad14;
}

.history-status.processing {
	background: rgba(24, 144, 255, 0.1);
	color: #1890ff;
}

.history-status.completed {
	background: rgba(82, 196, 26, 0.1);
	color: #52c41a;
}

/* 提交按钮 */
.submit-section {
	position: fixed;
	bottom: 40rpx;
	left: 30rpx;
	right: 30rpx;
}

.submit-btn {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50rpx;
	padding: 32rpx;
	text-align: center;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.4);
	transition: all 0.3s ease;
}

.submit-btn:active {
	transform: scale(0.98);
}

.submit-btn text {
	font-size: 32rpx;
	color: white;
	font-weight: 600;
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
	border-radius: 24rpx;
	width: 680rpx;
	max-width: 90vw;
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
	padding: 30rpx;
	max-height: 60vh;
	overflow-y: auto;
}

.feedback-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.feedback-type {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.feedback-time {
	font-size: 22rpx;
	color: #999;
}

.feedback-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	margin-bottom: 20rpx;
}

.feedback-images {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
	margin-bottom: 20rpx;
}

.feedback-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
}

.feedback-reply {
	background: #f8f9fa;
	border-radius: 16rpx;
	padding: 24rpx;
	border-left: 6rpx solid #ff6b35;
}

.reply-title {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
	display: block;
	margin-bottom: 12rpx;
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
}
</style>
