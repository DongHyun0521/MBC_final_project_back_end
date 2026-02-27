<template>
  <div class="check-res-wrap">
    <div class="check-res-container">
      <header class="page-header">
        <h2>나의 진료 예약 조회</h2>
        <div class="title-bar"></div>
        <p class="page-desc">예약 내역을 확인하고 관리하실 수 있습니다.</p>
      </header>

      <div class="res-list-card">
        <div class="status-filter-tabs">
          <button 
            v-for="tab in ['전체', '예약', '완료']" 
            :key="tab"
            :class="['filter-btn', { active: currentFilter === tab }]"
            @click="currentFilter = tab"
          >
            {{ tab }}
          </button>
        </div>

        <table class="hospital-table">
          <thead>
            <tr>
              <th class="w-id">예약번호</th>
              <th class="w-info">진료과 / 의료진</th>
              <th class="w-date">예약일시</th>
              <th class="w-type">방문구분</th>
              <th class="w-status">상태</th>
              <th class="w-action">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="res in filteredReservations" :key="res.reservation_id">
              <td class="w-id">
                <div class="cell-content res-id">#{{ res.reservation_id }}</div>
              </td>
              <td class="w-info">
                <div class="cell-content res-doc-info">
                  <strong class="dept-name">{{ res.med_dept_name }}</strong>
                  <span class="staff-name">{{ res.doctor_name }} 의사</span>
                </div>
              </td>
              <td class="w-date">
                <div class="cell-content res-date-time">
                  <span class="d-txt">{{ formatDate(res.reservation_date) }}</span>
                  <span class="t-txt">{{ res.reservation_time }}</span>
                </div>
              </td>
              <td class="w-type">
                <div class="cell-content"><span class="visit-badge">{{ res.visit_type || '진료' }}</span></div>
              </td>
              <td class="w-status">
                <div class="cell-content">
                  <span :class="['status-badge', statusClass(res.reservation_status)]">
                    {{ res.reservation_status }}
                  </span>
                </div>
              </td>
              <td class="w-action">
                <div class="cell-content txt-center">
                  <button v-if="res.reservation_status === '예약'" @click="handleCancel(res.reservation_id)"
                    class="btn-cancel-res">
                    예약취소
                  </button>
                  <span v-else class="done-txt">-</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="filteredReservations.length === 0" class="empty-state">
          <div class="empty-icon"></div>
          <p>{{ currentFilter === '전체' ? '예약된 내역이 존재하지 않습니다.' : '해당 상태의 예약 내역이 없습니다.' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
// API 파일 가져오기 (내 예약 목록 조회, 예약 취소)
import { getMyResReq, cancelResReq } from '@/api/reservation'

// =========================================
// 1. 기본 설정 및 상태 변수 (Data)
// =========================================
const myReservations = ref([]) // 서버에서 받아온 원본 데이터
const currentFilter = ref('전체') // 현재 선택된 필터 탭 상태

// =========================================
// 2. Computed (필터링 로직)
// =========================================

// [핵심] 선택된 탭(currentFilter)에 따라 보여줄 목록을 자동으로 계산
const filteredReservations = computed(() => {
  if (currentFilter.value === '전체') {
    return myReservations.value;
  }
  // 선택한 상태(예: '예약', '완료')와 같은 데이터만 걸러서 반환
  return myReservations.value.filter(res => res.reservation_status === currentFilter.value);
});

// =========================================
// 3. 유틸리티 함수 (데이터 가공)
// =========================================

// 날짜 변환 함수 (20240206 -> 2024-02-06)
const formatDate = (dateInt) => {
  if (!dateInt) return '-'
  const s = String(dateInt) // 숫자를 문자로 변환
  // YYYY-MM-DD 형식으로 조립
  return `${s.substring(0, 4)}-${s.substring(4, 6)}-${s.substring(6, 8)}`
}

// 상태에 따른 CSS 클래스 반환 함수 (색깔 바꾸기용)
const statusClass = (s) => {
  if (s === '예약') return 'is-pending'  // 파란색
  if (s === '취소') return 'is-canceled' // 빨간색
  return 'is-success'                    // 회색 (진료완료 등)
}

// =========================================
// 4. 서버 데이터 조회 및 처리 함수
// =========================================

// (A) 내 예약 목록 가져오기
const fetchMyReservations = async () => {
  try {
    const res = await getMyResReq() // 서버에 "내 예약 내역 줘!" 요청
    myReservations.value = res.data // 받아온 데이터를 변수에 저장
  } catch (err) {
    console.error('데이터 로드 실패')
  }
}

// (B) 예약 취소 버튼 클릭 시 실행
const handleCancel = async (resId) => {
  // 1. 진짜 취소할 건지 물어보기
  if (!confirm('정말로 예약을 취소하시겠습니까?')) return

  try {
    // 2. 서버에 취소 요청 (예약 번호 보냄)
    const res = await cancelResReq(resId)
    
    // 3. 성공하면 메시지 띄우고 목록 새로고침
    if (res.data === 'success' || res.data === true) {
      alert('예약이 정상적으로 취소되었습니다')
      fetchMyReservations() // 목록을 다시 불러와야 '취소됨' 상태가 반영됨
    }
  } catch (err) {
    alert('취소 처리 중 오류가 발생했습니다')
  }
}

// =========================================
// 5. 페이지 시작 (Life Cycle)
// =========================================

// 화면이 다 그려지자마자 내 예약 목록을 가져오기
onMounted(fetchMyReservations)
</script>

<style scoped>
/* [추가] 필터 탭 스타일 */
.status-filter-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
  background-color: #fcfcfc;
  padding: 0 20px;
}

.filter-btn {
  padding: 15px 25px;
  background: none;
  border: none;
  font-size: 15px;
  font-weight: 600;
  color: #94a3b8;
  cursor: pointer;
  position: relative;
  transition: 0.2s;
}

.filter-btn:hover {
  color: #475569;
}

.filter-btn.active {
  color: #005baa; /* 활성화되면 파란색 */
}

.filter-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px; /* border-bottom 위에 겹치게 */
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #005baa;
}

.check-res-wrap {
  background-color: #ffffff;
  min-height: 100vh;
  padding: 80px 20px;
}

.check-res-container {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h2 {
  font-size: 42px;
  color: #404347;
  margin-bottom: 15px;
  font-weight: 800;
}

.title-bar {
  width: 120px;
  height: 4px;
  background: #005baa;
  margin: 0 auto 20px;
}

.page-desc {
  color: #888;
  font-size: 18px;
  font-weight: 300;
}

.res-list-card {
  background: #fff;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.04);
  border: 1px solid #eee;
  overflow: hidden;
}

.hospital-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.w-id {
  width: 130px;
}

.w-info {
  width: auto;
}

.w-date {
  width: 180px;
}

.w-type {
  width: 100px;
}

.w-status {
  width: 100px;
}

.w-action {
  width: 130px;
}

.hospital-table th {
  background-color: #f8fafc;
  padding: 20px 15px;
  font-size: 16px;
  font-weight: 700;
  color: #64748b;
  border-bottom: 2px solid #edf2f7;
  text-align: center;
}

.hospital-table td {
  padding: 0;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}

.cell-content {
  padding: 25px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.res-id {
  color: #94a3b8;
  font-family: 'Consolas', monospace;
  font-size: 14px;
}

.res-doc-info {
  align-items: flex-start;
}

.dept-name {
  color: #005baa;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 5px;
}

.staff-name {
  color: #64748b;
  font-size: 16px;
}

.res-date-time {
  gap: 5px;
}

.d-txt {
  font-weight: 700;
  color: #1e293b;
  font-size: 16px;
}

.t-txt {
  font-size: 14px;
  color: #94a3b8;
}

.visit-badge {
  background: #f1f5f9;
  color: #475569;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 700;
}

.status-badge.is-pending {
  background-color: #e0f2fe;
  color: #005baa;
}

.status-badge.is-success {
  background-color: #f1f5f9;
  color: #64748b;
}

.status-badge.is-canceled {
  background-color: #fee2e2;
  color: #ef4444;
}

.btn-cancel-res {
  background-color: #fff;
  border: 1px solid #fecaca;
  color: #ef4444;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.btn-cancel-res:hover {
  background-color: #ef4444;
  color: #fff;
}

.txt-center {
  text-align: center;
}

.done-txt {
  color: #cbd5e1;
}

.empty-state {
  padding: 120px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 25px;
  opacity: 0.2;
}
</style>