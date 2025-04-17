import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi,info as infoApi } from '@/api/auth'

export default defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<{
    userId: string
    username: string
    role: 0 | 1 | 2 // 用户角色：0-普通用户，1-商户，2-管理员
  } | null>(null)

  const isLoggedIn = ref(!!token.value)

  const login = async (username: string, password: string) => {
    try {
      const response = await loginApi({ username, password })
      console.log(response)
      // 从响应中获取数据
      const data = response.data || response
      token.value = data.token
      userInfo.value = {
        userId: data.userId,
        username: data.username,
        role: data.role
      }
      isLoggedIn.value = true
      localStorage.setItem('token', token.value)
      return true
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  const register = async (username: string, password: string) => {
    try {
      await registerApi({ username, password })
      return true
    } catch (error) {
      console.error('Registration failed:', error)
      return false
    }
  }

  const info = async()=>{
    try {
      const response = await infoApi()
      console.log(response)
      // 从响应中获取数据
      const data = response.data || response
      token.value = data.token
      userInfo.value = {
        userId: data.userId,
        username: data.username,
        role: data.role
      }
      isLoggedIn.value = true
      return true
    } catch (error) {
      console.error('Login state unvaliud:', error)
      return false
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    logout,
    info
  }
})
