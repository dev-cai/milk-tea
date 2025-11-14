<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary">添加分类</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small">编辑</el-button>
            <el-button type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const tableData = ref([])

onMounted(() => {
  // 模拟数据
  tableData.value = [
    { id: 1, name: '茶饮', parentId: 0, icon: 'Coffee', sort: 1, status: 1, children: [
      { id: 4, name: '奶茶', parentId: 1, icon: null, sort: 1, status: 1 },
      { id: 5, name: '果茶', parentId: 1, icon: null, sort: 2, status: 1 },
      { id: 6, name: '奶盖茶', parentId: 1, icon: null, sort: 3, status: 1 }
    ]},
    { id: 2, name: '咖啡', parentId: 0, icon: 'Coffee', sort: 2, status: 1 },
    { id: 3, name: '小食', parentId: 0, icon: 'Food', sort: 3, status: 1 }
  ]
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
