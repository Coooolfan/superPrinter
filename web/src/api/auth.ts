import request from '@/utils/request'

interface LoginParams {
  username: string
  password: string
}

interface RegisterParams {
  username: string
  password: string
}

export const login = (data: LoginParams) => {
  return request({
    url: '/api/user/login',
    method: 'post',
    data,
  })
}

export const register = (data: RegisterParams) => {
  return request({
    url: '/api/user/register',
    method: 'post',
    data,
  })
}

export const info = () => {
  return request({
    url: '/api/user/info',
    method: 'get',
  })
}
