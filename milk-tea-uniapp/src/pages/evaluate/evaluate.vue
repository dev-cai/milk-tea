<template>
	<view class="page">
		<view class="panel">
			<text class="title">订单评价</text>
			<text class="order-no" v-if="orderNo">订单号：{{ orderNo }}</text>
			<view class="rating-row">
				<text class="label">满意度</text>
				<view class="stars">
					<text v-for="star in 5" :key="star" class="star" :class="{ active: star <= rating }" @click="rating = star">★</text>
				</view>
			</view>
			<textarea v-model="content" maxlength="500" placeholder="说说这次饮品的体验吧（选填）" class="textarea" />
			<button class="submit" :loading="submitting" @click="submit">提交评价</button>
		</view>
	</view>
</template>

<script>
import api from '@/utils/api.js'

export default {
	data() {
		return { orderId: null, orderNo: '', rating: 5, content: '', submitting: false }
	},
	onLoad(options) {
		this.orderId = options.orderId
		this.loadOrder()
	},
	methods: {
		async loadOrder() {
			try {
				const res = await api.order.getDetail(this.orderId)
				this.orderNo = res.data?.order?.orderNo || ''
			} catch (error) {
				uni.showToast({ title: '订单加载失败', icon: 'none' })
			}
		},
		async submit() {
			if (this.submitting) return
			this.submitting = true
			try {
				await api.order.evaluate(this.orderId, { rating: this.rating, content: this.content })
				uni.showToast({ title: '评价成功', icon: 'success' })
				setTimeout(() => uni.navigateBack(), 500)
			} catch (error) {
				uni.showToast({ title: error.message || error.data?.message || '评价失败', icon: 'none' })
			} finally {
				this.submitting = false
			}
		}
	}
}
</script>

<style lang="scss">
.page { min-height: 100vh; padding: 30rpx; background: #f8f9fa; box-sizing: border-box; }
.panel { padding: 36rpx 30rpx; background: #fff; border-radius: 16rpx; }
.title { display: block; color: #222; font-size: 38rpx; font-weight: 600; }
.order-no { display: block; margin-top: 14rpx; color: #999; font-size: 24rpx; }
.rating-row { display: flex; align-items: center; margin: 48rpx 0 32rpx; }
.label { color: #444; font-size: 28rpx; }
.stars { display: flex; margin-left: 34rpx; }
.star { margin-right: 14rpx; color: #ddd; font-size: 52rpx; line-height: 1; }
.star.active { color: #ffb000; }
.textarea { width: 100%; min-height: 240rpx; padding: 24rpx; background: #f7f7f7; border-radius: 10rpx; box-sizing: border-box; font-size: 28rpx; }
.submit { margin-top: 42rpx; color: #fff; background: #ff6b35; border-radius: 44rpx; }
</style>
