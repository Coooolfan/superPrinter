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
export function getOrderPreBalance(uuid: string): Promise<OrderPreBalanceResponse> {
  return request({
    url: `/api/print-order-pre/${uuid}`,
    method: 'get',
  })
}

/**
 * 创建订单请求数据类型
 */
export interface OrderCreateVO {
  /**
   * 打印机ID
   */
  printerId: number

  /**
   * 上传的文件IDs
   */
  fileIds: string

  /**
   * 纸张大小
   */
  paperSize: string

  /**
   * 是否彩色打印
   */
  color: boolean

  /**
   * 打印份数
   */
  copies: number

  /**
   * 是否双面打印
   */
  duplex: boolean

  /**
   * 备注信息
   */
  remark: string

  /**
   * 预处理token
   */
  token: string
}

/**
 * 创建订单响应数据类型
 */
export interface OrderCreateResponse {
  /**
   * 订单ID
   */
  orderId: string
}

/**
 * 创建订单
 * @param vo 订单请求数据
 * @returns Promise<OrderCreateResponse>
 */
export function createOrder(vo: OrderCreateVO): Promise<OrderCreateResponse> {
  return request({
    url: '/api/print-order',
    method: 'post',
    data: vo,
  })
}
