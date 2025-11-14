import request from '@/utils/request'

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/admin/product/page',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'get'
  })
}

// 添加商品
export function addProduct(data) {
  return request({
    url: '/admin/product',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(id, data) {
  return request({
    url: `/admin/product/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

// 批量删除商品
export function batchDeleteProducts(ids) {
  return request({
    url: '/admin/product/batch',
    method: 'delete',
    data: ids
  })
}

// 更新商品状态
export function updateProductStatus(id, status) {
  return request({
    url: `/admin/product/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 批量更新商品状态
export function batchUpdateProductStatus(ids, status) {
  return request({
    url: '/admin/product/batch/status',
    method: 'put',
    data: { ids, status }
  })
}

// 获取商品分类
export function getProductCategories() {
  return request({
    url: '/admin/category/list',
    method: 'get'
  })
}
