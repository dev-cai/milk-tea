import request from '@/utils/request'

/**
 * 获取所有配置
 */
export function getAllConfigs() {
  return request({
    url: '/admin/system/config/all',
    method: 'get'
  })
}

/**
 * 获取配置映射
 */
export function getConfigMap() {
  return request({
    url: '/admin/system/config/map',
    method: 'get'
  })
}

/**
 * 获取单个配置
 */
export function getConfig(key) {
  return request({
    url: `/admin/system/config/${key}`,
    method: 'get'
  })
}

/**
 * 更新单个配置
 */
export function updateConfig(key, value) {
  return request({
    url: `/admin/system/config/${key}`,
    method: 'put',
    data: { value }
  })
}

/**
 * 批量更新配置
 */
export function batchUpdateConfig(configMap) {
  return request({
    url: '/admin/system/config/batch',
    method: 'put',
    data: configMap
  })
}

/**
 * 数据备份
 */
export function backupData() {
  return request({
    url: '/admin/system/backup',
    method: 'post'
  })
}

/**
 * 获取最后备份时间
 */
export function getLastBackupTime() {
  return request({
    url: '/admin/system/backup/last-time',
    method: 'get'
  })
}

/**
 * 数据恢复
 */
export function restoreData(filePath) {
  return request({
    url: '/admin/system/restore',
    method: 'post',
    data: { file: filePath }
  })
}

/**
 * 清理缓存
 */
export function clearCache() {
  return request({
    url: '/admin/system/cache/clear',
    method: 'post'
  })
}

/**
 * 获取系统信息
 */
export function getSystemInfo() {
  return request({
    url: '/admin/system/info',
    method: 'get'
  })
}
