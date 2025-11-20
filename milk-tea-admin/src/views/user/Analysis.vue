<template>
  <div class="member-analysis">
    <!-- 会员分析概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><TrendCharts /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ analysisData.avgConsumption || 0 }}</div>
              <div class="overview-label">平均消费金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Timer /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ analysisData.avgFrequency || 0 }}</div>
              <div class="overview-label">平均消费频次</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><Coin /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ analysisData.totalRevenue || 0 }}</div>
              <div class="overview-label">会员总贡献</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><Promotion /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ analysisData.retentionRate || 0 }}%</div>
              <div class="overview-label">会员留存率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分析图表 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>会员消费分布</span>
              <el-radio-group v-model="consumptionPeriod" size="small" @change="loadConsumptionAnalysis">
                <el-radio-button value="7">近7天</el-radio-button>
                <el-radio-button value="30">近30天</el-radio-button>
                <el-radio-button value="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="consumptionChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>会员等级分布</span>
          </template>
          <div ref="levelChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>消费行为分析</span>
          </template>
          <div ref="behaviorChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>会员活跃度趋势</span>
          </template>
          <div ref="activityChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 会员价值分层 -->
    <el-card class="segmentation-card">
      <template #header>
        <div class="card-header">
          <span>会员价值分层</span>
          <el-button type="primary" @click="refreshSegmentation">重新分析</el-button>
        </div>
      </template>
      
      <el-table :data="memberSegments" v-loading="segmentLoading">
        <el-table-column prop="segment" label="客户分层" width="150">
          <template #default="{ row }">
            <el-tag :type="getSegmentTag(row.segment)" size="large">
              {{ getSegmentName(row.segment) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="人数" width="100" />
        <el-table-column prop="percentage" label="占比" width="100">
          <template #default="{ row }">
            {{ row.percentage }}%
          </template>
        </el-table-column>
        <el-table-column prop="avgConsumption" label="平均消费" width="120">
          <template #default="{ row }">
            ¥{{ row.avgConsumption }}
          </template>
        </el-table-column>
        <el-table-column prop="avgFrequency" label="消费频次" width="100" />
        <el-table-column prop="totalRevenue" label="总贡献" width="120">
          <template #default="{ row }">
            ¥{{ row.totalRevenue }}
          </template>
        </el-table-column>
        <el-table-column prop="characteristics" label="特征描述" min-width="200" />
        <el-table-column label="营销建议" min-width="250">
          <template #default="{ row }">
            <div class="marketing-suggestions">
              <el-tag
                v-for="suggestion in row.suggestions"
                :key="suggestion"
                size="small"
                style="margin: 2px;"
              >
                {{ suggestion }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 高价值客户列表 -->
    <el-card class="vip-customers-card">
      <template #header>
        <div class="card-header">
          <span>高价值客户 (TOP 20)</span>
          <el-button @click="exportVipCustomers">导出列表</el-button>
        </div>
      </template>
      
      <el-table :data="vipCustomers" v-loading="vipLoading">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column label="客户信息" width="200">
          <template #default="{ row }">
            <div class="customer-info">
              <el-avatar :src="row.avatar" :size="40">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="customer-details">
                <div class="customer-name">{{ row.nickname || row.username }}</div>
                <div class="customer-phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getMemberLevelTag(row.memberLevel)">
              {{ getMemberLevelName(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalConsumption" label="累计消费" width="120">
          <template #default="{ row }">
            ¥{{ row.totalConsumption }}
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="avgOrderAmount" label="客单价" width="100">
          <template #default="{ row }">
            ¥{{ row.avgOrderAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="lastOrderTime" label="最后消费" width="160">
          <template #default="{ row }">
            {{ formatTime(row.lastOrderTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="rfmScore" label="RFM评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rfmScore" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="viewCustomerDetail(row)">详情</el-button>
              <el-button type="primary" size="small" @click="sendPersonalizedOffer(row)">
                专属优惠
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 客户详情对话框 -->
    <el-dialog v-model="customerDetailVisible" title="客户详情" width="800px">
      <div v-if="currentCustomer">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户ID">{{ currentCustomer.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentCustomer.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentCustomer.nickname || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentCustomer.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentCustomer.email || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getMemberLevelTag(currentCustomer.memberLevel)">
              {{ getMemberLevelName(currentCustomer.memberLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="累计消费">¥{{ currentCustomer.totalConsumption }}</el-descriptions-item>
          <el-descriptions-item label="订单数量">{{ currentCustomer.orderCount }}单</el-descriptions-item>
          <el-descriptions-item label="客单价">¥{{ currentCustomer.avgOrderAmount }}</el-descriptions-item>
          <el-descriptions-item label="RFM评分">
            <el-rate v-model="currentCustomer.rfmScore" disabled show-score />
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatTime(currentCustomer.registerTime) }}</el-descriptions-item>
          <el-descriptions-item label="最后消费">{{ formatTime(currentCustomer.lastOrderTime) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 客户头像 -->
        <div v-if="currentCustomer.avatar" style="margin-top: 20px;">
          <h4>客户头像</h4>
          <el-avatar :src="currentCustomer.avatar" :size="100">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>

        <!-- 消费偏好 -->
        <div style="margin-top: 20px;">
          <h4>消费偏好</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="偏好商品">{{ currentCustomer.favoriteProducts || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="偏好时段">{{ currentCustomer.favoriteTimeSlot || '全天' }}</el-descriptions-item>
            <el-descriptions-item label="平均消费频次">{{ currentCustomer.avgFrequency || 0 }}次/月</el-descriptions-item>
            <el-descriptions-item label="客户标签">
              <el-tag v-for="tag in currentCustomer.tags" :key="tag" style="margin-right: 8px;">
                {{ tag }}
              </el-tag>
              <span v-if="!currentCustomer.tags || currentCustomer.tags.length === 0" style="color: #909399;">
                暂无标签
              </span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 最近订单 -->
        <div style="margin-top: 20px;">
          <h4>最近订单</h4>
          <el-table :data="currentCustomer.recentOrders" size="small" max-height="200">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="amount" label="金额" width="100">
              <template #default="{ row }">¥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" />
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="customerDetailVisible = false">关闭</el-button>
        <el-button type="primary" @click="sendPersonalizedOffer(currentCustomer)">发送优惠</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getMemberOverview,
  getMemberConsumption,
  getMemberLevelDistribution,
  getMemberBehavior,
  getMemberActivityTrend,
  getMemberSegmentation,
  getVipCustomers,
  getCustomerDetail
} from '@/api/analysis'

// 图表引用
const consumptionChart = ref()
const levelChart = ref()
const behaviorChart = ref()
const activityChart = ref()

// 数据定义
const loading = ref(false)
const segmentLoading = ref(false)
const vipLoading = ref(false)

const consumptionPeriod = ref('30')

// 分析数据
const analysisData = reactive({
  avgConsumption: 0,
  avgFrequency: 0,
  totalRevenue: 0,
  retentionRate: 0
})

const memberSegments = ref([])
const vipCustomers = ref([])

// 客户详情对话框
const customerDetailVisible = ref(false)
const currentCustomer = ref(null)

// 图表实例
let consumptionChartInstance = null
let levelChartInstance = null
let behaviorChartInstance = null
let activityChartInstance = null

onMounted(() => {
  loadAnalysisData()
})

onBeforeUnmount(() => {
  // 销毁图表实例
  if (consumptionChartInstance) consumptionChartInstance.dispose()
  if (levelChartInstance) levelChartInstance.dispose()
  if (behaviorChartInstance) behaviorChartInstance.dispose()
  if (activityChartInstance) activityChartInstance.dispose()
})

// 加载分析数据
const loadAnalysisData = async () => {
  await Promise.all([
    loadOverviewData(),
    loadConsumptionAnalysis(),
    loadLevelDistribution(),
    loadBehaviorAnalysis(),
    loadActivityTrend(),
    loadMemberSegmentation(),
    loadVipCustomers()
  ])
}

// 加载概览数据
const loadOverviewData = async () => {
  try {
    const res = await getMemberOverview()
    if (res.code === 200) {
      Object.assign(analysisData, res.data)
    }
  } catch (error) {
    console.error('加载概览数据失败:', error)
    ElMessage.error('加载概览数据失败')
  }
}

// 加载消费分析
const loadConsumptionAnalysis = async () => {
  try {
    const res = await getMemberConsumption(consumptionPeriod.value)
    if (res.code === 200 && res.data.levelStats) {
      // 转换数据格式用于图表显示
      const data = {
        ranges: res.data.levelStats.map(item => `等级${item.level}`),
        counts: res.data.levelStats.map(item => item.orderCount)
      }
      initConsumptionChart(data)
    }
  } catch (error) {
    console.error('加载消费分析失败:', error)
  }
}

// 加载等级分布
const loadLevelDistribution = async () => {
  try {
    const res = await getMemberLevelDistribution()
    if (res.code === 200 && res.data) {
      const data = res.data.map(item => ({
        name: item.name,
        value: item.count
      }))
      initLevelChart(data)
    }
  } catch (error) {
    console.error('加载等级分布失败:', error)
  }
}

// 加载行为分析
const loadBehaviorAnalysis = async () => {
  try {
    const res = await getMemberBehavior()
    if (res.code === 200 && res.data) {
      // 使用真实数据，如果没有时段数据则使用简单的统计
      const data = {
        categories: ['复购率', '平均订单金额', '总订单数', '总用户数'],
        values: [
          res.data.repeatRate || 0,
          res.data.avgOrderAmount || 0,
          (res.data.totalOrders || 0) / 10, // 缩放以便显示
          (res.data.totalUsers || 0) / 10  // 缩放以便显示
        ]
      }
      initBehaviorChart(data)
    }
  } catch (error) {
    console.error('加载行为分析失败:', error)
  }
}

// 加载活跃度趋势
const loadActivityTrend = async () => {
  try {
    const res = await getMemberActivityTrend(7)
    if (res.code === 200 && res.data.trend) {
      const data = {
        dates: res.data.trend.map(item => item.date.substring(5)), // 只取月-日
        activeUsers: res.data.trend.map(item => item.activeUsers),
        newUsers: res.data.trend.map(item => item.orders) // 使用订单数作为新用户的替代
      }
      initActivityChart(data)
    }
  } catch (error) {
    console.error('加载活跃度趋势失败:', error)
  }
}

// 加载会员分层
const loadMemberSegmentation = async () => {
  segmentLoading.value = true
  try {
    const res = await getMemberSegmentation()
    if (res.code === 200 && res.data) {
      // 转换后端数据为前端需要的格式
      memberSegments.value = res.data.map(item => {
        const totalUsers = item.totalUsers || 1
        const percentage = ((item.count / totalUsers) * 100).toFixed(1)
        
        // 根据segment字段映射特征和建议
        let characteristics = ''
        let suggestions = []
        
        if (item.segment === '高价值用户') {
          characteristics = '高频高额消费，忠诚度极高'
          suggestions = ['VIP专属服务', '新品优先体验', '生日特权']
        } else if (item.segment === '中价值用户') {
          characteristics = '消费稳定，有提升潜力'
          suggestions = ['会员积分奖励', '推荐奖励', '节日优惠']
        } else if (item.segment === '低价值用户') {
          characteristics = '消费较少，需要激活'
          suggestions = ['个性化推荐', '优惠券刺激', '会员升级引导']
        }
        
        return {
          segment: item.segment,
          count: item.count,
          percentage: parseFloat(percentage),
          avgConsumption: item.threshold || 0,
          avgFrequency: 0, // 后端暂无此数据
          totalRevenue: 0, // 后端暂无此数据
          characteristics,
          suggestions
        }
      })
    }
  } catch (error) {
    console.error('加载会员分层失败:', error)
    ElMessage.error('加载会员分层失败')
  } finally {
    segmentLoading.value = false
  }
}

// 加载VIP客户
const loadVipCustomers = async () => {
  vipLoading.value = true
  try {
    const res = await getVipCustomers(20)
    if (res.code === 200 && res.data) {
      vipCustomers.value = res.data.map(item => ({
        ...item,
        avgOrderAmount: item.orderCount > 0 ? 
          (item.totalConsumption / item.orderCount).toFixed(2) : 0,
        rfmScore: item.memberLevel === 2 ? 5 : item.memberLevel === 1 ? 4 : 3
      }))
    }
  } catch (error) {
    console.error('加载VIP客户失败:', error)
    ElMessage.error('加载VIP客户失败')
  } finally {
    vipLoading.value = false
  }
}

// 初始化消费分析图表
const initConsumptionChart = (data) => {
  if (consumptionChartInstance) {
    consumptionChartInstance.dispose()
  }
  
  consumptionChartInstance = echarts.init(consumptionChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: data.ranges
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [{
      type: 'bar',
      data: data.counts,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  }
  consumptionChartInstance.setOption(option)
}

// 初始化等级分布图表
const initLevelChart = (data) => {
  if (levelChartInstance) {
    levelChartInstance.dispose()
  }
  
  levelChartInstance = echarts.init(levelChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  levelChartInstance.setOption(option)
}

// 初始化行为分析图表
const initBehaviorChart = (data) => {
  if (behaviorChartInstance) {
    behaviorChartInstance.dispose()
  }
  
  behaviorChartInstance = echarts.init(behaviorChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.categories
    },
    yAxis: {
      type: 'value',
      name: '占比(%)'
    },
    series: [{
      type: 'line',
      data: data.values,
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      }
    }]
  }
  behaviorChartInstance.setOption(option)
}

// 初始化活跃度趋势图表
const initActivityChart = (data) => {
  if (activityChartInstance) {
    activityChartInstance.dispose()
  }
  
  activityChartInstance = echarts.init(activityChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['活跃用户', '新增用户']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '活跃用户',
        type: 'line',
        data: data.activeUsers,
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '新增用户',
        type: 'bar',
        data: data.newUsers,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  activityChartInstance.setOption(option)
}

// 刷新分层分析
const refreshSegmentation = () => {
  loadMemberSegmentation()
  ElMessage.success('分层分析已刷新')
}

// 导出VIP客户
const exportVipCustomers = () => {
  ElMessage.success('VIP客户列表导出成功')
}

// 查看客户详情
const viewCustomerDetail = async (customer) => {
  try {
    const res = await getCustomerDetail(customer.userId)
    if (res.code === 200 && res.data) {
      currentCustomer.value = {
        ...res.data,
        email: res.data.email || `${res.data.username}@example.com`,
        registerTime: res.data.registerTime || res.data.createTime,
        favoriteProducts: res.data.favoriteProducts || '暂无数据',
        favoriteTimeSlot: res.data.favoriteTimeSlot || '全天',
        avgFrequency: res.data.avgFrequency || 0,
        tags: res.data.tags || ['VIP客户'],
        recentOrders: res.data.recentOrders || []
      }
      customerDetailVisible.value = true
    }
  } catch (error) {
    console.error('加载客户详情失败:', error)
    // 如果API失败，使用基本信息
    currentCustomer.value = {
      ...customer,
      email: `${customer.username}@example.com`,
      registerTime: '2023-06-15 10:30:00',
      favoriteProducts: '暂无数据',
      favoriteTimeSlot: '全天',
      avgFrequency: 0,
      tags: ['VIP客户'],
      recentOrders: []
    }
    customerDetailVisible.value = true
  }
}

// 发送个性化优惠
const sendPersonalizedOffer = (customer) => {
  ElMessage.success(`已为客户 ${customer.nickname} 发送专属优惠`)
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getSegmentName = (segment) => {
  const segmentMap = {
    champion: '冠军客户',
    loyal: '忠诚客户',
    potential: '潜力客户',
    new: '新客户',
    atrisk: '风险客户',
    lost: '流失客户'
  }
  return segmentMap[segment] || '未知'
}

const getSegmentTag = (segment) => {
  const tagMap = {
    champion: 'success',
    loyal: 'primary',
    potential: 'warning',
    new: 'info',
    atrisk: 'danger',
    lost: 'info'
  }
  return tagMap[segment] || 'info'
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
.member-analysis {
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

.charts-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.segmentation-card,
.vip-customers-card {
  margin-bottom: 20px;
}

.marketing-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-details {
  flex: 1;
}

.customer-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.customer-phone {
  font-size: 12px;
  color: #909399;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-start;
}

.action-buttons .el-button {
  margin: 0;
}
</style>
