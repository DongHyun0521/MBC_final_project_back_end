<template>
    <section class="health-section-wrapper">
        <div class="health-area">
            <div class="section-top-header">
                <h2 class="main-title">에스병원 건강이야기</h2>
                <router-link to="/story" class="view-all-link">
                    전체보기
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                </router-link>
            </div>

            <div class="slider-outer-container">
                <div class="slider-window">
                    <div class="slide-track"
                        :style="{ transform: `translateX(calc(-33.333% * ${currentIndex}))`, transitionDuration: transitionSpeed }">

                        <div class="health-card" v-for="(story, index) in displayStories" :key="index"
                            :class="{ active: currentIndex + 1 === index }"
                            @click="router.push(`/story/${story.healthStoryId}`)">
                            <div class="card-inner">
                                <div class="h-img-box">
                                    <img v-if="story.fileUrl && isImage(story.fileName)" :src="story.fileUrl"
                                        class="real-thumb">
                                    <video v-else-if="story.fileUrl && isVideo(story.fileName)" :src="story.fileUrl"
                                        class="real-thumb" muted></video>
                                    <div v-else class="placeholder-thumb">✚</div>
                                    <span class="h-tag">{{ story.deptName || '건강정보' }}</span>
                                </div>
                                <div class="h-text-box">
                                    <h4 class="h-title">{{ story.title }}</h4>
                                </div>
                                <div class="h-text-box">
                                    <p class="h-date">{{ story.writeDate }}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div v-if="healthStories.length === 0" class="empty-story">등록된 건강 이야기가 없습니다.</div>
                </div>

                <div class="controls">
                    <button class="nav-arrow prev" @click="prevSlide">
                        <span class="arrow-line"></span> PREV
                    </button>

                    <div class="page-indicator">
                        <strong>{{ ((currentIndex + 1) % healthStories.length) || healthStories.length }}</strong> / {{
                        healthStories.length }}
                    </div>

                    <button class="nav-arrow next" @click="nextSlide">
                        NEXT <span class="arrow-line"></span>
                    </button>
                </div>
            </div>
        </div>
    </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getStoriesReq } from '@/api/health'

const router = useRouter()
const healthStories = ref([])
const currentIndex = ref(0)

const transitionSpeed = ref('0.6s') // 애니메이션 속도 제어
const isMoving = ref(false)        // 연속 클릭 방지

// [참고] 무한 루프를 위해 원본을 3배로 복제 (F A B C D E F A B C D E F A B C D E)
const displayStories = computed(() => {
    if (healthStories.value.length === 0) return []
    return [...healthStories.value, ...healthStories.value, ...healthStories.value]
})

// [추가/수정] API 연동 로직
const fetchHealthStories = async () => {
    try {
        const res = await getStoriesReq()
        let list = res.data || []

        // 최신순 정렬 및 6개 제한
        const originalList = list.sort((a, b) => new Date(b.writeDate) - new Date(a.writeDate))
            .slice(0, 6).map(item => ({
                ...item,
                fileUrl: getImageUrl(item.thumbnailImg),
                fileName: item.thumbnailImg,
                writerName: item.writerName || item.adminName,
                writeDate: item.writeDate ? String(item.writeDate).substring(0, 10) : ''
            }))
        healthStories.value = originalList

        currentIndex.value = originalList.length
    } catch (e) {
        console.error("데이터 로드 실패", e)
    }
}

// 무한 반복 이동 함수
const nextSlide = () => {
    if (isMoving.value || healthStories.value.length === 0) return
    isMoving.value = true
    transitionSpeed.value = '0.6s'
    currentIndex.value++

    setTimeout(() => {
        if (currentIndex.value >= healthStories.value.length * 2 - 1) {
            transitionSpeed.value = '0s'
            currentIndex.value = healthStories.value.length - 1
        }
        isMoving.value = false
    }, 600)
}

const prevSlide = () => {
    if (isMoving.value || healthStories.value.length === 0) return
    isMoving.value = true
    transitionSpeed.value = '0.6s'
    currentIndex.value--

    setTimeout(() => {
        if (currentIndex.value < healthStories.value.length - 1) {
            transitionSpeed.value = '0s'
            currentIndex.value = healthStories.value.length * 2 - 2
        }
        isMoving.value = false
    }, 600)
}

const getImageUrl = (p) => p ? (p.startsWith('http') ? p : `http://localhost:8080${p}`) : ''
const isImage = (f) => f && ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(f.split('.').pop().toLowerCase())
const isVideo = (f) => f && ['mp4', 'webm', 'mov'].includes(f.split('.').pop().toLowerCase())

onMounted(() => {
    fetchHealthStories() // 페이지 열리자마자 서버에서 데이터 긁어오기
})
</script>

<style scoped>
.health-section-wrapper {
    width: 100%;
    padding: 200px 0 100px;
    background-color: #005baa;
    overflow: hidden;
    position: relative;
    z-index: 10;
}

/* 헤더 컨테이너: 버튼 배치를 위해 relative 설정 */
.section-top-header {
    max-width: 1550px;
    margin: 0 auto 60px;
    padding: 0 15px;
    position: relative; /* 중요! */
}

/* [복구] 메인 타이틀 중앙 정렬 및 42px */
.main-title {
    color: #fff;
    font-size: 54px;
    font-weight: 800;
    font-family: 'Pretendard';
    text-align: center; /* 공주님 요청 사항 */
    margin: 0;
    width: 100%;
}

/* 전체보기 버튼 (우측 정렬 배치) */
.view-all-link {
    position: absolute;
    right: 15px;
    bottom: 5px; /* 타이틀 바닥선과 맞춤 */
    color: #fff;
    text-decoration: none;
    font-family: 'Pretendard';
    font-size: 18px; 
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border: 1px solid rgba(255, 255, 255, 0.4);
    border-radius: 0; /* 직선 유지 */
    transition: all 0.3s;
}

/* 호버 효과 */
.view-all-link:hover {
    background: #fff;
    color: #005baa;
    border-color: #fff;
}

/* 모바일 대응: 화면이 좁아지면 버튼을 아래로 */
@media (max-width: 768px) {
    .section-top-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 20px;
    }
    .view-all-link {
        position: static;
    }
}

.empty-story {
    font-family: 'pretendard';
    text-align: center;
    color: #fff;
    font-size: 16px;
}

/* 슬라이더 윈도우: 3개가 보이게 설정 */
.slider-window {
    width: 100%;
    max-width: 1550px;
    margin: 0 auto;
    overflow: visible;
}

.slide-track {
    display: flex;
    will-change: transform;
}

.health-card {
    flex: 0 0 33.333%;
    padding: 25px 5px;
    transition: all 0.5s ease;
    opacity: 0.5;
    /* 비활성 카드는 더 흐리게 */
    transform: scale(0.85);
    /* 비활성 카드는 더 작게 */
}

.health-card.active {
    opacity: 1;
    transform: scale(1.05);
    z-index: 30;
}

.card-inner {
    background: #fff;
    overflow: hidden;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.health-card.active .card-inner {
    border: 6px solid #fbb800;
}

/* 컨트롤러 스타일 */
.controls {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 50px;
    margin-bottom: 10px;
    gap: 50px;
}

.nav-arrow {
    background: none;
    border: none;
    color: #fff;
    font-family: 'pretendard';
    font-size: 20px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 15px;
    cursor: pointer;
}

.arrow-line {
    width: 40px;
    height: 1px;
    background: #fff;
}

.page-indicator {
    font-family: 'pretendard';
    color: #fff;
    font-size: 20px;
}

.page-indicator strong {
    font-size: 26px;
}

/* 제목 스타일 업그레이드 */
.h-title {
    font-size: 22px;
    font-weight: 700;
    color: #333;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    padding: 12px 20px;
    word-break: keep-all;
    font-family: 'pretendard';
}

.h-img-box {
    width: 100%;
    height: 300px;
    position: relative;
    overflow: hidden;
    background-color: #f1f1f1;
}

.real-thumb {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

/* .placeholder-thumb {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40px;
    color: #ccc;
} */

.h-tag {
    position: absolute;
    top: 15px;
    left: 15px;
    background-color: #fbb800;
    /* 노란색 포인트 */
    color: #333;
    padding: 5px 12px;
    font-weight: 700;
    font-size: 16px;
    z-index: 5;
}

.h-text-box {
    background: #fff;
    height: 70px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.h-date {
    padding: 0 20px;
    font-size: 18px;
    color: #aaa;
    text-align: left;
    font-family: 'pretendard';
    margin-top: 10px;
}
</style>