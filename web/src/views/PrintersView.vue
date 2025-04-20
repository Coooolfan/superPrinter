<template>
  <div class="min-h-screen bg-[#f5f7fa]">
    <div class="sticky top-0 z-10 flex items-center justify-between bg-white p-4 shadow-sm">
      <h1 class="m-0 text-lg font-semibold text-[#323233]">打印机管理</h1>
      <van-button type="primary" size="small" icon="plus" @click="showAddPrinter"
        >添加打印机</van-button
      >
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" class="min-h-screen pb-[70px]">
      <div class="p-3">
        <div v-if="loading" class="flex justify-center py-10">
          <van-loading type="spinner" color="#1989fa" size="24" />
        </div>
        <template v-else-if="printers.length > 0">
          <PrinterCard
            v-for="printer in printers"
            :key="printer.printerId"
            :printer="printer"
            @edit="onEditPrinter"
            @delete="onDeletePrinter(printer)"
          />
        </template>
        <van-empty v-else description="暂无打印机" />
      </div>
    </van-pull-refresh>

    <!-- 添加/编辑打印机弹窗 -->
    <van-popup v-model:show="formVisible" position="bottom" round :style="{ height: '60%' }">
      <div class="p-4">
        <div class="mb-4 flex items-center justify-between">
          <h2 class="m-0 text-lg font-medium">{{ isEdit ? '编辑打印机' : '添加打印机' }}</h2>
          <van-icon name="cross" @click="formVisible = false" class="cursor-pointer text-lg" />
        </div>
        <PrinterForm :printer="currentPrinter" :is-edit="isEdit" @submit="handleSubmit" />
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { getPrinterList, addPrinter, updatePrinter, deletePrinter } from '@/api/printer'
import type { PrinterResource } from '@/api/printer'
import PrinterCard from '@/components/common/PrinterCard.vue'
import PrinterForm from '@/components/printer/PrinterForm.vue'

interface Printer {
  printerId: number
  printerName: string
  status: string
  supportColor: boolean
  supportDuplex: boolean
  paperType: string // 以逗号分隔的字符串 A4,A5
  paperCount: number
  createTime: string
  updateTime: string
}

const printers = ref<Printer[]>([])
const loading = ref(true)
const refreshing = ref(false)
const formVisible = ref(false)
const isEdit = ref(false)
const currentPrinter = ref<PrinterResource | undefined>(undefined)

// 获取打印机列表
const fetchPrinters = async () => {
  try {
    loading.value = true
    const res = (await getPrinterList()) as any
    printers.value = res || []
    loading.value = false
    refreshing.value = false
  } catch (error) {
    console.error('获取打印机列表失败:', error)
    showToast('获取打印机列表失败')
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  fetchPrinters()
}

// 显示添加打印机弹窗
const showAddPrinter = () => {
  isEdit.value = false
  currentPrinter.value = undefined
  formVisible.value = true
}

// 显示编辑打印机弹窗
const onEditPrinter = (printer: Printer) => {
  isEdit.value = true
  currentPrinter.value = {
    printerId: printer.printerId,
    printerName: printer.printerName,
    status: printer.status,
    paperCount: printer.paperCount,
    supportColor: printer.supportColor,
    supportDuplex: printer.supportDuplex,
    paperType: printer.paperType
  }
  formVisible.value = true
}

// 处理表单提交
const handleSubmit = async (printerData: PrinterResource) => {
  try {
    if (isEdit.value && printerData.printerId) {
      // 更新打印机
      await updatePrinter(printerData.printerId, printerData)
      showToast('更新成功')
    } else {
      // 添加打印机
      await addPrinter(printerData)
      showToast('添加成功')
    }
    formVisible.value = false
    fetchPrinters()
  } catch (error) {
    console.error(`${isEdit.value ? '更新' : '添加'}打印机失败:`, error)
    showToast(`${isEdit.value ? '更新' : '添加'}打印机失败`)
  }
}

// 删除打印机
const onDeletePrinter = async (printer: Printer) => {
  try {
    const result = await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除打印机 "${printer.printerName}" 吗？`,
      confirmButtonText: '删除',
      confirmButtonColor: '#ee0a24',
    })

    if (result === 'confirm') {
      console.log(printer)
      await deletePrinter(printer.printerId)
      showToast('删除成功')
      fetchPrinters()
    }
  } catch (error) {
    console.error('删除打印机失败:', error)
    showToast('删除打印机失败')
  }
}

onMounted(() => {
  fetchPrinters()
})
</script>
