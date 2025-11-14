<template>
  <div class="member-management">
    <!-- 会员统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ memberStats.totalMembers || 0 }}</div>
              <div class="stat-label">总会员数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ memberStats.goldMembers || 0 }}</div>
              <div class="stat-label">黄金会员</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><Crown /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ memberStats.diamondMembers || 0 }}</div>
              <div class="stat-label">钻石会员</div>
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
              <div class="stat-value">{{ memberStats.newMembersToday || 0 }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 会员管理 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员管理</span>
          <div class="header-actions">
            <el-button type="success" @click="showBatchDialog" :disabled="selectedUsers.length === 0">
              批量操作 ({{ selectedUsers.length }})
            </el-button>
            <el-button type="primary" @click="exportUsers">导出数据</el-button>
            <el-button @click="refreshData">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="用户名/手机号" clearable />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="queryForm.memberLevel" placeholder="请选择会员等级" clearable>
            <el-option label="全部" value="" />
            <el-option label="普通会员" :value="0" />
            <el-option label="黄金会员" :value="1" />
            <el-option label="钻石会员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
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

      <!-- 会员列表 -->
      <el-table
        :data="memberList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getMemberLevelTag(row.memberLevel)">
              {{ getMemberLevelName(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="{ row }">
            ¥{{ row.balance }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatTime(row.lastLoginTime) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showUserDetail(row)">详情</el-button>
            <el-button type="primary" size="small" @click="showMemberLevelDialog(row)">
              等级
            </el-button>
            <el-button type="success" size="small" @click="showPointsDialog(row)">
              积分
            </el-button>
            <el-button type="warning" size="small" @click="showBalanceDialog(row)">
              余额
            </el-button>
            <el-dropdown trigger="click">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="toggleUserStatus(row)">
                    <el-icon><Switch /></el-icon>
                    {{ row.status === 1 ? '禁用' : '启用' }}
                  </el-dropdown-item>
                  <el-dropdown-item @click="showOrderHistory(row)">
                    <el-icon><List /></el-icon>
                    订单历史
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="用户详情" width="800px">
      <div v-if="currentUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getMemberLevelTag(currentUser.memberLevel)">
              {{ getMemberLevelName(currentUser.memberLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="积分">{{ currentUser.points }}</el-descriptions-item>
          <el-descriptions-item label="余额">¥{{ currentUser.balance }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">
            {{ currentUser.lastLoginTime ? formatTime(currentUser.lastLoginTime) : '从未登录' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 用户统计 -->
        <div v-if="userStatistics" style="margin-top: 20px;">
          <h4>消费统计</h4>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="user-stat-item">
                <div class="stat-label">总订单数</div>
                <div class="stat-value">{{ userStatistics.totalOrders }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="user-stat-item">
                <div class="stat-label">总消费金额</div>
                <div class="stat-value">¥{{ userStatistics.totalAmount }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="user-stat-item">
                <div class="stat-label">平均客单价</div>
                <div class="stat-value">¥{{ userStatistics.avgAmount }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="user-stat-item">
                <div class="stat-label">最后消费</div>
                <div class="stat-value">{{ userStatistics.lastOrderTime || '无' }}</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>

    <!-- 会员等级调整对话框 -->
    <el-dialog v-model="memberLevelDialogVisible" title="调整会员等级" width="500px">
      <el-form :model="memberLevelForm" label-width="100px">
        <el-form-item label="用户">
          <span>{{ currentUser?.nickname || currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="当前等级">
          <el-tag :type="getMemberLevelTag(currentUser?.memberLevel)">
            {{ getMemberLevelName(currentUser?.memberLevel) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新等级">
          <el-radio-group v-model="memberLevelForm.memberLevel">
            <el-radio :label="0">普通会员</el-radio>
            <el-radio :label="1">黄金会员</el-radio>
            <el-radio :label="2">钻石会员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberLevelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateMemberLevel">确定</el-button>
      </template>
    </el-dialog>

    <!-- 积分调整对话框 -->
    <el-dialog v-model="pointsDialogVisible" title="调整积分" width="500px">
      <el-form :model="pointsForm" label-width="100px">
        <el-form-item label="用户">
          <span>{{ currentUser?.nickname || currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="当前积分">
          <span>{{ currentUser?.points }}</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="pointsForm.type">
            <el-radio label="add">增加积分</el-radio>
            <el-radio label="subtract">减少积分</el-radio>
            <el-radio label="set">设置积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量">
          <el-input-number v-model="pointsForm.points" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePoints">确定</el-button>
      </template>
    </el-dialog>

    <!-- 余额调整对话框 -->
    <el-dialog v-model="balanceDialogVisible" title="调整余额" width="500px">
      <el-form :model="balanceForm" label-width="100px">
        <el-form-item label="用户">
          <span>{{ currentUser?.nickname || currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span>¥{{ currentUser?.balance }}</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="balanceForm.type">
            <el-radio label="add">充值</el-radio>
            <el-radio label="subtract">扣减</el-radio>
            <el-radio label="set">设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="balanceForm.balance" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="操作原因">
          <el-input v-model="balanceForm.reason" placeholder="请输入操作原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="balanceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateBalance">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量操作" width="500px">
      <el-form label-width="100px">
        <el-form-item label="选中用户">
          <span>已选择 {{ selectedUsers.length }} 个用户</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="batchOperation">
            <el-radio label="enable">批量启用</el-radio>
            <el-radio label="disable">批量禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchOperation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const memberList = ref([])
const total = ref(0)
const selectedUsers = ref([])

// 统计数据
const memberStats = reactive({
  totalMembers: 0,
  goldMembers: 0,
  diamondMembers: 0,
  newMembersToday: 0
})

// 查询表单
const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  memberLevel: '',
  status: ''
})

// 对话框状态
const detailDialogVisible = ref(false)
const memberLevelDialogVisible = ref(false)
const pointsDialogVisible = ref(false)
const balanceDialogVisible = ref(false)
const batchDialogVisible = ref(false)

const currentUser = ref(null)
const userStatistics = ref(null)

// 表单数据
const memberLevelForm = reactive({
  memberLevel: 0
})

const pointsForm = reactive({
  type: 'add',
  points: 0
})

const balanceForm = reactive({
  type: 'add',
  balance: 0,
  reason: ''
})

const batchOperation = ref('enable')

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadMemberStats(),
    handleQuery()
  ])
}

// 加载会员统计
const loadMemberStats = async () => {
  try {
    const res = await request({
      url: '/admin/user/member/statistics',
      method: 'get'
    })
    if (res.code === 200) {
      Object.assign(memberStats, res.data)
    }
  } catch (error) {
    console.error('加载会员统计失败:', error)
  }
}

// 查询会员
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/user/page',
      method: 'get',
      params: queryForm
    })
    if (res.code === 200) {
      memberList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败')
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
    memberLevel: '',
    status: ''
  })
  handleQuery()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 显示用户详情
const showUserDetail = async (user) => {
  currentUser.value = user
  try {
    const res = await request({
      url: `/admin/user/${user.id}/statistics`,
      method: 'get'
    })
    if (res.code === 200) {
      userStatistics.value = res.data
    }
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
  detailDialogVisible.value = true
}

// 显示会员等级调整对话框
const showMemberLevelDialog = (user) => {
  currentUser.value = user
  memberLevelForm.memberLevel = user.memberLevel
  memberLevelDialogVisible.value = true
}

// 更新会员等级
const updateMemberLevel = async () => {
  try {
    const res = await request({
      url: `/admin/user/${currentUser.value.id}/member-level`,
      method: 'put',
      data: { memberLevel: memberLevelForm.memberLevel }
    })
    if (res.code === 200) {
      ElMessage.success('会员等级更新成功')
      memberLevelDialogVisible.value = false
      handleQuery()
      loadMemberStats()
    }
  } catch (error) {
    console.error('更新会员等级失败:', error)
  }
}

// 显示积分调整对话框
const showPointsDialog = (user) => {
  currentUser.value = user
  pointsForm.type = 'add'
  pointsForm.points = 0
  pointsDialogVisible.value = true
}

// 更新积分
const updatePoints = async () => {
  try {
    const res = await request({
      url: `/admin/user/${currentUser.value.id}/points`,
      method: 'put',
      data: {
        points: pointsForm.points,
        type: pointsForm.type
      }
    })
    if (res.code === 200) {
      ElMessage.success('积分更新成功')
      pointsDialogVisible.value = false
      handleQuery()
    }
  } catch (error) {
    console.error('更新积分失败:', error)
  }
}

// 显示余额调整对话框
const showBalanceDialog = (user) => {
  currentUser.value = user
  balanceForm.type = 'add'
  balanceForm.balance = 0
  balanceForm.reason = ''
  balanceDialogVisible.value = true
}

// 更新余额
const updateBalance = async () => {
  if (!balanceForm.reason.trim()) {
    ElMessage.warning('请输入操作原因')
    return
  }
  
  try {
    const res = await request({
      url: `/admin/user/${currentUser.value.id}/balance`,
      method: 'put',
      data: {
        balance: balanceForm.balance,
        type: balanceForm.type,
        reason: balanceForm.reason
      }
    })
    if (res.code === 200) {
      ElMessage.success('余额更新成功')
      balanceDialogVisible.value = false
      handleQuery()
    }
  } catch (error) {
    console.error('更新余额失败:', error)
  }
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}此用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request({
      url: `/admin/user/${user.id}/status`,
      method: 'put',
      data: { status: newStatus }
    })
    
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      handleQuery()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

// 显示批量操作对话框
const showBatchDialog = () => {
  batchOperation.value = 'enable'
  batchDialogVisible.value = true
}

// 处理批量操作
const handleBatchOperation = async () => {
  try {
    const userIds = selectedUsers.value.map(user => user.id)
    const status = batchOperation.value === 'enable' ? 1 : 0
    
    const res = await request({
      url: '/admin/user/batch/status',
      method: 'put',
      data: { userIds, status }
    })
    
    if (res.code === 200) {
      ElMessage.success('批量操作成功')
      batchDialogVisible.value = false
      handleQuery()
    }
  } catch (error) {
    console.error('批量操作失败:', error)
  }
}

// 导出用户数据
const exportUsers = async () => {
  try {
    const res = await request({
      url: '/admin/user/export',
      method: 'get',
      params: {
        keyword: queryForm.keyword,
        memberLevel: queryForm.memberLevel
      }
    })
    if (res.code === 200) {
      ElMessage.success('导出成功')
    }
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 显示订单历史
const showOrderHistory = (user) => {
  ElMessage.info('订单历史功能开发中...')
}

// 刷新数据
const refreshData = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getMemberLevelName = (level) => {
  const levelMap = {
    0: '普通会员',
    1: '黄金会员',
    2: '钻石会员'
  }
  return levelMap[level] || '未知'
}

const getMemberLevelTag = (level) => {
  const tagMap = {
    0: 'info',
    1: 'warning',
    2: 'success'
  }
  return tagMap[level] || 'info'
}
</script>

<style scoped>
.member-management {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.user-stat-item {
  text-align: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.user-stat-item .stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.user-stat-item .stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>
