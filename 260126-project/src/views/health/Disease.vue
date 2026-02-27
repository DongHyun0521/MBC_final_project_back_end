<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>질환백과</h2>
      <p>정확한 질환 정보로 건강한 삶을 지원합니다</p>
    </div>

    <div class="search-bar-wrap">
      <input type="text" placeholder="질환명을 입력하세요" class="search-input">
      <button class="search-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
      </button>
    </div>

    <div class="alphabet-index">
      <button v-for="char in indexList" :key="char" class="idx-item">{{ char }}</button>
    </div>

    <div class="disease-grid">
      <div class="disease-card" v-for="disease in diseaseData" :key="disease.name">
        <div class="disease-icon" v-html="disease.icon"></div>
        <p class="disease-name">{{ disease.name }}</p>
        <span class="view-more">자세히 보기</span>
      </div>
    </div>

    <section class="notice-section">
      <div class="notice-box">
        <h4>질환백과 이용 안내</h4>
        <ul class="notice-list">
          <li>본 정보는 의학적 참고 자료이며, 정확한 진단은 반드시 내원하여 전문의와 상담하시기 바랍니다</li>
          <li>긴급한 증상이 발생한 경우 응급의료센터(02-1234-5678)로 즉시 연락해 주세요</li>
          <li>최신 의학 정보를 바탕으로 정기적으로 업데이트됩니다</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<script setup>
const indexList = ['ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ', 'A-Z'];

// 질환별 맞춤 SVG 아이콘 데이터
const diseaseData = [
  {
    name: '고혈압',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path><polyline points="12 11 12 16"></polyline><polyline points="9 14 15 14"></polyline></svg>`
  },
  {
    name: '당뇨병',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M12 2v20M2 12h20"></path><path d="M12 22a7 7 0 1 0 0-14 7 7 0 0 0 0 14z"></path></svg>`
  },
  {
    name: '위염',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M12 2c-4 0-7 3-7 7 0 5 7 13 7 13s7-8 7-13c0-4-3-7-7-7z"></path><circle cx="12" cy="9" r="3"></circle></svg>`
  },
  {
    name: '아토피',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M11 20H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v7"></path><path d="M16 20l2 2 4-4"></path><circle cx="9" cy="9" r="2"></circle><circle cx="15" cy="15" r="2"></circle></svg>`
  },
  {
    name: '백내장',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>`
  },
  {
    name: '공황장애',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><path d="M12 8v8"></path><path d="M8 12h8"></path></svg>`
  },
  {
    name: '허리디스크',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><rect x="8" y="2" width="8" height="4"></rect><rect x="8" y="10" width="8" height="4"></rect><rect x="8" y="18" width="8" height="4"></rect><path d="M12 6v4M12 14v4"></path></svg>`
  },
  {
    name: '협심증',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>`
  },
  {
    name: '알레르기 비염',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M7 10c.6-3 1.5-6 5-6s4.4 3 5 6c.5 2.5 1 5-2.5 6h-5c-3.5-1-3-3.5-2.5-6z"></path><path d="M12 16v5"></path></svg>`
  },
  {
    name: '기관지 천식',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M7 3c2-1 4-1 6 0 3 1 5 3 5 7 0 4-2 7-5 8-2 1-4 1-6 0-3-1-5-4-5-8 0-4 2-6 5-7z"></path><path d="M12 13v-3"></path><path d="M10 11h4"></path></svg>`
  },
  {
    name: 'A형 간염',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M12 21c-5-1-9-5-9-10V5l9-3 9 3v6c0 5-4 9-9 10z"></path><path d="M12 8v4"></path><path d="M12 16h.01"></path></svg>`
  },
  {
    name: '골다공증',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M18 3c-1 0-3 .5-3 2v14c0 1.5 2 2 3 2s3-.5 3-2V5c0-1.5-2-2-3-2z"></path><path d="M6 3c1 0 3 .5 3 2v14c0 1.5-2 2-3 2s-3-.5-3-2V5c0-1.5 2-2 3-2z"></path></svg>`
  }
];
</script>

<style scoped>
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif;
}

/* 타이틀 42px / 18px */
.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h2 {
  font-size: 42px;
  font-weight: 800;
  color: #111;
  margin-bottom: 15px;
  letter-spacing: -1px;
}

.page-header p {
  font-size: 18px;
  color: #666;
}

/* 검색바 (직선형) */
.search-bar-wrap {
  max-width: 700px;
  margin: 0 auto 50px;
  display: flex;
  border: 2px solid #005baa;
  border-radius: 0;
  overflow: hidden;
}

.search-input {
  flex: 1;
  padding: 18px 25px;
  border: none;
  outline: none;
  font-size: 17px;
}

.search-btn {
  background: #005baa;
  border: none;
  padding: 0 30px;
  color: #fff;
  cursor: pointer;
}

.search-btn svg {
  width: 24px;
  height: 24px;
}

/* 인덱스 (직선형) */
.alphabet-index {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 60px;
}

.idx-item {
  padding: 10px 18px;
  border: 1px solid #ddd;
  font-size: 15px;
  background: #fff;
  border-radius: 0;
  cursor: pointer;
  transition: 0.2s;
}

.idx-item:hover {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

/* 질환 그리드 (직선형 카드) */
.disease-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
  margin-bottom: 80px;
}

.disease-card {
  border: 1px solid #eee;
  padding: 40px 20px;
  text-align: center;
  border-radius: 0;
  background: #fff;
  transition: 0.3s;
}

.disease-card:hover {
  border-color: #005baa;
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}

.disease-icon {
  width: 50px;
  height: 50px;
  margin: 0 auto 20px;
}

.disease-icon :deep(svg) {
  width: 100%;
  height: 100%;
}

.disease-name {
  font-size: 19px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.view-more {
  font-size: 14px;
  color: #005baa;
  border-bottom: 1px solid #005baa;
  padding-bottom: 2px;
}

/* 주의사항 (직선형) */
.notice-section {
  border: 1px solid #eee;
}

.notice-box {
  padding: 35px;
  background: #f8f9fa;
  border-left: 6px solid #005baa;
}

.notice-box h4 {
  font-size: 20px;
  color: #111;
  margin-bottom: 20px;
  font-weight: 700;
}

.notice-list {
  padding-left: 20px;
  margin: 0;
}

.notice-list li {
  font-size: 16px;
  color: #666;
  margin-bottom: 12px;
  list-style-type: square;
}

@media (max-width: 1024px) {
  .disease-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .disease-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>