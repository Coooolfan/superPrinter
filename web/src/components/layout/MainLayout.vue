<template>
  <div class="main-layout">
    <router-view />
    <van-tabbar v-model="active" route>
      <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/orders" icon="orders-o">订单</van-tabbar-item>
      <van-tabbar-item v-if="isMerchant" to="/merchant-orders" icon="orders-o"
        >商家订单</van-tabbar-item
      >
      <van-tabbar-item v-if="isMerchant" to="/printers" icon="printer">打印机</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import useUserStore from '@/store/user'

const active = ref(0)
const userStore = useUserStore()

// 判断是否为商家
const isMerchant = computed(() => {
  return userStore.userInfo?.role === 1
})
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

:deep(.van-tabbar) {
  border-top: 1px solid #f7f8fa;
}
</style>
