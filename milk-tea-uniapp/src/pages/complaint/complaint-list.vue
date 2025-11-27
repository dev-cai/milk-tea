<template>
	<view class="container">
		<!-- 标签页 -->
		<view class="tabs">
			<view 
				class="tab-item" 
				:class="{ active: activeTab === tab.value }"
				v-for="tab in tabs" 
				:key="tab.value"
				@click="switchTab(tab.value)"
			>
				<text class="tab-text">{{ tab.name }}</text>
			</view>
		</view>

		<!-- 投诉列表 -->
		<view class="complaint-list" v-if="complaintList.length > 0">
			<view class="complaint-item" v-for="complaint in complaintList" :key="complaint.id" @click="showDetail(complaint)">
				<!-- 投诉头部 -->
				<view class="complaint-header">
					<view class="type-tag">{{ getComplaintTypeName(complaint.complaintType) }}</view>
					<view class="status-tag" :class="getStatusClass(complaint.status)">
						{{ getStatusName(complaint.status) }}
					</view>
				</view>

				<!-- 订单信息 -->
				<view class="order-info">
					<text class="order-no">订单号：{{ complaint.orderNo }}</text>
					<text class="create-time">{{ formatTime(complaint.createTime) }}</text>
				</view>

				<!-- 投诉内容 -->
				<view class="complaint-content">
					<text class="content-text">{{ complaint.content }}</text>
				</view>

				<!-- 回复内容 -->
				<view class="reply-section" v-if="complaint.response">
					<view class="reply-header">
						<text class="reply-label">客服回复</text>
						<text class="reply-time">{{ formatTime(complaint.processTime) }}</text>
					</view>
					<view class="reply-content">
						<text class="reply-text">{{ complaint.response }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<text class="empty-icon">📋</text>
			<text class="empty-text">暂无投诉记录</text>
			<text class="empty-desc">如有问题可随时投诉</text>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return {
			activeTab: null,
			tabs: [
				{ value: null, name: '全部' },
				{ value: 0, name: '待处理' },
				{ value: 1, name: '处理中' },
				{ value: 2, name: '已解决' },
				{ value: 3, name: '已关闭' }
			],
			complaintList: [],
			loading: false,
			page: 1,
			pageSize: 10
		}
	},
	onLoad() {
		this.loadComplaintList()
	},
	onPullDownRefresh() {
		this.loadComplaintList(true).then(() => {
			uni.stopPullDownRefresh()
		})
	},
	methods: {
		// 加载投诉列表
		async loadComplaintList(refresh = false) {
			if (refresh) {
				this.page = 1
				this.complaintList = []
			}

			if (this.loading) return
			this.loading = true

			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				const params = {
					page: this.page,
					size: this.pageSize
				}

				if (this.activeTab !== null) {
					params.status = this.activeTab
				}

				const res = await api.order.getMyComplaints(userInfo.id, params)
				if (res.code === 200) {
					const newList = res.data.records || []
					if (refresh) {
						this.complaintList = newList
					} else {
						this.complaintList.push(...newList)
					}
				}
			} catch (error) {
				console.error('加载投诉列表失败:', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},

		// 切换标签
		switchTab(tab) {
			this.activeTab = tab
			this.loadComplaintList(true)
		},

		// 显示详情
		showDetail(complaint) {
			uni.showModal({
				title: this.getComplaintTypeName(complaint.complaintType),
				content: `投诉内容：${complaint.content}\n\n${complaint.response ? '客服回复：' + complaint.response : '等待处理中...'}`,
				showCancel: false,
				confirmText: '知道了'
			})
		},

		// 获取投诉类型名称
		getComplaintTypeName(type) {
			const typeMap = {
				1: '商品质量',
				2: '服务态度',
				3: '配送问题',
				4: '其他问题'
			}
			return typeMap[type] || '未知'
		},

		// 获取状态名称
		getStatusName(status) {
			const statusMap = {
				0: '待处理',
				1: '处理中',
				2: '已解决',
				3: '已关闭'
			}
			return statusMap[status] || '未知'
		},

		// 获取状态样式类
		getStatusClass(status) {
			const classMap = {
				0: 'status-pending',
				1: 'status-processing',
				2: 'status-resolved',
				3: 'status-closed'
			}
			return classMap[status] || ''
		},

		// 格式化时间
		formatTime(time) {
			if (!time) return ''
			const date = new Date(time)
			const now = new Date()
			const diff = now - date
			
			// 小于1分钟
			if (diff < 60000) {
				return '刚刚'
			}
			// 小于1小时
			if (diff < 3600000) {
				return Math.floor(diff / 60000) + '分钟前'
			}
			// 小于1天
			if (diff < 86400000) {
				return Math.floor(diff / 3600000) + '小时前'
			}
			// 小于7天
			if (diff < 604800000) {
				return Math.floor(diff / 86400000) + '天前'
			}
			
			// 超过7天显示完整日期
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

/* 标签页 */
.tabs {
	display: flex;
	background: white;
	padding: 20rpx 0;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 16rpx 0;
	position: relative;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 40rpx;
	height: 4rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 2rpx;
}

.tab-text {
	font-size: 28rpx;
	color: #666;
}

.tab-item.active .tab-text {
	color: #ff6b35;
	font-weight: 600;
}

/* 投诉列表 */
.complaint-list {
	padding: 20rpx;
}

.complaint-item {
	background: white;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.complaint-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
}

.type-tag {
	padding: 8rpx 16rpx;
	background: #f0f0f0;
	border-radius: 8rpx;
	font-size: 24rpx;
	color: #666;
}

.status-tag {
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	font-size: 24rpx;
	font-weight: 500;
}

.status-pending {
	background: #fff7e6;
	color: #faad14;
}

.status-processing {
	background: #e6f7ff;
	color: #1890ff;
}

.status-resolved {
	background: #f6ffed;
	color: #52c41a;
}

.status-closed {
	background: #f5f5f5;
	color: #999;
}

.order-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
	padding-bottom: 16rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
	font-size: 24rpx;
	color: #666;
}

.create-time {
	font-size: 22rpx;
	color: #999;
}

.complaint-content {
	margin-bottom: 16rpx;
}

.content-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 3;
	overflow: hidden;
}

.reply-section {
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 16rpx;
	border-left: 4rpx solid #ff6b35;
}

.reply-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.reply-label {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: 600;
}

.reply-time {
	font-size: 22rpx;
	color: #999;
}

.reply-content {
	margin-top: 8rpx;
}

.reply-text {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
	text-align: center;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.6;
}

.empty-text {
	font-size: 32rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 16rpx;
}

.empty-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.5;
}
</style>
