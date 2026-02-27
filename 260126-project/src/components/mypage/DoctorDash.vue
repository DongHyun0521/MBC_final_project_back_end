<template>
    <div class="doctor-dash-wrapper">
        <div v-if="currentView === 'dash'" class="dash-home-grid">
            <section class="dash-card profile-card">
                <div class="card-head">
                    <h3>내 정보 요약</h3>
                </div>
                <div class="info-list">
                    <div class="info-item">
                        <span class="label">소속</span>
                        <p class="val">{{ userInfo.deptName || '소속없음' }}</p>
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
                    <h3>오늘 진료 현황</h3>
                    <span class="more-view">자세히 보기 +</span>
                </div>
                <div class="stat-grid">
                    <div class="stat-box blue">
                        <span>{{doctorSchedules.filter(s => s.reservation_status === '예약').length}}명</span><br>예약 대기
                    </div>
                    <div class="stat-box">
                        <span>{{doctorSchedules.filter(s => s.reservation_status === '완료').length}}</span><br>진료 완료
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

        <div v-if="currentView === 'doc_res'" class="view-section">
            <div class="section-card ">
                <div class="card-head">
                    <h3>진료 업무 일정</h3>
                    <div class="toggle-group">
                        <button :class="{ active: docViewMode === 'calendar' }"
                            @click="docViewMode = 'calendar'">달력</button>
                        <button :class="{ active: docViewMode === 'list' }" @click="docViewMode = 'list'">리스트</button>
                    </div>
                </div>

                <div v-if="docViewMode === 'calendar'" class="calendar-wrap">
                    <div class="cal-header">
                        <div class="month-selector">
                            <button @click="changeMonth(-1)">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2">
                                    <path d="M15 18l-6-6 6-6" />
                                </svg>
                            </button>
                            <h4>{{ calYear }}년 {{ calMonth }}월</h4>
                            <button @click="changeMonth(1)">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2">
                                    <path d="M9 18l6-6-6-6" />
                                </svg>
                            </button>
                        </div>

                        <div class="cal-legend">
                            <div class="legend-item"><span class="bar active"></span>예약</div>
                            <div class="legend-item"><span class="bar done"></span>완료</div>
                            <div class="legend-item"><span class="bar cancel"></span>취소</div>
                            <div class="legend-item"><span class="bar noshow"></span>미방문</div>
                        </div>
                    </div>

                    <div class="cal-grid">
                        <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">
                            {{ day }}
                        </div>
                        <div v-for="(date, idx) in calendarDays" :key="idx"
                            :class="['cal-cell', { 'diff-month': !date.isCurrentMonth, 'today': isToday(date.fullDate), 'selected': selectedDate === date.fullDate }]"
                            @click="handleCalDateClick(date.fullDate)">
                            <span class="day-num">{{ date.day }}</span>
                            <div class="cal-events">
                                <div v-for="(evt, i) in getEventsForDate(date.fullDate).slice(0, 2)" :key="i"
                                    :class="['cal-dot-bar', getBadgeClass(evt.reservation_status)]">
                                </div>
                                <div v-if="getEventsForDate(date.fullDate).length > 2" class="cal-more-text">
                                    +{{ getEventsForDate(date.fullDate).length - 2 }}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div v-if="selectedDate" class="selected-date-list">
                        <h4>{{ formatDate(selectedDate) }} 예약 리스트</h4>
                        <table class="hospital-tbl mt-10">
                            <thead>
                                <tr>
                                    <th>시간</th>
                                    <th>환자명</th>
                                    <th>상태</th>
                                    <th class="txt-center">관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="sc in selectedDateSchedules" :key="sc.reservation_id">
                                    <td>{{ formatTime(sc.reservation_time) }}</td>
                                    <td class="bold-blue">{{ sc.patient_name }}</td>
                                    <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{
                                        sc.reservation_status }}</span></td>
                                    <td class="txt-center">
                                        <div v-if="sc.reservation_status === '예약'" class="btn-group">
                                            <button class="btn-action complete"
                                                @click="completeTreatment(sc)">완료</button>
                                            <button class="btn-action cancel" @click="handleForceCancel(sc)">취소</button>
                                        </div>
                                        <span v-else>-</span>
                                    </td>
                                </tr>
                                <tr v-if="selectedDateSchedules.length === 0">
                                    <td colspan="4" class="txt-center py-20">해당 날짜에 예약이 없습니다.</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div v-else>
                    <div class="filter-tabs">
                        <button v-for="st in ['전체', '예약', '완료', '취소', '미방문']" :key="st"
                            :class="['filter-btn', { active: docFilter === st }]" @click="docFilter = st">{{ st }}</button>
                    </div>
                    <table class="hospital-tbl">
                        <thead>
                            <tr>
                                <th>날짜</th>
                                <th>시간</th>
                                <th>환자명</th>
                                <th>상태</th>
                                <th>메모</th>
                                <th class="txt-center">관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="sc in filteredDoctorSchedules" :key="sc.reservation_id">
                                <td class="bold-text">{{ formatDate(sc.reservation_date) }}</td>
                                <td>{{ formatTime(sc.reservation_time) }}</td>
                                <td class="bold-blue">{{ sc.patient_name }}</td>
                                <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{
                                    sc.reservation_status }}</span></td>
                                <td>{{ sc.reservation_memo || '-' }}</td>
                                <td class="txt-center">
                                    <div v-if="sc.reservation_status === '예약'" class="btn-group">
                                        <button class="btn-action complete" @click="completeTreatment(sc)">완료</button>
                                        <button class="btn-action cancel" @click="handleForceCancel(sc)">취소</button>
                                    </div>
                                    <span v-else>-</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div v-if="filteredDoctorSchedules.length === 0" class="empty-msg">내역이 없습니다.</div>
                </div>
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
                            <td>{{ formatDate(res.reservation_date) }}</td>
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
import { useRouter, useRoute } from 'vue-router'
import { getMyResReq, cancelResReq, getAllDoctorsReq, getDocSchedReq, completeResByDocReq, forceCancelResByDocReq } from '@/api/reservation'

    const props = defineProps({
        userInfo: Object,
        currentView: String
    })

    // 부모(MyPage.vue)의 탭 상태를 바꾸기 위한 통로 .개설
    const emit = defineEmits(['changeView'])

    const router = useRouter()
    const route = useRoute()

    // [데이터 주머니] 
    const myReservations = ref([])   // [개인용] 의사 본인이 환자로서 예약한 내역
    const doctorSchedules = ref([])  // [업무용] 의사가 오늘 진료해야 할 환자 리스트

    // [업무용 상태 변수]
    const docViewMode = ref('calendar') // 달력 보기 / 리스트 보기 전환
    const docFilter = ref('예약')       // 진료 상태 필터 (전체, 예약, 완료 등)
    const calYear = ref(new Date().getFullYear())
    const calMonth = ref(new Date().getMonth() + 1)
    const selectedDate = ref(null)     // 달력에서 클릭한 날짜 저장

    // [개인용 상태 변수]
    const resFilter = ref('전체')       // 내 예약 내역 필터링 상태

    // ======================================= 가공 데이터 =======================================
    // [업무용] 전체 진료 스케줄 중 사용자가 선택한 상태(docFilter)만 필터링
    const filteredDoctorSchedules = computed(() => {
        if (docFilter.value === '전체') return doctorSchedules.value
        return doctorSchedules.value.filter(s => s.reservation_status === docFilter.value)
    })

    // [업무용/달력] 달력에서 특정 날짜를 클릭했을 때, 그 날의 예약만 하단에 노출
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


    // [개인용] 내가 환자일 때 가장 가까운 예약 하나 찾기 (대시보드 요약용)
    // const upcomingRes = computed(() =>
    //     myReservations.value.find(r => r.reservation_status === '예약')
    // )

    // [개인용] 내 전체 예약 리스트 중 선택한 필터에 맞는 것만 노출
    const filteredMyReservations = computed(() => {
        if (resFilter.value === '전체') return myReservations.value
        return myReservations.value.filter(r => r.reservation_status === resFilter.value)
    })

    // [달력] 현재 연/월에 맞는 날짜 그리드(1일~말일)를 생성하는 로직
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
    // [업무 조회] 의사 본인의 환자 진료 스케줄을 서버에서 가져옴
    const fetchDoctorSchedules = async () => {
        if (!props.userInfo.id) return
        try {
            const docListRes = await getAllDoctorsReq()
            const allStaff = docListRes.data || []
            // 전체 직원 목록 중 현재 로그인한 userInfo.id와 매칭되는 staff_id를 찾음
            const myInfo = allStaff.find(s => String(s.user_id) === String(props.userInfo.id))
            if (myInfo) {
                const schedRes = await getDocSchedReq(myInfo.staff_id)
                doctorSchedules.value = schedRes.data || []
            }
        } catch (e) { console.error("업무 스케줄 로드 실패", e) }
    }

    // [개인 조회] 의사 본인이 환자로서 예약한 내역을 서버에서 가져옴
    const fetchReservations = async () => {
        try {
            const res = await getMyResReq()
            myReservations.value = res.data
        } catch (e) {
            console.error("예약 조회 실패", e)
        }
    }

    // ======================================= 기능 + 유틸리티 =======================================
    // [업무] 진료 완료 처리 (예약 시간이 지나야만 가능)
    const completeTreatment = async (scheduleItem) => {
        if (isFutureTime(scheduleItem)) {
            alert("아직 예약 시간이 되지 않았습니다.\n진료 시간이 지난 후에 처리해 주세요.")
            return
        }

        if (confirm(`${scheduleItem.patient_name} 님 진료를 완료하시겠습니까?`)) {
            try {
                const res = await completeResByDocReq(scheduleItem.reservation_id)

                if (res.data === 'success') {
                    alert("진료가 완료되었습니다.")
                    await fetchDoctorSchedules()
                } else {
                    alert("처리에 실패했습니다. 권한이 없거나 이미 처리된 예약입니다.")
                }
            } catch (e) {
                console.error("완료 처리 오류:", e)
                alert("서버와 통신 중 오류가 발생했습니다.")
            }
        }
    }

    // [업무] 예약 강제 취소 (의사 권한으로 환자 예약 취소)
    const handleForceCancel = async (scheduleItem) => {
        if (confirm("예약을 강제 취소(삭제)하시겠습니까?")) {
            try {
                const res = await forceCancelResByDocReq(scheduleItem.reservation_id)

                if (res.data === 'success') {
                    alert("정상적으로 취소되었습니다.")
                    await fetchDoctorSchedules()
                } else {
                    alert("취소에 실패했습니다. 권한을 확인해 주세요.")
                }
            } catch (e) {
                console.error("취소 처리 오류:", e)
                alert("서버와 통신 중 오류가 발생했습니다.")
            }
        }
    }

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
            return cleanDate === cleanTarget;
        });
    };

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

    // [업무] 예약 시간이 지났는지 체크 (미래 시간 진료 완료 방지용)
    const isFutureTime = (item) => {
        if (!item) return false;
        try {
            const now = new Date();
            // 숫자만 추출 (예: 2026-02-06 -> 20260206)
            let d = String(item.reservation_date).replace(/[^0-9]/g, '');
            let t = String(item.reservation_time).replace(/[^0-9]/g, '');

            // 시간 문자열이 길면(날짜포함) 뒤에서 6자리만 자름
            if (t.length > 6) t = t.slice(-6);
            t = t.padEnd(6, '0'); // 혹시 짧으면 0으로 채움

            const year = parseInt(d.substring(0, 4));
            const month = parseInt(d.substring(4, 6)) - 1; // 월은 0부터
            const day = parseInt(d.substring(6, 8));
            const hour = parseInt(t.substring(0, 2));
            const min = parseInt(t.substring(2, 4));

            const targetDate = new Date(year, month, day, hour, min);

            // 예약시간이 현재보다 미래면 true
            return targetDate > now;
        } catch (e) {
            return false; // 에러나면 일단 통과 (사용자 경험 방해 X)
        }
    };

    // 초기화 (Lifecycle)
    onMounted(() => {
        fetchDoctorSchedules() // 의사 업무 로드
        fetchReservations()    // 개인 예약 로드
    })
</script>

<style scoped>
/*  대시보드 홈 - 그리드 레이아웃 (MemberDash와 동일) */
.dash-home-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 40px;
    margin-top: 10px;
}

/*  섹션 카드 & 헤더 (패딩 40px 통일) */
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
    margin-bottom: 5px;
}

.card-head h3 {
    padding: 8px;
    font-size: 1.7rem;
    font-weight: 800;
    color: #005baa;
}

.label {
    padding: 8px;
    font-size: 20px;
}

.val {
    padding: 8px 8px 16px;
    font-size: 18px;
    margin-bottom: 18px;
    border-bottom: 1px solid #f5f5f5;
}

/*  정보 요약 및 기타 유틸리티 */
.info-list .info-item {
    font-size: 17px;
    margin-bottom: 12px;
}

.info-item .label {
    width: 100px;
    color: #888;
    font-weight: 700;
}

/*  오늘 진료 현황 (의사 전용 통계 박스) */
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

/*  필터 탭 & 뷰 전환 버튼 (차량 관리 버튼 톤) */
.filter-tabs,
.toggle-group {
    display: flex;
    gap: 12px;
    margin-bottom: 15px;
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

/*  병원 공통 테이블 (차량 관리와 100% 동일 수치) */
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

/*  관리 액션 버튼 (진료 완료/취소) */
.btn-group {
    display: flex;
    gap: 8px;
    justify-content: flex-start;
}

.btn-action {
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    border: 1px solid #ddd;
    background: #fff;
}

.btn-action.complete {
    color: #005baa;
    border-color: #005baa;
}

.btn-action.complete:hover {
    background: #e7f3ff;
}

.btn-action.cancel:hover {
    background: #f8f8f8;
    color: #dc3545;
    border-color: #dc3545;
}

/*  달력 레이아웃 (Calendar) */
.calendar-wrap {
    border: 1px solid #eee;
    padding: 20px;
}

.cal-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    margin-bottom: 2px;
    position: relative;
    padding: 20px 0;
}

.cal-header h4 {
    font-size: 24px;
    font-weight: 700;
}

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
}

/* 달력 내부 일정 막대기 스타일 (추가 필요!) */
.cal-events {
    margin-top: 5px;
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.cal-dot-bar {
    height: 5px;
    /* 막대기 두께 */
    width: 20%;
    border-radius: 2px;
}

/* 상태별 색상  */
.cal-dot-bar.active {
    background-color: #005baa;
}

.cal-dot-bar.done {
    background-color: #ccc;
}

.cal-dot-bar.cancel {
    background-color: #dc3545;
}

/* 추가 */
/* 달력 헤더 (가운데 정렬 + 기준점 확보) */
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

/* 컬러 바 (기존 색상 유지) */
.bar.active {
    background-color: #005baa;
}

.bar.done {
    background-color: #ccc;
}

.bar.cancel {
    background-color: #dc3545;
}

.bar.noshow {
    background-color: #fbb900;
}

.cal-more-text {
    font-size: 11px;
    color: #888;
    text-align: right;
    margin-top: 2px;
}

/* 달력 숫자 강조 */
.day-num {
    font-size: 16px;
    font-weight: 600;
    color: #444;
}

.cal-cell.diff-month .day-num {
    color: #ccc;
    /* 이전/다음 달 숫자는 흐리게 */
}

.selected-date-list h4 {
    padding: 8px;
    font-size: 26px;
    font-weight: 700;
    margin-top: 48px;
    color: #005baa;
}

/*  기타 하이라이트 및 유틸리티 */
.res-highlight {
    background: #f0f7ff;
    padding: 30px;
    text-align: center;
}

.d-day {
    display: inline-block;
    /* background: #005baa; */
    color: #005baa;
    /* padding: 12px 18px; */
    font-weight: 800;
    font-size: 60px;
    margin-bottom: 10px;
}

.res-time-txt {
    font-size: 26px;
    font-weight: 700;
    color: #444;
    margin-bottom: 5px;
}

.res-doc-txt {
    font-size: 22px;
    font-weight: 500;
    color: #666;
}
/* 내 진료 예약 버튼 css */
/* 취소 버튼: 깔끔한 레드 아웃라인 */
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

.bold-blue {
    color: #005baa;
    font-weight: 700;
}

.txt-center {
    text-align: left;
}

/* 해당하는 내역이 없습니다 메세지 */
.empty-msg {
    font-size: 20px;
    margin-top: 30px;
    text-align: center;
}

/* 예정된 예약이 없습니다 */
.empty-res {
    font-size: 20px;
    padding: 8px;
}

.py-20 {
    text-align: center;
}

/* 추가(내 예약 내역-대시보드) */
.clickable-card {
    cursor: pointer;
    transition: all 0.2s ease-in-out;
}
.clickable-card:hover {
    border-color: #005baa;
    transform: translateY(-5px);
}

.more-view {
    font-size: 18px;
    color: #888;
    font-weight: 700;
}

/* 서브 리스트 스타일 */
.sub-res-list {
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px dashed #d1e6ff;
}

.sub-res-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 15px;
    background: #f8f9fa;
    border-radius: 8px;
    margin-bottom: 5px;
    font-size: 15px;
    font-weight: 600;
}

.sub-time { color: #005baa; }
.sub-info { color: #555; }
</style>