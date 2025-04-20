<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
        v-model="formState.printerName"
        name="printerName"
        label="打印机名称"
        placeholder="请输入打印机名称"
        :rules="[{ required: true, message: '请输入打印机名称' }]"
      />
      <van-field
        v-model="formState.paperCount"
        name="paperCount"
        label="纸张数量"
        type="digit"
        placeholder="请输入纸张数量"
        :rules="[{ required: true, message: '请输入纸张数量' }]"
      />
      <van-field name="paperType" label="纸张类型">
        <template #input>
          <van-checkbox-group v-model="paperTypeChecked" direction="horizontal">
            <van-checkbox name="A4">A4</van-checkbox>
            <van-checkbox name="A5">A5</van-checkbox>
            <van-checkbox name="B5">B5</van-checkbox>
          </van-checkbox-group>
        </template>
      </van-field>
      <van-field name="supportColor" label="彩色打印">
        <template #input>
          <van-switch v-model="formState.supportColor" />
        </template>
      </van-field>
      <van-field name="supportDuplex" label="双面打印">
        <template #input>
          <van-switch v-model="formState.supportDuplex" />
        </template>
      </van-field>
      <van-field name="status" label="打印机状态">
        <template #input>
          <van-radio-group v-model="formState.status" direction="horizontal">
            <van-radio name="ONLINE">在线</van-radio>
            <van-radio name="OFFLINE">离线</van-radio>
            <van-radio name="OUT_OF_PAPER">缺纸</van-radio>
          </van-radio-group>
        </template>
      </van-field>
    </van-cell-group>
    <div class="mt-4 px-4">
      <van-button round block type="primary" native-type="submit">
        {{ isEdit ? '更新' : '添加' }}
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import { reactive, defineProps, defineEmits, watchEffect, ref, computed } from 'vue'
import { type PrinterResource } from '@/api/printer'

const props = defineProps<{
  printer?: PrinterResource
  isEdit: boolean
}>()

const emit = defineEmits<{
  (e: 'submit', printer: PrinterResource): void
}>()

const paperTypeChecked = ref<string[]>([])

const formState = reactive<PrinterResource>({
  printerName: '',
  paperCount: 0,
  status: 'ONLINE',
  supportColor: false,
  supportDuplex: false,
  paperType: '',
})

// 编辑模式下，填充表单数据
watchEffect(() => {
  if (props.printer && props.isEdit) {
    formState.printerName = props.printer.printerName
    formState.paperCount = props.printer.paperCount
    formState.status = props.printer.status
    formState.supportColor = props.printer.supportColor || false
    formState.supportDuplex = props.printer.supportDuplex || false
    
    // 处理paperType字符串到复选框数组
    if (props.printer.paperType) {
      paperTypeChecked.value = props.printer.paperType.split(',')
    } else {
      paperTypeChecked.value = []
    }
  }
})

// 监听paperTypeChecked变化，更新formState.paperType
watchEffect(() => {
  formState.paperType = paperTypeChecked.value.join(',')
})

// 提交表单
const onSubmit = () => {
  const printerData: PrinterResource = {
    printerName: formState.printerName,
    paperCount: Number(formState.paperCount),
    status: formState.status,
    supportColor: formState.supportColor,
    supportDuplex: formState.supportDuplex,
    paperType: paperTypeChecked.value.join(','),
  }

  // 如果是编辑模式，保留原有的printerId
  if (props.isEdit && props.printer?.printerId) {
    printerData.printerId = props.printer.printerId
  }

  emit('submit', printerData)
}
</script>
