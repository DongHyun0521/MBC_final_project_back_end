<template>
  <section class="info-hub-section">
    <div class="hub-container">
      <div class="hub-wrapper">

        <div class="notice-hub-box">
          <div class="hub-header">
            <h3>공지사항</h3>
            <span class="view-more" @click="router.push('/notice')">전체보기 +</span>
          </div>

          <ul class="hub-list">
            <li v-for="item in notices" :key="item.noticeId" @click="router.push(`/notice/${item.noticeId}`)">
              <p class="n-title">{{ item.title }}</p>

              <div class="n-bottom-info">
                <span class="n-date">{{ formatDate(item.writeDate) }}</span>
                <span class="n-bar">|</span>
                <div v-if="isNew(item.writeDate)" class="new-wrap">
                  <span class="new-badge">NEW</span>
                  <span class="new-dot"></span>
                </div>
              </div>
            </li>
            <li v-if="notices.length === 0" class="empty-msg">최신 공지사항이 없습니다</li>
          </ul>
        </div>

        <div class="parking-hub-box" @click="router.push('/parkinginfo')">
          <div class="p-card-content">
            <div class="p-top">
              <div class="p-icon-circle">P</div>
              <h4>주차 이용 안내</h4>
            </div>
            <p class="p-desc">서울에스병원을 방문하시는 고객님을 위한<br>빠르고 편리한 주차 시스템 안내</p>
            <div class="p-bottom">
              <span class="p-tag">#무료주차 최대 30분</span>
              <div class="p-btn">상세보기 →</div>
            </div>
          </div>
          <div class="p-bg-mask"></div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticesReq } from '@/api/customer'

const router = useRouter()
const notices = ref([])

// 1. 공지사항 서버에서 가져오기
const fetchNotices = async () => {
  try {
    const res = await getNoticesReq() // 여기서 서버에 "데이터 줘!" 하고 요청함
    let list = res.data || []

    // 최신 날짜가 위로 오도록 정렬
    list.sort((a, b) => new Date(b.writeDate) - new Date(a.writeDate))

    // 화면에는 깔끔하게 상위 4개만 딱 보여주기
    notices.value = list.slice(0, 4)
  } catch (e) {
    console.error("공지사항 로드 실패", e)
  }
}

// 2. NEW 배지 판별 함수
const isNew = (date) => {
  if (!date) return false
  const writeDate = new Date(date)
  const today = new Date()
  // 작성일로부터 2일 이내면 true 반환함
  const diffTime = Math.abs(today - writeDate)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays <= 2
}

// 3. 날짜 형식 예쁘게 바꾸기 (2026-02-17 -> 2026.02.17)
const formatDate = (d) => d ? String(d).substring(0, 10) : ''

onMounted(() => {
  fetchNotices() // 페이지 열리자마자 바로 호출
})
</script>

<style scoped>
.info-hub-section {
  width: 100%;
  background-color: #fff;
}

.hub-container {
  max-width: 1550px; 
  margin: 0 auto;
}

.hub-wrapper {
  display: flex;
  gap: 100px;
  align-items: stretch;
  margin-top: 90px;
  width: 1200px;
}

/* 1. 공지사항 섹션 */
.notice-hub-box {
  flex: 1;
}

.hub-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 2px solid #222;
}

.hub-header h3 {
  font-family: 'pretendard';
  font-size: 32px;
  font-weight: 700;
  color: #222;
}

.view-more {
  font-family: 'pretendard';
  font-size: 16px;
  color: #888;
  cursor: pointer;
}

.hub-list li {
  display: flex;
  flex-direction: column; 
  justify-content: center;
  padding: 28px 10px; 
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: 0.3s;
}

.hub-list li:hover { background-color: #e5e5e5; }

.n-title {
  font-family: 'pretendard';
  font-size: 20px;
  font-weight: 600;
  color: #222;
  margin-bottom: 10px; 
  transition: 0.2s;
}

/* 하단 정보창 세부 디자인 */
.n-bottom-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.n-date {
  font-family: 'pretendard';
  font-size: 15px;
  color: #999;
}

.n-bar {
  font-family: 'pretendard';
  font-size: 12px;
  color: #eee;
}

.new-wrap {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.new-badge {
  color: #005baa;
  font-size: 12px;
  font-weight: 700;
}

.new-dot {
  width: 5px;
  height: 5px;
  background-color: #005baa;
  border-radius: 50%;
  position: absolute;
  top: -3px;
  right: -8px;
}

/* 2. 주차 안내 카드  */
.parking-hub-box {
  flex: 0 0 500px;
  position: relative;
  overflow: hidden;
  background-color: #005baa;
  color: #fff;
  cursor: pointer;
  transition: 0.4s;
}

.parking-hub-box:hover { transform: translateY(-10px); }

.p-card-content {
  position: relative;
  z-index: 2;
  padding: 90px 45px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between; 
}

.p-top {
  display: flex;
  align-items: center;
  gap: 10px;
}

.p-icon-circle {
  width: 55px;
  height: 55px;
  background-color: #fff;
  color: #005baa;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  font-weight: 900;
}

.p-top h4 {
  font-family: 'pretendard';
  font-size: 32px;
  font-weight: 700;
}

.p-desc {
  font-family: 'pretendard';
  font-size: 19px;
  line-height: 1.6;
  opacity: 0.95;
  margin: 30px 0;
}

.p-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.p-tag {
  font-family: 'pretendard';
  font-size: 18px;
  color: #fbb800;
  font-weight: 600;
}

.p-btn {
  font-family: 'pretendard';
  font-size: 17px;
  font-weight: 600;
  border-bottom: 2px solid #fff;
  padding-bottom: 4px;
}

.p-bg-mask {
  position: absolute;
  inset: 0;
  background: url('@/assets/parking.jpg') no-repeat center/cover;
  opacity: 0.15;
  z-index: 1;
}

/* 반응형 유지 */
@media (max-width: 1550px) {
  .hub-container { max-width: 100%; padding: 0 40px; }
  .hub-wrapper { gap: 60px; }
  .parking-hub-box { flex: 0 0 500px; }
}
</style>