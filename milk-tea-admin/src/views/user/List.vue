<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.keyword" placeholder="请输入用户名或昵称" clearable />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="queryForm.memberLevel" placeholder="请选择会员等级" clearable>
            <el-option label="普通会员" :value="0" />
            <el-option label="黄金会员" :value="1" />
            <el-option label="钻石会员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="50">
              {{ row.nickname?.charAt(0) || row.username?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getMemberLevelType(row.memberLevel)">
              {{ getMemberLevelText(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="{ row }">
            ¥{{ row.balance || 0 }}
          </template>
        </el-table-column>
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
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="warning" size="small" @click="handleEditLevel(row)">等级</el-button>
            <el-button type="success" size="small" @click="handleEditPoints(row)">积分</el-button>
            <el-button type="info" size="small" @click="handleStatistics(row)">统计</el-button>
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

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="userDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userDetail.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userDetail.phone || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getMemberLevelType(userDetail.memberLevel)">
              {{ getMemberLevelText(userDetail.memberLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="积分">{{ userDetail.points }}</el-descriptions-item>
          <el-descriptions-item label="余额">¥{{ userDetail.balance || 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userDetail.status ? 'success' : 'danger'">
              {{ userDetail.status ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">{{ userDetail.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 调整会员等级对话框 -->
    <el-dialog v-model="levelVisible" title="调整会员等级" width="400px">
      <el-form :model="levelForm" label-width="100px">
        <el-form-item label="当前等级">
          <el-tag :type="getMemberLevelType(levelForm.currentLevel)">
            {{ getMemberLevelText(levelForm.currentLevel) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新等级">
          <el-select v-model="levelForm.newLevel" placeholder="请选择新等级">
            <el-option label="普通会员" :value="0" />
            <el-option label="黄金会员" :value="1" />
            <el-option label="钻石会员" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelVisible = false">取消</el-button>
        <el-button type="primary" @click="handleLevelSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 调整积分对话框 -->
    <el-dialog v-model="pointsVisible" title="调整积分" width="400px">
      <el-form :model="pointsForm" label-width="100px">
        <el-form-item label="当前积分">
          <span>{{ pointsForm.currentPoints }}</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="pointsForm.type">
            <el-radio label="add">增加</el-radio>
            <el-radio label="subtract">减少</el-radio>
            <el-radio label="set">设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量">
          <el-input-number v-model="pointsForm.points" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePointsSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 用户统计对话框 -->
    <el-dialog v-model="statisticsVisible" title="用户消费统计" width="600px">
      <div v-if="userStatistics">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-statistic title="总订单数" :value="userStatistics.totalOrders" />
          </el-col>
          <el-col :span="12">
            <el-statistic title="总消费金额" :value="userStatistics.totalAmount" prefix="¥" />
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-statistic title="平均订单金额" :value="userStatistics.avgAmount" prefix="¥" />
          </el-col>
          <el-col :span="12">
            <el-statistic title="最后下单时间" :value="userStatistics.lastOrderTime" />
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getUserList, 
  getUserDetail, 
  updateUserStatus, 
  updateMemberLevel, 
  updateUserPoints,
  getUserStatistics
} from '@/api'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const levelVisible = ref(false)
const pointsVisible = ref(false)
const statisticsVisible = ref(false)
const userDetail = ref(null)
const userStatistics = ref(null)

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  memberLevel: '',
  status: ''
})

const levelForm = reactive({
  userId: null,
  currentLevel: 0,
  newLevel: 0
})

const pointsForm = reactive({
  userId: null,
  currentPoints: 0,
  points: 0,
  type: 'add'
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryForm)
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
    memberLevel: '',
    status: ''
  })
  handleQuery()
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getUserDetail(row.id)
    if (res.code === 200) {
      userDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

const handleEditLevel = (row) => {
  levelForm.userId = row.id
  levelForm.currentLevel = row.memberLevel
  levelForm.newLevel = row.memberLevel
  levelVisible.value = true
}

const handleLevelSubmit = async () => {
  try {
    await updateMemberLevel(levelForm.userId, levelForm.newLevel)
    ElMessage.success('会员等级调整成功')
    levelVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('会员等级调整失败')
  }
}

const handleEditPoints = (row) => {
  pointsForm.userId = row.id
  pointsForm.currentPoints = row.points
  pointsForm.points = 0
  pointsForm.type = 'add'
  pointsVisible.value = true
}

const handlePointsSubmit = async () => {
  try {
    await updateUserPoints(pointsForm.userId, pointsForm.points, pointsForm.type)
    ElMessage.success('积分调整成功')
    pointsVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('积分调整失败')
  }
}

const handleStatistics = async (row) => {
  try {
    const res = await getUserStatistics(row.id)
    if (res.code === 200) {
      userStatistics.value = res.data
      statisticsVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户统计失败')
  }
}

const getMemberLevelType = (level) => {
  const types = { 0: '', 1: 'warning', 2: 'success' }
  return types[level] || ''
}

const getMemberLevelText = (level) => {
  const texts = { 0: '普通会员', 1: '黄金会员', 2: '钻石会员' }
  return texts[level] || '普通会员'
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
</style>
