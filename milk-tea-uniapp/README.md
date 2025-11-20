# 🧋 奶茶小程序 - uni-app版本

基于uni-app + Vue 3 + uView UI开发的现代化奶茶订购小程序，支持微信小程序、H5、App多端运行。

## ✨ 项目特色

### 🎨 现代化UI设计
- **uView UI组件库** - 丰富的组件，类似Element Plus的使用体验
- **毛玻璃效果** - backdrop-filter实现的现代视觉层次
- **渐变设计** - 橙色主题的渐变色彩系统 (#ff6b35)
- **动画交互** - 丰富的hover、active状态动画
- **响应式布局** - 适配不同屏幕尺寸

### 🛠️ 技术栈
- **前端框架**: Vue 3 + uni-app
- **UI组件库**: uView UI (类Element Plus体验)
- **状态管理**: Pinia (可选)
- **样式预处理**: SCSS
- **构建工具**: Vite
- **代码规范**: ESLint + Prettier

## 📱 功能模块

### 👤 用户中心模块 (25分)
- ✅ **会员体系** - 微信登录、会员等级、积分系统
- ✅ **个人信息管理** - 头像上传、信息编辑、会员卡管理
- ✅ **地址管理** - 智能定位、多地址管理、历史记录
- ✅ **安全设置** - 消息推送、隐私协议、账号注销
- ✅ **社交功能** - 分享优惠券、邀请好友

### 🛒 商品订购模块 (30分)
- ✅ **智能首页** - 个性化推荐、轮播图、促销活动
- ✅ **商品浏览** - 多级分类、筛选排序、详情展示
- ✅ **定制化点单** - 甜度/温度选择、加料系统
- ✅ **购物车结算** - 实时计价、优惠券匹配、订单备注
- ✅ **搜索功能** - 关键词搜索、历史记录、热门推荐

### 📋 订单管理模块 (25分)
- ✅ **订单流程** - 多种支付方式、状态跟踪
- ✅ **订单列表** - 分状态查看、筛选排序
- ✅ **订单详情** - 制作进度、预计时间
- ✅ **售后功能** - 申请退款、投诉建议
- ✅ **订单互动** - 催单功能、评价晒图

### 🎯 营销活动模块 (20分)
- ✅ **优惠券中心** - 领取管理、有效期跟踪
- ✅ **会员特权** - 专享价格、生日特权
- ✅ **促销活动** - 满减活动、限时折扣
- ✅ **积分商城** - 积分兑换商品/优惠券

## 🚀 快速开始

### 环境要求
- Node.js >= 16.0.0
- npm >= 8.0.0
- HBuilderX (推荐) 或 VS Code

### 安装依赖
```bash
# 进入项目目录
cd milk-tea-uniapp

# 安装依赖
npm install

# 安装sass依赖（已完成）
npm install sass -D
```

### 运行项目
```bash
# 微信小程序（推荐）
npm run dev:mp-weixin

# H5
npm run dev:h5

# App (生成App调试代码)
npm run dev:app

# 支付宝小程序
npm run dev:mp-alipay

# 其他小程序平台
npm run dev:mp-qq          # QQ小程序
npm run dev:mp-baidu       # 百度小程序
npm run dev:mp-toutiao     # 抖音小程序
npm run dev:mp-harmony     # 鸿蒙小程序
```

### ✅ 编译成功！
项目已经成功编译，现在可以：
1. 打开微信开发者工具
2. 导入项目目录：`dist/dev/mp-weixin`
3. 开始预览和调试

### 构建发布
```bash
# 构建微信小程序
npm run build:mp-weixin

# 构建H5
npm run build:h5

# 构建App (生成App源码)
npm run build:app

# 构建支付宝小程序
npm run build:mp-alipay

# 构建QQ小程序
npm run build:mp-qq

# 构建百度小程序
npm run build:mp-baidu

# 构建抖音小程序
npm run build:mp-toutiao

# 构建鸿蒙小程序
npm run build:mp-harmony
```

### 📱 App打包说明

#### CLI构建App源码
```bash
# 开发调试 - 生成App调试代码到 dist/dev/app
npm run dev:app

# 生产构建 - 生成App源码到 dist/build/app
npm run build:app
```

#### 最终App打包方式

**方式一：HBuilderX云打包（推荐）**
1. 使用HBuilderX打开项目
2. 点击"发行" -> "原生App-云打包"
3. 选择Android/iOS平台
4. 配置签名证书
5. 点击打包

**方式二：本地打包**
1. 安装Android Studio (Android) 或 Xcode (iOS)
2. 运行 `npm run build:app` 生成源码
3. 将生成的源码导入原生开发环境
4. 配置签名和打包

**方式三：离线SDK打包**
1. 下载uni-app离线SDK
2. 将构建的代码集成到离线SDK
3. 使用原生开发工具打包

## 🎯 uView UI组件使用示例

### 基础组件
\`\`\`vue
<template>
  <view>
    <!-- 按钮组件 -->
    <u-button type="primary" shape="circle">主要按钮</u-button>
    <u-button type="warning" shape="circle">警告按钮</u-button>
    
    <!-- 标签组件 -->
    <u-tag text="热销" type="error" size="mini"></u-tag>
    <u-tag text="新品" type="success" size="mini"></u-tag>
    
    <!-- 评分组件 -->
    <u-rate :value="4.5" :count="5" active-color="#ff6b35"></u-rate>
    
    <!-- 头像组件 -->
    <u-avatar src="/static/avatar.jpg" size="large"></u-avatar>
  </view>
</template>
\`\`\`

### 表单组件
\`\`\`vue
<template>
  <view>
    <!-- 输入框 -->
    <u-input 
      v-model="form.name" 
      placeholder="请输入姓名"
      border="bottom"
    ></u-input>
    
    <!-- 单选框组 -->
    <u-radio-group v-model="form.gender">
      <u-radio name="1" label="男"></u-radio>
      <u-radio name="2" label="女"></u-radio>
    </u-radio-group>
    
    <!-- 复选框组 -->
    <u-checkbox-group v-model="form.hobbies">
      <u-checkbox name="reading" label="阅读"></u-checkbox>
      <u-checkbox name="music" label="音乐"></u-checkbox>
    </u-checkbox-group>
  </view>
</template>
\`\`\`

### 反馈组件
\`\`\`vue
<template>
  <view>
    <!-- 弹窗 -->
    <u-popup v-model="showPopup" mode="bottom" border-radius="20">
      <view class="popup-content">
        <text>弹窗内容</text>
      </view>
    </u-popup>
    
    <!-- 操作菜单 -->
    <u-action-sheet 
      v-model="showActionSheet" 
      :list="actionList"
      @click="onActionClick"
    ></u-action-sheet>
    
    <!-- 加载更多 -->
    <u-loadmore 
      :status="loadStatus"
      @loadmore="loadMore"
    ></u-loadmore>
  </view>
</template>
\`\`\`

### 布局组件
\`\`\`vue
<template>
  <view>
    <!-- 单元格组 -->
    <u-cell-group>
      <u-cell title="标题" value="内容" is-link></u-cell>
      <u-cell title="设置" @click="goToSettings">
        <template #icon>
          <u-icon name="setting" size="20"></u-icon>
        </template>
      </u-cell>
    </u-cell-group>
    
    <!-- 标签页 -->
    <u-tabs 
      :list="tabList" 
      @change="onTabChange"
      active-color="#ff6b35"
    ></u-tabs>
    
    <!-- 轮播图 -->
    <u-swiper 
      :list="bannerList" 
      indicator 
      circular 
      :height="300"
    ></u-swiper>
  </view>
</template>
\`\`\`

## 📁 项目结构

\`\`\`
milk-tea-uniapp/
├── src/
│   ├── pages/                 # 页面文件
│   │   ├── index/            # 首页
│   │   ├── category/         # 分类页
│   │   ├── product/          # 商品详情
│   │   ├── cart/             # 购物车
│   │   ├── order/            # 订单确认
│   │   ├── order-list/       # 订单列表
│   │   ├── user/             # 用户中心
│   │   ├── login/            # 登录页
│   │   ├── coupon/           # 优惠券
│   │   └── ...               # 其他页面
│   ├── components/           # 组件
│   ├── utils/                # 工具类
│   │   ├── api.js           # API封装
│   │   ├── common.js        # 通用工具
│   │   └── cart.js          # 购物车管理
│   ├── static/              # 静态资源
│   ├── App.vue              # 应用入口
│   ├── main.js              # 主入口文件
│   ├── manifest.json        # 应用配置
│   ├── pages.json           # 页面配置
│   └── uni.scss             # 全局样式
├── package.json
└── README.md
\`\`\`

## 🎨 主题定制

### 颜色变量
\`\`\`scss
// uni.scss
$milktea-primary: #ff6b35;      // 主色调
$milktea-secondary: #f7931e;    // 辅助色
$milktea-accent: #ff8c42;       // 强调色
$milktea-bg-gradient: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
$milktea-shadow: 0 8rpx 32rpx rgba(255, 107, 53, 0.3);
$milktea-radius: 24rpx;         // 圆角
\`\`\`

### uView主题定制
\`\`\`scss
// 覆盖uView默认主题
$u-primary: #ff6b35;
$u-warning: #f7931e;
$u-success: #52c41a;
$u-error: #ff4d4f;
$u-info: #909399;
\`\`\`

## 🔧 开发工具推荐

### VS Code插件
- **uni-app-schemas** - uni-app语法提示
- **uniapp小程序扩展** - 代码片段和语法高亮
- **Vetur** - Vue语法支持
- **SCSS IntelliSense** - SCSS语法支持

### HBuilderX插件
- **uni-app编译器** - 官方编译器
- **uView UI** - 组件库支持
- **代码格式化** - 代码美化

## 📚 相关文档

- [uni-app官方文档](https://uniapp.dcloud.io/)
- [uView UI文档](https://www.uviewui.com/)
- [Vue 3文档](https://v3.vuejs.org/)
- [微信小程序文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (\`git checkout -b feature/AmazingFeature\`)
3. 提交更改 (\`git commit -m 'Add some AmazingFeature'\`)
4. 推送到分支 (\`git push origin feature/AmazingFeature\`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🎉 致谢

- [uni-app](https://uniapp.dcloud.io/) - 跨平台开发框架
- [uView UI](https://www.uviewui.com/) - 优秀的UI组件库
- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架

---

**🧋 享受编码，享受奶茶！**
