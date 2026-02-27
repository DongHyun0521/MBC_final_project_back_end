<template>
  <div class="full-dashboard">
    <aside class="sidebar">
      <!-- [공통 영역] 사이드바 프로필 영역 -->
      <div class="profile-area">
        <div class="avatar">
          <svg v-if="userType === 'ADMIN'" width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#005baa"
            stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
            <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
          </svg>

          <svg v-else-if="isDoctor" width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#005baa"
            stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
            <path d="M14 13v1a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-1" />
            <path d="M12 16v3" />
            <circle cx="12" cy="20" r="1" />
          </svg>

          <svg v-else-if="isNurse" width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#005baa"
            stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="m18 2 4 4" />
            <path d="m17 7 3-3" />
            <path d="M19 9 8.7 19.3c-1 1-2.5 1-3.4 0l-.6-.6c-1-1-1-2.5 0-3.4L15 5" />
            <path d="m9 11 4 4" />
            <path d="m5 19-3 3" />
            <path d="m14 4 6 6" />
          </svg>

          <svg v-else width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"
            stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </div>
      </div>
      <p class="user-id">{{ userInfo.name }} 님</p>
      <p class="user-tag">{{ userRoleDisplay }}</p>
      <p class="join-date">
        {{ userType === 'MEMBER' ? '가입일' : '입사일' }}: {{ formatDate(userInfo.createTime || userInfo.create_time) }}
      </p>

      <!-- [공통 영역] 사이드바 메뉴 네비게이션 -->
      <nav class="side-nav">
        <ul>
          <li :class="{ active: currentView === 'dash' }" @click="changeView('dash')">대시보드 홈</li>
          <li :class="{ active: currentView === 'res' }" @click="changeView('res')">내 진료 예약</li>

          <template v-if="isDoctor">
            <li :class="{ active: currentView === 'doc_res' }" @click="changeView('doc_res')">진료 업무 일정</li>
          </template>

          <template v-if="isNurse">
            <li :class="{ active: currentView === 'nur_res' }" @click="changeView('nur_res')">진료 일정</li>
            <li :class="{ active: currentView === 'nur_schedule' }" @click="changeView('nur_schedule')">나의 근무 일정표</li>
            <li :class="{ active: currentView === 'nur_ward' }" @click="changeView('nur_ward')">병동 현황</li>
          </template>

          <template v-if="userType === 'ADMIN'">
            <li v-if="userInfo.isWonmu" :class="{ active: currentView === 'admin_voc' }"
              @click="changeView('admin_voc')">VOC 관리</li>
            <li :class="{ active: currentView === 'admin_todo' }" @click="changeView('admin_todo')">업무 관리</li>
            <li v-if="userInfo.isPr" :class="{ active: currentView === 'admin_event' }"
              @click="changeView('admin_event')">병원 행사 관리</li>
          </template>

          <li :class="{ active: currentView === 'vehi' }" @click="changeView('vehi')">차량 관리</li>
          <li :class="{ active: currentView === 'edit' }" @click="changeView('edit')">개인 정보 수정</li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <!-- [공통 영역] 상단 헤더(인사/현재시간)-->
      <header class="dashboard-header">
        <div class="welcome-text">
          <h2>{{ getTimeGreeting() }}, <span class="blue-txt">{{ userInfo.name }}</span>님</h2>
          <p class="current-time">{{ currentTime }}</p>
        </div>
      </header>

      <div class="dash-content-body">
        <template v-if="currentView !== 'vehi' && currentView !== 'edit'">
          <MemberDash v-if="userType === 'MEMBER'" :userInfo="userInfo" :currentView="currentView" @changeView="(val) => currentView = val"/>
          <DoctorDash v-else-if="isDoctor" :userInfo="userInfo" :currentView="currentView" @changeView="(val) => currentView = val"/>
          <NurseDash v-else-if="isNurse" :userInfo="userInfo" :currentView="currentView" @changeView="(val) => currentView = val"/>
          <AdminDash v-else-if="userType === 'ADMIN'" :userInfo="userInfo" :currentView="currentView" @changeView="(val) => currentView = val"/>
        </template>
      </div>

      <!--[공통 화면] 차량 관리-->
      <div v-if="currentView === 'vehi'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>차량 관리</h3>
            <button @click="router.push('/vehiregi')" class="btn-add-sm">+ 새 차량 등록</button>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>차량번호</th>
                <th>차종/유종</th>
                <th class="txt-center">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="car in myVehicles" :key="car.vehicleNum">
                <td class="bold-blue">{{ car.vehicleNum }}</td>
                <td>{{ car.vehicleType }} / {{ car.fuelType }}</td>
                <td class="txt-center">
                  <button @click="openVehiEdit(car)" class="btn-update-table">수정</button>
                  <button @click="deleteVehicle(car.vehicleNum)" class="btn-cancel-table">차량 삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="myVehicles.length === 0" class="empty-msg">등록된 차량이 없습니다.</div>
        </div>
      </div>

      <div v-if="currentView === 'edit'" class="view-section centered">
        <div class="edit-card-wrap">

          <div class="section-card">
            <div class="card-head">
              <h3>개인 정보 수정</h3>
            </div>
            <div class="edit-form">
              <div class="f-row">
                <span>아이디</span>
                <p class="readonly-val">{{ userInfo.id }}</p>
              </div>

              <div class="input-section">
                <label>이름 <span class="req">*</span></label>
                <input type="text" v-model="editForm.name" placeholder="한글 입력 (2자 이상)" @input="checkName" maxlength="10"
                  class="ime-ko" />
                <p v-if="nameMsg" :class="['guide-msg', isNameValid ? 'success' : 'error']">{{ nameMsg }}</p>
              </div>

              <template v-if="userType !== 'MEMBER'">
                <div class="f-row"><span>소속 부서</span>
                  <p class="readonly-val">{{ userInfo.adminDeptName || userInfo.deptName || '소속없음' }}</p>
                </div>
                <div class="f-row"><span>직급/직책</span>
                  <p class="readonly-val">{{ userInfo.rank || userInfo.role }}</p>
                </div>
                <div class="f-row"><span>입사일</span>
                  <p class="readonly-val">{{ formatDate(userInfo.createTime || userInfo.create_time) }}</p>
                </div>
              </template>

              <template v-if="userType === 'MEMBER'">
                <div class="f-row"><span>생년월일</span>
                  <p class="readonly-val">{{ formatBirthday(userInfo.birthday) }}</p>
                </div>
              </template>

              <!-- <div class="input-section">
                <label>주소 <span class="req">*</span></label>
                <div class="addr-flex-row">
                  <input type="text" v-model="editForm.address" placeholder="주소 검색" readonly @click="openPostcode" />
                  <button type="button" @click="openPostcode" class="check-btn">검색</button>
                </div>
                <input type="text" v-model="editForm.addressDetail" placeholder="상세 주소를 입력해 주세요" class="mt-8" @input="checkAddr" />
                <p v-if="addrMsg" class="guide-msg error">{{ addrMsg }}</p>
              </div> -->

              <div class="input-section">
                <label>주소 <span class="req">*</span></label>

                <div class="addr-group">
                  <div class="addr-top-row">
                    <input type="text" v-model="editForm.address" placeholder="주소 검색" readonly @click="openPostcode" />
                    <button type="button" @click="openPostcode" class="check-btn">검색</button>
                  </div>

                  <input type="text" v-model="editForm.addressDetail" placeholder="상세 주소를 입력해 주세요" class="mt-8"
                    @input="checkAddr" />
                  <p v-if="addrMsg" class="guide-msg error">{{ addrMsg }}</p>
                </div>
              </div>

              <div class="input-section">
                <label>전화번호 <span class="req">*</span></label>
                <div class="tel-flex-row">
                  <select v-model="tel1">
                    <option v-for="opt in ['010', '011', '070', '02', '031', '032', '051', '042']" :key="opt" :value="opt">{{ opt }}
                    </option>
                  </select>
                  <span class="tel-dash">-</span>
                  <input type="text" v-model="tel2" maxlength="4" @input="onlyNumber($event, 'tel2')" class="ime-en" />
                  <span class="tel-dash">-</span>
                  <input type="text" ref="tel3Input" v-model="tel3" maxlength="4" @input="onlyNumber($event, 'tel3')"
                    class="ime-en" />
                </div>
                <p v-if="phoneMsg" :class="['guide-msg', isPhoneValid ? 'success' : 'error']">{{ phoneMsg }}</p>
              </div>

              <div class="input-section">
                <label>이메일 <span class="req">*</span></label>
                <div class="tel-flex-row">
                  <input type="text" v-model="emailId" placeholder="아이디" @input="checkEmail" class="ime-en" />
                  <span class="tel-dash">@</span>
                  <select v-model="emailDomain" @change="checkEmail" style="flex: 1.5;">
                    <option value="naver.com">naver.com</option>
                    <option value="gmail.com">gmail.com</option>
                    <option value="nate.com">nate.com</option>
                    <option value="daum.net">daum.net</option>
                    <option value="direct">직접 입력</option>
                  </select>
                </div>
                <input v-if="emailDomain === 'direct'" v-model="emailDirect" type="text" placeholder="도메인 입력"
                  class="mt-8 ime-en" @input="checkEmail" />
                <p v-if="emailMsg" :class="['guide-msg', isEmailValid ? 'success' : 'error']">{{ emailMsg }}</p>
              </div>

              <div class="btn-area-center">
                <button @click="saveUserInfo" class="regi-submit-btn" :disabled="!isInfoReady">정보 수정</button>
              </div>
            </div>
          </div>

          <div class="section-card mt-50">
            <div class="card-head">
              <h3>비밀번호 변경</h3>
            </div>
            <div class="edit-form">

              <div class="input-section">
                <label>현재 비밀번호 <span class="req">*</span></label>
                <div class="pw-field-box">
                  <input v-model="pwData.currentPw" :type="showCurrentPw ? 'text' : 'password'"
                    placeholder="현재 비밀번호를 입력해 주세요" @input="validatePassword" />
                  <div class="pw-eye-icon" @click="showCurrentPw = !showCurrentPw">
                    <svg v-if="showCurrentPw" width="20" height="20" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                      stroke-width="2">
                      <path
                        d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  </div>
                </div>
                <p v-if="currPwMsg" class="guide-msg error">{{ currPwMsg }}</p>
              </div>

              <div class="input-section">
                <label>새 비밀번호 <span class="req">*</span></label>
                <div class="pw-field-box">
                  <input v-model="pwData.newPw" :type="showNewPw ? 'text' : 'password'" placeholder="대문자+특수문자 포함 6~16자"
                    @input="validatePassword" />
                  <div class="pw-eye-icon" @click="showNewPw = !showNewPw">
                    <svg v-if="showNewPw" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                      stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                      stroke-width="2">
                      <path
                        d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  </div>
                </div>
                <p v-if="pwErrorMsg" class="guide-msg error">{{ pwErrorMsg }}</p>
              </div>

              <div class="input-section">
                <label>비밀번호 확인 <span class="req">*</span></label>
                <div class="pw-field-box">
                  <input v-model="pwData.newPwConfirm" :type="showNewPwConfirm ? 'text' : 'password'"
                    placeholder="한 번 더 입력해주세요" @input="validatePassword" />
                  <div class="pw-eye-icon" @click="showNewPwConfirm = !showNewPwConfirm">
                    <svg v-if="showNewPwConfirm" width="20" height="20" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                      stroke-width="2">
                      <path
                        d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  </div>
                </div>
                <p v-if="pwConfirmMsg" :class="['guide-msg', isPwMatch ? 'success' : 'error']">{{ pwConfirmMsg }}</p>
              </div>

              <div class="btn-area-center">
                <button @click="handlePasswordUpdate" class="btn-action-submit"
                  :disabled="!isPwMatch || pwErrorMsg !== '' || !pwData.newPw">
                  비밀번호 변경
                </button>
              </div>
            </div>
          </div>

          <div class="withdraw-container">
            <span class="withdraw-link" @click="openWithdrawModal">회원 탈퇴</span>
          </div>
        </div>
      </div>

      <div v-if="isWithdrawModalOpen" class="modal-overlay">
        <div class="modal-card auth-modal" style="text-align: left;">
          <h3 style="color: #dc3545;">회원 탈퇴 본인 확인</h3>
          <p class="guide-msg mb-20">본인 확인을 위해 아이디와 비밀번호를 입력해 주세요</p>

          <div class="input-section">
            <label>아이디</label>
            <input v-model="withdrawData.id" type="text" placeholder="아이디 입력" />
          </div>

          <div class="input-section mt-10">
            <label>비밀번호</label>
            <div class="pw-field-box" style="width: 100%;">
              <input v-model="withdrawData.pw" :type="showWithdrawPw ? 'text' : 'password'" placeholder="비밀번호 입력" />
              <div class="pw-eye-icon" @click="showWithdrawPw = !showWithdrawPw">
                <svg v-if="showWithdrawPw" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </div>
            </div>
          </div>

          <div class="modal-btns mt-25">
            <button @click="handleFinalWithdraw" class="btn-modal-confirm" style="background: #dc3545;">탈퇴 확정</button>
            <button @click="isWithdrawModalOpen = false" class="btn-modal-cancel">취소</button>
          </div>
        </div>
      </div>

      <div v-if="showVehiModal" class="modal-overlay">
        <div class="modal-card auth-modal" style="width: 420px; text-align: left;">
          <h3 style="text-align: center; margin-bottom: 20px;">차량 정보 수정</h3>
          <div class="edit-form">

            <div class="f-row">
              <span>차량번호</span>
              <p class="readonly-val"
                style="font-weight: bold; padding: 10px; background: #f5f5f5; border-radius: 4px; flex: 1;">
                {{ editVehiForm.vehicleNum }}
              </p>
            </div>

            <div class="f-row">
              <span>차종 선택</span>
              <select v-model="editVehiForm.vehicleType"
                style="flex:1; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
                <option value="경차">경차</option>
                <option value="승용차">승용차</option>
                <option value="SUV">SUV</option>
                <option value="기타">기타</option>
              </select>
            </div>

            <div class="f-row">
              <span>유종 선택</span>
              <select v-model="editVehiForm.fuelType"
                style="flex:1; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
                <option value="휘발유">휘발유</option>
                <option value="경유">경유</option>
                <option value="LPG">LPG</option>
                <option value="전기차">전기차</option>
              </select>
            </div>

            <div class="modal-btns" style="margin-top: 30px; display: flex; justify-content: center; gap: 10px;">
              <button @click="submitVehiEdit" class="btn-modal-confirm">수정완료</button>
              <button @click="showVehiModal = false" class="btn-modal-cancel">취소</button>
            </div>

          </div>
        </div>
      </div>
    </main>
  </div>

  <!--[공통 모달] 보안 비밀번호 재확인 모달-->
  <div v-if="isAuthModalOpen" class="modal-overlay">
    <div class="modal-card auth-modal">
      <h3>보안을 위해 비밀번호를 입력해 주세요</h3>
      <div class="pw-field-box mb-25">
        <input v-model="authPw" :type="showAuthPw ? 'text' : 'password'" class="auth-pw-input"
          @keyup.enter="verifyAccess" />
        <div class="pw-eye-icon" @click="showAuthPw = !showAuthPw">
          <svg v-if="showAuthPw" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
            stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
            stroke-linecap="round" stroke-linejoin="round">
            <path
              d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24">
            </path>
            <line x1="1" y1="1" x2="23" y2="23"></line>
          </svg>
        </div>
      </div>
      <div class="modal-btns">
        <button @click="verifyAccess" class="btn-modal-confirm">확인</button>
        <button @click="cancelAccess" class="btn-modal-cancel">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// ref: 데이터와 화면을 연결해서, 데이터만 바꾸면 화면도 알아서 변하도록 하는 프레임워크
// vue: 일반 데이터를 Vue가 실시간으로 감시할 수 있는 반응형 주머니에 담는 도구. 주머니 속 내용물을 꺼낼 땐 .value를 꼭 붙여야 함
// onMounted: 화면(DOM)이 그려지자마자 이것 먼저 실행하라고 시키는 시작 버튼이며, 주로 서버에서 데이터를 처음 가져올 때 사용
// computed: 결과값을 캐싱(저장)해둬서 똑같은 계산을 반복 안 함. 연결된 데이터가 바뀔 때만 다시 계산해서 효율적

import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { updateInfoReq, withdrawReq, getMyInfoReq } from '@/api/member'
import { getVehiReq, delVehiReq, editVehiReq } from '@/api/vehicle'
import { getAdminInfoReq } from '@/api/customer.js';

import MemberDash from '@/components/mypage/MemberDash.vue'
import DoctorDash from '@/components/mypage/DoctorDash.vue'
import NurseDash from '@/components/mypage/NurseDash.vue'
import AdminDash from '@/components/mypage/AdminDash.vue'

const router = useRouter()
const route = useRoute()

// [상태 변수]
const currentView = ref('dash')  // 현재 활성화된 메뉴 탭 (기본값: 대시보드)
const userInfo = ref({})         // 로그인한 사용자의 상세 정보 주머니
const userType = ref('MEMBER')   // 사용자 대분류 (MEMBER, MED, ADMIN)
const currentTime = ref("")      // 상단 헤더에 표시될 실시간 시계

// [정보 수정 폼 전용]
const editForm = ref({});        // 수정용 데이터 임시 저장소
const tel1 = ref('010');
const tel2 = ref('');
const tel3 = ref(''); // 전화번호 3단 분리
const tel3Input = ref(null);     // 전화번호 자동 포커스용 Ref
const emailId = ref('');
const emailDomain = ref('naver.com');
const emailDirect = ref(''); // 이메일 분리

// [유효성 검사 메시지]
const nameMsg = ref('');
const isNameValid = ref(true);
const phoneMsg = ref('');
const isPhoneValid = ref(true);
const emailMsg = ref('');
const isEmailValid = ref(true);
const addrMsg = ref('');

// [비밀번호 및 보안 모달]
const showAuthPw = ref(false)
const isAuthModalOpen = ref(false)  // 정보 수정 전 비번 확인 모달
const authPw = ref("")              // 보안 확인용 입력 비번
const pwData = ref({                // 비번 변경 데이터
  currentPw: '',
  newPw: '',
  newPwConfirm: ''
});
const showCurrentPw = ref(false);
const showNewPw = ref(false);
const showNewPwConfirm = ref(false); // 비번 숨김/표시 눈 아이콘
const showWithdrawPw = ref(false);
const currPwMsg = ref('');
const pwErrorMsg = ref('');
const pwConfirmMsg = ref('');
const isPwMatch = ref(false);       // 새 비번 일치 여부

// [차량 관리]
const myVehicles = ref([])          // 사용자가 등록한 차량 리스트
const showVehiModal = ref(false)    // 차량 수정 모달
const editVehiForm = ref({          // 차량 수정 임시 데이터
  vehicleNum: '',
  vehicleType: '',
  fuelType: ''
})

// [탈퇴 관리]
const isWithdrawModalOpen = ref(false) // 회원 탈퇴 확인 모달
const withdrawData = ref({ id: '', pw: '' }); // 탈퇴 확인용 데이터


// ======================================= 권한 및 유틸 판별기 =======================================
// 의사/간호사 판별
const isDoctor = computed(() => {
  const role = (userInfo.value.role || '').toUpperCase();
  return userType.value === 'MED' && (role.includes('의사') || role.includes('DOCTOR'));
});
const isNurse = computed(() => {
  const role = (userInfo.value.role || '').toUpperCase();
  return userType.value === 'MED' && (role.includes('간호사') || role.includes('NURSE'));
});

// 사이드바 상단 직함 표시용
const userRoleDisplay = computed(() => {
  if (userType.value === 'MEMBER') {
    return `${formatBirthday(userInfo.value.birthday)} | ${userInfo.value.gender == 1 ? '남' : '여'}`;
  }
  return `${userInfo.value.adminDeptName || userInfo.value.deptName || '소속없음'} | ${userInfo.value.role || userInfo.value.rank || '직원'}`;
});

// 정보 업데이트 버튼 활성화 조건
const isInfoReady = computed(() => {
  return (
    isNameValid.value &&        // 이름이 완성된 한글 2자 이상인가
    isPhoneValid.value &&       // 전화번호 형식이 맞는가
    isEmailValid.value &&       // 이메일 형식이 맞는가
    editForm.value.address      // 주소가 비어있지 않은가
  )
})

// ======================================= 공통 핵심 로직: 화면 전환 / 정보 로드 =======================================
// 화면 전환 및 데이터 로드
const changeView = (view) => {
  if (view === 'edit') {
    isAuthModalOpen.value = true; // 개인정보 수정 클릭 시 보안 비밀번호 확인 모달 띄움
  } else {

    if (currentView.value === 'edit') {
      resetPwData();
    }

    currentView.value = view;
    router.replace({ query: { tab: view } }); // 주소창에 현재 탭 표시
    if (view === 'vehi') fetchVehicles();      // 차량 관리 탭 클릭 시 목록 새로고침
  }
};

// 상세 정보 호출 함수
const fetchMyDetailInfo = async () => {
  try {
    // 서버에서 새 정보를 받기 전, 현재 세션이 들고 있는 비번을 미리 챙겨둠
    const currentStoredPw = userInfo.value.password;

    const res = await getMyInfoReq()
    if (res.data) {
      // 기본 정보 저장 (Proxy에 찍히는 데이터)
      userInfo.value = { ...userInfo.value, ...res.data };

      // 권한이 ADMIN일 경우, 백엔드 전용 정보를 추가로 가져와서 합침
      if (userType.value === 'ADMIN') {
        try {
          const adminRes = await getAdminInfoReq();
          if (adminRes.data) {
            // 기존 userInfo에 isWonmu, isPr, deptName 등을 덮어씌움
            userInfo.value = {
              ...userInfo.value,
              ...adminRes.data
            }
            console.log("어드민 합체 완료 데이터:", userInfo.value)
          }
        } catch (adminErr) {
          console.error("어드민 추가 정보 로드 실패:", adminErr)
        }
      }
      // 비번이 비어있으면 아까 백업한 비번을 다시 넣어줌
      if (!userInfo.value.password) {
        userInfo.value.password = currentStoredPw;
      }
      syncFields() // 화면 폼 데이터 분리 로직 실행
    }
  } catch (e) {
    console.error("상세 정보 가져오기 실패", e)
  }
}

// 서버 데이터를 화면 입력 칸(Input)들에 맞게 쪼개주는 작업
const syncFields = () => {
  editForm.value = { ...userInfo.value };

  // 연락처 분리 (예: 01012345678)
  const phone = editForm.value.phoneNumber || '';
  if (phone.length >= 10) {
    tel1.value = phone.substring(0, 3);
    tel2.value = phone.length === 11 ? phone.substring(3, 7) : phone.substring(3, 6);
    tel3.value = phone.length === 11 ? phone.substring(7, 11) : phone.substring(6, 10);
  }
  // 이메일 분리 (예: princess@naver.com)
  const email = editForm.value.email || '';
  if (email.includes('@')) {
    const parts = email.split('@');
    emailId.value = parts[0];
    const domains = ['naver.com', 'gmail.com', 'nate.com', 'daum.net'];
    if (domains.includes(parts[1])) {
      emailDomain.value = parts[1];
    } else {
      emailDomain.value = 'direct';
      emailDirect.value = parts[1];
    }
  }
};

// ======================================= 차량 관리 (fetch, edit, delete) =======================================
// 차량 목록
const fetchVehicles = async () => {
  try {
    const res = await getVehiReq();
    myVehicles.value = res.data;
  } catch (e) {
    console.error("차량 조회 실패", e);
  }
};

// 차량 수정 모달 열기 함수
const openVehiEdit = (car) => {
  editVehiForm.value = { ...car } // 선택한 차량 정보 복사
  showVehiModal.value = true
}

// 차량 수정 저장 함수
const submitVehiEdit = async () => {
  if (!editVehiForm.value.vehicleType || !editVehiForm.value.fuelType) {
    alert("차종과 유종을 모두 입력/선택해 주세요.")
    return
  }

  try {
    const res = await editVehiReq(editVehiForm.value)
    if (res.data === 'success' || res.data === true) {
      alert("차량 정보가 성공적으로 수정되었습니다.")
      showVehiModal.value = false
      fetchVehicles() // 목록 새로고침
    } else {
      alert("수정에 실패했습니다. 다시 시도해 주세요.")
    }
  } catch (e) {
    console.error("차량 수정 오류:", e)
    alert("오류가 발생했습니다")
  }
}

// 차량 삭제
const deleteVehicle = async (num) => {
  if (!confirm("선택한 차량을 삭제하시겠습니까?")) return;

  try {
    const res = await delVehiReq(num);

    if (res.data === 'success') {
      alert("정상적으로 삭제되었습니다.");
      fetchVehicles(); // 목록 새로고침
    } else {
      // 서버에서 "fail"이 돌아온 경우
      alert("삭제에 실패했습니다. 내 차량이 아니거나 이미 삭제된 차량입니다.");
      console.log("백엔드 응답:", res.data);
    }
  } catch (e) {
    console.error("차량 삭제 오류:", e);
    alert("오류가 발생했습니다.");
  }
};

// ======================================= 유효성 검사 및 수정 저장 =======================================
// 이름 검사
const checkName = () => {
  editForm.value.name = editForm.value.name.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  const pattern = /^[가-힣]{2,}$/;
  if (!pattern.test(editForm.value.name)) {
    isNameValid.value = false;
    nameMsg.value = "2자 이상 입력해 주세요";
  } else {
    isNameValid.value = true;
    nameMsg.value = "";
  }
};
// 전화번호 검사
const onlyNumber = (e, field) => {
  const val = e.target.value.replace(/[^0-9]/g, '');
  if (field === 'tel2') {
    tel2.value = val;
    if (val.length === 4) tel3Input.value?.focus();
  } else if (field === 'tel3') {
    tel3.value = val;
  }
  isPhoneValid.value = tel2.value.length >= 3 && tel3.value.length === 4;
  phoneMsg.value = isPhoneValid.value ? "" : "전화번호 형식을 확인해 주세요";
};
// 이메일 검사
const checkEmail = () => {
  const domain = emailDomain.value === 'direct' ? emailDirect.value : emailDomain.value;
  const fullEmail = `${emailId.value}@${domain}`;
  const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  isEmailValid.value = pattern.test(fullEmail);
  emailMsg.value = isEmailValid.value ? "" : "이메일 형식이 올바르지 않습니다";
};

const checkAddr = () => {
  a
  addrMsg.value = editForm.value.address ? "" : "주소를 입력해 주세요";
};

const saveUserInfo = async () => {
  editForm.value.phoneNumber = `${tel1.value}${tel2.value}${tel3.value}`;
  const domain = emailDomain.value === 'direct' ? emailDirect.value : emailDomain.value;
  editForm.value.email = `${emailId.value}@${domain}`;

  try {
    await updateInfoReq(editForm.value);
    userInfo.value = { ...editForm.value };
    alert("정보가 성공적으로 수정되었습니다.");
    currentView.value = 'dash';
    router.replace({ query: { tab: 'dash' } });
  } catch (e) {
    alert("수정에 실패했습니다.");
  }
};

// ======================================= [보안: 비밀번호 변경 및 탈퇴] =======================================
// 비밀번호 입력값 및 관련 메시지를 싹 비워주기
const resetPwData = () => {
  pwData.value = {
    currentPw: '',
    newPw: '',
    newPwConfirm: ''
  };
  currPwMsg.value = '';
  pwErrorMsg.value = '';
  pwConfirmMsg.value = '';
  isPwMatch.value = false;

  // 눈 아이콘 상태도 초기화하고 싶다면 추가
  showCurrentPw.value = false;
  showNewPw.value = false;
  showNewPwConfirm.value = false;
};

// 비밀번호 관리
const validatePassword = () => {
  if (pwData.value.currentPw) {
    pwData.value.currentPw = pwData.value.currentPw.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  }
  if (pwData.value.newPw) {
    pwData.value.newPw = pwData.value.newPw.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  }
  if (pwData.value.newPwConfirm) {
    pwData.value.newPwConfirm = pwData.value.newPwConfirm.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  }

  const { currentPw, newPw, newPwConfirm } = pwData.value;
  const pwRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,16}$/;

  // 초기화
  pwErrorMsg.value = '';
  pwConfirmMsg.value = '';
  isPwMatch.value = false;

  if (!currentPw) {
    currPwMsg.value = "현재 사용중인 비밀번호를 입력해 주세요";
  } else if (currentPw !== userInfo.value.password) {
    currPwMsg.value = "현재 비밀번호가 일치하지 않습니다";
  } else {
    currPwMsg.value = "";
  }

  // 새 비밀번호 형식 실시간 검사
  if (!newPw) {
    pwErrorMsg.value = "영문 대문자, 특수문자 포함 6 ~ 16자 이내";
  } else if (!pwRegex.test(newPw)) {
    pwErrorMsg.value = "비밀번호 형식이 올바르지 않습니다(영문 대문자, 특수문자 포함 6 ~ 16자 이내)";
  } else if (newPw === userInfo.value.password) {
    pwErrorMsg.value = "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다";
  } else {
    pwErrorMsg.value = "";
  }

  // 새 비밀번호 확인 일치 실시간 검사
  if (!newPwConfirm) {
    pwConfirmMsg.value = "비밀번호를 한 번 더 입력해 주세요";
    isPwMatch.value = false;
  } else if (newPw !== newPwConfirm) {
    pwConfirmMsg.value = "비밀번호가 일치하지 않습니다";
    isPwMatch.value = false;
  } else {
    pwConfirmMsg.value = "✔ 비밀번호가 일치합니다";
    isPwMatch.value = true;
  }
};

// [수정] 비밀번호 변경 (제출 시 최종 체크)
const handlePasswordUpdate = async () => {
  const { currentPw, newPw, newPwConfirm } = pwData.value;

  // 입력 누락 체크
  if (!currentPw || !newPw || !newPwConfirm) {
    return alert("비밀번호를 모두 입력해 주세요.");
  }

  // 현재 비밀번호 일치 확인
  if (currentPw !== userInfo.value.password) {
    currPwMsg.value = "현재 비밀번호가 일치하지 않습니다";
    return alert("현재 비밀번호를 다시 확인해 주세요");
  } else {
    currPwMsg.value = "";
  }

  // 실시간 에러가 남아있는지 체크
  if (pwErrorMsg.value) return alert("비밀번호 형식을 확인해 주세요(영문 대문자, 특수문자 포함 6 ~ 16자 이내)");
  if (!isPwMatch.value) return alert("비밀번호가 일치하지 않습니다.");

  try {
    const res = await updateInfoReq({
      ...userInfo.value,
      password: newPw
    });

    if (res.data === 'success') {
      alert("비밀번호가 성공적으로 변경되었습니다\n보안을 위해 다시 로그인해 주세요");

      resetPwData();
      // 세션 비우고 로그인 페이지로 이동
      sessionStorage.clear();
      router.push('/login');
    } else {
      alert("비밀번호 변경에 실패했습니다. 다시 시도해 주세요.");
    }
  } catch (e) {
    console.error("비밀번호 변경 에러:", e);
    alert("서버 통신 중 오류가 발생했습니다");
  }
};

const verifyAccess = () => {
  if (authPw.value === userInfo.value.password) {
    isAuthModalOpen.value = false;
    currentView.value = 'edit';

    router.replace({ query: { tab: 'edit' } });

    authPw.value = '';
  }
  else { alert('비밀번호가 일치하지 않습니다.'); }
};

const cancelAccess = () => {
  isAuthModalOpen.value = false;
  authPw.value = '';
  resetPwData(); // 혹시 모를 잔여 데이터 삭제
};

// 회원 탈퇴
// [이중 보안 탈퇴 로직]
const openWithdrawModal = () => {
  withdrawData.value = { id: '', pw: '' };
  isWithdrawModalOpen.value = true;
};

const handleFinalWithdraw = async () => {
  const { id, pw } = withdrawData.value;
  if (id !== userInfo.value.id || pw !== userInfo.value.password) {
    return alert("아이디 또는 비밀번호가 일치하지 않습니다");
  }
  if (!confirm("정말로 탈퇴하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다")) return;

  try {
    const res = await withdrawReq();
    if (res.data === 'success') {
      alert("회원 탈퇴가 완료되었습니다\n그동안 이용해 주셔서 감사합니다");

      sessionStorage.clear();
      router.push('/');
    } else {
      alert("탈퇴 처리에 실패했습니다");
    }
  } catch (e) {
    alert("서버 통신 중 오류가 발생했습니다");
  }
};

// ======================================= 공통 유틸 =======================================
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

const formatBirthday = (birth) => {
  return birth ? String(birth).replace(/(\d{4})(\d{2})(\d{2})/, '$1.$2.$3') : '';
}

const getTimeGreeting = () => {
  const h = new Date().getHours();
  if (h < 12) return '좋은 아침입니다';
  if (h < 18) return '즐거운 오후입니다';
  return '편안한 저녁입니다';
};

const updateClock = () => {
  currentTime.value = new Date().toLocaleString('ko-KR', { month: 'long', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit', second: '2-digit' });
};

const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      editForm.value.address = data.roadAddress;
    }
  }).open();
};


// ======================================= 초기 로딩 =======================================
onMounted(async () => {
  // 로그인 체크
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) {
    router.push('/login');
    return;
  }

  try {
    const parsed = JSON.parse(loginData);
    userInfo.value = parsed;

    // 사용자 타입 설정 (MEMBER, MED, ADMIN)
    if (sessionStorage.getItem('loginType')) {
      userType.value = sessionStorage.getItem('loginType');
    }
    else {
      userType.value = parsed.role ? 'MED' : 'MEMBER';
    }

    if ((parsed.loginType || '').toUpperCase() === 'ADMIN') {
      userType.value = 'ADMIN';
    }

    // 진짜 상세 정보 서버에서 긁어오기
    await fetchMyDetailInfo();

    // 초기 화면 설정
    if (route.query.tab) {
      // URL에 꼬리표(?tab=edit)가 있으면 해당 화면으로 이동
      currentView.value = route.query.tab;
    } else {
      // 꼬리표 없으면 무조건 dash
      currentView.value = 'dash';
    }

    // 차량 관리 화면이면 데이터 미리 가져오기
    if (currentView.value === 'vehi') {
      fetchVehicles();
    }
  }
  catch (e) {
    console.error("초기 로딩 에러", e)
  }

  console.log("현재 유저 타입:", userType.value);
  console.log("현재 부서명:", userInfo.value.adminDeptName);

  // 시계 작동
  updateClock();
  setInterval(updateClock, 1000);
});
</script>

<style scoped>
/* 1. 기본 폰트 및 배경 설정 */
:deep(*) {
  font-family: 'Pretendard', -apple-system, sans-serif !important;
  border-radius: 0;
  box-sizing: border-box;
}

.full-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #ffffff;
  /* 배경 화이트 */
  color: #333;
}

/* 2. 사이드바 (Sidebar) */
.sidebar {
  width: 260px;
  background-color: #ffffff;
  border-right: 1px solid #eee;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.03);
  /* 배경 구분을 위한 그림자 */
  padding: 43px 15px;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.profile-area {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #eee;
}

.user-id {
  font-size: 24px;
  font-weight: 700;
  text-align: center;
  color: #005baa;
  /* 메인 컬러 */
  margin-bottom: 7px;
}

.user-tag {
  font-size: 18px;
  text-align: center;
  color: #666;
  margin-bottom: 12px;
}

.join-date {
  font-size: 16px;
  text-align: center;
  color: #bcbcbc;
  margin-bottom: 36px;
}

/* 사이드바 메뉴 */
.side-nav ul {
  font-size: 20px;
  list-style: none;
  padding: 0;
}

.side-nav li {
  padding: 12px 20px;
  margin-bottom: 5px;
  cursor: pointer;
  color: #555;
  font-weight: 600;
  transition: all 0.2s ease;
  border-left: 4px solid transparent;
}

.side-nav li:hover {
  background-color: #f8fbff;
  color: #005baa;
}

.side-nav li.active {
  background-color: #f0f6ff;
  color: #005baa;
  font-weight: 700;
  border-left: 4px solid #005baa;
  /* 활성 탭 강조 */
}

/* 3. 메인 콘텐츠 영역 (Main Content) */
.main-content {
  flex-grow: 1;
  padding: 40px 50px;
  background-color: #ffffff;
  overflow-y: auto;
}

.dashboard-header {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.welcome-text h2 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #333;
}

.blue-txt {
  color: #005baa;
}

.current-time {
  color: #888;
  font-size: 1.1rem;
  margin-top: 8px;
}

/* 4. 공통 섹션 카드 디자인 (그림자 포인트) */
.section-card {
  background: #ffffff;
  border: 1px solid #eee;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  /* 화이트 배경 위 섹션 구분 */
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-head h3 {
  padding: 8px;
  font-size: 1.7rem;
  font-weight: 800;
  color: #005baa;
  position: relative;
}

/* 5. 병원 공통 테이블 (Hospital Table) */
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
}

.bold-blue {
  color: #005baa;
  font-weight: 700;
}

/* --- 6. 정보 수정 폼 --- */
.edit-card-wrap {
  width: 100%;
  max-width: 850px;
  margin: 0 auto;
}

.req {
  color: #dc3545;
}

.f-row,
.input-section {
  display: flex;
  flex-wrap: wrap;
  /* 에러 메시지가 아래로 떨어질 수 있게 허용 */
  align-items: flex-start;
  padding: 20px 10px;
  border-bottom: 1px solid #f5f5f5;
  gap: 0 20px;
  /* 가로 간격만 20px 유지 */
}

/* 라벨 스타일 */
.f-row span,
.input-section label {
  width: 120px;
  flex-shrink: 0;
  font-size: 18px;
  font-weight: 700;
  color: #666;
  padding-top: 14px;
}

/* 입력 영역 컨테이너 (박스 수축 방지 핵심) */
.readonly-val,
.tel-flex-row,
.addr-group,
.pw-field-box,
.input-section input[type="text"] {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.readonly-val {
  padding-top: 14px;
  font-size: 18px;
  color: #333;
  font-weight: 700;
}

/* 주소 영역 상세 설정 */
.addr-top-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.addr-top-row input {
  flex: 1 !important;
}

.mt-8 {
  margin-top: 8px;
  width: 100%;
}

/* 연락처 및 이메일 가로 나열 */
.tel-flex-row {
  display: flex;
  flex-direction: row;
  /* 여기는 가로 나열 */
  align-items: center;
  gap: 8px;
}

.tel-flex-row select,
.tel-flex-row input {
  flex: 1;
  min-width: 0;
}

.tel-dash {
  flex-shrink: 0;
  color: #888;
}

/* 가이드/에러 메시지 (하단 왼쪽 정렬 완성) */
.guide-msg {
  display: block;
  width: 100%;
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  text-align: left;
}

.mb-20 {
  display: block;
  width: 100%;
  font-size: 18px;
  font-weight: 500;
  text-align: left;
  margin-top: -12px;
  margin-bottom: 20px;
}

/* 컨테이너 내부에 있는 메시지는 마진 없이 바로 왼쪽 */
.addr-group .guide-msg,
.tel-flex-row+.guide-msg,
.pw-field-box .guide-msg {
  margin-left: 0;
}

/* 라벨 바로 옆에 붙는 메시지(이름 등)는 밀기 */
.input-section>.guide-msg {
  margin-left: 140px;
}

.success {
  color: #28a745;
}

.error {
  color: #dc3545;
}

/* 공통 인풋 스타일 */
input[type="text"],
input[type="password"],
select {
  width: 100%;
  padding: 15px;
  border: 1px solid #ddd;
  background: #fff;
  font-size: 18px;
  height: 55px;
}

input:focus,
select:focus {
  outline: none;
  border-color: #005baa;
  box-shadow: 0 0 0 3px rgba(0, 91, 170, 0.1);
}

/* 버튼 및 기타 */
.check-btn {
  width: 120px;
  height: 55px;
  background-color: #333;
  color: #fff;
  border: none;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  flex-shrink: 0;
}

/* 7. 버튼 디자인 (Buttons) */
.regi-submit-btn,
.btn-action-submit {
  min-width: 250px;
  /* 최소 너비 확보 */
  height: 60px;
  padding: 0 40px;
  background-color: #005baa;
  color: #fff;
  font-size: 18px;
  font-weight: 800;
  border: none;
  cursor: pointer;
  box-shadow: 0 5px 15px rgba(0, 91, 170, 0.2);
}

.regi-submit-btn:disabled {
  background-color: #bcbcbc;
}

/* 정보 업데이트 및 비밀번호 변경 버튼 (너비 최적화) */
.btn-area-center {
  text-align: center;
  margin-top: 40px;
}

.btn-add-sm {
  font-size: 18px;
  background-color: #fbb900;
  /* 포인트 오렌지 */
  color: #fff;
  border: none;
  padding: 12px 18px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 5px;
}


.btn-update-table {
  background-color: #005baa;
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
  margin-right: 8px;
  font-size: 16px;
}

.btn-cancel-table {
  background-color: #fff;
  color: #888;
  border: 1px solid #ddd;
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
  font-size: 16px;
}

.btn-update-table:hover {
  background-color: #004a8a;
}

.btn-cancel-table:hover {
  background-color: #f8f8f8;
  color: #dc3545;
  border-color: #dc3545;
}

/* 비밀번호 박스 내부 아이콘 배치 */
.pw-field-box {
  position: relative;
  /* 아이콘 배치의 기준점 */
  display: flex;
  align-items: center;
}

.pw-field-box input {
  padding-right: 50px;
  /* 아이콘 공간 확보 */
}

.pw-eye-icon {
  position: absolute;
  right: 25px;
  /* 인풋 박스 안쪽 오른쪽에 고정 */
  top: 17px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #888;
  z-index: 10;
}

.pw-eye-icon:hover {
  color: #005baa;
}

/* 브라우저 기본 비밀번호 눈 아이콘(Edge 등) 숨기기 */
input::-ms-reveal,
input::-ms-clear {
  display: none;
  /* MS Edge에서 나오는 눈 아이콘 제거 */
}

/* 다른 브라우저의 기본 요소들도 방어 */
input[type="password"]::-webkit-contacts-auto-fill-button,
input[type="password"]::-webkit-credentials-auto-fill-button {
  visibility: hidden;
  pointer-events: none;
  position: absolute;
  right: 0;
}

/* 8. 모달*/
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal-card {
  background: white;
  padding: 70px 40px;
  min-width: 600px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-card h3 {
  margin-bottom: 20px;
  font-size: 26px;
}

.modal-btns {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.btn-modal-confirm {
  flex: 1;
  background: #005baa;
  color: white;
  padding: 15px;
  border: none;
  font-weight: 700;
  font-size: 18px;
}

.btn-modal-cancel {
  flex: 1;
  background: #eee;
  color: #333;
  padding: 15px;
  border: none;
  font-size: 18px;
}

.modal-card .input-section {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 0;
}

.modal-card .input-section label {
  width: 100%;
  padding-top: 0;
}

/* 9. 기타 유틸리티 */
.mt-50 {
  margin-top: 50px;
}

.red-alert {
  color: #dc3545;
  font-weight: 700;
}

.success {
  color: #28a745;
}

.error {
  color: #dc3545;
  font-size: 0.85rem;
  margin-top: 5px;
}

/* 회원 탈퇴 */
.withdraw-container {
  margin-top: 40px;
  text-align: center;
  cursor: pointer;
}

.withdraw-link {
  color: #ddd;
  border-bottom: 1px solid #ddd;
}

/* 등록된 차량이 없습니다 */
.empty-msg {
    font-size: 20px;
    margin-top: 30px;
    text-align: center;
}
</style>