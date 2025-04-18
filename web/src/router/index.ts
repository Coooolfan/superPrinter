import { createRouter, createWebHistory } from 'vue-router'
import useUserStore from '@/store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'Auth',
      component: () => import('@/views/auth/AuthLayout.vue'),
      children: [
        {
          path: 'login',
          name: 'Login',
          component: () => import('@/views/auth/LoginView.vue'),
        },
        {
          path: 'register',
          name: 'Register',
          component: () => import('@/views/auth/RegisterView.vue'),
        },
      ],
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/components/layout/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('@/views/HomeView.vue'),
        },
        {
          path: 'orders',
          name: 'Orders',
          component: () => import('@/views/order/OrdersView.vue'),
        },
        {
          path: 'merchant-orders',
          name: 'merchantOrders',
          component: () => import('@/views/order/MerchantOrdersView.vue'),
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
        },
        {
          path: 'printers',
          name: 'Printers',
          component: () => import('@/views/PrintersView.vue'),
          meta: { requiresMerchant: true }, // 仅商家可见
        },
      ],
    },
    // 订单创建流程路由 - 独立页面，不带底部导航栏
    {
      path: '/order-step1',
      name: 'OrderStep1',
      component: () => import('@/views/order/OrderStep1View.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/order-step2',
      name: 'OrderStep2',
      component: () => import('@/views/order/OrderStep2View.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!userStore.isLoggedIn) {
      next({
        path: '/auth/login',
        query: { redirect: to.fullPath },
      })
    } else if (to.matched.some((record) => record.meta.requiresMerchant)) {
      // 检查是否为商家权限 (role === 1)
      if (userStore.userInfo?.role === 1) {
        next()
      } else {
        next('/')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
