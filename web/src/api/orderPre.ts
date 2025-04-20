import request from '@/utils/request'

/**
 * 订单预处理余额响应
 */
export interface OrderPreBalanceResponse {
  /**
   * 页数
   */
  pageCount: number | string
}

/**
 * 获取订单预处理页数信息
 * @param uuid 预处理token
 */
export const getOrderPreBalance = (uuid: string) => {
  return request<OrderPreBalanceResponse>({
    url: `/api/print-order-pre/${uuid}`,
    method: 'get',
  })
}
