import request from '@/utils/request'

interface LoginParams {
  username: string
  password: string
}

interface RegisterParams {
  username: string
  password: string
}

export function login(data: LoginParams) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data,
  })
}

export function register(data: RegisterParams) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data,
  })
}

export function info() {
  return request({
    url: '/api/user/info',
    method: 'get',
  })
}
