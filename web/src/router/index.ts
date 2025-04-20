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
      // 建议添加一个重定向，使得访问根路径'/'时也跳转到'/home'
      redirect: '/home', // 或者在children里添加 { path: '', redirect: '/home' }
      children: [
        // 如果上面添加了根路径重定向，这里的空路径可以去掉或保留用于特定逻辑
        // { path: '', redirect: '/home' },
        {
          path: 'home',
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

    // --- 新增：捕获所有未匹配的路由 ---
    // --- 必须放在路由配置数组的最后 ---
    {
      // 使用带参数的正则表达式匹配所有路径
      // :pathMatch(.*)* 是 vue-router 4.x 推荐的捕获所有路径的写法
      path: '/:pathMatch(.*)*',
      name: 'NotFound', // 可以给它一个名字，方便调试
      // 重定向到 HomeView 对应的路径
      redirect: '/home',
      // 注意：如果 HomeView 需要认证，这里的重定向会触发 beforeEach 守卫
      // 守卫会检查用户是否登录，如果未登录，会再次重定向到登录页
    },
  ],
})

// 路由守卫 (保持不变)
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查目标路由是否需要认证
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // 如果需要认证但用户未登录
    if (!userStore.isLoggedIn) {
      next({
        path: '/auth/login',
        // 保存用户原本想访问的路径，登录后可以跳回去
        query: { redirect: to.fullPath },
      })
    } else {
      // 如果用户已登录，再检查是否需要商家权限
      if (to.matched.some((record) => record.meta.requiresMerchant)) {
        // 检查用户角色是否为商家 (role === 1)
        if (userStore.userInfo?.role === 1) {
          next() // 允许访问
        } else {
          // 非商家，重定向到首页或其他无权限页面
          next('/') // 或者 next('/home')
        }
      } else {
        // 不需要特殊商家权限，直接允许访问
        next()
      }
    }
  } else {
    // 不需要认证的页面，直接允许访问
    next()
  }
})

export default router
