<template>
  <div class="vehi-regi-wrap">
    <div class="regi-card">
      <div class="regi-header">
        <h2>차량 정보 등록</h2>
        <div class="title-bar"></div>
        <p class="regi-desc">환자 및 보호자 차량 정보를 정확히 입력해 주세요</p>
      </div>

      <form @submit.prevent="handleVehiRegi" class="regi-form">
        <div class="input-section">
          <label for="vehicleNum">차량 번호</label>
          <div class="input-inner">
            <input 
              type="text" 
              id="vehicleNum" 
              v-model="vehi.vehicleNum" 
              ref="idInput" 
              placeholder="예) 12가 3456" 
              @input="checkVehiNum"
              class="res-input"
            />
            <p v-if="vehiMsg" class="status-txt error">{{ vehiMsg }}</p>
          </div>  
        </div>

        <div class="input-section">
          <label for="vehicleType">차종 선택</label>
          <div class="input-inner">
            <select id="vehicleType" v-model="vehi.vehicleType" class="res-select">
              <option value="">차종을 선택해 주세요</option>
              <option value="경차">경차</option>
              <option value="승용차">승용차</option>
              <option value="SUV">SUV</option>
              <option value="기타">기타</option>
            </select>
          </div>
        </div>

        <div class="input-section">
          <label>유종 선택</label>
          <div class="fuel-grid">
            <label v-for="fuel in ['휘발유', '경유', 'LPG', '전기차']" :key="fuel" class="fuel-tile">
              <input type="radio" :value="fuel" v-model="vehi.fuelType" class="hide-radio" />
              <span class="fuel-txt">{{ fuel }}</span>
            </label>
          </div>
        </div>

        <div class="btn-area">
          <button type="submit" class="regi-submit-btn" :disabled="!isReady">
            차량 등록하기
          </button> 
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// [API] 차량 등록 요청 함수
import { addVehiReq } from '@/api/vehicle' 

const router = useRouter() // 페이지 이동 도구
const route = useRoute()   // 현재 주소 정보

// 로그인 정보 저장용
const memId = ref(null)
const userId = ref('')

// 차량 등록 폼 데이터
const vehi = ref({
  vehicleNum: '',      // 차량 번호 (예: 12가3456)
  vehicleType: '',     // 차종 (소형, 중형 등)
  fuelType: '휘발유'    // 유종 (기본값: 휘발유)
})

// 유효성 검사 메시지 (차량 번호 관련)
const vehiMsg = ref('') 

onMounted(() => {
  // 세션에서 로그인 정보 확인
  const loginData = sessionStorage.getItem('loginId') 
  
  if (!loginData) {
    // 로그인이 안 되어 있으면 로그인 페이지로 튕겨냄 (원래 보려던 페이지 주소 기억)
    alert("로그인이 필요한 서비스입니다")
    router.push({ path: '/login', query: { redirect: route.fullPath } })
  } else {
    // 로그인 정보가 있으면 데이터 세팅
    const user = JSON.parse(loginData)
    memId.value = user.memId 
    userId.value = user.id
  }
})


/* [차량 번호] 입력 실시간 검사 */
const checkVehiNum = () => {
  // 1. 대문자 변환 & 공백 제거
  let val = vehi.value.vehicleNum.toUpperCase().replace(/\s/g, '')
  
  // 2. 한글, 영어, 숫자 외 특수문자 제거
  val = val.replace(/[^A-Z0-9가-힣]/g, '')
  
  // 3. 정제된 값 다시 입력창에 반영
  vehi.value.vehicleNum = val
  
  // 4. 차량 번호 형식 정규식 (숫자 2~3자리 + 한글 1자리 + 숫자 4자리)
  const vehiRegex = /^\d{2,3}[가-힣]{1}\d{4}$/
  
  // 5. 유효성 판별 및 메시지 출력
  if (!val) {
    vehiMsg.value = "차량 번호를 입력해 주세요"
  } else if (!vehiRegex.test(val)) {
    vehiMsg.value = "형식이 올바르지 않습니다 (예: 12가3456)"
  } else {
    vehiMsg.value = "" // 통과 시 메시지 비움
  }
}

/* [등록 버튼] 활성화 조건 (계산된 속성) */
const isReady = computed(() => { 
  const vehiRegex = /^\d{2,3}[가-힣]{1}\d{4}$/
  // 차량 번호 형식이 맞고 && 차종이 선택되어 있어야 함
  return vehiRegex.test(vehi.value.vehicleNum) && vehi.value.vehicleType !== ''
})


/* [차량 등록] 버튼 클릭 핸들러 */
const handleVehiRegi = async () => {
  // 유효성 통과 못했으면 함수 종료 (방어 코드)
  if (!isReady.value) return;
  
  // 전송할 데이터 복사
  const saveData = {
    ...vehi.value,
  }

  try {
    const res = await addVehiReq(saveData)
    
    if (res.data === "success") {
      alert("차량 등록이 완료되었습니다.")
      // 마이페이지 차량 관리 탭으로 슝 이동
      router.push({ path: '/mypage', query: { tab: 'vehi' } }) 
    } 
    else if (res.data === "duplicate") {
      alert("이미 등록된 차량 번호입니다.")
    } 
    else {
      alert("등록에 실패했습니다. 정보를 다시 확인해 주세요")
    }

  } catch (err) {
    console.error("차량 등록 서버 에러:", err)
    alert("서버 통신 오류가 발생했습니다")
  }
}
</script>

<style scoped>
.vehi-regi-wrap {
  font-family: 'Pretendard', -apple-system, sans-serif !important;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background-color: #ffffff;
  padding: 60px 20px;
}

.regi-card {
  width: 100%;
  max-width: 600px;
  background: #fff;
  padding: 60px 50px;
  border-radius: 4px;
  border: 1px solid #eee;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
}

.regi-header {
  text-align: center;
  margin-bottom: 50px;
}

.regi-header h2 {
  font-size: 40px;
  color: #404347;
  margin-bottom: 12px;
  font-weight: 700;
}

.title-bar {
  width: 120px;
  height: 3px;
  background: #005baa;
  margin: 0 auto 20px;
}

.regi-desc {
  font-size: 18px;
  color: #888;
  font-weight: 300;
}

.regi-form {
  display: flex;
  flex-direction: column;
  gap: 35px;
}

.input-section label {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #4e4e4e;
  margin-bottom: 12px;
}

/* 입력 필드 스타일 */
.res-input, .res-select {
  width: 100%;
  padding: 15px;
  border: 1px solid #ddd;
  font-size: 15px;
  background: #f9f9f9;
  transition: 0.3s;
}

.res-input:focus, .res-select:focus {
  border-color: #005baa;
  outline: none;
  background: #fff;
}

/* 유종 선택 그리드*/
.fuel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.fuel-tile {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #ddd;
  background: #f9f9f9;
  cursor: pointer;
  transition: 0.2s;
  text-align: center;
}

.fuel-txt {
  margin-top: 13px;
  font-family: 'pretendard';
  font-size: 16.5px;
  color: #666;
  font-weight: 500;
  display: inline-block;
}

.hide-radio {
  display: none !important;
}

.fuel-tile:hover {
  border-color: #005baa;
  background: #f0f7ff;
}

/* 라디오 체크 시 스타일 */
.fuel-tile:has(.hide-radio:checked) {
  background: #005baa;
  border-color: #005baa;
}

.fuel-tile:has(.hide-radio:checked) .fuel-txt {
  color: #fff;
  font-weight: 600;
}

/* 메시지 스타일 */
.status-txt {
  font-size: 12px;
  margin-top: 8px;
}

.error {
  color: #dc3545;
}

/* 등록 버튼 */
.regi-submit-btn {
  font-family: 'pretendard';
  width: 100%;
  padding: 18px;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
}

.regi-submit-btn:hover:not(:disabled) {
  background: #005baa;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(1, 113, 233, 0.2);
}

.regi-submit-btn:disabled {
  background: #eee;
  color: #bbb;
  cursor: not-allowed;
}
</style>

