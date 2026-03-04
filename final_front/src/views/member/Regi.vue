<template>
  <div class="regi-page-wrap">
    <div class="regi-card">
      <div class="regi-type-tabs">
        <button type="button" :class="['tab-btn', { active: mainTab === 'member' }]"
          @click="setMainTab('member')">일반회원</button>
        <button type="button" :class="['tab-btn', { active: mainTab === 'hospital' }]"
          @click="setMainTab('hospital')">병원 관계자</button>
      </div>

      <div class="regi-header">
        <h2>{{ headerTitle }}</h2>
        <div class="title-bar"></div>
      </div>

      <div v-if="mainTab === 'hospital'" class="input-section transition-box mb-30">
        <label>직무 선택</label>
        <div class="gender-selector">
          <input type="radio" id="doc" value="의사" v-model="user.role" class="hide-radio"
            @change="resetHospitalFields"><label for="doc">의사</label>
          <input type="radio" id="nur" value="간호사" v-model="user.role" class="hide-radio"
            @change="resetHospitalFields"><label for="nur">간호사</label>
          <input type="radio" id="adm" value="행정부서" v-model="user.role" class="hide-radio"
            @change="resetHospitalFields"><label for="adm">행정부서</label>
        </div>
      </div>

      <form @submit.prevent="handleRegi" class="regi-form transition-box">

        <div class="input-section">
          <label>아이디 <span class="req">*</span></label>
          <div class="id-flex-row">
            <input type="text" v-model="user.id" placeholder="아이디 입력" @input="checkID" maxlength="16" class="ime-en" />
            <button type="button" @click="idcheck" class="check-btn">중복확인</button>
          </div>
          <p class="guide-msg" :class="{ 'success': isIdChecked, 'error': idMsg && !isIdChecked }">
            {{ idMsg || '✔ 6~16자 영문 소문자/숫자' }}
          </p>
        </div>

        <div v-if="mainTab === 'hospital'" class="staff-special-fields transition-box">
          <div v-if="user.role !== '행정부서'" class="grid-row">
            <div class="input-section">
              <label>면허 번호 <span class="req">*</span></label>
              <input type="text" v-model="user.license_number" placeholder="면허 번호 입력" maxlength="6"
                @input="checkLicenseInput" class="ime-en" />
              <p class="guide-msg"
                :class="{ 'success': isLicenseValid, 'error': !isLicenseValid && user.license_number.length > 0 }">
                {{ licenseMsg }}
              </p>
            </div>
            <div class="input-section">
              <label>진료과 <span class="req">*</span></label>
              <select v-model="user.med_dept_id">
                <option value="">선택</option>
                <option v-for="d in medDepts" :key="d.med_dept_id" :value="d.med_dept_id">{{ d.med_dept_name }}</option>
              </select>
            </div>
          </div>
          <div v-else class="grid-row">
            <div class="input-section">
              <label>사원 번호 <span class="req">*</span></label>
              <input type="text" v-model="user.emp_number" placeholder="A20260101" maxlength="9"
                @input="checkEmpInput" class="ime-en" />
              <p class="guide-msg"
                :class="{ 'success': isEmpValid, 'error': !isEmpValid && user.emp_number.length > 0 }">
                {{ empMsg }}
              </p>
            </div>
            <div class="input-section">
              <label>행정 부서 <span class="req">*</span></label>
              <select v-model="user.admin_dept_id">
                <option value="">선택</option>
                <option v-for="a in adminDepts" :key="a.admin_dept_id" :value="a.admin_dept_id">{{ a.admin_dept_name }}</option>
              </select>
            </div>
          </div>
        </div>

        <div class="input-section">
          <label>비밀번호 <span class="req">*</span></label>
          <div class="pw-field-wrap">
            <input :type="showPw ? 'text' : 'password'" v-model="user.password" placeholder="비밀번호 입력" maxlength="16"
              @input="handlePasswordInput" class="ime-en" />
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
                <line x1="1" x2="23" y1="1" y2="23"></line>
              </svg>
            </button>
          </div>
          <ul class="validation-list">
            <li :class="{ 'pass': isPwLenValid }">✔ 6~16자 이내</li>
            <li :class="{ 'pass': isPwUpValid }">✔ 영문 대문자 포함</li>
            <li :class="{ 'pass': isPwSpValid }">✔ 특수문자 포함</li>
          </ul>
        </div>

        <div class="input-section">
          <label>비밀번호 확인 <span class="req">*</span></label>
          <div class="pw-field-wrap">
            <input :type="showPwCk ? 'text' : 'password'" v-model="user.pwCk" placeholder="비밀번호 재입력" maxlength="16"
              @input="handlePwCk" class="ime-en" />
            <button type="button" class="eye-toggle" @click="showPwCk = !showPwCk">
              <svg v-if="showPwCk" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path
                  d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24">
                </path>
                <line x1="1" x2="23" y1="1" y2="23"></line>
              </svg>
            </button>
          </div>
          <p class="guide-msg"
            :class="{ 'success': user.pwCk && user.password === user.pwCk, 'error': user.pwCk && user.password !== user.pwCk }">
            {{ user.pwCk && user.password === user.pwCk ? '✔ 비밀번호가 일치합니다' : (user.pwCk ? '비밀번호가 일치하지 않습니다' : '비밀번호를 한 번 더 입력해 주세요') }}
          </p>
        </div>

        <div class="grid-row">
          <div class="input-section">
            <label>이름 <span class="req">*</span></label>
            <input type="text" v-model="user.name" placeholder="한글 입력 (2자 이상)" @input="checkName" maxlength="10" class="ime-ko" />
            <p class="guide-msg" :class="{ 'success': isNameValid, 'error': !isNameValid && user.name.length > 0 }">
              {{ nameMsg || '' }}
            </p>
          </div>
          <div class="input-section">
            <label>성별</label>
            <div class="gender-selector">
              <input type="radio" id="m" value="1" v-model="user.gender" class="hide-radio"><label for="m">남자</label>
              <input type="radio" id="f" value="2" v-model="user.gender" class="hide-radio"><label for="f">여자</label>
            </div>
          </div>
        </div>

        <div class="input-section">
          <label>생년월일 <span class="req">*</span></label>
          <input type="text" v-model="user.birthday" placeholder="YYYYMMDD" maxlength="8" @input="checkBirthInput" class="ime-en" />
          <p class="guide-msg" :class="{ 'success': isBirthValid, 'error': !isBirthValid && user.birthday.length > 0 }">{{ birthMsg || '' }}</p>
        </div>

        <div class="input-section">
          <label>전화번호 <span class="req">*</span></label>
          <div class="tel-flex-row">
            <select v-model="tel1">
              <option value="010">010</option>
                  <option value="011">011</option>
                  <option value="070">070</option>
                  <option value="02">02</option>
                  <option value="031">031</option>
                  <option value="032">032</option>
                  <option value="051">051</option>
                  <option value="042">042</option>
            </select>
            <span class="tel-dash">-</span>
            <input type="text" v-model="tel2" maxlength="4" @input="onlyNumber($event, 'tel2')" class="ime-en" />
            <span class="tel-dash">-</span>
            <input type="text" ref="tel3Input" v-model="tel3" maxlength="4" @input="onlyNumber($event, 'tel3')" class="ime-en" />
          </div>
          <p class="guide-msg" :class="{ 'success': isPhoneValid }">
            {{ isPhoneValid ? '' : '전화번호를 입력해 주세요' }}
          </p>
        </div>

        <div class="input-section">
          <label>이메일 <span class="req">*</span></label>
          <input type="text" v-model="user.email" placeholder="example@email.com" @input="checkEmailInput" class="ime-en" />
          <p class="guide-msg" :class="{ 'success': isEmailValid, 'error': !isEmailValid && user.email.length > 0 }">
            {{ emailMsg }}
          </p>
        </div>

        <div class="input-section">
          <label>주소 <span class="req">*</span></label>
          <div class="addr-flex-row">
            <input type="text" v-model="user.address" placeholder="주소 검색" readonly @click="openPostcode" />
            <button type="button" @click="openPostcode" class="check-btn">검색</button>
          </div>
          <input type="text" v-model="user.addressDetail" placeholder="상세 주소" class="mt-8" id="userAddrDetail" />
          <p class="guide-msg" :class="{ 'success': user.address }">
            {{ user.address ? '' : '주소를 입력해 주세요' }}
          </p>
        </div>

        <button type="submit" class="regi-submit-btn">{{ submitBtnText }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
// [API] 회원가입, 중복체크, 부서 목록 조회 등
import { idCheckReq, regiReq, staffRegiReq, adminRegiReq, getAdminDeptsReq } from '@/api/member';
import { getDeptsReq } from '@/api/reservation';

const router = useRouter();
const route = useRoute();

const mainTab = ref('member'); // 현재 탭 (member: 일반 / hospital: 병원)
const medDepts = ref([]);      // 진료과 목록 (서버에서 가져옴)
const adminDepts = ref([]);    // 행정 부서 목록 (서버에서 가져옴)

// [회원가입 폼 데이터]
const user = ref({
  id: '', password: '', pwCk: '', name: '',
  birthday: '', gender: '1', phoneNumber: '', email: '',
  address: '', addressDetail: '', type: 'member', role: '의사',
  license_number: '', med_dept_id: '', emp_number: '', rank: '', admin_dept_id: ''
});

// 전화번호 분리 (010 - 1234 - 5678)
const tel1 = ref('010'); 
const tel2 = ref(''); 
const tel3 = ref('');
const tel3Input = ref(null); // 자동 포커스 이동용 ref

// [상태 플래그] UI 제어용
const isIdChecked = ref(false); // 아이디 중복확인 통과 여부
const showPw = ref(false);      // 비밀번호 보이기
const showPwCk = ref(false);    // 비밀번호 확인 보이기

// [유효성 메시지 및 상태]
const idMsg = ref("");
const isPwLenValid = ref(false); // 6~16자
const isPwUpValid = ref(false);  // 대문자 포함
const isPwSpValid = ref(false);  // 특수문자 포함

const isNameValid = ref(false);
const nameMsg = ref("");

const isBirthValid = ref(false);
const birthMsg = ref("");

// 전화번호는 입력 길이로 유효성 판단
const isPhoneValid = computed(() => tel2.value.length >= 3 && tel3.value.length === 4);

const isEmailValid = ref(false);
const emailMsg = ref("이메일 주소를 입력해 주세요");

const isLicenseValid = ref(false); // 면허번호 유효성
const licenseMsg = ref("면허번호 6자리를 입력해 주세요");

const isEmpValid = ref(false);     // 사원번호 유효성
const empMsg = ref("사원번호를 입력해 주세요 (예: A12345678)");



// 유효성 검사 및 입력 핸들러

// [아이디] 영문 소문자, 숫자만 허용 (나머지 제거)
const checkID = () => {
  user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, '');
  isIdChecked.value = false; // 내용 바뀌면 중복확인 초기화
  
  if (!user.value.id) idMsg.value = "";
  else if (user.value.id.length < 6 || user.value.id.length > 16) idMsg.value = "6 ~ 16자 이내로 입력해 주세요";
  else idMsg.value = "중복 확인이 필요합니다";
}

// [비밀번호] 한글 입력 방지
const handlePasswordInput = () => { 
  user.value.password = user.value.password.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, ''); 
}

// 비밀번호 실시간 조건 감시 (길이, 대문자, 특수문자)
watch(() => user.value.password, (val) => {
  isPwLenValid.value = val.length >= 6 && val.length <= 16;
  isPwUpValid.value = /[A-Z]/.test(val);
  isPwSpValid.value = /[!@#$%^&*()_+]/.test(val);
});

// [비번확인] 한글 입력 방지
const handlePwCk = () => { 
  user.value.pwCk = user.value.pwCk.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, ''); 
}

// [이름] 한글만 입력 가능 (영어/숫자 제거)
const checkName = (e) => {
  user.value.name = e.target.value.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  if (user.value.name.length < 2) { 
    isNameValid.value = false; 
    nameMsg.value = "2자 이상 입력해 주세요"; 
  } else { 
    isNameValid.value = true; 
    nameMsg.value = ""; 
  }
}

// [생년월일] 숫자만 + 날짜 유효성 검사
const checkBirthInput = (e) => {
  // 1. 숫자만 남기기
  user.value.birthday = e.target.value.replace(/[^0-9]/g, '');
  const val = user.value.birthday;
  
  // 2. 8자리가 아니면 에러
  if (val.length !== 8) { 
    isBirthValid.value = false; 
    birthMsg.value = "8자리 숫자로 입력해 주세요"; 
    return; 
  }

  // 3. 날짜 분해
  const y = parseInt(val.substring(0, 4));
  const m = parseInt(val.substring(4, 6));
  const d = parseInt(val.substring(6, 8));
  
  const date = new Date(y, m - 1, d);
  const today = new Date();

  // 과거 데이터 방지
  if (y < 1900) {
    isBirthValid.value = false;
    birthMsg.value = "정확한 생년월일을 입력해 주세요.";
    return;
  }

  // 4. 유효성 검사 (월/일 범위 초과 or 미래 날짜)
  if (m < 1 || m > 12 || d < 1 || d > 31 || date.getMonth() + 1 !== m || date.getDate() !== d) {
    isBirthValid.value = false; birthMsg.value = "존재하지 않는 날짜입니다.";
  } else if (date > today) {
    isBirthValid.value = false; birthMsg.value = "미래 날짜는 입력할 수 없습니다.";
  } else {
    isBirthValid.value = true; birthMsg.value = "";
  }
}

// [이메일] 한글 방지 + 정규식 체크
const checkEmailInput = (e) => {
  user.value.email = e.target.value.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  if (!user.value.email) {
    isEmailValid.value = false;
    emailMsg.value = "이메일 주소를 입력해 주세요";
  } else if (!pattern.test(user.value.email)) {
    isEmailValid.value = false;
    emailMsg.value = "이메일 형식이 올바르지 않습니다";
  } else {
    isEmailValid.value = true;
    emailMsg.value = "";
  }
}

// [면허번호] 숫자만 + 앞자리 코드 확인
const checkLicenseInput = (e) => {
  const val = e.target.value.replace(/[^0-9]/g, '');
  user.value.license_number = val;

  if (val.length === 0) {
    isLicenseValid.value = false; licenseMsg.value = "면허번호를 입력해 주세요"; return;
  }
  if (val.length !== 6) {
    isLicenseValid.value = false; licenseMsg.value = "6자리 숫자로 입력해 주세요"; return;
  }

  // 의사(13,14) vs 간호사(50,55) 구분 체크
  if (user.value.role === '의사') {
    if (val.startsWith('13') || val.startsWith('14')) {
      isLicenseValid.value = true; licenseMsg.value = "✔ 면허 인증이 완료되었습니다. 진료과를 선택해주세요.";
    } else {
      isLicenseValid.value = false; licenseMsg.value = "올바르지 않은 형식입니다. 다시 입력해 주세요.";
    }
  } else if (user.value.role === '간호사') {
    if (val.startsWith('50') || val.startsWith('55')) {
      isLicenseValid.value = true; licenseMsg.value = "✔ 면허 인증이 완료되었습니다. 진료과를 선택해주세요.";
    } else {
      isLicenseValid.value = false; licenseMsg.value = "올바르지 않은 형식입니다. 다시 입력해 주세요.";
    }
  }
}

// [사원번호] 대문자 변환 + 포맷(A+숫자8) 확인
const checkEmpInput = (e) => {
  const val = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
  user.value.emp_number = val;

  const pattern = /^[A-Z][0-9]{8}$/;

  if (val.length === 0) {
    isEmpValid.value = false; empMsg.value = "사원번호를 입력해 주세요";
  } else if (!pattern.test(val)) {
    isEmpValid.value = false; empMsg.value = "올바르지 않는 형식입니다. 다시 입력해 주세요.";
  } else {
    isEmpValid.value = true; empMsg.value = "✔ 사원 인증이 완료되었습니다. 부서를 선택해주세요.";
  }
}

// 탭/직무 변경 시 병원 관련 필드 초기화
const resetHospitalFields = () => {
  user.value.license_number = '';
  user.value.emp_number = '';
  isLicenseValid.value = false; licenseMsg.value = "면허번호 6자리를 입력해 주세요.";
  isEmpValid.value = false; empMsg.value = "사원번호를 입력해 주세요. (예: A12345678)";
}

// 숫자만 입력 + 전화번호 자동 포커스 이동
const onlyNumber = (e, field) => {
  const val = e.target.value.replace(/[^0-9]/g, '');
  if (field === 'tel2') {
    tel2.value = val;
    if (val.length === 4) tel3Input.value?.focus(); // 4글자 되면 다음 칸 이동
  } else if (field === 'tel3') {
    tel3.value = val;
  } else if (field === 'license_number') {
    user.value.license_number = val;
  }
}


// =========================================
// 3. API 통신 및 버튼 핸들러
// =========================================

// [아이디 중복확인]
const idcheck = async () => {
  if (idMsg.value === "6 ~ 16자 이내로 입력해 주세요" || !user.value.id) { 
    alert("아이디 형식을 확인해주세요."); return; 
  }
  try {
    const res = await idCheckReq(user.value.id);
    if (res.data === true) { 
      alert("사용 가능한 아이디입니다."); 
      isIdChecked.value = true; 
      idMsg.value = "✔ 사용 가능한 아이디입니다"; 
    } else { 
      alert("이미 사용 중인 아이디입니다."); 
      user.value.id = ''; 
      isIdChecked.value = false; 
      idMsg.value = ""; 
    }
  } catch (e) { alert("중복 확인 실패"); }
}

// [주소 검색] 다음 우편번호 API
const openPostcode = () => {
  if (!window.daum) { alert("다음 주소 API 로딩 중..."); return; }
  
  // (수정) user 코드에서 kakao로 되어있던 것을 daum으로 통일 (안전장치)
  new window.kakao.Postcode({
    oncomplete: (data) => {
      user.value.address = data.roadAddress || data.jibunAddress;
      document.getElementById("userAddrDetail")?.focus();
    }
  }).open();
}

// 헤더 텍스트 및 버튼 텍스트 계산
const headerTitle = computed(() => mainTab.value === 'member' ? '회원가입' : '병원 관계자 등록');
const submitBtnText = computed(() => mainTab.value === 'member' ? '가입하기' : '등록 신청하기');

// [탭 전환] 폼 초기화 및 탭 설정
const setMainTab = (type) => {
  mainTab.value = type; 
  user.value.type = type;
  user.value = { ...user.value, id: '', password: '', pwCk: '', name: '', birthday: '', email: '', role: type === 'hospital' ? '의사' : '' };
  
  // 에러 메시지 초기화
  isIdChecked.value = false; idMsg.value = "";
  isNameValid.value = false; nameMsg.value = "";
  isBirthValid.value = false; birthMsg.value = "";
  isEmailValid.value = false; emailMsg.value = "이메일 주소를 입력해 주세요";
  resetHospitalFields();
}

// [가입하기 버튼] 최종 유효성 검사 및 전송
const handleRegi = async () => {
  // 1. 공통 필수값 체크
  if (!user.value.id || !isIdChecked.value) { alert("아이디 중복 확인을 해주세요."); return; }
  if (!user.value.password || !isPwLenValid.value || !isPwUpValid.value || !isPwSpValid.value) { alert("비밀번호 형식을 모두 만족해야 합니다."); return; }
  if (user.value.password !== user.value.pwCk) { alert("비밀번호 확인이 일치하지 않습니다."); return; }
  if (!isNameValid.value) { alert("이름을 확인해주세요."); return; }
  if (!isBirthValid.value) { alert("생년월일을 확인해주세요."); return; }
  if (!isPhoneValid.value) { alert("전화번호를 입력해주세요."); return; }
  
  user.value.phoneNumber = `${tel1.value}${tel2.value}${tel3.value}`;
  
  if (!isEmailValid.value) { alert("이메일을 확인해주세요."); return; }
  if (!user.value.address) { alert("주소를 입력해주세요."); return; }

  // 2. 병원 관계자 전용 체크
  if (mainTab.value === 'hospital') {
    if (user.value.role !== '행정부서') { // 의사, 간호사
      if (!isLicenseValid.value) { alert(licenseMsg.value); return; }
      if (!user.value.med_dept_id) { alert("진료과를 선택해주세요."); return; }
    } else { // 행정직
      if (!isEmpValid.value) { alert(empMsg.value); return; }
      if (!user.value.admin_dept_id) { alert("부서를 선택해주세요."); return; }
    }
  }

  // 3. 전송 데이터 정리 (숫자 변환 등)
  const sendData = {
    ...user.value,
    birthday: Number(user.value.birthday),
    gender: Number(user.value.gender),
    licenseNumber: user.value.license_number || null,
    medDeptId: user.value.med_dept_id ? Number(user.value.med_dept_id) : null,
    empNumber: user.value.emp_number || null,
    adminDeptId: user.value.admin_dept_id ? Number(user.value.admin_dept_id) : null
  };

  // 4. API 전송
  try {
    let res;
    if (mainTab.value === 'member') res = await regiReq(sendData);
    else {
      if (user.value.role === '행정부서') res = await adminRegiReq(sendData);
      else res = await staffRegiReq(sendData);
    }

    if (res.data === "success" || res.data === true) {
      alert("환영합니다!");
      router.push('/login');
    } else {
      alert("가입에 실패했습니다. 입력 정보를 확인해주세요.");
    }
  } catch (err) { alert("서버 오류가 발생했습니다."); }
}

// [초기화] 페이지 로드 시 진료과/부서 목록 가져오기
onMounted(async () => {
  if (route.query.type === 'hospital') {
    setMainTab('hospital');
  }

  try {
    const medRes = await getDeptsReq();
    medDepts.value = medRes.data;
    const adminRes = await getAdminDeptsReq();
    adminDepts.value = adminRes.data;
  } catch (e) { }
})
</script>

<style scoped>
/* 입력 모드(IME) 설정 */
.ime-en {
  ime-mode: disabled; /* 한글 입력 불가 (영문 전용) */
}

.ime-ko {
  ime-mode: active; /* 한글 입력 우선 */
}

input::-ms-reveal,
input::-ms-clear {
  display: none;
}

.regi-page-wrap {
  font-family: 'pretendard';
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #ffffff;
  padding: 60px 20px;
}

.regi-card {
  width: 100%;
  max-width: 700px;
  background: #fff;
  padding: 60px 50px;
  border: 1px solid #eee;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
}

.regi-type-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 40px;
}

.tab-btn {
  font-family: 'pretendard';
  flex: 1;
  padding: 14px;
  border: 1px solid #eee;
  background: #f9f9f9;
  color: #999;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
  font-size: 18px;
}

.tab-btn.active {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

.regi-header {
  text-align: center;
  margin-bottom: 45px;
}

.regi-header h2 {
  font-size: 42px;
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

.regi-form {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.input-section label {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #4e4e4e;
  margin-bottom: 10px;
}

.req {
  color: #dc3545;
  margin-left: 4px;
  font-size: 18px;
}

input,
select {
  font-family: 'pretendard';
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  font-size: 16px;
  background: #f9f9f9;
  transition: 0.3s;
}

input:focus {
  border-color: #005baa;
  background: #fff;
  outline: none;
}

.id-flex-row,
.addr-flex-row {
  display: flex;
  gap: 10px;
}

.check-btn {
  font-family: 'pretendard';
  width: 130px;
  background: #404347;
  color: #fff;
  border: none;
  font-weight: 600;
  font-size: 18px;
  cursor: pointer;
}

.pw-field-wrap {
  position: relative;
  width: 100%;
}

.eye-toggle {
  position: absolute;
  right: 15px;
  top: 55%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #ccc;
  padding: 0;
  font-size: 18px;
}

.tel-flex-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tel-flex-row select {
  width: 100px;
  flex-shrink: 0;
}

.tel-flex-row input {
  flex: 1;
  text-align: center;
}

.tel-dash {
  color: #ccc;
  font-weight: bold;
}

.staff-special-fields {
  background: #005baa10;
  padding: 25px;
  border: 1px dashed #ccc;
  margin-bottom: 10px;
}

.grid-row {
  display: flex;
  gap: 20px;
}

.grid-row>div {
  flex: 1;
}

.gender-selector {
  display: flex;
  border: 1px solid #ddd;
  overflow: hidden;
  height: 48px;
}

.hide-radio {
  display: none !important;
}

.gender-selector label {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
  color: #888;
  cursor: pointer;
  transition: 0.2s;
  margin: 0;
}

.hide-radio:checked+label {
  background: #005baa;
  color: #fff;
  font-weight: 600;
}

.mt-8 {
  margin-top: 8px;
}

.mb-30 {
  margin-bottom: 30px;
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

.guide-msg {
  font-size: 15px;
  margin-top: 6px;
  color: #bbb;
  font-weight: 500;
  transition: 0.3s;
}

.guide-msg.error {
  color: #dc3545;
}

.guide-msg.success {
  color: #28a745;
  font-weight: 600;
}

.validation-list {
  display: flex;
  gap: 12px;
  list-style: none;
  padding: 0;
  margin-top: 8px;
}

.validation-list li {
  font-size: 12px;
  color: #bbb;
  display: flex;
  align-items: center;
  gap: 2px;
  transition: color 0.3s;
}

.validation-list li.pass {
  color: #28a745;
  font-weight: 600;
}

.regi-submit-btn {
  font-family: 'pretendard';
  width: 100%;
  padding: 18px;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 10px;
}

.regi-submit-btn:hover {
  background: #005baa;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(1, 113, 233, 0.2);
}
</style>