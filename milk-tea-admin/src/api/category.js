import request from '@/utils/request'

// 获取分类列表
export function getCategoryList(params) {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params
  })
}

// 获取分类树
export function getCategoryTree() {
  return request({
    url: '/admin/category/tree',
    method: 'get'
  })
}

// 添加分类
export function addCategory(data) {
  return request({
    url: '/admin/category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id, data) {
  return request({
    url: `/admin/category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/admin/category/${id}`,
    method: 'delete'
  })
}

// 更新分类状态
export function updateCategoryStatus(id, status) {
  return request({
    url: `/admin/category/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 默认导出
export default {
  getList: getCategoryList,
  getTree: getCategoryTree,
  create: addCategory,
  update: updateCategory,
  delete: deleteCategory,
  updateStatus: updateCategoryStatus
}
