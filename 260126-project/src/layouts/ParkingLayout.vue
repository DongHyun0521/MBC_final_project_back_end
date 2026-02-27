<template>
  <div class="parking-layout">
    <header class="parking-header">
      <div class="header-inner">
        <h2 class="header-title">🅿️ 주차 현황판</h2>
        <nav class="header-nav">
          <button
            :class="{ active: currentView === 'status' }"
            @click="goTo('ParkingStatus')"
          >현황판</button>
          <button
            :class="{ active: currentView === 'search' }"
            @click="goTo('VehicleSearch')"
          >차량검색</button>
        </nav>
      </div>
    </header>

    <main class="parking-main">
      <router-view />
    </main>

    <footer class="parking-footer">
      <p>S-HOSPITAL 주차관제시스템</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route  = useRoute();

const currentView = computed(() => {
  if (route.name === 'VehicleSearch') return 'search';
  return 'status';
});

const goTo = (name) => router.push({ name });
</script>

<style scoped>
.parking-layout {
  display: flex; flex-direction: column;
  height: 100vh; width: 100%;
  background: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.parking-header {
  background: #1a1a2e; color: #fff;
  padding: 0 24px; height: 56px;
  display: flex; align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.header-inner {
  width: 100%; max-width: 960px; margin: 0 auto;
  display: flex; justify-content: space-between; align-items: center;
}
.header-title { font-size: 18px; font-weight: 700; letter-spacing: -0.5px; }

.header-nav { display: flex; gap: 8px; }
.header-nav button {
  padding: 7px 18px; border: 1.5px solid rgba(255,255,255,0.3);
  border-radius: 6px; background: transparent; color: #ccc;
  font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s;
}
.header-nav button:hover { border-color: #fff; color: #fff; }
.header-nav button.active {
  background: #007AFF; border-color: #007AFF; color: #fff;
}

.parking-main {
  flex: 1; overflow-y: auto;
  padding: 20px;
}

.parking-footer {
  background: #1a1a2e; padding: 12px;
  text-align: center; font-size: 12px; color: #666;
}
</style>
