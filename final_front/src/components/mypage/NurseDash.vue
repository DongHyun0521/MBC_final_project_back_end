<template>
    <div class="nurse-dash-wrapper">
        <div v-if="currentView === 'dash'" class="dash-home-grid">
            <section class="dash-card profile-card">
                <div class="card-head">
                    <h3>내 정보 요약</h3>
                </div>
                <div class="info-list">
                    <div class="info-item">
                        <span class="label">소속</span>
                        <p class="val">{{ userInfo.deptName || '병동미지정' }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">전화번호</span>
                        <p class="val">{{ formatPhone(userInfo.phoneNumber) }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">생년월일</span>
                        <p class="val">{{ formatDate(userInfo.birthday) }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">이메일</span>
                        <p class="val">{{ userInfo.email }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">직책</span>
                        <p class="val">{{ userInfo.role }}</p>
                    </div>
                </div>
            </section>

            <section class="dash-card clickable-card" @click="goToResList">
                <div class="card-head">
                    <h3>금일 진료 현황</h3>
                    <span class="more-view">자세히 보기 +</span>
                </div>
                <div class="stat-grid">
                    <div class="stat-box blue">
                        <span>{{doctorSchedules.filter(s => s.reservation_status === '예약').length}}</span>명<br>진료 대기
                    </div>
                    <div class="stat-box">
                        <span>{{nurseShifts.find(s => isToday(s.fullDate))?.type || 'OFF'}}</span><br>오늘의 듀티
                    </div>
                </div>
            </section>

            <section class="dash-card clickable-card" @click="goToList">
                <div class="card-head">
                    <h3>나의 병원 예약</h3>
                    <span class="more-view">더보기 +</span>
                </div>

                <div v-if="upcomingResList.length > 0">
                    <div class="res-highlight main-res">
                        <span class="d-day">D-{{ calculateDday(upcomingRes.reservation_date) }}</span>
                        <p class="res-time-txt">{{ formatDate(upcomingRes.reservation_time) }}</p>
                        <p class="res-doc-txt">{{ upcomingRes.med_dept_name }} | {{ upcomingRes.doctor_name }} 의사</p>
                    </div>

                    <div v-if="upcomingResList.length > 1" class="sub-res-list">
                        <div v-for="(res, idx) in upcomingResList.slice(1)" :key="res.reservation_id"
                            class="sub-res-item">
                            <span class="sub-time">{{ formatDate(res.reservation_time) }}</span>
                            <span class="sub-info">{{ res.med_dept_name }} ({{ res.doctor_name }})</span>
                        </div>
                    </div>
                </div>

                <div v-else class="empty-res">예정된 예약이 없습니다</div>
            </section>
        </div>

        <div v-if="currentView === 'nur_res'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>진료 업무 일정</h3>
                    <div class="toggle-group">
                        <button :class="{ active: nurViewMode === 'calendar' }"
                            @click="nurViewMode = 'calendar'">달력</button>
                        <button :class="{ active: nurViewMode === 'list' }" @click="nurViewMode = 'list'">리스트</button>
                    </div>
                </div>

                <div v-if="nurViewMode === 'calendar'" class="calendar-wrap">
                    <div class="cal-header">
                        <button @click="changeMonth(-1)"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <path d="M15 18l-6-6 6-6" />
                            </svg></button>
                        <h4>{{ calYear }}년 {{ calMonth }}월</h4>
                        <button @click="changeMonth(1)"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <path d="M9 18l6-6-6-6" />
                            </svg></button>
                    </div>
                    <div class="cal-grid">
                        <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day
                            }}</div>
                        <div v-for="(date, idx) in calendarDays" :key="idx"
                            :class="['cal-cell', { 'diff-month': !date.isCurrentMonth, 'today': isToday(date.fullDate), 'selected': selectedDate === date.fullDate }]"
                            @click="handleCalDateClick(date.fullDate)">
                            <span class="day-num">{{ date.day }}</span>
                            <div class="cal-events">
                                <div v-for="(evt, i) in getEventsForDate(date.fullDate).slice(0, 2)" :key="i"
                                    :class="['cal-dot-bar', getBadgeClass(evt.reservation_status)]"></div>
                            </div>
                        </div>
                    </div>
                    <div v-if="selectedDate" class="selected-date-list">
                        <h4>{{ selectedDate }} 예약 리스트</h4>
                        <table class="hospital-tbl mt-10">
                            <thead>
                                <tr>
                                    <th>시간</th>
                                    <th>환자명</th>
                                    <th>상태</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="sc in selectedDateSchedules" :key="sc.reservation_id">
                                    <td>{{ formatTime(sc.reservation_time) }}</td>
                                    <td class="bold-blue">{{ sc.patient_name }}</td>
                                    <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{
                                            sc.reservation_status }}</span></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div v-else>
                    <div class="filter-tabs">
                        <button v-for="st in ['전체', '예약', '완료', '취소']" :key="st"
                            :class="['filter-btn', { active: nurFilter === st }]" @click="nurFilter = st">{{ st
                            }}</button>
                    </div>
                    <table class="hospital-tbl">
                        <thead>
                            <tr>
                                <th>날짜</th>
                                <th>시간</th>
                                <th>환자명</th>
                                <th>상태</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="sc in filteredDoctorSchedules" :key="sc.reservation_id">
                                <td>{{ formatDate(sc.reservation_date) }}</td>
                                <td>{{ formatTime(sc.reservation_time) }}</td>
                                <td class="bold-blue">{{ sc.patient_name }}</td>
                                <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{
                                        sc.reservation_status }}</span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div v-if="currentView === 'nur_schedule'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>나의 근무 일정표</h3>
                </div>
                <div class="calendar-wrap">
                    <div class="cal-header">
                        <button @click="changeMonth(-1)"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <path d="M15 18l-6-6 6-6" />
                            </svg></button>
                        <h4>{{ calYear }}년 {{ calMonth }}월</h4>
                        <button @click="changeMonth(1)"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <path d="M9 18l6-6-6-6" />
                            </svg></button>
                    </div>
                    <div class="cal-grid">
                        <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day
                            }}</div>
                        <div v-for="(date, idx) in calendarDays" :key="idx"
                            :class="['cal-cell', { 'diff-month': !date.isCurrentMonth, 'today': isToday(date.fullDate) }]">
                            <span class="day-num">{{ date.day }}</span>
                            <div class="cal-events">
                                <div v-if="getShiftForDate(date.fullDate)"
                                    :class="['shift-badge', getShiftClass(getShiftForDate(date.fullDate))]">
                                    {{ getShiftForDate(date.fullDate) }}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="currentView === 'nur_ward'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>병동 현황 관리</h3>
                </div>
                <table class="hospital-tbl">
                    <thead>
                        <tr>
                            <th>호실</th>
                            <th>환자명</th>
                            <th>입원일</th>
                            <th>상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="p in wardPatients" :key="p.id">
                            <td>{{ p.room }}호</td>
                            <td class="bold-blue">{{ p.name }}</td>
                            <td>{{ p.date }}</td>
                            <td><span class="status-badge active">{{ p.status }}</span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div v-if="currentView === 'res'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>내 진료 예약 내역</h3>
                </div>
                <div class="filter-tabs">
                    <button v-for="tab in ['전체', '예약', '완료', '취소']" :key="tab"
                        :class="['filter-btn', { active: resFilter === tab }]" @click="resFilter = tab">{{ tab
                        }}</button>
                </div>
                <table class="hospital-tbl">
                    <thead>
                        <tr>
                            <th>진료과</th>
                            <th>담당의</th>
                            <th>예약일시</th>
                            <th>상태</th>
                            <th class="txt-center">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="res in filteredMyReservations" :key="res.reservation_id">
                            <td class="bold-blue">{{ res.med_dept_name }}</td>
                            <td class="bold-blue">{{ res.doctor_name }}</td>
                            <td>{{ formatDate(res.reservation_date) }} {{ res.reservation_time }}</td>
                            <td><span :class="['status-badge',
                                res.reservation_status === '예약' ? 'active' :
                                    res.reservation_status === '완료' ? 'done' :
                                        res.reservation_status === '취소' ? 'cancel' : 'noshow']">
                                    {{ res.reservation_status }}
                                </span></td>
                            <td class="txt-center">
                                <button v-if="res.reservation_status === '예약'" class="btn-cancel-table"
                                    @click="cancelRes(res.reservation_id)">예약취소</button>
                                <span v-else>-</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div v-if="filteredMyReservations.length === 0" class="empty-msg">
                    {{ resFilter === '전체' ? '예약 내역이 없습니다' : '해당 내역이 없습니다' }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, defineEmits } from 'vue'
import { getMyResReq, cancelResReq, getAllDoctorsReq, getDocSchedReq } from '@/api/reservation'

const props = defineProps({
    userInfo: Object,
    currentView: String
})

// 부모(MyPage.vue)의 탭 상태를 바꾸기 위한 통로 개설
const emit = defineEmits(['changeView'])

// [데이터 주머니] 
const myReservations = ref([])   // 개인용
const doctorSchedules = ref([])  // 부서 진료 공유용

// [업무용 상태 변수]
const nurViewMode = ref('calendar') // 달력 보기 / 리스트 보기 전환
const nurFilter = ref('예약')       // 진료 상태 필터 (전체, 예약, 완료 등)
const calYear = ref(new Date().getFullYear())
const calMonth = ref(new Date().getMonth() + 1)
const selectedDate = ref(null)     // 달력에서 클릭한 날짜 저장

// [개인용 상태 변수]
const resFilter = ref('전체')       // 내 예약 내역 필터링 상태

// ======================================= 간호사 전용 가짜 데이터 (Mock) =======================================
// 근무 일정
const nurseShifts = ref([
    // 2월 근무
    { fullDate: '2026-02-01', type: 'D' }, { fullDate: '2026-02-02', type: 'D' }, { fullDate: '2026-02-03', type: 'E' },
    { fullDate: '2026-02-04', type: 'E' }, { fullDate: '2026-02-05', type: 'OFF' }, { fullDate: '2026-02-06', type: 'N' },
    { fullDate: '2026-02-07', type: 'N' }, { fullDate: '2026-02-08', type: 'OFF' }, { fullDate: '2026-02-09', type: 'D' },
    { fullDate: '2026-02-10', type: 'D' }, { fullDate: '2026-02-11', type: 'E' }, { fullDate: '2026-02-12', type: 'E' },
    { fullDate: '2026-02-13', type: 'OFF' }, { fullDate: '2026-02-14', type: 'N' }, { fullDate: '2026-02-15', type: 'N' },
    { fullDate: '2026-02-16', type: 'OFF' }, { fullDate: '2026-02-17', type: 'D' }, { fullDate: '2026-02-18', type: 'D' },
    { fullDate: '2026-02-19', type: 'E' }, { fullDate: '2026-02-20', type: 'D' }, { fullDate: '2026-02-21', type: 'E' },
    { fullDate: '2026-02-22', type: 'N' }, { fullDate: '2026-02-23', type: 'OFF' }, { fullDate: '2026-02-24', type: 'D' },
    { fullDate: '2026-02-25', type: 'E' }, { fullDate: '2026-02-26', type: 'N' }, { fullDate: '2026-02-27', type: 'OFF' },
    { fullDate: '2026-02-28', type: 'D' },

    // 3월 근무
    { fullDate: '2026-03-01', type: 'N' }, { fullDate: '2026-03-02', type: 'OFF' }, { fullDate: '2026-03-03', type: 'D' },
    { fullDate: '2026-03-04', type: 'D' }, { fullDate: '2026-03-05', type: 'E' }, { fullDate: '2026-03-06', type: 'E' },
    { fullDate: '2026-03-07', type: 'OFF' }, { fullDate: '2026-03-08', type: 'D' }, { fullDate: '2026-03-09', type: 'E' },
    { fullDate: '2026-03-10', type: 'E' }, { fullDate: '2026-03-11', type: 'N' }, { fullDate: '2026-03-12', type: 'N' },
    { fullDate: '2026-03-13', type: 'OFF' }, { fullDate: '2026-03-14', type: 'D' }, { fullDate: '2026-03-15', type: 'D' },
    { fullDate: '2026-03-20', type: 'N' }, { fullDate: '2026-03-21', type: 'OFF' }, { fullDate: '2026-03-25', type: 'D' },
    { fullDate: '2026-03-30', type: 'E' }, { fullDate: '2026-03-31', type: 'N' },

    // 4월 근무
    { fullDate: '2026-04-01', type: 'OFF' }, { fullDate: '2026-04-02', type: 'D' }, { fullDate: '2026-04-03', type: 'D' }
])

// 2. 병동 환자 리스트
const wardPatients = ref([
    { id: 1, room: '501', name: '김철수', date: '2026-02-10', status: '안정' },
    { id: 2, room: '502', name: '이영희', date: '2026-02-15', status: '관찰필요' },
    { id: 3, room: '503', name: '박지민', date: '2026-02-18', status: '집중케어' },
    { id: 4, room: '505', name: '최환자', date: '2026-02-20', status: '수술후' },
    { id: 5, room: '506', name: '정병원', date: '2026-02-21', status: '퇴원예정' },
    { id: 6, room: '601', name: '한간호', date: '2026-02-22', status: '안정' },
    { id: 7, room: '602', name: '오아픔', date: '2026-02-22', status: '입원대기' },
    { id: 8, room: '603', name: '강회복', date: '2026-02-19', status: '회복중' },
    { id: 9, room: '605', name: '조수술', date: '2026-02-17', status: '경과관찰' },
    { id: 10, room: '608', name: '윤퇴원', date: '2026-02-22', status: '퇴원준비' }
])

// ======================================= 가공 데이터 =======================================
// [업무] 부서 진료 스케줄 필터링
const filteredDoctorSchedules = computed(() => {
    if (nurFilter.value === '전체') return doctorSchedules.value
    return doctorSchedules.value.filter(s => s.reservation_status === nurFilter.value)
})

// [업무] 달력 클릭 날짜 스케줄
const selectedDateSchedules = computed(() => {
    if (!selectedDate.value) return []
    const cleanSelected = selectedDate.value.replace(/-/g, '').substring(0, 8)
    return doctorSchedules.value.filter(s => {
        const rDate = s.reservation_date || s.reservationDate
        if (!rDate) return false
        return String(rDate).replace(/-/g, '').substring(0, 8) === cleanSelected
    })
})

// 추가
// 예약 리스트를 '진짜 빠른 시간순'으로 정렬하고 최대 3개까지 추출
const upcomingResList = computed(() => {
    return [...myReservations.value] // 원본 복사
        .filter(r => r.reservation_status === '예약')
        .sort((a, b) => {
            // 날짜(YYYYMMDD)와 시간(HH:mm)을 합쳐서 비교 (예: "202602230900" < "202602231000")
            const timeA = String(a.reservation_date) + String(a.reservation_time || '00:00').replace(':', '')
            const timeB = String(b.reservation_date) + String(b.reservation_time || '00:00').replace(':', '')
            return timeA.localeCompare(timeB) // 오름차순 정렬 (빠른 시간 우선) 
        })
        .slice(0, 3) // 상위 3개만 자르기
})

// D-Day 계산용 (정렬된 리스트의 0번이 무조건 가장 빠른 예약!)
const upcomingRes = computed(() => upcomingResList.value[0])

// 탭 이동 함수
const goToList = () => emit('changeView', 'res');  // 목록으로 가기
const goToResList = () => emit('changeView', 'doc_res');

// // [개인] D-Day 대상 & 필터링
// const upcomingRes = computed(() => 
//   myReservations.value.find(r => r.reservation_status === '예약')
// )

// [개인용] 내 전체 예약 리스트 중 선택한 필터에 맞는 것만 노출
const filteredMyReservations = computed(() => {
    if (resFilter.value === '전체') return myReservations.value
    return myReservations.value.filter(r => r.reservation_status === resFilter.value)
})

// [달력] 날짜 생성
const calendarDays = computed(() => {
    const year = calYear.value
    const month = calMonth.value - 1
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const days = []

    for (let i = 0; i < firstDay.getDay(); i++) days.push({ day: '', isCurrentMonth: false })
    for (let d = 1; d <= lastDay.getDate(); d++) {
        const mm = month + 1 < 10 ? '0' + (month + 1) : (month + 1)
        const dd = d < 10 ? '0' + d : d
        days.push({ day: d, isCurrentMonth: true, fullDate: `${year}-${mm}-${dd}` })
    }
    return days
})

// ======================================= 서버 통신 로직 =======================================
// [업무 조회] 간호사 본인의 부서 의사들 스케줄 가져오기
const fetchDoctorSchedules = async () => {
    try {
        const docListRes = await getAllDoctorsReq()
        const allStaff = docListRes.data || []
        // 내 부서명(props.userInfo.deptName)과 같은 의사를 찾음
        const myDeptDoc = allStaff.find(s => s.dept_name === props.userInfo.deptName)
        if (myDeptDoc) {
            const schedRes = await getDocSchedReq(myDeptDoc.staff_id)
            doctorSchedules.value = schedRes.data || []
        }
    } catch (e) { console.error("부서 업무 로드 실패", e) }
}

// [개인 조회] 본인이 예약한 내역을 서버에서 가져옴
const fetchReservations = async () => {
    try {
        const res = await getMyResReq()
        myReservations.value = res.data
    } catch (e) {
        console.error("예약 조회 실패", e)
    }
}

// ======================================= 기능 + 유틸리티 =======================================
// [개인] 내 진료 예약 취소
const cancelRes = async (id) => {
    if (!confirm("정말 예약을 취소하시겠습니까?")) return
    try {
        await cancelResReq(id)
        fetchReservations() // 취소 후 목록 새로고침
        alert("예약 취소가 완료되었습니다")
    } catch (e) {
        alert("오류가 발생했습니다")
    }
}

// [가공] 전화번호 포맷 (01012345678 -> 010-1234-5678)
const formatPhone = (phone) => {
    if (!phone) return '-';
    const clean = String(phone).replace(/[^0-9]/g, '');
    if (clean.length === 11) {
        return clean.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    } else if (clean.length === 10) {
        return clean.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }
};

// [가공] 날짜 포맷 (20260222 -> 2026년 02월 22일)
const formatDate = (date) => {
    if (!date) return '-'
    const s = String(date)

    // 1. YYYYMMDD 형태인 경우 (예: 20260208)
    if (s.length === 8) {
        return `${s.substring(0, 4)}년 ${s.substring(4, 6)}월 ${s.substring(6, 8)}일`
    }

    // 2. 날짜 객체나 ISO 스트링인 경우 (예: 2026-02-08T...)
    try {
        const d = new Date(date)
        const year = d.getFullYear()
        const month = d.getMonth() + 1
        const day = d.getDate()

        // 월/일이 10보다 작을 때 앞에 0을 붙이기 위함
        const mm = month < 10 ? `0${month}` : month
        const dd = day < 10 ? `0${day}` : day

        return `${year}년 ${mm}월 ${dd}일`
    } catch (e) {
        return s
    }
}

// [가공] 시간 포맷 (문자열에서 시간 부분만 00:00 형태로 추출)
const formatTime = (time) => {
    const t = String(time)
    return t.includes(' ') ? t.substring(11, 16) : t.substring(0, 5)
}

// [계산] 디데이 계산
const calculateDday = (dateStr) => {
    // 예: dateStr = "20260206" (YYYYMMDD)
    if (!dateStr) return 0;

    // 날짜 변환 (YYYY-MM-DD로 만들기)
    const formattedDate = String(dateStr).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3');
    // 결과: "2026-02-06"

    // Date 객체 생성
    const targetDate = new Date(formattedDate);
    const today = new Date();

    // 차이 계산 (밀리초 단위)
    const diff = targetDate - today;

    // 밀리초 -> '일(Day)' 단위로 변환
    // Math.ceil : 소수점 올림 처리
    return Math.ceil(diff / (1000 * 3600 * 24));
};

// [달력] 월 이동 함수
const changeMonth = (delta) => {
    let newMonth = calMonth.value + delta
    if (newMonth > 12) { calYear.value++; newMonth = 1 }
    else if (newMonth < 1) { calYear.value--; newMonth = 12 }
    calMonth.value = newMonth
}

// [달력] 날짜 클릭 시 선택 날짜 저장
const handleCalDateClick = (dateStr) => {
    selectedDate.value = dateStr; // 의사: 해당 날짜 예약 조회
};

// [달력] 특정 날짜에 진료 예약이 있는지 확인 (점 표시용)
const getEventsForDate = (str) => {
    if (!str) return [];
    // 달력 날짜: "2026-02-21" -> "20260221"
    const cleanTarget = str.replace(/-/g, '').substring(0, 8);

    return doctorSchedules.value.filter(s => {
        const rDate = s.reservation_date || s.reservationDate; // 스네이크/카멜 케이스 둘 다 대응
        if (!rDate) return false;

        // 서버 날짜에서도 숫자 8자리만 뽑아서 비교
        const cleanDate = String(rDate).replace(/-/g, '').substring(0, 8);
        return cleanDate === cleanTarget && s.reservation_status !== '취소';
    });
};

// 간호사 전용 달력 유틸리티
const getShiftForDate = (date) => nurseShifts.value.find(s => s.fullDate === date)?.type
const getShiftClass = (t) => t === 'D' ? 'day-shift' : (t === 'E' ? 'evening-shift' : (t === 'N' ? 'night-shift' : 'off-shift'))

// [달력] 오늘 날짜 하이라이트 여부 확인
const isToday = (str) => {
    const t = new Date()
    const d = new Date(str)
    return t.toDateString() === d.toDateString()
}

// [가공] 상태에 따른 배지 클래스 반환
const getBadgeClass = (s) => {
    if (s === '예약') return 'active'
    if (s === '완료') return 'done'
    if (s === '미방문') return 'noshow-badge'
    return 'cancel'
}

// 초기화 (Lifecycle)
onMounted(() => {
    fetchDoctorSchedules() // 의사 업무 로드
    fetchReservations()    // 개인 예약 로드
})
</script>

<style scoped>
.nurse-dash-wrapper {
    width: 100%;
}

.dash-home-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 40px;
    /* [수치보존] */
    margin-top: 10px;
}

/* 섹션 카드 & 헤더 */
.dash-card,
.section-card {
    background: #ffffff;
    border: 1px solid #eee;
    padding: 40px;
    margin-bottom: 20px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.card-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.card-head h3 {
    padding: 8px;
    font-size: 1.4rem;
    font-weight: 700;
    color: #005baa;
}

/* 정보 리스트 스타일 */
.info-list .info-item {
    font-size: 17px;
    margin-bottom: 12px;
}

.info-item .label {
    width: 100px;
    color: #888;
    font-weight: 700;
    font-size: 20px;
    padding: 8px;
}

.val {
    padding: 8px 8px 16px;
    font-size: 18px;
    margin-bottom: 18px;
    border-bottom: 1px solid #f5f5f5;
}

/* 현황 통계 (Stat Box) */
.stat-grid {
    display: flex;
    gap: 20px;
}

.stat-box {
    flex: 1;
    padding: 30px 20px;
    background-color: #f8f9fa;
    border: 1px solid #eee;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #666;
}

.stat-box span {
    display: block;
    font-size: 40px;
    font-weight: 800;
    color: #333;
    margin-bottom: -12px;
}

.stat-box.blue span {
    color: #005baa;
}

/* 병원 공통 테이블 */
.hospital-tbl {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

.hospital-tbl th {
    background-color: #f8f9fa;
    padding: 30px;
    text-align: left;
    font-weight: 600;
    color: #555;
    border-bottom: 2px solid #eee;
    font-size: 18px;
}

.hospital-tbl td {
    padding: 18px 30px;
    border-bottom: 1px solid #f0f0f0;
    color: #444;
    font-size: 18px;
    vertical-align: middle;
}

.bold-blue {
    color: #005baa;
    font-weight: 700;
}

/* 필터 및 버튼 디자인 */
.filter-tabs,
.toggle-group {
    display: flex;
    gap: 12px;
    margin-bottom: 25px;
}

.filter-btn,
.toggle-group button {
    padding: 10px 20px;
    background-color: #fff;
    border: 1px solid #ddd;
    color: #888;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.filter-btn.active,
.toggle-group button.active {
    background-color: #005baa;
    color: #fff;
    border-color: #005baa;
}

.btn-cancel-table {
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 600;
    background: transparent;
    color: #ff3b30;
    border: 1.5px solid #ff3b30;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-cancel-table:hover {
    background: #ff3b30;
    color: #fff;
}

.calendar-wrap {
    border: 1px solid #eee;
    padding: 20px;
}

/*  달력 레이아웃 (Calendar) */
.cal-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
    position: relative;
    padding: 10px 0;
}

/* 트렌디한 화살표 버튼 */
.month-selector {
    display: flex;
    align-items: center;
    gap: 40px;
}

.month-selector button {
    background: #ffffff;
    border: 1px solid #eeeeee;
    border-radius: 4px;   
    padding: 10px;
    cursor: pointer;
    transition: all 0.2s ease; 
    display: flex;
    align-items: center;
    justify-content: center;
    color: #555;
    box-shadow: 0 2px 5px rgba(0,0,0,0.02);
}

.month-selector button:hover {
    background-color: #f8f9fa;
    border-color: #005baa;
    color: #005baa;
    transform: translateY(-2px); 
    box-shadow: 0 4px 10px rgba(0, 91, 170, 0.1);
}

.month-selector h4 {
    font-size: 30px;        
    font-weight: 800;
    color: #333;
    letter-spacing: -1px;  
    margin: 0;
}

/* 범례 (형광펜 위치) */
.cal-legend {
    display: flex;
    gap: 20px;
    align-self: flex-end;  
    background: transparent;
    padding-right: 10px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 700;
    color: #888;
    white-space: nowrap;
}

.legend-item .bar {
    width: 25px;
    height: 6px;
    border-radius: 2px;
}

/* 달력 그리드 */
.cal-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    background-color: #eee;
    gap: 1px;
    border: 1px solid #eee;
}

.cal-day-head {
    background: #f8f9fa;
    padding: 15px;
    text-align: center;
    font-weight: 700;
    font-size: 16px;
}

.cal-cell {
    background: #fff;
    min-height: 120px;
    padding: 15px;
    cursor: pointer;
}

.cal-cell.today {
    background: #f0f6ff;
}

.cal-cell.selected {
    border: 2px solid #005baa;
}

.day-num {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    display: block;
    color: #444;
}

.cal-events {
    margin-top: 5px;
    display: flex;
    flex-direction: column;
    gap: 2px;
}

/* 8. 기타 유틸리티 (D-Day 등) */
.res-highlight {
    background: #f0f7ff;
    padding: 30px;
    text-align: center;
    border-radius: 8px;
}

.d-day {
    display: inline-block;
    background: #005baa;
    color: #fff;
    padding: 12px 18px;
    font-weight: 600;
    font-size: 60px;
    margin-bottom: 20px;
}

.res-time-txt {
    font-size: 24px;
    font-weight: 700;
    color: #444;
    margin-bottom: 5px;
}

.res-doc-txt {
    font-size: 20px;
    font-weight: 500;
    color: #666;
}

/* 상태 배지: 상태별 컬러 차별화 */
.status-badge {
    width: 80px;
    height: 34px;

    display: inline-flex;
    align-items: center;
    justify-content: center;

    font-size: 14px;
    font-weight: 700;
    text-align: center;
    white-space: nowrap;
    /* 글자가 줄바꿈되지 않게 방지 */
}

.status-badge.active {
    background: #e7f3ff;
    color: #005baa;
}

.status-badge.done {
    background: #e8f5e9;
    color: #2e7d32;
}

.status-badge.cancel {
    background: #ffebee;
    color: #c62828;
}

.status-badge.noshow {
    background: #f5f5f5;
    color: #616161;
}

.status-badge.noshow-badge {
    background: #f5f5f5;
    color: #616161;
}

.empty-msg {
    font-size: 20px;
    margin-top: 30px;
    text-align: center;
    color: #888;
}
</style>