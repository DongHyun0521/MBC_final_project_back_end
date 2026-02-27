<template>
  <div class="reservation-page-wrap">
    <div class="reservation-card">
      <div class="res-header">
        <h2>진료 예약 신청</h2>
        <div class="title-bar"></div>
        <p class="res-desc">정보를 확인하고 예약을 진행해 주세요</p>
      </div>

      <div class="res-form-body">
        <div v-if="isFixedDoc" class="fixed-doc-card transition-box">
          <div class="fixed-label">선택하신 의료진</div>
          <div class="fixed-info">
            <span class="f-dept">{{ route.query.deptName }}</span>
            <span class="f-doc">{{ route.query.docName }} 의사</span>
            <button @click="resetToGeneral" class="btn-change">변경</button>
          </div>
        </div>

        <div v-else class="input-section">
          <label>진료 정보</label>
          <div class="grid-row">
            <select v-model="form.medDeptId" @change="fetchDoctors" class="res-select">
              <option value="">진료과 선택</option>
              <option v-for="d in depts" :key="d.med_dept_id" :value="d.med_dept_id"> {{ d.med_dept_name }}</option>
            </select>

            <select v-if="form.medDeptId" v-model="form.doctorId" @change="resetDateTime" class="res-select">
              <option value="">담당 의사 선택</option>
              <option v-for="s in doctors" :key="s.staff_id" :value="s.staff_id">
                {{ s.staff_name }} 의사
              </option>
            </select>
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.doctorId">
          <label>환자 정보 입력</label>
          <div class="grid-col-group">
            <div class="input-wrapper">
              <label class="sub-label-sm">성함</label>
              <input type="text" v-model="form.patientName" @input="handleNameInput" placeholder="한글 2자 이상 입력"
                class="res-input" :class="{ 'input-error': form.patientName && !isValidName }">
              <p v-if="form.patientName && !isValidName" class="error-msg">정확한 한글 이름을 입력해 주세요.</p>
            </div>

            <div class="input-wrapper">
              <label class="sub-label-sm">전화번호</label>
              <div class="phone-split-group">
                <select v-model="phonePart1" class="res-select phone-part">
                  <option value="010">010</option>
                  <option value="011">011</option>
                  <option value="070">070</option>
                  <option value="02">02</option>
                  <option value="031">031</option>
                  <option value="032">032</option>
                  <option value="051">051</option>
                  <option value="042">042</option>
                </select>
                <span class="dash">-</span>
                <input type="text" v-model="phonePart2" ref="phone2Ref" @input="handlePhonePart2" maxlength="4"
                  class="res-input phone-part center-txt">
                <span class="dash">-</span>
                <input type="text" v-model="phonePart3" ref="phone3Ref" @input="handlePhonePart3" maxlength="4"
                  class="res-input phone-part center-txt">
              </div>
              <p v-if="(phonePart2 || phonePart3) && !isValidPhone" class="error-msg">휴대폰 번호를 모두 입력해 주세요.</p>
            </div>
          </div>
        </div>

        <div class="input-section transition-box" v-if="isValidName && isValidPhone">
          <label>예약 날짜</label>

          <p class="guide-text">
            원활한 진료 진행을 위해 1시간 이내의 예약은 불가합니다.<br>너른 양해 부탁드리겠습니다.
          </p>

          <div class="custom-calendar-container">
            <div class="date-display-box" @click="toggleCalendar" :class="{ active: showCalendar }">
              <span v-if="form.reservationDate" class="selected-date-txt">{{ form.reservationDate }}</span>
              <span v-else class="placeholder">날짜를 선택해 주세요</span>
              <span class="calendar-icon">▼</span>
            </div>

            <div v-if="showCalendar" class="calendar-popup">
              <div class="cal-header">
                <button @click="prevMonth" class="cal-nav-btn">&lt;</button>
                <span class="cal-title">{{ currentYear }}년 {{ currentMonth }}월</span>
                <button @click="nextMonth" class="cal-nav-btn">&gt;</button>
              </div>
              <div class="cal-grid">
                <div v-for="d in ['일', '월', '화', '수', '목', '금', '토']" :key="d" class="cal-day-name">{{ d }}</div>
                <div v-for="n in startDayOfWeek" :key="'empty' + n" class="cal-day empty"></div>
                <div v-for="date in daysInMonth" :key="date" class="cal-day" :class="{
                  /* 1. 내가 '클릭한 날짜'라면? -> 파란색 배경 (.selected) */
                  'selected': isSelected(date),

                  /* 2. '지난 날짜'거나 '시간이 지난 오늘'이라면? -> 회색 글씨 (.disabled) */
                  'disabled': isPastDate(date),

                  /* 3. [핵심] '오늘 날짜' 스타일(.today)을 언제 줄 것인가? */
                  /* 조건: 오늘 날짜이면서(AND) + 예약 마감이 안 된(!isPastDate) 상태여야 함 */
                  /* 즉, 시간이 지나서 disabled가 되면 today 스타일은 벗겨버림 -> 회색만 남음! */
                  'today': isToday(date) && !isPastDate(date)
                }" @click="selectDate(date)">

                  {{ date }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.reservationDate">
          <label>예약 시간</label>
          <div class="time-grid">
            <button v-for="time in allTimeSlots" :key="time" type="button" class="time-btn"
              :class="{ 'selected': form.reservationTime === time }" :disabled="isBooked(time) || isPastOrSoon(time)"
              @click="form.reservationTime = time">

              <span class="time-txt">{{ time }}</span>

              <span v-if="isBooked(time)" class="booked-label">마감</span>
              <span v-else-if="isPastOrSoon(time)" class="booked-label gray"></span>
            </button>
          </div>
        </div>

        <button v-if="form.reservationTime" @click="submitReservation" class="res-submit-btn"
          :disabled="isBooked(form.reservationTime) || isPastOrSoon(form.reservationTime)">
          {{ isBooked(form.reservationTime) ? '이미 예약된 시간입니다' : '예약 확정하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDeptsReq, getDoctorsReq, addResReq, getDocSchedReq } from '@/api/reservation.js'

const route = useRoute();
const useRouterInstance = useRouter();

const depts = ref([]);
const doctors = ref([]);
const bookedSlots = ref([]);
const isFixedDoc = ref(false);

const phonePart1 = ref('010');
const phonePart2 = ref('');
const phonePart3 = ref('');
const phone2Ref = ref(null);
const phone3Ref = ref(null);

const showCalendar = ref(false);
const now = new Date();
const currentYear = ref(now.getFullYear());
const currentMonth = ref(now.getMonth() + 1);

const form = reactive({
  memId: '',
  medDeptId: '',
  doctorId: '',
  patientName: '',
  patientPhone: '',
  reservationDate: '',
  reservationTime: '',
  visitType: '초진',
  reservationStatus: '예약',
  reservationMemo: '홈페이지 예약'
});

const getTodayStr = () => {
  const d = new Date();
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}
const todayStr = getTodayStr();

// =========================================
// 2. Computed
// =========================================
const daysInMonth = computed(() => new Date(currentYear.value, currentMonth.value, 0).getDate());
const startDayOfWeek = computed(() => new Date(currentYear.value, currentMonth.value - 1, 1).getDay());

const allTimeSlots = computed(() => {
  const slots = [];
  for (let h = 9; h < 19; h++) {
    const hour = h < 10 ? `0${h}` : h;
    slots.push(`${hour}:00`, `${hour}:30`);
  }
  return slots;
});

const isValidName = computed(() => /^[가-힣]{2,}$/.test(form.patientName));
const isValidPhone = computed(() => phonePart2.value.length >= 3 && phonePart3.value.length === 4);

// =========================================
// 3. 달력 및 날짜 함수
// =========================================
const toggleCalendar = () => { showCalendar.value = !showCalendar.value; };
const prevMonth = () => {
  if (currentMonth.value === 1) { currentMonth.value = 12; currentYear.value--; } else { currentMonth.value--; }
};
const nextMonth = () => {
  if (currentMonth.value === 12) { currentMonth.value = 1; currentYear.value++; } else { currentMonth.value++; }
};
const formatDateString = (day) => {
  const m = currentMonth.value < 10 ? `0${currentMonth.value}` : currentMonth.value;
  const d = day < 10 ? `0${day}` : day;
  return `${currentYear.value}-${m}-${d}`;
};
const isSelected = (day) => form.reservationDate === formatDateString(day);
const isToday = (day) => formatDateString(day) === todayStr;

// [수정] 오늘 날짜라도 마감 시간(17:30) 지났으면 비활성화 처리
const isPastDate = (day) => {
  const dateStr = formatDateString(day);
  if (dateStr < todayStr) return true; // 과거 날짜 비활성

  if (dateStr === todayStr) {
    const now = new Date();
    // 17시 30분이 지났으면 오늘 날짜 예약 불가
    if (now.getHours() > 17 || (now.getHours() === 17 && now.getMinutes() >= 30)) {
      return true;
    }
  }
  return false;
};

const selectDate = (day) => {
  // 비활성 날짜는 클릭 방지
  if (isPastDate(day)) return;

  form.reservationDate = formatDateString(day);
  showCalendar.value = false;
  fetchBookedSlots();
};

// =========================================
// 4. 입력 핸들러
// =========================================
const handleNameInput = (e) => { form.patientName = e.target.value.replace(/[^가-힣]/g, ''); };
const handlePhonePart2 = (e) => {
  phonePart2.value = e.target.value.replace(/[^0-9]/g, '');
  if (phonePart2.value.length === 4) phone2Ref.value.blur();
  if (phonePart2.value.length === 4) phone3Ref.value.focus();
};
const handlePhonePart3 = (e) => { phonePart3.value = e.target.value.replace(/[^0-9]/g, ''); };

// =========================================
// 5. 서버 통신 및 핵심 로직
// =========================================

const isBooked = (time) => {
  return bookedSlots.value.some(bookedTime => bookedTime.trim() === time.trim());
};

const isPastOrSoon = (timeSlot) => {
  if (!form.reservationDate) return true;

  const now = new Date();
  const [year, month, day] = form.reservationDate.split('-').map(Number);
  const [hour, minute] = timeSlot.split(':').map(Number);

  const targetDate = new Date(year, month - 1, day, hour, minute);
  const minBookingTime = new Date(now.getTime() + (60 * 60 * 1000));

  return targetDate < minBookingTime;
};

const fetchBookedSlots = async () => {
  if (!form.doctorId || !form.reservationDate) return;
  try {
    const res = await getDocSchedReq(form.doctorId);
    const data = Array.isArray(res.data) ? res.data : [];
    const targetDate = form.reservationDate.replaceAll('-', '');

    bookedSlots.value = data
      .filter(item => {
        const status = String(item.reservation_status || item.reservationStatus || '예약').trim();
        if (status !== '예약') return false;
        let dbDate = String(item.reservation_date || item.reservationDate || '').trim();
        if (dbDate.includes('T')) dbDate = dbDate.split('T')[0];
        if (dbDate.includes(' ')) dbDate = dbDate.split(' ')[0];
        dbDate = dbDate.replaceAll('-', '');
        return dbDate === targetDate;
      })
      .map(item => {
        let t = String(item.reservation_time || item.reservationTime || '').trim();
        if (t.includes('T')) t = t.split('T')[1];
        if (t.includes(' ')) t = t.split(' ')[1];
        return t.substring(0, 5);
      });
  } catch (err) { bookedSlots.value = []; }
};

const fetchDoctors = async () => {
  if (!form.medDeptId) return;
  try { const res = await getDoctorsReq(form.medDeptId); doctors.value = res.data; } catch (err) { }
};

const resetDateTime = () => { form.reservationDate = ''; form.reservationTime = ''; };

const resetToGeneral = () => {
  isFixedDoc.value = false;
  form.doctorId = '';
  form.medDeptId = '';
  useRouterInstance.replace('/reservation');
};

const submitReservation = async () => {
  if (!isValidName.value || !isValidPhone.value) return;

  if (isPastOrSoon(form.reservationTime)) {
    alert("현재 시간 기준 1시간 이후부터 예약이 가능합니다.");
    return;
  }

  form.patientPhone = `${phonePart1.value}-${phonePart2.value}-${phonePart3.value}`;
  const loginInfo = JSON.parse(sessionStorage.getItem('loginId') || '{}');
  const fullDateTime = `${form.reservationDate}T${form.reservationTime}:00`;
  const finalDeptId = Number(form.medDeptId || route.query.deptId);

  if (!finalDeptId) { alert("진료과 정보 오류"); return; }

  const resDto = {
    memId: Number(loginInfo.memId || loginInfo.mem_id),
    medDeptId: finalDeptId,
    doctorId: Number(form.doctorId),
    reservationDate: Number(form.reservationDate.replaceAll('-', '')),
    reservationTime: fullDateTime,
    patientName: form.patientName,
    patientPhone: form.patientPhone,
    reservationType: '일반예약',
    visitType: form.visitType,
    reservationStatus: '예약',
    reservationMemo: form.reservationMemo
  };

  try {
    const res = await addResReq(resDto);

    if (res.data === 'success' || res.data === true) {
      alert('예약이 성공적으로 완료되었습니다');
      useRouterInstance.push('/checkreservation');
    }
    else if (res.data === 'soon') {
      alert('원활한 진료 준비를 위해, 당일 예약은 현재 시간 기준 1시간 이후부터 예약이 가능합니다.\n너른 양해 부탁드립니다');
    }
    else if (res.data === 'duplicate') {
      alert('이미 예약된 시간입니다. 다른 시간을 선택해 주세요.');
      fetchBookedSlots();
    } else {
      alert('예약에 실패했습니다. 정보를 다시 확인해주세요.');
    }
  } catch (err) {
    console.error(err);
    alert('오류가 발생했습니다.');
  }
};

onMounted(async () => {
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) {
    alert("로그인이 필요합니다.");
    useRouterInstance.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }

  try {
    const res = await getDeptsReq();
    depts.value = res.data;
    if (!route.query.deptId && route.query.deptName) {
      const found = depts.value.find(d => d.med_dept_name === route.query.deptName);
      if (found) form.medDeptId = found.med_dept_id;
    } else if (route.query.deptId) {
      form.medDeptId = Number(route.query.deptId);
    }
  } catch (err) { }

  if (route.query.docId) {
    form.doctorId = Number(route.query.docId);
    if (!form.medDeptId && route.query.deptId) form.medDeptId = Number(route.query.deptId);
    isFixedDoc.value = true;
    fetchBookedSlots();
  }
});
</script>

<style scoped>
/* [추가] 안내 문구 스타일 */
.guide-text {
  font-size: 15px;
  color: #b72323;
  margin-bottom: 12px;
  font-weight: 500;
  line-height: 1.4;
}

.reservation-page-wrap {
  display: flex;
  justify-content: center;
  padding: 100px 20px;
  min-height: 120vh;
  background-color: #ffffff;
}

.reservation-card {
  width: 100%;
  max-width: 770px;
  background: #fff;
  padding: 80px 50px;
  border: 1px solid #eee;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
  height: fit-content;
  margin-bottom: 100px;
}

.res-header {
  text-align: center;
  margin-bottom: 50px;
}

.res-header h2 {
  font-size: 42px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 800;
}

.title-bar {
  width: 120px;
  height: 3px;
  background: #005baa;
  margin: 0 auto 20px;
}

.res-desc {
  font-size: 18px;
  color: #666;
  font-weight: 300;
}

.res-form-body {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.input-section>label {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
  border-left: 4px solid #005baa;
  padding-left: 10px;
}

.sub-label-sm {
  display: block;
  font-size: 16px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 600;
}

.res-input,
.res-select {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  font-size: 15px;
  background: #fff;
  transition: 0.2s;
  color: #333;
}

.res-input:focus,
.res-select:focus {
  border-color: #005baa;
  outline: none;
  box-shadow: 0 0 0 3px rgba(1, 113, 233, 0.1);
}

.grid-row {
  display: flex;
  gap: 15px;
}

.grid-row>* {
  flex: 1;
}

.grid-col-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.phone-split-group {
  display: flex;
  align-items: center;
  text-align: center;
  gap: 8px;
}

.phone-part {
  text-align: center;
}

.dash {
  font-weight: 700;
  color: #ccc;
}

.center-txt {
  text-align: center;
}

.input-error {
  border-color: #e03131;
  background-color: #fff5f5;
}

.error-msg {
  font-size: 12px;
  color: #e03131;
  margin-top: 5px;
}

/* 캘린더 */
.custom-calendar-container {
  position: relative;
  width: 100%;
}

.date-display-box {
  width: 100%;
  padding: 18px;
  border: 2px solid #eee;
  background: #fff;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  transition: 0.2s;
}

.date-display-box:hover,
.date-display-box.active {
  border-color: #005baa;
  box-shadow: 0 5px 15px rgba(1, 113, 233, 0.1);
}

.calendar-popup {
  position: absolute;
  top: 110%;
  left: 0;
  width: 100%;
  background: #fff;
  border: 1px solid #eee;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  z-index: 100;
  padding: 20px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.cal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.cal-title {
  font-size: 20px;
  font-weight: 800;
  color: #333;
}

.cal-nav-btn {
  background: none;
  border: 1px solid #eee;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  font-weight: bold;
  color: #666;
  transition: 0.2s;
}

.cal-nav-btn:hover {
  background: #f0f7ff;
  color: #005baa;
  border-color: #005baa;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  text-align: center;
}

.cal-day-name {
  font-weight: 600;
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.cal-day {
  height: 45px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 15px;
  color: #333;
  transition: 0.2s;
}

.cal-day:hover:not(.empty):not(.disabled) {
  background-color: #f0f7ff;
  color: #005baa;
  font-weight: 700;
}

.cal-day.selected {
  background-color: #005baa;
  color: #fff !important;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(1, 113, 233, 0.4);
}

/* [수정] 비활성 날짜: 회색 글씨만 적용 (박스 배경 X) */
.cal-day.disabled {
  color: #ccc;
  background-color: transparent;
  cursor: not-allowed;
}

.cal-day.today {
  border: 1px solid #005baa;
  color: #005baa;
  font-weight: 700;
}

/* 시간 버튼 그리드 */
.time-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.time-btn {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  color: #555;
}

.time-txt {
  font-size: 15px;
  font-weight: 500;
}

.time-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #aaa;
  color: #333;
}

.time-btn.selected {
  background: #005baa;
  border-color: #005baa !important;
  color: #fff;
  box-shadow: 0 4px 10px rgba(1, 113, 233, 0.3);
  transform: translateY(-1px);
}

.time-btn.selected .time-txt {
  font-weight: 700;
}

/* 예약 불가 (마감 or 시간 지남) */
.time-btn:disabled {
  background: #f8f9fa !important;
  border: 1px solid #e9ecef !important;
  color: #adb5bd !important;
  cursor: not-allowed !important;
  opacity: 0.7;
  box-shadow: none !important;
  transform: none !important;
}

.time-btn:disabled .time-txt {
  color: #adb5bd !important;
  text-decoration: line-through;
}

.booked-label {
  font-size: 10px;
  color: #ff6b6b;
  font-weight: 700;
  position: absolute;
  bottom: 4px;
}

.booked-label.gray {
  color: #adb5bd;
}

/* 시간 지난 건 회색 글씨 */

.fixed-doc-card {
  background: #f0f7ff;
  border: 1px solid #a1d8f3;
  padding: 25px 30px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.fixed-label {
  font-size: 16px;
  color: #005baa;
  font-weight: 600;
}

.fixed-info {
  display: flex;
  align-items: center;
}

.f-dept {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.f-doc {
  font-size: 22px;
  color: #005baa;
  font-weight: 800;
}

.btn-change {
  margin-left: auto;
  background: #fff;
  border: 1px solid #ddd;
  padding: 6px 14px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: 0.2s;
}

.res-submit-btn {
  width: 100%;
  padding: 20px;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 20px;
  box-shadow: 0 10px 20px rgba(1, 113, 233, 0.15);
}

.res-submit-btn:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 15px 25px rgba(1, 113, 233, 0.25);
}

.res-submit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.transition-box {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>