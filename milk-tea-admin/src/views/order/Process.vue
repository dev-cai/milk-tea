<template>
  <div class="order-process">
    <!-- 订单概览卡片 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><Clock /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ pendingCount.pending || 0 }}</div>
              <div class="overview-label">待支付</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Tools /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ pendingCount.processing || 0 }}</div>
              <div class="overview-label">制作中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><Bell /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ pendingCount.ready || 0 }}</div>
              <div class="overview-label">待取餐</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><Warning /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ pendingCount.urgent || 0 }}</div>
              <div class="overview-label">催单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-card class="quick-actions">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
          <div class="header-actions">
            <el-switch
              v-model="voiceNotificationEnabled"
              active-text="语音提醒"
              inactive-text="静音"
              style="margin-right: 15px;"
              @change="handleVoiceToggle"
            />
            <el-button type="success" @click="batchAcceptOrders" :disabled="selectedOrders.length === 0">
              批量接单 ({{ selectedOrders.length }})
            </el-button>
            <el-button type="primary" @click="batchCompleteOrders" :disabled="selectedOrders.length === 0">
              批量完成 ({{ selectedOrders.length }})
            </el-button>
            <el-button @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 状态筛选 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待支付" name="0" />
        <el-tab-pane label="待制作" name="1" />
        <el-tab-pane label="制作中" name="2" />
        <el-tab-pane label="待取餐" name="3" />
        <el-tab-pane label="已完成" name="4" />
        <el-tab-pane label="已取消" name="5" />
      </el-tabs>
    </el-card>

    <!-- 订单列表 -->
    <el-card>
      <el-table
        :data="orders"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <div class="order-no">
              {{ row.orderNo }}
              <el-tag v-if="row.urgent" type="danger" size="small" style="margin-left: 5px;">
                催单
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <div v-for="item in row.items" :key="item.id" class="product-item">
                <span class="product-name">{{ item.productName }}</span>
                <span class="product-quantity">x{{ item.quantity }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="payType" label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getPayTypeTag(row.payType)">
              {{ getPayTypeName(row.payType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="等待时长" width="100">
          <template #default="{ row }">
            <span :class="getWaitTimeClass(row.createTime)">
              {{ getWaitTime(row.createTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 0"
                type="success"
                size="small"
                @click="acceptOrder(row)"
              >
                接单
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="primary"
                size="small"
                @click="startProcessing(row)"
              >
                开始制作
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="warning"
                size="small"
                @click="markReady(row)"
              >
                制作完成
              </el-button>
              <el-button
                v-if="row.status === 3"
                type="success"
                size="small"
                @click="completeOrder(row)"
              >
                已取餐
              </el-button>
              <el-button
                size="small"
                @click="showOrderDetail(row)"
              >
                详情
              </el-button>
              <el-dropdown trigger="click">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="showRemarkDialog(row)">
                      <el-icon><Edit /></el-icon>
                      备注
                    </el-dropdown-item>
                    <el-dropdown-item @click="toggleUrgent(row)">
                      <el-icon><Bell /></el-icon>
                      {{ row.urgent ? '取消催单' : '标记催单' }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="printOrder(row)">
                      <el-icon><Printer /></el-icon>
                      打印
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOrders"
        @current-change="loadOrders"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(currentOrder.status)">
              {{ getStatusName(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPayTypeName(currentOrder.payType) }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 10px 0;">商品明细</h4>
        <el-table :data="currentOrder.items" border>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="小计" width="100">
            <template #default="{ row }">¥{{ row.totalPrice }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 备注对话框 -->
    <el-dialog v-model="remarkDialogVisible" title="订单备注" width="500px">
      <el-form>
        <el-form-item label="备注内容">
          <el-input v-model="remarkText" type="textarea" :rows="4" placeholder="请输入备注内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateRemark">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

let lastOrderCount = 0 // 记录上次订单数量

// 语音提醒开关状态（从localStorage读取，默认开启）
const voiceNotificationEnabled = ref(
  localStorage.getItem('voiceNotificationEnabled') !== 'false'
)

// 数据定义
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const selectedOrders = ref([])
const activeTab = ref('1') // 默认显示待制作

// 统计数据
const pendingCount = reactive({
  pending: 0,
  processing: 0,
  ready: 0,
  urgent: 0
})

// 查询表单
const queryForm = reactive({
  page: 1,
  size: 10,
  status: 1
})

// 对话框状态
const detailDialogVisible = ref(false)
const remarkDialogVisible = ref(false)
const currentOrder = ref(null)
const remarkText = ref('')

// 定时器
let refreshTimer = null

onMounted(() => {
  loadData()
  // 每30秒自动刷新并检查新订单
  refreshTimer = setInterval(() => {
    checkNewOrders()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})

// 检查新订单并播放语音提醒
const checkNewOrders = async () => {
  try {
    const res = await request({
      url: '/admin/order/pending/count',
      method: 'get'
    })
    if (res.code === 200) {
      const currentCount = res.data.pending || 0
      // 如果有新订单（订单数增加）
      if (lastOrderCount > 0 && currentCount > lastOrderCount) {
        // 只有在开启语音提醒时才播放
        if (voiceNotificationEnabled.value) {
          playNotification()
        }
        ElMessage({
          message: `您有 ${currentCount - lastOrderCount} 个新订单！`,
          type: 'warning',
          duration: 5000
        })
      }
      lastOrderCount = currentCount
      Object.assign(pendingCount, res.data)
      // 如果当前在待制作标签页，刷新订单列表
      if (activeTab.value === '1') {
        loadOrders()
      }
    }
  } catch (error) {
    console.error('检查新订单失败:', error)
  }
}

// 播放语音提醒
const playNotification = () => {
	try {
		// Generate a short notification tone locally; this avoids a missing static mp3 asset.
		const AudioContext = window.AudioContext || window.webkitAudioContext
		if (!AudioContext) throw new Error('当前浏览器不支持音频提醒')
		const context = new AudioContext()
		const oscillator = context.createOscillator()
		const gain = context.createGain()
		oscillator.type = 'sine'
		oscillator.frequency.value = 880
		gain.gain.setValueAtTime(0.001, context.currentTime)
		gain.gain.exponentialRampToValueAtTime(0.18, context.currentTime + 0.02)
		gain.gain.exponentialRampToValueAtTime(0.001, context.currentTime + 0.25)
		oscillator.connect(gain).connect(context.destination)
		oscillator.start()
		oscillator.stop(context.currentTime + 0.25)
		oscillator.addEventListener('ended', () => context.close())
	} catch (error) {
		console.warn('语音提醒播放失败:', error)
		if ('Notification' in window && Notification.permission === 'granted') {
			new Notification('新订单提醒', { body: '您有新的订单需要处理' })
		}
	}
}

// 处理语音提醒开关切换
const handleVoiceToggle = (value) => {
  localStorage.setItem('voiceNotificationEnabled', value)
  ElMessage.success(value ? '语音提醒已开启' : '语音提醒已关闭')
}

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadOrders(),
    loadPendingCount()
  ])
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/order/page',
      method: 'get',
      params: queryForm
    })
    if (res.code === 200) {
      orders.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载待处理订单数量
const loadPendingCount = async () => {
  try {
    const res = await request({
      url: '/admin/order/pending/count',
      method: 'get'
    })
    if (res.code === 200) {
      Object.assign(pendingCount, res.data)
    }
  } catch (error) {
    console.error('加载待处理订单数量失败:', error)
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  queryForm.status = parseInt(tabName)
  queryForm.page = 1
  loadOrders()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 获取行样式
const getRowClassName = ({ row }) => {
  if (row.urgent) return 'urgent-row'
  if (getWaitMinutes(row.createTime) > 30) return 'warning-row'
  return ''
}

// 接单
const acceptOrder = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}/status`,
      method: 'put',
      data: { status: 1 }
    })
    if (res.code === 200) {
      ElMessage.success('接单成功')
      loadData()
    }
  } catch (error) {
    console.error('接单失败:', error)
  }
}

// 开始制作
const startProcessing = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}/status`,
      method: 'put',
      data: { status: 2 }
    })
    if (res.code === 200) {
      ElMessage.success('开始制作')
      loadData()
    }
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

// 制作完成
const markReady = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}/status`,
      method: 'put',
      data: { status: 3 }
    })
    if (res.code === 200) {
      ElMessage.success('制作完成，等待取餐')
      loadData()
    }
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

// 完成订单
const completeOrder = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}/status`,
      method: 'put',
      data: { status: 4 }
    })
    if (res.code === 200) {
      ElMessage.success('订单完成')
      loadData()
    }
  } catch (error) {
    console.error('完成订单失败:', error)
  }
}

// 批量接单
const batchAcceptOrders = async () => {
  try {
    const orderIds = selectedOrders.value.map(order => order.id)
    const res = await request({
      url: '/admin/order/batch/accept',
      method: 'put',
      data: { orderIds }
    })
    if (res.code === 200) {
      ElMessage.success('批量接单成功')
      loadData()
    }
  } catch (error) {
    console.error('批量接单失败:', error)
  }
}

// 批量完成
const batchCompleteOrders = async () => {
  try {
    const orderIds = selectedOrders.value.map(order => order.id)
    const res = await request({
      url: '/admin/order/batch/complete',
      method: 'put',
      data: { orderIds }
    })
    if (res.code === 200) {
      ElMessage.success('批量完成成功')
      loadData()
    }
  } catch (error) {
    console.error('批量完成失败:', error)
  }
}

// 显示订单详情
const showOrderDetail = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}`,
      method: 'get'
    })
    if (res.code === 200) {
      currentOrder.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

// 显示备注对话框
const showRemarkDialog = (order) => {
  currentOrder.value = order
  remarkText.value = order.remark || ''
  remarkDialogVisible.value = true
}

// 更新备注
const updateRemark = async () => {
  try {
    const res = await request({
      url: `/admin/order/${currentOrder.value.id}/remark`,
      method: 'put',
      data: { remark: remarkText.value }
    })
    if (res.code === 200) {
      ElMessage.success('备注更新成功')
      remarkDialogVisible.value = false
      loadOrders()
    }
  } catch (error) {
    console.error('更新备注失败:', error)
  }
}

// 切换催单状态
const toggleUrgent = async (order) => {
  try {
    const res = await request({
      url: `/admin/order/${order.id}/urgent`,
      method: 'put',
      data: { urgent: !order.urgent }
    })
    if (res.code === 200) {
      ElMessage.success(order.urgent ? '已取消催单' : '已标记催单')
      loadOrders()
    }
  } catch (error) {
    console.error('更新催单状态失败:', error)
  }
}

// 打印订单
const printOrder = async (order) => {
  try {
    // 获取打印设备和模板列表
    const [devicesRes, templatesRes] = await Promise.all([
      request({ url: '/admin/print/devices', method: 'get' }),
      request({ url: '/admin/print/templates', method: 'get' })
    ])
    
    if (devicesRes.code !== 200 || templatesRes.code !== 200) {
      ElMessage.error('获取打印配置失败')
      return
    }
    
    const devices = devicesRes.data || []
    const templates = templatesRes.data || []
    
    // 检查是否有可用设备
    const onlineDevices = devices.filter(d => d.status === 1)
    if (onlineDevices.length === 0) {
      ElMessage.warning('没有可用的打印设备')
      return
    }
    
    // 获取默认的订单小票模板
    let defaultTemplate = templates.find(t => t.type === 'order' && t.isDefault === 1)
    if (!defaultTemplate && templates.length > 0) {
      defaultTemplate = templates.find(t => t.type === 'order') || templates[0]
    }
    
    if (!defaultTemplate) {
      ElMessage.warning('没有可用的打印模板')
      return
    }
    
    // 使用第一个在线设备
    const defaultDevice = onlineDevices[0]
    
    // 确认打印
    await ElMessageBox.confirm(
      `将使用 ${defaultDevice.name} 打印订单 ${order.orderNo}，是否继续？`,
      '确认打印',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // 发送打印请求
    const printRes = await request({
      url: '/admin/print/order',
      method: 'post',
      data: {
        orderId: order.id,
        deviceId: defaultDevice.id,
        templateId: defaultTemplate.id
      }
    })
    
    if (printRes.code === 200) {
      ElMessage.success('打印成功')
    } else {
      ElMessage.error(printRes.message || '打印失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('打印订单失败:', error)
      ElMessage.error('打印失败')
    }
  }
}

// 刷新数据
const refreshData = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

// 工具函数
const getStatusName = (status) => {
  const statusMap = {
    0: '待支付',
    1: '待制作',
    2: '制作中',
    3: '待取餐',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusTag = (status) => {
  const tagMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return tagMap[status] || 'info'
}

const getPayTypeName = (payType) => {
  const payTypeMap = {
    1: '微信支付（开发测试）',
    2: '微信支付（历史订单）',
    3: '微信支付（历史订单）'
  }
  return payTypeMap[payType] || '未知'
}

const getPayTypeTag = (payType) => {
  const tagMap = {
    1: 'success',
    2: 'info',
    3: 'info'
  }
  return tagMap[payType] || 'info'
}

const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getWaitMinutes = (createTime) => {
  return Math.floor((Date.now() - new Date(createTime).getTime()) / 1000 / 60)
}

const getWaitTime = (createTime) => {
  const minutes = getWaitMinutes(createTime)
  if (minutes < 60) {
    return `${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const remainMinutes = minutes % 60
    return `${hours}小时${remainMinutes}分钟`
  }
}

const getWaitTimeClass = (createTime) => {
  const minutes = getWaitMinutes(createTime)
  if (minutes > 60) return 'wait-time-danger'
  if (minutes > 30) return 'wait-time-warning'
  return 'wait-time-normal'
}
</script>

<style scoped>
.order-process {
  padding: 20px;
}

.overview-row {
  margin-bottom: 20px;
}

.overview-card {
  margin-bottom: 20px;
}

.overview-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.overview-info {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.overview-label {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.order-no {
  display: flex;
  align-items: center;
}

.product-info {
  max-height: 60px;
  overflow-y: auto;
}

.product-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.product-name {
  flex: 1;
  margin-right: 10px;
}

.product-quantity {
  color: #909399;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.wait-time-normal {
  color: #67c23a;
}

.wait-time-warning {
  color: #e6a23c;
}

.wait-time-danger {
  color: #f56c6c;
  font-weight: bold;
}

:deep(.urgent-row) {
  background-color: #fef0f0;
}

:deep(.warning-row) {
  background-color: #fdf6ec;
}
</style>
