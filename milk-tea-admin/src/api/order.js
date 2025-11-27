import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/admin/order/page',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/admin/order/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 处理退款
export function handleRefund(id, approve) {
  return request({
    url: `/admin/order/${id}/refund`,
    method: 'put',
    data: { approve }
  })
}

// 获取订单统计数据
export function getOrderStatistics() {
  return request({
    url: '/admin/order/statistics',
    method: 'get'
  })
}

// 导出订单数据
export function exportOrders(params) {
  return request({
    url: '/admin/order/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}


// 获取退款申请列表
export function getRefundRequests(params) {
  return request({
    url: '/admin/order/refund/requests',
    method: 'get',
    params
  })
}

// 处理退款申请
export function processRefund(id, data) {
  return request({
    url: `/admin/order/refund/${id}/process`,
    method: 'put',
    data
  })
}

// 获取投诉列表
export function getComplaints(params) {
  return request({
    url: '/admin/order/complaints',
    method: 'get',
    params
  })
}

// 处理投诉
export function processComplaint(id, data) {
  return request({
    url: `/admin/order/complaint/${id}/process`,
    method: 'put',
    data
  })
}

// 获取售后统计
export function getAftersaleStatistics() {
  return request({
    url: '/admin/order/aftersale/statistics',
    method: 'get'
  })
}

// 获取反馈列表
export function getFeedbackList(params) {
  return request({
    url: '/api/admin/feedback/list',
    method: 'get',
    params
  })
}

// 回复反馈
export function replyFeedback(data) {
  return request({
    url: '/api/admin/feedback/reply',
    method: 'post',
    data
  })
}

// 获取反馈详情
export function getFeedbackDetail(id) {
  return request({
    url: `/api/admin/feedback/${id}`,
    method: 'get'
  })
}

// 更新反馈状态
export function updateFeedbackStatus(data) {
  return request({
    url: '/api/admin/feedback/status',
    method: 'put',
    data
  })
}
