<template>
  <div class="kiosk-main-wrapper">
    <div v-if="viewMode === 'idle'" class="screen-container screen-idle" @click="startEntry">
      <div class="center-content">
        <div class="logo-box">
          <img class="main-logo-img" src="@/assets/txtlogo2.png" alt="S-HOSPITAL" />
        </div>
        <div class="touch-guidance pulse-animation">
          <h2 class="touch-main-msg">SMART PARKING SYSTEM</h2>
          <p class="touch-sub-msg">주차장 입차를 시작합니다</p>
          <!-- <span class="touch-prompt-txt">화면을 터치해 주세요</span> -->
        </div>
      </div>
    </div>

    <div v-if="viewMode === 'input'" class="screen-container screen-input">
      <div class="center-content">
        <h1 class="page-title">주차장 입차 안내</h1>

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

    <div v-if="viewMode === 'result'" class="screen-container screen-result">
      <div class="center-content">
        <h1 class="page-title highlight-title">입차 결과 안내</h1>

        <div class="result-card-container">
          <div class="info-card image-card">
            <h2 class="card-title">인식된 차량 이미지</h2>
            <div class="image-wrapper">
              <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" class="captured-img" />
              <div v-else class="no-image-placeholder">이미지 없음</div>
            </div>
          </div>

          <div class="info-card data-card">
            <template v-if="resultText && !isAlreadyParked">
              <h2 class="card-title">인식 결과 상세</h2>
              <table class="res-table">
                <tr>
                  <th>차량 번호</th>
                  <td class="plate-num">{{ resultText }}</td>
                </tr>
                <tr>
                  <th>입차 시각</th>
                  <td>{{ entryTime || '시간 정보 없음' }}</td>
                </tr>
                <tr>
                  <th>상태</th>
                  <td class="status-success">정상 입차</td>
                </tr>
              </table>
              <p class="welcome-msg">어서 오십시오. 즐거운 하루 되세요!</p>
            </template>

            <template v-else-if="isAlreadyParked">
              <h2 class="card-title warning-title">중복 입차 안내</h2>
              <table class="res-table warning">
                <tr>
                  <th>차량 번호</th>
                  <td class="plate-num warn">{{ resultText }}</td>
                </tr>
                <tr>
                  <th>알림</th>
                  <td class="status-warn">이미 입차된 차량</td>
                </tr>
              </table>
              <div class="warning-guide-box">
                <p>차단기가 열리지 않으면 호출 버튼을 눌러주세요</p>
              </div>
            </template>

            <template v-else>
              <h2 class="card-title error-title">인식 오류</h2>
              <div class="error-visual-box">
                <svg viewBox="0 0 24 24" class="svg-error">
                  <path fill="currentColor"
                    d="M12,2L1,21H23L12,2M12,6L19.53,19H4.47L12,6M11,10V14H13V10H11M11,16V18H13V16H11Z" />
                </svg>
                <p class="error-msg">번호판 인식에 실패했습니다</p>
                <p class="sub-guide">관리자 호출 또는 다시 시도해 주세요</p>
              </div>
            </template>
          </div>
        </div>

        <div class="timer-section">
          <div class="timer-bar">
            <div class="timer-progress" :style="{ width: (countdown / 5) * 100 + '%' }"></div>
          </div>
          <p class="timer-text"><b>{{ countdown }}초</b> 후 초기 화면으로 돌아갑니다</p>
        </div>
      </div>
    </div>

    <div v-if="viewMode === 'full'" class="screen-container screen-full">
      <div class="center-content">
        <!-- <div class="logo-box compact-logo">
          <img class="main-logo-img" src="@/assets/txtlogo2.png" alt="S-HOSPITAL" />
        </div> -->

        <h1 class="page-title error-title compact-title">주차장 만차 안내</h1>

        <div class="result-card-container single-card">
          <div class="info-card data-card full-status-card">
            <h2 class="card-title error-title">입차 제한</h2>

            <div class="error-visual-box">
              <svg viewBox="0 0 24 24" class="svg-error compact-svg">
                <path fill="currentColor"
                  d="M12,2L1,21H23L12,2M12,6L19.53,19H4.47L12,6M11,10V14H13V10H11M11,16V18H13V16H11Z" />
              </svg>
              <p class="error-msg">현재 주차 공간이 부족합니다</p>
              <p class="sub-guide">
                방문객이 많아 입차가 제한되고 있습니다 <br />
                잠시 후 다시 시도해 주시기 바랍니다
              </p>
            </div>
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
import { ref } from 'vue';
import { uploadEntryImage } from '@/api/entry.js';
import axios from 'axios';

const viewMode = ref('idle'); // 현재 화면 모드 (idle, input, result, full)
const isLoading = ref(false); // 로딩 스피너 표시 여부
const selectedFile = ref(null); // 사용자가 선택한 사진 파일 객체
const debugImages = ref([]); // 백엔드에서 받은 OCR 처리 과정 이미지 리스트
const resultText = ref(''); // 인식된 차량 번호
const isAlreadyParked = ref(false); // 이미 주차된 차량인지 여부
const entryTime = ref(''); // 입차 확정 시각
const countdown = ref(3); // 초기 화면 복귀까지 남은 초
let timerId = null; // 타이머(setInterval)를 멈추기 위해 보관하는 ID


const startEntry = () => {
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
  debugImages.value = [];
  isAlreadyParked.value = false;

  try {
    const response = await uploadEntryImage(selectedFile.value);
    const data = response.data;

    // 데이터 구조 확인을 위한 필수 로그
    console.log("백엔드 수신 데이터:", data);

    debugImages.value = data.debugImages || [];
    resultText.value = data.resultText || '';

    if (data.entryTime === 'ALREADY_PARKED') {
      isAlreadyParked.value = true;
      entryTime.value = '';
    } else {
      isAlreadyParked.value = false;
      entryTime.value = data.entryTime;
    }

    viewMode.value = 'result';
    countdown.value = 3;

    // [내장] 1초마다 countdown을 깎는 타이머 실행
    if (timerId) clearInterval(timerId);
    timerId = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timerId); // 0초가 되면 타이머 중지
        resetToIdle(); // 초기 화면으로 복귀
      }
    }, 1000);

  } catch (error) {
    console.error(error);
    alert('인식에 실패했습니다. 다시 시도해주세요.');
    const input = document.getElementById('fileInput');
    if (input) input.value = '';
    selectedFile.value = null;
    isLoading.value = false;
  } finally {
    if (viewMode.value !== 'result') {
      isLoading.value = false;
    }
  }
};

const resetToIdle = () => {
  viewMode.value = 'idle';
  isLoading.value = false;
  selectedFile.value = null;
  debugImages.value = [];
  resultText.value = '';
  entryTime.value = '';
  if (timerId) {
    clearInterval(timerId);
    timerId = null;
  }
  const input = document.getElementById('fileInput');
  if (input) input.value = '';
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
  min-height: 90%;
  flex-direction: column;
  /* 세로 정렬 보장 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 60px;
  /* 위아래 패딩 조절 */
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
  0% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
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

/* 주차장 입차 안내 화면 */
.page-title {
  font-size: 70px;
  margin-bottom: 20px;
  margin-top: 30px;
}

.upload-section {
  text-align: center;
}

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

.svg-icon {
  width: 32px;
  height: 32px;
  margin-right: 15px;
}

/* 입차 결과 */
.result-card-container {
  display: flex;
  gap: 40px;
  width: 100%;
  margin-bottom: 20px;
}

.info-card {
  flex: 1;
  background: #fff;
  padding: 40px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
  border: 1px solid #eef2f6;
}

.res-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.res-table th {
  text-align: left;
  font-size: 1.6rem;
  color: var(--text-grey);
  padding: 20px 0;
  border-bottom: 1px solid #f1f5f9;
  width: 35%;
}

.res-table td {
  text-align: left;
  font-size: 1.8rem;
  font-weight: 700;
  padding: 20px 0;
  border-bottom: 1px solid #f1f5f9;
}

.plate-num {
  font-size: 4.5rem;
  color: #005baa;
  letter-spacing: 3px;
}

.plate-num.warn {
  color: #fb6900;
}

.highlight-title {
  font-size: 70px;
}

.welcome-msg {
  font-size: 32px;
  font-weight: 500;
  color: var(--main-blue);
  margin-top: 40px;
  text-align: center;
  letter-spacing: -0.5px;
}

/* 중복 입차 안내 박스 정돈 */
.warning-guide-box {
  margin-top: 40px;
  padding: 30px;
  background: #fff8e6;
  border-radius: 12px;
  border-left: 8px solid var(--accent-yellow);
  text-align: center;
}

.warning-guide-box p {
  font-size: 26px;
  font-weight: 600;
  color: #856404;
  margin: 0;
}

/* 인식 오류 상황 레이아웃 */
.error-visual-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 20px 0;
}

.svg-error {
  width: 100px;
  height: 100px;
  color: #e74c3c;
  margin-bottom: 30px;
}

.error-msg {
  font-size: 38px !important;
  font-weight: 700;
  color: #e74c3c;
  margin: 0 0 15px 0;
}

.sub-guide {
  font-size: 24px !important;
  color: var(--text-grey);
  font-weight: 500;
}

/* 타이머 섹션 구조화 */
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
  from {
    width: 100%;
  }

  to {
    width: 0%;
  }
}

.timer-text {
  font-size: 24px;
  color: #333;
  margin-top: 20px;
  text-align: center;
}

.image-wrapper {
  width: 100%;
  height: 200px;
  background: #ffffff;
  overflow: hidden;
  align-items: center;
}

.captured-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  align-items: center;
}

/* 번호판 인식 */
.loading-section {
  margin-top: 80px;
}

.loading-msg {
  text-align: center;
  font-size: 30px;
}

.loading-sub {
  text-align: center;
  font-size: 24px;
}

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

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>

