<template>
  <div id="home-layout">
    <header :class="['main-header', {
      'is-main': isMainPage,
      'is-scrolled': (isScrolled && !isTransparentSection) || isMegamenuOpen
    }]" @mouseleave="closeMegamenu">
      <div class="header-inner">
        <div class="logo-box" @click="router.push('/')">
          <img :src="logoSrc" alt="S-HOSPITAL" class="main-logo-img">
        </div>

        <nav class="nav-menu">
          <span class="nav-item" @mouseenter="openMegamenu('intro')">병원소개</span>
          <span class="nav-item" @mouseenter="openMegamenu('medical')">의료진/진료과</span>
          <span class="nav-item" @mouseenter="openMegamenu('reserve')">진료예약/안내</span>
          <span class="nav-item" @mouseenter="openMegamenu('health')">건강정보</span>
          <span class="nav-item" @mouseenter="openMegamenu('service')">고객서비스</span>
        </nav>

        <div class="combined-util">
          <template v-if="!isLogin">
            <span class="util-item" @click="router.push('/login')">로그인</span>
            <span class="util-item" @click="router.push('/regi')">회원가입</span>
          </template>
          <template v-else>
            <span class="user-info"><b>{{ loginName }}</b>님</span>
            <span @click="handleLogout" class="util-item logout-txt">로그아웃</span>
          </template>
          <span class="util-item" @click="goPage('/mypage')">마이페이지</span>
        </div>
      </div>

      <div :class="['megamenu-panel', { active: isMegamenuOpen }]">
        <div class="megamenu-inner">

          <div v-if="activeMenu === 'medical'" class="special-menu-box type-medical">
            <div class="left-search-zone">
              <h3 class="st-title">의료진/진료과</h3>
              <p class="st-desc">의료진 이름이나 진료과를 입력하세요</p>
              <div class="st-search-bar">
                <input type="text" placeholder="의료진/진료과 입력" v-model="searchKeyword" @keyup.enter="onMenuSearch">
                <button @click="onMenuSearch">검색</button>
              </div>
            </div>
            <div class="right-icon-zone">
              <div class="st-icon-item" @click="goPage('/deptsearch')">
                <div class="circle-icon">
                  <svg width="80" height="80" viewBox="0 0 64 64" fill="none">
                    <path d="M54 48H10V16H22L26 10H54V48Z" stroke="currentColor" stroke-width="1" />
                    <path d="M32 24V40M24 32H40" stroke="#005baa" stroke-width="1" />
                  </svg>
                </div>
                <div class="icon-txt"><span class="main-t">진료과</span></div>
              </div>
              <div class="st-icon-item" @click="goPage('/doctorsearch')">
                <div class="circle-icon">
                  <svg width="80" height="80" viewBox="0 0 64 64" fill="none">
                    <circle cx="32" cy="24" r="10" stroke="currentColor" stroke-width="1" />
                    <path d="M14 54C14 42.9543 22.0589 34 32 34C41.9411 34 50 42.9543 50 54" stroke="currentColor"
                      stroke-width="1" />
                  </svg>
                </div>
                <div class="icon-txt"><span class="main-t">의료진</span></div>
              </div>
            </div>
          </div>

          <div v-else-if="activeMenu === 'reserve'" class="special-menu-box type-reserve">
            <div class="left-info-zone">
              <h3 class="st-title">진료예약/안내</h3>
              <div class="reserve-guide-card">
                <p class="guide-tag">RESERVATION</p>
                <div class="call-info-group">
                  <p class="guide-text">전화 예약</p>
                  <div class="call-center-box"><span class="number">1577-0083</span></div>
                </div>
                <button class="primary-rect-btn" @click="goPage('/reservation')">온라인 예약</button>
              </div>
            </div>
            <div class="right-list-zone">
              <div class="menu-box-item">
                <p class="box-head">예약/조회</p>
                <ul>
                  <li @click="goPage('/reservation')">진료예약</li>
                  <li @click="goPage('/checkreservation')">예약내역 조회</li>
                </ul>
              </div>
              <div class="menu-box-item">
                <p class="box-head">이용안내</p>
                <ul>
                  <li @click="goPage('/guide')">진료 시간 안내</li>
                  <li @click="goPage('/process')">진료 절차 안내</li>
                </ul>
              </div>
            </div>
          </div>

          <div v-else class="column-menu-box">
            <div v-if="activeMenu === 'intro'" class="full-menu-grid">
              <div class="promo-small-box">
                <div class="promo-icon-circle">
                  <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
                    <path d="M3 21h18M3 7v14M21 7v14M9 21V11M15 21V11M2 7l10-5 10 5" />
                  </svg>
                </div>
                <div class="promo-txt-area">
                  <p><b>Smart S-Hospital</b><br>첨단 스마트 의료 시스템</p>
                  <button class="promo-go-btn" @click="goPage('/greeting')">병원 소개 보러가기 &gt;</button>
                </div>
              </div>
              <div class="menu-box-item">
                <p class="box-head">소개</p>
                <ul>
                  <li @click="goPage('/greeting')">인사말</li>
                  <li @click="goPage('/history')">연혁</li>
                  <li @click="goPage('/mission')">미션/비전</li>
                </ul>
              </div>
              <div class="menu-box-item">
                <p class="box-head">오시는 길</p>
                <ul>
                  <li @click="goPage('/location')">병원 지도</li>
                  <li @click="goPage('/parkinginfo')">주차 이용 안내</li>
                </ul>
              </div>
            </div>

            <div v-if="activeMenu === 'health'" class="full-menu-grid">
              <div class="promo-small-box sky">
                <div class="promo-icon-circle">
                  <svg width="36" height="36" viewBox="-0.5 -0.5 24 24" fill="none" stroke="#005baa" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round" preserveAspectRatio="xMidYMid meet">
                    <path
                      d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                  </svg>
                </div>
                <div class="promo-txt-area">
                  <p><b>Health Guide</b><br>정확한 정보를 약속합니다</p>
                  <button class="promo-go-btn" @click="goPage('/story')">건강 정보 전체보기 &gt;</button>
                </div>
              </div>
              <div class="menu-box-item">
                <p class="box-head">건강한 삶</p>
                <ul>
                  <li @click="goPage('/story')">건강이야기</li>
                  <li @click="goPage('/disease')">질환백과</li>
                </ul>
              </div>
              <div class="menu-box-item">
                <p class="box-head">자가 체크</p>
                <ul>
                  <li @click="goPage('/checkup')">자가진단 서비스</li>
                </ul>
              </div>
            </div>

            <div v-if="activeMenu === 'service'" class="full-menu-grid">
              <div class="promo-small-box point">
                <div class="promo-icon-circle">
                  <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
                    <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" />
                  </svg>
                </div>
                <div class="promo-txt-area">
                  <p><b>Customer Support</b><br>친절하고 빠른 도움</p>
                  <button class="promo-go-btn" @click="goPage('/notice')">고객 지원 센터 &gt;</button>
                </div>
              </div>
              <div class="menu-box-item">
                <p class="box-head">병원소식</p>
                <ul>
                  <li @click="goPage('/notice')">공지사항</li>
                </ul>
              </div>
              <div class="menu-box-item">
                <p class="box-head">도움말</p>
                <ul>
                  <li @click="goPage('/faq')">자주 묻는 질문(FAQ)</li>
                </ul>
              </div>
              <div class="menu-box-item">
                <p class="box-head" v-if="showVoc">소통</p>
                <ul>
                  <li @click="goPage('/voc')" v-if="showVoc">고객의 소리</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div v-if="!isMainPage" class="breadcrumb-bar">
      <div class="breadcrumb-inner">
        <span class="home-icon" @click="router.push('/')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        </span>
        <span class="divider">&gt;</span>
        <div class="crumb-dropdown" @mouseenter="openDepth1 = true" @mouseleave="openDepth1 = false">
          <div class="current-select">{{ currentCategoryInfo.categoryName || '서울에스병원' }}
            <svg class="arrow-icon" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="2">
              <path d="M6 9l6 6 6-6" />
            </svg>
          </div>
          <ul v-show="openDepth1" class="dropdown-list">
            <li v-for="cat in siteMap" :key="cat.name" @click="goPage(cat.list[0].path)">{{ cat.name }}</li>
          </ul>
        </div>
        <span class="divider">&gt;</span>
        <div class="crumb-dropdown" @mouseenter="openDepth2 = true" @mouseleave="openDepth2 = false">
          <div class="current-select active">{{ currentMenuName }}
            <svg class="arrow-icon" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="2">
              <path d="M6 9l6 6 6-6" />
            </svg>
          </div>
          <ul v-show="openDepth2" class="dropdown-list">
            <li v-for="page in currentCategoryInfo.pages" :key="page.path"
              :class="{ 'on': route.path.startsWith(page.path) }" @click="goPage(page.path)">{{ page.name }}</li>
          </ul>
        </div>
      </div>
    </div>

    <main :class="['content-body', { 'is-main-view': isMainPage }]">
      <router-view @mainScroll="handleMainScroll" />
    </main>

    <footer v-if="!isMainPage" class="main-footer">
      <div class="footer-inner">
        <div class="f-row f-top">
          <div class="f-logo-box"><img src="@/assets/txtlogo2.png" alt="S-HOSPITAL" class="f-logo-img"></div>
          <div class="f-links">
            <span class="bold">개인정보처리방침</span><span class="bar">|</span>
            <span>이용약관</span><span class="bar">|</span>
            <span>환자의 권리와 의무</span>
          </div>
        </div>
        <div class="f-row f-middle">
          <div class="f-info-text">
            <span>경기도 성남시 수정구 금토로80번길 56, 서울에스병원</span><span class="bar">|</span>
            <span>대표자: 엠비씨</span><span class="bar">|</span>
            <span>사업자등록번호: 123-45-67890</span>
          </div>
          <div class="f-sns-side">
            <a href="#" class="sns-link" title="유튜브"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path
                  d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z" />
                <polygon points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02" />
              </svg></a>
            <a href="#" class="sns-link" title="인스타그램"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="2" width="20" height="20" rx="5" ry="5" />
                <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
                <line x1="17.5" y1="6.5" x2="17.51" y2="6.5" />
              </svg></a>
            <a href="#" class="sns-link" title="네이버 블로그"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16v16H4z" />
                <path d="M9 8v8" />
                <path d="M9 12h4a2 2 0 0 0 0-4H9" />
                <path d="M9 16h5a2 2 0 0 0 0-4H9" />
              </svg></a>
          </div>
        </div>
        <div class="f-row f-bottom-info">
          <span>대표전화: <b class="blue-txt">1588-8282</b></span><span class="bar">|</span>
          <span>응급의료센터: 02-123-4567</span><span class="bar">|</span>
          <span>이메일: help@s-hospital.com</span>
        </div>
        <div class="footer-copyright">
          <p>Copyright © S-HOSPITAL. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <div class="floating-aside">
      <button class="float-btn car-regi" @click="goPage('/vehiregi')" title="차량 등록">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M7 10h10M7 14h10M19 18l-14 0M19 6l-14 0" />
          <rect x="3" y="6" width="18" height="12" rx="2" />
        </svg>
        <span class="btn-label">차량등록</span>
      </button>

      <button v-show="isScrolled" class="float-btn top-move" @click="scrollToTop" title="위로 가기">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <polyline points="18 15 12 9 6 15"></polyline>
        </svg>
        <span class="btn-label">TOP</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import logoDark from '@/assets/txtlogo2.png'  // 어두운 로고 (txtlogo2)
import logoLight from '@/assets/txtlogo3.png' // 밝은색 로고 (txtlogo3)

const router = useRouter();
const route = useRoute();

const isScrolled = ref(false);

const isLogin = ref(false);
const loginName = ref('');
const loginInfo = ref(null);

const isMegamenuOpen = ref(false);
const isTransparentSection = ref(false);

const activeMenu = ref(''); // 현재 어떤 메뉴가 열려있는지 저장

const openDepth1 = ref(false);
const openDepth2 = ref(false);

// [추가] 검색어 상태 관리
const searchKeyword = ref('');

// [추가] 메가메뉴 전용 검색 함수
const onMenuSearch = () => {
  if (!searchKeyword.value.trim()) {
    alert("검색어를 입력해 주세요");
    return;
  }

  // 검색 페이지로 이동하면서 쿼리 파라미터(q)로 검색어 전달
  router.push({
    path: '/doctorsearch',
    query: { q: searchKeyword.value }
  });

  // 검색 후 메가메뉴 닫기 및 검색어 초기화
  isMegamenuOpen.value = false;
  searchKeyword.value = '';
};


// 메가메뉴 열기 함수
const openMegamenu = (menuName) => {
  activeMenu.value = menuName;
  isMegamenuOpen.value = true;
};

// 메가메뉴 닫기 함수
const closeMegamenu = () => {
  activeMenu.value = '';
  isMegamenuOpen.value = false;
};


// [추가] 현재 상태에 따른 로고 선택 로직
const logoSrc = computed(() => {
  // 1. 메인 페이지가 아니면 무조건 어두운 로고 (흰색 배경)
  if (!isMainPage.value) return logoDark;

  // 2. 메인 페이지일 때: 스크롤 되었거나 메가메뉴가 열렸으면 흰색 배경이므로 어두운 로고
  const isWhiteBg = (isScrolled.value && !isTransparentSection.value) || isMegamenuOpen.value;

  return isWhiteBg ? logoDark : logoLight;
});

// 로그인 필요한 서비스 + 요청 경로로 보내주기
const goPage = (path) => {
  const protectedPaths = ['/reservation', '/checkreservation', '/voc', '/mypage', '/vehiregi'];
  if (protectedPaths.includes(path) && !isLogin.value) {
    alert("로그인 후 이용 가능한 서비스입니다");
    isMegamenuOpen.value = false;
    // 쿼리 파라미터에 현재 가려던 주소(path)를 담기
    router.push({ path: '/login', query: { redirect: path } });
    return;
  }
  isMegamenuOpen.value = false;
  router.push(path);
}

const handleScroll = () => {
  if (!isMainPage.value) {
    isScrolled.value = window.scrollY > 50
  }
}

const handleMainScroll = (info) => {
  if (typeof info === 'object') {
    isScrolled.value = info.scrolled;
    // 섹션 1(메인) 혹은 섹션 3(건강이야기 - 배경색)일 때 투명 유지
    isTransparentSection.value = (info.section === 1 || info.section === 3);
  } else {
    isScrolled.value = info;
  }
}

const showVoc = computed(() => {
  if (!isLogin.value) return false;
  if (loginInfo.value && loginInfo.value.loginType === 'MED') return false;
  return true;
});

const siteMap = [
  {
    name: '병원소개',
    list: [
      { name: '인사말', path: '/greeting' },
      { name: '연혁', path: '/history' },
      { name: '미션/비전', path: '/mission' },
      { name: '오시는 길', path: '/location' },
      { name: '주차안내', path: '/parkinginfo' }
    ]
  },
  {
    name: '의료진/진료과',
    list: [
      { name: '의료진 찾기', path: '/doctorsearch' },
      { name: '진료과 찾기', path: '/deptsearch' },
      { name: '센터/클리닉', path: '/centerclinic' }
    ]
  },
  {
    name: '진료예약',
    list: [
      { name: '진료예약 신청', path: '/reservation' },
      { name: '예약내역 조회', path: '/checkreservation' },
      { name: '진료안내', path: '/guide' },
      { name: '진료절차', path: '/process' }
    ]
  },
  {
    name: '건강정보',
    list: [
      { name: '건강이야기', path: '/story' },
      { name: '질환백과', path: '/disease' },
      { name: '자가진단', path: '/checkup' }
    ]
  },
  {
    name: '고객서비스',
    list: [
      { name: '고객의 소리', path: '/voc' },
      { name: '공지사항', path: '/notice' },
      { name: 'FAQ', path: '/faq' },
      { name: '차량등록', path: '/vehiregi' }
    ]
  },
  {
    name: '마이페이지',
    list: [
      { name: '나의 정보', path: '/mypage' }
    ]
  }
];

const menuMap = {
  'doctorsearch': { parent: '의료진/진료과', current: '의료진 찾기' },
  'deptsearch': { parent: '의료진/진료과', current: '진료과 찾기' },
  'centerclinic': { parent: '의료진/진료과', current: '센터/클리닉' },
  'reservation': { parent: '진료예약', current: '진료예약 신청' },
  'checkreservation': { parent: '진료예약', current: '예약내역 조회' },
  'guide': { parent: '진료예약', current: '진료안내' },
  'process': { parent: '진료예약', current: '진료절차' },
  'notice': { parent: '고객서비스', current: '공지사항' },
  'faq': { parent: '고객서비스', current: 'FAQ' },
  'voc': { parent: '고객서비스', current: '고객의 소리' },
  'vehiregi': { parent: '고객서비스', current: '차량등록' },
  'login': { parent: '회원서비스', current: '로그인' },
  'regi': { parent: '회원서비스', current: '회원가입' },
  'mypage': { parent: '마이페이지', current: '나의 정보' },
  'location': { parent: '병원소개', current: '오시는 길' },
  'greeting': { parent: '병원소개', current: '인사말' },
  'history': { parent: '병원소개', current: '연혁' },
  'mission': { parent: '병원소개', current: '미션/비전' },
  'disease': { parent: '건강정보', current: '질환백과' },
  'checkup': { parent: '건강정보', current: '자가진단' },
  'stroy': { parent: '건강정보', current: '건강이야기' },
  'search': { parent: '통합검색', current: '검색결과' },
  'parking': { parent: 'HOME', current: '주차 이용 안내' }
};

const currentCategoryInfo = computed(() => {
  const currentPath = route.path;
  for (const group of siteMap) {
    const found = group.list.find(page => currentPath.startsWith(page.path) && page.path !== '/');
    if (found) return { categoryName: group.name, pages: group.list };
  }
  return { categoryName: '', pages: [] };
});

const currentMenuName = computed(() => {
  const foundInSiteMap = currentCategoryInfo.value.pages.find(p => route.path.startsWith(p.path));
  if (foundInSiteMap) return foundInSiteMap.name;
  const key = route.path.replace('/', '').split('/')[0];
  return menuMap[key]?.current || '페이지';
});

const checkLogin = () => {
  const loginData = sessionStorage.getItem('loginId')
  if (loginData) {
    try {
      const user = JSON.parse(loginData);
      isLogin.value = true;
      loginName.value = user.name || user.id;
      loginInfo.value = user;
    }
    catch (e) {
      isLogin.value = true;
      loginName.value = loginData;
    }
  } else {
    isLogin.value = false;
    loginInfo.value = null;
  }
}

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    sessionStorage.removeItem('loginId');
    isLogin.value = false;
    router.push('/');
  }
}

const isMainPage = computed(() => route.path === '/' || route.path === '/mainhome')

// 위로 가기 로직
const scrollToTop = () => {
  console.log("TOP 버튼 클릭. 강제 상단 이동 시작");

  // 실제 스크롤이 일어나는 박스
  const homeContainer = document.querySelector('.home-container');
  if (homeContainer) {
    homeContainer.scrollTo({
      top: 0,
      behavior: 'smooth' // 스르륵 올라가기
    });
  }

  // 혹시 일반 페이지(병원소개 등)일 경우를 대비한 보험 코드
  const contentBody = document.querySelector('.content-body');
  if (contentBody) {
    contentBody.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // 윈도우 자체 스크롤 (기본)
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

watch(() => route.path, () => { checkLogin(); isMegamenuOpen.value = false; isScrolled.value = false; })

onMounted(() => { checkLogin(); window.addEventListener('scroll', handleScroll); })
onUnmounted(() => { window.removeEventListener('scroll', handleScroll); })
</script>

<!-- <style scoped>

/* =====================================================================
   [1] 공통 및 레이아웃 (Layout & Global)
   ===================================================================== */
#home-layout {
  width: 100%;
  font-family: 'pretendard', sans-serif !important;
}
.content-body {
  min-height: 100vh;
}
.content-body.is-main-view {
  padding-top: 0;
}

/* =====================================================================
   [2] 메인 헤더 (Main Header)
   ===================================================================== */
.main-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100px;
  z-index: 5000;
  background: #fff;
  border-bottom: 1px solid #e5e5e5;
  transition: background 0.3s, border 0.3s;
}

/* 메인 페이지 투명 헤더 상태 */
.main-header.is-main:not(.is-scrolled) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

/* 스크롤 시 또는 메뉴 오픈 시 흰색 배경 고정 */
.main-header:has(.megamenu-panel.active),
.main-header.is-scrolled {
  background-color: #ffffff;
  border-bottom: 1px solid #e5e5e5;
}

/* 투명 헤더일 때 글자색 하얗게 */
.main-header.is-main:not(.is-scrolled):not(:has(.megamenu-panel.active)) .nav-item,
.main-header.is-main:not(.is-scrolled):not(:has(.megamenu-panel.active)) .util-item {
  color: #fff;
}

/* 헤더 내부 정렬 */
.header-inner {
  max-width: 1800px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* =====================================================================
   [3] 헤더 구성 요소 (Logo, Nav, Util)
   ===================================================================== */
/* 로고 */
.logo-box {
  flex: 0 0 240px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.main-logo-img {
  height: 60px;
  width: auto;
  padding-left: 30px;
  object-fit: contain;
}

/* 메인 메뉴 */
.nav-menu {
  display: flex;
  gap: 50px;
  justify-content: left;
  padding-left: 40px;
  flex: 1;
}

.nav-item {
  font-size: 19px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  line-height: 100px;
  transition: 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  color: #005baa;
}

/* 우측 유틸리티 (로그인/마이페이지) */
.combined-util {
  flex: 0 0 350px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 30px;
  padding-right: 30px;
}

.util-item {
  font-size: 16px;
  font-weight: 400;
  color: #666;
  cursor: pointer;
}

.logout-txt {
  color: #888;
}

.user-info {
  color: #fbb900;
}

/* =====================================================================
   [4] 메가메뉴 패널 (Megamenu Panel)
   ===================================================================== */
.megamenu-panel {
  position: absolute;
  top: 100px;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  z-index: 4900;
}

.megamenu-panel.active {
  max-height: 350px;
  visibility: visible;
}

.megamenu-inner {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0;
  background: #fff;
}

/* 메뉴 박스 공통 구조 */
.special-menu-box,
.full-menu-grid {
  display: flex;
  align-items: stretch;
  min-height: 350px;
}

/* =====================================================================
   [5] 메가메뉴 - 왼쪽 프로모션 구역
   ===================================================================== */
.promo-small-box,
.left-search-zone,
.left-info-zone {
  flex: 0 0 350px;
  background-color: #f8f9fb;
  padding: 0 55px;
  border-right: 1px solid #e5e5e5;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 2px;
}

/* 아이콘 원형 배경 */
.promo-icon-circle {
  width: 64px;
  height: 64px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 15px rgba(0, 91, 170, 0.08);
  margin-bottom: 5px;
}

/* 텍스트 타이틀 */
.st-title {
  font-size: 26px;
  font-weight: 700;
  color: #005baa;
  margin-bottom: 0;
}

.promo-small-box p b {
  font-size: 22px;
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.st-desc,
.promo-small-box p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 0;
}

/* 바로가기 버튼 (하단 여백 채우기용) */
.promo-go-btn {
  margin-top: 10px;
  padding: 10px 22px;
  background: transparent;
  border: 1px solid #005baa;
  color: #005baa;
  font-size: 14px;
  font-weight: 700;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.promo-go-btn:hover {
  background: #005baa;
  color: #fff;
  transform: translateX(5px);
}

/* 테마별 배경색 */
.promo-small-box.sky {
  background-color: #f0f7ff;
}

.promo-small-box.point {
  background-color: #fbb8001b;
}

/* =====================================================================
   [6] 메가메뉴 - 오른쪽 상세 리스트 및 아이콘 구역
   ===================================================================== */
/* 아이콘 구역 (의료진/진료과) */
.right-icon-zone {
  flex: 1;
  padding: 50px 60px;
  display: flex;
  gap: 80px;
  align-items: flex-start;
}

.st-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: 0.3s;
}

.circle-icon {
  width: 120px;
  height: 120px;
  background-color: #f2f2f2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  transition: 0.3s;
}

.st-icon-item:hover .circle-icon {
  border-color: #005baa;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 91, 170, 0.1);
}

.icon-txt .main-t {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.st-icon-item:hover .main-t {
  color: #005baa;
}

/* 리스트 구역 (예약/안내 등) */
.right-list-zone {
  display: flex;
  align-items: flex-start;
}

.menu-box-item {
  flex: 0 0 300px;
  display: flex;
  flex-direction: column;
  padding: 50px 40px;
}

.box-head {
  font-size: 18px;
  font-weight: 800;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #333;
  text-align: left;
}

.menu-box-item li {
  font-size: 16px;
  color: #555;
  margin-bottom: 14px;
  cursor: pointer;
  list-style: none;
  text-align: left;
  transition: 0.2s;
}

.menu-box-item li:hover {
  color: #005baa;
  text-decoration: underline;
  font-weight: 600;
}

/* 검색바 및 예약 카드 */
.st-search-bar {
  display: flex;
  height: 50px;
  border: 1px solid #005baa;
  overflow: hidden;
  width: 100%;
  margin-top: 15px;
}

.st-search-bar input {
  flex: 1;
  border: none;
  padding: 0 15px;
  outline: none;
  font-size: 16px;
}

.st-search-bar button {
  background: #005baa;
  color: #fff;
  border: none;
  padding: 0 25px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.reserve-guide-card {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 35px 30px;
  margin-top: 20px;
}

.guide-tag {
  font-size: 14px;
  font-weight: 700;
  color: #005baa;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.call-center-box .number {
  font-size: 30px;
  font-weight: 800;
  color: #333;
  line-height: 1;
}

.primary-rect-btn {
  width: 100%;
  height: 55px;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 15px;
}

.primary-rect-btn:hover {
  background-color: #004a8a;
  box-shadow: 0 5px 15px rgba(0, 91, 170, 0.2);
}

/* =====================================================================
   [7] 브레드크럼 (Breadcrumb Bar)
   ===================================================================== */
.breadcrumb-bar {
  width: 100%;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
  padding: 20px 35px;
  margin-top: 100px;
  padding-left: 78px;
}

.breadcrumb-inner {
  max-width: 1880px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  color: #666;
}

.home-icon,
.divider {
  color: #999;
  cursor: pointer;
}

.crumb-dropdown {
  position: relative;
  cursor: pointer;
}

.current-select {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #444;
}

.current-select.active {
  font-weight: 700;
  color: #005baa;
}

.dropdown-list {
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #ddd;
  min-width: 150px;
  z-index: 6000;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* =====================================================================
   [8] 푸터 
   ===================================================================== */
.main-footer {
  width: 100%;
  background-color: #f8f8f8;
  padding: 40px 0; /* 2번 파일 규격 */
  border-top: 1px solid #eee;
  font-family: 'pretendard', sans-serif;
}

.footer-inner {
  max-width: 1500px; /* 2번 파일의 wide-footer 가로폭 */
  margin: 0 auto;
  padding: 0 40px;   /* 미세 패딩 동기화 */
  text-align: left;
}

.f-row {
  display: flex;
  justify-content: space-between; /* 양 끝 정렬로 우측 요소 위치 고정 */
  align-items: center;
  margin-bottom: 9px;
}

.f-logo-img {
  filter: grayscale(1);
  height: 50px;
  width: auto;
  object-fit: contain;
  opacity: 0.5;
  transition: 0.3s;
}

.f-logo-img:hover {
  filter: grayscale(0);
  opacity: 1;
}

/* 우측 링크 구역 */
.f-links {
  font-size: 17px;
  color: #666;
  display: flex;
  gap: 15px;
}

.f-links .bold {
  font-weight: 700;
  color: #005baa;
}

/* 중간 정보 텍스트 구역 */
.f-info-text {
  font-size: 15px;
  margin-bottom: -10px; /* 2번 파일 특유의 쫀쫀한 간격 */
  color: #888;
  line-height: 1.2;
}

/* 우측 SNS 아이콘 구역 */
.f-sns-side {
  display: flex;
  gap: 10px;
}

.sns-link {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 50%;
  color: #777;
  transition: 0.3s;
}

.sns-link:hover {
  background-color: #005baa;
  color: #fff;
  border-color: #005baa;
  transform: translateY(-3px); /* 2번 파일의 호버 액션 이식 */
}

/* 하단 연락처 구역 */
.f-bottom-info {
  display: flex;
  justify-content: flex-start;
  font-size: 15px;
  color: #888;
  line-height: 1.2;
  margin-bottom: 30px; /* 하단 여백 확보 */
}

.blue-txt {
  color: #005baa;
  font-weight: 800;
}

/* 카피라이트 구역 */
.footer-copyright {
  font-size: 13px;
  color: #bbb;
  border-top: 1px solid #f2f2f2;
  padding-top: 20px; /* 패딩 동기화 */
}

.bar {
  margin: 0 5px;
  color: #ddd;
}

/* 플로팅 버튼 */
/* 플로팅 컨테이너 */
.floating-aside {
  position: fixed;
  right: 40px;
  bottom: 50px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 99999;
  pointer-events: auto; /* 마우스 이벤트를 확실히 받도록 설정 */
}

/* 플로팅 버튼 공통 스타일 */
.float-btn {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  border: none;
  background: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: #555;
}

.float-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

/* 차량 등록 버튼 특화 */
.car-regi {
  background: #005baa;
  color: #fff;
}

.btn-label {
  font-size: 13px;
  font-weight: 600;
  margin-top: 2px;
}

/* TOP 버튼 특화 */
.top-move {
  background: #333;
  color: #fff;
}

.floating-aside {
  transition: opacity 0.3s;
}
</style> -->

<style scoped>
/* =====================================================================
   [1] 공통 및 레이아웃 (Layout & Global)
   ===================================================================== */
#home-layout {
  width: 100%;
  font-family: 'pretendard', sans-serif !important;
}

.content-body {
  min-height: 100vh;
}

.content-body.is-main-view {
  padding-top: 0;
}

/* =====================================================================
   [2] 메인 헤더 및 상태 제어 (Main Header States)
   ===================================================================== */
.main-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100px;
  z-index: 5000;
  background: #fff;
  border-bottom: 1px solid #e5e5e5;
  transition: background 0.3s, border 0.3s;
}

/* 메인 페이지 투명 헤더 상태 */
.main-header.is-main:not(.is-scrolled) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

/* 스크롤 시 또는 메뉴 오픈 시 배경색 반전 */
.main-header:has(.megamenu-panel.active),
.main-header.is-scrolled {
  background-color: #ffffff;
  border-bottom: 1px solid #e5e5e5;
}

/* 투명 헤더일 때 텍스트 컬러 (White) */
.main-header.is-main:not(.is-scrolled):not(:has(.megamenu-panel.active)) .nav-item,
.main-header.is-main:not(.is-scrolled):not(:has(.megamenu-panel.active)) .util-item {
  color: #fff;
}

/* 헤더 내부 가로 정렬 컨테이너 (공주님 기준 1800px) */
.header-inner {
  max-width: 1800px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* =====================================================================
   [3] 헤더 구성 요소 (Logo, Navigation, Utility)
   ===================================================================== */
/* 로고 영역 */
.logo-box {
  flex: 0 0 240px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.main-logo-img {
  height: 60px;
  width: auto;
  object-fit: contain;
}

/* 내비게이션 메뉴 영역 */
.nav-menu {
  display: flex;
  gap: 50px;
  justify-content: left;
  padding-left: 40px;
  flex: 1;
}

.nav-item {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  line-height: 100px;
  transition: 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  color: #005baa;
}

/* 우측 유틸리티 영역 */
.combined-util {
  flex: 0 0 350px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 30px;
  padding-right: 30px;
}

.util-item {
  font-size: 18px;
  font-weight: 400;
  color: #666;
  cursor: pointer;
}

.logout-txt {
  color: #888;
}

.user-info {
  color: #fbb900;
}

/* =====================================================================
   [4] 메가메뉴 패널 설정 (Megamenu Panel)
   ===================================================================== */
.megamenu-panel {
  position: absolute;
  top: 100px;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  z-index: 4900;
}

.megamenu-panel.active {
  max-height: 350px;
  visibility: visible;
}

/* 메가메뉴 내부 수직 정렬을 내비바에 맞춤 (1800px) */
.megamenu-inner {
  max-width: 1800px;
  margin: 0 auto;
  padding: 0 20px;
  /* 헤더 inner와 동일한 패딩 부여 */
  background: #fff;
}

.special-menu-box,
.full-menu-grid {
  display: flex;
  align-items: stretch;
  min-height: 350px;
}

/* =====================================================================
   [5] 메가메뉴 내부 서브 섹션 (Promo & Search & Icon)
   ===================================================================== */
/* 왼쪽 프로모션 영역 */
.promo-small-box,
.left-search-zone,
.left-info-zone {
  flex: 0 0 350px;
  background-color: #f8f9fb;
  padding: 0 55px;
  border-right: 1px solid #e5e5e5;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 2px;
}

.promo-icon-circle {
  width: 64px;
  height: 64px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 15px rgba(0, 91, 170, 0.08);
  margin-bottom: 5px;
}

.st-title {
  font-size: 26px;
  font-weight: 700;
  color: #005baa;
}

.promo-small-box p b {
  font-size: 22px;
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.st-desc,
.promo-small-box p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.promo-go-btn {
  margin-top: 10px;
  padding: 10px 22px;
  background: transparent;
  border: 1px solid #005baa;
  color: #005baa;
  font-size: 14px;
  font-weight: 700;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.promo-go-btn:hover {
  background: #005baa;
  color: #fff;
  transform: translateX(5px);
}

.promo-small-box.sky {
  background-color: #f0f7ff;
}

.promo-small-box.point {
  background-color: #fbb8001b;
}

/* 오른쪽 아이콘/리스트 메뉴 영역 */
.right-icon-zone {
  flex: 1;
  padding: 50px 60px;
  display: flex;
  gap: 80px;
  align-items: flex-start;
}

.st-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: 0.3s;
}

.circle-icon {
  width: 120px;
  height: 120px;
  background-color: #f2f2f2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  transition: 0.3s;
}

.st-icon-item:hover .circle-icon {
  border-color: #005baa;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 91, 170, 0.1);
}

.icon-txt .main-t {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.st-icon-item:hover .main-t {
  color: #005baa;
}

.right-list-zone {
  display: flex;
  align-items: flex-start;
}

.menu-box-item {
  flex: 0 0 300px;
  display: flex;
  flex-direction: column;
  padding: 50px 40px;
}

.box-head {
  font-size: 20px;
  font-weight: 800;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #333;
  text-align: left;
}

.menu-box-item li {
  font-size: 18px;
  color: #555;
  margin-bottom: 14px;
  cursor: pointer;
  list-style: none;
  /* 점 제거 */
  text-align: left;
  transition: 0.2s;
}

.menu-box-item li:hover {
  color: #005baa;
  text-decoration: underline;
  font-weight: 600;
}

/* 검색바 및 예약 카드 관련 */
.st-search-bar {
  display: flex;
  height: 50px;
  border: 1px solid #005baa;
  overflow: hidden;
  width: 100%;
  margin-top: 15px;
}

.st-search-bar input {
  flex: 1;
  border: none;
  padding: 0 15px;
  outline: none;
  font-size: 16px;
}

.st-search-bar button {
  background: #005baa;
  color: #fff;
  border: none;
  padding: 0 25px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.reserve-guide-card {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 35px 30px;
  margin-top: 20px;
}

.guide-tag {
  font-size: 14px;
  font-weight: 700;
  color: #005baa;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.call-center-box .number {
  font-size: 30px;
  font-weight: 800;
  color: #333;
  line-height: 1;
}

.primary-rect-btn {
  width: 100%;
  height: 55px;
  background: #005baa;
  color: #fff;
  border: none;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 15px;
}

.primary-rect-btn:hover {
  background-color: #004a8a;
  box-shadow: 0 5px 15px rgba(0, 91, 170, 0.2);
}

/* =====================================================================
   [6] 브레드크럼 바 (Breadcrumb Section) 이미지 대응 정렬
   ===================================================================== */
.breadcrumb-bar {
  width: 100%;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
  padding: 20px 0;
  /* 상하 패딩만 남기고 좌측 강제 패딩 제거 */
  margin-top: 100px;
  /* padding-left: 78px; */
  /* 정렬을 방해하므로 주석 처리 */
}

.breadcrumb-inner {
  max-width: 1800px;
  /* 헤더 내비바와 동일하게 1800px로 수정 */
  margin: 0 auto;
  padding: 0 20px;
  /* 헤더와 동일한 패딩 부여 */
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  color: #666;
}

.home-icon,
.divider {
  color: #999;
  cursor: pointer;
}

/* 브레드크럼 드롭다운 및 리스트 기강 잡기 (이미지 4번 대응) */
.crumb-dropdown {
  position: relative;
  cursor: pointer;
}

.current-select {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #444;
}

.current-select.active {
  font-weight: 700;
  color: #005baa;
}

.dropdown-list {
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #ddd;
  min-width: 160px;
  z-index: 6000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  list-style: none;
  /* 불필요한 점(Bullet) 제거 */
  padding: 10px 0;
  margin: 0;
}

.dropdown-list li {
  padding: 10px 20px;
  font-size: 15px;
  color: #555;
  transition: 0.2s;
  list-style: none;
  /* 이중 방어 */
}

.dropdown-list li:hover {
  background-color: #f5f8ff;
  color: #005baa;
}

.dropdown-list li.on {
  font-weight: 700;
  color: #005baa;
}

/* =====================================================================
   [7] 메인 푸터 (Footer)
   ===================================================================== */
.main-footer {
  width: 100%;
  background-color: #f8f8f8;
  padding: 40px 0;
  border-top: 1px solid #eee;
  font-family: 'pretendard', sans-serif;
}

.footer-inner {
  max-width: 1500px;
  /* 푸터는 조금 좁은 것이 안정적 (원하시면 1800으로 변경 가능) */
  margin: 0 auto;
  padding: 0 40px;
  text-align: left;
}

.f-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 9px;
}

.f-logo-img {
  filter: grayscale(1);
  height: 50px;
  width: auto;
  object-fit: contain;
  opacity: 0.5;
  transition: 0.3s;
}

.f-logo-img:hover {
  filter: grayscale(0);
  opacity: 1;
}

.f-links {
  font-size: 17px;
  color: #666;
  display: flex;
  gap: 15px;
}

.f-links .bold {
  font-weight: 700;
  color: #005baa;
}

.f-info-text {
  font-size: 15px;
  color: #888;
  line-height: 1.2;
}

.f-sns-side {
  display: flex;
  gap: 10px;
}

.sns-link {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 50%;
  color: #777;
  transition: 0.3s;
}

.sns-link:hover {
  background-color: #005baa;
  color: #fff;
  border-color: #005baa;
  transform: translateY(-3px);
}

.f-bottom-info {
  display: flex;
  justify-content: flex-start;
  font-size: 15px;
  color: #888;
  line-height: 1.2;
  margin-bottom: 30px;
}

.blue-txt {
  color: #005baa;
  font-weight: 800;
}

.footer-copyright {
  font-size: 13px;
  color: #bbb;
  border-top: 1px solid #f2f2f2;
  padding-top: 20px;
}

.bar {
  margin: 0 5px;
  color: #ddd;
}

/* =====================================================================
   [8] 플로팅 버튼 및 탑 버튼 (Floating Aside)
   ===================================================================== */
.floating-aside {
  position: fixed;
  right: 40px;
  bottom: 50px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 99999;
}

.float-btn {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  border: none;
  background: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: #555;
}

.float-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.car-regi {
  background: #005baa;
  color: #fff;
}

.btn-label {
  font-size: 13px;
  font-weight: 600;
  margin-top: 2px;
}

.top-move {
  background: #333;
  color: #fff;
}

/* 중복 선언 주석 처리 */
/* .floating-aside { transition: opacity 0.3s; } */
</style>