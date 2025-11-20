import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/admin/activity/page',
    method: 'get',
    params
  })
}

// 获取活动详情
export function getActivity(id) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'get'
  })
}

// 创建活动
export function createActivity(data) {
  return request({
    url: '/admin/activity',
    method: 'post',
    data
  })
}

// 更新活动
export function updateActivity(id, data) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'put',
    data
  })
}

// 删除活动
export function deleteActivity(id) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'delete'
  })
}

// 批量删除活动
export function batchDeleteActivities(ids) {
  return request({
    url: '/admin/activity/batch',
    method: 'delete',
    data: ids
  })
}

// 更新活动状态
export function updateActivityStatus(id, status) {
  return request({
    url: `/admin/activity/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取活动统计
export function getActivityStatistics() {
  return request({
    url: '/admin/activity/statistics',
    method: 'get'
  })
}

// 获取活动分析
export function getActivityAnalysis(id) {
  return request({
    url: `/admin/activity/${id}/analysis`,
    method: 'get'
  })
}

export default {
  getActivityList,
  getActivity,
  createActivity,
  updateActivity,
  deleteActivity,
  batchDeleteActivities,
  updateActivityStatus,
  getActivityStatistics,
  getActivityAnalysis
}
