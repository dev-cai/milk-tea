import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      }
    ]
  },
  {
    path: '/product',
    component: Layout,
    redirect: '/product/list',
    meta: { title: '商品管理', icon: 'ShoppingBag' },
    children: [
      {
        path: 'list',
        name: 'ProductList',
        component: () => import('@/views/product/List.vue'),
        meta: { title: '商品列表', icon: 'List' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/product/Category.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: 'stock',
        name: 'ProductStock',
        component: () => import('@/views/product/Stock.vue'),
        meta: { title: '库存管理', icon: 'Box' }
      },
      {
        path: 'recipe',
        name: 'ProductRecipe',
        component: () => import('@/views/product/Recipe.vue'),
        meta: { title: '配方管理', icon: 'Document' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    redirect: '/order/process',
    meta: { title: '订单管理', icon: 'Document' },
    children: [
      {
        path: 'process',
        name: 'OrderProcess',
        component: () => import('@/views/order/Process.vue'),
        meta: { title: '订单处理', icon: 'Tools' }
      },
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/List.vue'),
        meta: { title: '订单列表', icon: 'List' }
      },
      {
        path: 'aftersale',
        name: 'OrderAftersale',
        component: () => import('@/views/order/Aftersale.vue'),
        meta: { title: '售后处理', icon: 'Service' }
      },
      {
        path: 'print',
        name: 'OrderPrint',
        component: () => import('@/views/order/Print.vue'),
        meta: { title: '打印管理', icon: 'Printer' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/member',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: 'member',
        name: 'UserMember',
        component: () => import('@/views/user/Member.vue'),
        meta: { title: '会员管理', icon: 'User' }
      },
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: { title: '用户列表', icon: 'List' }
      },
      {
        path: 'analysis',
        name: 'UserAnalysis',
        component: () => import('@/views/user/Analysis.vue'),
        meta: { title: '会员分析', icon: 'TrendCharts' }
      }
    ]
  },
  {
    path: '/marketing',
    component: Layout,
    redirect: '/marketing/activity',
    meta: { title: '营销管理', icon: 'Promotion' },
    children: [
      {
        path: 'activity',
        name: 'MarketingActivity',
        component: () => import('@/views/marketing/Activity.vue'),
        meta: { title: '活动管理', icon: 'Trophy' }
      },
      {
        path: 'coupon',
        name: 'MarketingCoupon',
        component: () => import('@/views/marketing/Coupon.vue'),
        meta: { title: '优惠券管理', icon: 'Ticket' }
      },
      {
        path: 'banner',
        name: 'MarketingBanner',
        component: () => import('@/views/marketing/Banner.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/staff',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'staff',
        name: 'SystemStaff',
        component: () => import('@/views/system/Staff.vue'),
        meta: { title: '员工管理', icon: 'UserFilled' }
      },
      {
        path: 'store',
        name: 'SystemStore',
        component: () => import('@/views/system/Store.vue'),
        meta: { title: '门店设置', icon: 'Shop' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/Config.vue'),
        meta: { title: '系统设置', icon: 'Tools' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else if (to.path !== '/login') {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (user && user.userType !== undefined && Number(user.userType) !== 1) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
