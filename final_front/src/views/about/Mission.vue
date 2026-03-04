<template>
  <div class="sub-page-container">
    <div class="section-title-area">
      <h3 class="sub-title">미션 / 비전</h3>
      <p class="slogan-sub">"서울에스병원이 꿈꾸는 미래와 인류를 향한 고귀한 약속입니다."</p>
      <div class="accent-line"></div>
    </div>

    <section class="mv-block mission-section">
      <div class="block-label">MISSION</div>
      <div class="mission-banner">
        <div class="mission-inner">
          <h4 class="mission-title">인류를 질병으로부터 자유롭게 한다</h4>
          <p class="mission-desc">
            우리는 최상의 진료, 연구, 교육을 통해 <br>
            인류의 건강과 삶의 질 향상에 기여하기 위해 존재합니다.
          </p>
        </div>
        <div class="bg-pattern">
          <svg width="400" height="400" viewBox="0 0 100 100" opacity="0.1">
            <circle cx="50" cy="50" r="40" stroke="white" stroke-width="0.5" fill="none" />
            <path d="M50 10 L50 90 M10 50 L90 50" stroke="white" stroke-width="0.5" />
          </svg>
        </div>
      </div>
    </section>

    <section class="mv-block vision-section">
      <div class="block-label">VISION 2030</div>
      <h4 class="vision-main-title">스마트 의료의 표준을 제시하는 글로벌 리딩 병원</h4>
      
      <div class="vision-grid">
        <div class="v-card">
          <div class="v-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="8.5" cy="7" r="4" />
              <line x1="18" y1="8" x2="23" y2="13" />
              <line x1="23" y1="8" x2="18" y2="13" />
            </svg>
          </div>
          <h5>환자 중심의 치유 시스템</h5>
          <p>환자의 안전과 편의를 최우선으로 하며,<br>마음까지 어루만지는 전인적 치유를 실천합니다.</p>
        </div>

        <div class="v-card">
          <div class="v-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.2">
              <path d="M10 2v8L4.72 20.55a1 1 0 0 0 .9 1.45h12.76a1 1 0 0 0 .9-1.45L14 10V2" />
              <path d="M8 2h8M7 16h10" />
            </svg>
          </div>
          <h5>미래 의학의 연구 혁신</h5>
          <p>빅데이터와 AI를 활용한 끊임없는 연구로<br>난치성 질병 극복을 위한 해답을 제시합니다.</p>
        </div>

        <div class="v-card">
          <div class="v-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.2">
              <circle cx="12" cy="12" r="10" />
              <path d="M2 12h20M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
            </svg>
          </div>
          <h5>글로벌 의료 표준 선도</h5>
          <p>세계적 수준의 의료 역량을 바탕으로<br>지구촌 어디서나 신뢰받는 병원으로 도약합니다.</p>
        </div>
      </div>
    </section>

    <section class="mv-block core-section">
      <div class="block-label">CORE VALUES</div>
      <div class="values-container">
        <div class="value-tag">
          <span class="en">EXCELLENCE</span>
          <span class="ko">탁월성</span>
        </div>
        <div class="value-tag">
          <span class="en">RESPECT</span>
          <span class="ko">존중</span>
        </div>
        <div class="value-tag">
          <span class="en">INNOVATION</span>
          <span class="ko">혁신</span>
        </div>
        <div class="value-tag">
          <span class="en">TEAMWORK</span>
          <span class="ko">협력</span>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* [1] 공통 컨테이너 기강 잡기 (1800px) */
.sub-page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px 150px;
  font-family: 'Pretendard', sans-serif !important;
  color: #333;
}

/* [2] 상단 타이틀 영역 (다른 서브 페이지와 통일) */
.section-title-area {
  margin-bottom: 80px;
  text-align: left;
  padding-left: 20px;
}
.sub-title {
  font-size: 48px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 15px;
  letter-spacing: -1.5px;
}
.slogan-sub {
  font-size: 20px;
  color: #666;
  font-weight: 300;
  margin-bottom: 30px;
}
.accent-line {
  width: 60px;
  height: 5px;
  background-color: #005baa;
}

/* [3] 섹션 공통 스타일 */
.mv-block {
  margin-bottom: 120px;
  padding: 0 20px;
}
.block-label {
  font-size: 16px;
  font-weight: 800;
  color: #005baa;
  letter-spacing: 3px;
  margin-bottom: 30px;
}

/* MISSION 섹션 디자인 */
.mission-banner {
  background: linear-gradient(135deg, #005baa 0%, #003a7a 100%);
  border-radius: 8px;
  padding: 80px 60px;
  color: #fff;
  position: relative;
  overflow: hidden;
  text-align: center;
}
.mission-inner { position: relative; z-index: 2; }
.mission-title {
  font-size: 42px;
  font-weight: 800;
  margin-bottom: 25px;
  letter-spacing: -1px;
}
.mission-desc {
  font-size: 20px;
  font-weight: 300;
  line-height: 1.8;
  opacity: 0.9;
}
.bg-pattern {
  position: absolute;
  right: -50px;
  top: -50px;
  pointer-events: none;
}

/* VISION 섹션 디자인 */
.vision-main-title {
  font-size: 32px;
  font-weight: 800;
  color: #111;
  margin-bottom: 60px;
}
.vision-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}
.v-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 60px 40px;
  border-radius: 4px;
  transition: all 0.3s ease;
  text-align: left;
}
.v-card:hover {
  border-color: #005baa;
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0,0,0,0.06);
}
.v-icon { margin-bottom: 35px; }
.v-card h5 {
  font-size: 22px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 15px;
}
.v-card p {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

/* CORE VALUES 섹션 디자인 */
.values-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.value-tag {
  background-color: #f8f9fb;
  border: 1px solid #e5e5e5;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: 0.3s;
}
.value-tag:hover {
  background-color: #fff;
  border-color: #005baa;
}
.value-tag .en {
  font-size: 14px;
  font-weight: 800;
  color: #005baa;
  letter-spacing: 1px;
  margin-bottom: 10px;
}
.value-tag .ko {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

/* 반응형 배율 대응 */
@media (max-width: 1024px) {
  .vision-grid, .values-container { grid-template-columns: 1fr; }
  .mission-title { font-size: 32px; }
}
</style>