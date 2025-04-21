<template>
  <div class="bg-gray-50 min-h-screen pb-20">
    <!-- 顶部导航栏 带返回按钮 -->
    <van-nav-bar
      title="确认订单"
      left-text="返回"
      left-arrow
      @click-left="goBack"
      fixed
      placeholder
      class="bg-white shadow-sm"
    />

    <div class="p-4">
      <!-- 文件概览 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">已上传文件 ({{ orderFileCount }})</div>
        <div class="flex items-center justify-between py-2">
          <div class="flex items-center">
            <van-icon name="description" size="20" class="mr-2 text-primary" />
            <span class="text-sm">{{ orderFileCount }}个文件</span>
          </div>
        </div>
      </div>

      <!-- 打印参数选择 (简易版) -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">基本打印设置</div>

        <div class="grid grid-cols-2 gap-4">
          <div class="mb-4">
            <div class="mb-2">打印颜色</div>
            <van-radio-group v-model="printSettings.color" direction="horizontal">
              <van-radio name="black" class="mr-4">黑白</van-radio>
              <van-radio name="color" :disabled="!hasSupportForColor">彩色</van-radio>
            </van-radio-group>
          </div>

          <div class="mb-4">
            <div class="mb-2">纸张大小</div>
            <van-dropdown-menu>
              <van-dropdown-item v-model="printSettings.paperSize" :options="paperSizeOptions" />
            </van-dropdown-menu>
          </div>

          <div class="mb-4">
            <div class="mb-2">双面打印</div>
            <van-switch v-model="printSettings.duplex" :disabled="!hasSupportForDuplex" size="20" />
            <span class="text-xs text-gray-500 ml-2" v-if="!hasSupportForDuplex">
              当前无可用的双面打印机
            </span>
          </div>

          <div>
            <div class="mb-2">打印份数</div>
            <van-stepper v-model="printSettings.copies" min="1" max="100" />
          </div>
        </div>
        
        <!-- 纸张用量提示 -->
        <div v-if="requiredPaperCount" class="mt-4 p-3 bg-blue-50 rounded-lg">
          <div class="flex items-center">
            <van-icon name="description" class="mr-2 text-blue-500" />
            <span class="text-blue-700 font-medium">纸张用量：{{ requiredPaperCount }}张</span>
          </div>
          <div class="text-xs text-blue-600 mt-1" v-if="printSettings.duplex">
            <van-icon name="info-o" class="mr-1" />
            已按双面打印计算纸张用量 ({{ documentPageCount }}页÷2≈{{ Math.ceil(documentPageCount / 2) }}张/份×{{ printSettings.copies }}份)
          </div>
          <div class="text-xs text-blue-600 mt-1" v-else>
            <van-icon name="info-o" class="mr-1" />
            单面打印纸张用量 ({{ documentPageCount }}页/份×{{ printSettings.copies }}份)
          </div>
        </div>
      </div>

      <!-- 选择打印机 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">选择打印机</div>

        <!-- 使用打印机选择器组件 -->
        <printer-selector
          v-model="selectedPrinter"
          :printers="printers"
          :loading="loadingPrinters"
          :print-requirements="printRequirements"
          @search="onSearch"
        />
      </div>

      <!-- 备注信息 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">备注信息</div>
        <van-field
          v-model="remark"
          type="textarea"
          placeholder="请输入备注信息（可选）"
          rows="2"
          autosize
        />
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200">
      <!-- 价格信息 -->
      <div class="flex justify-between items-center px-4 py-2">
        <div class="flex flex-col">
          <div class="flex items-center">
            <span class="text-gray-600 mr-2">预估费用:</span>

            <!-- 打印参数与打印机不匹配 -->
            <template v-if="selectedPrinter && !isPrinterMatchRequirements">
              <span class="text-xl font-bold text-orange-500">参数不匹配</span>
            </template>

            <!-- 计算中状态 -->
            <template v-else-if="typeof totalPrice === 'string'">
              <span class="text-xl font-bold text-gray-500">
                {{ totalPrice }}
                <van-loading v-if="loadingPageCount" type="spinner" size="12px" class="ml-1" />
              </span>
            </template>

            <!-- 正常显示价格 -->
            <template v-else>
              <span class="text-xl font-bold text-red-500">¥{{ totalPrice.toFixed(2) }}</span>
              <span v-if="documentPageCount" class="text-xs text-gray-500 ml-2">
                ({{ documentPageCount }}页 x {{ printSettings.copies }}份)
              </span>
            </template>
          </div>

          <!-- 参数不匹配提示 -->
          <div
            v-if="selectedPrinter && !isPrinterMatchRequirements"
            class="text-xs text-orange-500 mt-1"
          >
            {{ getMismatchReason }}
          </div>
        </div>

        <van-button
          type="primary"
          :disabled="!selectedPrinter || documentPageCount === null || !isPrinterMatchRequirements"
          @click="createOrder"
        >
          创建订单
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import useOrderFileStore from '@/store/orderFile'
import { getOrderPreToken } from '@/api/order'
import { getPrinterList, type PrinterResource } from '@/api/printer'
import PrinterSelector from '@/components/printer/PrinterSelector.vue'
import { getOrderPreBalance } from '@/api/orderPre' // 导入新的 API 方法

const orderFileStore = useOrderFileStore()
const router = useRouter()
const searchPrinter = ref('')
const selectedPrinter = ref('')
const remark = ref('')
const showPrinterDialog = ref(false)
const selectedPrinterDetails = ref<PrinterResource | null>(null)
const orderPreToken = ref('') // 存储订单预处理token
const printers = ref<PrinterResource[]>([]) // 使用PrinterResource类型
const loadingPrinters = ref(false) // 添加加载状态标识

// 文档页数
const documentPageCount = ref<number | null>(null)
const loadingPageCount = ref(false)
const pollInterval = ref<number | null>(null)

// 打印设置
const printSettings = ref({
  color: 'black',
  paperSize: 'A4',
  copies: 1,
  duplex: false, // 添加双面打印选项，默认为单面
})

// 获取打印机列表数据
const fetchPrinters = async () => {
  loadingPrinters.value = true
  try {
    const response = await getPrinterList()
    printers.value = response // 直接使用API返回的数据，无需转换
    console.log('成功获取打印机列表:', printers.value)
  } catch (error) {
    console.error('获取打印机列表失败:', error)
    showToast('获取打印机列表失败，请稍后重试')
  } finally {
    loadingPrinters.value = false
  }
}

// 纸张大小选项
const paperSizeOptions = computed(() => {
  // 从打印机支持的纸张类型中提取唯一的纸张类型选项
  const availablePaperTypes = new Set<string>()

  printers.value.forEach((printer) => {
    if (printer.paperType) {
      printer.paperType.split(',').forEach((type) => {
        availablePaperTypes.add(type.trim())
      })
    }
  })

  // 如果没有任何选项，提供默认的A4
  if (availablePaperTypes.size === 0) {
    availablePaperTypes.add('A4')
  }

  return Array.from(availablePaperTypes).map((type) => ({
    text: type,
    value: type,
  }))
})

// 筛选打印机
const filteredPrinters = computed(() => {
  if (!searchPrinter.value) return printers.value

  const keyword = searchPrinter.value.toLowerCase()
  return printers.value.filter((printer) => printer.printerName.toLowerCase().includes(keyword))
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

// 是否支持彩色打印
const hasSupportForColor = computed(() => {
  return printers.value.some((printer) => printer.supportColor && printer.status === 'ONLINE')
})

// 是否支持双面打印
const hasSupportForDuplex = computed(() => {
  return printers.value.some((printer) => printer.supportDuplex && printer.status === 'ONLINE')
})

// 计算所需纸张数量
const requiredPaperCount = computed(() => {
  if (documentPageCount.value === null) return null;
  
  // 对于双面打印，需要考虑实际消耗的纸张数量
  // 例如：10页文档双面打印理论上需要5张纸，但因为可能有奇数页，所以向上取整
  let sheetsPerCopy = documentPageCount.value;
  if (printSettings.value.duplex) {
    sheetsPerCopy = Math.ceil(documentPageCount.value / 2);
  }
  
  // 总纸张数 = 每份所需纸张数 × 份数
  return sheetsPerCopy * printSettings.value.copies;
})

// 检查当前打印设置是否与选择的打印机匹配
const isPrinterMatchRequirements = computed(() => {
  if (!selectedPrinter.value) return false
  
  const printer = printers.value.find((p) => String(p.printerId) === selectedPrinter.value)
  if (!printer) return false
  
  // 检查颜色需求
  if (printSettings.value.color === 'color' && !printer.supportColor) {
    return false
  }
  
  // 检查纸张类型
  const supportedPaperTypes = printer.paperType
    ? printer.paperType.split(',').map((type) => type.trim())
    : ['A4']
  if (!supportedPaperTypes.includes(printSettings.value.paperSize)) {
    return false
  }
  
  // 检查双面打印需求
  if (printSettings.value.duplex && !printer.supportDuplex) {
    return false
  }
  
  // 检查剩余纸张数量是否足够
  if (requiredPaperCount.value && printer.paperCount < requiredPaperCount.value) {
    return false
  }
  
  return true
})

// 获取不匹配的原因
const getMismatchReason = computed(() => {
  if (!selectedPrinter.value) return ''

  const printer = printers.value.find((p) => String(p.printerId) === selectedPrinter.value)
  if (!printer) return ''

  const reasons = []

  // 检查颜色需求
  if (printSettings.value.color === 'color' && !printer.supportColor) {
    reasons.push('该打印机不支持彩色打印')
  }

  // 检查纸张类型
  const supportedPaperTypes = printer.paperType
    ? printer.paperType.split(',').map((type) => type.trim())
    : ['A4']
  if (!supportedPaperTypes.includes(printSettings.value.paperSize)) {
    reasons.push(`该打印机不支持${printSettings.value.paperSize}纸张`)
  }
  
  // 检查双面打印需求
  if (printSettings.value.duplex && !printer.supportDuplex) {
    reasons.push('该打印机不支持双面打印')
  }
  
  // 检查纸张数量是否足够
  if (requiredPaperCount.value && printer.paperCount < requiredPaperCount.value) {
    reasons.push(`打印机纸张不足(需要${requiredPaperCount.value}张，剩余${printer.paperCount}张)`)
  }

  return reasons.join('；')
})

// 轮询获取文档页数信息
const pollDocumentPageCount = async () => {
  if (!orderPreToken.value) return

  try {
    loadingPageCount.value = true
    const response = await getOrderPreBalance(orderPreToken.value)

    // 检查返回值是否为自然数
    if (typeof response.pageCount === 'number' && response.pageCount > 0) {
      documentPageCount.value = response.pageCount
      // 停止轮询
      if (pollInterval.value) {
        clearInterval(pollInterval.value)
        pollInterval.value = null
      }
      loadingPageCount.value = false
    } else {
      console.log('文档页数计算中，继续轮询...')
    }
  } catch (error) {
    console.error('获取文档页数失败:', error)
  }
}

// 当获取到预处理token后开始轮询页数信息
watch(
  () => orderPreToken.value,
  (newToken) => {
    if (newToken) {
      // 立即执行一次
      pollDocumentPageCount()

      // 开始轮询，每3秒获取一次，直到成功获取页数
      if (!pollInterval.value) {
        pollInterval.value = window.setInterval(pollDocumentPageCount, 3000)
      }
    }
  },
)

// 组件卸载时清除定时器
onUnmounted(() => {
  if (pollInterval.value) {
    clearInterval(pollInterval.value)
  }
})

// 计算总价格
const totalPrice = computed(() => {
  // 如果页数未获取到，返回加载状态或0
  if (documentPageCount.value === null) {
    return loadingPageCount.value ? '正在核算中...' : 0
  }

  // 如果没有选择打印机，返回加载状态
  if (!selectedPrinter.value) {
    return '请选择打印机'
  }

  // 查找选中的打印机
  const printer = printers.value.find((p) => String(p.printerId) === selectedPrinter.value)
  if (!printer) return 0

  // 基础价格（元/页）
  const basePrice = printSettings.value.color === 'color' ? 0.5 : 0.2

  // 双面打印折扣
  const duplexDiscount = printSettings.value.duplex ? 0.9 : 1.0

  // 特惠打印机 五折
  const printerDiscount = printer.printerDiscount === 0 ? 0.5 : 1.0

  // 计算总价: 页数 * 份数 * 单价 * 双面打印折扣
  return (
    documentPageCount.value *
    printSettings.value.copies *
    basePrice *
    duplexDiscount *
    printerDiscount
  )
})

// 搜索打印机
const onSearch = () => {
  console.log('搜索打印机:', searchPrinter.value)
}

// 获取订单预处理token
const fetchOrderPreToken = async () => {
  try {
    const files = orderFileStore.getOrderFile()
    if (files.length === 0) {
      showToast('请先上传文件')
      router.push({ name: 'OrderStep1' })
      return
    }

    // 文件IDs直接来自store并转换为字符串
    const fileIdsString = files.join(',')
    const response = await getOrderPreToken({ fileIds: fileIdsString })
    orderPreToken.value = response.token
    console.log('获取订单预处理token成功:', orderPreToken.value)
  } catch (error) {
    console.error('获取订单预处理token失败:', error)
    showToast('获取订单信息失败，请稍后重试')
  }
}

// 页面加载时请求预处理token和打印机列表
onMounted(() => {
  fetchOrderPreToken()
  fetchPrinters() // 加载打印机列表
})

// 显示打印机详情
const showPrinterDetails = (printer: PrinterResource) => {
  selectedPrinterDetails.value = printer
  showPrinterDialog.value = true
}

// 返回上一步
const goBack = () => {
  router.push({
    name: 'OrderStep1',
  })
}

// 创建订单
const createOrder = () => {
  if (orderFileStore.getOrderFile().length === 0) {
    showToast('请先上传文件')
    return
  }

  if (!orderPreToken.value) {
    showToast('订单信息加载失败，请刷新页面重试')
    return
  }

  // 在真实场景中，这里会调用API创建订单
  console.log('创建订单:', {
    printerId: selectedPrinter.value, // 选择的打印机ID
    fileIds: orderFileStore.getOrderFile(), // 上传的文件IDs
    paperSize: printSettings.value.paperSize, // 纸张大小
    color: printSettings.value.color === 'color', // 是否彩色打印
    copies: printSettings.value.copies, // 打印份数
    duplex: printSettings.value.duplex, // 是否双面打印
    remark: remark.value, // 备注信息
    token: orderPreToken.value, // 使用预处理token
  })

  // 显示成功提示
  showSuccessToast('订单创建成功')

  // 跳转到订单列表页
  router.push('/orders')
}

const orderFileCount = computed(() => {
  return orderFileStore.getOrderFile().length
})

const printRequirements = computed(() => ({
  color: printSettings.value.color,
  paperSize: printSettings.value.paperSize,
  copies: printSettings.value.copies,
  duplex: printSettings.value.duplex,
  requiredPaperCount: requiredPaperCount.value // 添加纸张数量需求
}))
</script>

<style scoped>
/* 可添加自定义样式 */
</style>
