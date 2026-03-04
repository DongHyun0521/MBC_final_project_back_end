<template>
  <div class="sub-page-container">
    <div class="section-title-area">
      <h3 class="sub-title">인사말</h3>
      <p class="slogan-sub">"인류의 건강한 미래를 선도하는 환자 중심의 스마트 의료 파트너"</p>
      <div class="accent-line"></div>
    </div>

    <div class="greeting-main">
      <div class="ceo-visual">
        <div class="img-frame">
          <img src="@/assets/병원장.jpg" alt="서울에스병원 병원장 김에스" class="ceo-img">
        </div>
        <div class="ceo-info-tag">
          <p class="dept">서울에스병원 병원장</p>
          <p class="name">김 에 스 <span>MD. Ph.D.</span></p>
        </div>
      </div>

      <div class="greeting-text">
        <h3 class="slogan-title">
          인류의 건강한 미래를 선도하는 <br>
          <span class="blue-point">환자 중심의 스마트 의료 파트너</span>
        </h3>
        
        <div class="message-body">
          <p class="lead">
            안녕하십니까. 서울에스병원 홈페이지를 방문해 주신 여러분을 진심으로 환영합니다.
          </p>
          <p>
            저희 서울에스병원은 1976년 개원 이래 <strong>'생명 존중'과 '인간 사랑'</strong>이라는 고귀한 가치를 실천하며, 
            지역 사회를 넘어 대한민국 의료의 표준을 제시하고자 끊임없이 노력해 왔습니다.
          </p>
          <p>
            단순히 질병을 치료하는 공간을 넘어, 환자의 아픔을 공감하고 마음까지 치유하는 <strong>'따뜻한 의료'</strong>를 실현하기 위해 
            전 의료진이 혼신의 힘을 다하고 있습니다. 첨단 스마트 시스템 도입을 통해 더욱 안전하고 정밀한 진료 환경을 구축하였으며, 
            세계적 수준의 연구 역량을 바탕으로 미래 의학의 새 길을 열어가고 있습니다.
          </p>
          <p>
            가장 신뢰받는 병원, 환자가 가장 먼저 떠올리는 병원이 될 것을 약속드리며, 
            여러분의 가정에 건강과 행복이 가득하시길 기원합니다. 감사합니다.
          </p>
        </div>

        <div class="signature-box">
          <div class="sign-txt">
            <span class="pos">서울에스병원 병원장</span>
            <span class="name-sign">김 에 스</span>
          </div>
          <div class="stamp">印</div>
        </div>
      </div>
    </div>

    <div class="philosophy-section">
      <div class="phi-item">
        <div class="phi-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <h4>환자 안전 최우선</h4>
        <p>철저한 감염 관리와 최첨단 모니터링 시스템으로 가장 안전한 진료 환경을 제공합니다.</p>
      </div>
      <div class="phi-item">
        <div class="phi-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 6v6l4 2"/>
          </svg>
        </div>
        <h4>신속한 정밀 진료</h4>
        <p>대학병원급 협진 시스템을 통해 당일 검사 및 판독이 가능한 One-Stop 서비스를 실현합니다.</p>
      </div>
      <div class="phi-item">
        <div class="phi-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
        </div>
        <h4>나눔과 상생 경영</h4>
        <p>수익의 일부를 지역 사회 소외 계층 의료 지원에 환원하며 사회적 책임을 다합니다.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ceo-img {
  width: 100%;
  height: 100%;
  object-fit: cover; 
  object-position: top;
}
/* [공통] Pretendard 서체 적용 및 초기화 */
.sub-page-container {
  width: 100%;
  font-family: 'Pretendard', sans-serif !important;
  color: #333;
  line-height: 1.6;
}

/* [헤더 섹션] */
.page-header-wrap {
  background-color: #f8f9fb;
  padding: 60px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 80px;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.breadcrumb {
  font-size: 14px;
  color: #999;
  margin-bottom: 15px;
}
.page-title {
  font-size: 42px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 20px;
}
.title-underline {
  width: 60px;
  height: 4px;
  background-color: #005baa;
}

/* [메인 컨텐츠 레이아웃] */
.greeting-main {
  max-width: 1200px;
  margin: 0 auto 40px;
  display: flex;
  gap: 80px;
  padding: 0 20px;
}

/* [비주얼 영역] */
.ceo-visual {
  flex: 0 0 420px;
}
.img-frame {
  width: 100%;
  height: 540px;
  background-color: #eee;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}
.placeholder-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #bbb;
}
.img-label {
  margin-top: 15px;
  font-size: 13px;
  letter-spacing: 2px;
  font-weight: 600;
}
.ceo-info-tag {
  margin-top: 25px;
  border-left: 4px solid #005baa;
  padding-left: 20px;
}
.ceo-info-tag .dept {
  font-size: 16px;
  color: #666;
  margin-bottom: 5px;
}
.ceo-info-tag .name {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a1a;
}
.ceo-info-tag .name span {
  font-size: 16px;
  font-weight: 400;
  color: #999;
  margin-left: 10px;
}

/* [텍스트 영역] */
.greeting-text {
  flex: 1;
}
.slogan-title {
  font-size: 34px;
  font-weight: 800;
  line-height: 1.4;
  color: #1a1a1a;
  margin-bottom: 40px;
}
.blue-point {
  color: #005baa;
}
.message-body p {
  font-size: 18px;
  color: #444;
  margin-bottom: 25px;
  text-align: justify;
  
}
.message-body .lead {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}
.message-body strong {
  color: #005baa;
  font-weight: 700;
}

/* [서인 영역] */
.signature-box {
  margin-top: 140px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}
.sign-txt {
  text-align: right;
}
.sign-txt .pos {
  display: block;
  font-size: 16px;
  color: #666;
  margin-bottom: 5px;
}
.sign-txt .name-sign {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 4px;
  color: #1a1a1a;
}
.stamp {
  width: 50px;
  height: 50px;
  border: 2px solid #dc3545;
  color: #dc3545;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 24px;
  border-radius: 50%;
  transform: rotate(15deg);
  opacity: 0.8;
}

/* [하단 경영철학 섹션] */
.philosophy-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 50px 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  border-top: 1px solid #eee;
}
.phi-item {
  text-align: center;
  padding: 40px 20px;
  background-color: #fcfcfc;
  transition: 0.3s;
}
.phi-item:hover {
  background-color: #fff;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
  transform: translateY(-5px);
}
.phi-icon {
  margin-bottom: 25px;
}
.phi-item h4 {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 15px;
}
.phi-item p {
  font-size: 15px;
  color: #777;
  line-height: 1.6;
}

.sub-page-container {
  max-width: 1200px; 
  margin: 0 auto;
  padding: 60px 20px 120px; /* 상단 여백 확보 */
  font-family: 'Pretendard', sans-serif !important;
  color: #333;
  line-height: 1.2;
}

/* 상단 타이틀 구역 정렬 보정 */
.section-title-area {
  margin-bottom: 70px;
  padding-left: 20px; 
  text-align: left;
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
</style>