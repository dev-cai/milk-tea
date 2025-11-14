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
