<template>
  <div class="search-wrap">
    <div class="search-container">
      <header class="page-header">
        <h2>진료과 찾기</h2>
        <div class="title-bar"></div>
        <p class="page-desc">우리 병원의 모든 진료과 정보를 한눈에 확인해보세요.</p>
      </header>

      <div class="filter-box">
        <select v-model="selectedLocation" class="search-input select-icon">
          <option value="">전체 위치(층/동)</option>
          <option value="본관">본관</option>
          <option value="신관">신관</option>
          <option value="별관">별관</option>
        </select>
        <input type="text" v-model="searchDeptName" placeholder="진료과 명칭 입력" class="search-input name-input">
      </div>

      <div class="dept-grid">
        <div v-for="dept in filteredDepts" :key="dept.med_dept_id" class="dept-card">
          <div class="dept-info">
            <div class="dept-main">
              <h3>{{ dept.med_dept_name }}</h3>
              <div class="dept-tags">
                <span class="tag location">{{ dept.dept_location }}</span>
                <span class="tag phone">{{ dept.dept_phone_number }}</span>
              </div>
            </div>
            <p class="desc">{{ dept.med_dept_name }}에서 최상의 진료 서비스를 제공합니다.</p>
          </div>
          <button class="view-doc-btn" @click="goToDoctorSearch(dept.med_dept_id)"> 의료진 상세정보 〉</button>
        </div>

        <div v-if="filteredDepts.length === 0" class="no-result">
          <p>검색된 진료과가 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// API 파일 가져오기 (진료과 목록 조회)
import { getDeptsReq } from '@/api/reservation'

// =========================================
// 1. 기본 설정 및 변수 (Data)
// =========================================
const router = useRouter() // 페이지 이동 도구

// 검색용 변수
const selectedLocation = ref('') // 선택된 위치 (본관, 신관 등)
const searchDeptName = ref('')   // 검색창에 입력한 진료과 이름

// 서버 데이터 저장소
const allDepts = ref([]) // 모든 진료과 목록

// =========================================
// 2. Computed (자동 필터링 로직)
// =========================================

// 사용자가 선택한 위치와 이름에 맞는 진료과만 걸러내기
const filteredDepts = computed(() => {
  return allDepts.value.filter(dept => {
    
    // (1) 위치 필터
    // 선택 안 했으면('') 무조건 통과, 했으면 해당 글자가 포함되는지 확인
    const matchLocation = selectedLocation.value === '' || (dept.dept_location || '').includes(selectedLocation.value)
    
    // (2) 이름 필터
    // 검색어가 이름에 포함되는지 확인
    const matchName = (dept.med_dept_name || '').includes(searchDeptName.value)
    
    // 두 조건이 다 맞아야 화면에 보여줌
    return matchLocation && matchName
  })
})

// =========================================
// 3. 페이지 이동 함수
// =========================================

// [의료진 상세정보] 버튼 클릭 시 실행
const goToDoctorSearch = (id) => {
  // 의료진 찾기 페이지(/doctorsearch)로 이동
  // 쿼리스트링(?deptId=3)으로 진료과 ID를 함께 보냄 -> 그래야 거기가서 자동으로 선택됨
  router.push({ 
    path: '/doctorsearch', 
    query: { deptId: id } 
  })
}

// =========================================
// 4. 서버 데이터 조회 함수
// =========================================

// 전체 진료과 목록 가져오기
const fetchDepts = async () => {
  try {
    const res = await getDeptsReq() // 서버에 요청
    allDepts.value = res.data       // 받아온 데이터 저장
  } catch (err) {
    console.error('진료과 로드 실패')
  }
}

// =========================================
// 5. 페이지 시작 (Life Cycle)
// =========================================

// 화면이 다 그려지면 바로 진료과 목록 가져옴
onMounted(fetchDepts)
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
  font-weight: 300;
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
  padding: 15px;
  border: 1px solid #ddd;
  font-size: 15px;
  color: #4e4e4e;
  transition: 0.3s;
  background-color: #f9f9f9;
}

select.search-input {
  width: 220px;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px;
  cursor: pointer;
}

.name-input {
  flex: 1;
}

.search-input:focus {
  border-color: #005baa;
  outline: none;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(1, 113, 233, 0.1);
}

.dept-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 25px;
}

.dept-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 35px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.dept-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(1, 113, 233, 0.08);
  border-color: #a1d8f3;
}

.dept-info h3 {
  font-size: 22px;
  color: #005baa;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.dept-tags {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.tag {
  font-size: 13px;
  padding: 4px 10px;
  font-weight: 500;
}

.location {
  background: #f0f7ff;
  color: #005baa;
}

.phone {
  background: #f1f5f9;
  color: #666;
}

.desc {
  color: #777;
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
  font-weight: 300;
}

.view-doc-btn {
  margin-top: 30px;
  width: 100%;
  padding: 15px;
  background: #404347;
  color: white;
  border: none;
  cursor: pointer;
  font-weight: 600;
  font-size: 15px;
  transition: 0.3s;
}

.view-doc-btn:hover {
  background: #005baa;
}

.no-result {
  grid-column: 1 / -1;
  text-align: center;
  padding: 100px 20px;
  color: #999;
}
</style>