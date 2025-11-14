<template>
  <div class="banner-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAdd">添加轮播图</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="标题">
          <el-input v-model="queryForm.keyword" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="image" label="图片" width="120">
          <template #default="{ row }">
            <el-image 
              :src="row.image || '/placeholder.jpg'" 
              style="width: 80px; height: 40px;" 
              fit="cover"
              :preview-src-list="[row.image || '/placeholder.jpg']"
            />
          </template>
        </el-table-column>
        <el-table-column prop="link" label="跳转链接" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="100">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.sort" 
              :min="0" 
              size="small"
              @change="handleSortChange(row)"
            />
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handlePreview(row)">预览</el-button>
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

    <!-- 添加/编辑轮播图对话框 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="600px">
      <el-form :model="bannerForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <div class="upload-container">
            <el-upload
              class="image-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
            >
              <img v-if="bannerForm.image" :src="bannerForm.image" class="uploaded-image" />
              <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tips">
              <p>建议尺寸：750x300px</p>
              <p>支持格式：jpg、png、gif</p>
              <p>文件大小：不超过2MB</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="bannerForm.link" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="bannerForm.sort" :min="0" />
          <span style="margin-left: 10px; color: #909399;">数字越小排序越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="bannerForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="轮播图预览" width="800px">
      <div class="preview-container">
        <img :src="previewImage" style="width: 100%; max-height: 400px; object-fit: contain;" />
        <div class="preview-info">
          <p><strong>标题：</strong>{{ previewData.title }}</p>
          <p><strong>跳转链接：</strong>{{ previewData.link || '无' }}</p>
          <p><strong>排序：</strong>{{ previewData.sort }}</p>
          <p><strong>状态：</strong>{{ previewData.status ? '启用' : '禁用' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getBannerList, 
  addBanner, 
  updateBanner, 
  deleteBanner,
  updateBannerStatus,
  updateBannerSort
} from '@/api'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const formVisible = ref(false)
const previewVisible = ref(false)
const previewImage = ref('')
const previewData = ref({})
const formRef = ref()

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: ''
})

const bannerForm = reactive({
  id: null,
  title: '',
  image: '',
  link: '',
  sort: 0,
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  image: [{ required: true, message: '请上传图片', trigger: 'change' }]
}

const formTitle = computed(() => {
  return bannerForm.id ? '编辑轮播图' : '添加轮播图'
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getBannerList(queryForm)
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
    status: ''
  })
  handleQuery()
}

const handleStatusChange = async (row) => {
  try {
    await updateBannerStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleSortChange = async (row) => {
  try {
    await updateBannerSort(row.id, row.sort)
    ElMessage.success('排序更新成功')
  } catch (error) {
    ElMessage.error('排序更新失败')
    handleQuery() // 重新加载数据恢复原排序
  }
}

const handleAdd = () => {
  Object.assign(bannerForm, {
    id: null,
    title: '',
    image: '',
    link: '',
    sort: 0,
    status: 1
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(bannerForm, { ...row })
  formVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (bannerForm.id) {
      await updateBanner(bannerForm.id, bannerForm)
      ElMessage.success('更新成功')
    } else {
      await addBanner(bannerForm)
      ElMessage.success('添加成功')
    }
    
    formVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) { // 表单验证失败时error为false
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
      type: 'warning'
    })
    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePreview = (row) => {
  previewImage.value = row.image
  previewData.value = { ...row }
  previewVisible.value = true
}

const beforeImageUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('只能上传 JPG/PNG/GIF 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleImageUpload = (options) => {
  // 这里应该实现真实的图片上传逻辑
  // 目前使用模拟的方式
  const file = options.file
  const reader = new FileReader()
  reader.onload = (e) => {
    bannerForm.image = e.target.result
    ElMessage.success('图片上传成功')
  }
  reader.readAsDataURL(file)
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-container {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-uploader:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 80px;
  text-align: center;
  line-height: 80px;
}

.uploaded-image {
  width: 150px;
  height: 80px;
  display: block;
  object-fit: cover;
}

.upload-tips {
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}

.upload-tips p {
  margin: 0 0 5px 0;
}

.preview-container {
  text-align: center;
}

.preview-info {
  margin-top: 20px;
  text-align: left;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.preview-info p {
  margin: 8px 0;
  line-height: 1.5;
}
</style>
