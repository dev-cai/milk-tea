import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

// 获取销售趋势数据
export function getSalesTrend(params) {
  return request({
    url: '/admin/dashboard/sales-trend',
    method: 'get',
    params
  })
}

// 获取商品销售排行
export function getProductRanking(params) {
  return request({
    url: '/admin/dashboard/product-ranking',
    method: 'get',
    params
  })
}

// 获取用户增长数据
export function getUserGrowth(params) {
  return request({
    url: '/admin/dashboard/user-growth',
    method: 'get',
    params
  })
}

// 获取预警信息
export function getAlerts() {
  return request({
    url: '/admin/dashboard/alerts',
    method: 'get'
  })
}

// 获取订单状态分布
export function getOrderStatus() {
  return request({
    url: '/admin/dashboard/order-status',
    method: 'get'
  })
}
