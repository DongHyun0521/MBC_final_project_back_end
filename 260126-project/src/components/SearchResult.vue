<template>
  <div class="search-result-wrap">
    <div class="result-header">
      <h2>
        <span class="highlight">'{{ queryKeyword }}'</span> 검색 결과 
        <span class="count">총 {{ totalCount }}건의 결과가 있습니다</span>
      </h2>
    </div>

    <section v-if="filteredMenus.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>메뉴 바로가기 <span class="num">{{ filteredMenus.length }}</span></h3>
      </div>
      <div class="simple-list-container">
        <div 
          v-for="(menu, idx) in filteredMenus" 
          :key="idx" 
          class="simple-list-item" 
          @click="router.push(menu.path)"
        >
          <span class="txt">{{ menu.name }}</span>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </div>
      </div>
    </section>

    <section v-if="filteredDoctors.length > 0 || filteredDepts.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>의료진 / 진료과 <span class="num">{{ filteredDoctors.length + filteredDepts.length }}</span></h3>
      </div>
      
      <ul v-if="filteredDepts.length > 0" class="dept-list">
        <li v-for="dept in filteredDepts" :key="dept.med_dept_id" @click="goReservation({ deptId: dept.med_dept_id })">
          <div class="dept-info">
            <span class="badge">진료과</span>
            <strong>{{ dept.dept_name }}</strong>
          </div>
          <div class="link-txt">진료 예약하기</div>
        </li>
      </ul>

      <div v-if="filteredDoctors.length > 0" class="doc-list-wrap">
        <div v-for="doc in filteredDoctors" :key="doc.staff_id" class="doc-item" @click="goReservation({ docId: doc.staff_id, deptId: doc.med_dept_id })">
          <div class="doc-profile">
            <img v-if="doc.profile_img" :src="doc.profile_img" class="real-img" />
            <div v-else class="img-placeholder">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1.5">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
          </div>
          <div class="doc-info-text">
            <p class="dept">{{ doc.dept_name }}</p>
            <p class="name">{{ doc.name }} <span class="role">교수</span></p>
          </div>
          <div class="link-btn">예약하기</div>
        </div>
      </div>
    </section>

    <section v-if="filteredNotices.length > 0 || filteredFaqs.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>게시판 / 소식 <span class="num">{{ filteredNotices.length + filteredFaqs.length }}</span></h3>
      </div>
      
      <div v-if="filteredNotices.length > 0" class="board-container">
        <h4 class="sub-h4">공지사항</h4>
        <ul>
          <li v-for="notice in filteredNotices" :key="notice.noticeId" @click="router.push(`/notice/${notice.noticeId}`)">
            <span class="b-title">{{ notice.title }}</span>
            <span class="b-date">{{ notice.writeDate || notice.date }}</span>
          </li>
        </ul>
      </div>

      <div v-if="filteredFaqs.length > 0" class="board-container">
        <h4 class="sub-h4">FAQ</h4>
        <ul>
          <li v-for="faq in filteredFaqs" :key="faq.faqId" @click="router.push('/faq')">
            <div class="b-left">
              <span class="q-mark">Q</span>
              <span class="b-title">{{ faq.question || faq.title }}</span>
            </div>
          </li>
        </ul>
      </div>
    </section>

    <div v-if="totalCount === 0" class="no-result">
      <div class="icon-box">
        <svg width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#ddd" stroke-width="1">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          <line x1="8" y1="11" x2="14" y2="11"></line>
        </svg>
      </div>
      <p class="main-msg">검색 결과가 없습니다</p>
      <p class="sub-msg">'{{ queryKeyword }}'와 일치하는 내용을 찾을 수 없습니다<br>검색어를 다시 확인해 주세요</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// [API] 기존 파일 재사용 (새 파일 생성 X)
import { getAllDoctorsReq, getDeptsReq } from '@/api/reservation' 
import { getNoticesReq, getFaqsReq } from '@/api/customer'

const route = useRoute()
const router = useRouter()

// 현재 검색어
const queryKeyword = ref('')

// ============================================
// 1. 데이터 State
// ============================================

// [최신화] 라우터 파일을 기반으로 한 전체 메뉴 리스트
const siteMenus = [
  // 1. 병원소개 (About)
  { name: '인사말', path: '/greeting' },
  { name: '미션/비전', path: '/mission' },
  { name: '연혁', path: '/history' },
  
  // 2. 오시는 길 및 주차 (Location) - '주차' 키워드 강화
  { name: '오시는 길', path: '/location' },
  { name: '주차 이용 안내', path: '/parkinginfo' },
  
  // 3. 의료진 및 진료과 (Medical / Reservation)
  { name: '의료진 찾기', path: '/doctorsearch' },
  { name: '진료과 찾기', path: '/deptsearch' },
  { name: '센터/클리닉 안내', path: '/centerclinic' },
  { name: '진료 예약 신청', path: '/reservation' },
  { name: '예약 내역 조회', path: '/checkreservation' },
  { name: '진료 시간 및 안내', path: '/guide' },
  { name: '진료 절차 안내', path: '/process' },
  
  // 4. 건강정보 (Health)
  { name: '건강이야기', path: '/story' },
  { name: '질환백과', path: '/disease' },
  { name: '자가진단 서비스', path: '/checkup' },
  
  // 5. 고객서비스 (Customer / Vehicle)
  { name: '공지사항', path: '/notice' },
  { name: '자주 묻는 질문', path: '/faq' },
  { name: '고객의 소리', path: '/voc' },
  { name: '차량 등록', path: '/vehiregi' },
  
  // 6. 회원 서비스 (Member)
  { name: '로그인', path: '/login' },
  { name: '회원가입', path: '/regi' },
  { name: '마이페이지', path: '/mypage' }
]

// 서버 데이터 저장소
const allDoctors = ref([]) 
const allDepts = ref([])   
const allNotices = ref([]) 
const allFaqs = ref([])    

// ============================================
// 2. 검색 필터링 로직 (Computed)
// ============================================

// [메뉴] 사이트 메뉴 이름 검색
const filteredMenus = computed(() => {
  if (!queryKeyword.value) return []
  return siteMenus.filter(m => m.name.includes(queryKeyword.value))
})

// [진료과] 진료과 이름 검색 (데이터 없을 경우 빈문자열 처리)
const filteredDepts = computed(() => {
  if (!queryKeyword.value) return []
  return allDepts.value.filter(d => (d.dept_name || '').includes(queryKeyword.value))
})

// [의료진] 의사 이름 OR 진료과 이름으로 검색
const filteredDoctors = computed(() => {
  if (!queryKeyword.value) return []
  return allDoctors.value.filter(d => 
    (d.name || '').includes(queryKeyword.value) || 
    (d.dept_name || '').includes(queryKeyword.value)
  )
})

// [공지사항] 글 제목 검색
const filteredNotices = computed(() => {
  if (!queryKeyword.value) return []
  return allNotices.value.filter(n => (n.title || '').includes(queryKeyword.value))
})

// [FAQ] 질문 내용 OR 제목 검색
const filteredFaqs = computed(() => {
  if (!queryKeyword.value) return []
  return allFaqs.value.filter(f => 
    (f.question || '').includes(queryKeyword.value) || 
    (f.title || '').includes(queryKeyword.value)
  )
})

// [전체 건수] 각 필터링 결과의 개수 합산
const totalCount = computed(() => {
  return filteredMenus.value.length + 
         filteredDepts.value.length + 
         filteredDoctors.value.length + 
         filteredNotices.value.length + 
         filteredFaqs.value.length
})

// ============================================
// 3. API 호출
// ============================================

const fetchData = async () => {
  try {
    // 4가지 데이터를 병렬로 한 번에 가져옴
    const [docRes, deptRes, noticeRes, faqRes] = await Promise.all([
      getAllDoctorsReq(),
      getDeptsReq(),
      getNoticesReq(),
      getFaqsReq()
    ])

    allDoctors.value = docRes.data || []
    allDepts.value = deptRes.data || []
    allNotices.value = noticeRes.data || []
    allFaqs.value = faqRes.data || []

  } catch (e) {
    console.error("데이터 로딩 실패:", e)
  }
}

// 예약 페이지로 이동 (쿼리 포함)
const goReservation = (query) => {
  router.push({ path: '/reservation', query })
}

onMounted(() => {
  queryKeyword.value = route.query.q || ''
  fetchData()
})

watch(() => route.query.q, (newVal) => {
  queryKeyword.value = newVal || ''
})
</script>

<style scoped>
.search-result-wrap {
  font-family: 'Pretendard';
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px;
  min-height: 80vh;
}

/* 헤더: 직선형 마감 */
.result-header {
  border-bottom: 3px solid #005baa;
  padding-bottom: 30px;
  margin-bottom: 60px;
}

.result-header h2 {
  font-size: 32px;
  color: #111;
  font-weight: 800;
}

.highlight { color: #005baa; }
.count { font-size: 18px; color: #888; margin-left: 15px; font-weight: 400; }

/* 섹션 공통 스타일 */
.result-section { margin-bottom: 80px; }
.sec-title-box { margin-bottom: 25px; border-bottom: 1px solid #eee; padding-bottom: 15px; display: flex; align-items: center; }
.sec-title-box h3 { font-size: 22px; font-weight: 700; color: #222; }
.sec-title-box .num { font-size: 16px; color: #005baa; margin-left: 10px; font-weight: 600; background: #f0f7ff; padding: 2px 10px; border-radius: 0; }

/* 1. 메뉴 리스트 (직선 강조) */
.simple-list-container { border-top: 1px solid #222; }
.simple-list-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 22px 20px; border-bottom: 1px solid #eee;
  cursor: pointer; background: #fff; transition: all 0.3s;
}
.simple-list-item:hover { background-color: #f9f9f9; padding-left: 30px; }
.simple-list-item:hover .txt { color: #005baa; font-weight: 600; }
.simple-list-item .txt { font-size: 18px; color: #333; }
.simple-list-item svg { color: #ccc; transition: 0.3s; }
.simple-list-item:hover svg { color: #005baa; transform: translateX(5px); }

/* 2. 진료과 리스트 */
.dept-list { list-style: none; padding: 0; margin-bottom: 30px; border-top: 1px solid #222; }
.dept-list li {
  display: flex; justify-content: space-between; align-items: center;
  padding: 22px 20px; border-bottom: 1px solid #eee; cursor: pointer; transition: 0.3s;
}
.dept-list li:hover { background-color: #f9f9f9; }
.dept-info .badge {
  background-color: #005baa; color: #fff; font-size: 12px;
  padding: 4px 10px; border-radius: 0; margin-right: 15px; font-weight: 600;
}
.dept-info strong { font-size: 18px; color: #333; }
.link-txt { font-size: 15px; color: #005baa; font-weight: 600; text-decoration: underline; }

/* 3. 의료진 카드 (직선형 그리드) */
.doc-list-wrap { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.doc-item {
  display: flex; align-items: center; padding: 25px;
  border: 1px solid #eee; border-radius: 0; /* 곡선 제거 */
  cursor: pointer; background: #fff; transition: 0.3s;
}
.doc-item:hover { border-color: #005baa; box-shadow: 10px 10px 0px rgba(0, 91, 170, 0.05); }
.doc-profile {
  width: 80px; height: 80px; background-color: #f8f9fa;
  border-radius: 0; margin-right: 25px; overflow: hidden;
  border: 1px solid #eee; display: flex; align-items: center; justify-content: center;
}
.real-img { width: 100%; height: 100%; object-fit: cover; }
.doc-info-text .dept { font-size: 14px; color: #005baa; font-weight: 700; margin-bottom: 5px; }
.doc-info-text .name { font-size: 20px; font-weight: 800; color: #222; }
.doc-info-text .role { font-size: 16px; font-weight: 400; color: #888; }
.link-btn {
  margin-left: auto; font-size: 14px; background-color: #333;
  padding: 10px 18px; border-radius: 0; color: #fff; font-weight: 600; transition: 0.3s;
}
.doc-item:hover .link-btn { background-color: #005baa; }

/* 4. 게시판 스타일 */
.board-container { margin-bottom: 40px; }
.sub-h4 { font-size: 16px; color: #222; margin-bottom: 12px; font-weight: 800; padding-left: 10px; border-left: 4px solid #005baa; }
.board-container ul { list-style: none; padding: 0; border-top: 2px solid #333; }
.board-container li {
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 10px; border-bottom: 1px solid #eee; cursor: pointer;
}
.board-container li:hover .b-title { color: #005baa; font-weight: 600; }
.q-mark { font-weight: 900; color: #005baa; font-size: 18px; margin-right: 15px; }
.b-title { font-size: 16px; color: #333; }
.b-date { font-size: 14px; color: #999; }

/* 결과 없음 */
.no-result { text-align: center; padding: 150px 0; border-top: 1px solid #eee; }
.no-result .icon-box { margin-bottom: 30px; opacity: 0.5; }
.main-msg { font-size: 24px; color: #222; margin-bottom: 15px; font-weight: 800; }
.sub-msg { font-size: 16px; color: #888; line-height: 1.6; }
</style>