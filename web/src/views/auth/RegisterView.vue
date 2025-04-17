<template>
  <div class="min-h-screen flex items-center justify-center pb-40 bg-gray-50 px-4 py-12">
    <div class="w-full max-w-md">
      <van-form @submit="onSubmit" class="bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="px-6 py-8 bg-primary bg-gradient-to-r from-blue-500 to-blue-600 text-white">
          <h2 class="text-2xl font-bold text-center mb-2">注册账号</h2>
          <p class="text-center text-blue-100">创建您的超级打印账号</p>
        </div>

        <div class="px-6 py-6">
          <van-field
            v-model="username"
            name="username"
            placeholder="请输入用户名"
            class="mb-4"
            :rules="[
              { required: true, message: '请输入用户名' },
              { pattern: /^[a-zA-Z0-9_]{4,16}$/, message: '用户名必须是4-16位字母、数字或下划线' },
            ]"
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
            class="mb-4"
            :rules="[
              { required: true, message: '请输入密码' },
              { pattern: /^.{6,20}$/, message: '密码长度必须在6-20位之间' },
            ]"
          >
            <template #label>
              <span class="text-gray-700 font-medium">密码</span>
            </template>
          </van-field>

          <van-field
            v-model="confirmPassword"
            type="password"
            name="confirmPassword"
            placeholder="请再次输入密码"
            class="mb-6"
            :rules="[
              { required: true, message: '请确认密码' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' },
            ]"
          >
            <template #label>
              <span class="text-gray-700 font-medium">确认密码</span>
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
              注册
            </van-button>

            <div class="flex items-center justify-center mt-6">
              <span class="text-gray-500 mr-4">已有账号?</span>
              <van-button plain type="default" size="small" to="/auth/login">
                <span class="text-blue-500 font-medium">返回登录</span>
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
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import useUserStore from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)

const validateConfirmPassword = () => {
  if (password.value !== confirmPassword.value) {
    return false
  }
  return true
}

const onSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.register(username.value, password.value)
    if (success) {
      showToast({
        message: '注册成功，请登录',
        type: 'success',
      })
      router.push('/auth/login')
    }
  } finally {
    loading.value = false
  }
}
</script>
