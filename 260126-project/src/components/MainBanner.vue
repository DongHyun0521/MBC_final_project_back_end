<template>
  <div class="banner-wrapper">
    <section 
      class="hero-section" 
      @mouseenter="stopSlide" 
      @mouseleave="handleMouseLeave"
      @mousedown="startDrag"
      @touchstart="startDrag"
      @mouseup="endDrag"
      @touchend="endDrag"
    >
      
      <div 
        v-for="(slide, index) in slides" :key="index" class="slide-item" :class="{ active: currentSlide === index }" :style="{ backgroundImage: `url(${slide.image})` }">
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <h2 class="hero-title">{{ slide.title }}</h2>
          <p class="hero-subtitle">{{ slide.subtitle }}</p>
        </div>
      </div>

      <button class="nav-btn prev" @click="prevSlide">
        <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M15 18l-6-6 6-6"/></svg>
      </button>
      <button class="nav-btn next" @click="nextSlide">
        <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 18l6-6-6-6"/></svg>
      </button>

      <div class="slide-indicators">
        <span v-for="(slide, index) in slides" :key="index" :class="{ active: currentSlide === index }" @click="currentSlide = index"></span>
      </div>
    </section>

    <section class="search-bar-section">
      <div class="search-inner">
        <span class="search-label">통합검색</span>
        <div class="search-input-box">
          <input type="text" v-model="totalSearchQuery" placeholder="검색어를 입력해 주세요" @keyup.enter="onSearch">
          <button class="search-btn" @click="onSearch">
            <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

import img1 from '@/assets/MainImg.jpg'
import img2 from '@/assets/MainImg2.jpg'
import img3 from '@/assets/MainImg5.jpg'
import img4 from '@/assets/MainImg3.jpg'
import img5 from '@/assets/MainImg4.jpg'

const router = useRouter()
const totalSearchQuery = ref('')

const currentSlide = ref(0)
const slides = [
  { image: img1, title: '당신의 건강과 편안한 방문을 위해', subtitle: '최고의 의료진이 365일 함께합니다' },
  { image: img2, title: '첨단 의료 장비와 따뜻한 케어', subtitle: '환자 중심의 맞춤형 진료 서비스' },
  { image: img3, title: '건강한 미래를 여는 서울에스병원', subtitle: '연구와 혁신으로 의학을 선도합니다' },
  { image: img4, title: '당신의 든든한 평생 건강 파트너', subtitle: '가족을 대하는 마음으로 정성을 다합니다' },
  { image: img5, title: '미래 의료를 선도하는 스마트 병원', subtitle: '과학적인 데이터 분석과 혁신적인 의료 시스템' }
]

// 자동 슬라이드 타이머
let slideInterval = null

// [드래그/스와이프] 좌표 저장용 변수
const touchStartX = ref(0)
const touchEndX = ref(0)

// 다음/이전 슬라이드 함수
const nextSlide = () => { currentSlide.value = (currentSlide.value + 1) % slides.length }
const prevSlide = () => { currentSlide.value = currentSlide.value === 0 ? slides.length - 1 : currentSlide.value - 1 }

// 타이머 제어
const startSlide = () => { if (!slideInterval) slideInterval = setInterval(nextSlide, 4000) }
const stopSlide = () => { if (slideInterval) { clearInterval(slideInterval); slideInterval = null } }

// [추가] 드래그 시작 (마우스 누름 / 터치 시작)
const startDrag = (e) => {
  stopSlide() // 드래그 중엔 자동 넘김 정지
  // 마우스 이벤트면 e.clientX, 터치 이벤트면 e.touches[0].clientX 사용
  touchStartX.value = e.type.includes('mouse') ? e.clientX : e.touches[0].clientX
}

// [추가] 드래그 끝 (마우스 뗌 / 터치 끝)
const endDrag = (e) => {
  // 마우스 뗐을 때 좌표 or 터치 끝났을 때 좌표
  touchEndX.value = e.type.includes('mouse') ? e.clientX : e.changedTouches[0].clientX
  handleSwipe() // 계산 시작!
  startSlide()  // 다시 자동 재생 시작
}

// [추가] 마우스가 배너 밖으로 나갔을 때 (드래그 취소 처리 겸 재시작)
const handleMouseLeave = () => {
  touchStartX.value = 0
  touchEndX.value = 0
  startSlide()
}

// [추가] 스와이프 계산 로직
const handleSwipe = () => {
  // 30px 이상 움직여야 의도적인 드래그로 판단 (너무 민감하지 않게)
  const threshold = 10 
  const diff = touchStartX.value - touchEndX.value

  if (diff > threshold) {
    // 시작(우) > 끝(좌) : 왼쪽으로 밀었음 -> 다음 슬라이드
    nextSlide()
  } else if (diff < -threshold) {
    // 시작(좌) < 끝(우) : 오른쪽으로 밀었음 -> 이전 슬라이드
    prevSlide()
  }
}

onMounted(() => { startSlide() })
onUnmounted(() => { stopSlide() })

const onSearch = () => {
  if (!totalSearchQuery.value.trim()) { alert('검색어를 입력해 주세요'); return }
  router.push({ path: '/search', query: { q: totalSearchQuery.value } })
}
</script>

<style scoped>
.banner-wrapper {
  width: 100%;
}

.hero-section {
  width: 100%;
  height: 470px;
  position: relative;
  overflow: hidden;
  cursor: grab; /* ★ 마우스 올리면 '잡을 수 있다'는 손바닥 모양 */
  user-select: none; /* 드래그할 때 텍스트 선택 방지 */
}

/* 드래그 중일 때 커서 모양 (선택사항) */
.hero-section:active {
  cursor: grabbing; /* 잡은 손 모양 */
}

.slide-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center -58px;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 0.6s ease-in-out;
  display: flex;
  align-items: center;
  padding: 0 10%;
  z-index: 1;
}

.slide-item.active {
  opacity: 1;
  z-index: 2;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  pointer-events: none; /* 글자 위에서도 드래그 되게 하려면 클릭 이벤트 통과시키기 */
}

.hero-title {
  font-family: 'pretendard';
  color: white;
  font-size: 38px;
  font-weight: 700;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 8px;
}

.hero-subtitle {
    font-family: 'pretendard';
  color: white;
  font-size: 26px;
  font-weight: 400;
  text-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
}

/* 화살표 버튼 */
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  z-index: 10;
  transition: color 0.2s, transform 0.2s;
  padding: 10px;
}

.nav-btn:hover {
  color: #fff;
  transform: translateY(-50%) scale(1.1);
}

.prev { left: 20px; }
.next { right: 20px; }

/* 인디케이터 */
.slide-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  display: flex;
  gap: 10px;
}

.slide-indicators span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: 0.3s;
}

.slide-indicators span.active {
  background: #fff;
  width: 30px;
  border-radius: 10px;
}

/* 검색바 섹션 */
.search-bar-section {
  width: 100%;
  background-color: #005BAA;
  padding: 20px 0;
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 20;
}

.search-inner {
  width: 100%;
  max-width: 1100px;
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 0 20px;
}

.search-label {
  color: white;
  font-size: 18px;
  font-weight: 500;
  font-family: 'pretendard';
  white-space: nowrap;
}

.search-input-box {
  position: relative;
  flex: 1;
}

.search-input-box input {
  width: 100%;
  padding: 12px 25px;
  padding-right: 60px;
  border-radius: 40px;
  border: none;
  font-size: 17px;
  outline: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  font-family: 'pretendard';
}

.search-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-52%);
  background: white;
  color: #15487e;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}

.search-btn:hover {
  background: #0056b3;
  color: white;
}
</style>