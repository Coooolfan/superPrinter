<template>
  <div class="min-h-screen flex items-center justify-center pb-40 bg-gray-50 px-4 py-12">
    <div class="w-full max-w-md">
      <van-form @submit="onSubmit" class="bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="px-6 py-8 bg-primary bg-gradient-to-r from-blue-500 to-blue-600 text-white">
          <h2 class="text-2xl font-bold text-center mb-2">登录</h2>
          <p class="text-center text-blue-100">欢迎使用超级打印</p>
        </div>

        <div class="px-6 py-6">
          <van-field
            v-model="username"
            name="username"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
            class="mb-4"
          >
            <template #label>
              <span class="text-gray-700 font-medium">用户名</span>
            </template>
          </van-field>

          <van-field
            v-model="password"
            type="password"
            name="password"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
            class="mb-6"
          >
            <template #label>
              <span class="text-gray-700 font-medium">密码</span>
            </template>
          </van-field>

          <div class="space-y-4">
            <van-button
              round
              block
              type="primary"
              native-type="submit"
              :loading="loading"
              class="h-12 font-medium text-base shadow-md"
            >
              登录
            </van-button>

            <div class="flex items-center justify-center mt-6">
              <span class="text-gray-500 mr-4">还没有账号?</span>
              <van-button type="default" size="small" to="/auth/register">
                <span class="text-blue-500 font-medium">立即注册</span>
              </van-button>
            </div>
          </div>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import useUserStore from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const loading = ref(false)

const onSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.login(username.value, password.value)
    if (success) {
      showToast({
        message: '登录成功',
        type: 'success',
      })
      const redirectPath = (route.query.redirect as string) || '/'
      router.replace(redirectPath)
    }
  } finally {
    loading.value = false
  }
}
</script>
