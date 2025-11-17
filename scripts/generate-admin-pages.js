/**
 * 管理后台页面生成脚本
 * 用于快速生成所有缺失的页面模板
 * 
 * 使用方法：node scripts/generate-admin-pages.js
 */

const fs = require('fs')
const path = require('path')

// 页面模板配置
const pageTemplates = {
  'product/Stock.vue': {
    title: '库存管理',
    description: '管理商品库存，设置预警阈值，查看库存变动历史'
  },
  'product/Recipe.vue': {
    title: '配方管理',
    description: '管理商品配方，计算成本，分析利润'
  },
  'product/Dashboard.vue': {
    title: '商品看板',
    description: '商品数据统计和分析'
  },
  'order/Process.vue': {
    title: '订单处理',
    description: '实时订单处理看板'
  },
  'order/Aftersale.vue': {
    title: '售后管理',
    description: '处理退款和投诉'
  },
  'order/Print.vue': {
    title: '打印管理',
    description: '订单打印和模板管理'
  },
  'user/Member.vue': {
    title: '会员管理',
    description: '会员等级和权益配置'
  },
  'user/Analysis.vue': {
    title: '会员分析',
    description: '会员数据分析和统计'
  },
  'user/Message.vue': {
    title: '消息推送',
    description: '向用户发送消息通知'
  },
  'marketing/Coupon.vue': {
    title: '优惠券管理',
    description: '创建和管理优惠券'
  },
  'marketing/Activity.vue': {
    title: '活动管理',
    description: '创建和管理营销活动'
  },
  'system/Staff.vue': {
    title: '员工管理',
    description: '管理员工账号和权限'
  },
  'system/Store.vue': {
    title: '门店管理',
    description: '管理门店信息和营业时间'
  },
  'system/Settings.vue': {
    title: '系统设置',
    description: '系统参数配置'
  }
}

// Vue页面模板
const generateVueTemplate = (title, description) => `<template>
  <div class="${title.toLowerCase().replace(/\s+/g, '-')}">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>${title}</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
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
        style="margin-top: 20px"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <!-- 添加更多表单项 -->
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
// import api from '@/api/xxx'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: ''
})

const form = reactive({
  id: null,
  name: ''
})

const rules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ]
}

onMounted(() => {
  handleQuery()
})

const handleQuery = async () => {
  loading.value = true
  try {
    // const res = await api.getList(queryForm)
    // tableData.value = res.data.records
    // total.value = res.data.total
    ElMessage.info('${description} - 功能开发中')
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(queryForm, {
    page: 1,
    size: 10,
    keyword: ''
  })
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    // if (form.id) {
    //   await api.update(form.id, form)
    //   ElMessage.success('更新成功')
    // } else {
    //   await api.create(form)
    //   ElMessage.success('创建成功')
    // }

    ElMessage.info('功能开发中')
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除？', '提示', {
      type: 'warning'
    })

    // await api.delete(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: ''
  })
  formRef.value?.clearValidate()
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
`

// 创建目录
const ensureDir = (dirPath) => {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true })
    console.log(`✅ 创建目录: ${dirPath}`)
  }
}

// 生成页面文件
const generatePage = (pagePath, config) => {
  const fullPath = path.join(__dirname, '../milk-tea-admin/src/views', pagePath)
  const dir = path.dirname(fullPath)
  
  ensureDir(dir)
  
  if (fs.existsSync(fullPath)) {
    console.log(`⚠️  文件已存在，跳过: ${pagePath}`)
    return
  }
  
  const content = generateVueTemplate(config.title, config.description)
  fs.writeFileSync(fullPath, content, 'utf8')
  console.log(`✅ 生成页面: ${pagePath}`)
}

// 主函数
const main = () => {
  console.log('🚀 开始生成管理后台页面...\n')
  
  let successCount = 0
  let skipCount = 0
  
  Object.entries(pageTemplates).forEach(([pagePath, config]) => {
    try {
      generatePage(pagePath, config)
      successCount++
    } catch (error) {
      console.error(`❌ 生成失败: ${pagePath}`, error.message)
      skipCount++
    }
  })
  
  console.log(`\n📊 生成完成！`)
  console.log(`   成功: ${successCount} 个`)
  console.log(`   跳过: ${skipCount} 个`)
  console.log(`\n💡 提示: 请根据实际需求修改生成的页面模板`)
}

// 执行
main()
