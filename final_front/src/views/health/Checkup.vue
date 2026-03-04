<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>자가진단</h2>
      <p>간단한 체크리스트를 통해 본인의 건강 상태를 확인해 보세요</p>
    </div>

    <div class="checkup-grid">
      <div class="check-card" v-for="item in checkupList" :key="item.title">
        <div class="check-top">
          <span class="category">{{ item.category }}</span>
          <div class="icon-wrap" v-html="item.icon"></div>
        </div>
        <h4>{{ item.title }}</h4>
        <p>{{ item.desc }}</p>
        <button class="start-btn">진단 시작하기</button>
      </div>
    </div>

    <section class="notice-section">
      <div class="notice-box">
        <h4>자가진단 이용 시 주의사항</h4>
        <ul class="notice-list">
          <li>본 서비스는 일반적인 증상을 바탕으로 한 간이 검사이며, 의학적 진단을 대신할 수 없습니다</li>
          <li>정확한 상태 확인을 위해서는 반드시 해당 진료과 전문의의 상담과 정밀 검사가 필요합니다</li>
          <li>진단 결과는 입력하신 답변을 바탕으로 산출되며, 개인의 컨디션에 따라 차이가 있을 수 있습니다</li>
          <li>심각한 통증이나 증상이 지속될 경우 즉시 병원을 방문해 주시기 바랍니다</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<script setup>
// 각 질환별 맞춤 SVG 포함 데이터
const checkupList = [
  {
    category: '마음건강',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z"></path><path d="M8 14s1.5 2 4 2 4-2 4-2"></path><line x1="9" y1="9" x2="9.01" y2="9"></line><line x1="15" y1="9" x2="15.01" y2="9"></line></svg>`,
    title: '우울증 자가진단',
    desc: '최근 마음이 답답하고 무기력하신가요? 마음의 상태를 들여다보세요'
  },
  {
    category: '눈건강',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle><path d="M12 17v4M12 3v4"></path></svg>`,
    title: '안구건조증 체크',
    desc: '눈이 자주 침침하고 이물감이 느껴진다면 안구 건조 상태를 확인하세요'
  },
  {
    category: '생활습관',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M18 12H2v4h16v-4z"></path><path d="M22 12v4"></path><path d="M18 12h4v4h-4"></path><line x1="14" y1="12" x2="14" y2="16"></line></svg>`,
    title: '금연 성공 가능성',
    desc: '나의 흡연 의존도는 얼마나 될까요? 건강한 금연 계획을 세워보세요'
  },
  {
    category: '관절건강',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5"><path d="M7 11V7a5 5 0 0 1 10 0v4"></path><rect x="5" y="11" width="14" height="10" rx="0"></rect><path d="M12 15v3"></path></svg>`,
    title: '손목터널증후군',
    desc: '손가락 끝이 저릿하고 통증이 있다면 손목 건강 신호를 체크해 보세요'
  }
];
</script>

<style scoped>
/* 전체 기본 설정 (Pretendard & No Curve) */
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif;
}

/* 타이틀 42px / 18px */
.page-header {
  text-align: center;
  margin-bottom: 70px;
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
  font-weight: 400;
}

/* 자가진단 그리드 */
.checkup-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin-bottom: 80px;
}

/* 카드 디자인 (직각형) */
.check-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 45px;
  border-radius: 0;
  /* 곡선 제거 */
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.check-card:hover {
  border-color: #005baa;
  box-shadow: 0 10px 30px rgba(0, 91, 170, 0.08);
  transform: translateY(-5px);
}

.check-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 25px;
}

/* 카테고리 태그 (직각형) */
.category {
  font-size: 14px;
  color: #005baa;
  font-weight: 700;
  background: #f0f7ff;
  padding: 6px 14px;
  border-radius: 0;
  /* 곡선 제거 */
}

/* SVG 아이콘 */
.icon-wrap {
  width: 45px;
  height: 45px;
}

.icon-wrap :deep(svg) {
  width: 100%;
  height: 100%;
}

h4 {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 15px;
  color: #222;
}

p {
  color: #777;
  font-size: 16px;
  margin-bottom: 40px;
  line-height: 1.6;
  flex-grow: 1;
}

/* 시작 버튼 (직각형) */
.start-btn {
  width: 100%;
  padding: 18px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 0;
  /* 곡선 제거 */
  font-weight: 700;
  font-size: 16px;
  color: #555;
  cursor: pointer;
  transition: 0.2s;
  font-family: 'Pretendard';
}

.start-btn:hover {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

/* 하단 주의사항 (직선형) */
.notice-section {
  border: 1px solid #eee;
}

.notice-box {
  padding: 40px;
  background: #f8f9fa;
  border-left: 6px solid #005baa;
}

.notice-box h4 {
  font-size: 20px;
  color: #111;
  margin-bottom: 20px;
  font-weight: 700;
  border-bottom: none;
}

.notice-list {
  padding-left: 20px;
  margin: 0;
}

.notice-list li {
  font-size: 15px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.6;
  list-style-type: square;
}

/* 반응형 */
@media (max-width: 768px) {
  .page-header h2 {
    font-size: 32px;
  }

  .checkup-grid {
    grid-template-columns: 1fr;
  }

  .check-card {
    padding: 30px;
  }
}
</style>