<template>
  <div class="quick-menu-wrapper">
    <div class="quick-menu-container">

      <h1>주요 서비스</h1>

      <div class="card-row">
        <div class="q-card" @click="$emit('reserve')">
          <div class="card-icon-img">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="0.8"
              stroke-linecap="round" stroke-linejoin="round">
              <rect width="18" height="18" x="3" y="4" rx="2" ry="2" />
              <line x1="16" x2="16" y1="2" y2="6" />
              <line x1="8" x2="8" y1="2" y2="6" />
              <line x1="3" x2="21" y1="10" y2="10" />
              <line x1="12" x2="12" y1="14" y2="20" />
              <line x1="9" x2="15" y1="17" y2="17" />
            </svg>
          </div>
          <h4>진료예약</h4>
          <span class="hover-tip">바로가기 ></span>
        </div>
        <div class="q-card" @click="$emit('doctor')">
          <div class="card-icon-img">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="0.8"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <h4>의료진찾기</h4>
          <span class="hover-tip">바로가기 ></span>
        </div>
        <div class="q-card" @click="$emit('dept')">
          <div class="card-icon-img">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="0.8"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 14 4-4" />
              <path d="M3.34 17a10 10 0 1 1 17.32 0" />
              <path d="M12 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2Z" />
              <path d="M12 14v7" />
              <path d="M8 21h8" />
            </svg>
          </div>
          <h4>진료과찾기</h4>
          <span class="hover-tip">바로가기 ></span>
        </div>
        <div class="q-card" @click="$emit('check')">
          <div class="card-icon-img">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="0.8"
              stroke-linecap="round" stroke-linejoin="round">
              <rect width="18" height="18" x="3" y="4" rx="2" ry="2" />
              <path d="m9 16 2 2 4-4" />
              <path d="M3 10h18" />
            </svg>
          </div>
          <h4>예약조회</h4>
          <span class="hover-tip">바로가기 ></span>
        </div>
      </div>

      <div class="util-btn-row">
        <button class="util-btn main-blue">
          <div class="btn-left">
            <span class="btn-txt">증명서(진단서, 소견서 등) 출력</span>
          </div>
          <div class="btn-right">
            <span class="btn-icon"></span>
            <span class="arrow">→</span>
          </div>
        </button>

        <button class="util-btn or-blue">
          <div class="btn-left">
            <span class="btn-txt emphasis">건강검진 안내 (예약/결과)</span>
          </div>
          <div class="btn-right">
            <span class="btn-icon"></span>
            <span class="arrow dark">→</span>
          </div>
        </button>

        <button class="util-btn sky-blue">
          <div class="btn-left">
            <span class="btn-txt">입/퇴원 안내 및 서류 발급</span>
          </div>
          <div class="btn-right">
            <span class="btn-icon"></span>
            <span class="arrow">→</span>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

defineEmits(['reserve', 'doctor', 'dept', 'check'])

// [스크롤 버그 해결] 진입 시 화면을 맨 위로 강제로 올림
onMounted(() => {
  window.scrollTo(0, 0)
})
</script>

<style scoped>
/* .quick-menu-wrapper {
  width: 100%;
  border-bottom: 1px solid #eeeeee;
  margin: 30px 0;
} */

.quick-menu-container {
  font-family: 'pretendard';
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 90px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.quick-menu-container h1 {
  font-size: 39px;
  font-weight: 700;
  color: #666;
  text-align: center;
}

/* 상단 카드 스타일 */
.card-row {
  display: flex;
  gap: 20px;
  width: 100%;
}

.q-card {
  flex: 1;
  background: #eeeeee31;
  height: 200px;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  position: relative;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  color: #444;
}

.q-card h4 {
  font-size: 20px;
  font-weight: 500;
  color: inherit;
  margin-top: 15px;
  transition: 0.3s;
}

.q-card:hover {
  border-color: #757575;
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(72, 72, 72, 0.2);
  color: #444;
}

.hover-tip {
  position: absolute;
  bottom: -20px;
  font-size: 11px;
  color: #757575;
  font-weight: 600;
  transition: 0.3s;
  opacity: 0;
}

.q-card:hover .hover-tip {
  bottom: 20px;
  opacity: 1;
}

.card-icon-img {
  color:#fbb800bb; 
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 하단 유틸리티 버튼*/
.util-btn-row {
  display: flex;
  gap: 15px;
  width: 100%;
}

.util-btn {
  font-family: 'pretendard';
  flex: 1;
  height: 90px;
  border: none;
  display: flex;
  border-radius: 8px;
  justify-content: space-between;
  align-items: center;
  padding: 0 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.btn-txt {
  color: #ffffff;
  font-size: 18px;
  font-weight: 400;
  text-align: left;
}

.btn-txt.emphasis {
  color: #fff;
  font-weight: 500;
}

.btn-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-icon {
  font-size: 24px;
  opacity: 0.9;
  transition: 0.3s;
}

.arrow {
  color: #ffffff;
  font-size: 18px;
  font-weight: 300;
  opacity: 0.5;
  transition: 0.3s;
}

.arrow.dark {
  color: #404347;
}

.main-blue {
  background-color: #005baa;
}

.or-blue {
  background-color: #84c2e6
}

.sky-blue {
  background-color: #005baa;
}

.util-btn:hover {
  transform: scale(1.02);
  filter: brightness(1.08);
  box-shadow: 0 8px 20px rgba(1, 113, 233, 0.2);
}

.util-btn:hover .arrow {
  opacity: 1;
  transform: translateX(5px);
}

.util-btn:hover .btn-icon {
  transform: scale(1.1);
}
</style>