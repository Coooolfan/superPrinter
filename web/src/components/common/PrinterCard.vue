<template>
    <div class="mb-2 rounded-lg bg-white p-3 shadow-[0_2px_8px_rgba(0,0,0,0.06)] transition-all duration-300 ease-in-out"
        :class="[
            printer.status === 'ONLINE' ? 'border-l-4 border-l-[#07c160]' : '',
            printer.status === 'OFFLINE' ? 'border-l-4 border-l-[#ee0a24] opacity-80' : '',
            printer.status === 'OUT_OF_PAPER' ? 'border-l-4 border-l-[#ff976a]' : '',
        ]">
        <div class="mb-2 flex items-center">
            <div class="mr-2 h-2.5 w-2.5 rounded-full" :class="{
                'bg-[#07c160]': printer.status === 'ONLINE',
                'bg-[#ee0a24]': printer.status === 'OFFLINE',
                'bg-[#ff976a]': printer.status === 'OUT_OF_PAPER',
                'bg-[#969799]': !['ONLINE', 'OFFLINE', 'OUT_OF_PAPER'].includes(printer.status),
            }"></div>
            <h2 class="m-0 flex-1 text-base font-medium text-[#323233]">
                {{ printer.printerName }}
            </h2>
            <van-tag :type="getStatusType(printer.status)" size="medium"
                :class="[`status-tag-${printer.status.toLowerCase()}`]">
                {{ getStatusText(printer.status) }}
            </van-tag>
        </div>
        <div class="rounded-lg bg-[#f7f8fa] p-3">
            <div class="grid grid-cols-2 gap-x-3 gap-y-2">
                <div class="flex items-center text-sm">
                    <van-icon name="description" class="mr-1.5 text-base text-[#969799]" />
                    <span class="mr-1 text-[#646566]">纸张:</span>
                    <span class="font-medium text-[#323233]">{{ printer.paperCount }}</span>
                </div>
                <div class="flex items-center text-sm">
                    <van-icon name="label-o" class="mr-1.5 text-base text-[#969799]" />
                    <span class="mr-1 text-[#646566]">类型:</span>
                    <span class="font-medium text-[#323233]">{{ formatPaperType(printer.paperType) }}</span>
                </div>
                <div class="flex items-center text-sm">
                    <van-icon name="clock-o" class="mr-1.5 text-base text-[#969799]" />
                    <span class="mr-1 text-[#646566]">更新:</span>
                    <span class="font-medium text-[#323233]">{{ formatDateTime(printer.updateTime) }}</span>
                </div>
                <div class="flex flex-wrap gap-2">
                    <van-tag v-if="printer.supportColor" type="success" plain>彩色</van-tag>
                    <van-tag v-else class="bg-black text-white border-black">黑白</van-tag>
                    <van-tag v-if="printer.supportDuplex" type="warning" plain>双面</van-tag>
                    <van-tag v-else type="primary" plain>单面</van-tag>
                </div>
            </div>
        </div>
        <div class="mt-3 flex gap-3 justify-end">
            <van-button size="small" type="primary" @click="onEdit">编辑</van-button>
            <van-button size="small" type="danger" @click="onDelete">删除</van-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { TagType } from 'vant'

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

const props = defineProps<{
    printer: Printer
}>()

const emit = defineEmits<{
    (e: 'edit', printer: Printer): void
    (e: 'delete', printer: Printer): void
}>()

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        ONLINE: '在线',
        OFFLINE: '离线',
        OUT_OF_PAPER: '缺纸',
    }
    return statusMap[status] || '未知状态'
}

// 获取状态类型
const getStatusType = (status: string): TagType => {
    const typeMap: Record<string, TagType> = {
        ONLINE: 'success',
        OFFLINE: 'danger',
        OUT_OF_PAPER: 'warning',
    }
    return typeMap[status] || 'default'
}

// 格式化日期时间
const formatDateTime = (dateTimeStr: string) => {
    if (!dateTimeStr) return '-'
    const date = new Date(dateTimeStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 格式化纸张类型
const formatPaperType = (paperType: string) => {
    if (!paperType) return '-'
    return paperType.split(',').join(', ')
}

// 编辑打印机
const onEdit = () => {
    emit('edit', props.printer)
}

// 删除打印机
const onDelete = () => {
    emit('delete', props.printer)
}
</script>

<style scoped>
.status-tag-online {
    font-size: 12px;
}

.status-tag-offline {
    font-size: 12px;
}
</style>
