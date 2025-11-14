import request from '@/utils/request'

// 获取轮播图列表
export function getBannerList(params) {
  return request({
    url: '/admin/banner/page',
    method: 'get',
    params
  })
}

// 获取轮播图详情
export function getBannerDetail(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'get'
  })
}

// 添加轮播图
export function addBanner(data) {
  return request({
    url: '/admin/banner',
    method: 'post',
    data
  })
}

// 更新轮播图
export function updateBanner(id, data) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'put',
    data
  })
}

// 删除轮播图
export function deleteBanner(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'delete'
  })
}

// 更新轮播图状态
export function updateBannerStatus(id, status) {
  return request({
    url: `/admin/banner/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新轮播图排序
export function updateBannerSort(id, sort) {
  return request({
    url: `/admin/banner/${id}/sort`,
    method: 'put',
    data: { sort }
  })
}
