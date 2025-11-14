<template>
  <div class="print-management">
    <!-- 打印设备管理 -->
    <el-card class="device-card">
      <template #header>
        <div class="card-header">
          <span>打印设备管理</span>
          <el-button type="primary" @click="showDeviceDialog()">添加设备</el-button>
        </div>
      </template>
      
      <el-table :data="devices" v-loading="deviceLoading">
        <el-table-column prop="name" label="设备名称" width="150" />
        <el-table-column prop="type" label="设备类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getDeviceTypeTag(row.type)">
              {{ getDeviceTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="150" />
        <el-table-column prop="port" label="端口" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="testDevice(row)">测试</el-button>
            <el-button type="primary" size="small" @click="showDeviceDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteDevice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 小票模板管理 -->
    <el-card class="template-card">
      <template #header>
        <div class="card-header">
          <span>小票模板管理</span>
          <el-button type="primary" @click="showTemplateDialog()">新建模板</el-button>
        </div>
      </template>
      
      <el-table :data="templates" v-loading="templateLoading">
        <el-table-column prop="name" label="模板名称" width="150" />
        <el-table-column prop="type" label="模板类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTemplateTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="width" label="纸张宽度" width="100">
          <template #default="{ row }">{{ row.width }}mm</template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认模板" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isDefault ? 'success' : 'info'">
              {{ row.isDefault ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="previewTemplate(row)">预览</el-button>
            <el-button type="primary" size="small" @click="showTemplateDialog(row)">编辑</el-button>
            <el-button type="success" size="small" @click="setDefaultTemplate(row)" v-if="!row.isDefault">
              设为默认
            </el-button>
            <el-button type="danger" size="small" @click="deleteTemplate(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 打印记录 -->
    <el-card class="record-card">
      <template #header>
        <div class="card-header">
          <span>打印记录</span>
          <el-button @click="loadPrintRecords">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="printRecords" v-loading="recordLoading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="deviceName" label="打印设备" width="150" />
        <el-table-column prop="templateName" label="使用模板" width="150" />
        <el-table-column prop="status" label="打印状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPrintStatusTag(row.status)">
              {{ getPrintStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="printTime" label="打印时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.printTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="reprintOrder(row)" v-if="row.status === 2">
              重新打印
            </el-button>
            <el-button size="small" @click="viewPrintContent(row)">查看内容</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 设备配置对话框 -->
    <el-dialog v-model="deviceDialogVisible" :title="deviceForm.id ? '编辑设备' : '添加设备'" width="600px">
      <el-form :model="deviceForm" :rules="deviceRules" ref="deviceFormRef" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="deviceForm.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="deviceForm.type" placeholder="请选择设备类型">
            <el-option label="热敏打印机" value="thermal" />
            <el-option label="针式打印机" value="dot" />
            <el-option label="激光打印机" value="laser" />
          </el-select>
        </el-form-item>
        <el-form-item label="连接方式" prop="connection">
          <el-radio-group v-model="deviceForm.connection">
            <el-radio label="network">网络连接</el-radio>
            <el-radio label="usb">USB连接</el-radio>
            <el-radio label="bluetooth">蓝牙连接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="IP地址" prop="ip" v-if="deviceForm.connection === 'network'">
          <el-input v-model="deviceForm.ip" placeholder="请输入IP地址" />
        </el-form-item>
        <el-form-item label="端口" prop="port" v-if="deviceForm.connection === 'network'">
          <el-input-number v-model="deviceForm.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="设备路径" prop="path" v-if="deviceForm.connection === 'usb'">
          <el-input v-model="deviceForm.path" placeholder="如: /dev/usb/lp0" />
        </el-form-item>
        <el-form-item label="MAC地址" prop="mac" v-if="deviceForm.connection === 'bluetooth'">
          <el-input v-model="deviceForm.mac" placeholder="请输入蓝牙MAC地址" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="deviceForm.description" type="textarea" placeholder="请输入设备描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deviceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDevice">保存</el-button>
      </template>
    </el-dialog>

    <!-- 模板编辑对话框 -->
    <el-dialog v-model="templateDialogVisible" :title="templateForm.id ? '编辑模板' : '新建模板'" width="800px">
      <el-form :model="templateForm" :rules="templateRules" ref="templateFormRef" label-width="100px">
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板类型" prop="type">
          <el-select v-model="templateForm.type" placeholder="请选择模板类型">
            <el-option label="订单小票" value="order" />
            <el-option label="厨房单" value="kitchen" />
            <el-option label="配送单" value="delivery" />
          </el-select>
        </el-form-item>
        <el-form-item label="纸张宽度" prop="width">
          <el-select v-model="templateForm.width" placeholder="请选择纸张宽度">
            <el-option label="58mm" :value="58" />
            <el-option label="80mm" :value="80" />
            <el-option label="A4" :value="210" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板内容" prop="content">
          <el-input
            v-model="templateForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入模板内容，支持变量：{orderNo}, {customerName}, {totalAmount}, {items}, {createTime}"
          />
        </el-form-item>
        <el-form-item label="页眉">
          <el-input v-model="templateForm.header" placeholder="页眉内容" />
        </el-form-item>
        <el-form-item label="页脚">
          <el-input v-model="templateForm.footer" placeholder="页脚内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 模板预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="模板预览" width="600px">
      <div class="template-preview">
        <div class="preview-content" v-html="previewContent"></div>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="testPrint">测试打印</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据定义
const deviceLoading = ref(false)
const templateLoading = ref(false)
const recordLoading = ref(false)

const devices = ref([])
const templates = ref([])
const printRecords = ref([])

// 对话框状态
const deviceDialogVisible = ref(false)
const templateDialogVisible = ref(false)
const previewDialogVisible = ref(false)

const previewContent = ref('')

// 表单引用
const deviceFormRef = ref()
const templateFormRef = ref()

// 表单数据
const deviceForm = reactive({
  id: null,
  name: '',
  type: 'thermal',
  connection: 'network',
  ip: '',
  port: 9100,
  path: '',
  mac: '',
  description: ''
})

const templateForm = reactive({
  id: null,
  name: '',
  type: 'order',
  width: 80,
  content: '',
  header: '',
  footer: ''
})

// 表单验证规则
const deviceRules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  connection: [{ required: true, message: '请选择连接方式', trigger: 'change' }],
  ip: [
    { required: true, message: '请输入IP地址', trigger: 'blur' },
    { pattern: /^(\d{1,3}\.){3}\d{1,3}$/, message: 'IP地址格式不正确', trigger: 'blur' }
  ]
}

const templateRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
  width: [{ required: true, message: '请选择纸张宽度', trigger: 'change' }],
  content: [{ required: true, message: '请输入模板内容', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadDevices(),
    loadTemplates(),
    loadPrintRecords()
  ])
}

// 加载打印设备
const loadDevices = async () => {
  deviceLoading.value = true
  try {
    // 模拟数据
    devices.value = [
      {
        id: 1,
        name: '前台打印机',
        type: 'thermal',
        ip: '192.168.1.100',
        port: 9100,
        status: 1,
        description: '前台收银打印机'
      },
      {
        id: 2,
        name: '厨房打印机',
        type: 'thermal',
        ip: '192.168.1.101',
        port: 9100,
        status: 1,
        description: '厨房订单打印机'
      }
    ]
  } catch (error) {
    console.error('加载打印设备失败:', error)
  } finally {
    deviceLoading.value = false
  }
}

// 加载打印模板
const loadTemplates = async () => {
  templateLoading.value = true
  try {
    // 模拟数据
    templates.value = [
      {
        id: 1,
        name: '标准订单小票',
        type: 'order',
        width: 80,
        isDefault: true,
        content: '订单号: {orderNo}\n客户: {customerName}\n金额: {totalAmount}\n时间: {createTime}',
        createTime: new Date().toISOString()
      },
      {
        id: 2,
        name: '厨房制作单',
        type: 'kitchen',
        width: 80,
        isDefault: false,
        content: '制作单\n订单号: {orderNo}\n商品: {items}\n时间: {createTime}',
        createTime: new Date().toISOString()
      }
    ]
  } catch (error) {
    console.error('加载打印模板失败:', error)
  } finally {
    templateLoading.value = false
  }
}

// 加载打印记录
const loadPrintRecords = async () => {
  recordLoading.value = true
  try {
    // 模拟数据
    printRecords.value = [
      {
        id: 1,
        orderNo: 'MT202411130001',
        deviceName: '前台打印机',
        templateName: '标准订单小票',
        status: 1,
        printTime: new Date().toISOString(),
        errorMessage: ''
      },
      {
        id: 2,
        orderNo: 'MT202411130002',
        deviceName: '厨房打印机',
        templateName: '厨房制作单',
        status: 2,
        printTime: new Date().toISOString(),
        errorMessage: '设备离线'
      }
    ]
  } catch (error) {
    console.error('加载打印记录失败:', error)
  } finally {
    recordLoading.value = false
  }
}

// 显示设备对话框
const showDeviceDialog = (device = null) => {
  if (device) {
    Object.assign(deviceForm, device)
  } else {
    Object.assign(deviceForm, {
      id: null,
      name: '',
      type: 'thermal',
      connection: 'network',
      ip: '',
      port: 9100,
      path: '',
      mac: '',
      description: ''
    })
  }
  deviceDialogVisible.value = true
}

// 保存设备
const saveDevice = async () => {
  try {
    await deviceFormRef.value.validate()
    
    if (deviceForm.id) {
      ElMessage.success('设备更新成功')
    } else {
      ElMessage.success('设备添加成功')
    }
    
    deviceDialogVisible.value = false
    loadDevices()
  } catch (error) {
    console.error('保存设备失败:', error)
  }
}

// 测试设备
const testDevice = async (device) => {
  try {
    ElMessage.info('正在测试设备连接...')
    // 模拟测试
    setTimeout(() => {
      ElMessage.success('设备连接正常')
    }, 1000)
  } catch (error) {
    console.error('测试设备失败:', error)
    ElMessage.error('设备连接失败')
  }
}

// 删除设备
const deleteDevice = async (device) => {
  try {
    await ElMessageBox.confirm('确定要删除这个设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('设备删除成功')
    loadDevices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除设备失败:', error)
    }
  }
}

// 显示模板对话框
const showTemplateDialog = (template = null) => {
  if (template) {
    Object.assign(templateForm, template)
  } else {
    Object.assign(templateForm, {
      id: null,
      name: '',
      type: 'order',
      width: 80,
      content: '',
      header: '',
      footer: ''
    })
  }
  templateDialogVisible.value = true
}

// 保存模板
const saveTemplate = async () => {
  try {
    await templateFormRef.value.validate()
    
    if (templateForm.id) {
      ElMessage.success('模板更新成功')
    } else {
      ElMessage.success('模板创建成功')
    }
    
    templateDialogVisible.value = false
    loadTemplates()
  } catch (error) {
    console.error('保存模板失败:', error)
  }
}

// 预览模板
const previewTemplate = (template) => {
  // 模拟数据替换
  let content = template.content
  content = content.replace('{orderNo}', 'MT202411130001')
  content = content.replace('{customerName}', '张三')
  content = content.replace('{totalAmount}', '¥25.80')
  content = content.replace('{items}', '珍珠奶茶 x1\n芋泥奶茶 x1')
  content = content.replace('{createTime}', new Date().toLocaleString())
  
  previewContent.value = content.replace(/\n/g, '<br>')
  previewDialogVisible.value = true
}

// 设为默认模板
const setDefaultTemplate = async (template) => {
  try {
    ElMessage.success('已设为默认模板')
    loadTemplates()
  } catch (error) {
    console.error('设置默认模板失败:', error)
  }
}

// 删除模板
const deleteTemplate = async (template) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('模板删除成功')
    loadTemplates()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除模板失败:', error)
    }
  }
}

// 重新打印
const reprintOrder = async (record) => {
  try {
    ElMessage.info('正在重新打印...')
    setTimeout(() => {
      ElMessage.success('打印成功')
      loadPrintRecords()
    }, 1000)
  } catch (error) {
    console.error('重新打印失败:', error)
  }
}

// 查看打印内容
const viewPrintContent = (record) => {
  ElMessage.info('查看打印内容功能开发中...')
}

// 测试打印
const testPrint = () => {
  ElMessage.info('正在测试打印...')
  setTimeout(() => {
    ElMessage.success('测试打印成功')
  }, 1000)
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const getDeviceTypeName = (type) => {
  const typeMap = {
    thermal: '热敏打印机',
    dot: '针式打印机',
    laser: '激光打印机'
  }
  return typeMap[type] || '未知'
}

const getDeviceTypeTag = (type) => {
  const tagMap = {
    thermal: 'success',
    dot: 'warning',
    laser: 'primary'
  }
  return tagMap[type] || 'info'
}

const getTemplateTypeName = (type) => {
  const typeMap = {
    order: '订单小票',
    kitchen: '厨房单',
    delivery: '配送单'
  }
  return typeMap[type] || '未知'
}

const getPrintStatusName = (status) => {
  const statusMap = {
    1: '成功',
    2: '失败',
    3: '处理中'
  }
  return statusMap[status] || '未知'
}

const getPrintStatusTag = (status) => {
  const tagMap = {
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return tagMap[status] || 'info'
}
</script>

<style scoped>
.print-management {
  padding: 20px;
}

.device-card,
.template-card,
.record-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-preview {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 20px;
  background-color: #f5f7fa;
}

.preview-content {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  background-color: white;
  padding: 15px;
  border-radius: 4px;
  min-height: 200px;
}
</style>
