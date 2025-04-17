import './assets/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vant from 'vant'
import { Lazyload } from 'vant'
import 'vant/lib/index.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册 Vant 组件
app.use(Vant)
// Lazyload 指令需要单独进行注册
app.use(Lazyload)
app.use(createPinia())
app.use(router)
app.mount('#app')
