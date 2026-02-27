<template>
  <section class="notice-section-wrapper">
    <div class="notice-area">
      <div class="top-section">
        <div class="notice-box">
          <div class="section-header">
            <h3>공지사항</h3>
            <span class="plus-btn" @click="router.push('/notice')">전체보기 +</span>
          </div>
          <ul class="notice-list">
            <li v-for="item in notices" :key="item.noticeId" @click="router.push(`/notice/${item.noticeId}`)">
              <span class="n-title">{{ item.title }}</span>
              <span class="n-date">{{ formatDate(item.writeDate) }}</span>
            </li>
            <li v-if="notices.length === 0" class="empty-txt">최신 공지사항이 없습니다</li>
          </ul>
        </div>

        <div class="parking-guide-box" @click="router.push('/parkinginfo')">
          <img src="@/assets/parking.jpg" class="p-bg-img" alt="주차장">
          <div class="parking-overlay">
            <div class="p-icon">
              <div class="p-text-box">P</div>
            </div>
            <h4>주차 이용 안내</h4>
            <p>외래/입원 고객을 위한<br>주차 요금 및 이용 방법 안내</p>
            <div class="p-arrow">더 알아보기 →</div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="health-section-wrapper">
    <div class="health-area">
      <div class="section-header-center">
        <h3>건강이야기</h3>
      </div>

      <div class="slider-outer-container">
        <button class="arrow-btn side-btn prev" @click="prevSlide" :disabled="currentIndex === 0">〈</button>

        <div class="slider-window">
          <div class="slide-track" :style="{ transform: `translateX(calc(-1 * (100% + 25px) / 3 * ${currentIndex}))` }">
            <div class="health-card" v-for="story in healthStories" :key="story.healthStoryId"
              @click="router.push(`/story/${story.healthStoryId}`)">
              <div class="h-img-box">
                <img v-if="story.fileUrl && isImage(story.fileName)" :src="story.fileUrl" class="real-thumb" alt="썸네일">
                <video v-else-if="story.fileUrl && isVideo(story.fileName)" :src="story.fileUrl" class="real-thumb" muted></video>
                <div v-else class="placeholder-thumb">
                  <span class="play-icon">✚</span>
                </div>
                <span class="h-tag">{{ story.deptName || '건강정보' }}</span>
              </div>

              <div class="h-text-box">
                <h4 class="h-title">{{ story.title }}</h4>
                <p class="h-desc">{{ story.writerName || '서울에스병원 홍보팀' }}</p>
              </div>
            </div>
          </div>
          
          <div v-if="healthStories.length === 0" class="empty-story">
            등록된 건강 이야기가 없습니다
          </div>
        </div>

        <button class="arrow-btn side-btn next" @click="nextSlide" :disabled="currentIndex >= maxIndex">〉</button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticesReq } from '@/api/customer'
import { getStoriesReq } from '@/api/health'

const router = useRouter()
const notices = ref([])
const healthStories = ref([])

const currentIndex = ref(0)
const displayLimit = 30

const maxIndex = computed(() => Math.max(0, healthStories.value.length - 3))

const nextSlide = () => { if (currentIndex.value < maxIndex.value) currentIndex.value++ }
const prevSlide = () => { if (currentIndex.value > 0) currentIndex.value-- }

const fetchNotices = async () => {
  try {
    const res = await getNoticesReq()
    let list = res.data || []
    list.sort((a, b) => new Date(b.writeDate) - new Date(a.writeDate))
    // 메인에는 3개만 노출하도록 설정
    notices.value = list.slice(0, 4)
  } catch (e) { console.error("공지사항 로드 실패", e) }
}

const fetchHealthStories = async () => {
  try {
    const res = await getStoriesReq()
    let list = res.data || []
    list.sort((a, b) => b.healthStoryId - a.healthStoryId)
    
    healthStories.value = list.slice(0, displayLimit).map(item => ({
      ...item,
      fileUrl: getImageUrl(item.thumbnailImg),
      fileName: item.thumbnailImg,
      writerName: item.writerName || item.adminName
    }))
  } catch (e) { console.error("건강이야기 로드 실패", e) }
}

const formatDate = (d) => d ? String(d).substring(0, 10).replace(/-/g, '.') : ''
const getImageUrl = (p) => p ? (p.startsWith('http') ? p : `http://localhost:8080${p}`) : ''
const isImage = (f) => f && ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(f.split('.').pop().toLowerCase())
const isVideo = (f) => f && ['mp4', 'webm', 'mov'].includes(f.split('.').pop().toLowerCase())

onMounted(() => { 
  fetchNotices()
  fetchHealthStories() 
})
</script>

<style scoped>
/* 구조 흐름: 배경색을 위한 전체 넓이 래퍼 */
.notice-section-wrapper {
  width: 100%;
  background-color: #f8f9fa; /* 너무 진한 #eee보다는 세련된 연회색 추천 */
  padding: 60px 0;
}

/* 이해 포인트: 실제 중앙 정렬을 담당하는 컨테이너 */
.notice-area {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 상단 섹션 배치 수정: 가로로 나란히 배치 */
.top-section {
  display: flex;
  gap: 25px;
  height: 320px; /* 높이 조절 */
}

.notice-box {
  flex: 1.5;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.notice-list {
  list-style: none;
  padding: 0;
}

.notice-list li {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #f8f9fa;
  cursor: pointer;
}

.n-title {
  font-size: 16px;
  color: #555;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 80%;
}

.n-date {
  font-size: 14px;
  color: #999;
}

/* 주차안내 박스 스타일 */
.parking-guide-box {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.p-bg-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.parking-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 35px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  z-index: 2;
}

.p-text-box {
  width: 44px;
  height: 44px;
  background-color: #fff;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 800;
  border-radius: 8px;
}

.parking-overlay h4 {
  font-size: 22px;
  font-weight: 700;
  margin: 15px 0 10px;
}

.parking-overlay p {
  font-size: 14px;
  line-height: 1.6;
  opacity: 0.9;
  margin-bottom: 20px;
}

/* 하단 건강이야기 섹션 설정 */
.health-section-wrapper {
  width: 100%;
  padding: 70px 0;
  background-color: #fff;
}

.health-area {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
  box-sizing: border-box;
}

/* 추가 설명: 타이틀을 화면 중앙으로 배치 */
.section-header-center {
  text-align: center;
  margin-bottom: 42px;
}

.section-header-center h3 {
  font-size: 39px;
  font-weight: 700;
  color: #666;
  margin: 0;
}

/* 추가 설명: 슬라이더와 양옆 버튼을 포함하는 상대 좌표 컨테이너 */
.slider-outer-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.slider-window {
  width: 100%;
  overflow: hidden;
  /* 버튼이 카드와 겹치지 않게 여백 조정 */
  margin: 0 10px;
}

.slide-track {
  display: flex;
  gap: 25px;
  transition: transform 0.5s ease-in-out;
}

.health-card {
  /* 너비 계산 및 곡률 유지 */
  flex: 0 0 calc((100% - 50px) / 3);
  background: #fff;
  border: 1px solid #eee;
  /* border-radius: 6px; */
  cursor: pointer;
  overflow: hidden;
  transition: 0.3s;
}

.health-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(72, 72, 72, 0.2);
}


.h-img-box {
  position: relative;
  aspect-ratio: 16 / 9;
}

.real-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.h-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  background: #fbb900;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.h-text-box {
  padding: 20px;
}

.h-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
  height: 1.6em;
  overflow: hidden;
  margin-top: -8px;
  margin-bottom: -5px;
}

.h-desc {
  font-size: 13px;
  color: #777;
  margin-top: 4px;
}

/* 슬라이드 버튼 스타일 */
.arrow-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
  /* 버튼 압축 방지 및 공중에 띄우기 */
  flex-shrink: 0;
  z-index: 10;
}

.arrow-btn:hover:not(:disabled) {
  background: #0171e9;
  color: #fff;
  border-color: #0171e9;
}

.arrow-btn:disabled {
  opacity: 0.3;
  cursor: default;
}

/* 추가 설명: 버튼을 카드 슬라이더 양옆 끝으로 배치 */
.side-btn.prev {
  margin-right: 10px;
}

.side-btn.next {
  margin-left: 10px;
}

.empty-story {
  color: #bbb;
  text-align: center;
  padding: 50px 0;
  font-size: 14px;
  width: 100%;
}
</style>