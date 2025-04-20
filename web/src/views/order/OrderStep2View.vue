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

        <div class="mb-4">
          <div class="mb-2">打印颜色</div>
          <van-radio-group v-model="printSettings.color" direction="horizontal">
            <van-radio name="black" class="mr-4">黑白</van-radio>
            <van-radio name="color">彩色</van-radio>
          </van-radio-group>
        </div>

        <div class="mb-4">
          <div class="mb-2">纸张大小</div>
          <van-dropdown-menu>
            <van-dropdown-item v-model="printSettings.paperSize" :options="paperSizeOptions" />
          </van-dropdown-menu>
        </div>

        <div>
          <div class="mb-2">打印份数</div>
          <van-stepper v-model="printSettings.copies" min="1" max="100" />
        </div>
      </div>

      <!-- 选择打印机 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">选择打印机</div>

        <!-- 打印机搜索 -->
        <van-search v-model="searchPrinter" placeholder="搜索打印机名称或位置" @search="onSearch" />

        <!-- 打印机列表 -->
        <van-radio-group v-model="selectedPrinter">
          <div
            v-for="(printer, index) in filteredPrinters"
            :key="index"
            class="p-3 border-b border-gray-100"
          >
            <div class="flex items-center">
              <van-radio :name="printer.id" class="flex-1">
                <div class="ml-1">
                  <div class="font-medium">{{ printer.name }}</div>
                  <div class="text-xs text-gray-500 mt-1">
                    <van-icon name="location-o" size="12" /> {{ printer.location }}
                  </div>
                  <div class="flex mt-2">
                    <van-tag type="primary" plain class="mr-1">{{ printer.type }}</van-tag>
                    <van-tag v-if="printer.color" type="success" plain class="mr-1">彩色</van-tag>
                    <van-tag v-if="printer.availability" type="success">空闲</van-tag>
                    <van-tag v-else type="danger">忙碌</van-tag>
                  </div>
                </div>
              </van-radio>
            </div>
          </div>
        </van-radio-group>

        <!-- 无打印机提示 -->
        <div v-if="filteredPrinters.length === 0" class="py-4 text-center text-gray-500">
          没有找到符合条件的打印机
        </div>
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
        <div>
          <span class="text-gray-600 mr-2">预估费用:</span>
          <span class="text-xl font-bold text-red-500">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <van-button type="primary" :disabled="!selectedPrinter" @click="createOrder">
          创建订单
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import useOrderFileStore from '@/store/orderFile'

const orderFileStore = useOrderFileStore()
const router = useRouter()
const searchPrinter = ref('')
const selectedPrinter = ref('')
const remark = ref('')
const showPrinterDialog = ref(false)
const selectedPrinterDetails = ref<any>(null)
// 打印设置
const printSettings = ref({
  color: 'black',
  paperSize: 'A4',
  copies: 1,
})

// 纸张大小选项
const paperSizeOptions = [
  { text: 'A4', value: 'A4' },
  { text: 'A5', value: 'A5' },
  { text: 'B5', value: 'B5' },
]
// 模拟打印机数据
const printers = ref([
  {
    id: '1',
    name: '图书馆一楼打印机',
    location: '中心图书馆一楼大厅',
    type: '激光打印机',
    model: 'HP LaserJet Pro M402n',
    color: false,
    duplex: true,
    paperSizes: ['A4', 'A5', 'B5'],
    availability: true,
    prices: {
      bw: 0.2,
      color: 0.5,
    },
  },
  {
    id: '2',
    name: '学生中心打印机',
    location: '学生活动中心一楼',
    type: '喷墨打印机',
    model: 'Epson L3150',
    color: true,
    duplex: true,
    paperSizes: ['A4', 'A5', '4R'],
    availability: true,
    prices: {
      bw: 0.2,
      color: 1.0,
    },
  },
  {
    id: '3',
    name: '教学楼打印机',
    location: '综合教学楼二楼走廊',
    type: '激光打印机',
    model: 'Canon imageRUNNER 2204N',
    color: false,
    duplex: true,
    paperSizes: ['A4', 'B5'],
    availability: false,
    prices: {
      bw: 0.15,
      color: 0.0,
    },
  },
])

// 筛选打印机
const filteredPrinters = computed(() => {
  if (!searchPrinter.value) return printers.value

  const keyword = searchPrinter.value.toLowerCase()
  return printers.value.filter(
    (printer) =>
      printer.name.toLowerCase().includes(keyword) ||
      printer.location.toLowerCase().includes(keyword),
  )
})

// 计算总价格（假设是3页黑白文档）
const totalPrice = computed(() => {
  if (!selectedPrinter.value) return 0

  const printer = printers.value.find((p) => p.id === selectedPrinter.value)
  if (!printer) return 0

  // 假设总共3页黑白文档
  return printer.prices.bw * 3
})

// 搜索打印机
const onSearch = () => {
  // 在真实情况下可能需要从后端获取数据
  console.log('搜索打印机:', searchPrinter.value)
}

// 显示打印机详情
const showPrinterDetails = (printer: any) => {
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
  // 在真实场景中，这里会调用API创建订单
  console.log('创建订单:', {
    printerId: selectedPrinter.value,
    remark: remark.value,
    // 其他订单信息
  })

  // 显示成功提示
  showSuccessToast('订单创建成功')

  // 跳转到订单列表页
  router.push('/orders')
}

const orderFileCount = computed(() => {
  return orderFileStore.getOrderFile().length
})
</script>

<style scoped>
/* 可添加自定义样式 */
</style>
