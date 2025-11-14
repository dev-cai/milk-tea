import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/admin/user/page',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 调整用户会员等级
export function updateMemberLevel(id, memberLevel) {
  return request({
    url: `/admin/user/${id}/member-level`,
    method: 'put',
    data: { memberLevel }
  })
}

// 调整用户积分
export function updateUserPoints(id, points, type) {
  return request({
    url: `/admin/user/${id}/points`,
    method: 'put',
    data: { points, type }
  })
}

// 获取用户消费统计
export function getUserStatistics(id) {
  return request({
    url: `/admin/user/${id}/statistics`,
    method: 'get'
  })
}
