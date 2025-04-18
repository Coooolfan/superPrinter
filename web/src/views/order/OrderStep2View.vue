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
        <div class="text-lg font-medium mb-3">上传文件 (3)</div>
        <div class="flex items-center justify-between py-2">
          <div class="flex items-center">
            <van-icon name="description" size="20" class="mr-2 text-primary" />
            <span class="text-sm">3个文件</span>
          </div>
          <van-button size="small" plain hairline type="primary" @click="goToStep1"
            >修改</van-button
          >
        </div>
      </div>

      <!-- 打印设置概览 -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-4">
        <div class="text-lg font-medium mb-3">打印设置</div>
        <div class="flex justify-between py-1">
          <span class="text-gray-600">打印颜色</span>
          <span>黑白</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-gray-600">纸张大小</span>
          <span>A4</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-gray-600">打印份数</span>
          <span>1</span>
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

            <!-- 打印机详情按钮 -->
            <div class="mt-2 text-right">
              <van-button size="mini" plain hairline @click="showPrinterDetails(printer)">
                详情
              </van-button>
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

    <!-- 打印机详情弹窗 -->
    <van-dialog v-model:show="showPrinterDialog" title="打印机详情" :showConfirmButton="false">
      <div class="p-4" v-if="selectedPrinterDetails">
        <div class="mb-3">
          <div class="text-lg font-medium">{{ selectedPrinterDetails.name }}</div>
          <div class="text-sm text-gray-500">
            <van-icon name="location-o" /> {{ selectedPrinterDetails.location }}
          </div>
        </div>

        <van-divider />

        <div class="mb-2 text-sm">
          <div class="flex justify-between py-1">
            <span class="text-gray-600">型号</span>
            <span>{{ selectedPrinterDetails.model }}</span>
          </div>
          <div class="flex justify-between py-1">
            <span class="text-gray-600">彩色打印</span>
            <span>{{ selectedPrinterDetails.color ? '支持' : '不支持' }}</span>
          </div>
          <div class="flex justify-between py-1">
            <span class="text-gray-600">双面打印</span>
            <span>{{ selectedPrinterDetails.duplex ? '支持' : '不支持' }}</span>
          </div>
          <div class="flex justify-between py-1">
            <span class="text-gray-600">支持纸张</span>
            <span>{{ selectedPrinterDetails.paperSizes.join(', ') }}</span>
          </div>
          <div class="flex justify-between py-1">
            <span class="text-gray-600">状态</span>
            <span>{{ selectedPrinterDetails.availability ? '空闲' : '忙碌' }}</span>
          </div>
        </div>

        <van-divider />

        <div class="py-2">
          <div class="text-sm font-medium mb-2">价格</div>
          <div class="text-xs text-gray-600">
            <div class="flex justify-between py-1">
              <span>黑白单面</span>
              <span>¥{{ selectedPrinterDetails.prices.bw }} / 页</span>
            </div>
            <div class="flex justify-between py-1">
              <span>彩色单面</span>
              <span>¥{{ selectedPrinterDetails.prices.color }} / 页</span>
            </div>
          </div>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'

const router = useRouter()
const searchPrinter = ref('')
const selectedPrinter = ref('')
const remark = ref('')
const showPrinterDialog = ref(false)
const selectedPrinterDetails = ref<any>(null)

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

// 返回修改文件
const goToStep1 = () => {
  router.push({
    name: 'OrderStep1',
  })
}

// 创建订单
const createOrder = () => {
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
</script>

<style scoped>
/* 可添加自定义样式 */
</style>
