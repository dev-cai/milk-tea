<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><ShoppingBag /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todaySales }}</div>
              <div class="stat-label">今日销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todayCustomerPrice || stats.avgOrderAmount }}</div>
              <div class="stat-label">客单价</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrders }}</div>
              <div class="stat-label">待处理订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预警系统 -->
    <el-row :gutter="20" class="alerts-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统预警</span>
              <el-button link @click="refreshAlerts">刷新</el-button>
            </div>
          </template>
          <div class="alerts-container">
            <div v-for="alert in alerts" :key="alert.type" class="alert-item" :class="alert.level">
              <el-icon class="alert-icon">
                <Warning v-if="alert.level === 'warning'" />
                <CircleClose v-else-if="alert.level === 'error'" />
                <InfoFilled v-else />
              </el-icon>
              <div class="alert-content">
                <div class="alert-title">{{ alert.title }}</div>
                <div class="alert-message">{{ alert.message }}</div>
              </div>
              <div class="alert-time">{{ alert.time }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="trendPeriod" size="small" @change="loadSalesTrend">
                <el-radio-button value="7">近7天</el-radio-button>
                <el-radio-button value="30">近30天</el-radio-button>
                <el-radio-button value="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="salesChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>商品销量排行</span>
          </template>
          <div ref="productChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单状态分布 -->
    <el-row :gutter="20" class="status-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div ref="statusChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户增长趋势</span>
          </template>
          <div ref="userChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  getDashboardStats, 
  getSalesTrend, 
  getProductRanking, 
  getUserGrowth,
  getAlerts,
  getOrderStatus
} from '@/api/dashboard'

const salesChart = ref()
const productChart = ref()
const statusChart = ref()
const userChart = ref()

const trendPeriod = ref('7')

// 统计数据
const stats = reactive({
  todayOrders: 0,
  todaySales: 0,
  avgOrderAmount: 0,  // 客单价
  pendingOrders: 0
})

// 预警数据
const alerts = ref([])

// 图表实例
let salesChartInstance = null
let productChartInstance = null
let statusChartInstance = null
let userChartInstance = null

onMounted(() => {
  loadDashboardData()
})

onActivated(() => {
  // 使用 nextTick 确保 DOM 已经更新
  nextTick(() => {
    // 如果图表已存在，只需调整大小，不重新加载数据
    if (salesChartInstance && productChartInstance && statusChartInstance && userChartInstance) {
      salesChartInstance.resize()
      productChartInstance.resize()
      statusChartInstance.resize()
      userChartInstance.resize()
    } else {
      // 如果图表不存在，重新加载数据并初始化
      loadDashboardData()
    }
  })
})

onBeforeUnmount(() => {
  // 销毁图表实例
  if (salesChartInstance) salesChartInstance.dispose()
  if (productChartInstance) productChartInstance.dispose()
  if (statusChartInstance) statusChartInstance.dispose()
  if (userChartInstance) userChartInstance.dispose()
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    await Promise.all([
      loadStats(),
      loadAlerts(),
      loadSalesTrend(),
      loadProductRanking(),
      loadOrderStatus(),
      loadUserGrowth()
    ])
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载预警信息
const loadAlerts = async () => {
  try {
    const res = await getAlerts()
    if (res.code === 200) {
      alerts.value = res.data
    }
  } catch (error) {
    console.error('加载预警信息失败:', error)
  }
}

// 加载销售趋势
const loadSalesTrend = async () => {
  try {
    const res = await getSalesTrend({ days: trendPeriod.value })
    if (res.code === 200) {
      initSalesChart(res.data)
    }
  } catch (error) {
    console.error('加载销售趋势失败:', error)
  }
}

// 加载商品排行
const loadProductRanking = async () => {
  try {
    const res = await getProductRanking({ limit: 10 })
    if (res.code === 200) {
      initProductChart(res.data)
    }
  } catch (error) {
    console.error('加载商品排行失败:', error)
  }
}

// 加载订单状态
const loadOrderStatus = async () => {
  try {
    const res = await getOrderStatus()
    if (res.code === 200) {
      initStatusChart(res.data)
    }
  } catch (error) {
    console.error('加载订单状态失败:', error)
  }
}

// 加载用户增长
const loadUserGrowth = async () => {
  try {
    const res = await getUserGrowth({ days: 30 })
    if (res.code === 200) {
      initUserChart(res.data)
    }
  } catch (error) {
    console.error('加载用户增长失败:', error)
  }
}

// 初始化销售趋势图表
const initSalesChart = (data) => {
  if (!salesChart.value) return
  
  if (salesChartInstance) {
    salesChartInstance.dispose()
    salesChartInstance = null
  }
  
  salesChartInstance = echarts.init(salesChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['销售额', '订单数']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        data: data.sales,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: data.orders,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  salesChartInstance.setOption(option)
}

// 初始化商品排行图表
const initProductChart = (data) => {
  if (!productChart.value) return
  
  if (productChartInstance) {
    productChartInstance.dispose()
    productChartInstance = null
  }
  
  productChartInstance = echarts.init(productChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.names.reverse()
    },
    series: [{
      type: 'bar',
      data: data.salesCount.reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  }
  productChartInstance.setOption(option)
}

// 初始化订单状态图表
const initStatusChart = (data) => {
  if (!statusChart.value) return
  
  if (statusChartInstance) {
    statusChartInstance.dispose()
    statusChartInstance = null
  }
  
  statusChartInstance = echarts.init(statusChart.value)
  const statusMap = {
    pending: '待支付',
    processing: '制作中',
    ready: '待取餐',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款'
  }
  
  const pieData = Object.entries(data.statusCount).map(([key, value]) => ({
    name: statusMap[key],
    value: value
  }))
  
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
      data: pieData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  statusChartInstance.setOption(option)
}

// 初始化用户增长图表
const initUserChart = (data) => {
  if (!userChart.value) return
  
  if (userChartInstance) {
    userChartInstance.dispose()
    userChartInstance = null
  }
  
  userChartInstance = echarts.init(userChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增用户', '总用户数']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: [
      {
        type: 'value',
        name: '新增用户',
        position: 'left'
      },
      {
        type: 'value',
        name: '总用户数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '新增用户',
        type: 'bar',
        data: data.newUsers,
        itemStyle: {
          color: '#E6A23C'
        }
      },
      {
        name: '总用户数',
        type: 'line',
        yAxisIndex: 1,
        data: data.totalUsers,
        smooth: true,
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
  userChartInstance.setOption(option)
}

// 刷新预警信息
const refreshAlerts = () => {
  loadAlerts()
  ElMessage.success('预警信息已刷新')
}
</script>

<style scoped>
.dashboard {
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

.alerts-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alerts-container {
  max-height: 200px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 6px;
  border-left: 4px solid;
}

.alert-item.warning {
  background-color: #fdf6ec;
  border-left-color: #e6a23c;
}

.alert-item.error {
  background-color: #fef0f0;
  border-left-color: #f56c6c;
}

.alert-item.info {
  background-color: #f4f4f5;
  border-left-color: #909399;
}

.alert-icon {
  margin-right: 12px;
  font-size: 18px;
}

.alert-item.warning .alert-icon {
  color: #e6a23c;
}

.alert-item.error .alert-icon {
  color: #f56c6c;
}

.alert-item.info .alert-icon {
  color: #909399;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.alert-message {
  font-size: 14px;
  color: #606266;
}

.alert-time {
  font-size: 12px;
  color: #c0c4cc;
}

.charts-row {
  margin-bottom: 20px;
}

.status-row {
  margin-bottom: 20px;
}
</style>
