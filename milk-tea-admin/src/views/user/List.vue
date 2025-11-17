<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="queryForm.memberLevel" placeholder="请选择" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="普通会员" :value="0" />
            <el-option label="黄金会员" :value="1" />
            <el-option label="钻石会员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar || '/default-avatar.png'" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getMemberLevelType(row.memberLevel)">
              {{ getMemberLevelText(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="{ row }">
            ¥{{ row.balance }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
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
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button type="success" size="small" @click="handleAdjustLevel(row)">
              调整等级
            </el-button>
            <el-button type="warning" size="small" @click="handleAdjustPoints(row)">
              调整积分
            </el-button>
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
        style="margin-top: 20px"
      />
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="800px">
      <div v-if="userDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userDetail.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getMemberLevelType(userDetail.memberLevel)">
              {{ getMemberLevelText(userDetail.memberLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="积分">{{ userDetail.points }}</el-descriptions-item>
          <el-descriptions-item label="余额">¥{{ userDetail.balance }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
              {{ userDetail.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ userDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ userDetail.lastLoginTime || '未登录' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 10px 0">消费统计</h4>
        <el-descriptions :column="2" border v-if="userStats">
          <el-descriptions-item label="订单总数">{{ userStats.totalOrders }}</el-descriptions-item>
          <el-descriptions-item label="消费总额">¥{{ userStats.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="平均客单价">¥{{ userStats.avgAmount }}</el-descriptions-item>
          <el-descriptions-item label="最后下单时间">{{ userStats.lastOrderTime || '无' }}</el-descriptions-item>
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
        <el-form-item label="调整为">
          <el-select v-model="levelForm.newLevel" placeholder="请选择">
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
          <el-input :value="pointsForm.currentPoints" disabled />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import userApi from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const levelVisible = ref(false)
const pointsVisible = ref(false)
const userDetail = ref(null)
const userStats = ref(null)

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
  type: 'add',
  points: 0
})

onMounted(() => {
  handleQuery()
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await userApi.getList(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
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

const handleView = async (row) => {
  try {
    const [detailRes, statsRes] = await Promise.all([
      userApi.getById(row.id),
      userApi.getStatistics(row.id)
    ])
    
    if (detailRes.code === 200) {
      userDetail.value = detailRes.data
    }
    if (statsRes.code === 200) {
      userStats.value = statsRes.data
    }
    
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await userApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleAdjustLevel = (row) => {
  levelForm.userId = row.id
  levelForm.currentLevel = row.memberLevel
  levelForm.newLevel = row.memberLevel
  levelVisible.value = true
}

const handleLevelSubmit = async () => {
  try {
    await userApi.updateMemberLevel(levelForm.userId, levelForm.newLevel)
    ElMessage.success('会员等级调整成功')
    levelVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('会员等级调整失败')
  }
}

const handleAdjustPoints = (row) => {
  pointsForm.userId = row.id
  pointsForm.currentPoints = row.points
  pointsForm.type = 'add'
  pointsForm.points = 0
  pointsVisible.value = true
}

const handlePointsSubmit = async () => {
  try {
    await userApi.updatePoints(pointsForm.userId, {
      type: pointsForm.type,
      points: pointsForm.points
    })
    ElMessage.success('积分调整成功')
    pointsVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('积分调整失败')
  }
}

const getMemberLevelType = (level) => {
  const types = { 0: '', 1: 'warning', 2: 'danger' }
  return types[level] || ''
}

const getMemberLevelText = (level) => {
  const texts = { 0: '普通会员', 1: '黄金会员', 2: '钻石会员' }
  return texts[level] || '未知'
}
</script>

<style scoped lang="scss">
.user-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
