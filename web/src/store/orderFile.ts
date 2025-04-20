import { defineStore } from 'pinia'
import { ref } from 'vue'

export default defineStore('orderFile', () => {
  const orderFile = ref<number[]>([])
  const addOrderFile = (fileId: number) => {
    orderFile.value.push(fileId)
  }
  const removeOrderFile = (fileId: number) => {
    orderFile.value = orderFile.value.filter((id) => id !== fileId)
  }
  const getOrderFile = () => {
    return orderFile.value
  }
  const clearOrderFile = () => {
    orderFile.value = []
  }
  return { orderFile, addOrderFile, removeOrderFile, clearOrderFile, getOrderFile }
})
