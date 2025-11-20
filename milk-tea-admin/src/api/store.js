import request from '@/utils/request'

/**
 * 获取门店统计信息
 */
export function getStoreStats() {
  return request({
    url: '/admin/store/stats',
    method: 'get'
  })
}

/**
 * 获取门店列表（分页）
 */
export function getStoreList(params) {
  return request({
    url: '/admin/store/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有门店列表（不分页）
 */
export function getAllStores() {
  return request({
    url: '/admin/store/all',
    method: 'get'
  })
}

/**
 * 获取门店详情
 */
export function getStoreDetail(id) {
  return request({
    url: `/admin/store/${id}`,
    method: 'get'
  })
}

/**
 * 添加门店
 */
export function addStore(data) {
  return request({
    url: '/admin/store',
    method: 'post',
    data
  })
}

/**
 * 更新门店信息
 */
export function updateStore(data) {
  return request({
    url: '/admin/store',
    method: 'put',
    data
  })
}

/**
 * 删除门店
 */
export function deleteStore(id) {
  return request({
    url: `/admin/store/${id}`,
    method: 'delete'
  })
}

/**
 * 切换门店状态
 */
export function toggleStoreStatus(id) {
  return request({
    url: `/admin/store/${id}/toggle-status`,
    method: 'put'
  })
}

/**
 * 更新门店员工数量
 */
export function updateStoreStaffCount(id, count) {
  return request({
    url: `/admin/store/${id}/staff-count`,
    method: 'put',
    data: { count }
  })
}
