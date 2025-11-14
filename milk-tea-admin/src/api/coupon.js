import request from '@/utils/request'

// 获取优惠券列表
export function getCouponList(params) {
  return request({
    url: '/admin/coupon/page',
    method: 'get',
    params
  })
}

// 获取优惠券详情
export function getCouponDetail(id) {
  return request({
    url: `/admin/coupon/${id}`,
    method: 'get'
  })
}

// 添加优惠券
export function addCoupon(data) {
  return request({
    url: '/admin/coupon',
    method: 'post',
    data
  })
}

// 更新优惠券
export function updateCoupon(id, data) {
  return request({
    url: `/admin/coupon/${id}`,
    method: 'put',
    data
  })
}

// 删除优惠券
export function deleteCoupon(id) {
  return request({
    url: `/admin/coupon/${id}`,
    method: 'delete'
  })
}

// 获取优惠券使用记录
export function getCouponUsage(id, params) {
  return request({
    url: `/admin/coupon/${id}/usage`,
    method: 'get',
    params
  })
}

// 批量删除优惠券
export function batchDeleteCoupons(ids) {
  return request({
    url: '/admin/coupon/batch',
    method: 'delete',
    data: ids
  })
}
