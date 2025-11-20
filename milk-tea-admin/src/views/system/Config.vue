<template>
  <div class="system-config">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="basicForm" label-width="120px">
            <el-form-item label="店铺名称">
              <el-input v-model="basicForm.shopName" placeholder="请输入店铺名称" />
            </el-form-item>
            <el-form-item label="店铺Logo">
              <el-upload
                class="logo-uploader"
                action="/api/upload/image"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
              >
                <img v-if="basicForm.logo" :src="basicForm.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="basicForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="联系地址">
              <el-input v-model="basicForm.address" placeholder="请输入联系地址" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 支付配置 -->
        <el-tab-pane label="支付配置" name="payment">
          <el-form :model="paymentForm" label-width="120px">
            <el-form-item label="微信支付">
              <el-switch v-model="paymentForm.wechatEnabled" />
            </el-form-item>
            <el-form-item label="支付宝支付">
              <el-switch v-model="paymentForm.alipayEnabled" />
            </el-form-item>
            <el-form-item label="余额支付">
              <el-switch v-model="paymentForm.balanceEnabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePaymentConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 订单配置 -->
        <el-tab-pane label="订单配置" name="order">
          <el-form :model="orderForm" label-width="180px">
            <el-form-item label="自动取消时间">
              <el-input-number v-model="orderForm.autoCancelTime" :min="5" :max="60" />
              <span style="margin-left: 10px">分钟（未支付订单自动取消）</span>
            </el-form-item>
            <el-form-item label="预计制作时间">
              <el-input-number v-model="orderForm.prepareTime" :min="5" :max="120" />
              <span style="margin-left: 10px">分钟（订单预计完成时间）</span>
            </el-form-item>
            <el-form-item label="自动完成时间">
              <el-input-number v-model="orderForm.autoCompleteTime" :min="1" :max="72" />
              <span style="margin-left: 10px">小时（已取餐订单自动完成）</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveOrderConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 积分规则 -->
        <el-tab-pane label="积分规则" name="points">
          <el-form :model="pointsForm" label-width="180px">
            <el-form-item label="消费获取比例">
              <el-input-number v-model="pointsForm.earnRate" :min="0" :max="100" :precision="2" />
              <span style="margin-left: 10px">倍（消费1元获得积分数）</span>
            </el-form-item>
            <el-form-item label="积分抵扣比例">
              <el-input-number v-model="pointsForm.redeemRate" :min="0" :max="100" :precision="2" />
              <span style="margin-left: 10px">元（100积分可抵扣金额）</span>
            </el-form-item>
            <el-form-item label="最低抵扣积分">
              <el-input-number v-model="pointsForm.minRedeem" :min="0" :max="10000" :step="10" />
              <span style="margin-left: 10px">积分（低于此值不可抵扣）</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePointsConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 数据管理 -->
        <el-tab-pane label="数据管理" name="data">
          <el-form label-width="120px">
            <el-form-item label="数据备份">
              <div>
                <el-button type="primary" @click="handleBackup">
                  <el-icon><Download /></el-icon>
                  立即备份
                </el-button>
                <div style="margin-top: 10px; color: #909399; font-size: 12px">
                  最后备份时间：{{ lastBackupTime || '未备份' }}
                </div>
                <div style="margin-top: 5px; color: #909399; font-size: 12px">
                  💡 建议定期备份数据，以防数据丢失
                </div>
              </div>
            </el-form-item>
            
            <el-divider />
            
            <el-form-item label="数据恢复">
              <div>
                <el-upload
                  action="/api/admin/system/restore"
                  :on-success="handleRestoreSuccess"
                  :before-upload="beforeRestore"
                  :show-file-list="false"
                >
                  <el-button type="warning">
                    <el-icon><Upload /></el-icon>
                    选择备份文件
                  </el-button>
                </el-upload>
                <div style="margin-top: 10px; color: #f56c6c; font-size: 12px">
                  ⚠️ 数据恢复将覆盖现有数据，请谨慎操作！
                </div>
              </div>
            </el-form-item>
            
            <el-divider />
            
            <el-form-item label="缓存管理">
              <div>
                <el-button type="info" @click="handleClearCache">
                  <el-icon><Delete /></el-icon>
                  清理缓存
                </el-button>
                <div style="margin-top: 10px; color: #909399; font-size: 12px">
                  清理系统缓存可以释放内存，提升系统性能
                </div>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 系统信息 -->
        <el-tab-pane label="系统信息" name="info">
          <el-descriptions title="服务器信息" :column="2" border>
            <el-descriptions-item label="操作系统">{{ systemInfo.osName }}</el-descriptions-item>
            <el-descriptions-item label="系统版本">{{ systemInfo.osVersion }}</el-descriptions-item>
            <el-descriptions-item label="Java版本">{{ systemInfo.javaVersion }}</el-descriptions-item>
            <el-descriptions-item label="CPU核心数">{{ systemInfo.processors }}</el-descriptions-item>
            <el-descriptions-item label="总内存">{{ systemInfo.totalMemory }}</el-descriptions-item>
            <el-descriptions-item label="空闲内存">{{ systemInfo.freeMemory }}</el-descriptions-item>
            <el-descriptions-item label="最大内存">{{ systemInfo.maxMemory }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Upload, Delete } from '@element-plus/icons-vue'
import {
  getConfigMap,
  batchUpdateConfig,
  backupData,
  getLastBackupTime,
  clearCache,
  getSystemInfo
} from '@/api/system'

const activeTab = ref('basic')
const lastBackupTime = ref('')
const systemInfo = reactive({
  osName: '',
  osVersion: '',
  javaVersion: '',
  totalMemory: '',
  freeMemory: '',
  maxMemory: '',
  processors: 0
})

const basicForm = reactive({
  shopName: '',
  logo: '',
  phone: '',
  address: ''
})

const paymentForm = reactive({
  wechatEnabled: true,
  alipayEnabled: false,
  balanceEnabled: true
})

const orderForm = reactive({
  autoCancelTime: 30,
  prepareTime: 15,
  autoCompleteTime: 24
})

const pointsForm = reactive({
  earnRate: 1,
  redeemRate: 1,
  minRedeem: 100
})

onMounted(() => {
  loadConfig()
  loadLastBackupTime()
  loadSystemInfo()
})

const loadConfig = async () => {
  try {
    const res = await getConfigMap()
    if (res.code === 200) {
      const config = res.data
      
      // 基本信息
      basicForm.shopName = config.shop_name || ''
      basicForm.logo = config.shop_logo || ''
      basicForm.phone = config.shop_phone || ''
      basicForm.address = config.shop_address || ''
      
      // 支付配置
      paymentForm.wechatEnabled = config.payment_wechat_enabled === 'true'
      paymentForm.alipayEnabled = config.payment_alipay_enabled === 'true'
      paymentForm.balanceEnabled = config.payment_balance_enabled === 'true'
      
      // 订单配置
      orderForm.autoCancelTime = parseInt(config.order_auto_cancel_time || '30')
      orderForm.prepareTime = parseInt(config.order_prepare_time || '15')
      orderForm.autoCompleteTime = parseInt(config.order_auto_complete_time || '24')
      
      // 积分规则
      pointsForm.earnRate = parseFloat(config.points_earn_rate || '1.0')
      pointsForm.redeemRate = parseFloat(config.points_redeem_rate || '1.0')
      pointsForm.minRedeem = parseInt(config.points_min_redeem || '100')
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    ElMessage.error('加载配置失败')
  }
}

const loadLastBackupTime = async () => {
  try {
    const res = await getLastBackupTime()
    if (res.code === 200) {
      lastBackupTime.value = res.data || '未备份'
    }
  } catch (error) {
    console.error('获取备份时间失败:', error)
  }
}

const loadSystemInfo = async () => {
  try {
    const res = await getSystemInfo()
    if (res.code === 200) {
      Object.assign(systemInfo, res.data)
    }
  } catch (error) {
    console.error('获取系统信息失败:', error)
  }
}

const saveBasicConfig = async () => {
  try {
    const configMap = {
      shop_name: basicForm.shopName,
      shop_logo: basicForm.logo,
      shop_phone: basicForm.phone,
      shop_address: basicForm.address
    }
    
    const res = await batchUpdateConfig(configMap)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const savePaymentConfig = async () => {
  try {
    const configMap = {
      payment_wechat_enabled: paymentForm.wechatEnabled.toString(),
      payment_alipay_enabled: paymentForm.alipayEnabled.toString(),
      payment_balance_enabled: paymentForm.balanceEnabled.toString()
    }
    
    const res = await batchUpdateConfig(configMap)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const saveOrderConfig = async () => {
  try {
    const configMap = {
      order_auto_cancel_time: orderForm.autoCancelTime.toString(),
      order_prepare_time: orderForm.prepareTime.toString(),
      order_auto_complete_time: orderForm.autoCompleteTime.toString()
    }
    
    const res = await batchUpdateConfig(configMap)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const savePointsConfig = async () => {
  try {
    const configMap = {
      points_earn_rate: pointsForm.earnRate.toString(),
      points_redeem_rate: pointsForm.redeemRate.toString(),
      points_min_redeem: pointsForm.minRedeem.toString()
    }
    
    const res = await batchUpdateConfig(configMap)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const handleLogoSuccess = (response) => {
  if (response.code === 200) {
    basicForm.logo = response.data.url
    ElMessage.success('上传成功')
  }
}

const handleBackup = async () => {
  try {
    await ElMessageBox.confirm('确认备份当前数据？备份可能需要几分钟时间。', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    ElMessage.info('正在备份数据，请稍候...')
    
    const res = await backupData()
    
    if (res.code === 200) {
      ElMessage.success('备份成功，正在下载...')
      loadLastBackupTime()
      
      // 下载备份文件
      if (res.data && res.data.downloadUrl) {
        const downloadUrl = '/api' + res.data.downloadUrl
        window.location.href = downloadUrl
      } else {
        ElMessage.warning('备份成功，但无法自动下载')
      }
    } else {
      ElMessage.error(res.msg || '备份失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('备份失败:', error)
      ElMessage.error('备份失败')
    }
  }
}

const handleClearCache = async () => {
  try {
    await ElMessageBox.confirm('确认清理系统缓存？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    const res = await clearCache()
    if (res.code === 200) {
      ElMessage.success('缓存清理成功')
    } else {
      ElMessage.error(res.msg || '缓存清理失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理缓存失败:', error)
      ElMessage.error('清理缓存失败')
    }
  }
}

const beforeRestore = (file) => {
  const isBackup = file.name.endsWith('.sql') || file.name.endsWith('.zip')
  if (!isBackup) {
    ElMessage.error('请选择正确的备份文件!')
    return false
  }
  return true
}

const handleRestoreSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('数据恢复成功，请重新登录')
    setTimeout(() => {
      location.reload()
    }, 2000)
  }
}
</script>

<style scoped lang="scss">
.system-config {
  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }

  .logo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
  }

  .logo {
    width: 100px;
    height: 100px;
    display: block;
    object-fit: contain;
  }
}
</style>
