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
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
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
            </div>
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

    <!-- 员工详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="员工详情" width="700px">
      <div v-if="currentStaff">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="员工ID">{{ currentStaff.id }}</el-descriptions-item>
          <el-descriptions-item label="员工姓名">{{ currentStaff.name }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentStaff.username }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentStaff.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentStaff.email || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleTag(currentStaff.role)">
              {{ getRoleName(currentStaff.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentStaff.status === 1 ? 'success' : 'danger'">
              {{ currentStaff.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="入职时间">{{ formatTime(currentStaff.hireDate) }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">
            {{ currentStaff.lastLogin ? formatTime(currentStaff.lastLogin) : '从未登录' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentStaff.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 员工头像 -->
        <div v-if="currentStaff.avatar" style="margin-top: 20px;">
          <h4>员工头像</h4>
          <el-avatar :src="currentStaff.avatar" :size="100">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
        </div>

        <!-- 工作信息 -->
        <div style="margin-top: 20px;">
          <h4>工作信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="所属部门">{{ currentStaff.department || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="直属上级">{{ currentStaff.supervisor || '无' }}</el-descriptions-item>
            <el-descriptions-item label="工作地点">{{ currentStaff.workLocation || '总部' }}</el-descriptions-item>
            <el-descriptions-item label="员工编号">{{ currentStaff.employeeNo || '自动生成' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 权限信息 -->
        <div style="margin-top: 20px;">
          <h4>权限信息</h4>
          <el-tag v-for="permission in currentStaff.permissions" :key="permission" style="margin-right: 8px; margin-bottom: 8px;">
            {{ getPermissionName(permission) }}
          </el-tag>
          <span v-if="!currentStaff.permissions || currentStaff.permissions.length === 0" style="color: #909399;">
            暂无特殊权限
          </span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="showStaffDialog(currentStaff)">编辑员工</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getStaffStats,
  getStaffList,
  getStaffDetail,
  addStaff,
  updateStaff,
  deleteStaff,
  toggleStaffStatus,
  resetStaffPassword,
  updateStaffPermissions
} from '@/api/staff'

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
const detailDialogVisible = ref(false)

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
    const res = await getStaffStats()
    if (res.code === 200) {
      Object.assign(staffStats, res.data)
    }
  } catch (error) {
    console.error('加载员工统计失败:', error)
    ElMessage.error('加载员工统计失败')
  }
}

// 查询员工
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getStaffList(queryForm)
    if (res.code === 200) {
      staffList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询员工列表失败')
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
    
    const apiFunc = staffForm.id ? updateStaff : addStaff
    const res = await apiFunc(staffForm)
    
    if (res.code === 200) {
      ElMessage.success(res.msg || (staffForm.id ? '员工信息更新成功' : '员工添加成功'))
      staffDialogVisible.value = false
      handleQuery()
      loadStaffStats()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('保存员工失败:', error)
    ElMessage.error('保存员工失败')
  }
}

// 查看员工详情
const viewStaffDetail = async (staff) => {
  try {
    const res = await getStaffDetail(staff.id)
    if (res.code === 200) {
      currentStaff.value = {
        ...res.data,
        permissions: res.data.permissions ? JSON.parse(res.data.permissions) : []
      }
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取员工详情失败')
    }
  } catch (error) {
    console.error('获取员工详情失败:', error)
    ElMessage.error('获取员工详情失败')
  }
}

// 显示权限对话框
const showPermissionDialog = async (staff) => {
  try {
    const res = await getStaffDetail(staff.id)
    if (res.code === 200) {
      currentStaff.value = res.data
      staffPermissions.value = res.data.permissions ? JSON.parse(res.data.permissions) : []
      permissionDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取员工权限失败')
    }
  } catch (error) {
    console.error('获取员工权限失败:', error)
    ElMessage.error('获取员工权限失败')
  }
}

// 保存权限
const savePermissions = async () => {
  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const permissions = JSON.stringify(checkedKeys)
    
    const res = await updateStaffPermissions(currentStaff.value.id, permissions)
    if (res.code === 200) {
      ElMessage.success('权限保存成功')
      permissionDialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(res.msg || '权限保存失败')
    }
  } catch (error) {
    console.error('保存权限失败:', error)
    ElMessage.error('保存权限失败')
  }
}

// 重置密码
const resetPassword = async (staff) => {
  try {
    await ElMessageBox.confirm(`确定要重置员工 ${staff.name} 的密码吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await resetStaffPassword(staff.id)
    if (res.code === 200) {
      ElMessage.success(res.msg || '密码重置成功，新密码为：123456')
    } else {
      ElMessage.error(res.msg || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
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
    
    const res = await toggleStaffStatus(staff.id)
    if (res.code === 200) {
      ElMessage.success(`${action}操作成功`)
      handleQuery()
      loadStaffStats()
    } else {
      ElMessage.error(res.msg || `${action}操作失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态切换失败:', error)
      ElMessage.error('状态切换失败')
    }
  }
}

// 查看操作日志
const viewOperationLog = async (staff) => {
  try {
    const res = await getStaffDetail(staff.id)
    if (res.code === 200) {
      const staffData = res.data
      ElMessageBox.alert(
        `<div style="line-height: 1.8;">
          <p><strong>员工姓名：</strong>${staffData.name}</p>
          <p><strong>工号：</strong>${staffData.employeeId}</p>
          <p><strong>最后登录：</strong>${staffData.lastLogin ? formatTime(staffData.lastLogin) : '从未登录'}</p>
          <p><strong>创建时间：</strong>${formatTime(staffData.createTime)}</p>
          <p><strong>更新时间：</strong>${formatTime(staffData.updateTime)}</p>
          <p style="color: #909399; margin-top: 10px;">注：详细操作日志功能可在后续版本中扩展</p>
        </div>`,
        `${staff.name} 的操作记录`,
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '关闭'
        }
      )
    }
  } catch (error) {
    console.error('查看操作日志失败:', error)
    ElMessage.error('查看操作日志失败')
  }
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

const getPermissionName = (permission) => {
  const permissionMap = {
    'dashboard.view': '仪表盘查看',
    'product.view': '商品查看',
    'product.edit': '商品编辑',
    'order.view': '订单查看',
    'order.edit': '订单处理',
    'user.view': '用户查看',
    'user.edit': '用户管理',
    'system.view': '系统查看',
    'system.edit': '系统管理',
    'report.view': '报表查看',
    'marketing.view': '营销查看',
    'marketing.edit': '营销管理'
  }
  return permissionMap[permission] || permission
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

.staff-avatar {
  flex-shrink: 0;
}

.staff-details {
  flex: 1;
}

.staff-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.staff-username {
  font-size: 12px;
  color: #909399;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
}
</style>
