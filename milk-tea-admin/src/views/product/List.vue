<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品列表</span>
          <div>
            <el-button type="success" @click="handleBatchEnable" :disabled="!selectedRows.length">批量上架</el-button>
            <el-button type="warning" @click="handleBatchDisable" :disabled="!selectedRows.length">批量下架</el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length">批量删除</el-button>
            <el-button type="primary" @click="handleAdd">添加商品</el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm">
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
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }">
            <el-image 
              :src="row.image || '/placeholder.jpg'" 
              style="width: 50px; height: 50px;"
              fit="cover"
              :preview-src-list="[row.image || '/placeholder.jpg']"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="memberPrice" label="会员价" width="100">
          <template #default="{ row }">
            ¥{{ row.memberPrice || row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="isRecommend" label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRecommend ? 'warning' : 'info'">
              {{ row.isRecommend ? '推荐' : '普通' }}
            </el-tag>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getProductList, 
  deleteProduct, 
  updateProductStatus,
  batchDeleteProducts,
  batchUpdateProductStatus,
  getProductCategories
} from '@/api'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedRows = ref([])
const categories = ref([])

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  status: ''
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getProductList(queryForm)
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
    categoryId: '',
    status: ''
  })
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleStatusChange = async (row) => {
  try {
    await updateProductStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleBatchEnable = async () => {
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 1)
    ElMessage.success('批量上架成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('批量上架失败')
  }
}

const handleBatchDisable = async () => {
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 0)
    ElMessage.success('批量下架成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('批量下架失败')
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个商品吗？`, '批量删除', {
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteProducts(ids)
    ElMessage.success('批量删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleAdd = () => {
  ElMessage.info('添加商品功能开发中')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑商品: ${row.name}`)
}

const handleView = (row) => {
  ElMessage.info(`查看商品详情: ${row.name}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const loadCategories = async () => {
  try {
    const res = await getProductCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

onMounted(() => {
  handleQuery()
  loadCategories()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
