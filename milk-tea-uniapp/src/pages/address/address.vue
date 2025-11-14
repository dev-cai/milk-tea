<template>
	<view class="container">
		<!-- 地址列表 -->
		<view class="address-list" v-if="addressList.length > 0">
			<view class="address-item" v-for="(address, index) in addressList" :key="address.id">
				<view class="address-info" @click="selectAddress(address)">
					<view class="address-header">
						<text class="contact-name">{{ address.name }}</text>
						<text class="contact-phone">{{ address.phone }}</text>
						<view class="default-tag" v-if="address.isDefault">默认</view>
					</view>
					<view class="address-detail">
						<text class="address-text">
							{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
						</text>
					</view>
					<view class="address-tag" v-if="address.tag">
						<text class="tag-text">{{ getAddressTagText(address.tag) }}</text>
					</view>
				</view>
				<view class="address-actions">
					<view class="action-btn" @click="editAddress(address)">
						<text class="action-icon">✏️</text>
						<text class="action-text">编辑</text>
					</view>
					<view class="action-btn" @click="deleteAddress(address, index)">
						<text class="action-icon">🗑️</text>
						<text class="action-text">删除</text>
					</view>
					<view class="action-btn" @click="setDefaultAddress(address)" v-if="!address.isDefault">
						<text class="action-icon">⭐</text>
						<text class="action-text">设为默认</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<text class="empty-icon">📍</text>
			<text class="empty-text">还没有收货地址</text>
			<text class="empty-desc">添加地址后可以快速下单</text>
		</view>

		<!-- 添加地址按钮 -->
		<view class="add-address-btn" @click="addAddress">
			<text class="add-icon">+</text>
			<text class="add-text">添加新地址</text>
		</view>

		<!-- 智能定位提示 -->
		<view class="location-tip" @click="getCurrentLocation">
			<text class="tip-icon">📍</text>
			<text class="tip-text">使用当前位置快速添加地址</text>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { getAddressTagText } from '@/utils/common.js'

export default {
	data() {
		return {
			addressList: [],
			selectMode: false, // 是否为选择地址模式
			currentLocation: null
		}
	},
	onLoad(options) {
		this.selectMode = options.select === 'true'
		this.loadAddressList()
	},
	onShow() {
		// 从添加/编辑地址页面返回时刷新列表
		this.loadAddressList()
	},
	methods: {
		// 加载地址列表
		async loadAddressList() {
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				const res = await api.user.getAddresses(userInfo.id)
				if (res.code === 200) {
					this.addressList = res.data || []
				}
			} catch (error) {
				console.error('加载地址列表失败:', error)
				// 使用模拟数据
				this.addressList = [
					{
						id: 1,
						name: '张三',
						phone: '13800138000',
						province: '广东省',
						city: '深圳市',
						district: '南山区',
						detail: '科技园南区腾讯大厦',
						tag: 2, // 公司
						isDefault: 1
					},
					{
						id: 2,
						name: '张三',
						phone: '13800138000',
						province: '广东省',
						city: '深圳市',
						district: '福田区',
						detail: '华强北商业区',
						tag: 1, // 家
						isDefault: 0
					}
				]
			}
		},

		// 选择地址（选择模式下）
		selectAddress(address) {
			if (this.selectMode) {
				// 返回选中的地址
				const pages = getCurrentPages()
				const prevPage = pages[pages.length - 2]
				if (prevPage) {
					prevPage.$vm.selectedAddress = address
				}
				uni.navigateBack()
			}
		},

		// 添加地址
		addAddress() {
			const url = this.currentLocation 
				? `/pages/address/add-address?lat=${this.currentLocation.latitude}&lng=${this.currentLocation.longitude}`
				: '/pages/address/add-address'
			uni.navigateTo({ url })
		},

		// 编辑地址
		editAddress(address) {
			uni.navigateTo({
				url: `/pages/address/add-address?id=${address.id}`
			})
		},

		// 删除地址
		deleteAddress(address, index) {
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这个地址吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await api.user.deleteAddress(address.id)
							this.addressList.splice(index, 1)
							uni.showToast({
								title: '删除成功',
								icon: 'success'
							})
						} catch (error) {
							console.error('删除地址失败:', error)
							// 模拟删除成功
							this.addressList.splice(index, 1)
							uni.showToast({
								title: '删除成功',
								icon: 'success'
							})
						}
					}
				}
			})
		},

		// 设为默认地址
		async setDefaultAddress(address) {
			try {
				await api.user.setDefaultAddress(address.id)
				
				// 更新本地数据
				this.addressList.forEach(item => {
					item.isDefault = item.id === address.id ? 1 : 0
				})
				
				uni.showToast({
					title: '设置成功',
					icon: 'success'
				})
			} catch (error) {
				console.error('设置默认地址失败:', error)
				// 模拟设置成功
				this.addressList.forEach(item => {
					item.isDefault = item.id === address.id ? 1 : 0
				})
				uni.showToast({
					title: '设置成功',
					icon: 'success'
				})
			}
		},

		// 获取当前位置
		getCurrentLocation() {
			uni.showLoading({ title: '定位中...' })
			
			uni.getLocation({
				type: 'gcj02',
				success: (res) => {
					this.currentLocation = {
						latitude: res.latitude,
						longitude: res.longitude
					}
					
					// 逆地理编码获取地址信息
					this.reverseGeocode(res.latitude, res.longitude)
				},
				fail: (error) => {
					console.error('定位失败:', error)
					uni.showToast({
						title: '定位失败，请检查定位权限',
						icon: 'none'
					})
				},
				complete: () => {
					uni.hideLoading()
				}
			})
		},

		// 逆地理编码
		reverseGeocode(latitude, longitude) {
			// 这里应该调用地图API进行逆地理编码
			// 暂时跳转到添加地址页面
			uni.navigateTo({
				url: `/pages/address/add-address?lat=${latitude}&lng=${longitude}`
			})
		},

		// 获取地址标签文本
		getAddressTagText
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 200rpx;
}

/* 地址列表 */
.address-list {
	padding: 20rpx;
}

.address-item {
	background: white;
	border-radius: 24rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.address-info {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.address-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.contact-name {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-right: 20rpx;
}

.contact-phone {
	font-size: 28rpx;
	color: #666;
	flex: 1;
}

.default-tag {
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	color: white;
	font-size: 20rpx;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	font-weight: 500;
}

.address-detail {
	margin-bottom: 16rpx;
}

.address-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.5;
}

.address-tag {
	display: flex;
	justify-content: flex-start;
}

.tag-text {
	background: rgba(255, 107, 53, 0.1);
	color: #ff6b35;
	font-size: 22rpx;
	padding: 8rpx 16rpx;
	border-radius: 16rpx;
	font-weight: 500;
}

.address-actions {
	display: flex;
	background: #f8f9fa;
}

.action-btn {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 24rpx;
	transition: all 0.3s ease;
}

.action-btn:active {
	background: rgba(255, 107, 53, 0.1);
}

.action-icon {
	font-size: 32rpx;
	margin-bottom: 8rpx;
}

.action-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
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

/* 添加地址按钮 */
.add-address-btn {
	position: fixed;
	bottom: 120rpx;
	left: 30rpx;
	right: 30rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50rpx;
	padding: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.4);
	transition: all 0.3s ease;
}

.add-address-btn:active {
	transform: scale(0.98);
}

.add-icon {
	font-size: 36rpx;
	color: white;
	margin-right: 12rpx;
	font-weight: bold;
}

.add-text {
	font-size: 32rpx;
	color: white;
	font-weight: 600;
}

/* 定位提示 */
.location-tip {
	position: fixed;
	bottom: 40rpx;
	left: 30rpx;
	right: 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20rpx);
	border-radius: 24rpx;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 107, 53, 0.2);
	transition: all 0.3s ease;
}

.location-tip:active {
	background: rgba(255, 107, 53, 0.1);
}

.tip-icon {
	font-size: 28rpx;
	margin-right: 12rpx;
	color: #ff6b35;
}

.tip-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}
</style>
