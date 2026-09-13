# 🧋 奶茶小程序管理后台

基于 Vue 3 + Element Plus + Vite 的现代化管理后台系统

## ✨ 项目特色

- 🎨 **现代化UI** - Element Plus组件库，美观易用
- 📊 **数据可视化** - ECharts图表，直观展示数据
- 🔐 **安全访问** - JWT认证与后台路由登录保护
- 📱 **响应式设计** - 适配不同屏幕尺寸
- ⚡ **快速开发** - Vite构建，热更新快速

## 🛠️ 技术栈

- **前端框架**: Vue 3.3+
- **UI组件库**: Element Plus 2.3+
- **状态管理**: Vuex 4.0+
- **路由管理**: Vue Router 4.2+
- **HTTP客户端**: Axios 1.5+
- **图表库**: ECharts 5.4+
- **构建工具**: Vite 4.4+

## 📁 项目结构

\`\`\`
milk-tea-admin/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── index.js      # Axios配置
│   │   ├── auth.js       # 认证接口
│   │   ├── product.js    # 商品接口
│   │   ├── order.js      # 订单接口
│   │   ├── user.js       # 用户接口
│   │   ├── coupon.js     # 优惠券接口
│   │   ├── banner.js     # 轮播图接口
│   │   └── dashboard.js  # 仪表盘接口
│   ├── assets/           # 资源文件
│   │   ├── images/       # 图片
│   │   └── styles/       # 样式
│   ├── components/       # 公共组件
│   │   ├── common/       # 通用组件
│   │   └── charts/       # 图表组件
│   ├── layout/           # 布局组件
│   │   ├── Index.vue     # 主布局
│   │   ├── Sidebar.vue   # 侧边栏
│   │   ├── Header.vue    # 顶部栏
│   │   └── Breadcrumb.vue # 面包屑
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── store/            # Vuex状态管理
│   │   ├── index.js
│   │   └── modules/
│   ├── utils/            # 工具函数
│   │   ├── request.js    # 请求封装
│   │   ├── auth.js       # 认证工具
│   │   └── validate.js   # 验证工具
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Dashboard.vue # 仪表盘
│   │   ├── product/      # 商品管理
│   │   │   ├── List.vue
│   │   │   ├── Category.vue
│   │   │   ├── Stock.vue
│   │   │   └── Recipe.vue
│   │   ├── order/        # 订单管理
│   │   │   ├── List.vue
│   │   │   ├── Process.vue
│   │   │   ├── Aftersale.vue
│   │   │   └── Print.vue
│   │   ├── user/         # 用户管理
│   │   │   ├── List.vue
│   │   │   ├── Member.vue
│   │   │   ├── Analysis.vue
│   │   │   └── Message.vue
│   │   ├── marketing/    # 营销管理
│   │   │   ├── Coupon.vue
│   │   │   ├── Banner.vue
│   │   │   └── Activity.vue
│   │   └── system/       # 系统管理
│   │       ├── Staff.vue
│   │       ├── Store.vue
│   │       └── Settings.vue
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── .env.development      # 开发环境配置
├── .env.production       # 生产环境配置
├── index.html
├── vite.config.js        # Vite配置
├── package.json
└── README.md
\`\`\`

## 🚀 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0

### 安装依赖

\`\`\`bash
cd milk-tea-admin
npm install
\`\`\`

### 开发运行

\`\`\`bash
npm run dev
\`\`\`

访问 http://localhost:3000

### 生产构建

\`\`\`bash
npm run build
\`\`\`

### 预览构建结果

\`\`\`bash
npm run preview
\`\`\`

## 📋 功能模块

### 1. 仪表盘模块 (15分)

#### ✅ 数据概览 (5分)
- [x] 今日订单数
- [x] 今日销售额
- [x] 待处理订单
- [x] 总用户数

#### ✅ 经营分析 (5分)
- [x] 销售趋势图表（日/周/月）
- [x] 商品销售排行
- [x] 订单状态分布
- [x] 用户增长趋势

#### ✅ 预警系统 (5分)
- [x] 库存预警
- [x] 异常订单提醒
- [x] 预警列表展示

### 2. 商品管理模块 (20分)

#### ✅ 商品管理 (5分)
- [x] 商品列表（分页、搜索、筛选）
- [x] 添加/编辑商品
- [x] 商品上下架
- [x] 批量操作（上架、下架、删除）

#### ✅ 分类管理 (5分)
- [x] 分类树形展示
- [x] 添加/编辑/删除分类
- [x] 多级分类支持
- [x] 分类排序

#### 🚧 库存管理 (5分)
- [ ] 库存列表
- [ ] 库存调整
- [ ] 库存预警设置
- [ ] 库存变动历史

#### 🚧 配方管理 (5分)
- [ ] 配方列表
- [ ] 原料管理
- [ ] 成本计算
- [ ] 利润分析

### 3. 订单管理模块 (20分)

#### ✅ 订单列表 (5分)
- [x] 订单查询（订单号、状态、日期）
- [x] 订单详情查看
- [x] 订单状态更新
- [x] 订单导出

#### 🚧 订单处理 (5分)
- [ ] 看板视图
- [ ] 新订单提醒
- [ ] 批量接单
- [ ] 实时刷新

#### 🚧 售后管理 (5分)
- [ ] 退款申请列表
- [ ] 退款审核
- [ ] 投诉处理

#### 🚧 打印管理 (5分)
- [ ] 打印模板设置
- [ ] 批量打印
- [ ] 打印机配置

### 4. 用户管理模块 (15分)

#### ✅ 用户列表 (5分)
- [x] 用户查询
- [x] 用户详情
- [x] 会员等级调整
- [x] 积分管理
- [x] 用户状态管理

#### 🚧 会员管理 (5分)
- [ ] 会员等级配置
- [ ] 会员权益设置
- [ ] 升级规则配置

#### 🚧 会员分析 (5分)
- [ ] 会员分布统计
- [ ] 消费趋势分析
- [ ] 会员价值分层
- [ ] 留存率分析

### 5. 营销管理模块 (15分)

#### 🚧 优惠券管理 (5分)
- [ ] 优惠券列表
- [ ] 创建/编辑优惠券
- [ ] 优惠券使用记录
- [ ] 效果分析

#### ✅ 轮播图管理 (5分)
- [x] 轮播图列表
- [x] 添加/编辑轮播图
- [x] 图片上传
- [x] 排序管理

#### 🚧 活动管理 (5分)
- [ ] 活动列表
- [ ] 创建/编辑活动
- [ ] 活动规则配置
- [ ] 效果统计

### 6. 系统管理模块 (15分)

#### 🚧 员工管理 (5分)
- [ ] 员工列表
- [ ] 添加/编辑员工
- [ ] 角色权限配置
- [ ] 操作日志

#### 🚧 门店管理 (5分)
- [ ] 门店列表
- [ ] 门店信息管理
- [ ] 营业时间配置
- [ ] 门店业绩

#### 🚧 系统设置 (5分)
- [ ] 基本信息配置
- [ ] 支付配置
- [ ] 订单配置
- [ ] 数据备份

## 🔑 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

## 🎨 主题定制

### 颜色变量

\`\`\`scss
// 主色调
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;
\`\`\`

### Element Plus主题定制

在 \`vite.config.js\` 中配置：

\`\`\`javascript
css: {
  preprocessorOptions: {
    scss: {
      additionalData: \`@use "@/assets/styles/element-variables.scss" as *;\`
    }
  }
}
\`\`\`

## 📝 开发规范

### 代码规范

- 当前项目使用 JavaScript + Vue 3 Composition API；提交前执行生产构建验证
- 组件命名使用 PascalCase
- 方法命名使用 camelCase
- 常量命名使用 UPPER_SNAKE_CASE

### Git提交规范

\`\`\`
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具相关
\`\`\`

### 组件开发规范

1. 使用 Composition API
2. 优先使用 \`<script setup>\` 语法
3. Props 使用明确的 JavaScript 默认值和校验
4. 事件命名使用 kebab-case
5. 样式使用 scoped

## 🔧 常用命令

\`\`\`bash
# 安装依赖
npm install

# 开发运行
npm run dev

# 生产构建
npm run build

# 预览构建
npm run preview

\`\`\`

## 📚 相关文档

- [Vue 3 文档](https://v3.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [ECharts 文档](https://echarts.apache.org/)
- [Vite 文档](https://vitejs.dev/)
- [系统设计文档](../docs/系统设计文档.md)
- [API文档](../API文档.md)
- [实施清单](../docs/管理后台完整实施清单.md)

## 🐛 常见问题

### 1. 跨域问题

在 \`vite.config.js\` 中配置代理：

\`\`\`javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
\`\`\`

### 2. 图片上传失败

检查后端上传接口是否正常，确认文件大小限制。

### 3. Token过期

Token过期后会自动跳转到登录页，重新登录即可。

### 4. 图表不显示

确保 ECharts 正确引入，检查数据格式是否正确。

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (\`git checkout -b feature/AmazingFeature\`)
3. 提交更改 (\`git commit -m 'Add some AmazingFeature'\`)
4. 推送到分支 (\`git push origin feature/AmazingFeature\`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证

## 👥 开发团队

- 项目负责人: MilkTea Team
- 邮箱: milktea@example.com

---

**🧋 享受编码，享受奶茶！**
