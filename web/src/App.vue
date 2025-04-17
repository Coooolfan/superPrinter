<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import useUserStore from './store/user'

const userStore = useUserStore()

onMounted(() => {
  // 在挂载时获取用户信息
  if (userStore.token) {
    userStore.info()
  }
})
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
#app {
  font-family:
    -apple-system,
    BlinkMacSystemFont,
    'Helvetica Neue',
    Helvetica,
    Segoe UI,
    Arial,
    Roboto,
    'PingFang SC',
    'miui',
    'Hiragino Sans GB',
    'Microsoft Yahei',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
