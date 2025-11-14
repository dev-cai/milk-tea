<template>
  <div class="recipe-management">
    <!-- 商品选择 -->
    <el-card class="product-selector">
      <template #header>
        <span>选择商品</span>
      </template>
      <el-select
        v-model="selectedProductId"
        placeholder="请选择商品"
        filterable
        @change="handleProductChange"
        style="width: 300px;"
      >
        <el-option
          v-for="product in products"
          :key="product.id"
          :label="product.name"
          :value="product.id"
        />
      </el-select>
    </el-card>

    <!-- 配方管理 -->
    <el-card v-if="selectedProductId">
      <template #header>
        <div class="card-header">
          <span>配方管理 - {{ currentProduct?.name }}</span>
          <div class="header-actions">
            <el-button type="success" @click="showCopyDialog">复制配方</el-button>
            <el-button type="primary" @click="showRecipeDialog()">添加配方</el-button>
            <el-button @click="showCostAnalysis">成本分析</el-button>
          </div>
        </div>
      </template>

      <!-- 配方列表 -->
      <el-table :data="recipes" v-loading="loading">
        <el-table-column prop="ingredientName" label="原料名称" min-width="150" />
        <el-table-column prop="quantity" label="用量" width="100">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="unitCost" label="单价" width="100">
          <template #default="{ row }">
            ¥{{ row.unitCost }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCost" label="总成本" width="100">
          <template #default="{ row }">
            ¥{{ row.totalCost }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showRecipeDialog(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteRecipe(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 成本汇总 -->
      <div class="cost-summary" v-if="recipes.length > 0">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="summary-item">
              <div class="label">配方总成本</div>
              <div class="value">¥{{ totalCost.toFixed(2) }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="label">售价</div>
              <div class="value">¥{{ currentProduct?.price || 0 }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="label">毛利润</div>
              <div class="value">¥{{ ((currentProduct?.price || 0) - totalCost).toFixed(2) }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="label">毛利率</div>
              <div class="value">{{ profitMargin.toFixed(1) }}%</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 配方编辑对话框 -->
    <el-dialog
      v-model="recipeDialogVisible"
      :title="recipeForm.id ? '编辑配方' : '添加配方'"
      width="600px"
    >
      <el-form :model="recipeForm" :rules="recipeRules" ref="recipeFormRef" label-width="100px">
        <el-form-item label="原料名称" prop="ingredientName">
          <el-input v-model="recipeForm.ingredientName" placeholder="请输入原料名称" />
        </el-form-item>
        <el-form-item label="用量" prop="quantity">
          <el-input-number v-model="recipeForm.quantity" :precision="2" :min="0" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-select v-model="recipeForm.unit" placeholder="请选择单位">
            <el-option label="克(g)" value="g" />
            <el-option label="毫升(ml)" value="ml" />
            <el-option label="个" value="个" />
            <el-option label="勺" value="勺" />
            <el-option label="包" value="包" />
            <el-option label="片" value="片" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价" prop="unitCost">
          <el-input-number v-model="recipeForm.unitCost" :precision="2" :min="0" style="width: 200px;" />
          <span style="margin-left: 10px; color: #909399;">元/{{ recipeForm.unit || '单位' }}</span>
        </el-form-item>
        <el-form-item label="总成本">
          <span>¥{{ ((recipeForm.quantity || 0) * (recipeForm.unitCost || 0)).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="recipeForm.description" type="textarea" placeholder="请输入说明" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="recipeForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recipeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecipeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 复制配方对话框 -->
    <el-dialog v-model="copyDialogVisible" title="复制配方" width="500px">
      <el-form label-width="100px">
        <el-form-item label="源商品">
          <span>{{ currentProduct?.name }}</span>
        </el-form-item>
        <el-form-item label="目标商品">
          <el-select v-model="copyTargetProductId" placeholder="请选择目标商品" filterable>
            <el-option
              v-for="product in products.filter(p => p.id !== selectedProductId)"
              :key="product.id"
              :label="product.name"
              :value="product.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="copyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCopyRecipe">确定</el-button>
      </template>
    </el-dialog>

    <!-- 成本分析对话框 -->
    <el-dialog v-model="analysisDialogVisible" title="成本分析" width="800px">
      <div v-if="costAnalysis">
        <el-row :gutter="20" class="analysis-summary">
          <el-col :span="6">
            <div class="analysis-item">
              <div class="label">商品名称</div>
              <div class="value">{{ costAnalysis.productName }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="analysis-item">
              <div class="label">售价</div>
              <div class="value">¥{{ costAnalysis.sellingPrice }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="analysis-item">
              <div class="label">总成本</div>
              <div class="value">¥{{ costAnalysis.totalCost }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="analysis-item">
              <div class="label">毛利率</div>
              <div class="value">{{ costAnalysis.profitMargin?.toFixed(1) }}%</div>
            </div>
          </el-col>
        </el-row>
        
        <el-table :data="costAnalysis.recipes" style="margin-top: 20px;">
          <el-table-column prop="ingredientName" label="原料名称" />
          <el-table-column prop="quantity" label="用量">
            <template #default="{ row }">
              {{ row.quantity }} {{ row.unit }}
            </template>
          </el-table-column>
          <el-table-column prop="unitCost" label="单价">
            <template #default="{ row }">
              ¥{{ row.unitCost }}
            </template>
          </el-table-column>
          <el-table-column prop="totalCost" label="成本">
            <template #default="{ row }">
              ¥{{ row.totalCost }}
            </template>
          </el-table-column>
          <el-table-column label="成本占比">
            <template #default="{ row }">
              {{ ((row.totalCost / costAnalysis.totalCost) * 100).toFixed(1) }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const products = ref([])
const recipes = ref([])
const selectedProductId = ref(null)
const currentProduct = ref(null)

// 对话框状态
const recipeDialogVisible = ref(false)
const copyDialogVisible = ref(false)
const analysisDialogVisible = ref(false)

// 表单数据
const recipeFormRef = ref()
const recipeForm = reactive({
  id: null,
  productId: null,
  ingredientName: '',
  quantity: 0,
  unit: 'g',
  unitCost: 0,
  description: '',
  sort: 0
})

const copyTargetProductId = ref(null)
const costAnalysis = ref(null)

// 表单验证规则
const recipeRules = {
  ingredientName: [{ required: true, message: '请输入原料名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入用量', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  unitCost: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

// 计算属性
const totalCost = computed(() => {
  return recipes.value.reduce((sum, recipe) => sum + (recipe.totalCost || 0), 0)
})

const profitMargin = computed(() => {
  const price = currentProduct.value?.price || 0
  if (price === 0) return 0
  return ((price - totalCost.value) / price) * 100
})

onMounted(() => {
  loadProducts()
})

// 加载商品列表
const loadProducts = async () => {
  try {
    const res = await request({
      url: '/admin/product/page',
      method: 'get',
      params: { page: 1, size: 1000 }
    })
    if (res.code === 200) {
      products.value = res.data.records
    }
  } catch (error) {
    console.error('加载商品失败:', error)
  }
}

// 商品变化处理
const handleProductChange = (productId) => {
  currentProduct.value = products.value.find(p => p.id === productId)
  if (productId) {
    loadRecipes(productId)
  } else {
    recipes.value = []
  }
}

// 加载配方
const loadRecipes = async (productId) => {
  loading.value = true
  try {
    const res = await request({
      url: `/admin/recipe/product/${productId}`,
      method: 'get'
    })
    if (res.code === 200) {
      recipes.value = res.data
    }
  } catch (error) {
    console.error('加载配方失败:', error)
  } finally {
    loading.value = false
  }
}

// 显示配方对话框
const showRecipeDialog = (recipe = null) => {
  if (recipe) {
    Object.assign(recipeForm, recipe)
  } else {
    Object.assign(recipeForm, {
      id: null,
      productId: selectedProductId.value,
      ingredientName: '',
      quantity: 0,
      unit: 'g',
      unitCost: 0,
      description: '',
      sort: 0
    })
  }
  recipeDialogVisible.value = true
}

// 提交配方
const handleRecipeSubmit = async () => {
  try {
    await recipeFormRef.value.validate()
    
    const url = recipeForm.id ? `/admin/recipe/${recipeForm.id}` : '/admin/recipe'
    const method = recipeForm.id ? 'put' : 'post'
    
    const res = await request({
      url,
      method,
      data: { ...recipeForm, productId: selectedProductId.value }
    })
    
    if (res.code === 200) {
      ElMessage.success(recipeForm.id ? '更新成功' : '添加成功')
      recipeDialogVisible.value = false
      loadRecipes(selectedProductId.value)
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除配方
const deleteRecipe = async (recipe) => {
  try {
    await ElMessageBox.confirm('确定要删除这个配方吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request({
      url: `/admin/recipe/${recipe.id}`,
      method: 'delete'
    })
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadRecipes(selectedProductId.value)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 显示复制对话框
const showCopyDialog = () => {
  if (recipes.value.length === 0) {
    ElMessage.warning('当前商品没有配方可复制')
    return
  }
  copyTargetProductId.value = null
  copyDialogVisible.value = true
}

// 复制配方
const handleCopyRecipe = async () => {
  if (!copyTargetProductId.value) {
    ElMessage.warning('请选择目标商品')
    return
  }
  
  try {
    const res = await request({
      url: '/admin/recipe/copy',
      method: 'post',
      data: {
        fromProductId: selectedProductId.value,
        toProductId: copyTargetProductId.value
      }
    })
    
    if (res.code === 200) {
      ElMessage.success('复制配方成功')
      copyDialogVisible.value = false
    }
  } catch (error) {
    console.error('复制失败:', error)
  }
}

// 显示成本分析
const showCostAnalysis = async () => {
  try {
    const res = await request({
      url: `/admin/recipe/product/${selectedProductId.value}/analysis`,
      method: 'get'
    })
    
    if (res.code === 200) {
      costAnalysis.value = res.data
      analysisDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取成本分析失败:', error)
  }
}
</script>

<style scoped>
.recipe-management {
  padding: 20px;
}

.product-selector {
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

.cost-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.summary-item, .analysis-item {
  text-align: center;
}

.summary-item .label, .analysis-item .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.summary-item .value, .analysis-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.analysis-summary {
  margin-bottom: 20px;
}
</style>
