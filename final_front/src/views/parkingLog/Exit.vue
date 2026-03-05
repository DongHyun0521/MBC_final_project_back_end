<template>
  <div class="kiosk-main-wrapper">
    <!-- 출차 대기 화면 -->
    <div v-if="viewMode === 'idle'" class="screen-container screen-idle" @click="startExit">
      <div class="center-content">
        <div class="logo-box">
          <img class="main-logo-img" src="@/assets/txtlogo2.png" alt="S-HOSPITAL" />
        </div>

        <div class="touch-guidance pulse-animation">
          <h2 class="touch-main-msg">SMART PARKING SYSTEM</h2>
          <p class="touch-sub-msg">주차장 출차를 시작합니다</p>
        </div>
      </div>
    </div>

    <!-- 출차 입력 화면 -->
    <div v-if="viewMode === 'input'" class="screen-container screen-input">
      <div class="center-content">
        <h1 class="page-title">주차장 출차 안내</h1>

        <div v-if="!isLoading" class="upload-section">
          <p class="guide-text">인식할 차량의 정면 사진을 선택해 주세요.</p>

          <label for="fileInput" class="professional-btn">
            <svg viewBox="0 0 24 24" class="svg-icon">
              <path fill="currentColor"
                d="M4,4H7L9,2H15L17,4H20A2,2,0,0,1,22,6V18A2,2,0,0,1,20,20H4A2,2,0,0,1,2,18V6A2,2,0,0,1,4,4M12,7A5,5,0,1,0,17,12A5,5,0,0,0,12,7M12,9A3,3,0,1,1,9,12A3,3,0,0,1,12,9Z" />
            </svg>
            차량 사진 선택하기
          </label>

          <input type="file" id="fileInput" @change="handleFileUpload" accept="image/*" hidden />
        </div>

        <div v-if="isLoading" class="loading-section">
          <div class="modern-spinner"></div>
          <h3 class="loading-msg">번호판 인식중...</h3>
          <p class="loading-sub">잠시만 기다려 주십시오</p>
        </div>
      </div>
    </div>

    <!-- 출차 결과 화면 -->
    <div v-if="viewMode === 'result'" class="screen-container screen-result">
      <div class="center-content">
        <h1 class="page-title highlight-title">출차 결과 안내</h1>

        <div class="result-card-container">
          <!-- 이미지 카드 -->
          <div class="info-card image-card">
            <h2 class="card-title">인식된 차량 이미지</h2>
            <div class="image-wrapper">
              <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" class="captured-img" />
              <div v-else class="no-image-placeholder">이미지 없음</div>
            </div>
          </div>

          <!-- 데이터 카드 -->
          <div class="info-card data-card">
            <!-- 입차 기록 없음 -->
            <template v-if="!entryTime">
              <h2 class="card-title error-title">차량 조회 실패</h2>

              <table class="res-table">
                <tr>
                  <th>차량 번호</th>
                  <td class="plate-num warn">{{ resultText || '-' }}</td>
                </tr>
                <tr>
                  <th>알림</th>
                  <td class="status-warn">입차 기록 없음</td>
                </tr>
              </table>

              <div class="error-visual-box compact">
                <svg viewBox="0 0 24 24" class="svg-error">
                  <path fill="currentColor" d="M12,2L1,21H23L12,2M12,6L19.53,19H4.47L12,6M11,10V14H13V10H11M11,16V18H13V16H11Z" />
                </svg>
                <p class="error-msg">확인 불가 (입차 기록 없음)</p>
                <p class="sub-guide">관리자 호출 또는 다시 시도해 주세요</p>
              </div>
            </template>

            <!-- 무료 회차 -->
            <template v-else-if="parkingFee === 0">
              <h2 class="card-title">출차 확인</h2>

              <table class="res-table">
                <tr>
                  <th>차량 번호</th>
                  <td class="plate-num">{{ resultText || '-' }}</td>
                </tr>
                <tr>
                  <th>구분</th>
                  <td>
                    <span :class="['chip', isMember ? 'chip-member' : 'chip-guest']">
                      {{ isMember ? '회원 차량' : '일반 차량' }}
                    </span>
                  </td>
                </tr>
                <tr>
                  <th>입차 시각</th>
                  <td>{{ entryTime || '-' }}</td>
                </tr>
                <tr>
                  <th>출차 시각</th>
                  <td>{{ exitTime || '-' }}</td>
                </tr>
                <tr>
                  <th>총 주차 시간</th>
                  <td class="time-num">{{ totalParkingTime }}</td>
                </tr>
                <tr>
                  <th>결제 금액</th>
                  <td class="fee-num">0원 (무료)</td>
                </tr>
              </table>

              <p class="welcome-msg pay-msg">30분 이내 회차 차량입니다. 안녕히 가십시오.</p>
            </template>

            <!-- 유료 결제 -->
            <template v-else>
              <h2 class="card-title">출차 확인</h2>

              <table class="res-table">
                <tr>
                  <th>차량 번호</th>
                  <td class="plate-num">{{ resultText || '-' }}</td>
                </tr>
                <tr>
                  <th>구분</th>
                  <td>
                    <span :class="['chip', isMember ? 'chip-member' : 'chip-guest']">
                      {{ isMember ? '회원 차량' : '일반 차량' }}
                    </span>
                  </td>
                </tr>
                <tr>
                  <th>입차 시각</th>
                  <td>{{ entryTime || '-' }}</td>
                </tr>
                <tr>
                  <th>출차 시각</th>
                  <td>{{ exitTime || '-' }}</td>
                </tr>
                <tr>
                  <th>총 주차 시간</th>
                  <td class="time-num">{{ totalParkingTime }}</td>
                </tr>
                <tr>
                  <th>결제 금액</th>
                  <td class="fee-num">{{ formattedFee }}원</td>
                </tr>
                <tr>
                  <th>요금 안내</th>
                  <td class="fee-guide">
                    (최초 30분 무료 / 이후 30분당 {{ isMember ? '1,000' : '2,000' }}원)
                  </td>
                </tr>
              </table>

              <div class="action-area">
                <button class="professional-btn pay-btn" @click="handlePayment">
                  결제하기
                </button>
              </div>
            </template>
          </div>
        </div>

        <!-- 자동 복귀 타이머: 무료/실패일 때만 보여주기 -->
        <div v-if="parkingFee <= 0" class="timer-section">
          <div class="timer-bar">
            <div class="timer-progress" :style="{ width: (countdown / 3) * 100 + '%' }"></div>
          </div>
          <p class="timer-text"><b>{{ countdown }}초</b> 후 초기 화면으로 돌아갑니다</p>
        </div>
      </div>
    </div>

    <!-- 결제 완료 화면 -->
    <div v-if="viewMode === 'success'" class="screen-container screen-result">
      <div class="center-content">
        <h1 class="page-title highlight-title">결제 완료</h1>

        <div class="result-card-container single">
          <div class="info-card data-card">
            <h2 class="card-title">정산 완료 내역</h2>

            <table class="res-table">
              <tr>
                <th>차량 번호</th>
                <td class="plate-num">{{ resultText || '-' }}</td>
              </tr>
              <tr>
                <th>총 주차 시간</th>
                <td class="time-num">{{ totalParkingTime }}</td>
              </tr>
              <tr>
                <th>결제 금액</th>
                <td class="fee-num">{{ formattedFee }}원</td>
              </tr>
              <tr>
                <th>안내</th>
                <td class="status-success">정상 결제 완료</td>
              </tr>
            </table>

            <p class="welcome-msg pay-msg">안녕히 가십시오. 즐거운 하루 되세요!</p>
          </div>
        </div>

        <div class="timer-section">
          <div class="timer-bar">
            <div class="timer-progress" :style="{ width: (countdown / 3) * 100 + '%' }"></div>
          </div>
          <p class="timer-text"><b>{{ countdown }}초</b> 후 초기 화면으로 돌아갑니다</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { uploadExitImage, requestPayment } from '@/api/exit.js';
  // 배경 이미지 import 제거함

  const viewMode = ref('idle'); // 화면 모드 (idle, input, result, success)

  const selectedFile = ref(null);
  const resultText = ref('');
  const entryTime = ref('');
  const exitTime = ref('');
  const parkingFee = ref(0); // 주차 요금 (-1: 조회 실패, 0: 무료, >0: 유료)
  const isMember = ref(false);
  const debugImages = ref([]);
  const isLoading = ref(false);
  
  const currentLogId = ref(null); // 결제 시 필요한 주차 기록 고유 번호(PK)
  const currentMemId = ref(null);

  const countdown = ref(3);
  let timerId = null;

  const formattedFee = computed(() => {
    if (parkingFee.value === -1) return '확인 불가';
    return parkingFee.value.toLocaleString(); // [내장] 1000 -> 1,000 변환
  });

  // 총 주차 시간
  const totalParkingTime = computed(() => {
    // 입차 시간이나 출차 시간 데이터가 하나라도 없으면 계산을 중단하고 '-'를 반환
    if (!entryTime.value || !exitTime.value) return '-';

    try {
      const start = new Date(entryTime.value.replace(/-/g, '/')); 
      const end = new Date(exitTime.value.replace(/-/g, '/'));

      // 출차 시간에서 입차 시간을 빼서 밀리초(ms) 단위의 차이를 구함
      const diffMs = end - start;

      // 밀리초를 시간 단위로 변환 (1000ms * 60초 * 60분으로 나눈 뒤 소수점 버림)
      const diffHrs = Math.floor(diffMs / (1000 * 60 * 60));

      // 전체 차이에서 시간으로 계산된 부분을 뺀 나머지(%)를 분 단위로 변환
      const diffMins = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

      // 계산된 결과값을 X시간 Y분 형태의 템플릿 리터럴 문자열로 최종 반환
      return `${diffHrs}시간 ${diffMins}분`;
    } catch (e) {
      return '계산 불가';
    }
});

  const startExit = () => {
    viewMode.value = 'input';
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    await submitImage();
  };

  const submitImage = async () => {
    isLoading.value = true;
    
    resultText.value = '';
    entryTime.value = '';
    exitTime.value = '';
    parkingFee.value = 0;
    debugImages.value = [];

    try {
      const response = await uploadExitImage(selectedFile.value);
      const data = response.data;
      
      resultText.value = data.resultText;
      entryTime.value = data.entryTime;
      exitTime.value = data.exitTime;
      parkingFee.value = data.parkingFee;
      isMember.value = data.isMember;
      debugImages.value = data.debugImages;
      
      currentLogId.value = data.parkingLogId;
      currentMemId.value = data.memId;

      viewMode.value = 'result';

      // 무료(0) 이거나 입차기록 없음(-1)일 경우 모두 자동 카운트다운 시작
      if (data.parkingFee <= 0) {
        startCountdown();
      }
      
    } catch (error) {
      console.error(error);
      alert('차량 인식 중 오류 발생');
      resetToIdle();
    } finally {
      if (viewMode.value !== 'result') {
         isLoading.value = false;
      } else {
         isLoading.value = false;
      }
    }
  };

  const handlePayment = async () => {
    if (!currentLogId.value) {
      alert("결제할 정보가 없습니다.");
      return;
    }

    const paymentData = {
      parkingLogId: currentLogId.value,
      memId: currentMemId.value,
      amount: parkingFee.value
    };

    try {
      const res = await requestPayment(paymentData);
      
      if (res.data === 'success') {
        viewMode.value = 'success';
        startCountdown();
      } else {
        alert("결제 실패");
      }
    } catch (err) {
      console.error(err);
      alert("서버 통신 오류");
    }
  };

  const startCountdown = () => {
    countdown.value = 3; 
    
    if (timerId) clearInterval(timerId);

    timerId = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
            clearInterval(timerId);
            resetToIdle();
        }
    }, 1000);
  };

  const resetToIdle = () => {
    if (timerId) {
        clearInterval(timerId);
        timerId = null;
    }

    selectedFile.value = null;
    resultText.value = '';
    entryTime.value = '';
    exitTime.value = '';
    parkingFee.value = 0;
    debugImages.value = [];
    currentLogId.value = null;
    currentMemId.value = null;
    
    const input = document.getElementById('fileInput');
    if(input) input.value = '';

    viewMode.value = 'idle';
  };
</script>

<style scoped>
.kiosk-main-wrapper {
  font-family: 'Pretendard', sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  background: linear-gradient(135deg, var(--bg-light) 0%, #e0eafc 100%);
  overflow: hidden;
}

.screen-container {
  width: 100%;
  min-height: 100%;
  flex-direction: column; /* 세로 정렬 보장 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 60px; /* 위아래 패딩 조절 */
}

.center-content {
  width: 100%;
  max-width: 1400px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-box {
  width: 100%;
  height: 170px;
  display: flex;
  justify-content: center;
  margin-bottom: 80px;
  margin-top: 50px;
}

.main-logo-img {
  height: 100%;
  width: auto;
  object-fit: contain;
}

.pulse-animation {
  animation: pulseBtn 2s infinite ease-in-out;
}

@keyframes pulseBtn {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.05); opacity: 0.8; }
  100% { transform: scale(1); opacity: 1; }
}

.touch-guidance {
  margin-top: 120px;
  text-align: center;
}

.touch-main-msg {
  font-size: 30px;
  margin-bottom: 12px;
}

.touch-sub-msg {
  font-size: 22px;
}

/* 안내 화면 */
.page-title {
  font-size: 70px;
  margin-bottom: 20px;
  margin-top: 30px;
}

.upload-section { text-align: center; }

.guide-text {
  font-size: 28px;
  margin-bottom: 80px;
  letter-spacing: -1.8px;
  text-align: center;
}

.professional-btn {
  background: #005baa;
  color: #ffffff;
  padding: 50px 60px;
  font-size: 30px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(0, 91, 170, 0.2);
  border: none;
}

.pay-btn{
  width: 100%;
  max-width: 520px;
  padding: 32px 40px;
  font-size: 32px;
}

.professional-btn:active{
  transform: translateY(1px);
}

.svg-icon {
  width: 32px;
  height: 32px;
  margin-right: 15px;
}

/* 결과 */
.result-card-container {
  display: flex;
  gap: 20px;
  width: 100%;
  margin-bottom: 20px;
}

.result-card-container.single{
  justify-content: center;
}

.info-card {
  flex: 1;
  background: #fff;
  padding: 30px 35px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.05);
  border: 1px solid #eef2f6;
}

.card-title{
  font-size: 26px;
  font-weight: 800;
  color: var(--text-dark);
  margin-bottom: 20px;
}

.res-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.res-table th {
  text-align: left;
  font-size: 23px;
  color: var(--text-grey);
  padding: 20px 0;
  border-bottom: 1px solid #f1f5f9;
  width: 35%;
}

.res-table td {
  text-align: left;
  font-size: 1.5rem;
  font-weight: 700;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}

.plate-num {
  font-size: 3.5rem;
  color: #005baa;
  letter-spacing: 3px;
}

.plate-num.warn { color: #fb6900; }

.highlight-title { font-size: 70px; }

.welcome-msg {
  font-size: 32px;
  font-weight: 500;
  color: var(--main-blue);
  margin-top: 40px;
  text-align: center;
  letter-spacing: -0.5px;
}

/* 총 주차 시간 / 결제금액 강조 */
.time-num{
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: 1px;
  color: #1f2d3d;
}

.fee-num{
  font-size: 3.0rem;
  font-weight: 900;
  letter-spacing: 1px;
  color: var(--main-blue);
}

.fee-guide{
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-grey);
}

/* 구분 칩 */
.chip{
  display: inline-flex;
  align-items: center;
  padding: 10px 16px;
  border-radius: 999px;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.chip-member{
  background: rgba(0, 91, 170, 0.12);
  color: var(--main-blue);
  border: 1px solid rgba(0, 91, 170, 0.18);
}

.chip-guest{
  background: rgba(251, 185, 0, 0.16);
  color: #856404;
  border: 1px solid rgba(251, 185, 0, 0.25);
}

/* 액션 영역 */
.action-area{
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 34px;
}

/* 경고/오류 */
.error-title{ color: #e74c3c; }

.error-visual-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  margin-top: 10px;
}

.error-visual-box.compact{
  padding: 10px 0;
}

.svg-error {
  width: 80px;
  height: 80px;
  color: #e74c3c;
  margin-bottom: 15px;
}

.error-msg {
  font-size: 22px !important;
  font-weight: 700;
  color: #e74c3c;
  margin: 0 0 15px 0;
  text-align: center;
}

.sub-guide {
  font-size: 18px !important;
  color: var(--text-grey);
  font-weight: 500;
  text-align: center;
}

.status-success{
  color: #0b6b2a;
  font-weight: 900;
}

.status-warn{
  color: #fb6900;
  font-weight: 900;
}

/* 이미지 */
.image-wrapper {
  width: 100%;
  height: 160px;
  background: #ffffff;
  overflow: hidden;
  align-items: center;
  display: flex;
  justify-content: center;
}

.captured-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 로딩 */
.loading-section { margin-top: 80px; }

.loading-msg { text-align: center; font-size: 30px; }
.loading-sub { text-align: center; font-size: 24px; }

.modern-spinner {
  width: 80px;
  height: 80px;
  border: 8px solid rgba(0, 91, 170, 0.1);
  border-top: 8px solid var(--main-blue);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 30px;
  box-shadow: 0 0 15px rgba(0, 91, 170, 0.05);
}

@keyframes spin { to { transform: rotate(360deg); } }

/* 타이머 */
.timer-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: auto;
  padding-bottom: 30px;
}

.timer-bar {
  width: 100%;
  height: 14px;
  background: rgba(0, 91, 170, 0.15);
  border-radius: 10px;
  overflow: hidden;
  margin-top: 40px;
}

.timer-progress {
  height: 100%;
  background: #005baa;
  animation: timerShrink 5s linear forwards;
}

@keyframes timerShrink {
  from { width: 100%; }
  to { width: 0%; }
}

.timer-text {
  font-size: 24px;
  color: #333;
  margin-top: 20px;
  text-align: center;
}

/* (필수) template에서 바로 쓰는 계산값: script 안 건드리고 여기서만 처리 */
</style>