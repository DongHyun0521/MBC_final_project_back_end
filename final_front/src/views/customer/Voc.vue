<template>
  <div class="voc-container">
    <div class="page-header">
      <h2>고객의 소리</h2>
      <p>병원 이용 중 불편하셨던 점이나 칭찬하고 싶은 점을 편하게 남겨주세요.</p>
    </div>

    <div class="list-controls">
      <div class="status-tabs">
        <button v-for="tab in currentTabs" :key="tab.id" :class="['tab-btn', { active: filterStatus === tab.id }]"
          @click="changeFilter(tab.id)">
          {{ tab.label }}
        </button>
      </div>
      <div class="right-area">
        <button v-if="!isAdmin" class="btn-write" @click="openWriteModal">문의 작성</button>
      </div>
    </div>

    <div class="voc-list">
      <div v-if="vocList.length === 0" class="no-data">등록된 게시글이 없습니다.</div>

      <div v-for="item in vocList" :key="item.vocId" class="voc-item" :class="{ 'is-open': openItemId === item.vocId }">

        <div class="voc-header" @click="toggleItem(item)">
          <div class="vh-left">
            <span :class="['status-badge', getStatusClass(item)]">{{ getStatusText(item) }}</span>
            <span class="vh-title">{{ item.title }}
              <span v-if="item.uploadImg" class="clip-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="11" height="11" viewBox="0 0 24 24" fill="none"
                  stroke="#666" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48" />
                </svg>
              </span>
            </span>
          </div>

          <div class="vh-right">
            <span class="vh-writer">{{ getWriterName(item) }}</span>
            <span class="vh-date">{{ formatDate(item.writeDate) }}</span>
            <span class="vh-arrow">
              <svg v-if="openItemId === item.vocId" xmlns="http://www.w3.org/2000/svg" width="12" height="12"
                viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round">
                <path d="m18 15-6-6-6 6" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
                stroke="#999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="m6 9 6 6 6-6" />
              </svg>
            </span>
          </div>
        </div>

        <transition name="slide-fade">
          <div v-if="openItemId === item.vocId" class="voc-body">

            <div class="q-section">
              <div class="type-label q-label">Q</div>
              <div class="content-box">
                <pre class="content-text">{{ item.content }}</pre>

                <div v-if="item.uploadImg" class="file-download-area">
                  <div v-if="isImage(item.uploadImg)" class="img-preview-box">
                    <img :src="getImageUrl(item.uploadImg)" alt="첨부이미지" class="attached-img"
                      @click.stop="openImgModal(item.uploadImg)" style="cursor: pointer" title="클릭해서 크게 보기">
                    <!-- <div style="margin-top: 5px;">
                      <a :href="getImageUrl(item.uploadImg)" download class="file-link">{{
                        item.uploadImg.split('/').pop() }}</a>
                    </div> -->
                  </div>
                  <div v-else class="doc-preview-box" style="margin-top: 5px;">
                    <a :href="getImageUrl(item.uploadImg)" download class="file-link">
                      {{ item.uploadImg?.split('/').pop() || '' }}
                    </a>
                  </div>
                </div>

                <div v-if="!isAdmin && !item.answerStatus" class="action-row">
                  <button @click.stop="openEditModal(item)">수정</button>
                  <span class="bar">|</span>
                  <button @click.stop="deleteVocUser(item.vocId)" class="btn-del">삭제</button>
                </div>

                <div v-if="canManageDeleted" class="action-row">
                  <button v-if="item.del === 0" @click.stop="deleteVocAdmin(item.vocId)" class="btn-del">보관함 이동</button>
                  <button v-else @click.stop="restoreVoc(item.vocId)" class="btn-restore">복구</button>
                </div>
              </div>
            </div>

            <div class="a-section">
              <div class="type-label a-label" :class="{ 'waiting': !item.answerStatus }">A</div>

              <div class="answer-wrapper">

                <div v-if="item.answerStatus" class="answer-view-box">
                  <div class="ans-meta">
                    <span class="admin-name">서울에스병원 원무팀</span>
                    <span class="ans-date">{{ formatDate(item.answerWriteDate) }}</span>
                  </div>

                  <div v-if="editingReplyId !== item.vocId" class="ans-content">
                    <pre>{{ item.answerContent }}</pre>
                  </div>

                  <div v-else class="reply-edit-area">
                    <textarea v-model="replyText" class="common-textarea"></textarea>
                    <div class="edit-btns">
                      <button @click="updateReply(item)" class="btn-save">저장</button>
                      <button @click="cancelEditReply" class="btn-cancel">취소</button>
                    </div>
                  </div>

                  <div v-if="isWonmu && editingReplyId !== item.vocId" class="ans-actions">
                    <button @click="startEditReply(item)">수정</button>
                    <span class="bar">|</span>
                    <button @click="deleteReply(item)" class="btn-del">삭제</button>
                  </div>
                </div>

                <div v-else-if="canReply && item.del === 0" class="reply-write-box">
                  <div class="write-header">
                    <span class="guide-txt">답변 작성</span>
                  </div>

                  <textarea v-model="replyText" class="common-textarea" placeholder="답변 내용을 입력해 주세요."></textarea>
                  <div class="write-footer">
                    <button @click="submitReply(item)" class="btn-submit">답변 등록</button>
                  </div>
                </div>

                <div v-else class="waiting-box">
                  <p v-if="item.del === 1">삭제된 게시글입니다.</p>
                  <p v-else>담당자가 내용을 확인하고 있습니다.<br>빠른 시일 내에 답변드리겠습니다.</p>
                </div>

              </div>
            </div>

          </div>
        </transition>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3>{{ isEditing ? '문의 수정' : '새 문의 작성' }}</h3>
          <button class="close-btn" @click="showModal = false">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#333"
              stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 6 6 18" />
              <path d="m6 6 12 12" />
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>제목 <span class="req">*</span></label>
            <input type="text" v-model="writeForm.title" class="modal-input" placeholder="제목을 입력하세요">
          </div>
          <div class="input-group">
            <label>내용 <span class="req">*</span></label>
            <textarea v-model="writeForm.content" class="modal-textarea" placeholder="문의하실 내용을 상세히 입력하세요"></textarea>
          </div>

          <div class="input-group">
            <label>첨부파일 (사진/문서)</label>
            <div class="file-upload-box">
              <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*, .pdf, .hwp, .doc, .docx">

              <div v-if="previewUrl" class="preview-container">
                <img v-if="isImageFile" :src="previewUrl" class="preview-img">
                <div v-else class="file-info-tag">{{ selectedFile ? selectedFile.name : (writeForm.uploadImg?.split('/').pop() || '') }}</div>
                <button class="btn-remove-file" @click="removeFile">X</button>
              </div>
            </div>
          </div>

        </div>
        <div class="modal-foot">
          <button class="btn-cancel" @click="showModal = false">취소</button>
          <button class="btn-confirm" @click="submitWrite">{{ isEditing ? '수정완료' : '등록하기' }}</button>
        </div>
      </div>
    </div>

    <div v-if="showImgModal" class="img-modal-overlay" @click="closeImgModal">
      <div class="img-modal-content" @click.stop>
        <img :src="currentImgUrl" alt="확대 이미지">
        <button class="btn-close-img" @click="closeImgModal">×</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getMyVocsReq, addVocReq, editVocReq, delVocReq,
  getAdminVocListReq,
  addVocReplyReq, editVocReplyReq, delVocReplyReq,
  delVocByAdminReq, restoreVocReq, getAdminInfoReq
} from '@/api/customer';

// ============================= 상태 변수 (State) =============================
const filterStatus = ref('all');
const openItemId = ref(null);
const editingReplyId = ref(null);
const replyText = ref('');
const showModal = ref(false);
const isEditing = ref(false);
const loginInfo = ref({});
const vocList = ref([]);
const writeForm = ref({ vocId: '', title: '', content: '', uploadImg: ''});
const isWonmuState = ref(false);

// 파일 업로드를 위한 변수들
const fileInput = ref(null);
const selectedFile = ref(null);
const previewUrl = ref(null);
const isImageFile = ref(false);

// 이미지 확대 모달 제어 변수
const showImgModal = ref(false);
const currentImgUrl = ref('');

// 답변 가이드 상수
const replyHeader = "안녕하십니까, 서울에스병원 원무과입니다.\n고객님의 소중한 의견에 깊이 감사드립니다.\n\n";
const replyFooter = "\n\n추가로 궁금하신 점이 있으시면 언제든 문의해주시기 바랍니다.\n항상 고객님의 건강과 행복을 기원합니다.\n감사합니다.";

// ============================= 권한 및 설정 =============================
// 행정 부서인지 확인
const isAdmin = computed(() => String(loginInfo.value.loginType || loginInfo.value.role || '').toUpperCase() === 'ADMIN');
// 행정 부서에서 원무팀인지 확인
const isWonmu = computed(() => {
  if (!isAdmin.value) return false;
  if (isWonmuState.value) return true;
  const info = loginInfo.value || {};
  const dept = String(info.admin_dept_name || '').trim();
  return dept.includes('원무');
});
const canReply = computed(() => isAdmin.value && isWonmu.value);
const canManageDeleted = computed(() => isAdmin.value && isWonmu.value);

// 목록 필터
const currentTabs = computed(() => {
  const tabs = [{ id: 'all', label: '전체' }];

  // [미답변] 일반회원이거나(isAdmin이 아님) 원무팀일 때만 보임
  if (!isAdmin.value || isWonmu.value) {
    tabs.push({ id: 'unanswered', label: '미답변' });
  }

  // [답변완료] 누구나 공통
  tabs.push({ id: 'answered', label: '답변완료' });

  // [휴지통] 오직 원무팀만 보임
  if (isWonmu.value) {
    tabs.push({ id: 'deleted', label: '휴지통' });
  }

  return tabs;
});
// const currentTabs = computed(() => {
//   const tabs = [{ id: 'all', label: '전체' }];

//   tabs.push({ id: 'unanswered', label: '미답변' });
//   tabs.push({ id: 'answered', label: '답변완료' });

//   if (isWonmu.value) {
//     tabs.push({ id: 'deleted', label: '휴지통' });
//   }
//   return tabs;
// });


// ============================= 유틸리티 함수 =============================
const getWriterName = (item) => {
  if (item.writerName || item.name) {
    return item.writerName || item.name;
  }
  if (!isAdmin.value && loginInfo.value.name) return loginInfo.value.name;
  return '익명';
};
const getStatusText = (item) => item.del === 1 ? '삭제됨' : (item.answerStatus ? '답변완료' : '대기중');
const getStatusClass = (item) => item.del === 1 ? 'badge-del' : (item.answerStatus ? 'badge-done' : 'badge-wait');
const formatDate = (d) => d ? String(d).substring(0, 10) : '';

// [추가] 서버 이미지 경로(/images/...)를 전체 URL(http://...)로 변환
const getImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

// [신규] 파일 이름(abc.jpg)을 보고 이미지인지 아닌지 판단하는 함수
const isImage = (fileName) => {
  if (!fileName) return false;
  const ext = fileName.split('.').pop().toLowerCase();
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext);
};

// ============================= 데이터 조회 및 목록 제어 =============================
const fetchList = async () => {
  try {
    let res;
    if (isAdmin.value) {
      if (filterStatus.value === 'deleted' && !isWonmu.value) { filterStatus.value = 'all'; return; }
      res = await getAdminVocListReq(filterStatus.value);
    } else {
      res = await getMyVocsReq(filterStatus.value);
    }

    let list = res.data || [];

    // 관리자인데 원무팀이 아닐 경우, 답변 목록 제외하고 접근 x
    if (isAdmin.value && !isWonmu.value) {
      list = list.filter(item => item.answerStatus === true);
    }
    vocList.value = list;

  } catch (e) { vocList.value = []; }
};

const changeFilter = (id) => {
  filterStatus.value = id;
  openItemId.value = null;
  fetchList();
};

const toggleItem = (item) => {
  // [정답] 카멜케이스 사용
  if (openItemId.value === item.vocId) {
    openItemId.value = null;
  }
  // 2. 닫힌 글을 누르면? -> 열기
  else {
    openItemId.value = item.vocId;
    editingReplyId.value = null; // 수정 모드 초기화

    // [핵심] 관리자이고(canReply), 아직 답변이 안 달린 글이면 자동으로 기본 서식 채우기
    if (canReply.value && !item.answerStatus) {
      replyText.value = replyHeader + replyFooter;
    } else {
      // 그 외(일반 유저거나 이미 답변이 있는 경우)는 빈칸으로 시작
      replyText.value = '';
    }
  }
};

// ============================= 글쓰기 및 파일 업로드 =============================
// 모달 열 때 (초기화)
const openWriteModal = () => {
  isEditing.value = false;
  writeForm.value = { title: '', content: '', uploadImg: '' };
  removeFile(); // 모달 열 때 기존에 선택했던 파일 정보 싹 지우기
  showModal.value = true;
};

// 수정 모달 열 때
const openEditModal = (item) => {
  isEditing.value = true;
  // [정답] 카멜케이스 매칭
  writeForm.value = { 
    vocId: item.vocId, 
    title: item.title, 
    content: item.content,
    uploadImg: item.uploadImg
  };
  // 추가
  if (item.uploadImg) {
    previewUrl.value = getImageUrl(item.uploadImg); // 기존 파일 주소를 미리보기에 연결
    isImageFile.value = isImage(item.uploadImg); // 이미지 여부 체크
    selectedFile.value = null; // 아직 새로 고른 파일은 없다는 뜻
  } else {
    removeFile(); // 원래 파일이 없었다면 깔끔하게 초기화
  }
  showModal.value = true;
};

// 사용자가 파일을 선택했을 때 실행되는 함수
const handleFileChange = (e) => {
  const file = e.target.files[0]; // 선택한 파일 중 첫 번째 것 가져오기
  if (!file) return; // 취소 눌러서 파일이 없으면 그냥 종료

  selectedFile.value = file; // 파일 변수에 저장

  // 파일 타입이 'image/...' 로 시작하면 이미지임
  if (file.type.startsWith('image/')) {
    isImageFile.value = true;
    // URL.createObjectURL: 브라우저가 이 파일을 임시 주소로 만들어줌 (미리보기에 사용)
    previewUrl.value = URL.createObjectURL(file);
  } else {
    isImageFile.value = false;
    previewUrl.value = 'doc'; 
  }
};

// 선택한 파일 취소(X버튼) 함수
const removeFile = () => {
  selectedFile.value = null;
  previewUrl.value = null;
  isImageFile.value = false;
  writeForm.value.uploadImg = '';
  if (fileInput.value) fileInput.value.value = '';
};

const submitWrite = async () => {
  if (!writeForm.value.title) return alert("제목을 입력해주세요.");
  if (!writeForm.value.content) return alert("내용을 입력해주세요.");

  try {
    const memId = Number(loginInfo.value.memId || loginInfo.value.id || 0);
    const formData = new FormData();
    
    // 글자 정보 담기 (append: 박스에 넣기)
    formData.append('title', writeForm.value.title);
    formData.append('content', writeForm.value.content);
    formData.append('memId', memId);

    if (isEditing.value) {
      formData.append('vocId', writeForm.value.vocId);

      // 기존 파일 경로도 같이 보냄(새 파일 없으면 이걸 유지)
      formData.append('uploadImg', writeForm.value.uploadImg || '');
    } else {
      // 신규일 때는 초기값 설정
      formData.append('del', 0);
      formData.append('answerStatus', false);
    }

    // 파일이 있으면 박스에 담기
    if (selectedFile.value) {
      formData.append('uploadFile', selectedFile.value);
    }

    // API 호출 (택배 박스째로 보냄)
    let res;
    if (isEditing.value) res = await editVocReq(formData);
    else res = await addVocReq(formData);

    if (res.data === 'success' || res.data === true) {
      alert("완료되었습니다.");
      showModal.value = false;
      fetchList();
    }
  } catch (e) { alert("저장에 실패했습니다."); }
};

const deleteVocUser = async (id) => {
  if (confirm("정말 삭제하시겠습니까?")) {
    await delVocReq(id);
    alert("삭제되었습니다.");
    fetchList();
  }
};

// ============================= 관리자 기능 =============================
const submitReply = async (item) => {
  if (!replyText.value) return alert("답변 내용을 입력하세요.");
  const memId = Number(loginInfo.value.memId || loginInfo.value.id);
  try {
    // [정답] 카멜케이스 vocId
    const payload = { vocId: Number(item.vocId), answerContent: replyText.value, answerStatus: true, del: 0, memId };
    await addVocReplyReq(payload);
    alert("답변이 등록되었습니다.");
    fetchList();
  } catch (e) { alert("등록 실패"); }
};
const startEditReply = (item) => { replyText.value = item.answerContent; editingReplyId.value = item.vocId; };
const cancelEditReply = () => { editingReplyId.value = null; replyText.value = ''; };

const updateReply = async (item) => {
  if (!replyText.value) return alert("내용을 입력하세요.");
  try {
    const payload = { vocId: Number(item.vocId), answerContent: replyText.value, answerStatus: true, memId: Number(loginInfo.value.memId || loginInfo.value.id) };
    await editVocReplyReq(payload);
    alert("수정되었습니다.");
    fetchList();
    editingReplyId.value = null;
  } catch (e) { alert("오류가 발생했습니다."); }
};
const deleteReply = async (item) => {
  if (!confirm("답변을 삭제하시겠습니까?"))
    return;
  try {
    await delVocReplyReq(item.vocId);
    alert("삭제되었습니다.");
    fetchList();
  } catch (e) {
  }
};

const deleteVocAdmin = async (id) => {
  if (confirm("보관함(휴지통)으로 이동하시겠습니까?")) {
    await delVocByAdminReq(id); fetchList();
  }
};

const restoreVoc = async (id) => {
  if (confirm("글을 복구하시겠습니까?")) {
    await restoreVocReq(id);
    fetchList();
  }
};

// ============================= 이미지 모달 및 onmounted =============================
// 이미지 클릭 시 실행 (확대 창 열기)
const openImgModal = (path) => {
  // getImageUrl 함수를 통해 전체 주소로 변환해서 저장
  currentImgUrl.value = getImageUrl(path);
  showImgModal.value = true;
};

// 확대 창 닫기
const closeImgModal = () => {
  showImgModal.value = false;
  currentImgUrl.value = '';
};

onMounted(async () => {
  const raw = sessionStorage.getItem('loginId');
  if (raw) loginInfo.value = JSON.parse(raw);
  if (isAdmin.value) {
    try { const res = await getAdminInfoReq(); if (res?.data?.isWonmu) isWonmuState.value = true; } catch (e) { }
  }
  fetchList();
});
</script>

<style scoped>
.voc-container {
  font-family: 'Pretendard', -apple-system, sans-serif !important;
  max-width: 900px;
  margin: 60px auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 50px;
}

.page-header h2 {
  font-size: 42px;
  font-weight: 800;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 18px;
}

.list-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
  border-bottom: 1px solid #333;
  padding-bottom: 15px;
}

.status-tabs {
  display: flex;
  gap: 10px;
}

.tab-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  transition: 0.2s;
}

.tab-btn.active {
  background: #404347;
  color: #fff;
  border-color: #404347;
}

.btn-write {
  background: #005baa;
  color: #fff;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  font-weight: 600;
  font-size: 16px;
}

.btn-write:hover {
  background: #005baa;
}

.voc-list {
  border-top: none;
}

.voc-item {
  background: #fff;
  border-bottom: 1px solid #eee;
}

.voc-item.is-open {
  background: #f9f9f9;
}

.voc-header {
  display: flex;
  justify-content: space-between;
  padding: 25px 15px;
  cursor: pointer;
  align-items: center;
  transition: background 0.1s;
}

.voc-header:hover {
  background: #fcfcfc;
}

.vh-left {
  display: flex;
  align-items: center;
  gap: 18px;
  flex: 1;
}

.status-badge {
  padding: 6px 0;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  width: 80px;
  display: inline-block;
  text-align: center;
}

.badge-wait {
  background: #999;
}

.badge-done {
  background: #005baa;
}

.badge-del {
  background: #e03131;
}

.vh-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.vh-right {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #888;
  align-items: center;
}

.vh-writer {
  font-size: 16px;
  font-weight: 500;
  color: #555;
  min-width: 60px;
  text-align: center;
}

.vh-date {
  font-family: 'Roboto', sans-serif;
  font-size: 16px;
}

.vh-arrow {
  display: flex;
  align-items: center;
}

.voc-body {
  padding: 30px 40px;
  background-color: #f9fbff;
  border-top: 1px solid #eee;
}

.q-section,
.a-section {
  display: flex;
  gap: 20px;
}

.q-section {
  margin-bottom: 40px;
}

.type-label {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 18px;
  flex-shrink: 0;
  border: 1px solid #ddd;
  background: #fff;
}

.q-label {
  color: #333;
}

.a-label {
  color: #005baa;
  border-color: #005baa;
}

.a-label.waiting {
  color: #bbb;
  border-color: #ddd;
}

.content-box {
  flex: 1;
}

.content-text {
  white-space: pre-wrap;
  font-size: 15px;
  color: #444;
  line-height: 1.6;
  font-family: 'Noto Sans KR', sans-serif;
  margin: 0 0 15px 0;
}

.action-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-row button {
  background: none;
  border: none;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  padding: 0;
}

.action-row button:hover {
  text-decoration: underline;
}

.action-row .bar {
  color: #ddd;
  font-size: 12px;
}

.btn-del {
  color: #e03131 !important;
}

.btn-restore {
  color: #2b8a3e !important;
}

.answer-wrapper {
  flex: 1;
}

.answer-view-box {
  background: #fff;
  border: 1px solid #e1e1e1;
  padding: 25px;
}

.ans-meta {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.admin-name {
  font-weight: 700;
  color: #005baa;
  font-size: 14px;
}

.ans-date {
  font-size: 13px;
  color: #888;
}

.ans-content pre {
  white-space: pre-wrap;
  font-size: 15px;
  color: #333;
  line-height: 1.6;
  font-family: inherit;
}

.ans-actions {
  margin-top: 15px;
  text-align: right;
  font-size: 13px;
}

.ans-actions button {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
}

.reply-write-box {
  background: #fff;
  border: 1px solid #ddd;
  padding: 20px;
}

.write-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.guide-txt {
  font-weight: 700;
  color: #333;
  font-size: 14px;
}

.btn-template {
  background: #fff;
  border: 1px solid #ccc;
  color: #555;
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
  transition: 0.2s;
}

.btn-template:hover {
  border-color: #005baa;
  color: #005baa;
}

.common-textarea {
  width: 100%;
  height: 120px;
  padding: 10px;
  border: 1px solid #ddd;
  resize: vertical;
  font-family: inherit;
  font-size: 14px;
}

.common-textarea:focus {
  outline: none;
  border-color: #333;
}

.write-footer {
  text-align: right;
  margin-top: 10px;
}

.btn-submit {
  background: #333;
  color: #fff;
  border: none;
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.waiting-box {
  background: #fff;
  border: 1px dashed #ccc;
  padding: 30px;
  text-align: center;
  color: #888;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-card {
  background: #fff;
  width: 600px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-head {
  padding: 20px 25px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-head h3 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
}

.modal-body {
  padding: 25px;
}

.req {
  color: #dc3545; 
  margin-left: 2px;
}

.content-text, .ans-content pre, .modal-input, .modal-textarea {
  font-family: inherit; 
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.modal-input,
.modal-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  font-size: 14px;
  font-family: inherit;
}

.modal-textarea {
  height: 200px;
  resize: none;
}

.modal-foot {
  padding: 15px 25px;
  border-top: 1px solid #eee;
  text-align: right;
  background: #f9f9f9;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #ddd;
  color: #555;
  padding: 10px 20px;
  cursor: pointer;
  margin-right: 8px;
  font-weight: 600;
}

.btn-confirm {
  background: #005baa;
  border: none;
  color: #fff;
  padding: 10px 24px;
  cursor: pointer;
  font-weight: 600;
}

.no-data {
  text-align: center;
  padding: 100px;
  color: #999;
  border-bottom: 1px solid #eee;
}

.file-upload-box {
  border: 1px dashed #ddd;
  padding: 15px;
  background: #fdfdfd;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #eee;
  background: #fff;
  width: fit-content;
}

.preview-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.file-info-tag {
  font-size: 13px;
  color: #555;
  background: #eee;
  padding: 5px 10px;
  border-radius: 4px;
}

.btn-remove-file {
  background: #999;
  color: #fff;
  border: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-download-area {
  margin-top: 15px;
  margin-bottom: 15px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
  border-left: 3px solid #005baa;
}

.file-label {
  font-weight: 600;
  margin-right: 10px;
  font-size: 13px;
  color: #555;
}

.file-link {
  color: #005baa;
  text-decoration: underline;
  font-size: 14px;
  cursor: pointer;
}

.img-preview-box {
  margin-top: 15px;
}

.attached-img {
  max-width: 30%;
  max-height: 400px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.clip-icon {
  font-size: 14px;
  margin-left: 5px;
}

/*  이미지 확대 모달 스타일 */
.img-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.img-modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.img-modal-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
}

.btn-close-img {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: #fff;
  font-size: 40px;
  cursor: pointer;
}
</style>