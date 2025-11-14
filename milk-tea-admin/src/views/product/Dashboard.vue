<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF;">
              <el-icon :size="30"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A;">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todaySales || 0 }}</div>
              <div class="stat-label">今日销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C;">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C;">
              <el-icon :size="30"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
              <div class="stat-label">待处理订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>销售趋势</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getDashboardStats, getSalesTrend, getProductRanking } from '@/api'

const salesChart = ref()
const productChart = ref()

// 统计数据
const stats = reactive({
  todayOrders: 0,
  todaySales: 0,
  totalUsers: 0,
  pendingOrders: 0
})

// 图表数据
const chartData = reactive({
  salesTrend: {
    dates: [],
    values: []
  },
  productRanking: {
    names: [],
    values: []
  }
})

onMounted(() => {
  loadDashboardData()
})

// 每次页面激活时重新加载数据（配合keep-alive使用）
onActivated(() => {
  loadDashboardData()
})

// 组件销毁前清理ECharts实例
onBeforeUnmount(() => {
  if (salesChart.value) {
    const salesChartInstance = echarts.getInstanceByDom(salesChart.value)
    if (salesChartInstance) {
      salesChartInstance.dispose()
    }
  }
  if (productChart.value) {
    const productChartInstance = echarts.getInstanceByDom(productChart.value)
    if (productChartInstance) {
      productChartInstance.dispose()
    }
  }
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statsRes = await getDashboardStats()
    if (statsRes.code === 200) {
      Object.assign(stats, statsRes.data)
    }

    // 加载销售趋势数据
    const salesRes = await getSalesTrend({ days: 7 })
    if (salesRes.code === 200) {
      chartData.salesTrend = salesRes.data
      initSalesChart()
    }

    // 加载商品排行数据
    const rankingRes = await getProductRanking({ limit: 5 })
    if (rankingRes.code === 200) {
      chartData.productRanking = rankingRes.data
      initProductChart()
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    // 使用模拟数据
    loadMockData()
  }
}

// 加载模拟数据（当API不可用时）
const loadMockData = () => {
  stats.todayOrders = 128
  stats.todaySales = 3280
  stats.totalUsers = 1256
  stats.pendingOrders = 15

  chartData.salesTrend = {
    dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    values: [820, 932, 901, 934, 1290, 1330, 1320]
  }

  chartData.productRanking = {
    names: ['珍珠奶茶', '芋泥奶茶', '芝士奶盖茶', '百香果茶', '波霸奶茶'],
    values: [156, 145, 134, 112, 98]
  }

  initSalesChart()
  initProductChart()
}

const initSalesChart = () => {
  // 如果已存在图表实例，先销毁
  if (salesChart.value) {
    const existingChart = echarts.getInstanceByDom(salesChart.value)
    if (existingChart) {
      existingChart.dispose()
    }
  }
  const chart = echarts.init(salesChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: chartData.salesTrend.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: chartData.salesTrend.values,
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }]
  }
  chart.setOption(option)
}

const initProductChart = () => {
  // 如果已存在图表实例，先销毁
  if (productChart.value) {
    const existingChart = echarts.getInstanceByDom(productChart.value)
    if (existingChart) {
      existingChart.dispose()
    }
  }
  const chart = echarts.init(productChart.value)
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
      data: chartData.productRanking.names
    },
    series: [{
      type: 'bar',
      data: chartData.productRanking.values,
      itemStyle: {
        color: '#67C23A'
      }
    }]
  }
  chart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
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
</style>
