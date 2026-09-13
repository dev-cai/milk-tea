<template>
  <div class="activity-management">
    <!-- 活动统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activityStats.total || 0 }}</div>
              <div class="stat-label">总活动数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activityStats.active || 0 }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activityStats.participants || 0 }}</div>
              <div class="stat-label">参与用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activityStats.revenue || 0 }}</div>
              <div class="stat-label">活动收益</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动管理 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动管理</span>
          <el-button type="primary" @click="showActivityDialog()">创建活动</el-button>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="活动名称">
          <el-input v-model="queryForm.keyword" placeholder="请输入活动名称" clearable />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="queryForm.type" placeholder="请选择活动类型" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="满减活动" value="discount" />
            <el-option label="限时秒杀" value="seckill" />
            <el-option label="买赠活动" value="gift" />
            <el-option label="会员专享" value="member" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
            <el-option label="已暂停" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 活动列表 -->
      <el-table :data="activities" v-loading="loading">
        <el-table-column label="活动信息" min-width="250">
          <template #default="{ row }">
            <div class="activity-info">
              <img :src="row.banner" class="activity-banner" />
              <div class="activity-details">
                <div class="activity-name">{{ row.name }}</div>
                <div class="activity-desc">{{ row.description }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="活动类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getActivityTypeTag(row.type)">
              {{ getActivityTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="200">
          <template #default="{ row }">
            <div>{{ formatTime(row.startTime) }}</div>
            <div>{{ formatTime(row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="participants" label="参与人数" width="100" />
        <el-table-column prop="revenue" label="活动收益" width="120">
          <template #default="{ row }">
            ¥{{ row.revenue }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getActivityStatusTag(row.status)">
              {{ getActivityStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; gap: 5px; flex-wrap: nowrap;">
              <el-button size="small" @click="viewActivityDetail(row)">详情</el-button>
              <el-button type="primary" size="small" @click="showActivityDialog(row)">编辑</el-button>
              <el-button v-if="row.status === 0" type="success" size="small" @click="startActivity(row)">启动</el-button>
              <el-button v-if="row.status === 1" type="warning" size="small" @click="pauseActivity(row)">暂停</el-button>
              <el-button type="danger" size="small" @click="deleteActivity(row)">删除</el-button>
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
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 活动编辑对话框 -->
    <el-dialog v-model="activityDialogVisible" :title="activityForm.id ? '编辑活动' : '创建活动'" width="800px">
      <el-form :model="activityForm" :rules="activityRules" ref="activityFormRef" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="activityForm.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="activityForm.type" placeholder="请选择活动类型">
            <el-option label="满减活动" value="discount" />
            <el-option label="限时秒杀" value="seckill" />
            <el-option label="买赠活动" value="gift" />
            <el-option label="会员专享" value="member" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="activityForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="activityForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="活动规则" prop="rules">
          <el-input v-model="activityForm.rules" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="活动横幅">
          <el-input v-model="activityForm.banner" placeholder="请输入横幅图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="activityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveActivity">保存</el-button>
      </template>
    </el-dialog>

    <!-- 活动效果分析对话框 -->
    <el-dialog v-model="analysisDialogVisible" title="活动效果分析" width="900px">
      <div v-if="currentActivity">
        <!-- 活动基本信息 -->
        <el-descriptions :column="2" border style="margin-bottom: 20px;">
          <el-descriptions-item label="活动名称">{{ currentActivity.name }}</el-descriptions-item>
          <el-descriptions-item label="活动类型">
            <el-tag :type="getActivityTypeTag(currentActivity.type)">
              {{ getActivityTypeName(currentActivity.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="活动状态">
            <el-tag :type="getActivityStatusTag(currentActivity.status)">
              {{ getActivityStatusName(currentActivity.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="活动时间">
            {{ formatTime(currentActivity.startTime) }} 至 {{ formatTime(currentActivity.endTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 效果数据统计 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="6">
            <el-card class="analysis-card">
              <div class="analysis-item">
                <div class="analysis-label">参与人数</div>
                <div class="analysis-value">{{ activityAnalysis.participants || 0 }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="analysis-card">
              <div class="analysis-item">
                <div class="analysis-label">订单数量</div>
                <div class="analysis-value">{{ activityAnalysis.orders || 0 }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="analysis-card">
              <div class="analysis-item">
                <div class="analysis-label">活动收益</div>
                <div class="analysis-value">¥{{ activityAnalysis.revenue || 0 }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="analysis-card">
              <div class="analysis-item">
                <div class="analysis-label">转化率</div>
                <div class="analysis-value">{{ activityAnalysis.conversionRate || 0 }}%</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 详细数据表格 -->
        <el-table :data="activityAnalysis.details" border>
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="views" label="浏览量" width="100" />
          <el-table-column prop="participants" label="参与人数" width="100" />
          <el-table-column prop="orders" label="订单数" width="100" />
          <el-table-column prop="revenue" label="收益" width="120">
            <template #default="{ row }">
              ¥{{ row.revenue }}
            </template>
          </el-table-column>
          <el-table-column prop="avgAmount" label="客单价" width="120">
            <template #default="{ row }">
              ¥{{ row.avgAmount }}
            </template>
          </el-table-column>
          <el-table-column prop="conversionRate" label="转化率" width="100">
            <template #default="{ row }">
              {{ row.conversionRate }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="analysisDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportAnalysisData">导出数据</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getActivityList,
  getActivity,
  createActivity,
  updateActivity,
  deleteActivity as deleteActivityApi,
  batchDeleteActivities,
  updateActivityStatus,
  getActivityStatistics,
  getActivityAnalysis
} from '@/api/activity'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const activities = ref([])
const total = ref(0)

// 统计数据
const activityStats = reactive({
  total: 0,
  active: 0,
  participants: 0,
  revenue: 0
})

// 查询表单
const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  type: '',
  status: ''
})

// 对话框状态
const activityDialogVisible = ref(false)
const analysisDialogVisible = ref(false)

// 当前活动
const currentActivity = ref(null)

// 活动效果分析数据
const activityAnalysis = reactive({
  participants: 0,
  orders: 0,
  revenue: 0,
  conversionRate: 0,
  details: []
})

// 表单引用和数据
const activityFormRef = ref()
const activityForm = reactive({
  id: null,
  name: '',
  type: 'discount',
  timeRange: [],
  description: '',
  rules: '',
  banner: ''
})

// 表单验证规则
const activityRules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  timeRange: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadActivityStats(),
    handleQuery()
  ])
}

// 加载活动统计
const loadActivityStats = async () => {
  try {
    const res = await getActivityStatistics()
    if (res.code === 200) {
      Object.assign(activityStats, res.data)
    }
  } catch (error) {
    console.error('加载活动统计失败:', error)
    ElMessage.error('加载活动统计失败')
  }
}

// 查询活动
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getActivityList(queryForm)
    if (res.code === 200) {
      activities.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('查询活动失败:', error)
    ElMessage.error('查询活动失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
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

// 显示活动对话框
const showActivityDialog = (activity = null) => {
  if (activity) {
    Object.assign(activityForm, {
      ...activity,
      timeRange: [activity.startTime, activity.endTime]
    })
  } else {
    Object.assign(activityForm, {
      id: null,
      name: '',
      type: 'discount',
      timeRange: [],
      description: '',
      rules: '',
      banner: ''
    })
  }
  activityDialogVisible.value = true
}

// 保存活动
const saveActivity = async () => {
  try {
    await activityFormRef.value.validate()
    
    // 处理时间范围
    if (activityForm.timeRange && activityForm.timeRange.length === 2) {
      activityForm.startTime = activityForm.timeRange[0]
      activityForm.endTime = activityForm.timeRange[1]
    }
    
    if (activityForm.id) {
      const res = await updateActivity(activityForm.id, activityForm)
      if (res.code === 200) {
        ElMessage.success('活动更新成功')
      }
    } else {
      const res = await createActivity(activityForm)
      if (res.code === 200) {
        ElMessage.success('活动创建成功')
      }
    }
    
    activityDialogVisible.value = false
    handleQuery()
    loadActivityStats()
  } catch (error) {
    console.error('保存活动失败:', error)
    ElMessage.error('保存活动失败')
  }
}

// 查看活动详情（效果分析）
const viewActivityDetail = async (activity) => {
  currentActivity.value = activity
  analysisDialogVisible.value = true
  
  // 加载活动效果数据
  try {
    const res = await getActivityAnalysis(activity.id)
    
    if (res.code === 200) {
      Object.assign(activityAnalysis, res.data)
      // 如果没有详细数据，使用空数组
      if (!activityAnalysis.details) {
        activityAnalysis.details = []
      }
    }
  } catch (error) {
    console.error('加载活动效果数据失败:', error)
    ElMessage.error('加载效果数据失败')
  }
}

// 导出分析数据
const exportAnalysisData = () => {
  ElMessage.success(`活动 ${currentActivity.value.name} 的效果数据已导出`)
}

// 启动活动
const startActivity = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要启动这个活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await updateActivityStatus(activity.id, 1)
    if (res.code === 200) {
      ElMessage.success('活动启动成功')
      handleQuery()
      loadActivityStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启动活动失败:', error)
      ElMessage.error('启动活动失败')
    }
  }
}

// 暂停活动
const pauseActivity = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要暂停这个活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await updateActivityStatus(activity.id, 3)
    if (res.code === 200) {
      ElMessage.success('活动暂停成功')
      handleQuery()
      loadActivityStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('暂停活动失败:', error)
      ElMessage.error('暂停活动失败')
    }
  }
}

// 删除活动
const deleteActivity = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要删除这个活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteActivityApi(activity.id)
    if (res.code === 200) {
      ElMessage.success('活动删除成功')
      handleQuery()
      loadActivityStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动失败:', error)
      ElMessage.error('删除活动失败')
    }
  }
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getActivityTypeName = (type) => {
  const typeMap = {
    discount: '满减活动',
    seckill: '限时秒杀',
    gift: '买赠活动',
    member: '会员专享'
  }
  return typeMap[type] || '未知'
}

const getActivityTypeTag = (type) => {
  const tagMap = {
    discount: 'primary',
    seckill: 'danger',
    gift: 'success',
    member: 'warning'
  }
  return tagMap[type] || 'info'
}

const getActivityStatusName = (status) => {
  const statusMap = {
    0: '未开始',
    1: '进行中',
    2: '已结束',
    3: '已暂停'
  }
  return statusMap[status] || '未知'
}

const getActivityStatusTag = (status) => {
  const tagMap = {
    0: 'info',
    1: 'success',
    2: 'info',
    3: 'warning'
  }
  return tagMap[status] || 'info'
}
</script>

<style scoped>
.activity-management {
  padding: 20px;
}

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

.activity-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.activity-banner {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.activity-details {
  flex: 1;
}

.activity-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 12px;
  color: #909399;
}
</style>
