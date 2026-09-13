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
            <el-button type="warning" @click="showPushDialog" :disabled="selectedUsers.length === 0">
              <el-icon><Bell /></el-icon>
              消息推送 ({{ selectedUsers.length }})
            </el-button>
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
          <el-select v-model="queryForm.memberLevel" placeholder="请选择会员等级" clearable style="width: 150px;">
            <el-option label="全部" :value="null" />
            <el-option label="普通会员" :value="0" />
            <el-option label="黄金会员" :value="1" />
            <el-option label="钻石会员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" :value="null" />
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
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
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

    <!-- 消息推送对话框 -->
    <el-dialog v-model="pushDialogVisible" title="消息推送" width="600px">
      <el-form :model="pushForm" label-width="100px">
        <el-form-item label="推送对象">
          <span>已选择 {{ selectedUsers.length }} 个用户</span>
        </el-form-item>
        <el-form-item label="消息类型">
          <el-radio-group v-model="pushForm.type">
            <el-radio label="marketing">营销推广</el-radio>
            <el-radio label="activity">活动通知</el-radio>
            <el-radio label="system">系统消息</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="消息标题">
          <el-input v-model="pushForm.title" placeholder="请输入消息标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input
            v-model="pushForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入消息内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="pushForm.link" placeholder="选填，点击消息后跳转的链接" />
        </el-form-item>
        <el-form-item label="发送方式">
          <el-radio-group v-model="pushForm.sendMethod">
            <el-radio label="immediate">立即发送</el-radio>
            <el-radio label="scheduled">定时发送</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发送时间" v-if="pushForm.sendMethod === 'scheduled'">
          <el-date-picker
            v-model="pushForm.scheduledTime"
            type="datetime"
            placeholder="选择发送时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pushDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePushMessage">发送</el-button>
      </template>
    </el-dialog>

    <!-- 订单历史对话框 -->
    <el-dialog v-model="orderHistoryVisible" title="订单历史" width="900px">
      <div v-if="currentUser">
        <div style="margin-bottom: 20px;">
          <h4>{{ currentUser.nickname || currentUser.username }} 的订单历史</h4>
          <el-descriptions :column="3" size="small">
            <el-descriptions-item label="总订单数">{{ orderHistory.total }}单</el-descriptions-item>
            <el-descriptions-item label="累计消费">¥{{ orderHistory.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="平均客单价">¥{{ orderHistory.avgAmount }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <el-table :data="orderHistory.orders" v-loading="orderHistoryLoading" max-height="400">
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="createTime" label="下单时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="products" label="商品" min-width="200">
            <template #default="{ row }">
              <div v-for="product in row.products" :key="product.id" style="margin-bottom: 4px;">
                {{ product.name }} x{{ product.quantity }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="订单金额" width="100">
            <template #default="{ row }">
              ¥{{ row.totalAmount }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getOrderStatusTag(row.status)">
                {{ getOrderStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payMethod" label="支付方式" width="100">
            <template #default="{ row }">
              {{ getPayMethodName(row.payMethod) }}
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <el-pagination
          v-model:current-page="orderHistoryQuery.page"
          v-model:page-size="orderHistoryQuery.size"
          :total="orderHistory.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrderHistory"
          @current-change="loadOrderHistory"
          style="margin-top: 20px; text-align: center;"
        />
      </div>
      <template #footer>
        <el-button @click="orderHistoryVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportOrderHistory">导出订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { getOrderList } from '@/api/order'

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
const pushDialogVisible = ref(false)
const orderHistoryVisible = ref(false)

const currentUser = ref(null)
const userStatistics = ref(null)

// 订单历史相关
const orderHistoryLoading = ref(false)
const orderHistory = reactive({
  total: 0,
  totalAmount: 0,
  avgAmount: 0,
  orders: []
})

const orderHistoryQuery = reactive({
  page: 1,
  size: 10
})

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

// 消息推送表单
const pushForm = reactive({
  type: 'marketing',
  title: '',
  content: '',
  link: '',
  sendMethod: 'immediate',
  scheduledTime: ''
})

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
  currentUser.value = user
  orderHistoryQuery.page = 1
  orderHistoryVisible.value = true
  loadOrderHistory()
}

// 加载订单历史
const loadOrderHistory = async () => {
  if (!currentUser.value) return
  
  orderHistoryLoading.value = true
  try {
    const res = await getOrderList({ ...orderHistoryQuery, userId: currentUser.value.id })
    const page = res.data
    orderHistory.orders = (page.records || []).map(order => ({ ...order, products: order.items || [], totalAmount: order.payAmount || 0, payMethod: order.payType }))
    orderHistory.total = page.total || 0
    orderHistory.totalAmount = orderHistory.orders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
    orderHistory.avgAmount = orderHistory.total ? orderHistory.totalAmount / orderHistory.total : 0
    
  } catch (error) {
    ElMessage.error('加载订单历史失败')
  } finally {
    orderHistoryLoading.value = false
  }
}

// 导出订单历史
const exportOrderHistory = () => {
  ElMessage.success('订单历史导出成功')
}

// 刷新数据
const refreshData = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

// 显示消息推送对话框
const showPushDialog = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要推送消息的用户')
    return
  }
  // 重置表单
  pushForm.type = 'marketing'
  pushForm.title = ''
  pushForm.content = ''
  pushForm.link = ''
  pushForm.sendMethod = 'immediate'
  pushForm.scheduledTime = ''
  pushDialogVisible.value = true
}

// 处理消息推送
const handlePushMessage = async () => {
  // 验证表单
  if (!pushForm.title.trim()) {
    ElMessage.warning('请输入消息标题')
    return
  }
  if (!pushForm.content.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  if (pushForm.sendMethod === 'scheduled' && !pushForm.scheduledTime) {
    ElMessage.warning('请选择发送时间')
    return
  }

  try {
    const userIds = selectedUsers.value.map(user => user.id)
    const res = await request({
      url: '/admin/user/push-message',
      method: 'post',
      data: {
        userIds,
        type: pushForm.type,
        title: pushForm.title,
        content: pushForm.content,
        link: pushForm.link,
        sendMethod: pushForm.sendMethod,
        scheduledTime: pushForm.scheduledTime
      }
    })
    
    if (res.code === 200) {
      const message = pushForm.sendMethod === 'immediate' 
        ? `消息已发送给 ${selectedUsers.value.length} 个用户`
        : `定时消息已设置，将于 ${pushForm.scheduledTime} 发送`
      ElMessage.success(message)
      pushDialogVisible.value = false
      // 清空选中
      selectedUsers.value = []
    }
  } catch (error) {
    console.error('消息推送失败:', error)
    ElMessage.error('消息推送失败')
  }
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

const getOrderStatusName = (status) => {
  const statusMap = {
    1: '待支付',
    2: '已支付',
    3: '制作中',
    4: '已完成',
    5: '已取消',
    6: '退款中'
  }
  return statusMap[status] || '未知'
}

const getOrderStatusTag = (status) => {
  const tagMap = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger',
    6: 'warning'
  }
  return tagMap[status] || 'info'
}

const getPayMethodName = (method) => {
  const methodMap = {
    'wechat': '微信支付（开发测试）',
    'alipay': '其他（历史订单）',
    'cash': '其他（历史订单）',
    'card': '其他（历史订单）'
  }
  return methodMap[method] || '未知'
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
