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
          <el-select v-model="queryForm.categoryId" placeholder="请选择分类" clearable style="width: 150px;">
            <el-option 
              v-for="category in categories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" :value="null" />
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
            <div v-if="row.image" class="image-container" @click="previewImage(row.image)">
              <img 
                :src="row.image" 
                style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; cursor: pointer;"
                onerror="this.style.display='none'; this.parentElement.innerHTML='<div style=&quot;width: 50px; height: 50px; background: #f5f5f5; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #ccc; font-size: 12px;&quot;>无图</div>'"
              />
            </div>
            <div v-else class="no-image" style="width: 50px; height: 50px; background: #f5f5f5; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #ccc; font-size: 12px;">
              无图
            </div>
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

    <!-- 商品编辑/添加对话框 -->
    <el-dialog v-model="formDialogVisible" :title="formTitle" width="800px">
      <el-form :model="productForm" :rules="formRules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="productForm.name" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="productForm.categoryId" placeholder="请选择分类" style="width: 100%;">
                <el-option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price">
              <el-input-number v-model="productForm.price" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员价格" prop="memberPrice">
              <el-input-number v-model="productForm.memberPrice" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number v-model="productForm.stock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品状态" prop="status">
              <el-radio-group v-model="productForm.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="image-uploader"
            action="/api/admin/upload/image"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :on-error="handleImageError"
            :before-upload="beforeImageUpload"
          >
            <img v-if="productForm.image" :src="productForm.image" class="image-preview" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="productForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入商品描述" 
          />
        </el-form-item>

        <el-form-item label="推荐商品">
          <el-switch v-model="productForm.isRecommend" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 商品详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="商品详情" width="800px">
      <div v-if="currentProduct">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentProduct.name }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentProduct.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ currentProduct.price }}</el-descriptions-item>
          <el-descriptions-item label="会员价">¥{{ currentProduct.memberPrice || currentProduct.price }}</el-descriptions-item>
          <el-descriptions-item label="库存">{{ currentProduct.stock }}</el-descriptions-item>
          <el-descriptions-item label="销量">{{ currentProduct.sales }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentProduct.status === 1 ? 'success' : 'danger'">
              {{ currentProduct.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="推荐">
            <el-tag :type="currentProduct.isRecommend ? 'warning' : 'info'">
              {{ currentProduct.isRecommend ? '推荐' : '普通' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentProduct.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatTime(currentProduct.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="currentProduct.image" style="margin-top: 20px;">
          <h4>商品图片</h4>
          <el-image 
            :src="currentProduct.image" 
            style="width: 200px; height: 200px;"
            fit="cover"
            :preview-src-list="[currentProduct.image]"
            :preview-disabled="false"
          />
        </div>
        
        <div v-if="currentProduct.description" style="margin-top: 20px;">
          <h4>商品描述</h4>
          <p>{{ currentProduct.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getProductList, 
  getProduct,
  addProduct,
  updateProduct,
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

// 对话框状态
const formDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const submitLoading = ref(false)
const currentProduct = ref(null)
const formRef = ref()

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  status: ''
})

// 商品表单
const productForm = reactive({
  id: null,
  name: '',
  categoryId: '',
  price: 0,
  memberPrice: 0,
  stock: 0,
  image: '',
  description: '',
  status: 1,
  isRecommend: 0
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

// 计算属性
const formTitle = computed(() => {
  return productForm.id ? '编辑商品' : '添加商品'
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
  resetForm()
  formDialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await getProduct(row.id)
    if (res.code === 200) {
      Object.assign(productForm, res.data)
      formDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getProduct(row.id)
    if (res.code === 200) {
      currentProduct.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  }
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

// 重置表单
const resetForm = () => {
  Object.assign(productForm, {
    id: null,
    name: '',
    categoryId: '',
    price: 0,
    memberPrice: 0,
    stock: 0,
    image: '',
    description: '',
    status: 1,
    isRecommend: 0
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const formData = { ...productForm }
    if (formData.id) {
      await updateProduct(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addProduct(formData)
      ElMessage.success('添加成功')
    }
    
    formDialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) { // 表单验证失败时error为false
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 图片上传成功回调
const handleImageSuccess = (response) => {
  console.log('上传成功响应:', response)
  if (response.code === 200) {
    productForm.image = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败: ' + (response.message || '未知错误'))
  }
}

// 图片上传失败回调
const handleImageError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败，请检查网络连接或联系管理员')
}

// 图片预览功能
const previewImage = (imageSrc) => {
  if (!imageSrc) return
  
  // 创建简单的预览对话框
  const previewDialog = document.createElement('div')
  previewDialog.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 3000;
    cursor: pointer;
  `
  
  const img = document.createElement('img')
  img.src = imageSrc
  img.style.cssText = `
    max-width: 90%;
    max-height: 90%;
    object-fit: contain;
  `
  
  previewDialog.appendChild(img)
  document.body.appendChild(previewDialog)
  
  // 点击关闭预览
  previewDialog.addEventListener('click', () => {
    document.body.removeChild(previewDialog)
  })
}


// 图片上传前验证
const beforeImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
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

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s;
}

.image-uploader:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

/* 修复表格中图片预览的样式 */
.image-container {
  display: inline-block;
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  transition: transform 0.3s;
}

.image-container:hover {
  transform: scale(1.05);
}

.image-container img {
  transition: all 0.3s;
}

.image-container:hover img {
  filter: brightness(1.1);
}

.no-image {
  font-size: 12px;
}

</style>
