import request from '@/utils/request'
import axios from 'axios'

/**
 * 文件预签名上传请求类型
 */
export interface FileUploadPreSignVO {
  originalName: string
  fileType: string
}

/**
 * 文件预签名上传响应类型
 */
export interface FileUploadPreSignResponse {
  preSignUrl: string
  fileInfo: {
    fileId: number
    userId: string
    originalName: string
    storedName: string
    fileType: string
    fileSize: number
    pageCount: number
    uploadTime: string
  }
}

/**
 * 获取文件上传预签名URL
 * @param vo 文件信息
 * @returns Promise<FileUploadPreSignResponse>
 */
export function getFileUploadPreSign(vo: FileUploadPreSignVO): Promise<FileUploadPreSignResponse> {
  return request({
    url: '/api/file',
    method: 'post',
    data: vo,
  })
}

/**
 * 使用预签名URL上传文件
 * @param preSignUrl 预签名URL
 * @param file 文件对象
 * @returns Promise
 */
export function uploadFileWithPreSign(preSignUrl: string, file: File) {
  return axios.put(preSignUrl, file, {
    headers: {
      'Content-Type': 'application/octet-stream',
    },
  })
}
