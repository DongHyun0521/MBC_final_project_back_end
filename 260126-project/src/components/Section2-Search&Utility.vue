<template>
  <div class="section-2-wrapper">
    <section class="search-bar-section">
      <div class="search-inner">
        <h2 class="section-main-title">무엇을 도와드릴까요?</h2>
        <div class="search-input-box">
          <input type="text" v-model="totalSearchQuery" placeholder="검색어를 입력해 주세요" @keyup.enter="onSearch">
          <button class="search-btn" @click="onSearch">
            <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </button>
        </div>
        <div class="hashtag-area">
          <span v-for="tag in hashtags" :key="tag" class="tag" @click="handleTagClick(tag)">
            {{ tag }}
          </span>
        </div>
      </div>
    </section>

    <section class="major-service-section">
      <!-- <h3 class="service-title">주요 서비스</h3> -->
      <div class="util-grid">
        <div class="util-item" v-for="(item, idx) in utilMenus" :key="idx" @click="handleUtilClick(item)">
          <div class="icon-box" v-html="item.svg"></div>
          <p>{{ item.title }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const totalSearchQuery = ref('') // 검색어 저장용 반응형 변수

// 해시태그 리스트
const hashtags = ['#오시는길', '#진료예약', '#응급진료안내', '#층별안내', '#건강검진예약', '#전화번호안내']

// 주요 서비스 데이터
const utilMenus = [
  { 
    title: '외래진료 내역', 
    emitName: 'outpatient', 
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M12 8v8M8 12h8"/></svg>'
  },
  { 
    title: '증명서 발급', 
    emitName: 'certificate', 
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/></svg>'
  },
  { 
    title: '건강검진 안내', 
    emitName: 'checkup', 
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="M22 4 12 14.01l-3-3"/></svg>'
  },
  { 
    title: '입/퇴원 안내', 
    emitName: 'admission', 
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><rect x="2" y="6" width="20" height="12" rx="2"/><path d="M6 12h4M14 12h4M12 2v4M12 18v4"/></svg>'
  },
  { 
    title: '오시는 길', 
    emitName: 'location', 
    path: 'location',
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0z"/><circle cx="12" cy="10" r="3"/></svg>'
  },
  { 
    title: '주차 안내', 
    emitName: 'parking', 
    path: 'parkinginfo',
    svg: '<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><circle cx="12" cy="12" r="10"/><path d="M9 17V7h4a3 3 0 1 1 0 6H9"/></svg>'
  }
]

const onSearch = () => {
    if (!totalSearchQuery.value.trim()) { 
        alert('검색어를 입력해 주세요')
        return 
    }
    router.push({ path: '/search', query: { q: totalSearchQuery.value } })
}

const handleTagClick = (tag) => {
  const cleanTag = tag.replace('#', '')
  
  // 목적지 매핑
  const tagMap = {
    '오시는길': '/location',
    '진료예약': '/reservation',
    '응급진료안내': '/',
    '층별안내': '/', 
    '건강검진예약': '/',
    '전화번호안내': '/guide'
  }

  if (tagMap[cleanTag]) {
    router.push(tagMap[cleanTag]) // 즉시 이동
  } else {
    totalSearchQuery.value = cleanTag // 매핑 안 된 태그만 검색창에 입력
  }
}

const handleUtilClick = (item) => {
  router.push(item.path)
}

// [스크롤 버그 해결] 진입 시 화면을 맨 위로 강제로 올림
onMounted(() => {
  window.scrollTo(0, 0)
})
</script>

<style scoped>
.section-2-wrapper {
  width: 100%;
  background-color: #fff;
}

.search-bar-section {
  width: 100%;
  max-width: 1400px; /* 구분선 길이를 위해 폭을 조금 넓힘 */
  margin: 0 auto;
  text-align: center;
  padding-bottom: 60px; /* 구분선 위쪽 여백 */
  border-bottom: 1px solid #bcbcbc9f; 
}

.search-inner {
  max-width: 1200px;
  margin: 0 auto;
}

.section-main-title {
  font-family: 'pretendard';
  font-size: 54px;
  font-weight: 600;
  color: #222;
  margin-bottom: 50px;
}

.search-input-box {
  position: relative;
  max-width: 800px;
  margin: 0 auto 30px;
  border: 5px solid #005baa;
  background: #fff;
}

.search-input-box input {
  width: 100%;
  padding: 25px 70px 25px 25px;
  border: none;
  font-size: 20px;
  outline: none;
  font-family: 'pretendard';
}

.search-btn {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding-left: 15px;
  border-left: 1px solid #eee;
}

.hashtag-area {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.tag {
  font-family: 'pretendard';
  background: #bcbcbc9f;
  padding: 6px 18px;
  border-radius: 30px;
  font-size: 18px;
  color: #fff;
  cursor: pointer;
}

.tag:hover {
  background-color: #005baa;
  border-color: #005baa;
  color: #ffffff;
  transform: translateY(-5px);
}

.major-service-section {
  width: 100%;
  max-width: 1400px;
  margin: 30px auto 0; 
  text-align: center;
}

.util-grid {
  display: flex;
  justify-content: center; 
  gap: 40px; 
  flex-wrap: wrap; 
  width: 100%;
}

.util-item {
  font-family: 'pretendard';
  flex: 0 1 150px; 
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: 0.3s;
  margin-bottom: -30px;
}

.icon-box {
  width: 100px;
  height: 100px;
  background-color: #f8f8f8;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #444;
  margin-bottom: 15px;
  border: 1px solid #f0f0f0;
  transition: 0.3s;
}

.util-item p {
  font-size: 18px;
  font-weight: 500;
  color: #444;
  margin: 0;
  word-break: keep-all;
}

.util-item:hover .icon-box {
  background-color: #005baa;
  border-color: #005baa;
  color: #ffffff;
  transform: translateY(-5px);
}
</style>