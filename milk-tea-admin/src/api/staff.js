import request from '@/utils/request'

/**
 * 获取员工统计信息
 */
export function getStaffStats() {
  return request({
    url: '/admin/staff/stats',
    method: 'get'
  })
}

/**
 * 获取员工列表
 */
export function getStaffList(params) {
  return request({
    url: '/admin/staff/list',
    method: 'get',
    params
  })
}

/**
 * 获取员工详情
 */
export function getStaffDetail(id) {
  return request({
    url: `/admin/staff/${id}`,
    method: 'get'
  })
}

/**
 * 添加员工
 */
export function addStaff(data) {
  return request({
    url: '/admin/staff',
    method: 'post',
    data
  })
}

/**
 * 更新员工信息
 */
export function updateStaff(data) {
  return request({
    url: '/admin/staff',
    method: 'put',
    data
  })
}

/**
 * 删除员工
 */
export function deleteStaff(id) {
  return request({
    url: `/admin/staff/${id}`,
    method: 'delete'
  })
}

/**
 * 切换员工状态
 */
export function toggleStaffStatus(id) {
  return request({
    url: `/admin/staff/${id}/toggle-status`,
    method: 'put'
  })
}

/**
 * 重置员工密码
 */
export function resetStaffPassword(id) {
  return request({
    url: `/admin/staff/${id}/reset-password`,
    method: 'put'
  })
}

/**
 * 更新员工权限
 */
export function updateStaffPermissions(id, permissions) {
  return request({
    url: `/admin/staff/${id}/permissions`,
    method: 'put',
    data: { permissions }
  })
}
