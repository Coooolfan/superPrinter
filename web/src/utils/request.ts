import axios from 'axios'
import { showToast } from 'vant'
import useUserStore from '@/store/user'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // const userStore = useUserStore()
    // if (userStore.token) {
    //   config.headers['Authorization'] = `Bearer ${userStore.token}`
    // }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (Math.floor(response.status) / 100 != 2) {
      showToast({
        message: res.message || '请求失败',
        type: 'fail',
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('Response error:', error)
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/auth/login')
    }
    showToast({
      message: error.response?.data?.message || '网络错误',
      type: 'fail',
    })
    return Promise.reject(error)
  },
)

export default request
