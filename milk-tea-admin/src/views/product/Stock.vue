<template>
  <div class="stock-management">
    <!-- 库存统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProducts }}</div>
              <div class="stat-label">总商品数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.lowStockProducts }}</div>
              <div class="stat-label">库存不足</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon size="24"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.zeroStockProducts }}</div>
              <div class="stat-label">零库存</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.sufficientStockProducts }}</div>
              <div class="stat-label">库存充足</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警 -->
    <el-card class="alert-card" v-if="alertProducts.length > 0">
      <template #header>
        <div class="card-header">
          <span>库存预警</span>
          <el-button type="text" @click="refreshAlerts">刷新</el-button>
        </div>
      </template>
      <div class="alert-list">
        <div v-for="product in alertProducts" :key="product.id" class="alert-item">
          <div class="product-info">
            <img :src="product.image || '/default-product.jpg'" class="product-image" />
            <div class="product-details">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-category">分类ID: {{ product.categoryId }}</div>
            </div>
          </div>
          <div class="stock-info">
            <el-tag :type="getStockTagType(product.stock)" size="large">
              库存: {{ product.stock }}
            </el-tag>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click="showStockDialog(product)">
              补货
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 商品库存管理 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品库存管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showBatchStockDialog">批量操作</el-button>
            <el-button @click="refreshData">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.keyword" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="queryForm.stockStatus" placeholder="请选择库存状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="库存充足" value="sufficient" />
            <el-option label="库存不足" value="low" />
            <el-option label="零库存" value="zero" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 商品列表 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品图片" width="80">
          <template #default="{ row }">
            <img :src="row.image || '/default-product.jpg'" class="table-product-image" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="categoryId" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="当前库存" width="120">
          <template #default="{ row }">
            <el-tag :type="getStockTagType(row.stock)" size="large">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showStockDialog(row)">
              库存操作
            </el-button>
            <el-button type="success" size="small" @click="quickRestock(row)">
              快速补货
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
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 库存操作对话框 -->
    <el-dialog v-model="stockDialogVisible" title="库存操作" width="500px">
      <el-form :model="stockForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ currentProduct?.name }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ currentProduct?.stock }}</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="stockForm.operation">
            <el-radio label="add">增加库存</el-radio>
            <el-radio label="subtract">减少库存</el-radio>
            <el-radio label="set">设置库存</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="stockForm.stock" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockForm.remark" type="textarea" placeholder="请输入操作备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStockUpdate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量库存操作" width="600px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="选中商品">
          <span>已选择 {{ selectedRows.length }} 个商品</span>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="batchForm.operation">
            <el-radio label="add">批量增加</el-radio>
            <el-radio label="subtract">批量减少</el-radio>
            <el-radio label="set">批量设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="batchForm.stock" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchStockUpdate">确定</el-button>
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
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const alertProducts = ref([])
const selectedRows = ref([])

// 统计数据
const statistics = reactive({
  totalProducts: 0,
  lowStockProducts: 0,
  zeroStockProducts: 0,
  sufficientStockProducts: 0
})

// 查询表单
const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  stockStatus: ''
})

// 库存操作对话框
const stockDialogVisible = ref(false)
const currentProduct = ref(null)
const stockForm = reactive({
  operation: 'add',
  stock: 0,
  remark: ''
})

// 批量操作对话框
const batchDialogVisible = ref(false)
const batchForm = reactive({
  operation: 'add',
  stock: 0
})

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadStatistics(),
    loadAlertProducts(),
    loadCategories(),
    handleQuery()
  ])
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await request({
      url: '/admin/product/stock/statistics',
      method: 'get'
    })
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载预警商品
const loadAlertProducts = async () => {
  try {
    const res = await request({
      url: '/admin/product/stock/alerts',
      method: 'get',
      params: { threshold: 10 }
    })
    if (res.code === 200) {
      alertProducts.value = res.data
    }
  } catch (error) {
    console.error('加载预警商品失败:', error)
  }
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await request({
      url: '/admin/category/list',
      method: 'get'
    })
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 查询商品
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/product/page',
      method: 'get',
      params: queryForm
    })
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

// 重置查询
const handleReset = () => {
  Object.assign(queryForm, {
    page: 1,
    size: 10,
    keyword: '',
    categoryId: '',
    stockStatus: ''
  })
  handleQuery()
}

// 获取库存标签类型
const getStockTagType = (stock) => {
  if (stock === 0) return 'danger'
  if (stock <= 10) return 'warning'
  if (stock <= 50) return 'info'
  return 'success'
}

// 显示库存操作对话框
const showStockDialog = (product) => {
  currentProduct.value = product
  stockForm.operation = 'add'
  stockForm.stock = 0
  stockForm.remark = ''
  stockDialogVisible.value = true
}

// 显示批量操作对话框
const showBatchStockDialog = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  batchForm.operation = 'add'
  batchForm.stock = 0
  batchDialogVisible.value = true
}

// 处理库存更新
const handleStockUpdate = async () => {
  try {
    const res = await request({
      url: `/admin/product/${currentProduct.value.id}/stock`,
      method: 'put',
      data: {
        stock: stockForm.stock,
        operation: stockForm.operation
      }
    })
    if (res.code === 200) {
      ElMessage.success('库存更新成功')
      stockDialogVisible.value = false
      refreshData()
    }
  } catch (error) {
    console.error('库存更新失败:', error)
    ElMessage.error('库存更新失败')
  }
}

// 处理批量库存更新
const handleBatchStockUpdate = async () => {
  try {
    const items = selectedRows.value.map(row => ({
      id: row.id,
      stock: batchForm.stock,
      operation: batchForm.operation
    }))
    
    const res = await request({
      url: '/admin/product/batch/stock',
      method: 'put',
      data: { items }
    })
    if (res.code === 200) {
      ElMessage.success('批量更新成功')
      batchDialogVisible.value = false
      refreshData()
    }
  } catch (error) {
    console.error('批量更新失败:', error)
    ElMessage.error('批量更新失败')
  }
}

// 快速补货
const quickRestock = async (product) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入补货数量', '快速补货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^\d+$/,
      inputErrorMessage: '请输入有效的数量'
    })
    
    const res = await request({
      url: `/admin/product/${product.id}/stock`,
      method: 'put',
      data: {
        stock: parseInt(value),
        operation: 'add'
      }
    })
    if (res.code === 200) {
      ElMessage.success('补货成功')
      refreshData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('补货失败:', error)
      ElMessage.error('补货失败')
    }
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 刷新预警
const refreshAlerts = () => {
  loadAlertProducts()
  ElMessage.success('预警信息已刷新')
}

// 刷新数据
const refreshData = () => {
  loadData()
}
</script>

<style scoped>
.stock-management {
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

.alert-card {
  margin-bottom: 20px;
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

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 8px;
}

.product-info {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 12px;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
}

.table-product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.product-category {
  font-size: 12px;
  color: #909399;
}

.stock-info {
  margin: 0 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
