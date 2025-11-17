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
          <el-form :model="orderForm" label-width="150px">
            <el-form-item label="自动取消时间">
              <el-input-number v-model="orderForm.autoCancelTime" :min="5" :max="60" />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>
            <el-form-item label="预计制作时间">
              <el-input-number v-model="orderForm.prepareTime" :min="5" :max="120" />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveOrderConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 积分规则 -->
        <el-tab-pane label="积分规则" name="points">
          <el-form :model="pointsForm" label-width="150px">
            <el-form-item label="消费获取比例">
              <el-input-number v-model="pointsForm.earnRate" :min="0" :max="100" :precision="2" />
              <span style="margin-left: 10px">%（消费1元获得积分）</span>
            </el-form-item>
            <el-form-item label="积分抵扣比例">
              <el-input-number v-model="pointsForm.redeemRate" :min="0" :max="100" :precision="2" />
              <span style="margin-left: 10px">%（100积分可抵扣金额）</span>
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
              <el-button type="primary" @click="handleBackup">立即备份</el-button>
              <div style="margin-top: 10px; color: #909399; font-size: 12px">
                最后备份时间：{{ lastBackupTime || '未备份' }}
              </div>
            </el-form-item>
            <el-form-item label="数据恢复">
              <el-upload
                action="/api/admin/system/restore"
                :on-success="handleRestoreSuccess"
                :before-upload="beforeRestore"
              >
                <el-button type="warning">选择备份文件</el-button>
              </el-upload>
              <div style="margin-top: 10px; color: #f56c6c; font-size: 12px">
                ⚠️ 数据恢复将覆盖现有数据，请谨慎操作！
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('basic')
const lastBackupTime = ref('')

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
  prepareTime: 15
})

const pointsForm = reactive({
  earnRate: 1,
  redeemRate: 1
})

onMounted(() => {
  loadConfig()
})

const loadConfig = async () => {
  try {
    // TODO: 调用API加载配置
    ElMessage.info('系统设置功能开发中')
  } catch (error) {
    ElMessage.error('加载配置失败')
  }
}

const saveBasicConfig = async () => {
  try {
    // TODO: 调用API保存配置
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const savePaymentConfig = async () => {
  try {
    // TODO: 调用API保存配置
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const saveOrderConfig = async () => {
  try {
    // TODO: 调用API保存配置
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const savePointsConfig = async () => {
  try {
    // TODO: 调用API保存配置
    ElMessage.success('保存成功')
  } catch (error) {
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
    await ElMessageBox.confirm('确认备份当前数据？', '提示', {
      type: 'warning'
    })
    
    // TODO: 调用API备份数据
    ElMessage.success('备份成功')
    lastBackupTime.value = new Date().toLocaleString()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('备份失败')
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
