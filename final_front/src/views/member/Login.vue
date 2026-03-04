<template>
  <div class="login-page-wrap">
    <div class="portal-login-container">
      <div class="portal-info-panel">
        <div class="brand-area">
          <img src="@/assets/txtlogo2.png" alt="S-HOSPITAL" class="brand-logo">
        </div>

        <div v-if="mainTab === 'member'" class="service-intro transition-box">
          <p class="intro-main">안전하고 편리한<br>환자 맞춤형 서비스</p>
          <ul class="service-list">
            <li><span>✔</span> 온라인 진료 예약 및 내역 확인</li>
            <li><span>✔</span> 검사 결과 및 증명서 발급</li>
            <li><span>✔</span> 모바일 건강 수첩 및 알림 서비스</li>
          </ul>
        </div>

        <div v-else class="service-intro transition-box">
          <p class="intro-main">신속하고 정확한<br>스마트 병원 관리</p>
          <ul class="service-list">
            <li><span>✔</span> 통합 의료 정보 시스템(HIS) 연동</li>
            <li><span>✔</span> 실시간 환자 데이터 및 통계 확인</li>
            <li><span>✔</span> 부서 간 신속한 협업 및 커뮤니케이션</li>
          </ul>
        </div>

        <div class="cs-info">
          <p>도움이 필요하신가요?</p>
          <span class="cs-tel">고객센터 1588-0000</span>
        </div>
      </div>

      <div class="portal-form-panel">
        <div class="login-type-tabs">
          <button type="button" :class="['tab-btn', { active: mainTab === 'member' }]"
            @click="setMainTab('member')">일반회원 로그인</button>
          <button type="button" :class="['tab-btn', { active: mainTab === 'hospital' }]"
            @click="setMainTab('hospital')">관계자 로그인</button>
        </div>

        <div class="form-title-group">
          <h3>{{ pageTitle }}</h3>
          <div class="title-bar"></div>
        </div>

        <div v-if="mainTab === 'hospital'" class="sub-role-toggle-wrap transition-box">
          <div class="toggle-track">
            <input type="radio" id="role_med" v-model="hospitalType" value="med" class="hide-radio">
            <input type="radio" id="role_admin" v-model="hospitalType" value="admin" class="hide-radio">
            <label for="role_med" :class="['toggle-label', { active: hospitalType === 'med' }]">의료진</label>
            <label for="role_admin" :class="['toggle-label', { active: hospitalType === 'admin' }]">행정/원무</label>
            <div class="sliding-handle" :style="handleStyle"></div>
          </div>
        </div>

        <form @submit.prevent="handleLogin" class="login-form transition-box" :key="mainTab">
          <div class="input-field">
            <span class="field-label">아이디</span>
            <input type="text" v-model="user.id" :placeholder="idPlaceholder" @input="checkID" />
          </div>

          <div class="input-field">
            <span class="field-label">비밀번호</span>
            <div class="pw-box">
              <input :type="showPw ? 'text' : 'password'" v-model="user.password" placeholder="Password" />
              <button type="button" class="eye-toggle" @click="showPw = !showPw">
                <svg v-if="showPw" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                  fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24">
                  </path>
                  <line x1="1" y1="1" x2="23" y2="23"></line>
                </svg>
              </button>
            </div>
          </div>

          <div class="helper-row">
            <div class="save-chk">
              <input type="checkbox" id="saveId" v-model="saveId" @change="handleSaveIdChange" />
              <label for="saveId">아이디 저장</label>
            </div>
            <div class="find-group">
              <span @click="isFindIdModalOpen = true">아이디 찾기</span>
              <span class="bar">|</span>
              <span>비밀번호 찾기</span>
            </div>
          </div>

          <button type="submit" class="btn-login-submit">로그인</button>

          <div class="regi-footer">
            <p>{{ footerText }}</p>
            <div class="regi-links">
              <a href="#" @click.prevent="goToRegi('member')">일반회원 가입</a>
              <span class="divider">|</span>
              <a href="#" @click.prevent="goToRegi('hospital')">병원 관계자 가입</a>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div v-if="isFindIdModalOpen" class="modal-overlay">
      <div class="modal-card">
        <h3>아이디 찾기</h3>
        <div class="modal-body">
          <div v-if="!foundId">
            <input v-model="findUser.name" type="text" placeholder="성함" class="m-input" />
            <input v-model="findUser.phoneNumber" type="text" placeholder="휴대폰 번호 (- 없이 입력)" class="m-input" />
            <input v-model="findUser.email" type="email" placeholder="이메일" class="m-input" />
            <button class="btn-m-find" @click="handleFindId">아이디 확인</button>
          </div>
          <div v-else class="m-result">
            <div class="id-res-box">{{ foundId }}</div>
            <button class="btn-m-find" @click="copyAndLogin">아이디 적용</button>
          </div>
          <button class="btn-m-close" @click="closeFindIdModal">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useCookies } from 'vue3-cookies';
// API 파일 가져오기 (로그인, 아이디 찾기, 전체 의료진 목록 조회)
import { loginReq, loginMedReq, loginAdminReq, findIdReq, getAdminMyInfoReq } from '@/api/member.js';
import { getAllDoctorsReq } from '@/api/reservation.js';

// =========================================
// 1. 기본 설정 및 상태 변수 (Data)
// =========================================
const { cookies } = useCookies(); // 쿠키 사용 (아이디 저장용)
const router = useRouter();       // 페이지 이동
const route = useRoute();         // 현재 주소 정보

// 탭 및 모드 설정
const mainTab = ref('member');    // 메인 탭 ('member': 환자, 'hospital': 병원 관계자)
const hospitalType = ref('med');  // 병원 관계자 서브 탭 ('med': 의료진, 'admin': 행정직)

// 입력 폼 데이터
const user = ref({ id: '', password: '' }); // 로그인 입력값
const showPw = ref(false);        // 비밀번호 보이기/숨기기 토글
const saveId = ref(false);        // 아이디 저장 체크박스

// 아이디 찾기 모달 관련
const isFindIdModalOpen = ref(false); // 모달 열림 여부
const findUser = ref({ name: '', email: '', phoneNumber: '' }); // 찾기 입력값
const foundId = ref("");          // 찾은 아이디 결과


// =========================================
// 2. Computed (화면 텍스트 및 스타일)
// =========================================

// 페이지 제목 (탭에 따라 변경)
const pageTitle = computed(() => mainTab.value === 'member' ? '환자 포털 로그인' : '병원 관계자 로그인');

// 아이디 입력창 안내 문구
const idPlaceholder = computed(() => mainTab.value === 'member' ? 'ID를 입력해 주세요' : '사번 또는 아이디 입력');

// 하단 회원가입 안내 문구
const footerText = computed(() => mainTab.value === 'member' ? '아직 계정이 없으신가요?' : '아직 관계자 등록 전이신가요?');

// 관계자 로그인 토글 스위치 애니메이션 스타일
const handleStyle = computed(() => {
  return hospitalType.value === 'med' ? { left: '4px' } : { left: 'calc(50% + 4px)' };
});


// =========================================
// 3. 탭 및 화면 제어 함수
// =========================================

// 메인 탭 변경 (환자 <-> 관계자)
const setMainTab = (type) => {
  mainTab.value = type;

  user.value.password = '';

  // 아이디 처리
  const savedCookie = cookies.get('userId'); // 쿠키에서 저장된 아이디 확인

  if (saveId.value && savedCookie) {
    // 아이디 저장이 체크되어 있고 저장된 쿠키가 있다면, 지우지 말고 다시 채워줌
    user.value.id = savedCookie;
  } else {
    // 그게 아니라면(아이디 저장을 안 썼다면) 원래대로 비워줌
    user.value.id = '';
  }
};

// 회원가입 페이지로 이동 (쿼리로 타입 전달)
const goToRegi = (type) => {
  router.push({ path: '/regi', query: { type: type } });
};

// 아이디 저장 체크박스 변경 시 경고
const handleSaveIdChange = () => {
  if (saveId.value && !confirm("개인정보 보호를 위해 본인 기기에서만 저장해 주세요")) {
    saveId.value = false;
  }
};

// 아이디 입력 시 소문자/숫자만 남기기 (자동 보정)
const checkID = () => {
  user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, '');
};


// =========================================
// 4. 아이디 찾기 모달 관련 함수
// =========================================

// 아이디 찾기 요청
const handleFindId = async () => {
  if (!findUser.value.name || !findUser.value.email || !findUser.value.phoneNumber) {
    alert("모든 정보를 입력해 주세요"); return;
  }
  try {
    const res = await findIdReq(findUser.value);
    if (res.data) foundId.value = res.data; // 찾음
    else alert("일치하는 정보가 없습니다");   // 못 찾음
  } catch (err) { alert("오류 발생"); }
};

// 모달 닫기 및 초기화
const closeFindIdModal = () => {
  isFindIdModalOpen.value = false;
  foundId.value = "";
};

// 찾은 아이디 복사해서 로그인 입력창에 넣기
const copyAndLogin = () => {
  user.value.id = foundId.value;
  closeFindIdModal();
};


// =========================================
// 5. 로그인 핵심 로직 (서버 통신)
// =========================================
// [Login.vue] handleLogin 함수 수정
const handleLogin = async () => {
  if (!user.value.id || !user.value.password) {
    alert('아이디와 비밀번호를 입력해 주십시오'); return;
  }

  try {
    let res;
    const payload = { id: user.value.id, password: user.value.password };

    // 1. 로그인 API 호출
    if (mainTab.value === 'member') {
      res = await loginReq(payload);
    } else {
      if (hospitalType.value === 'med') res = await loginMedReq(payload);
      else res = await loginAdminReq(payload);
    }

    if (!res.data || res.data === "") {
      alert("아이디 또는 비밀번호가 일치하지 않습니다.");
      return;
    }

    const loginData = { ...res.data };
    let userRole = 'MEMBER';

    // 2. 의료진 여부 확인
    let isMedicalStaff = false;
    let myStaffInfo = null;
    try {
      const staffRes = await getAllDoctorsReq();
      myStaffInfo = staffRes.data.find(s => s.user_id === user.value.id);
      if (myStaffInfo) isMedicalStaff = true;
    } catch (e) { console.error("명단 조회 실패", e); }

    // 3. 상호 차단 및 역할 설정 (B-2 구간 수정)
    if (mainTab.value === 'member') {
      if (isMedicalStaff) {
        alert("병원 관계자입니다. 관계자 로그인 탭을 이용해 주세요.");
        return;
      }
    } else if (mainTab.value === 'hospital') {
      if (hospitalType.value === 'med') {
        if (!isMedicalStaff) {
          alert("등록된 의료진 정보가 없습니다.");
          return;
        }
        userRole = 'MED';
        loginData.role = myStaffInfo.role;
        loginData.deptName = myStaffInfo.med_dept_name;
        loginData.medStaffId = myStaffInfo.staff_id;
      } else {
        if (isMedicalStaff) {
          alert("의료진입니다. 의료진 항목을 선택해 주세요.");
          return;
        }
        userRole = 'ADMIN';
        loginData.role = '행정직';

        try {
          const adminExtraRes = await getAdminMyInfoReq();

          if (adminExtraRes.data) {
            // 서버의 /admin/my-info에서 준 값들로 싹 갈아치우기
            loginData.adminDeptName = adminExtraRes.data.deptName; // "홍보팀" 등 진짜 이름
            loginData.isWonmu = adminExtraRes.data.isWonmu;      // true/false
            loginData.isPr = adminExtraRes.data.isPr;            // true/false
          }
        } catch (e) {
          console.error("행정 상세 정보 로드 실패", e);
          loginData.adminDeptName = '행정지원팀'; // 실패 시 기본값
        }

        console.log("데이터:", loginData);
      }
    }

    // 4. 최종 데이터 세션 저장
    loginData.loginType = userRole;
    sessionStorage.setItem('loginId', JSON.stringify(loginData));

    alert(`${loginData.name}님 환영합니다.`);
    if (saveId.value) cookies.set("userId", user.value.id, '7d');
    else cookies.remove("userId");

    router.push(route.query.redirect || "/");

  } catch (err) {
    console.error(err);
    alert('로그인 처리 중 오류가 발생했습니다.');
  }
};


// =========================================
// 6. 페이지 시작 (Life Cycle)
// =========================================
onMounted(() => {
  // 저장된 아이디(쿠키)가 있으면 불러와서 채워넣기
  const savedCookie = cookies.get('userId');
  if (savedCookie) {
    user.value.id = savedCookie;
    saveId.value = true;
  }
});
</script>

<style scoped>
input::-ms-reveal,
input::-ms-clear {
  display: none !important;
  /* MS Edge/IE 전용 눈 아이콘 제거 */
}

/* 크롬/사파리 등 자동완성 아이콘 방어 (깔끔함 유지) */
input[type="password"]::-webkit-contacts-auto-fill-button,
input[type="password"]::-webkit-credentials-auto-fill-button {
  visibility: hidden;
  display: none !important;
  pointer-events: none;
}

.login-page-wrap {
  font-family: 'Pretendard', -apple-system, sans-serif !important;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #ffffff;
  padding: 50px 20px;
}

.portal-login-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.portal-info-panel {
  flex: 1;
  background-color: #f9f9f9;
  padding: 90px 80px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  height: 60px;
}

.intro-main {
  font-size: 28px;
  font-weight: 600;
  color: #005baa;
  line-height: 1.2;
  margin-bottom: 20px;
}

.service-list {
  list-style: none;
  padding: 0;
}

.service-list li {
  font-size: 18px;
  color: #777;
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
  font-weight: 400;
}

.service-list span {
  color: #a1d8f3;
  font-weight: bold;
}

.cs-info {
  margin-bottom: 10px;
  line-height: 1.8;
  font-size: 18px;
}

.cs-tel {
  font-size: 22px;
  font-weight: 600;
  color: #005baa;
}

.portal-form-panel {
  flex: 1.2;
  padding: 60px 80px;
  background: #fff;
}

.login-type-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 40px;
}

.tab-btn {
  font-family: 'pretendard';
  font-size: 15px;
  flex: 1;
  padding: 12px;
  border: 1px solid #eee;
  background: #f9f9f9;
  color: #999;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
}

.tab-btn.active {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

.form-title-group {
  margin-bottom: 35px;
}

.form-title-group h3 {
  font-size: 26px;
  color: #005baa;
  font-weight: 700;
  margin-bottom: 10px;
}

.title-bar {
  width: 30px;
  height: 3px;
  background-color: #005baa;
}

.sub-role-toggle-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 35px;
}

.toggle-track {
  position: relative;
  display: flex;
  width: 240px;
  height: 44px;
  background-color: #f0f2f5;
  border-radius: 22px;
  padding: 4px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);
}

.hide-radio {
  display: none;
}

.toggle-label {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  z-index: 2;
  transition: color 0.3s ease;
}

.toggle-label.active {
  color: #fff;
}

.sliding-handle {
  position: absolute;
  top: 4px;
  width: calc(50% - 8px);
  height: calc(100% - 8px);
  background-color: #005baa;
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(1, 113, 233, 0.3);
  transition: left 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  z-index: 1;
}

.input-field {
  margin-bottom: 25px;
}

.field-label {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: #999;
  margin-bottom: 8px;
}

input[type="text"],
input[type="password"] {
  font-family: 'pretendard';
  width: 100%;
  padding: 12px 0;
  border: none;
  border-bottom: 1px solid #ddd;
  font-size: 17px;
  color: #4e4e4e;
  transition: 0.3s;
  background: transparent;
}

input:focus {
  border-bottom-color: #005baa;
  outline: none;
}

.pw-box {
  position: relative;
}

.eye-toggle {
  position: absolute;
  right: 0;
  top: 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ccc;
}

.helper-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  font-size: 16px;
  color: #777;
}

.save-chk {
  display: flex;
  align-items: center;
  gap: 8px;
}

.find-group {
  color: #aaa;
}

.find-group span {
  cursor: pointer;
}

.bar {
  margin: 0 10px;
  color: #eee;
}

.btn-login-submit {
  font-family: 'pretendard';
  width: 100%;
  padding: 18px;
  background-color: #005baa;
  color: #fff;
  border: none;
  font-size: 22px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
}

.btn-login-submit:hover {
  background-color: #005baa;
  box-shadow: 0 5px 15px rgba(1, 113, 233, 0.2);
}

.regi-footer {
  margin-top: 30px;
  text-align: center;
}

.regi-footer p {
  font-size: 16px;
  color: #bbb;
  margin-bottom: 10px;
}

/* 링크 스타일*/
.regi-links {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.regi-footer a {
  color: #005baa;
  font-weight: 600;
  font-size: 18px;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  /* 밑줄 호버 효과용 */
  transition: border-bottom 0.3s;
}

.regi-footer a:hover {
  border-bottom: 1px solid #005baa;
}

.divider {
  color: #ddd;
  font-size: 12px;
}

.transition-box {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

/* 아이디 찾기 모달 카드 디자인 */
.modal-card {
  background: #fff;
  padding: 50px 40px;
  width: 100%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.modal-card h3 {
  font-size: 24px;
  color: #005baa;
  margin-bottom: 30px;
  font-weight: 700;
}

/* 모달 내부 input을 메인 로그인창과 동일한 '밑줄' 스타일로 통일 */
.m-input {
  font-family: 'pretendard';
  width: 100%;
  padding: 12px 0;
  border: none;
  border-bottom: 1px solid #ddd; /* 메인창과 동일 */
  margin-bottom: 20px;
  background: transparent;
  font-size: 16px;
  transition: 0.3s;
}

.m-input:focus {
  border-bottom-color: #005baa;
  outline: none;
}

/* 모달 버튼 스타일 통일 */
.btn-m-find {
  width: 100%;
  padding: 16px;
  background: #005baa; /* 메인 버튼 컬러와 통일 */
  color: #fff;
  border: none;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
  margin-bottom: 10px;
}

.btn-m-close {
  width: 100%;
  padding: 12px;
  background: #fff;
  color: #bbb;
  border: none;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline; /* 닫기 버튼은 링크 느낌으로 */
}

/* 아이디 결과 박스 */
.id-res-box {
  font-size: 26px;
  font-weight: 800;
  color: #005baa;
  padding: 30px 20px;
  background: #f0f7ff;
  margin-bottom: 20px;
  border-radius: 4px;
}
</style>