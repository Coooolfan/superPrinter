import request from '@/utils/request'

/**
 * 订单预处理请求数据类型
 */
export interface OrderPreTokenVO {
  fileIds: string
}

/**
 * 订单预处理响应数据类型
 */
export interface OrderPreTokenResponse {
  token: string
}

/**
 * 获取订单预处理token
 * @param vo 订单文件ID列表，以逗号分隔的字符串
 * @returns Promise<OrderPreTokenResponse>
 */
export function getOrderPreToken(vo: OrderPreTokenVO): Promise<OrderPreTokenResponse> {
  return request({
    url: '/api/print-order-pre',
    method: 'post',
    data: vo,
  })
}
