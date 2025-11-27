<template>
  <div class="aftersale-management">
    <!-- 售后统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><RefreshRight /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRefunds || 0 }}</div>
              <div class="stat-label">总退款申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingRefunds || 0 }}</div>
              <div class="stat-label">待处理退款</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalComplaints || 0 }}</div>
              <div class="stat-label">总投诉</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingComplaints || 0 }}</div>
              <div class="stat-label">待处理投诉</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能标签页 -->
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="退款管理" name="refund">
          <!-- 退款申请列表 -->
          <div class="refund-management">
            <!-- 筛选条件 -->
            <el-form :model="refundQuery" :inline="true" class="search-form">
              <el-form-item label="状态">
                <el-select v-model="refundQuery.status" placeholder="请选择状态" clearable style="width: 150px;">
                  <el-option label="全部" :value="null" />
                  <el-option label="待处理" :value="0" />
                  <el-option label="已同意" :value="1" />
                  <el-option label="已拒绝" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadRefundRequests">查询</el-button>
                <el-button @click="resetRefundQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <!-- 退款列表 -->
            <el-table :data="refundList" v-loading="refundLoading">
              <el-table-column prop="orderNo" label="订单号" width="180" />
              <el-table-column prop="customerName" label="客户" width="120" />
              <el-table-column prop="refundAmount" label="退款金额" width="120">
                <template #default="{ row }">
                  ¥{{ row.refundAmount }}
                </template>
              </el-table-column>
              <el-table-column prop="reason" label="退款原因" min-width="200" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getRefundStatusTag(row.status)">
                    {{ getRefundStatusName(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="申请时间" width="160">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="220" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button
                      v-if="row.status === 0"
                      type="success"
                      size="small"
                      @click="approveRefund(row)"
                    >
                      同意
                    </el-button>
                    <el-button
                      v-if="row.status === 0"
                      type="danger"
                      size="small"
                      @click="showRejectDialog(row)"
                    >
                      拒绝
                    </el-button>
                    <el-button size="small" @click="showRefundDetail(row)">
                      详情
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
              v-model:current-page="refundQuery.page"
              v-model:page-size="refundQuery.size"
              :total="refundTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadRefundRequests"
              @current-change="loadRefundRequests"
              style="margin-top: 20px;"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="投诉管理" name="complaint">
          <!-- 投诉处理列表 -->
          <div class="complaint-management">
            <!-- 筛选条件 -->
            <el-form :model="complaintQuery" :inline="true" class="search-form">
              <el-form-item label="状态">
                <el-select v-model="complaintQuery.status" placeholder="请选择状态" clearable style="width: 150px;">
                  <el-option label="全部" :value="null" />
                  <el-option label="待处理" :value="0" />
                  <el-option label="处理中" :value="1" />
                  <el-option label="已解决" :value="2" />
                  <el-option label="已关闭" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadComplaints">查询</el-button>
                <el-button @click="resetComplaintQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <!-- 投诉列表 -->
            <el-table :data="complaintList" v-loading="complaintLoading">
              <el-table-column prop="orderNo" label="订单号" width="180" />
              <el-table-column prop="customerName" label="客户" width="120" />
              <el-table-column prop="complaintType" label="投诉类型" width="120">
                <template #default="{ row }">
                  <el-tag>{{ getComplaintTypeName(row.complaintType) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="投诉内容" min-width="200" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getComplaintStatusTag(row.status)">
                    {{ getComplaintStatusName(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="投诉时间" width="160">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button
                      v-if="row.status === 0"
                      type="primary"
                      size="small"
                      @click="showResponseDialog(row)"
                    >
                      处理
                    </el-button>
                    <el-button size="small" @click="showComplaintDetail(row)">
                      详情
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
              v-model:current-page="complaintQuery.page"
              v-model:page-size="complaintQuery.size"
              :total="complaintTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadComplaints"
              @current-change="loadComplaints"
              style="margin-top: 20px;"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="意见反馈" name="feedback">
          <!-- 意见反馈管理 -->
          <div class="feedback-management">
            <!-- 筛选条件 -->
            <el-form :model="feedbackQuery" :inline="true" class="search-form">
              <el-form-item label="状态">
                <el-select v-model="feedbackQuery.status" placeholder="请选择状态" clearable style="width: 150px;">
                  <el-option label="全部" :value="null" />
                  <el-option label="待处理" :value="0" />
                  <el-option label="处理中" :value="1" />
                  <el-option label="已回复" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadFeedbacks">查询</el-button>
                <el-button @click="resetFeedbackQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <!-- 反馈列表 -->
            <el-table :data="feedbackList" v-loading="feedbackLoading">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="type" label="反馈类型" width="120">
                <template #default="{ row }">
                  <el-tag>{{ getFeedbackTypeName(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
              <el-table-column prop="contactPhone" label="联系电话" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getFeedbackStatusTag(row.status)">
                    {{ getFeedbackStatusName(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="提交时间" width="160">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button
                      v-if="row.status !== 2"
                      type="primary"
                      size="small"
                      @click="showFeedbackReplyDialog(row)"
                    >
                      回复
                    </el-button>
                    <el-button size="small" @click="showFeedbackDetail(row)">
                      详情
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
              v-model:current-page="feedbackQuery.page"
              v-model:page-size="feedbackQuery.size"
              :total="feedbackTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadFeedbacks"
              @current-change="loadFeedbacks"
              style="margin-top: 20px;"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 拒绝退款对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝退款" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ currentRefund?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="退款金额">
          <span>¥{{ currentRefund?.refundAmount }}</span>
        </el-form-item>
        <el-form-item label="拒绝原因" required>
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectRefund">确定拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 投诉回复对话框 -->
    <el-dialog v-model="responseDialogVisible" title="处理投诉" width="600px">
      <el-form :model="responseForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ currentComplaint?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="投诉内容">
          <div class="complaint-content">{{ currentComplaint?.content }}</div>
        </el-form-item>
        <el-form-item label="处理状态" required>
          <el-radio-group v-model="responseForm.status">
            <el-radio :label="1">处理中</el-radio>
            <el-radio :label="2">已解决</el-radio>
            <el-radio :label="3">关闭投诉</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复内容" required>
          <el-input
            v-model="responseForm.response"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="responseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="processComplaint">确定</el-button>
      </template>
    </el-dialog>

    <!-- 退款详情对话框 -->
    <el-dialog v-model="refundDetailVisible" title="退款详情" width="700px">
      <div v-if="currentRefund">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ currentRefund.customerName }}</el-descriptions-item>
          <el-descriptions-item label="退款金额">¥{{ currentRefund.refundAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getRefundStatusTag(currentRefund.status)">
              {{ getRefundStatusName(currentRefund.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatTime(currentRefund.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="处理时间">
            {{ currentRefund.processTime ? formatTime(currentRefund.processTime) : '未处理' }}
          </el-descriptions-item>
          <el-descriptions-item label="退款原因" :span="2">{{ currentRefund.reason }}</el-descriptions-item>
          <el-descriptions-item label="拒绝原因" :span="2" v-if="currentRefund.rejectReason">
            {{ currentRefund.rejectReason }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 投诉详情对话框 -->
    <el-dialog v-model="complaintDetailVisible" title="投诉详情" width="700px">
      <div v-if="currentComplaint">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentComplaint.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ currentComplaint.customerName }}</el-descriptions-item>
          <el-descriptions-item label="投诉类型">{{ getComplaintTypeName(currentComplaint.complaintType) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getComplaintStatusTag(currentComplaint.status)">
              {{ getComplaintStatusName(currentComplaint.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="投诉时间">{{ formatTime(currentComplaint.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="处理时间">
            {{ currentComplaint.processTime ? formatTime(currentComplaint.processTime) : '未处理' }}
          </el-descriptions-item>
          <el-descriptions-item label="投诉内容" :span="2">{{ currentComplaint.content }}</el-descriptions-item>
          <el-descriptions-item label="处理回复" :span="2" v-if="currentComplaint.response">
            {{ currentComplaint.response }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 反馈回复对话框 -->
    <el-dialog v-model="feedbackReplyDialogVisible" title="回复反馈" width="600px">
      <el-form :model="feedbackReplyForm" label-width="100px">
        <el-form-item label="反馈类型">
          <span>{{ getFeedbackTypeName(currentFeedback?.type) }}</span>
        </el-form-item>
        <el-form-item label="反馈内容">
          <div class="feedback-content">{{ currentFeedback?.content }}</div>
        </el-form-item>
        <el-form-item label="联系方式">
          <span>{{ currentFeedback?.contactPhone || currentFeedback?.contactEmail || '未提供' }}</span>
        </el-form-item>
        <el-form-item label="回复内容" required>
          <el-input
            v-model="feedbackReplyForm.reply"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackReplyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="replyFeedback">确定</el-button>
      </template>
    </el-dialog>

    <!-- 反馈详情对话框 -->
    <el-dialog v-model="feedbackDetailVisible" title="反馈详情" width="700px">
      <div v-if="currentFeedback">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ currentFeedback.id }}</el-descriptions-item>
          <el-descriptions-item label="反馈类型">{{ getFeedbackTypeName(currentFeedback.type) }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentFeedback.contactPhone || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="联系邮箱">{{ currentFeedback.contactEmail || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getFeedbackStatusTag(currentFeedback.status)">
              {{ getFeedbackStatusName(currentFeedback.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatTime(currentFeedback.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="反馈内容" :span="2">{{ currentFeedback.content }}</el-descriptions-item>
          <el-descriptions-item label="回复内容" :span="2" v-if="currentFeedback.reply">
            {{ currentFeedback.reply }}
          </el-descriptions-item>
          <el-descriptions-item label="回复时间" :span="2" v-if="currentFeedback.replyTime">
            {{ formatTime(currentFeedback.replyTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { getFeedbackList, replyFeedback as replyFeedbackApi } from '@/api/order'

// 数据定义
const activeTab = ref('refund')
const refundLoading = ref(false)
const complaintLoading = ref(false)

// 统计数据
const statistics = reactive({
  totalRefunds: 0,
  pendingRefunds: 0,
  totalComplaints: 0,
  pendingComplaints: 0
})

// 退款相关
const refundList = ref([])
const refundTotal = ref(0)
const refundQuery = reactive({
  page: 1,
  size: 10,
  status: ''
})

// 投诉相关
const complaintList = ref([])
const complaintTotal = ref(0)
const complaintQuery = reactive({
  page: 1,
  size: 10,
  status: ''
})

// 反馈相关
const feedbackList = ref([])
const feedbackTotal = ref(0)
const feedbackLoading = ref(false)
const feedbackQuery = reactive({
  page: 1,
  size: 10,
  status: null
})

// 对话框状态
const rejectDialogVisible = ref(false)
const responseDialogVisible = ref(false)
const refundDetailVisible = ref(false)
const complaintDetailVisible = ref(false)
const feedbackReplyDialogVisible = ref(false)
const feedbackDetailVisible = ref(false)

const currentRefund = ref(null)
const currentComplaint = ref(null)
const currentFeedback = ref(null)

// 表单数据
const rejectForm = reactive({
  reason: ''
})

const responseForm = reactive({
  status: 1,
  response: ''
})

const feedbackReplyForm = reactive({
  reply: ''
})

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadStatistics(),
    loadRefundRequests(),
    loadComplaints()
  ])
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await request({
      url: '/admin/order/aftersale/statistics',
      method: 'get'
    })
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载退款申请
const loadRefundRequests = async () => {
  refundLoading.value = true
  try {
    const res = await request({
      url: '/admin/order/refund/requests',
      method: 'get',
      params: refundQuery
    })
    if (res.code === 200) {
      refundList.value = res.data.records
      refundTotal.value = res.data.total
    }
  } catch (error) {
    console.error('加载退款申请失败:', error)
  } finally {
    refundLoading.value = false
  }
}

// 加载投诉列表
const loadComplaints = async () => {
  complaintLoading.value = true
  try {
    const res = await request({
      url: '/admin/order/complaints',
      method: 'get',
      params: complaintQuery
    })
    if (res.code === 200) {
      complaintList.value = res.data.records
      complaintTotal.value = res.data.total
    }
  } catch (error) {
    console.error('加载投诉列表失败:', error)
  } finally {
    complaintLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'refund') {
    loadRefundRequests()
  } else if (tabName === 'complaint') {
    loadComplaints()
  } else if (tabName === 'feedback') {
    loadFeedbacks()
  }
}

// 同意退款
const approveRefund = async (refund) => {
  try {
    await ElMessageBox.confirm('确定同意此退款申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request({
      url: `/admin/order/refund/${refund.id}/process`,
      method: 'put',
      data: { approve: true }
    })
    
    if (res.code === 200) {
      ElMessage.success('退款申请已同意')
      loadRefundRequests()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('处理退款失败:', error)
    }
  }
}

// 显示拒绝对话框
const showRejectDialog = (refund) => {
  currentRefund.value = refund
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

// 拒绝退款
const rejectRefund = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    const res = await request({
      url: `/admin/order/refund/${currentRefund.value.id}/process`,
      method: 'put',
      data: {
        approve: false,
        rejectReason: rejectForm.reason
      }
    })
    
    if (res.code === 200) {
      ElMessage.success('退款申请已拒绝')
      rejectDialogVisible.value = false
      loadRefundRequests()
      loadStatistics()
    }
  } catch (error) {
    console.error('拒绝退款失败:', error)
  }
}

// 显示投诉回复对话框
const showResponseDialog = (complaint) => {
  currentComplaint.value = complaint
  responseForm.status = 1
  responseForm.response = ''
  responseDialogVisible.value = true
}

// 处理投诉
const processComplaint = async () => {
  if (!responseForm.response.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    const res = await request({
      url: `/admin/order/complaint/${currentComplaint.value.id}/process`,
      method: 'put',
      data: {
        response: responseForm.response,
        status: responseForm.status
      }
    })
    
    if (res.code === 200) {
      ElMessage.success('投诉处理成功')
      responseDialogVisible.value = false
      loadComplaints()
      loadStatistics()
    }
  } catch (error) {
    console.error('处理投诉失败:', error)
  }
}

// 显示退款详情
const showRefundDetail = (refund) => {
  currentRefund.value = refund
  refundDetailVisible.value = true
}

// 显示投诉详情
const showComplaintDetail = (complaint) => {
  currentComplaint.value = complaint
  complaintDetailVisible.value = true
}

// 重置查询
const resetRefundQuery = () => {
  Object.assign(refundQuery, {
    page: 1,
    size: 10,
    status: ''
  })
  loadRefundRequests()
}

const resetComplaintQuery = () => {
  Object.assign(complaintQuery, {
    page: 1,
    size: 10,
    status: ''
  })
  loadComplaints()
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getRefundStatusName = (status) => {
  const statusMap = {
    0: '待处理',
    1: '已同意',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

const getRefundStatusTag = (status) => {
  const tagMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return tagMap[status] || 'info'
}

const getComplaintStatusName = (status) => {
  const statusMap = {
    0: '待处理',
    1: '处理中',
    2: '已解决',
    3: '已关闭'
  }
  return statusMap[status] || '未知'
}

const getComplaintStatusTag = (status) => {
  const tagMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return tagMap[status] || 'info'
}

const getComplaintTypeName = (type) => {
  const typeMap = {
    1: '商品质量',
    2: '服务态度',
    3: '配送问题',
    4: '其他'
  }
  return typeMap[type] || '未知'
}

// 加载反馈列表
const loadFeedbacks = async () => {
  feedbackLoading.value = true
  try {
    const res = await getFeedbackList(feedbackQuery)
    if (res.code === 200) {
      feedbackList.value = res.data.records
      feedbackTotal.value = res.data.total
    }
  } catch (error) {
    console.error('加载反馈列表失败:', error)
  } finally {
    feedbackLoading.value = false
  }
}

// 显示反馈回复对话框
const showFeedbackReplyDialog = (feedback) => {
  currentFeedback.value = feedback
  feedbackReplyForm.reply = ''
  feedbackReplyDialogVisible.value = true
}

// 回复反馈
const replyFeedback = async () => {
  if (!feedbackReplyForm.reply.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    const res = await replyFeedbackApi({
      id: currentFeedback.value.id,
      reply: feedbackReplyForm.reply
    })
    
    if (res.code === 200) {
      ElMessage.success('回复成功')
      feedbackReplyDialogVisible.value = false
      loadFeedbacks()
    }
  } catch (error) {
    console.error('回复反馈失败:', error)
    ElMessage.error('回复失败')
  }
}

// 显示反馈详情
const showFeedbackDetail = (feedback) => {
  currentFeedback.value = feedback
  feedbackDetailVisible.value = true
}

// 重置反馈查询
const resetFeedbackQuery = () => {
  Object.assign(feedbackQuery, {
    page: 1,
    size: 10,
    status: null
  })
  loadFeedbacks()
}

// 反馈相关工具函数
const getFeedbackTypeName = (type) => {
  const typeMap = {
    1: '产品问题',
    2: '服务问题',
    3: '配送问题',
    4: '支付问题',
    5: '功能建议',
    6: '其他问题'
  }
  return typeMap[type] || '未知'
}

const getFeedbackStatusName = (status) => {
  const statusMap = {
    0: '待处理',
    1: '处理中',
    2: '已回复'
  }
  return statusMap[status] || '未知'
}

const getFeedbackStatusTag = (status) => {
  const tagMap = {
    0: 'warning',
    1: 'primary',
    2: 'success'
  }
  return tagMap[status] || 'info'
}
</script>

<style scoped>
.aftersale-management {
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

.search-form {
  margin-bottom: 20px;
}

.complaint-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  max-height: 100px;
  overflow-y: auto;
}

.feedback-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  max-height: 100px;
  overflow-y: auto;
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
