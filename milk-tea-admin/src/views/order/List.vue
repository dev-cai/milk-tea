<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <div>
            <el-button type="success" @click="handleExport">导出订单</el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" :value="null" />
            <el-option label="待支付" :value="0" />
            <el-option label="待制作" :value="1" />
            <el-option label="制作中" :value="2" />
            <el-option label="待取餐" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
            <el-option label="申请退款" :value="6" />
            <el-option label="已退款" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="订单ID" width="100" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" width="100">
          <template #default="{ row }">
            ¥{{ row.actualAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="payType" label="支付方式" width="100">
          <template #default="{ row }">
            {{ getPayTypeText(row.payType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
              <el-button 
                type="success" 
                size="small" 
                v-if="row.status === 1"
                @click="handleAccept(row)"
              >
                接单
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                v-if="row.status === 2"
                @click="handleComplete(row)"
              >
                完成
              </el-button>
              <el-button 
                type="info" 
                size="small" 
                v-if="row.status === 6"
                @click="handleRefund(row)"
              >
                处理退款
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <div v-if="orderDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ orderDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ orderDetail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">¥{{ orderDetail.discountAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ orderDetail.actualAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPayTypeText(orderDetail.payType) }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(orderDetail.status)">
              {{ getStatusText(orderDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ orderDetail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 10px 0;">订单商品</h4>
        <el-table :data="orderDetail.items" border>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="sweetness" label="甜度" width="80">
            <template #default="{ row }">{{ getSweetnessText(row.sweetness) }}</template>
          </el-table-column>
          <el-table-column prop="temperature" label="温度" width="80">
            <template #default="{ row }">{{ getTemperatureText(row.temperature) }}</template>
          </el-table-column>
          <el-table-column prop="toppings" label="加料" />
        </el-table>
      </div>
    </el-dialog>

    <!-- 退款处理对话框 -->
    <el-dialog v-model="refundVisible" title="处理退款" width="500px">
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款金额">
          <el-input v-model="refundForm.amount" disabled>
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="refundForm.approve">
            <el-radio :label="true">同意退款</el-radio>
            <el-radio :label="false">拒绝退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input 
            v-model="refundForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入处理说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRefundSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getOrderList, 
  getOrderDetail, 
  updateOrderStatus, 
  handleRefund as handleOrderRefund,
  exportOrders
} from '@/api'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const refundVisible = ref(false)
const orderDetail = ref(null)
const dateRange = ref([])

const queryForm = reactive({
  page: 1,
  size: 10,
  orderNo: '',
  status: '',
  startDate: '',
  endDate: ''
})

const refundForm = reactive({
  orderId: null,
  amount: '',
  approve: true,
  remark: ''
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getOrderList(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(queryForm, {
    page: 1,
    size: 10,
    orderNo: '',
    status: '',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  handleQuery()
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startDate = dates[0]
    queryForm.endDate = dates[1]
  } else {
    queryForm.startDate = ''
    queryForm.endDate = ''
  }
}

const handleView = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    if (res.code === 200) {
      orderDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const handleAccept = async (row) => {
  try {
    await ElMessageBox.confirm('确定要接受此订单吗？', '确认接单', {
      type: 'warning'
    })
    await updateOrderStatus(row.id, 2) // 更新为制作中
    ElMessage.success('接单成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('接单失败')
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要完成此订单吗？', '确认完成', {
      type: 'warning'
    })
    await updateOrderStatus(row.id, 3) // 更新为待取餐
    ElMessage.success('订单已完成制作')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleRefund = (row) => {
  refundForm.orderId = row.id
  refundForm.amount = row.actualAmount
  refundForm.approve = true
  refundForm.remark = ''
  refundVisible.value = true
}

const handleRefundSubmit = async () => {
  try {
    await handleOrderRefund(refundForm.orderId, refundForm.approve)
    ElMessage.success('退款处理成功')
    refundVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('退款处理失败')
  }
}

const handleExport = async () => {
  try {
    const res = await exportOrders(queryForm)
    // 处理文件下载
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `订单数据_${new Date().toISOString().split('T')[0]}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const getStatusType = (status) => {
  const types = { 
    0: 'warning', 
    1: 'primary', 
    2: 'warning', 
    3: 'success', 
    4: 'success', 
    5: 'danger',
    6: 'info',
    7: 'info'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 
    0: '待支付', 
    1: '待制作', 
    2: '制作中', 
    3: '待取餐', 
    4: '已完成', 
    5: '已取消',
    6: '申请退款',
    7: '已退款'
  }
  return texts[status] || '未知'
}

const getPayTypeText = (payType) => {
  const texts = { 1: '微信支付（开发测试）', 2: '微信支付（历史订单）', 3: '微信支付（历史订单）' }
  return texts[payType] || '未知'
}

const getSweetnessText = (sweetness) => {
  const texts = { 0: '无糖', 1: '三分糖', 2: '五分糖', 3: '七分糖', 4: '正常糖' }
  return texts[sweetness] || '正常糖'
}

const getTemperatureText = (temperature) => {
  const texts = { 0: '去冰', 1: '少冰', 2: '正常冰', 3: '热饮' }
  return texts[temperature] || '正常冰'
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
}
</style>
