<template>
    <div class="notice-container">
        <div class="page-header">
            <h2>병원 공지사항</h2>
            <p>서울에스병원의 새로운 소식을 알려드립니다.</p>
        </div>

        <div v-if="mode === 'list'" class="list-wrap">
            <div class="top-controls">
                <div class="total-count">총 <span>{{ noticeList.length }}</span>건</div>
                <div class="right-area">
                    <select v-model="sortOrder" class="sort-select">
                        <option value="desc">최신순</option>
                        <option value="asc">오래된 순</option>
                    </select>
                    <div class="search-box">
                        <input type="text" v-model="keyword" placeholder="검색어 입력" @keyup.enter="getNoticeList">
                        <button class="btn-search" @click="getNoticeList">검색</button>
                    </div>
                    <button v-if="canManageNotice" class="btn-write" @click="goWrite">글쓰기</button>
                </div>
            </div>

            <table class="notice-table">
                <colgroup>
                    <col width="60px" />
                    <col width="*" />
                    <col width="120px" />
                    <col width="80px" />
                </colgroup>
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>등록일</th>
                        <th>조회</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="paginatedList.length === 0">
                        <td colspan="4" class="no-data">등록된 공지사항이 없습니다.</td>
                    </tr>
                    <tr v-for="item in paginatedList" :key="item.noticeId" @click="goDetail(item)" class="notice-row"
                        :class="{ 'pinned': item.topFix }">
                        <td class="text-center">
                            <span v-if="item.topFix" class="badge-red">공지</span>
                            <span v-else>{{ item.noticeId }}</span>
                        </td>
                        <td class="text-left title-cell">
                            {{ item.title }}
                            <span v-if="isNew(item.writeDate)" class="icon-new">N</span>
                            <span v-if="item.hasFile || item.fileName" class="clip-icon">📎</span>
                        </td>
                        <td class="text-center">{{ formatDate(item.writeDate) }}</td>
                        <td class="text-center">{{ item.readCount }}</td>
                    </tr>
                </tbody>
            </table>

            <div class="pagination-area" v-if="noticeList.length > 0">
                <button class="page-btn prev" :disabled="currentPage === 1" @click="currentPage--">&lt;</button>
                <button v-for="page in visiblePages" :key="page" class="page-btn number"
                    :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
                <button class="page-btn next" :disabled="currentPage === totalPages"
                    @click="currentPage++">&gt;</button>
            </div>
        </div>

        <div v-else-if="mode === 'detail'" class="detail-wrap">
            <div class="detail-header">
                <div class="dh-title">
                    <span v-if="selectedItem.topFix" class="badge-red">공지</span>
                    {{ selectedItem.title }}
                </div>
                <div class="dh-info">
                    <span><strong>등록일</strong> {{ formatDate(selectedItem.writeDate) }}</span>
                    <span class="bar">|</span>
                    <span><strong>작성자</strong> 관리자</span>
                    <span class="bar">|</span>
                    <span><strong>조회수</strong> {{ selectedItem.readCount }}</span>
                </div>
            </div>

            <div class="detail-content">
                <div class="view-text" @click="handleLinkClick" v-html="formatContent(selectedItem.content)"></div>

                <div v-if="isImage(selectedItem.fileName)" class="img-preview-box">
                    <img :src="selectedItem.fileUrl" alt="공지 이미지" class="attached-img">
                </div>
            </div>

            <div class="file-attach-area">
                <span class="fa-label">첨부파일</span>
                <div class="fa-content">
                    <a v-if="selectedItem.fileName" :href="selectedItem.fileUrl" download class="file-link">
                        {{ selectedItem.fileName }}
                    </a>
                    <span v-else class="no-file">첨부된 파일이 없습니다.</span>
                </div>
            </div>

            <div class="nav-links">
                <div class="nav-item prev" @click="prevItem ? goDetail(prevItem) : null"
                    :class="{ disabled: !prevItem }">
                    <span class="nav-label">이전글 ▲</span>
                    <span class="nav-title">{{ prevItem ? prevItem.title : '이전 글이 없습니다.' }}</span>
                </div>
                <div class="nav-item next" @click="nextItem ? goDetail(nextItem) : null"
                    :class="{ disabled: !nextItem }">
                    <span class="nav-label">다음글 ▼</span>
                    <span class="nav-title">{{ nextItem ? nextItem.title : '다음 글이 없습니다.' }}</span>
                </div>
            </div>
            
            <div class="btn-group">
                <button class="btn-list" @click="goList">목록</button>
                <div class="admin-btns" v-if="canManageNotice">
                    <button class="btn-mod" @click="goEdit(selectedItem)">수정</button>
                    <button class="btn-del" @click="deleteNotice(selectedItem.noticeId)">삭제</button>
                </div>
            </div>
        </div>

        <div v-else-if="mode === 'write' || mode === 'edit'" class="write-wrap">
            <h3 class="write-title">{{ mode === 'write' ? '공지사항 등록' : '공지사항 수정' }}</h3>
            <div class="write-form">
                <div class="form-row">
                    <label>상단 고정</label>
                    <div class="input-wrap">
                        <input type="checkbox" v-model="writeForm.topFix" id="chkTop">
                        <label for="chkTop" class="chk-label">고정 공지로 등록</label>
                    </div>
                </div>
                <div class="form-row">
                    <label>제목</label>
                    <div class="input-wrap">
                        <input type="text" v-model="writeForm.title" placeholder="제목을 입력하세요" class="full-input">
                    </div>
                </div>

                <div class="form-row">
                    <label>첨부파일</label>
                    <div class="input-wrap file-wrap">
                        <input type="file" ref="fileInput" @change="handleFileChange"
                            accept="image/*, .pdf, .hwp, .doc, .docx">

                        <div v-if="previewUrl" class="preview-box">
                            <img v-if="isImageFile" :src="previewUrl" class="mini-img">
                            <span v-else class="file-name-tag">📄 {{ selectedFile.name }}</span>
                            <button class="btn-x" @click="removeFile">X</button>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <label>내용</label>
                    <div class="input-wrap">
                        <textarea v-model="writeForm.content" placeholder="내용을 입력해 주세요" class="full-textarea"></textarea>
                    </div>
                </div>
            </div>
            <div class="btn-group center">
                <button class="btn-cancel" @click="goList">취소</button>
                <button class="btn-save" @click="submitNotice">{{ mode === 'write' ? '등록' : '수정' }}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
// [API] 경로가 맞는지 꼭 확인해주세요!
import {
    getNoticesReq, getNoticeDetailReq,
    addNoticeReq, editNoticeReq, delNoticeReq,
    getAdminInfoReq
} from '@/api/customer';

const route = useRoute();
const router = useRouter();

// 상태 변수 (State)
const mode = ref('list');           // 현재 화면 모드 (list, detail, write, edit)
const keyword = ref('');            // 검색어
const loginInfo = ref({});          // 로그인 정보
const selectedItem = ref({});       // 상세 보기할 글 데이터
const sortOrder = ref('desc');      // 정렬 순서
const currentPage = ref(1);         // 현재 페이지
const itemsPerPage = 10;            // 페이지당 글 개수
const maxPageBtn = 5;               // 페이지네이션 버튼 개수
const noticeList = ref([]);         // 공지사항 리스트 데이터
const writeForm = ref({ noticeId: '', title: '', content: '', topFix: false }); // 글쓰기 폼 데이터
const isWonmuState = ref(false);    // 원무과 여부 상태

// [추가] 파일 업로드 관련 변수
const fileInput = ref(null);        // input 태그 연결
const selectedFile = ref(null);     // 선택된 파일 데이터
const previewUrl = ref(null);       // 미리보기 URL
const isImageFile = ref(false);     // 이미지 여부 체크


// 권한 체크 (Auth)
// [관리자 확인]
const isAdmin = computed(() => {
    const info = loginInfo.value || {};
    const userRole = info.loginType || info.role || '';
    return String(userRole).toUpperCase() === 'ADMIN';
});

// [원무과 확인]
const isWonmuTeam = computed(() => {
    if (isWonmuState.value === true) return true;
    const info = loginInfo.value || {};
    const deptName = String(info.admin_dept_name ?? info.adminDeptName ?? '').trim();
    return deptName.includes('원무');
});

// [최종 권한] 관리자이면서 원무과여야 글쓰기/수정/삭제 가능
const canManageNotice = computed(() => {
    return isAdmin.value && isWonmuTeam.value;
});


// Computed
// [정렬] 상단 고정(topFix) 우선, 그 뒤 최신순/과거순 정렬
const sortedList = computed(() => {
    const list = [...noticeList.value];
    return list.sort((a, b) => {
        // 1순위: 상단 고정 여부
        if (a.topFix !== b.topFix) { 
            return Number(b.topFix) - Number(a.topFix);
        }
        // 2순위: 정렬 필터 (최신순/오래된순)
        if (sortOrder.value === 'desc') {
            return b.noticeId - a.noticeId;
        } else {
            return a.noticeId - b.noticeId;
        }
    });
});

// [페이징 & 검색] 화면에 보여줄 데이터 자르기
const paginatedList = computed(() => {
    let targetList = sortedList.value;
    
    // 검색 필터
    if (keyword.value) {
        targetList = targetList.filter(item => item.title.includes(keyword.value));
    }

    // 페이징 자르기
    const startIndex = (currentPage.value - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    return targetList.slice(startIndex, endIndex);
});

// [전체 페이지 수 계산]
const totalPages = computed(() => {
    let count = 0;
    if (keyword.value) {
        count = sortedList.value.filter(item => item.title.includes(keyword.value)).length;
    } else {
        count = sortedList.value.length;
    }
    const pages = Math.ceil(count / itemsPerPage);
    return pages === 0 ? 1 : pages;
});

// [페이지네이션 버튼 번호 계산]
const visiblePages = computed(() => {
    const currentGroup = Math.ceil(currentPage.value / maxPageBtn);
    const startPage = (currentGroup - 1) * maxPageBtn + 1;
    let endPage = startPage + maxPageBtn - 1;
    if (endPage > totalPages.value) endPage = totalPages.value;
    
    const pages = [];
    for (let i = startPage; i <= endPage; i++) pages.push(i);
    return pages;
});


// 네비게이션 (이전글/다음글)
const currentIndex = computed(() => {
    if (!selectedItem.value.noticeId) return -1;
    return sortedList.value.findIndex(item => item.noticeId === selectedItem.value.noticeId);
});

const prevItem = computed(() => {
    if (currentIndex.value <= 0) return null;
    return sortedList.value[currentIndex.value - 1];
});

const nextItem = computed(() => {
    if (currentIndex.value === -1 || currentIndex.value >= sortedList.value.length - 1) return null;
    return sortedList.value[currentIndex.value + 1];
});


// Utils
const formatDate = (dateStr) => dateStr ? String(dateStr).substring(0, 10) : '';
const isNew = (dateStr) => dateStr ? (new Date() - new Date(dateStr)) / (1000 * 60 * 60 * 24) < 1 : false;

// 권한 가드 함수 (버튼 클릭 시 재확인용)
const guardWonmu = () => { 
    if (!canManageNotice.value) { 
        alert("권한이 없습니다."); 
        return false; 
    } 
    return true; 
};

// [추가] 서버 이미지 경로(/images/...)를 전체 URL(http://...)로 변환
const getImageUrl = (path) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return `http://localhost:8080${path}`; 
};

// 파일 확장자 체크 (이미지인지 확인)
const isImage = (fileName) => {
    if (!fileName) return false;
    const ext = fileName.split('.').pop().toLowerCase();
    return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext);
};

// 본문 줄바꿈 처리
const formatContent = (text) => text ? text.replace(/\n/g, '<br>') : '';

// 내부 링크 클릭 처리 (새로고침 방지)
const handleLinkClick = (e) => {
    const target = e.target.closest('a');
    if (target) {
        const href = target.getAttribute('href');
        if (href && href.startsWith('/')) {
            e.preventDefault();
            router.push(href);
        }
    }
};

// 파일 처리 핸들러 (File Handler)
const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    
    // 이미지 파일이면 미리보기 생성
    if (file.type.startsWith('image/')) {
        isImageFile.value = true;
        previewUrl.value = URL.createObjectURL(file);
    } else {
        isImageFile.value = false;
        previewUrl.value = 'doc'; // 문서 아이콘용 플래그
    }
};

const removeFile = () => {
    selectedFile.value = null;
    previewUrl.value = null;
    isImageFile.value = false;
    // input 태그 값도 초기화
    if (fileInput.value) fileInput.value.value = '';
};

// 화면 이동 로직 (Routing)
const goList = () => { 
    router.push('/notice'); 
    mode.value = 'list'; 
    selectedItem.value = {}; 
    writeForm.value = { noticeId: '', title: '', content: '', topFix: false }; // 폼 초기화
    removeFile(); 
    getNoticeList(); // 리스트 새로고침
};

const goWrite = () => {
    if (!guardWonmu()) return;
    writeForm.value = { title: '', content: '', topFix: false };
    removeFile();
    mode.value = 'write';
};

const goEdit = (item) => {
    if (!guardWonmu()) return;
    
    // 기존 데이터 복사
    writeForm.value = { ...item };
    
    // 기존 파일이 있다면 미리보기 설정
    if (item.fileUrl) {
        previewUrl.value = item.fileUrl;
        selectedFile.value = null; // 새로 올릴 파일은 아직 없음
        
        if (isImage(item.fileName)) {
            isImageFile.value = true;
        } else {
            isImageFile.value = false;
        }
    } else {
        removeFile();
    }
    mode.value = 'edit';
};

// 상세 화면 이동
const goDetail = (item) => { 
    router.push(`/notice/${item.noticeId}`); 
};

// 상세 데이터 로드 (새로고침 시 사용)
const loadDetailData = async (id) => {
    try {
        const res = await getNoticeDetailReq(id);
        const data = res.data;

        selectedItem.value = {
            ...data,
            // [중요] 서버 이미지 경로 변환
            fileUrl: getImageUrl(data.thumbnailImg),
            // 파일명만 추출
            fileName: data.thumbnailImg ? data.thumbnailImg.split('/').pop() : ''
        };
        
        mode.value = 'detail';
        window.scrollTo(0, 0);
    } catch (e) { 
        console.error(e);
        mode.value = 'list'; 
    }
};

// 서버 통신 (API Call)
// 목록 조회
const getNoticeList = async () => {
    try {
        const params = keyword.value ? { keyword: keyword.value } : null;
        const res = await getNoticesReq(params);
        
        // 서버 데이터를 화면에 맞게 변환
        noticeList.value = res.data.map(item => ({
            ...item,
            fileUrl: getImageUrl(item.thumbnailImg),
            fileName: item.thumbnailImg,
            hasFile: !!item.thumbnailImg // 파일 존재 여부
        })) || [];
    } catch (err) { 
        console.error(err);
        noticeList.value = []; 
    }
};

// 글 등록 및 수정 (파일 업로드 포함)
const submitNotice = async () => {
    if (!guardWonmu()) return;
    if (!writeForm.value.title || !writeForm.value.content) { 
        alert("내용을 입력해주세요"); 
        return; 
    }

    try {
        const memId = Number(loginInfo.value.memId || 0);

        // [중요] 파일 전송을 위해 FormData 사용
        const formData = new FormData();
        formData.append('title', writeForm.value.title);
        formData.append('content', writeForm.value.content);
        formData.append('topFix', writeForm.value.topFix);
        formData.append('memId', memId);

        if (mode.value === 'edit') {
            formData.append('noticeId', writeForm.value.noticeId);
        } else {
            formData.append('del', 0);
        }

        // 파일이 선택된 경우에만 추가
        if (selectedFile.value) {
            formData.append('uploadFile', selectedFile.value); 
        }

        let res = mode.value === 'write' ? await addNoticeReq(formData) : await editNoticeReq(formData);

        if (res.data === 'success' || res.data === true) {
            alert("완료되었습니다.");
            goList();
        } else {
            alert("실패했습니다.");
        }
    } catch (e) { 
        console.error(e);
        alert("오류가 발생했습니다."); 
    }
};

// 글 삭제
const deleteNotice = async (id) => {
    if (!guardWonmu()) return;
    if (!confirm("정말 삭제하시겠습니까?")) return;
    try {
        const res = await delNoticeReq(id);
        if (res.data === 'success' || res.data === true) { 
            alert("삭제되었습니다."); 
            goList(); 
        }
    } catch (e) { alert("오류가 발생했습니다."); }
};

// URL 변경 감지 (뒤로가기 지원)
watch(() => route.params.id, (newId) => { 
    if (newId) loadDetailData(newId); 
    else {
        mode.value = 'list';
        getNoticeList(); // 목록으로 돌아올 때 데이터 갱신
    }
});

onMounted(async () => {
    // 세션에서 로그인 정보 확인
    const raw = sessionStorage.getItem('loginId');
    if (raw) loginInfo.value = JSON.parse(raw);
    
    // 원무과 권한 체크
    if (isAdmin.value) {
        try { 
            const res = await getAdminInfoReq(); 
            if (res && res.data && res.data.isWonmu) {
                isWonmuState.value = true;
            }
        } catch (e) { console.log(e); }
    }
    
    // 초기 데이터 로드
    await getNoticeList();
    
    // URL에 ID가 있으면 상세 페이지 로드
    if (route.params.id) {
        loadDetailData(route.params.id);
    }
});
</script>

<style scoped>
.notice-table {
    width: 100%;
    border-top: 2px solid #333;
    border-bottom: 1px solid #333;
    border-collapse: collapse;
}

.notice-container {
    font-family: 'Pretendard', -apple-system, sans-serif !important;
    max-width: 1000px;
    margin: 80px auto;
    padding: 0 20px;
    font-family: 'pretendard';
}

.page-header {
    margin-bottom: 40px;
    text-align: center;
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

.top-controls {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 15px;
}

.total-count {
    font-size: 18px;
    color: #666;
}

.total-count span {
    color: #005baa;
    font-weight: 700;
}

.right-area {
    display: flex;
    gap: 10px;
}

.sort-select {
    padding: 0 10px;
    border: 1px solid #ddd;
    font-size: 13px;
    outline: none;
    height: 34px;
    cursor: pointer;
    color: #555;
}

.search-box {
    display: flex;
}

.search-box input {
    border: 1px solid #ddd;
    padding: 8px 10px;
    font-size: 13px;
    outline: none;
    width: 200px;
    height: 34px;
    box-sizing: border-box;
}

.btn-search {
    background: #333;
    color: #fff;
    border: none;
    padding: 0 15px;
    cursor: pointer;
    font-size: 13px;
    height: 34px;
}

.btn-write {
    background: #005baa;
    color: #fff;
    border: none;
    padding: 0 20px;
    font-weight: 600;
    cursor: pointer;
    height: 34px;
}

.notice-table th {
    background: #f8f9fa;
    padding: 20px 0px;
    font-size: 18px;
    color: #333;
    border-bottom: 1px solid #ddd;
    font-weight: 600;
}

.notice-table td {
    padding: 15px 10px;
    border-bottom: 1px solid #eee;
    font-size: 16px;
    color: #555;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.notice-row {
    cursor: pointer;
    transition: background 0.2s;
}

.notice-row:hover {
    background: #fbfbfb;
}

.notice-row.pinned {
    background-color: #f0f7ff;
}

.notice-row.pinned td {
    font-weight: 600;
    color: #333;
}

.icon-new {
    display: inline-block;
    background: #ff4d4f;
    color: #fff;
    font-size: 10px;
    padding: 1px 4px;
    margin-left: 5px;
    border-radius: 2px;
    vertical-align: middle;
}

.text-center {
    text-align: center;
}

.text-left {
    text-align: left;
    padding-left: 20px;
}

.no-data {
    text-align: center;
    padding: 50px;
    color: #999;
}

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

.detail-wrap {
    border-top: 2px solid #333;
}

.detail-header {
    background: #f8f9fa;
    padding: 25px 30px;
    border-bottom: 1px solid #eee;
}

.dh-title {
    font-size: 22px;
    font-weight: 700;
    color: #333;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.dh-info {
    font-size: 13px;
    color: #777;
}

.bar {
    margin: 0 15px;
    color: #ddd;
}

.detail-content {
    padding: 40px 30px;
    min-height: 200px; /* [수정] 최소 높이 조정 */
    color: #444;
    line-height: 1.6;
    font-size: 15px;
    border-bottom: 1px solid #eee;
}

.btn-group {
    margin-top: 60px;
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
}

.admin-btns {
    display: flex;
    gap: 5px;
}

.btn-mod {
    padding: 12px 25px;
    background: #555;
    color: #fff;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

.btn-del {
    padding: 12px 25px;
    background: #333;
    color: #fff;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

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

/* [수정 후] : .form-row의 '직계 자식' 라벨에만 적용 (제목용 라벨만 해당됨) */
.form-row > label {
   width: 120px;
   background: #f9f9f9;
   padding: 15px 20px;
   font-weight: 600;
   color: #333;
   display: flex;
   align-items: center;
}

/* 체크박스 옆 텍스트 스타일 */
.chk-label {
  margin-left: 8px; /* 체크박스와 글씨 사이 간격 */
  font-size: 15px;
  color: #333;
  cursor: pointer;
  width: auto; /* 혹시 모르니 width 자동 */
}

/* 체크박스랑 글씨 높이 딱 맞게 정렬 */
.input-wrap {
  display: flex;
  align-items: center;
}

/* 체크박스 크기 살짝 키움 (선택사항) */
.input-wrap input[type="checkbox"] {
  width: 17px;
  height: 17px;
  margin: 0;
  cursor: pointer;
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

.badge-red {
    background-color: #ff4d4f;
    color: #fff;
    font-size: 11px;
    font-weight: 600;
    padding: 3px 6px;
    border-radius: 4px;
    display: inline-block;
    margin-right: 5px;
}

.nav-links {
    border-bottom: 1px solid #000000;
    margin-bottom: 30px;
}

.nav-item {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}

.nav-label {
    font-size: 13px;
    font-weight: 600;
    width: 80px;
    color: #333;
}

.nav-title {
    font-size: 14px;
    color: #555;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
    text-decoration: underline darkgray;
    transition: 0.3s;  /* 0.3초 동안 부드럽게 바뀌는 효과 (선택사항) */
    cursor: pointer;
}

.nav-title:hover {
    font-size: 14px;
    color: #c55b5b;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
    text-decoration: underline #c55b5b;
}

/* [추가] 상세화면 링크 스타일 (v-html 내부 적용용) */
.view-text :deep(a) {
    color: #005baa;
    text-decoration: underline;
    cursor: pointer;
    font-weight: 600;
}

/* [추가] 파일 첨부 관련 스타일 */
.clip-icon {
    font-size: 14px;
    margin-left: 5px;
}

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

/* [추가] 상세화면 파일 다운로드 영역 (본문 박스 밖으로) */
.file-attach-area {
    margin-top: 0;
    padding: 20px 20px;
    background: #fdfdfd;
    border-bottom: 1px solid #d2d2d2;
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

.img-preview-box {
    margin-top: 20px;
    text-align: center;
}

.attached-img {
    max-width: 100%;
    max-height: 600px;
    border: 1px solid #ddd;
}
</style>