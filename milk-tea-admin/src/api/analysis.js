import request from '@/utils/request'

// 获取会员分析概览
export function getMemberOverview() {
  return request({
    url: '/admin/analysis/member/overview',
    method: 'get'
  })
}

// 获取会员消费分析
export function getMemberConsumption(days = 30) {
  return request({
    url: '/admin/analysis/member/consumption',
    method: 'get',
    params: { days }
  })
}

// 获取会员等级分布
export function getMemberLevelDistribution() {
  return request({
    url: '/admin/analysis/member/level-distribution',
    method: 'get'
  })
}

// 获取会员行为分析
export function getMemberBehavior() {
  return request({
    url: '/admin/analysis/member/behavior',
    method: 'get'
  })
}

// 获取会员活跃度趋势
export function getMemberActivityTrend(days = 7) {
  return request({
    url: '/admin/analysis/member/activity-trend',
    method: 'get',
    params: { days }
  })
}

// 获取会员分层
export function getMemberSegmentation() {
  return request({
    url: '/admin/analysis/member/segmentation',
    method: 'get'
  })
}

// 获取VIP客户列表
export function getVipCustomers(limit = 20) {
  return request({
    url: '/admin/analysis/vip-customers',
    method: 'get',
    params: { limit }
  })
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/admin/analysis/customer/${id}`,
    method: 'get'
  })
}
