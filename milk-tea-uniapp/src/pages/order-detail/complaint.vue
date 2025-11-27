<template>
	<view class="container">
		<view class="header">
			<text class="title">订单投诉</text>
			<text class="subtitle">我们会认真处理您的投诉</text>
		</view>

		<!-- 订单信息 -->
		<view class="order-info">
			<view class="info-row">
				<text class="label">订单号</text>
				<text class="value">{{ orderInfo.orderNo }}</text>
			</view>
			<view class="info-row">
				<text class="label">订单金额</text>
				<text class="value price">¥{{ orderInfo.payAmount }}</text>
			</view>
		</view>

		<!-- 投诉类型 -->
		<view class="section">
			<view class="section-title">投诉类型</view>
			<view class="type-list">
				<view 
					class="type-item" 
					:class="{ active: complaintType === type.value }"
					v-for="type in complaintTypes" 
					:key="type.value"
					@click="selectType(type.value)"
				>
					<text class="type-icon">{{ type.icon }}</text>
					<text class="type-name">{{ type.name }}</text>
				</view>
			</view>
		</view>

		<!-- 投诉内容 -->
		<view class="section">
			<view class="section-title">投诉内容</view>
			<textarea 
				class="complaint-textarea" 
				placeholder="请详细描述您的投诉内容，我们会尽快处理"
				v-model="complaintContent"
				maxlength="500"
			/>
			<view class="char-count">{{ complaintContent.length }}/500</view>
		</view>

		<!-- 图片上传 -->
		<view class="section">
			<view class="section-title">上传图片（选填）</view>
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
			<text class="image-tip">最多上传3张图片</text>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" @click="submitComplaint">提交投诉</button>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			orderId: null,
			orderInfo: {
				orderNo: '',
				payAmount: 0
			},
			complaintType: 1,
			complaintTypes: [
				{ value: 1, name: '商品质量', icon: '🧋' },
				{ value: 2, name: '服务态度', icon: '👨‍💼' },
				{ value: 3, name: '配送问题', icon: '🚚' },
				{ value: 4, name: '其他问题', icon: '💬' }
			],
			complaintContent: '',
			uploadedImages: [],
			loading: true
		}
	},
	onLoad(options) {
		console.log('投诉页面参数:', options)
		if (options.orderId) {
			this.orderId = options.orderId
			this.loadOrderInfo()
		} else {
			uni.showToast({
				title: '订单ID不存在',
				icon: 'none'
			})
		}
	},
	methods: {
		// 加载订单信息
		async loadOrderInfo() {
			this.loading = true
			uni.showLoading({ title: '加载中...' })
			
			try {
				console.log('开始加载订单信息，订单ID:', this.orderId)
				const res = await api.order.getDetail(this.orderId)
				console.log('订单信息响应:', res)
				
				if (res && res.data) {
					// 后端返回的数据结构是 { items: [...], order: {...} }
					// 我们需要使用 order 对象
					if (res.data.order) {
						this.orderInfo = res.data.order
					} else {
						// 如果直接是订单对象
						this.orderInfo = res.data
					}
					console.log('订单信息已设置:', this.orderInfo)
				} else {
					uni.showToast({
						title: '订单信息加载失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('加载订单信息失败:', error)
				uni.showToast({
					title: '加载失败，请重试',
					icon: 'none'
				})
			} finally {
				this.loading = false
				uni.hideLoading()
			}
		},

		// 选择投诉类型
		selectType(type) {
			this.complaintType = type
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

		// 验证表单
		validateForm() {
			if (!this.complaintContent.trim()) {
				uni.showToast({
					title: '请输入投诉内容',
					icon: 'none'
				})
				return false
			}

			if (this.complaintContent.length < 10) {
				uni.showToast({
					title: '投诉内容至少10个字符',
					icon: 'none'
				})
				return false
			}

			return true
		},

		// 提交投诉
		async submitComplaint() {
			if (!this.validateForm()) return

			try {
				uni.showLoading({ title: '提交中...' })

				const userInfo = uni.getStorageSync('userInfo')
				const complaintData = {
					orderId: this.orderId,
					orderNo: this.orderInfo.orderNo,
					userId: userInfo.id,
					customerName: userInfo.nickname || userInfo.username,
					complaintType: this.complaintType,
					content: this.complaintContent,
					images: this.uploadedImages.join(',')
				}

				await api.order.submitComplaint(complaintData)

				uni.showToast({
					title: '投诉提交成功',
					icon: 'success'
				})

				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (error) {
				console.error('提交投诉失败:', error)
				uni.showToast({
					title: error.message || '提交失败，请重试',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 120rpx;
}

.header {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	padding: 60rpx 40rpx 40rpx;
	color: white;
}

.title {
	font-size: 40rpx;
	font-weight: 600;
	display: block;
	margin-bottom: 12rpx;
}

.subtitle {
	font-size: 24rpx;
	opacity: 0.9;
}

.order-info {
	background: white;
	margin: 20rpx;
	padding: 24rpx;
	border-radius: 16rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.info-row:last-child {
	border-bottom: none;
}

.label {
	font-size: 28rpx;
	color: #666;
}

.value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.price {
	color: #ff6b35;
	font-weight: 600;
}

.section {
	background: white;
	margin: 20rpx;
	padding: 24rpx;
	border-radius: 16rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 20rpx;
}

.type-list {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.type-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	padding: 24rpx 20rpx;
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

.type-icon {
	font-size: 36rpx;
}

.type-name {
	font-size: 24rpx;
	color: #666;
}

.type-item.active .type-name {
	color: #ff6b35;
	font-weight: 600;
}

.complaint-textarea {
	width: 100%;
	min-height: 200rpx;
	padding: 20rpx;
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	background: #f8f8f8;
	border-radius: 12rpx;
	border: none;
}

.char-count {
	text-align: right;
	font-size: 24rpx;
	color: #999;
	margin-top: 12rpx;
}

.image-list {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
	margin-bottom: 12rpx;
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

.image-tip {
	font-size: 24rpx;
	color: #999;
}

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
</style>
