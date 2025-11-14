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
                <el-radio-button label="7">近7天</el-radio-button>
                <el-radio-button label="30">近30天</el-radio-button>
                <el-radio-button label="90">近90天</el-radio-button>
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewCustomerDetail(row)">详情</el-button>
            <el-button type="primary" size="small" @click="sendPersonalizedOffer(row)">
              专属优惠
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

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
    // 模拟数据
    Object.assign(analysisData, {
      avgConsumption: 156.8,
      avgFrequency: 2.3,
      totalRevenue: 285600,
      retentionRate: 68.5
    })
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

// 加载消费分析
const loadConsumptionAnalysis = async () => {
  try {
    // 模拟数据
    const data = {
      ranges: ['0-50', '50-100', '100-200', '200-500', '500+'],
      counts: [120, 280, 350, 180, 70]
    }
    
    initConsumptionChart(data)
  } catch (error) {
    console.error('加载消费分析失败:', error)
  }
}

// 加载等级分布
const loadLevelDistribution = async () => {
  try {
    // 模拟数据
    const data = [
      { name: '普通会员', value: 650 },
      { name: '黄金会员', value: 280 },
      { name: '钻石会员', value: 70 }
    ]
    
    initLevelChart(data)
  } catch (error) {
    console.error('加载等级分布失败:', error)
  }
}

// 加载行为分析
const loadBehaviorAnalysis = async () => {
  try {
    // 模拟数据
    const data = {
      categories: ['早餐时段', '午餐时段', '下午茶', '晚餐时段', '夜宵时段'],
      values: [15, 35, 25, 20, 5]
    }
    
    initBehaviorChart(data)
  } catch (error) {
    console.error('加载行为分析失败:', error)
  }
}

// 加载活跃度趋势
const loadActivityTrend = async () => {
  try {
    // 模拟数据
    const data = {
      dates: ['11-07', '11-08', '11-09', '11-10', '11-11', '11-12', '11-13'],
      activeUsers: [320, 280, 350, 400, 380, 420, 390],
      newUsers: [20, 15, 25, 30, 28, 35, 22]
    }
    
    initActivityChart(data)
  } catch (error) {
    console.error('加载活跃度趋势失败:', error)
  }
}

// 加载会员分层
const loadMemberSegmentation = async () => {
  segmentLoading.value = true
  try {
    // 模拟数据
    memberSegments.value = [
      {
        segment: 'champion',
        count: 85,
        percentage: 8.5,
        avgConsumption: 450,
        avgFrequency: 5.2,
        totalRevenue: 38250,
        characteristics: '高频高额消费，忠诚度极高',
        suggestions: ['VIP专属服务', '新品优先体验', '生日特权']
      },
      {
        segment: 'loyal',
        count: 180,
        percentage: 18.0,
        avgConsumption: 280,
        avgFrequency: 3.8,
        totalRevenue: 50400,
        characteristics: '消费稳定，品牌忠诚度高',
        suggestions: ['会员积分奖励', '推荐奖励', '节日优惠']
      },
      {
        segment: 'potential',
        count: 220,
        percentage: 22.0,
        avgConsumption: 180,
        avgFrequency: 2.5,
        totalRevenue: 39600,
        characteristics: '有消费潜力，需要激活',
        suggestions: ['个性化推荐', '优惠券刺激', '会员升级引导']
      },
      {
        segment: 'new',
        count: 315,
        percentage: 31.5,
        avgConsumption: 85,
        avgFrequency: 1.2,
        totalRevenue: 26775,
        characteristics: '新用户，消费习惯待培养',
        suggestions: ['新人礼包', '引导消费', '体验优化']
      },
      {
        segment: 'atrisk',
        count: 120,
        percentage: 12.0,
        avgConsumption: 120,
        avgFrequency: 0.8,
        totalRevenue: 14400,
        characteristics: '消费下降，有流失风险',
        suggestions: ['挽回营销', '问卷调研', '专属客服']
      },
      {
        segment: 'lost',
        count: 80,
        percentage: 8.0,
        avgConsumption: 60,
        avgFrequency: 0.3,
        totalRevenue: 4800,
        characteristics: '长期未消费，已流失',
        suggestions: ['重新激活', '大额优惠', '情感营销']
      }
    ]
  } catch (error) {
    console.error('加载会员分层失败:', error)
  } finally {
    segmentLoading.value = false
  }
}

// 加载VIP客户
const loadVipCustomers = async () => {
  vipLoading.value = true
  try {
    // 模拟数据
    vipCustomers.value = Array.from({ length: 20 }, (_, i) => ({
      id: i + 1,
      username: `vip_user_${i + 1}`,
      nickname: `VIP客户${i + 1}`,
      phone: `138****${String(i + 1).padStart(4, '0')}`,
      avatar: '',
      memberLevel: i < 5 ? 2 : i < 15 ? 1 : 0,
      totalConsumption: 5000 - i * 200,
      orderCount: 50 - i * 2,
      avgOrderAmount: (5000 - i * 200) / (50 - i * 2),
      lastOrderTime: new Date(Date.now() - i * 24 * 60 * 60 * 1000).toISOString(),
      rfmScore: 5 - Math.floor(i / 4)
    }))
  } catch (error) {
    console.error('加载VIP客户失败:', error)
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
const viewCustomerDetail = (customer) => {
  ElMessage.info(`查看客户 ${customer.nickname} 的详细信息`)
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
</style>
