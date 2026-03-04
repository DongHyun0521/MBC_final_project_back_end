<template>
  <div class="faq-container">
    <div class="faq-header">
      <h2>자주 묻는 질문</h2>
      <p>궁금하신 내용을 검색하거나 카테고리를 선택해 주세요.</p>
    </div>

    <div class="category-tabs">
      <button v-for="cat in categories" :key="cat" :class="['tab-btn', { active: currentCategory === cat }]"
        @click="changeCategory(cat)">
        {{ cat }}
      </button>
    </div>

    <div class="search-box">
      <input type="text" v-model="keyword" placeholder="검색어를 입력해 주세요" @keyup.enter="getFaqList">
      <button class="btn-search" @click="getFaqList">
        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
      </button>
    </div>

    <!-- 원무팀만 FAQ 등록 가능 -->
    <div class="admin-action-bar" v-if="canManageFaq">
      <button class="btn-write" @click="openWriteModal">FAQ 등록</button>
    </div>

    <div class="faq-list">
      <div v-if="filteredList.length === 0" class="no-result">
        {{ keyword ? '검색 결과가 없습니다.' : '등록된 질문이 없습니다.' }}
      </div>

      <div v-for="item in displayedList" :key="item.faqId" class="faq-item" :class="{ open: item.isOpen }">
        <div class="faq-question" @click="toggleItem(item.faqId)">
          <div class="q-left">
            <span class="q-badge">Q</span>

            <select v-if="item.isEditing" v-model="editForm.category" @click.stop class="edit-select">
              <option v-for="cat in categories.filter(c => c !== '전체')" :key="cat" :value="cat">
                {{ cat }}
              </option>
            </select>
            <span v-else class="q-cat">[{{ item.category }}]</span>

            <input v-if="item.isEditing" v-model="editForm.title" @click.stop class="edit-input" placeholder="질문 입력">
            <span v-else class="q-text">{{ item.title }}</span>
          </div>
          <span class="toggle-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </span>
        </div>

        <div class="faq-answer-wrap" :class="{ active: item.isOpen }">
          <div class="faq-answer">
            <span class="a-badge">A</span>
            <div class="a-content-box">
              <textarea v-if="item.isEditing" v-model="editForm.content" class="edit-textarea" placeholder="내용 입력"></textarea>
              
              <div v-else class="a-text" @click="handleLinkClick">
                <div v-html="formatContent(item.content)"></div>
              </div>
              <div class="admin-btns" v-if="canManageFaq"></div>

              <!-- 원무팀만 수정/삭제 가능 -->
              <div class="admin-btns" v-if="canManageFaq">
                <template v-if="!item.isEditing">
                  <button @click.stop="startEdit(item)">수정</button>
                  <button @click.stop="deleteFaq(item.faqId)">삭제</button>
                </template>
                <template v-else>
                  <button @click.stop="saveEdit(item)" class="btn-save">저장</button>
                  <button @click.stop="cancelEdit(item)">취소</button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    

    <div class="load-more-area" v-if="visibleCount < filteredList.length">
      <button class="btn-more" @click="loadMore">
        더 보기
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="6 9 12 15 18 9"></polyline>
        </svg>
      </button>
    </div>

    <div v-if="showWriteModal" class="modal-overlay">
      <div class="modal-content">
        <h3>FAQ 등록</h3>
        <div class="modal-body">
          <select v-model="writeForm.category" class="modal-select">
            <option value="" disabled>카테고리 선택</option>
            <option v-for="cat in categories.filter(c => c !== '전체')" :key="cat" :value="cat">{{ cat }}</option>
          </select>
          <input v-model="writeForm.title" placeholder="질문을 입력하세요" class="modal-input">
          <textarea v-model="writeForm.content" placeholder="답변을 입력하세요" class="modal-textarea"></textarea>
        </div>
        <div class="modal-footer">
          <button @click="submitWrite" class="btn-confirm">등록</button>
          <button @click="closeWriteModal" class="btn-cancel">취소</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// API 파일 가져오기 (FAQ 목록, 등록, 수정, 삭제, 관리자 확인)
import { 
  getFaqsReq, addFaqReq, editFaqReq, delFaqReq, getAdminInfoReq 
} from '@/api/customer';

const router = useRouter();

// 1. 기본 설정 및 상태 변수 (Data)
// 카테고리 및 검색
const currentCategory = ref('전체'); // 현재 선택된 탭
const keyword = ref(''); // 검색어
const categories = ['전체', '병원이용', '증명서발급', '건강검진', '홈페이지 이용'];

// 데이터 저장소
const loginInfo = ref({}); // 로그인 사용자 정보
const faqList = ref([]);   // FAQ 목록 데이터
const showWriteModal = ref(false); // 글쓰기 모달창 표시 여부

// 더보기 기능 변수
const visibleCount = ref(10); // 처음에 보여줄 개수
const increment = 5;          // 더보기 누르면 늘어날 개수

// 폼 데이터 (작성용 / 수정용)
const writeForm = ref({ category: '', title: '', content: '' });
const editForm = ref({ faqId: '', category: '', title: '', content: '' });

// 권한 확인용 변수
const isWonmuState = ref(false); // API로 확인된 원무과 여부



// [추가] 링크 처리용 함수들
// 1. 줄바꿈(\n)을 <br> 태그로 바꾸고 HTML 허용하기
const formatContent = (text) => {
  if (!text) return '';
  // 엔터키(줄바꿈)를 <br>로 바꿔서 줄바꿈이 되게 함
  return text.replace(/\n/g, '<br>');
};

// 2. 내용 안에 있는 링크 클릭 시, 새로고침 막고 라우터로 이동하기
const handleLinkClick = (e) => {
  // 클릭한 요소가 <a> 태그(링크)인지 확인
  const target = e.target.closest('a');
  if (target) {
    const href = target.getAttribute('href');
    // 내부 링크('/'로 시작)라면 SPA 방식으로 부드럽게 이동
    if (href && href.startsWith('/')) {
      e.preventDefault(); // 새로고침 방지
      router.push(href);  // 페이지 이동
    }
  }
};


// 2. Computed (권한 및 필터링)
// [권한 1] 관리자(ADMIN)인지 확인
const isAdmin = computed(() => {
  const info = loginInfo.value || {};
  return String(info.loginType || info.role || '').toUpperCase() === 'ADMIN';
});

// [권한 2] 원무팀 직원인지 확인
const isWonmuTeam = computed(() => {
  // 1. API 확인 값 우선 사용
  if (isWonmuState.value) return true; 

  // 2. 로그인 세션 정보로 확인 (보조 수단)
  const info = loginInfo.value || {};
  const dept = String(
  info.admin_dept_name || info.dept_name || ''
).trim();
  return dept.includes('원무');
});

// [권한 3] FAQ 관리 권한 최종 확인 (관리자 + 원무팀)
const canManageFaq = computed(() => isAdmin.value && isWonmuTeam.value);

// [필터링 1] 검색어로 목록 거르기
const filteredList = computed(() => {
  if (!keyword.value) return faqList.value;
  return faqList.value.filter(item =>
    item.title.includes(keyword.value) || item.content.includes(keyword.value)
  );
});

// [필터링 2] '더보기' 기능 적용해서 화면에 보여줄 개수만큼 자르기
const displayedList = computed(() => {
  return filteredList.value.slice(0, visibleCount.value);
});


// 3. 유틸리티 및 가드 함수

// 더보기 버튼 클릭 시 실행
const loadMore = () => {
  visibleCount.value += increment;
};

// 카테고리 탭 변경 시 실행
const changeCategory = (cat) => {
  currentCategory.value = cat;
  getFaqList(); // 해당 카테고리 목록 다시 불러오기
};

// 원무팀 권한 가드 (작성/수정/삭제 전 체크)
const guardWonmu = () => {
  if (!canManageFaq.value) {
    alert("FAQ 등록/수정/삭제는 원무팀만 가능합니다.");
    return false;
  }
  return true;
};

// 아코디언 토글 (질문 클릭 시 답변 열기/닫기)
const toggleItem = (id) => {
  const target = faqList.value.find(item => item.faqId === id);
  // 수정 중이 아닐 때만 열고 닫음 (수정 중엔 닫히면 안 되니까)
  if (target && !target.isEditing) {
    target.isOpen = !target.isOpen;
  }
};


// 4. 데이터 조회 함수

// FAQ 목록 불러오기
const getFaqList = async () => {
  visibleCount.value = increment; // 개수 초기화
  try {
    const catParam = currentCategory.value === '전체' ? null : currentCategory.value;
    const res = await getFaqsReq(catParam);

    // 받아온 데이터에 화면 제어용 변수(isOpen, isEditing) 추가해서 저장
    faqList.value = (res.data || []).map(item => ({
      ...item,
      isOpen: false,    // 닫힌 상태로 시작
      isEditing: false  // 보기 모드로 시작
    }));
  } catch (err) {
    console.error("FAQ 로딩 실패", err);
    faqList.value = [];
  }
};


// 5. 관리자 기능 (등록)

// 등록 모달 열기
const openWriteModal = () => {
  if (!guardWonmu()) return;
  writeForm.value = { category: '', title: '', content: '' };
  showWriteModal.value = true;
};

// 등록 모달 닫기
const closeWriteModal = () => showWriteModal.value = false;

// 새 글 등록 완료
const submitWrite = async () => {
  if (!guardWonmu()) return;

  if (!writeForm.value.category || !writeForm.value.title || !writeForm.value.content) {
    alert("모든 항목을 입력해 주세요."); return;
  }

  try {
    const memIdRaw = loginInfo.value.memId ?? loginInfo.value.mem_id;
    const memId = Number(memIdRaw);
    if (!memId || Number.isNaN(memId)) {
      alert("로그인 정보(memId)가 없습니다."); return;
    }

    const payload = { ...writeForm.value, memId };

    const res = await addFaqReq(payload);
    if (res.data === 'success' || res.data === true) {
      alert("정상적으로 등록되었습니다.");
      closeWriteModal();
      getFaqList(); // 목록 새로고침
    } else {
      alert("등록 실패");
    }
  } catch (e) {
    alert("오류 발생");
  }
};


// 6. 관리자 기능 (수정, 삭제)

// 수정 모드 시작 (인라인 에디트)
const startEdit = (item) => {
  if (!guardWonmu()) return;

  // 다른 글 수정 중이면 다 끄기 (한 번에 하나만 수정)
  faqList.value.forEach(i => i.isEditing = false);

  // 현재 글 내용을 수정 폼에 복사
  editForm.value = {
    faqId: item.faqId,
    category: item.category,
    title: item.title,
    content: item.content
  };
  
  item.isEditing = true; // 수정 모드 켜기
  item.isOpen = true;    // 내용 보이게 열기
};

// 수정 취소
const cancelEdit = (item) => {
  if (!guardWonmu()) return;
  item.isEditing = false; // 그냥 닫기
};

// 수정 내용 저장
const saveEdit = async (item) => {
  if (!guardWonmu()) return;

  try {
    const memIdRaw = loginInfo.value.memId ?? loginInfo.value.mem_id;
    const memId = Number(memIdRaw);
    if (!memId || Number.isNaN(memId)) {
      alert("로그인 정보(memId)가 없습니다."); return;
    }

    const sendData = {
      faqId: item.faqId,
      category: editForm.value.category,
      title: editForm.value.title,
      content: editForm.value.content,
      memId
    };

    const res = await editFaqReq(sendData);
    if (res.data === 'success' || res.data === true) {
      alert("정상적으로 수정되었습니다.");
      item.isEditing = false;

      // 목록 다시 불러오지 않고, 화면 데이터만 싹 바꿔치기 (속도 향상)
      item.category = editForm.value.category;
      item.title = editForm.value.title;
      item.content = editForm.value.content;
    } else {
      alert("수정 실패");
    }
  } catch (e) {
    alert("오류 발생");
  }
};

// 글 삭제
const deleteFaq = async (id) => {
  if (!guardWonmu()) return;

  if (!confirm("정말 삭제하시겠습니까?")) return;
  try {
    const res = await delFaqReq(id);
    if (res.data === 'success' || res.data === true) {
      alert("정상적으로 삭제되었습니다.");
      getFaqList(); // 목록 새로고침
    } else {
      alert("삭제 실패");
    }
  } catch (e) {
    alert("오류 발생");
  }
};


// 7. 페이지 시작 (Life Cycle)
onMounted(async () => {
  // 1. 로그인 정보 확인
  const raw = sessionStorage.getItem('loginId');
  if (raw) {
    try { loginInfo.value = JSON.parse(raw); }
    catch (e) { loginInfo.value = { id: raw }; }
  }

  // 2. 관리자라면 원무과 권한 한 번 더 체크 (보안)
  if (isAdmin.value) {
    try {
        const res = await getAdminInfoReq();
        if (res && res.data && res.data.isWonmu) {
            isWonmuState.value = true;
        }
    } catch (e) {
        console.error("관리자 정보 확인 실패", e);
    }
  }

  // 3. 목록 불러오기
  getFaqList();
});
</script>

<style scoped>
.faq-container {
  font-family: 'Pretendard', -apple-system, sans-serif !important;
  max-width: 1000px;
  margin: 80px auto;
  padding: 0 20px;
  font-family: 'Noto Sans KR', sans-serif;
}

.faq-header {
  text-align: center;
  margin-bottom: 50px;
}

.faq-header h2 {
  font-size: 42px;
  font-weight: 800;
  color: #333;
  margin-bottom: 15px;
}

.faq-header p {
  color: #666;
  font-size: 18px;
}

.category-tabs {
  display: flex;
  justify-content: center;
  gap: -1px;
  margin-bottom: 30px;
}

.tab-btn {
  width: 180px;
  padding: 15px 0;
  background: #fff;
  border: 1px solid #ddd;
  font-size: 18px;
  font-weight: 600;
  color: #555;
  cursor: pointer;
  transition: 0.2s;
  margin-right: -1px;
}

.tab-btn:hover {
  background: #f8f9fa;
  color: #005baa;
  z-index: 1;
  border-color: #005baa;
}

.tab-btn.active {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
  font-weight: 600;
  z-index: 2;
}

.search-box {
  display: flex;
  justify-content: center;
  gap: 0;
  margin-bottom: 50px;
}

.search-box input {
  width: 500px;
  padding: 15px 20px;
  border: 1px solid #ddd;
  border-right: none;
  font-size: 15px;
  outline: none;
  transition: 0.2s;
}

.search-box input:focus {
  border-color: #005baa;
}

.btn-search {
  width: 60px;
  background: #005baa;
  color: #fff;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.3s;
}

.btn-search:hover {
  background: #005baa;
}

.admin-action-bar {
  text-align: right;
  margin-bottom: 20px;
}

.btn-write {
  padding: 10px 20px;
  background: #333;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.faq-list {
  border-top: 2px solid #333;
  border-bottom: 1px solid #333;
}

.no-result {
  padding: 80px;
  text-align: center;
  color: #999;
  border-bottom: 1px solid #eee;
}

.faq-item {
  border-bottom: 1px solid #e5e5e5;
  background: #fff;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  cursor: pointer;
  transition: 0.2s;
}

.faq-question:hover {
  background-color: #fbfbfb;
}

.q-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.q-badge {
  font-size: 22px;
  font-weight: 800;
  color: #005baa;
}

.q-cat {
  font-size: 18px;
  color: #666;
  font-weight: 500;
  min-width: 80px;
}

.q-text {
  font-size: 20px;
  color: #333;
  font-weight: 500;
  line-height: 1.4;
}

.toggle-icon {
  color: #ccc;
  transition: transform 0.3s ease;
}

.faq-item.open .toggle-icon {
  transform: rotate(180deg);
  color: #005baa;
}

.faq-answer-wrap {
  max-height: 0;
  overflow: hidden;
  opacity: 0;
  background-color: #f8f9fa;
  transition: all 0.2s ease-in;
}

.faq-answer-wrap.active {
  max-height: 1000px;
  opacity: 1;
  transition: all 0.5s ease-out;
}

.faq-answer {
  padding: 30px 40px;
  display: flex;
  gap: 20px;
  border-top: 1px solid #f0f0f0;
}

.a-badge {
  font-size: 22px;
  font-weight: 800;
  color: #333;
  margin-left: -8px;
}

.a-content-box {
  flex: 1;
}

.a-text {
  font-size: 18px;
  color: #555;
  line-height: 1.7;
  white-space: pre-wrap;
}

.a-text pre {
  font-family: 'Noto Sans KR', sans-serif;
  white-space: pre-wrap;
  margin: 0;
}

.admin-btns {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.admin-btns button {
  padding: 5px 12px;
  font-size: 13px;
  border: 1px solid #ccc;
  background: #fff;
  cursor: pointer;
  color: #666;
  border-radius: 4px;
}

.admin-btns button:hover {
  background: #eee;
}

.admin-btns .btn-save {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

.load-more-area {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.btn-more {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 40px;
  background: #fff;
  border: 1px solid #ddd;
  color: #555;
  font-size: 15px;
  cursor: pointer;
  border-radius: 30px;
  transition: 0.2s;
}

.btn-more:hover {
  background: #f9f9f9;
  border-color: #ccc;
  color: #333;
}

.edit-input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  font-size: 16px;
  border-radius: 4px;
}

.edit-textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #ddd;
  font-size: 15px;
  border-radius: 4px;
  resize: none;
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
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 30px;
  width: 500px;
  border-radius: 8px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 700;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.modal-input,
.modal-select,
.modal-textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
}

.modal-textarea {
  height: 150px;
  resize: none;
}

.modal-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-confirm {
  background: #005baa;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel {
  background: #eee;
  color: #333;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

/* 인라인 수정 카테고리 셀렉트 */
.edit-select {
  padding: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  border-radius: 4px;
  margin-right: 10px;
  cursor: pointer;
  height: 36px;
  vertical-align: middle;
}

/* [추가] 답변 내용 안에 있는 링크 스타일 */
/* v-html로 들어간 내용은 ::v-deep을 써야 먹힙니다 */
.a-text :deep(a) {
  color: #005baa;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 600;
}

.a-text :deep(a):hover {
  color: #005baa;
}
</style>
