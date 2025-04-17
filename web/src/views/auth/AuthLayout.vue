<template>
  <div class="min-h-screen bg-gray-100">
    <router-view v-slot="{ Component }">
      <transition 
        enter-active-class="transition-opacity duration-300 ease-in-out" 
        leave-active-class="transition-opacity duration-300 ease-in-out"
        enter-from-class="opacity-0"
        leave-to-class="opacity-0"
        mode="out-in"
      >
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>
