<template>
  <div class="store-management">
    <!-- 门店统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><Shop /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ storeStats.total || 0 }}</div>
              <div class="stat-label">总门店数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ storeStats.active || 0 }}</div>
              <div class="stat-label">营业中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ storeStats.closed || 0 }}</div>
              <div class="stat-label">已关闭</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ storeStats.totalStaff || 0 }}</div>
              <div class="stat-label">总员工数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>门店列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增门店
          </el-button>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="关键字">
          <el-input 
            v-model="queryForm.keyword" 
            placeholder="门店名称/地址/店长" 
            clearable 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="全部" :value="null" />
            <el-option label="营业中" :value="1" />
            <el-option label="已关闭" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="门店名称" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="manager" label="店长" width="100" />
        <el-table-column label="营业时间" width="150">
          <template #default="{ row }">
            {{ formatBusinessHours(row.businessHours) }}
          </template>
        </el-table-column>
        <el-table-column prop="staffCount" label="员工数" width="80" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '营业中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                删除
              </el-button>
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
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="门店名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入门店名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="门店地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入门店地址" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="店长姓名">
              <el-input v-model="form.manager" placeholder="请输入店长姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="店长电话">
              <el-input v-model="form.managerPhone" placeholder="请输入店长电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="门店面积">
              <el-input-number 
                v-model="form.area" 
                :min="0" 
                :precision="2"
                placeholder="平方米"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工数量">
              <el-input-number 
                v-model="form.staffCount" 
                :min="0"
                placeholder="人"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="营业时间">
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-time-select
              v-model="businessStartTime"
              start="00:00"
              step="00:30"
              end="23:30"
              placeholder="开始时间"
              style="width: 150px;"
            />
            <span>至</span>
            <el-time-select
              v-model="businessEndTime"
              start="00:00"
              step="00:30"
              end="23:30"
              placeholder="结束时间"
              style="width: 150px;"
            />
          </div>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number 
                v-model="form.latitude" 
                :precision="6"
                placeholder="纬度坐标"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number 
                v-model="form.longitude" 
                :precision="6"
                placeholder="经度坐标"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="门店描述">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入门店描述"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">营业</el-radio>
            <el-radio :label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Shop, CircleCheck, CircleClose, UserFilled } from '@element-plus/icons-vue'
import {
  getStoreStats,
  getStoreList,
  getStoreDetail,
  addStore,
  updateStore,
  deleteStore,
  toggleStoreStatus
} from '@/api/store'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 门店统计
const storeStats = reactive({
  total: 0,
  active: 0,
  closed: 0,
  totalStaff: 0
})

// 营业时间
const businessStartTime = ref('09:00')
const businessEndTime = ref('22:00')

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: null
})

const form = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  manager: '',
  managerPhone: '',
  businessHours: '["09:00","22:00"]',
  latitude: null,
  longitude: null,
  area: null,
  staffCount: 0,
  description: '',
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入门店名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入门店地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  await Promise.all([
    loadStoreStats(),
    fetchData()
  ])
}

const loadStoreStats = async () => {
  try {
    const res = await getStoreStats()
    if (res.code === 200) {
      Object.assign(storeStats, res.data)
    }
  } catch (error) {
    console.error('加载门店统计失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStoreList(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryForm.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryForm, {
    page: 1,
    size: 10,
    keyword: '',
    status: null
  })
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增门店'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await getStoreDetail(row.id)
    if (res.code === 200) {
      dialogTitle.value = '编辑门店'
      Object.assign(form, res.data)
      
      // 解析营业时间
      if (res.data.businessHours) {
        try {
          const hours = JSON.parse(res.data.businessHours)
          if (Array.isArray(hours) && hours.length === 2) {
            businessStartTime.value = hours[0]
            businessEndTime.value = hours[1]
          }
        } catch (e) {
          console.error('解析营业时间失败:', e)
        }
      }
      
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取门店详情失败:', error)
    ElMessage.error('获取门店详情失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 验证营业时间
    if (!businessStartTime.value || !businessEndTime.value) {
      ElMessage.warning('请设置营业时间')
      return
    }
    
    if (businessStartTime.value >= businessEndTime.value) {
      ElMessage.warning('结束时间必须大于开始时间')
      return
    }
    
    submitLoading.value = true

    // 组装营业时间
    const submitData = {
      ...form,
      businessHours: JSON.stringify([businessStartTime.value, businessEndTime.value])
    }

    const apiFunc = form.id ? updateStore : addStore
    const res = await apiFunc(submitData)
    
    if (res.code === 200) {
      ElMessage.success(res.msg || (form.id ? '更新成功' : '添加成功'))
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== false) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除门店"${row.name}"吗？删除后将无法恢复！`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    const res = await deleteStore(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row) => {
  const originalStatus = row.status
  try {
    const res = await toggleStoreStatus(row.id)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '状态更新失败')
      row.status = originalStatus
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    row.status = originalStatus
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    address: '',
    phone: '',
    manager: '',
    managerPhone: '',
    businessHours: '["09:00","22:00"]',
    latitude: null,
    longitude: null,
    area: null,
    staffCount: 0,
    description: '',
    status: 1
  })
  
  // 重置营业时间
  businessStartTime.value = '09:00'
  businessEndTime.value = '22:00'
  
  formRef.value?.clearValidate()
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

const formatBusinessHours = (hours) => {
  if (!hours) return '-'
  try {
    const parsed = JSON.parse(hours)
    if (Array.isArray(parsed) && parsed.length === 2) {
      return `${parsed[0]} - ${parsed[1]}`
    }
  } catch (e) {
    console.error('解析营业时间失败:', e)
  }
  return '-'
}
</script>

<style scoped lang="scss">
.store-management {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    margin-bottom: 20px;
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 5px;
  }

  .stat-label {
    font-size: 14px;
    color: #909399;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
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
}
</style>
