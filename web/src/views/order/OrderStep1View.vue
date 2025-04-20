<template>
  <div class="bg-gray-50 min-h-screen pb-20">
    <!-- 顶部导航栏 带返回按钮 -->
    <van-nav-bar
      title="上传打印文件"
      left-text="返回"
      left-arrow
      @click-left="goBack"
      fixed
      placeholder
      class="bg-white shadow-sm"
    />

    <div class="p-4">
      <!-- 文件上传区域 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-center py-6 border-2 border-dashed border-gray-300 rounded-lg">
          <van-uploader
            v-model="fileList"
            multiple
            :preview-image="false"
            :max-count="10"
            :after-read="afterRead"
            accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
            :disabled="isUploading"
          >
            <van-icon name="plus" size="24" class="mb-2 text-primary" />
            <div class="text-gray-500">点击上传或拖拽文件到此处</div>
            <div class="text-xs text-gray-400 mt-1">支持PDF、Word、图片等多种格式</div>
          </van-uploader>
        </div>
      </div>

      <!-- 已上传文件列表 -->
      <div v-if="fileList.length > 0" class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">已上传文件 ({{ fileList.length }})</div>
        <div
          v-for="(file, index) in fileList"
          :key="index"
          class="flex items-center justify-between py-3 border-b border-gray-100"
        >
          <div class="flex items-center">
            <van-icon name="description" size="24" class="mr-3 text-primary" />
            <div>
              <div class="text-base truncate max-w-52">{{ file.file?.name ?? '未知文件' }}</div>
              <div class="text-xs text-gray-500">{{ formatFileSize(file.file?.size ?? 0) }}</div>
            </div>
          </div>
          <div class="flex items-center">
            <van-tag
              v-if="file.status === 'uploading' && !(file as CustomFileItem).customStatus"
              type="primary"
              >上传中</van-tag
            >
            <van-tag v-else-if="(file as CustomFileItem).customStatus === 'success'" type="success"
              >已上传</van-tag
            >
            <van-tag v-else-if="file.status === 'failed'" type="danger">上传失败</van-tag>
            <van-icon name="cross" @click="removeFile(index)" class="text-gray-500 ml-2" />
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="fixed bottom-0 left-0 right-0 p-4 bg-white border-t border-gray-200">
      <van-button
        type="primary"
        block
        :disabled="fileList.length === 0 || isUploading"
        @click="goToNextStep"
      >
        继续
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { UploaderFileListItem } from 'vant'
import { showToast } from 'vant'
import { getFileUploadPreSign, uploadFileWithPreSign } from '@/api/file'
import type { FileUploadPreSignVO } from '@/api/file'
import useOrderFileStore from '@/store/orderFile'

const orderFileStore = useOrderFileStore()

const router = useRouter()
const fileList = ref<UploaderFileListItem[]>([])
const isUploading = ref(false)

// 自定义上传状态类型
type FileStatus = 'uploading' | 'success' | 'failed'

// 扩展文件项类型
interface CustomFileItem extends UploaderFileListItem {
  customStatus?: FileStatus
}

// 获取文件类型
const getFileType = (filename: string): string => {
  const ext = filename.split('.').pop()?.toLowerCase() || ''

  // 映射常见文件扩展名到文件类型
  const typeMap: Record<string, string> = {
    pdf: 'application/pdf',
    doc: 'application/msword',
    docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    jpg: 'image/jpeg',
    jpeg: 'image/jpeg',
    png: 'image/png',
  }

  return typeMap[ext] || 'application/octet-stream'
}

// 上传后处理
const afterRead = async (file: UploaderFileListItem | UploaderFileListItem[]) => {
  const files = Array.isArray(file) ? file : [file]
  isUploading.value = true

  try {
    for (const fileItem of files) {
      const customFileItem = fileItem as CustomFileItem

      // 确保文件存在
      if (!customFileItem.file) {
        console.error('文件对象不存在')
        continue
      }

      customFileItem.status = 'uploading'

      const fileName = customFileItem.file.name
      const fileType = getFileType(fileName)

      // 准备请求参数
      const requestVO: FileUploadPreSignVO = {
        originalName: fileName,
        fileType: fileType,
      }

      // 获取预签名URL
      const response = await getFileUploadPreSign(requestVO)
      const preSignUrl = response.preSignUrl

      // 上传文件
      await uploadFileWithPreSign(preSignUrl, customFileItem.file)

      // 设置为自定义状态
      customFileItem.customStatus = 'success'
      orderFileStore.addOrderFile(response.fileInfo.fileId)
      showToast('文件上传成功')
    }
  } catch (error) {
    console.error('文件上传错误:', error)
    files.forEach((fileItem) => {
      const customFileItem = fileItem as CustomFileItem
      customFileItem.status = 'failed'
    })
    showToast('文件上传失败')
  } finally {
    isUploading.value = false
  }
}

// 移除文件
const removeFile = (index: number) => {
  fileList.value.splice(index, 1)
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(1) + ' MB'
  }
}

// 返回上一页
const goBack = () => {
  router.push('/')
}

// 前往下一步
const goToNextStep = () => {
  // 检查是否所有文件都已上传完成
  const allUploaded = fileList.value.every(
    (file) => (file as CustomFileItem).customStatus === 'success',
  )

  if (!allUploaded) {
    showToast('请等待所有文件上传完成')
    return
  }

  // 保存当前选择的文件和设置
  router.push({
    name: 'OrderStep2',
  })
}
</script>

<style scoped>
/* 自定义样式 */
:deep(.van-uploader__upload) {
  width: 100%;
  height: 120px;
  margin: 0;
}
</style>
