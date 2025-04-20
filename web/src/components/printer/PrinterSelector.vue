<template>
  <div>
    <!-- 打印机搜索 -->
    <van-search v-model="searchValue" placeholder="搜索打印机名称" @search="onSearch" />

    <!-- 打印机加载状态 -->
    <div v-if="loading" class="py-4 text-center">
      <van-loading type="spinner" size="24px">加载中...</van-loading>
    </div>

    <!-- 打印机列表 -->
    <van-radio-group v-model="selected" v-else>
      <div
        v-for="printer in filteredPrinters"
        :key="printer.printerId"
        class="p-3 border-b border-gray-100"
      >
        <div class="flex items-center">
          <van-radio 
            :name="String(printer.printerId)" 
            class="flex-1" 
            :disabled="isDisabled(printer)"
          >
            <div class="ml-1">
              <div class="font-medium">{{ printer.printerName }}</div>
              <div class="flex flex-wrap gap-1 mt-2">
                <van-tag :type="getPrinterStatusTag(printer.status).type">
                  {{ getPrinterStatusTag(printer.status).text }}
                </van-tag>
                <van-tag type="primary" plain>
                  {{ printer.supportColor ? '彩色' : '黑白' }}
                </van-tag>
                <van-tag type="success" plain v-if="printer.supportDuplex">
                  双面打印
                </van-tag>
                <van-tag type="warning" plain v-if="printer.paperCount < 50">
                  纸张剩余: {{ printer.paperCount }}
                </van-tag>
              </div>
              <div class="text-xs text-gray-500 mt-2">
                支持纸张: {{ printer.paperType || 'A4' }}
              </div>
              <!-- 不匹配原因提示 -->
              <div v-if="isPrinterMismatch(printer)" class="text-xs text-red-500 mt-1">
                {{ getPrinterMismatchReason(printer) }}
              </div>
            </div>
          </van-radio>
        </div>
      </div>
    </van-radio-group>

    <!-- 无打印机提示 -->
    <div v-if="!loading && filteredPrinters.length === 0" class="py-4 text-center text-gray-500">
      没有找到符合条件的打印机
    </div>
    
    <!-- 没有匹配打印需求的打印机提示 -->
    <div v-if="!loading && filteredPrinters.length > 0 && !hasMatchingPrinters" class="mt-3 p-2 bg-orange-50 text-orange-600 text-sm rounded">
      提示: 没有找到完全匹配您打印需求的打印机。请考虑调整打印设置或联系管理员。
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { PrinterResource } from '@/api/printer'

const props = defineProps<{
  printers: PrinterResource[]
  loading: boolean
  modelValue: string
  printRequirements?: {
    color: string
    paperSize: string
    duplex?: boolean
    requiredPaperCount?: number // 添加所需纸张数量参数
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
}>()

const searchValue = ref('')
const selected = ref(props.modelValue)

// 监听props的modelValue变化
watch(() => props.modelValue, (newValue) => {
  selected.value = newValue
})

// 监听组件内部selected变化，向父组件发送更新
watch(() => selected.value, (newValue) => {
  emit('update:modelValue', newValue)
})

// 筛选打印机
const filteredPrinters = computed(() => {
  if (!searchValue.value) return props.printers

  const keyword = searchValue.value.toLowerCase()
  return props.printers.filter(
    (printer) => printer.printerName.toLowerCase().includes(keyword)
  )
})

// 获取打印机状态对应的标签类型
const getPrinterStatusTag = (status: string) => {
  switch (status) {
    case 'ONLINE':
      return { type: 'success', text: '在线' }
    case 'OFFLINE':
      return { type: 'danger', text: '离线' }
    case 'OUT_OF_PAPER':
      return { type: 'warning', text: '缺纸' }
    default:
      return { type: 'default', text: '未知' }
  }
}

// 搜索打印机
const onSearch = () => {
  emit('search', searchValue.value)
}

// 检查打印机是否与打印需求匹配
const isPrinterMismatch = (printer: PrinterResource) => {
  // 如果没有指定打印需求，则不需要检查
  if (!props.printRequirements) return false
  
  // 检查颜色需求
  if (props.printRequirements.color === 'color' && !printer.supportColor) {
    return true
  }
  
  // 检查纸张类型
  const supportedPaperTypes = printer.paperType 
    ? printer.paperType.split(',').map(type => type.trim()) 
    : ['A4']
  if (!supportedPaperTypes.includes(props.printRequirements.paperSize)) {
    return true
  }
  
  // 检查双面打印
  if (props.printRequirements.duplex && !printer.supportDuplex) {
    return true
  }
  
  // 检查纸张数量是否足够
  if (props.printRequirements.requiredPaperCount && 
      printer.paperCount < props.printRequirements.requiredPaperCount) {
    return true
  }
  
  return false
}

// 获取打印机不匹配的原因
const getPrinterMismatchReason = (printer: PrinterResource) => {
  if (!props.printRequirements) return ''
  
  const reasons = []
  
  // 检查颜色需求
  if (props.printRequirements.color === 'color' && !printer.supportColor) {
    reasons.push('不支持彩色打印')
  }
  
  // 检查纸张类型
  const supportedPaperTypes = printer.paperType 
    ? printer.paperType.split(',').map(type => type.trim()) 
    : ['A4']
  if (!supportedPaperTypes.includes(props.printRequirements.paperSize)) {
    reasons.push(`不支持${props.printRequirements.paperSize}纸张`)
  }
  
  // 检查双面打印
  if (props.printRequirements.duplex && !printer.supportDuplex) {
    reasons.push('不支持双面打印')
  }
  
  // 检查纸张数量是否足够
  if (props.printRequirements.requiredPaperCount && 
      printer.paperCount < props.printRequirements.requiredPaperCount) {
    reasons.push(`纸张不足(需要${props.printRequirements.requiredPaperCount}张,剩余${printer.paperCount}张)`)
  }
  
  return reasons.join('、')
}

// 检查打印机是否禁用
const isDisabled = (printer: PrinterResource) => {
  // 离线或缺纸的打印机不可选
  if (printer.status !== 'ONLINE' || printer.paperCount <= 0) {
    return true
  }
  
  // 如果不匹配打印需求，也禁用
  return isPrinterMismatch(printer)
}

// 是否有匹配打印需求的打印机
const hasMatchingPrinters = computed(() => {
  return filteredPrinters.value.some(printer => !isDisabled(printer))
})
</script>

<style scoped>
/* 可添加自定义样式 */
</style>