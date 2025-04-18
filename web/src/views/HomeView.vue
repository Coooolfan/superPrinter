<template>
  <div class="bg-gray-50 min-h-screen pb-40">
    <!-- 顶部导航栏 -->
    <van-nav-bar title="超级打印" fixed placeholder class="bg-white shadow-sm" />

    <!-- 轮播图 -->
    <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
      <van-swipe-item v-for="(image, index) in banners" :key="index">
        <img :src="image" class="w-full h-40 object-cover" />
      </van-swipe-item>
    </van-swipe>

    <!-- 服务分类 -->
    <div class="bg-white p-4 mt-3 rounded-lg shadow-sm mx-3">
      <div class="text-lg font-bold mb-3">打印服务</div>
      <van-grid :column-num="4" :border="false">
        <van-grid-item
          v-for="(item, index) in services"
          :key="index"
          :icon="item.icon"
          :text="item.text"
        />
      </van-grid>
    </div>

    <!-- 优惠活动 -->
    <div class="bg-white p-4 mt-3 rounded-lg shadow-sm mx-3">
      <div class="flex justify-between items-center mb-3">
        <span class="text-lg font-bold">优惠活动</span>
        <span class="text-sm text-gray-500">查看全部</span>
      </div>
      <van-card
        v-for="(promo, index) in promotions"
        :key="index"
        :price="promo.price"
        :desc="promo.desc"
        :title="promo.title"
        :thumb="promo.thumb"
        class="mb-3 shadow-sm"
      >
        <template #tags>
          <van-tag type="danger" v-if="promo.isHot">热门</van-tag>
        </template>
        <template #footer>
          <van-button size="small">了解详情</van-button>
        </template>
      </van-card>
    </div>

    <!-- 大型立即下单按钮 -->
    <div class="fixed bottom-20 inset-x-0 px-4 z-10">
      <van-button
        type="primary"
        block
        size="large"
        icon="orders-o"
        color="#07c160"
        class="h-14 rounded-full shadow-lg text-lg font-bold"
        @click="handleOrder"
      >
        立即下单
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 轮播图数据
const banners = ref([
  'https://img01.yzcdn.cn/vant/apple-1.jpg',
  'https://img01.yzcdn.cn/vant/apple-2.jpg',
  'https://img01.yzcdn.cn/vant/apple-3.jpg',
])

// 服务分类
const services = ref([
  { icon: 'photo-o', text: '文档打印' },
  { icon: 'photo-o', text: '照片冲印' },
  { icon: 'certificate', text: '证件打印' },
  { icon: 'gift-o', text: '礼品定制' },
  { icon: 'label-o', text: '海报制作' },
  { icon: 'desktop-o', text: '图文设计' },
  { icon: 'logistics', text: '配送服务' },
  { icon: 'more-o', text: '更多服务' },
])

// 优惠活动
const promotions = ref([
  {
    title: '学生专享打印套餐',
    desc: '黑白文档打印，单面0.1元/张起',
    price: '9.9',
    thumb: 'https://img01.yzcdn.cn/vant/ipad.jpeg',
    isHot: true,
  },
  {
    title: '照片冲印特惠',
    desc: '6寸照片冲印，每张低至0.5元',
    price: '19.9',
    thumb: 'https://img01.yzcdn.cn/vant/ipad.jpeg',
    isHot: false,
  },
])

// 跳转到下单页面
const handleOrder = () => {
  router.push({
    name: 'OrderStep1',
  })
}
</script>

<style scoped>
.my-swipe .van-swipe-item {
  color: #fff;
  font-size: 20px;
  height: 160px;
  text-align: center;
  background-color: #39a9ed;
}
</style>
