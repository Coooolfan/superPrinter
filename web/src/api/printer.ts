import request from '@/utils/request'

/**
 * 打印机资源接口类型
 */
export interface PrinterResource {
  /**
   * 打印机名称
   */
  printerName: string

  /**
   * 打印机状态：ONLINE,OFFLINE,OUT_OF_PAPER
   */
  status: string

  /**
   * 支持彩色打印
   */
  supportColor: boolean

  /**
   * 支持双面打印
   */
  supportDuplex: boolean

  /**
   * 支持的纸张类型
   * 例如：A4,A5,Letter,Legal 以逗号分隔
   */
  paperType: string

  /**
   * A纸张数量
   */
  paperCount: number

  /**
   * 打印机ID（可选，用于返回数据）
   */
  printerId?: number
}

/**
 * 获取单个打印机资源信息
 * @param printerId 打印机ID
 */
export function getPrinter(printerId: number) {
  return request<PrinterResource>({
    url: `/api/printer/${printerId}`,
    method: 'get',
  })
}

/**
 * 获取所有打印机资源列表
 */
export function getPrinterList(): Promise<PrinterResource[]> {
  return request({
    url: '/api/printer/list',
    method: 'get',
  })
}

/**
 * 添加打印机资源
 * @param printerData 打印机资源信息
 */
export function addPrinter(printerData: PrinterResource): Promise<PrinterResource> {
  return request({
    url: '/api/printer',
    method: 'post',
    data: printerData,
  })
}

/**
 * 更新打印机资源
 * @param printerId 打印机ID
 * @param printerData 打印机资源信息
 */
export function updatePrinter(printerId: number, printerData: PrinterResource) {
  return request<{ message: string }>({
    url: `/api/printer/${printerId}`,
    method: 'put',
    data: {
      ...printerData,
      printerId: undefined,
    },
  })
}

/**
 * 删除打印机资源
 * @param printerId 打印机ID
 */
export function deletePrinter(printerId: number): Promise<{ message: string }> {
  return request({
    url: `/api/printer/${printerId}`,
    method: 'delete',
  })
}
