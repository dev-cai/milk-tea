<template>
  <div class="staff-management">
    <!-- 员工统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon size="24"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ staffStats.total || 0 }}</div>
              <div class="stat-label">总员工数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon size="24"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ staffStats.active || 0 }}</div>
              <div class="stat-label">在职员工</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon size="24"><Crown /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ staffStats.managers || 0 }}</div>
              <div class="stat-label">管理员</div>
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
              <div class="stat-value">{{ staffStats.onDuty || 0 }}</div>
              <div class="stat-label">当前在岗</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 员工管理 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>员工管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showStaffDialog()">添加员工</el-button>
            <el-button @click="exportStaff">导出员工</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="queryForm" :inline="true" class="search-form">
        <el-form-item label="员工姓名">
          <el-input v-model="queryForm.keyword" placeholder="请输入员工姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.role" placeholder="请选择角色" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="超级管理员" value="super_admin" />
            <el-option label="店长" value="manager" />
            <el-option label="收银员" value="cashier" />
            <el-option label="制作员" value="maker" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 员工列表 -->
      <el-table :data="staffList" v-loading="loading">
        <el-table-column label="员工信息" width="200">
          <template #default="{ row }">
            <div class="staff-info">
              <el-avatar :src="row.avatar" :size="40">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <div class="staff-details">
                <div class="staff-name">{{ row.name }}</div>
                <div class="staff-phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="employeeId" label="工号" width="120" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.role)">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="hireDate" label="入职时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.hireDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="salary" label="薪资" width="100">
          <template #default="{ row }">
            ¥{{ row.salary }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" width="160">
          <template #default="{ row }">
            {{ row.lastLogin ? formatTime(row.lastLogin) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewStaffDetail(row)">详情</el-button>
            <el-button type="primary" size="small" @click="showStaffDialog(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="showPermissionDialog(row)">权限</el-button>
            <el-dropdown trigger="click">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="resetPassword(row)">
                    <el-icon><Refresh /></el-icon>
                    重置密码
                  </el-dropdown-item>
                  <el-dropdown-item @click="toggleStatus(row)">
                    <el-icon><Switch /></el-icon>
                    {{ row.status === 1 ? '离职' : '复职' }}
                  </el-dropdown-item>
                  <el-dropdown-item @click="viewOperationLog(row)">
                    <el-icon><Document /></el-icon>
                    操作日志
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 员工编辑对话框 -->
    <el-dialog v-model="staffDialogVisible" :title="staffForm.id ? '编辑员工' : '添加员工'" width="600px">
      <el-form :model="staffForm" :rules="staffRules" ref="staffFormRef" label-width="100px">
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="staffForm.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="staffForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="工号" prop="employeeId">
          <el-input v-model="staffForm.employeeId" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="staffForm.role" placeholder="请选择角色">
            <el-option label="超级管理员" value="super_admin" />
            <el-option label="店长" value="manager" />
            <el-option label="收银员" value="cashier" />
            <el-option label="制作员" value="maker" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="staffForm.department" placeholder="请选择部门">
            <el-option label="管理部" value="management" />
            <el-option label="前台部" value="front" />
            <el-option label="制作部" value="kitchen" />
            <el-option label="配送部" value="delivery" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职时间" prop="hireDate">
          <el-date-picker
            v-model="staffForm.hireDate"
            type="date"
            placeholder="请选择入职时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="薪资" prop="salary">
          <el-input-number v-model="staffForm.salary" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="staffForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="staffForm.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="staffDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveStaff">保存</el-button>
      </template>
    </el-dialog>

    <!-- 权限管理对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="权限管理" width="500px">
      <div v-if="currentStaff">
        <h4>{{ currentStaff.name }} - {{ getRoleName(currentStaff.role) }}</h4>
        <el-tree
          :data="permissionTree"
          :default-checked-keys="staffPermissions"
          node-key="id"
          show-checkbox
          ref="permissionTreeRef"
        />
      </div>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存权限</el-button>
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
const staffList = ref([])
const total = ref(0)

// 统计数据
const staffStats = reactive({
  total: 0,
  active: 0,
  managers: 0,
  onDuty: 0
})

// 查询表单
const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  role: '',
  status: ''
})

// 对话框状态
const staffDialogVisible = ref(false)
const permissionDialogVisible = ref(false)

const currentStaff = ref(null)
const staffPermissions = ref([])

// 表单引用和数据
const staffFormRef = ref()
const permissionTreeRef = ref()

const staffForm = reactive({
  id: null,
  name: '',
  phone: '',
  employeeId: '',
  role: 'cashier',
  department: 'front',
  hireDate: '',
  salary: 0,
  email: '',
  address: ''
})

// 权限树数据
const permissionTree = ref([
  {
    id: 'dashboard',
    label: '仪表盘',
    children: [
      { id: 'dashboard.view', label: '查看数据' },
      { id: 'dashboard.export', label: '导出报表' }
    ]
  },
  {
    id: 'product',
    label: '商品管理',
    children: [
      { id: 'product.view', label: '查看商品' },
      { id: 'product.create', label: '添加商品' },
      { id: 'product.edit', label: '编辑商品' },
      { id: 'product.delete', label: '删除商品' }
    ]
  },
  {
    id: 'order',
    label: '订单管理',
    children: [
      { id: 'order.view', label: '查看订单' },
      { id: 'order.process', label: '处理订单' },
      { id: 'order.refund', label: '退款处理' }
    ]
  },
  {
    id: 'user',
    label: '用户管理',
    children: [
      { id: 'user.view', label: '查看用户' },
      { id: 'user.edit', label: '编辑用户' }
    ]
  },
  {
    id: 'system',
    label: '系统管理',
    children: [
      { id: 'system.staff', label: '员工管理' },
      { id: 'system.config', label: '系统配置' }
    ]
  }
])

// 表单验证规则
const staffRules = {
  name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  employeeId: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  hireDate: [{ required: true, message: '请选择入职时间', trigger: 'change' }],
  salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  await Promise.all([
    loadStaffStats(),
    handleQuery()
  ])
}

// 加载员工统计
const loadStaffStats = async () => {
  try {
    // 模拟数据
    Object.assign(staffStats, {
      total: 25,
      active: 22,
      managers: 3,
      onDuty: 18
    })
  } catch (error) {
    console.error('加载员工统计失败:', error)
  }
}

// 查询员工
const handleQuery = async () => {
  loading.value = true
  try {
    // 模拟数据
    staffList.value = [
      {
        id: 1,
        name: '张三',
        phone: '13800138001',
        employeeId: 'EMP001',
        role: 'manager',
        department: 'management',
        hireDate: '2023-01-15',
        salary: 8000,
        email: 'zhangsan@example.com',
        address: '北京市朝阳区',
        status: 1,
        lastLogin: new Date().toISOString()
      },
      {
        id: 2,
        name: '李四',
        phone: '13800138002',
        employeeId: 'EMP002',
        role: 'cashier',
        department: 'front',
        hireDate: '2023-03-20',
        salary: 5000,
        email: 'lisi@example.com',
        address: '北京市海淀区',
        status: 1,
        lastLogin: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
      }
    ]
    total.value = 25
  } catch (error) {
    console.error('查询失败:', error)
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
    role: '',
    status: ''
  })
  handleQuery()
}

// 显示员工对话框
const showStaffDialog = (staff = null) => {
  if (staff) {
    Object.assign(staffForm, staff)
  } else {
    Object.assign(staffForm, {
      id: null,
      name: '',
      phone: '',
      employeeId: '',
      role: 'cashier',
      department: 'front',
      hireDate: '',
      salary: 0,
      email: '',
      address: ''
    })
  }
  staffDialogVisible.value = true
}

// 保存员工
const saveStaff = async () => {
  try {
    await staffFormRef.value.validate()
    
    if (staffForm.id) {
      ElMessage.success('员工信息更新成功')
    } else {
      ElMessage.success('员工添加成功')
    }
    
    staffDialogVisible.value = false
    handleQuery()
    loadStaffStats()
  } catch (error) {
    console.error('保存员工失败:', error)
  }
}

// 查看员工详情
const viewStaffDetail = (staff) => {
  ElMessage.info(`查看员工 ${staff.name} 的详细信息`)
}

// 显示权限对话框
const showPermissionDialog = (staff) => {
  currentStaff.value = staff
  // 模拟获取员工权限
  staffPermissions.value = ['dashboard.view', 'product.view', 'order.view']
  permissionDialogVisible.value = true
}

// 保存权限
const savePermissions = () => {
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  ElMessage.success('权限保存成功')
  permissionDialogVisible.value = false
}

// 重置密码
const resetPassword = async (staff) => {
  try {
    await ElMessageBox.confirm(`确定要重置员工 ${staff.name} 的密码吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('密码重置成功，新密码已发送到员工手机')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
    }
  }
}

// 切换员工状态
const toggleStatus = async (staff) => {
  const action = staff.status === 1 ? '离职' : '复职'
  try {
    await ElMessageBox.confirm(`确定要${action}员工 ${staff.name} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success(`${action}操作成功`)
    handleQuery()
    loadStaffStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态切换失败:', error)
    }
  }
}

// 查看操作日志
const viewOperationLog = (staff) => {
  ElMessage.info(`查看员工 ${staff.name} 的操作日志`)
}

// 导出员工
const exportStaff = () => {
  ElMessage.success('员工数据导出成功')
}

// 工具函数
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString()
}

const getRoleName = (role) => {
  const roleMap = {
    super_admin: '超级管理员',
    manager: '店长',
    cashier: '收银员',
    maker: '制作员'
  }
  return roleMap[role] || '未知'
}

const getRoleTag = (role) => {
  const tagMap = {
    super_admin: 'danger',
    manager: 'warning',
    cashier: 'primary',
    maker: 'success'
  }
  return tagMap[role] || 'info'
}
</script>

<style scoped>
.staff-management {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.staff-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.staff-details {
  flex: 1;
}

.staff-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.staff-phone {
  font-size: 12px;
  color: #909399;
}
</style>
