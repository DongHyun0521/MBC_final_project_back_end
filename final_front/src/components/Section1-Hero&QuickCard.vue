<template>
    <div class="wrapper">
        <section class="hero-section" @mouseenter="stopSlide" @mouseleave="handleMouseLeave" @mousedown="startDrag"
            @touchstart="startDrag" @mouseup="endDrag" @touchend="endDrag">

            <div v-for="(slide, index) in slides" :key="index" class="slide-item"
                :class="{ active: currentSlide === index }" :style="{ backgroundImage: `url(${slide.image})` }">
                <div class="hero-overlay"></div>
                <div class="hero-content">
                    <h2 class="hero-title">{{ slide.title }}</h2>
                    <p class="hero-subtitle">{{ slide.subtitle }}</p>
                </div>
            </div>

            <button class="nav-btn prev" @click="prevSlide">〈</button>
            <button class="nav-btn next" @click="nextSlide">〉</button>

            <div class="slide-indicators">
                <span v-for="(slide, index) in slides" :key="index" :class="{ active: currentSlide === index }"
                    @click="currentSlide = index"></span>
            </div>
        </section>

        <div class="quick-bar-bottom">
            <div class="q-bar-item maroon">
                <div class="base-content">
                    <h4>진료예약</h4>
                </div>
                <div class="hover-content">
                    <p>간편하게 온라인으로 <br> 진료를 예약하세요</p>
                    <button class="action-btn" @click="$emit('reserve')">예약하기</button>
                </div>
            </div>

            <div class="q-bar-item orange">
                <div class="base-content">
                    <h4>의료진찾기</h4>
                </div>
                <div class="hover-content">
                    <div class="search-box-mini">
                        <input type="text" v-model="doctorSearchQuery" placeholder="의료진 이름/진료과 입력"
                            @keyup.enter="onDoctorSearch">
                        <button @click="onDoctorSearch">검색</button>
                    </div>
                </div>
            </div>

            <div class="q-bar-item blue">
                <div class="base-content">
                    <h4>진료과찾기</h4>
                </div>
                <div class="hover-content">
                    <p>원하시는 진료과를 <br> 빠르게 찾아보세요</p>
                    <button class="action-btn" @click="$emit('dept')">전체보기</button>
                </div>
            </div>

            <div class="q-bar-item green">
                <div class="base-content">
                    <h4>예약조회</h4>
                </div>
                <div class="hover-content">
                    <p>예약 내역을 <br> 실시간으로 확인하세요</p>
                    <button class="action-btn" @click="$emit('check')">확인하기</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

import img1 from '@/assets/MainImg.jpg'
// import img2 from '@/assets/MainImg2.jpg'
import img2 from '@/assets/MainImg5.jpg'
// import img4 from '@/assets/MainImg3.jpg'
// import img5 from '@/assets/MainImg4.jpg'

const router = useRouter()
const totalSearchQuery = ref('')

const emit = defineEmits(['doctor', 'reserve', 'dept', 'check']) // 부모에게 보낼 신호 명단(MainHome)
const doctorSearchQuery = ref('') // 의료진 검색어를 담을 바구니 생성

const currentSlide = ref(0) // 값이 바뀌면 화면을 새로 그리도록
const slides = [
    {
        image: img1,
        title: '서울에스병원은',
        subtitle: `환자 안전을 최우선 가치로 삼아 전문의 중심의 협진 체계와 표준화된 진료 시스템을 통해
신뢰받는 의료 서비스를 제공하기 위해 노력하고 있습니다.`
    },
    {
        image: img2,
        title: '서울에스병원은',
        subtitle: `환자 안전을 최우선 가치로 삼아 전문의 중심의 협진 체계와 표준화된 진료 시스템을 통해
신뢰받는 의료 서비스를 제공하기 위해 노력하고 있습니다.`
    },
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
const startSlide = () => {
    if (!slideInterval) slideInterval = setInterval(nextSlide, 4000) // 4초마다 nextSlide 실행
}

const stopSlide = () => {
    if (slideInterval) {
        clearInterval(slideInterval);
        slideInterval = null
    }
}

// 드래그 시작 (마우스 누름 / 터치 시작)
const startDrag = (e) => {
    console.log('startDrag:', e)

    stopSlide() // 드래그 중엔 자동 넘김 정지
    // e.type.includes('mouse'): 마우스를 쓰는지 손가락(터치)을 쓰는지 판별해서 좌표 읽는 방식을 바꿈
    touchStartX.value = e.type.includes('mouse') ? e.clientX : e.touches[0].clientX
}

// 드래그 끝 (마우스 뗌 / 터치 끝)
const endDrag = (e) => {
    console.log('endDrag:', e)

    // 마우스 뗐을 때 좌표 or 터치 끝났을 때 좌표
    touchEndX.value = e.type.includes('mouse') ? e.clientX : e.changedTouches[0].clientX
    handleSwipe() // 계산 시작!
    startSlide()  // 다시 자동 재생 시작
}

// 마우스가 배너 밖으로 나갔을 때 (드래그 취소 처리 겸 재시작)
const handleMouseLeave = () => {
    touchStartX.value = 0
    touchEndX.value = 0
    startSlide()
}

// 스와이프 계산 로직
const handleSwipe = () => {
    // 10px 이상 움직여야 의도적인 드래그로 판단
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

// 의료진 검색(메인배너 두번째 탭)
const onDoctorSearch = () => {
    if (!doctorSearchQuery.value.trim()) {
        alert('검색어를 입력해주세요.');
        return;
    }
    // 부모(MainHome.vue)에게 검색어와 함께 신호를 쏘기
    emit('doctor', doctorSearchQuery.value);
}

onMounted(() => { startSlide() })
onUnmounted(() => { stopSlide() })
</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100vh;
    overflow: hidden;
}

.hero-section {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
}

.slide-item {
    position: absolute;
    inset: 0;
    background-size: cover;
    background-position: center;
    opacity: 0;
    transition: opacity 1s ease-in-out;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1;
}

.slide-item.active {
    opacity: 1;
    z-index: 2;
}

.hero-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.2);
    z-index: 3;
}

.hero-content {
    position: relative;
    z-index: 10;
    text-align: center;
    color: white;
    margin-bottom: 26px;
}

.hero-title {
    font-family: 'pretendard';
    font-size: 58px;
    font-weight: 700;
    margin-bottom: 12px;
    letter-spacing: -1.8px;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.659)
}

.hero-subtitle {
    font-family: 'pretendard';
    font-size: 28px;
    font-weight: 400;
    opacity: 0.9;
    letter-spacing: -1.5px;
    white-space: pre-line;
    line-height: 1.4;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.659);
}

/* 하단 바 전체 컨테이너 */
.quick-bar-bottom {
    position: absolute;
    bottom: 0;
    left: 148px;
    right: 148px;
    display: flex;
    align-items: flex-end;
    /* 아래쪽에서 위로 길어지게 설정 */
    z-index: 50;
}

/* 개별 바구니 스타일 */
.q-bar-item {
    flex: 1;
    height: 110px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    overflow: hidden;
    /* 높이가 낮을 때 내용물 숨기기 */
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
    color: white;
    padding: 38px 20px;
}

/* 핵심: 마우스 올리면 높이가 올라오도록 */
.q-bar-item:hover {
    height: 240px;
}

/* 평소 보이는 제목 */
.base-content h4 {
    font-family: 'pretendard';
    font-size: 30px;
    font-weight: 700;
    margin: 0;
    text-align: center;
}

/* 숨겨져 있다가 나타나는 내용물 */
.hover-content {
    font-family: 'pretendard';
    margin-top: 20px;
    text-align: center;
    opacity: 0;
    /* 평소엔 투명 */
    transition: opacity 0.3s 0.1s;
    /* 높이가 커진 후 나타나도록 0.1초 딜레이 */
    font-size: 20px;
    font-weight: 300;
}

.q-bar-item:hover .hover-content {
    opacity: 1;
    /* 호버 시 선명하게 */
}

/* 내부 버튼 및 검색창 스타일 */
.action-btn {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid #fff;
    color: #fff;
    font-family: 'pretendard';
    padding: 10px 30px;
    cursor: pointer;
    margin-top: 20px;
    font-size: 16px;
}

.search-box-mini {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.6);
    padding: 2px;
    /* 버튼과 박스 사이의 미세한 간격 */
    width: 100%;
    box-sizing: border-box;
}

.search-box-mini input {
    flex: 1;
    background: transparent;
    border: none;
    color: white;
    padding: 10px 15px;
    font-size: 15px;
    outline: none;
    min-width: 0;
}

.search-box-mini input::placeholder {
    color: rgba(255, 255, 255, 0.7);
    font-weight: 300;
}

.maroon {
    background-color: #3a7ab1;
}

/* 진료예약 */
.orange {
    background-color: #daa102;
}

/* 의료진찾기 */
.blue {
    background-color: #9d9d9d;
}

/* 진료과찾기 */
.green {
    background-color: #537a91;
}

/* 예약조회 */

/* 2. 검색박스 내 버튼 스타일 */
.search-box-mini button {
    background: white;
    border: none;
    padding: 10px 20px;
    font-weight: 800;
    color: #333;
    cursor: pointer;
    white-space: nowrap;
    transition: 0.3s;
}

.search-box-mini button:hover {
    background: #f0f0f0;
    color: #005baa;
}

/* 3. 호버 시 배경 밝기 조절 (선택 사항) */
.q-bar-item:hover {
    filter: brightness(1.1);
    /* 살짝 밝아지며 강조됨 */
}

/* 화살표 및 인디케이터 */
.nav-btn {
    position: absolute;
    margin: 0 50px;
    top: 50%;
    transform: translateY(-50%);
    z-index: 20;
    background: rgba(0, 0, 0, 0.2);
    border: none;
    color: #bcbcbc9f;
    font-size: 70px;
    padding: 20px 30px;
    cursor: pointer;
}

.prev {
    left: 0;
}

.next {
    right: 0;
}

.slide-indicators {
    position: absolute;
    bottom: 170px;
    /* 4색 바 바로 위로 */
    left: 50%;
    transform: translateX(-50%);
    z-index: 20;
    display: flex;
    gap: 10px;
}

.slide-indicators span {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    cursor: pointer;
}

.slide-indicators span.active {
    background: white;
    width: 30px;
    border-radius: 5px;
}
</style>