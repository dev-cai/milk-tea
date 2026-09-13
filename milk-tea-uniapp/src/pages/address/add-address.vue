<template>
	<view class="container">
		<view class="form-container">
			<!-- 联系人信息 -->
			<view class="form-section">
				<view class="section-title">
					<text class="title-icon">👤</text>
					<text class="title-text">联系人信息</text>
				</view>
				
				<view class="form-item">
					<text class="form-label">姓名</text>
					<input 
						class="form-input" 
						type="text" 
						placeholder="请输入收货人姓名" 
						v-model="formData.name"
						maxlength="20"
					/>
				</view>
				
				<view class="form-item">
					<text class="form-label">手机号</text>
					<input 
						class="form-input" 
						type="number" 
						placeholder="请输入手机号" 
						v-model="formData.phone"
						maxlength="11"
					/>
				</view>
			</view>

			<!-- 地址信息 -->
			<view class="form-section">
				<view class="section-title">
					<text class="title-icon">📍</text>
					<text class="title-text">地址信息</text>
				</view>
				
				<!-- 地区选择 -->
				<view class="form-item" @click="showRegionPicker">
					<text class="form-label">所在地区</text>
					<view class="region-display">
						<text class="region-text" v-if="selectedRegion.length > 0">
							{{ selectedRegion.join(' ') }}
						</text>
						<text class="region-placeholder" v-else>请选择省市区</text>
						<text class="region-arrow">›</text>
					</view>
				</view>
				
				<!-- 详细地址 -->
				<view class="form-item">
					<text class="form-label">详细地址</text>
					<textarea 
						class="form-textarea" 
						placeholder="请输入详细地址，如街道、楼栋号、门牌号等" 
						v-model="formData.detail"
						maxlength="100"
						auto-height
					/>
				</view>
			</view>

			<!-- 地址标签 -->
			<view class="form-section">
				<view class="section-title">
					<text class="title-icon">🏷️</text>
					<text class="title-text">地址标签</text>
				</view>
				
				<view class="tag-list">
					<view 
						class="tag-item" 
						:class="{ active: formData.tag === tag.value }"
						v-for="tag in addressTags" 
						:key="tag.value"
						@click="selectTag(tag.value)"
					>
						<text class="tag-icon">{{ tag.icon }}</text>
						<text class="tag-name">{{ tag.name }}</text>
					</view>
				</view>
			</view>

			<!-- 设置选项 -->
			<view class="form-section">
				<view class="form-item">
					<text class="form-label">设为默认地址</text>
					<switch 
						class="form-switch" 
						:checked="formData.isDefault" 
						@change="onDefaultChange"
						color="#ff6b35"
					/>
				</view>
			</view>

			<!-- 当前位置信息 -->
			<view class="location-info" v-if="currentLocation">
				<view class="location-header">
					<text class="location-icon">📍</text>
					<text class="location-title">当前位置</text>
					<text class="location-refresh" @click="refreshLocation">刷新</text>
				</view>
				<text class="location-address">{{ currentLocation.address }}</text>
			</view>
		</view>

		<!-- 保存按钮 -->
		<view class="save-btn" @click="saveAddress">
			<text>{{ isEdit ? '更新地址' : '保存地址' }}</text>
		</view>

		<!-- 遮罩层 -->
		<view class="picker-mask" :class="{ show: showPicker }" @click="hidePicker"></view>
		
		<!-- 地区选择器容器 -->
		<view class="picker-container" :class="{ show: showPicker }">
			<!-- 选择器工具栏 -->
			<view class="picker-toolbar">
				<text class="picker-cancel" @click="hidePicker">取消</text>
				<text class="picker-title">选择地区</text>
				<text class="picker-confirm" @click="confirmRegion">确定</text>
			</view>
			
			<!-- 地区选择器 -->
			<picker-view 
				class="region-picker"
				:value="pickerValue" 
				@change="onPickerChange"
			>
				<picker-view-column>
					<view v-for="(province, index) in provinces" :key="index">
						{{ province.name }}
					</view>
				</picker-view-column>
				<picker-view-column>
					<view v-for="(city, index) in cities" :key="index">
						{{ city.name }}
					</view>
				</picker-view-column>
				<picker-view-column>
					<view v-for="(district, index) in districts" :key="index">
						{{ district.name }}
					</view>
				</picker-view-column>
			</picker-view>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'
import { validatePhone } from '@/utils/common.js'
import { regionData } from '@/utils/region.js' // 需要创建地区数据文件

export default {
	data() {
		return {
			isEdit: false,
			addressId: null,
			formData: {
				name: '',
				phone: '',
				province: '',
				city: '',
				district: '',
				detail: '',
				tag: 1, // 默认为家
				isDefault: false
			},
			addressTags: [
				{ value: 1, name: '家', icon: '🏠' },
				{ value: 2, name: '公司', icon: '🏢' },
				{ value: 3, name: '学校', icon: '🎓' },
				{ value: 4, name: '其他', icon: '📍' }
			],
			currentLocation: null,
			showPicker: false,
			selectedRegion: [],
			pickerValue: [0, 0, 0],
			provinces: [],
			cities: [],
			districts: []
		}
	},
	onLoad(options) {
		this.addressId = options.id
		this.isEdit = !!options.id
		
		// 如果有经纬度参数，获取位置信息
		if (options.lat && options.lng) {
			this.getCurrentLocationInfo(options.lat, options.lng)
		}
		
		// 初始化地区数据
		this.initRegionData()
		
		// 如果是编辑模式，加载地址信息
		if (this.isEdit) {
			this.loadAddressInfo()
		}
	},
	methods: {
		// 初始化地区数据
		initRegionData() {
			this.provinces = regionData
			this.updateCities()
		},

		// 更新城市数据
		updateCities() {
			const provinceIndex = this.pickerValue[0]
			this.cities = this.provinces[provinceIndex]?.children || []
			this.pickerValue[1] = Math.min(this.pickerValue[1], Math.max(this.cities.length - 1, 0))
			this.updateDistricts()
		},

		// 更新区县数据
		updateDistricts() {
			const cityIndex = this.pickerValue[1]
			this.districts = this.cities[cityIndex]?.children || []
			this.pickerValue[2] = Math.min(this.pickerValue[2], Math.max(this.districts.length - 1, 0))
		},

		// 加载地址信息（编辑模式）
		async loadAddressInfo() {
			try {
				const res = await api.user.getAddress(this.addressId)
				const address = res.data
				this.formData = { ...this.formData, ...address, isDefault: address.isDefault === 1 }
				this.selectedRegion = [address.province, address.city, address.district]
				const pi = this.provinces.findIndex(item => item.name === address.province)
				this.pickerValue[0] = Math.max(pi, 0)
				this.updateCities()
				this.pickerValue[1] = Math.max(this.cities.findIndex(item => item.name === address.city), 0)
				this.updateDistricts()
				this.pickerValue[2] = Math.max(this.districts.findIndex(item => item.name === address.district), 0)
			} catch (error) {
				console.error('加载地址信息失败:', error)
				uni.showToast({ title: error.message || '地址加载失败', icon: 'none' })
			}
		},

		// 获取当前位置信息
		getCurrentLocationInfo(lat, lng) {
			this.currentLocation = {
				latitude: lat,
				longitude: lng,
				address: `纬度 ${Number(lat).toFixed(6)}，经度 ${Number(lng).toFixed(6)}`
			}
		},

		// 刷新位置
		refreshLocation() {
			uni.getLocation({
				type: 'gcj02',
				success: (res) => {
					this.getCurrentLocationInfo(res.latitude, res.longitude)
				},
				fail: () => {
					uni.showToast({
						title: '定位失败',
						icon: 'none'
					})
				}
			})
		},

		// 显示地区选择器
		showRegionPicker() {
			this.showPicker = true
		},

		// 隐藏地区选择器
		hidePicker() {
			this.showPicker = false
		},

		// 选择器值改变
		onPickerChange(e) {
			this.pickerValue = e.detail.value
			this.updateCities()
		},

		// 确认地区选择
		confirmRegion() {
			const province = this.provinces[this.pickerValue[0]]
			const city = this.cities[this.pickerValue[1]]
			const district = this.districts[this.pickerValue[2]]
			
			this.selectedRegion = [province.name, city.name, district.name]
			this.formData.province = province.name
			this.formData.city = city.name
			this.formData.district = district.name
			
			this.hidePicker()
		},

		// 选择标签
		selectTag(tagValue) {
			this.formData.tag = tagValue
		},

		// 默认地址开关改变
		onDefaultChange(e) {
			this.formData.isDefault = e.detail.value
		},

		// 验证表单
		validateForm() {
			if (!this.formData.name.trim()) {
				uni.showToast({
					title: '请输入收货人姓名',
					icon: 'none'
				})
				return false
			}

			if (!validatePhone(this.formData.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return false
			}

			if (!this.formData.province || !this.formData.city || !this.formData.district) {
				uni.showToast({
					title: '请选择所在地区',
					icon: 'none'
				})
				return false
			}

			if (!this.formData.detail.trim()) {
				uni.showToast({
					title: '请输入详细地址',
					icon: 'none'
				})
				return false
			}

			return true
		},

		// 保存地址
		async saveAddress() {
			if (!this.validateForm()) return

			try {
				uni.showLoading({ title: '保存中...' })

				const userInfo = uni.getStorageSync('userInfo')
				const addressData = {
					...this.formData,
					userId: userInfo.id,
					isDefault: this.formData.isDefault ? 1 : 0
				}

				if (this.isEdit) {
					// 更新地址时需要包含 id 字段
					addressData.id = this.addressId
					await api.user.updateAddress(addressData)
				} else {
					await api.user.addAddress(addressData)
				}

				uni.showToast({
					title: '保存成功',
					icon: 'success'
				})

				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (error) {
				console.error('保存地址失败:', error)
				uni.showToast({ title: error.message || '保存失败', icon: 'none' })
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
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

.form-container {
	padding: 20rpx;
}

/* 表单区块 */
.form-section {
	background: white;
	border-radius: 24rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-title {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(247, 147, 30, 0.05) 100%);
}

.title-icon {
	font-size: 32rpx;
	margin-right: 12rpx;
}

.title-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

/* 表单项 */
.form-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
	border-bottom: none;
}

.form-label {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	width: 160rpx;
	flex-shrink: 0;
}

.form-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	text-align: right;
}

.form-input::placeholder {
	color: #999;
}

.form-textarea {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	min-height: 80rpx;
	text-align: right;
}

.form-textarea::placeholder {
	color: #999;
}

.form-switch {
	transform: scale(0.8);
}

/* 地区选择 */
.region-display {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: flex-end;
}

.region-text {
	font-size: 28rpx;
	color: #333;
}

.region-placeholder {
	font-size: 28rpx;
	color: #999;
}

.region-arrow {
	font-size: 24rpx;
	color: #999;
	margin-left: 12rpx;
}

/* 标签选择 */
.tag-list {
	display: flex;
	padding: 20rpx 30rpx 30rpx;
	gap: 20rpx;
}

.tag-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 16rpx;
	border: 2rpx solid #f0f0f0;
	transition: all 0.3s ease;
	min-width: 120rpx;
}

.tag-item.active {
	border-color: #ff6b35;
	background: rgba(255, 107, 53, 0.1);
}

.tag-icon {
	font-size: 32rpx;
	margin-bottom: 8rpx;
}

.tag-name {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

.tag-item.active .tag-name {
	color: #ff6b35;
}

/* 位置信息 */
.location-info {
	background: rgba(255, 107, 53, 0.1);
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	border: 1rpx solid rgba(255, 107, 53, 0.2);
}

.location-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.location-icon {
	font-size: 28rpx;
	margin-right: 12rpx;
	color: #ff6b35;
}

.location-title {
	font-size: 26rpx;
	color: #ff6b35;
	font-weight: 600;
	flex: 1;
}

.location-refresh {
	font-size: 24rpx;
	color: #ff6b35;
	padding: 8rpx 16rpx;
	border-radius: 12rpx;
	background: rgba(255, 255, 255, 0.8);
}

.location-address {
	font-size: 24rpx;
	color: #666;
	line-height: 1.5;
}

/* 保存按钮 */
.save-btn {
	position: fixed;
	bottom: 40rpx;
	left: 30rpx;
	right: 30rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
	border-radius: 50rpx;
	padding: 32rpx;
	text-align: center;
	box-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.4);
	transition: all 0.3s ease;
}

.save-btn:active {
	transform: scale(0.98);
}

.save-btn text {
	font-size: 32rpx;
	color: white;
	font-weight: 600;
}

/* 地区选择器 */
.picker-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	opacity: 0;
	visibility: hidden;
	transition: all 0.3s ease;
	z-index: 999;
}

.picker-mask.show {
	opacity: 1;
	visibility: visible;
}

/* 选择器容器 */
.picker-container {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	transform: translateY(100%);
	transition: all 0.3s ease;
	z-index: 1000;
	border-radius: 32rpx 32rpx 0 0;
	overflow: hidden;
}

.picker-container.show {
	transform: translateY(0);
}

.picker-toolbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx 30rpx;
	background: #f8f9fa;
	border-bottom: 1rpx solid #e0e0e0;
}

.picker-cancel,
.picker-confirm {
	font-size: 28rpx;
	color: #ff6b35;
	font-weight: 600;
	padding: 8rpx 16rpx;
	border-radius: 12rpx;
	transition: all 0.2s ease;
}

.picker-cancel:active,
.picker-confirm:active {
	background: rgba(255, 107, 53, 0.1);
}

.picker-title {
	font-size: 30rpx;
	color: #333;
	font-weight: 700;
}

.region-picker {
	height: 500rpx;
	background: white;
}
</style>
