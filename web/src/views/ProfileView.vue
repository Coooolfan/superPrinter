<template>
  <div class="bg-[#f7f8fa] min-h-screen p-[3rem_16px_16px_16px] box-border">
    <!-- 问候语 -->
    <div class="mb-5 py-[5px]">
      <h1 class="text-2xl font-semibold text-[#323233] m-0 mb-1">
        {{ greeting }}，{{ userStore.userInfo?.username || '用户' }}
      </h1>
      <p class="text-sm text-[#969799] m-0">欢迎使用打印机管理系统</p>
    </div>

    <!-- 用户信息区 -->
    <div class="bg-white rounded-xl overflow-hidden shadow-[0_1px_3px_rgba(0,0,0,0.05)] mb-4">
      <div class="bg-gradient-to-r from-[#1989fa] to-[#39a9ed] p-5 text-white">
        <div class="text-xl font-semibold mb-1">{{ userStore.userInfo?.username || '未登录' }}</div>
        <div class="inline-block text-sm bg-[rgba(255,255,255,0.2)] px-[10px] py-[3px] rounded-xl">
          {{ userRoleText }}
        </div>
      </div>
      <div class="p-[12px_20px]">
        <div class="flex justify-between py-3 border-b border-[#f5f7fa]">
          <span class="text-[#646566] text-[15px]">用户ID</span>
          <span class="text-[#323233] text-[15px] font-medium">{{
            userStore.userInfo?.userId || '-'
          }}</span>
        </div>
        <div class="flex justify-between py-3 border-b border-[#f5f7fa]">
          <span class="text-[#646566] text-[15px]">角色权限</span>
          <span class="text-[#323233] text-[15px] font-medium">{{ userRoleDesc }}</span>
        </div>
        <div class="flex justify-between py-3 border-b border-[#f5f7fa]">
          <span class="text-[#646566] text-[15px]">登录状态</span>
          <span class="text-[#07c160] text-[15px] font-medium">活跃</span>
        </div>
        <div class="flex justify-between py-3">
          <span class="text-[#646566] text-[15px]">当前时间</span>
          <span class="text-[#323233] text-[15px] font-medium">{{ currentTime }}</span>
        </div>
      </div>
    </div>

    <!-- 系统状态卡片 -->
    <div
      class="bg-white rounded-xl overflow-hidden shadow-[0_1px_3px_rgba(0,0,0,0.05)] mb-4 p-[16px_20px]"
    >
      <h3 class="text-base font-medium text-[#323233] m-0 mb-3">系统状态</h3>
      <div class="p-0">
        <div class="flex items-center py-2">
          <van-icon name="checked" class="text-lg mr-2 text-[#07c160]" />
          <span>所有服务正常运行</span>
        </div>
        <div class="flex items-center py-2">
          <van-icon name="clock-o" class="text-lg mr-2 text-[#969799]" />
          <span>上次更新: {{ formatDate(new Date()) }}</span>
        </div>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <div class="mt-6">
      <van-button round block type="danger" class="h-11 text-base" @click="handleLogout">
        退出登录
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showToast, showDialog } from 'vant'
import { useRouter } from 'vue-router'
import useUserStore from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const currentTime = ref('')

// 获取问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

// 获取用户角色文本
const userRoleText = computed(() => {
  if (!userStore.userInfo) return '未登录'

  const roleMap: Record<number, string> = {
    0: '普通用户',
    1: '商户',
    2: '管理员',
  }

  return roleMap[userStore.userInfo.role] || '未知角色'
})

// 获取用户角色描述
const userRoleDesc = computed(() => {
  if (!userStore.userInfo) return '-'

  const roleDescMap: Record<number, string> = {
    0: '可以使用系统基础功能',
    1: '可以管理商户资源和用户',
    2: '可以管理所有系统资源',
  }

  return roleDescMap[userStore.userInfo.role] || '未知权限'
})

// 格式化日期
const formatDate = (date: Date) => {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${hours}:${minutes}:${seconds}`
}

// 处理退出登录
const handleLogout = () => {
  showDialog({
    title: '确认退出',
    message: '您确定要退出当前账号吗？',
    showCancelButton: true,
  })
    .then(() => {
      userStore.logout()
      showToast('已成功退出登录')
      router.push('/auth/login')
    })
    .catch(() => {
      // 取消退出
    })
}

// 检查登录状态并初始化
onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  // 初始化当前时间并每秒更新
  updateCurrentTime()
  setInterval(updateCurrentTime, 1000)
})
</script>
