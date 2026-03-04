<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>건강이야기</h2>
      <p>서울에스병원 전문의가 들려주는 생생한 건강 정보</p>
    </div>

    <div v-if="mode === 'list'" class="list-wrap">
      <div class="top-controls">
        <div class="video-search">
          <input type="text" v-model="keyword" placeholder="검색어를 입력하세요" @keyup.enter="fetchList">
          <button class="s-btn" @click="fetchList">검색</button>
        </div>
        <button v-if="canManageStory" class="btn-write" @click="goWrite">글쓰기</button>
      </div>

      <div v-if="storyList.length > 0" class="video-grid">
        <div class="video-card" v-for="item in paginatedList" :key="item.healthStoryId" @click="goDetail(item)">
          <div class="thumb-box">
            <img v-if="item.fileUrl && isImage(item.fileName)" :src="item.fileUrl" class="thumb-img" alt="썸네일">
            <video v-else-if="item.fileUrl && isVideo(item.fileName)" :src="item.fileUrl" class="thumb-img"
              muted></video>
            <div v-else class="placeholder-thumb" style="background-color: #f1f1f1;">
               <span class="play-icon">✚</span>
            </div>
          </div>

          <div class="video-info">
            <p class="v-title">{{ item.title }}</p>
            <p class="v-author">{{ item.writerName || '서울에스병원' }}</p>
          </div>
        </div>
      </div>

      <div v-else class="no-data">등록된 건강정보 컨텐츠가 없습니다.</div>

      <div class="pagination-area" v-if="storyList.length > 0">
        <button class="page-btn prev" :disabled="currentPage === 1" @click="currentPage--">&lt;</button>
        <button v-for="page in visiblePages" :key="page" class="page-btn number"
          :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
        <button class="page-btn next" :disabled="currentPage === totalPages" @click="currentPage++">&gt;</button>
      </div>
    </div>

    <div v-else-if="mode === 'detail'" class="detail-wrap">
      <div class="detail-header">
        <div class="dh-title">{{ selectedItem.title }}</div>
        <div class="dh-info">
          <span><strong>등록일</strong> | {{ formatDate(selectedItem.writeDate) }}</span>
          <span class="bar">|</span>
          <span><strong>작성자</strong> | {{ selectedItem.writerName }}</span>
          <span class="bar">|</span>
          <span><strong>조회수</strong> | {{ selectedItem.readCount }}</span>
        </div>
      </div>

      <div class="detail-content">
        <div class="view-text" @click="handleLinkClick" v-html="formatContent(selectedItem.content)"></div>

        <div v-if="selectedItem.fileUrl" class="media-view">
          <img v-if="isImage(selectedItem.fileName)" :src="selectedItem.fileUrl" class="detail-media">
          <video v-else-if="isVideo(selectedItem.fileName)" :src="selectedItem.fileUrl" controls
            class="detail-media"></video>
        </div>
      </div>

      <div class="file-attach-area">
        <span class="fa-label">첨부파일</span>
        <div class="fa-content">
          <a v-if="selectedItem.fileName" :href="selectedItem.fileUrl" download class="file-link">{{ selectedItem.fileName }}</a>
          <span v-else class="no-file">첨부된 파일이 없습니다.</span>
        </div>
      </div>

      <div class="btn-group">
        <button class="btn-list" @click="goList">목록</button>
        <div class="admin-btns" v-if="canManageStory">
          <button class="btn-mod" @click="goEdit(selectedItem)">수정</button>
          <button class="btn-del" @click="deleteItem(selectedItem.healthStoryId)">삭제</button>
        </div>
      </div>
    </div>

    <div v-else-if="mode === 'write' || mode === 'edit'" class="write-wrap">
      <h3 class="write-title">{{ mode === 'write' ? '컨텐츠 등록' : '컨텐츠 수정' }}</h3>
      <div class="write-form">
        <div class="form-row">
          <label>제목</label>
          <div class="input-wrap">
            <input type="text" v-model="writeForm.title" placeholder="제목을 입력하세요" class="full-input">
          </div>
        </div>

        <div class="form-row">
          <label>파일/이미지</label>
          <div class="input-wrap file-wrap">
            <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*, video/*">
            <div v-if="previewUrl" class="preview-box">
              <img v-if="isImageFile" :src="previewUrl" class="mini-img">
              <video v-else-if="isVideoFile" :src="previewUrl" class="mini-img"></video>
              <span v-else class="file-name-tag">📄 {{ selectedFile.name }}</span>
              <button class="btn-x" @click="removeFile">X</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <label>내용</label>
          <div class="input-wrap">
            <textarea v-model="writeForm.content" placeholder="내용을 입력하세요" class="full-textarea"></textarea>
          </div>
        </div>
      </div>
      <div class="btn-group center">
        <button class="btn-cancel" @click="goList">취소</button>
        <button class="btn-save" @click="submitStory">{{ mode === 'write' ? '등록' : '수정' }}</button>
      </div>
    </div>

  </div>
</template>

<script setup>
// [중요] watch를 꼭 import 해야 뒤로가기 됨
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';

// [API]
import {
  getStoriesReq, getStoryDetailReq,
  addStoryReq, editStoryReq, delStoryReq,
  getAdminInfoReq
} from '@/api/health';

const router = useRouter();
const route = useRoute();

const mode = ref('list');
const keyword = ref('');
const loginInfo = ref({});
const selectedItem = ref({});
const currentPage = ref(1);
const itemsPerPage = 9;
const maxPageBtn = 5;
const storyList = ref([]);
const writeForm = ref({ healthStoryId: '', title: '', content: '' });
const isPrTeamState = ref(false);

// 파일 관련
const fileInput = ref(null);
const selectedFile = ref(null);
const previewUrl = ref(null);
const isImageFile = ref(false);
const isVideoFile = ref(false);

// 2. 권한 체크
const isAdmin = computed(() => String(loginInfo.value.loginType || loginInfo.value.role || '').toUpperCase() === 'ADMIN');
// [홍보팀 확인]
const isPrTeam = computed(() => {
  if (!isAdmin.value) return false;
  if (isPrTeamState.value === true) return true;
  const info = loginInfo.value || {};
  return info.isPr === true;
});

const canManageStory = computed(() => {
  return isPrTeam.value;
});


// ==========================================
// 3. 리스트 데이터 가공 (Seq 부여)
// ==========================================
const paginatedList = computed(() => {
  let list = storyList.value;

  if (keyword.value) {
    list = list.filter(item => item.title.includes(keyword.value));
  }

  // 최신순 (ID 내림차순)
  list.sort((a, b) => b.healthStoryId - a.healthStoryId);

  // 번호 부여를 위한 전체 개수
  const totalCount = list.length;

  const start = (currentPage.value - 1) * itemsPerPage;
  const sliced = list.slice(start, start + itemsPerPage);

  // 화면에 보여줄 가짜 번호(seq) 계산해서 넣기
  return sliced.map((item, index) => ({
    ...item,
    seq: totalCount - start - index
  }));
});

const totalPages = computed(() => {
  const len = keyword.value ? storyList.value.filter(i => i.title.includes(keyword.value)).length : storyList.value.length;
  return Math.ceil(len / itemsPerPage) || 1;
});

const visiblePages = computed(() => {
  const currentGroup = Math.ceil(currentPage.value / maxPageBtn);
  const start = (currentGroup - 1) * maxPageBtn + 1;
  let end = start + maxPageBtn - 1;
  if (end > totalPages.value) end = totalPages.value;
  const pages = [];
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});


// ==========================================
// 4. 유틸리티 함수
// ==========================================
const formatDate = (d) => d ? String(d).substring(0, 10) : '';

const getImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

const isImage = (fileName) => {
  if (!fileName) return false;
  const ext = fileName.split('.').pop().toLowerCase();
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext);
};

const isVideo = (fileName) => {
  if (!fileName) return false;
  const ext = fileName.split('.').pop().toLowerCase();
  return ['mp4', 'webm', 'ogg', 'avi', 'mov'].includes(ext);
};

const formatContent = (text) => text ? text.replace(/\n/g, '<br>') : '';

const handleLinkClick = (e) => {
  const target = e.target.closest('a');
  if (target) {
    const href = target.getAttribute('href');
    if (href) {
      e.preventDefault();
      if (href.startsWith('http')) window.open(href, '_blank');
      else router.push(href);
    }
  }
};


// ==========================================
// 5. 기능 로직 (상세보기, 글쓰기 등)
// ==========================================
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (!file) return;
  selectedFile.value = file;

  if (file.type.startsWith('image/')) {
    isImageFile.value = true;
    isVideoFile.value = false;
    previewUrl.value = URL.createObjectURL(file);
  } else if (file.type.startsWith('video/')) {
    isImageFile.value = false;
    isVideoFile.value = true;
    previewUrl.value = URL.createObjectURL(file);
  } else {
    isImageFile.value = false;
    isVideoFile.value = false;
    previewUrl.value = 'doc';
  }
};

const removeFile = () => {
  selectedFile.value = null;
  previewUrl.value = null;
  if (fileInput.value) fileInput.value.value = '';
};

// [상세 보기] - 클릭 시 & URL 접속 시 공용
const goDetail = async (item) => {
  try {
    // 1. 주소창 URL 변경 (클릭으로 들어온 경우만)
    // item.healthStoryId가 있으면 URL을 바꿈. 이미 URL이 맞으면(watch 등) 패스
    if (item.healthStoryId && route.params.id != item.healthStoryId) {
        router.push(`/story/${item.healthStoryId}`);
        return; // URL을 바꾸면 어차피 watch가 실행돼서 아래 로직 다시 함
    }

    // 2. 데이터 가져오기 (item은 객체일 수도 있고, 그냥 ID만 있는 껍데기일 수도 있음)
    const targetId = item.healthStoryId || route.params.id;
    if (!targetId) return;

    const res = await getStoryDetailReq(targetId);
    const data = res.data;

    selectedItem.value = {
      healthStoryId: data.healthStoryId,
      title: data.title,
      content: data.content,
      writeDate: data.writeDate,
      readCount: data.readCount,
      writerName: data.writerName || data.adminName || '관리자',
      fileUrl: getImageUrl(data.thumbnailImg),
      fileName: data.thumbnailImg ? data.thumbnailImg.split('/').pop() : '',
      
      // 리스트에서 클릭했을 때만 seq가 있음. URL 접속 시엔 0 처리 (화면에 안 보여주면 상관없음)
      seq: item.seq || 0 
    };

    mode.value = 'detail';
    window.scrollTo(0, 0);
  } catch (e) { 
    console.error("상세보기 실패:", e);
    // 실패하면 리스트로
    mode.value = 'list'; 
  }
};

// 목록으로 가기 (뒤로가기 처리 포함)
const goList = () => { 
    // 주소창을 일단 목록 주소로 바꿈
    router.push('/story'); 

    mode.value = 'list'; 
    
    selectedItem.value = {};
    writeForm.value = { healthStoryId: '', title: '', content: '' };
    removeFile(); 

    fetchList();
};

const goWrite = () => {
  if (!canManageStory.value) return alert("권한이 없습니다.");
  writeForm.value = { title: '', content: '' };
  removeFile();
  mode.value = 'write';
};

// [수정] 수정 모드 진입
const goEdit = (item) => {
  if (!canManageStory.value) return alert("권한이 없습니다.");
  writeForm.value = { ...item };
  
  if (item.fileUrl) {
    previewUrl.value = item.fileUrl;
    selectedFile.value = null; 
    if (isImage(item.fileName)) {
      isImageFile.value = true;
      isVideoFile.value = false;
    } else if (isVideo(item.fileName)) {
      isImageFile.value = false;
      isVideoFile.value = true;
    }
  } else {
    removeFile();
  }
  mode.value = 'edit';
};


// ==========================================
// 6. 서버 통신
// ==========================================
const fetchList = async () => {
  try {
    const res = await getStoriesReq(keyword.value ? { keyword: keyword.value } : null);
    storyList.value = res.data.map(item => ({
      ...item,
      healthStoryId: item.healthStoryId,
      title: item.title,
      writeDate: item.writeDate,
      writerName: item.writerName || item.adminName,
      fileUrl: getImageUrl(item.thumbnailImg),
      fileName: item.thumbnailImg
    })) || [];
  } catch (e) { storyList.value = []; }
};

const submitStory = async () => {
  if (!canManageStory.value) return;
  if (!writeForm.value.title || !writeForm.value.content) return alert("제목과 내용을 입력하세요.");

  try {
    const formData = new FormData();
    formData.append('title', writeForm.value.title);
    formData.append('content', writeForm.value.content);

    if (mode.value === 'edit') {
      formData.append('healthStoryId', writeForm.value.healthStoryId);
    } else {
      formData.append('del', 0);
    }

    if (selectedFile.value) {
      formData.append('uploadFile', selectedFile.value);
    }

    let res = mode.value === 'write' ? await addStoryReq(formData) : await editStoryReq(formData);

    if (res.data === 'success') {
      alert("완료되었습니다.");
      goList();
    } else {
      alert("실패했습니다.");
    }
  } catch (e) { alert("오류 발생"); console.error(e); }
};

const deleteItem = async (id) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;
  try {
    await delStoryReq(id);
    alert("삭제되었습니다.");
    goList();
  } catch (e) { alert("오류 발생"); }
};

watch(() => route.params.id, (newId) => {
    if (newId) {
        // 주소에 ID가 있으면 상세보기 실행
        goDetail({ healthStoryId: newId });
    } else {
        // 주소에 ID가 없으면 리스트 실행
        mode.value = 'list';
        selectedItem.value = {};
        fetchList();
    }
});

onMounted(async () => {
  // 로그인 정보 확인
  const raw = sessionStorage.getItem('loginId');
  if (raw) loginInfo.value = JSON.parse(raw);

  if (isAdmin.value) {
    try {
      const res = await getAdminInfoReq();
      if (res && res.data) {
        if (res.data.isPr === true) {
          isPrTeamState.value = true;
        }
      }
    } catch (e) { console.log(e); }
  }

  // 초기 로딩
  fetchList();

  // 외부(메인화면 등)에서 ID를 달고 들어왔을 때 바로 실행
  if (route.params.id) {
      goDetail({ healthStoryId: route.params.id });
  }
});
</script>

<style scoped>
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'pretendard';
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h2 {
  font-size: 42px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 18px;
}

/* 상단 컨트롤 */
.top-controls {
  display: flex;
  justify-content: center;
  position: relative;
  margin-bottom: 50px;
  height: 50px;
}

.video-info {
  padding: 0px 8px 10px;
  text-align: left;    
  margin-bottom: 8px;
}

.video-search {
  display: flex;
  background: #f4f4f4;
  /* border-radius: 4px; */
  padding: 0;
  width: 100%;
  max-width: 600px;
  height: 100%;
}

.video-search input {
  flex: 1;
  background: none;
  border: none;
  padding: 10px 15px;
  outline: none;
  font-size: 14px;
}

.s-btn {
  background: #005baa;
  color: #fff;
  border: none;
  padding: 0 20px;
  /* border-radius: 4px; */
  cursor: pointer;
  font-size: 14px;
}

.btn-write {
  position: absolute;
  right: 0;
  top: 0; 
  background: #005baa;
  color: #fff;
  border: none;
  padding: 0 20px; 
  font-weight: 600;
  cursor: pointer;
  height: 100%;
}

/* 그리드 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}


.video-card {
  cursor: pointer;
  transition: 0.3s;
  background: #fff;
  /* border-radius: 8px; */
  overflow: hidden;
}

.video-card:hover {
  transform: translateY(-7px);
  box-shadow: 0 20px 40px rgba(0, 91, 170, 0.15);
  border-color: #005baa;
  z-index: 2;
}

.thumb-box {
  width: 120%;
  aspect-ratio: 16/9;
  /* border-radius: 8px; */
  overflow: hidden;
  margin-bottom: 15px;
  border: 1px solid #eee;
  position: relative;
}

.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-thumb {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
}

.v-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
  height: 1.4em;
  overflow: hidden;
  display: block;
  word-wrap: break-word;
}

.v-author {
  font-size: 16px;
  color: #888;
}

/* 상세 화면 */
.detail-wrap {
  border-top: 2px solid #333;
}

.detail-header {
  background: #f8f9fa;
  padding: 25px 30px;
  border-bottom: 1px solid #eee;
}

.dh-title {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
}

.dh-info {
  font-size: 16px;
  color: #777;
}

.bar {
  margin: 0 15px;
  color: #ddd;
}

.detail-content {
  font-size: 20px;
  padding: 40px 30px;
  min-height: 300px;
  color: #444;
  line-height: 1.6;
  border-bottom: 1px solid #eee;
}

.media-view {
  margin-top: 40px;
  text-align: center;
}

.detail-media {
  max-width: 100%;
  max-height: 400px;
  border: 1px solid #ddd;
}

.view-text :deep(a) {
  color: #005baa;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 600;
}

/* 파일 다운로드 */
.file-attach-area {
  padding: 20px 30px;
  background: #fdfdfd;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
}

.fa-label {
  font-weight: 600;
  margin-right: 20px;
  font-size: 14px;
  color: #333;
}

.fa-content {
  flex: 1;
}

.file-link {
  color: #555;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.file-link:hover {
  color: #005baa;
  text-decoration: underline;
}

.no-file {
  color: #999;
  font-size: 14px;
}

/* 버튼 그룹 */
.btn-group {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
}

.btn-list {
  padding: 12px 40px;
  background: #fff;
  border: 1px solid #333;
  color: #333;
  font-weight: 600;
  cursor: pointer;
  font-size: 16px;
}

.admin-btns {
  display: flex;
  gap: 5px;
}

.btn-mod {
  padding: 12px 40px;
  background: #555;
  color: #fff;
  border: none;
  font-weight: 600;
  cursor: pointer;
  font-size: 16px;
}

.btn-del {
  padding: 12px 40px;
  background: #333;
  color: #fff;
  border: none;
  font-weight: 600;
  cursor: pointer;
  font-size: 16px;
}

/* 글쓰기 폼 */
.write-wrap {
  border-top: 2px solid #333;
  padding-top: 30px;
}

.write-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
  text-align: center;
}

.write-form {
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
}

.form-row {
  display: flex;
  border-bottom: 1px solid #eee;
}

.form-row label {
  width: 140px;
  background: #f9f9f9;
  padding: 15px 20px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
}

.input-wrap {
  flex: 1;
  padding: 10px 15px;
  display: flex;
  align-items: center;
}

.full-input {
  width: 100%;
  border: none;
  padding: 5px;
  font-size: 14px;
  outline: none;
}

.full-textarea {
  width: 100%;
  height: 300px;
  border: none;
  padding: 5px;
  font-size: 14px;
  outline: none;
  resize: none;
  font-family: 'pretendard';
}

.btn-group.center {
  justify-content: center;
  gap: 10px;
}

.btn-cancel {
  padding: 12px 40px;
  background: #fff;
  border: 1px solid #ccc;
  cursor: pointer;
}

.btn-save {
  padding: 12px 40px;
  background: #005baa;
  color: #fff;
  border: none;
  cursor: pointer;
  font-weight: 600;
}

/* 파일 첨부 */
.file-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.preview-box {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #eee;
  padding: 5px;
  border-radius: 4px;
}

.mini-img {
  width: 40px;
  height: 40px;
  object-fit: cover;
}

.file-name-tag {
  font-size: 12px;
  color: #666;
}

.btn-x {
  background: #999;
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  cursor: pointer;
}

/* 페이지네이션 */
.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: #fff;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.page-btn.active {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

.no-data {
  text-align: center;
  padding: 100px;
  color: #999;
}
</style>