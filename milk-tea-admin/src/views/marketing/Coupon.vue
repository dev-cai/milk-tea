<template>
  <div class="coupon-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <div>
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length">批量删除</el-button>
            <el-button type="primary" @click="handleAdd">添加优惠券</el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="优惠券名称">
          <el-input v-model="queryForm.keyword" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="请选择类型" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="满减券" :value="1" />
            <el-option label="折扣券" :value="2" />
            <el-option label="兑换券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%" 
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCouponTypeColor(row.type)">{{ getCouponTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discount" label="折扣" width="120">
          <template #default="{ row }">
            <span v-if="row.type === 1">减¥{{ row.discount }}</span>
            <span v-else-if="row.type === 2">{{ (row.discount * 10).toFixed(1) }}折</span>
            <span v-else>{{ row.discount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="最低消费" width="100">
          <template #default="{ row }">
            {{ row.minAmount > 0 ? `¥${row.minAmount}` : '无门槛' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发行数量" width="100" />
        <el-table-column prop="receivedCount" label="已领取" width="100" />
        <el-table-column prop="validStart" label="有效期开始" width="120" />
        <el-table-column prop="validEnd" label="有效期结束" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="info" size="small" @click="handleUsage(row)">使用记录</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑优惠券对话框 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="600px">
      <el-form :model="couponForm" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="couponForm.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠券类型" prop="type">
          <el-radio-group v-model="couponForm.type">
            <el-radio :label="1">满减券</el-radio>
            <el-radio :label="2">折扣券</el-radio>
            <el-radio :label="3">兑换券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="折扣金额/折扣率" prop="discount">
          <el-input-number 
            v-model="couponForm.discount" 
            :precision="2"
            :min="0"
            :max="couponForm.type === 2 ? 1 : 9999"
            :step="couponForm.type === 2 ? 0.01 : 1"
          />
          <span style="margin-left: 10px; color: #909399;">
            {{ couponForm.type === 1 ? '元' : couponForm.type === 2 ? '(0.8表示8折)' : '积分' }}
          </span>
        </el-form-item>
        <el-form-item label="最低消费金额" prop="minAmount">
          <el-input-number v-model="couponForm.minAmount" :precision="2" :min="0" />
          <span style="margin-left: 10px; color: #909399;">元（0表示无门槛）</span>
        </el-form-item>
        <el-form-item label="发行数量" prop="totalCount">
          <el-input-number v-model="couponForm.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="有效期" prop="validPeriod">
          <el-date-picker
            v-model="validPeriod"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="couponForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 使用记录对话框 -->
    <el-dialog v-model="usageVisible" title="优惠券使用记录" width="800px">
      <el-table :data="usageData" border>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="useTime" label="使用时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : ''">
              {{ row.status === 0 ? '未使用' : row.status === 1 ? '已使用' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCouponList, 
  addCoupon, 
  updateCoupon, 
  deleteCoupon, 
  batchDeleteCoupons,
  getCouponUsage
} from '@/api'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const formVisible = ref(false)
const usageVisible = ref(false)
const selectedRows = ref([])
const usageData = ref([])
const formRef = ref()
const validPeriod = ref([])

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  type: '',
  status: ''
})

const couponForm = reactive({
  id: null,
  name: '',
  type: 1,
  discount: 0,
  minAmount: 0,
  totalCount: 100,
  validStart: '',
  validEnd: '',
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
  discount: [{ required: true, message: '请输入折扣金额/折扣率', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发行数量', trigger: 'blur' }]
}

const formTitle = computed(() => {
  return couponForm.id ? '编辑优惠券' : '添加优惠券'
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCouponList(queryForm)
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
    keyword: '',
    type: '',
    status: ''
  })
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleStatusChange = async (row) => {
  try {
    await updateCoupon(row.id, { status: row.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleAdd = () => {
  Object.assign(couponForm, {
    id: null,
    name: '',
    type: 1,
    discount: 0,
    minAmount: 0,
    totalCount: 100,
    validStart: '',
    validEnd: '',
    status: 1
  })
  validPeriod.value = []
  formVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(couponForm, { ...row })
  validPeriod.value = [row.validStart, row.validEnd]
  formVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (validPeriod.value && validPeriod.value.length === 2) {
      couponForm.validStart = validPeriod.value[0]
      couponForm.validEnd = validPeriod.value[1]
    }

    if (couponForm.id) {
      await updateCoupon(couponForm.id, couponForm)
      ElMessage.success('更新成功')
    } else {
      await addCoupon(couponForm)
      ElMessage.success('添加成功')
    }
    
    formVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) { // 表单验证失败时error为false
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', {
      type: 'warning'
    })
    await deleteCoupon(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个优惠券吗？`, '批量删除', {
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteCoupons(ids)
    ElMessage.success('批量删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleUsage = async (row) => {
  try {
    const res = await getCouponUsage(row.id)
    if (res.code === 200) {
      usageData.value = res.data
      usageVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取使用记录失败')
  }
}

const getCouponTypeText = (type) => {
  const types = { 1: '满减券', 2: '折扣券', 3: '兑换券' }
  return types[type] || '未知'
}

const getCouponTypeColor = (type) => {
  const colors = { 1: 'primary', 2: 'success', 3: 'warning' }
  return colors[type] || ''
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
