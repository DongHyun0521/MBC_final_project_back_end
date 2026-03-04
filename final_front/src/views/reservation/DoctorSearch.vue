<template>
  <div class="search-wrap">
    <div class="search-container">
      <header class="page-header">
        <h2>의료진 찾기</h2>
        <div class="title-bar"></div>
        <p class="page-desc">우리 병원의 전문 의료진을 확인하고 바로 예약해보세요.</p>
      </header>

      <div class="filter-box">
        <select v-model="selectedDeptId" class="search-input select-icon" @change="fetchDoctors">
          <option value="">진료과 전체 보기</option>
          <option v-for="dept in depts" :key="dept.med_dept_id" :value="dept.med_dept_id"> {{ dept.med_dept_name }}</option>
        </select>
        <input type="text" v-model="searchName" placeholder="의사 성함 입력" class="search-input name-input">
      </div>

      <div class="doctor-grid">
        <div v-for="doc in filteredDoctors" :key="doc.staff_id" class="doc-card">
          <div class="doc-img-area">
            <div class="placeholder-icon"></div>
            <span>사진 준비중</span>
          </div>
          <div class="doc-info">
            <div class="doc-header">
              <h4>{{ doc.staff_name }} <span class="doc-role">{{ doc.role }}</span></h4>
            </div>
            <div class="doc-details">
              <p><span class="label">진료과</span> {{ doc.med_dept_name }}</p>
              <p><span class="label">상태</span> {{ doc.status || '진료가능' }}</p>
            </div>
            <button @click="goToReserve(doc)" class="btn-reserve">진료 예약하기</button>
          </div>
        </div>

        <div v-if="filteredDoctors.length === 0" class="no-result">
          <p>검색 결과가 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// API 파일 가져오기 (진료과 목록 조회, 의사 목록 조회)
import { getDeptsReq, getDoctorsReq, getAllDoctorsReq } from '@/api/reservation'

// =========================================
// 1. 기본 설정 및 상태 변수 (Data)
// =========================================
const router = useRouter() // 페이지 이동 도구
const route = useRoute()   // 현재 주소 정보 (쿼리스트링 확인용)

// 검색 및 필터용 변수
const selectedDeptId = ref('') // 선택된 진료과 ID
const searchName = ref('')     // 검색창에 입력한 의사 이름

// 서버에서 받아올 데이터 저장소
const depts = ref([])      // 진료과 목록 (드롭다운용)
const allDoctors = ref([]) // 전체(혹은 해당 과) 의사 목록


// =========================================
// 2. Computed (자동 필터링 로직)
// =========================================

// 화면에 보여줄 진짜 의사만 걸러내는 필터
const filteredDoctors = computed(() => {
  return allDoctors.value.filter(doc => {
    // 검색어가 없으면 모두 통과
    if (!searchName.value) return (doc.role || '').toUpperCase() === '의사' || (doc.role || '').toUpperCase() === 'DOCTOR';

    const keyword = searchName.value.toLowerCase();
    
    // (1) 이름 매칭 확인
    const nameMatch = (doc.staff_name || '').includes(keyword);
    // (2) 진료과 이름 매칭 확인 (추가 설명 / 참고)
    const deptMatch = (doc.med_dept_name || '').includes(keyword);

    // 직업이 의사인지 확인
    const role = (doc.role || '').toUpperCase();
    const isDoctor = role === '의사' || role === 'DOCTOR';

    // 이름 혹은 진료과 중 하나라도 맞고 + 의사여야 함
    return (nameMatch || deptMatch) && isDoctor;
  })
})

// const filteredDoctors = computed(() => {
//   return allDoctors.value.filter(doc => {
    
//     // (1) 이름 검색어 필터: 검색어가 없으면 무조건 통과, 있으면 포함 여부 확인
//     const nameMatch = (doc.staff_name || '').includes(searchName.value);

//     // (2) 직업 필터: '간호사' 등은 제외하고 '의사'만 통과시킴
//     const role = (doc.role || '').toUpperCase(); // 대소문자 무시 (Doctor, doctor 다 통과)
//     const isDoctor = role === '의사' || role === 'DOCTOR';

//     // 두 조건(이름도 맞고 && 의사여야 함)을 모두 만족해야 화면에 나옴
//     return nameMatch && isDoctor;
//   })
// })


// =========================================
// 3. 페이지 이동 함수
// =========================================

// '예약하기' 버튼 클릭 시 실행
const goToReserve = (doc) => {
  // 예약 페이지(/reservation)로 이동하면서, 선택한 의사 정보를 쿼리로 넘겨줌
  router.push({
    path: '/reservation',
    query: {
      docId: doc.staff_id,
      docName: doc.staff_name,
      deptId: doc.med_dept_id || selectedDeptId.value,
      deptName: doc.med_dept_name
    }
  })
}


// =========================================
// 4. 서버 데이터 조회 함수
// =========================================

// (A) 초기 데이터 로딩 (진료과 목록 + 의사 목록)
const fetchData = async () => {
  try {
    // 1. 진료과 목록 가져오기 (드롭다운 채우기)
    const resDept = await getDeptsReq()
    depts.value = resDept.data

    // 2. 만약 주소창에 특정 진료과 ID가 있다면? (예: ?deptId=3)
    if (route.query.deptId) {
      selectedDeptId.value = Number(route.query.deptId) // 드롭다운 값 세팅
      await fetchDoctors() // 그 과의 의사만 가져옴
    } else {
      await fetchDoctors() // 전체 의사 가져옴
    }
  } catch (err) { 
    console.error('데이터 로드 실패') 
  }
}

// (B) 의사 목록 가져오기 (진료과 선택 변경 시에도 실행됨)
const fetchDoctors = async () => {
  try {
    let res;
    // 진료과가 선택되어 있으면 -> 그 과 의사만 조회
    if (selectedDeptId.value) {
      res = await getDoctorsReq(selectedDeptId.value)
    } 
    // 선택 안 되어 있으면 -> 전체 의사 조회
    else {
      res = await getAllDoctorsReq()
    }
    allDoctors.value = res.data // 받아온 데이터를 변수에 저장
  } catch (err) {
    console.error('의료진 로드 실패')
    allDoctors.value = [] // 에러나면 빈 목록으로 초기화
  }
}

// 외부(메가메뉴 등) 검색어 수신 로직
// 주소창의 쿼리 파라미터(q)가 바뀔 때마다 실행됨
watch(() => route.query.q, (newKeyword) => {
  if (newKeyword !== undefined) {
    searchName.value = newKeyword;
  }
}, { immediate: true }); // immediate: true를 주면 페이지 처음 들어올 때도 바로 실행함

// =========================================
// 5. 페이지 시작 (Life Cycle)
// =========================================

// 화면이 다 그려지자마자 fetchData 함수 실행!
onMounted(fetchData)
</script>

<style scoped>
.search-wrap {
  background-color: #ffffff;
  min-height: 100vh;
  padding: 60px 20px;
}

.search-container {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 50px;
}

.page-header h2 {
  font-size: 42px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 800;
}

.title-bar {
  width: 120px;
  height: 3px;
  background: #005baa;
  margin: 0 auto 15px;
}

.page-desc {
  color: #666;
  font-size: 18px;
}

.filter-box {
  display: flex;
  gap: 15px;
  margin-bottom: 40px;
  background: #fff;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
  border: 1px solid #eee;
}

.search-input {
  flex: 1;
  padding: 15px;
  border: 1px solid #ddd;
  font-size: 15px;
  color: #4e4e4e;
  transition: 0.3s;
  background-color: #f9f9f9;
}

.search-input:focus {
  border-color: #005baa;
  outline: none;
  background-color: #fff;
}

.doctor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 25px;
}

.doc-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 25px;
  display: flex;
  gap: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  align-items: center;
}

.doc-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(1, 113, 233, 0.08);
}

.doc-img-area {
  width: 110px;
  height: 130px;
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #aaa;
  font-size: 13px;
  flex-shrink: 0;
}

.placeholder-icon {
  font-size: 30px;
  margin-bottom: 5px;
  opacity: 0.5;
}

.doc-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.doc-header h4 {
  font-size: 20px;
  color: #333;
  margin: 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}

.doc-role {
  font-size: 13px;
  color: #005baa;
  background: #e0f2fe;
  padding: 3px 8px;
  font-weight: 600;
}

.doc-details p {
  margin: 0;
  font-size: 15px;
  color: #555;
  line-height: 1.5;
}

.doc-details .label {
  color: #005baa;
  font-weight: 600;
  margin-right: 8px;
  display: inline-block;
  width: 60px;
}

.btn-reserve {
  margin-top: 10px;
  padding: 12px;
  width: 100%;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
}

.no-result {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;
  color: #999;
}
</style>