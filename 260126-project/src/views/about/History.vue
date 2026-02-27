<template>
  <div class="sub-page-container">
    <div class="section-title-area">
      <h3 class="sub-title">연혁</h3>
      <p class="slogan-sub">"서울에스병원이 걸어온 도전과 열정, 인류를 향한 치유의 기록입니다."</p>
      <div class="accent-line"></div>
    </div>

    <div class="history-content">
      <div class="history-summary">
        <div class="summary-card">
          <div class="s-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="1.5">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
          </div>
          <h4 class="s-title">Since 1976</h4>
          <p class="s-desc">
            반세기 동안 이어온 <br>
            끊임없는 혁신과 성장의 기록
          </p>
        </div>
      </div>

      <div class="timeline-wrap">
        <div v-for="(era, idx) in expandedHistory" :key="idx" class="era-group">
          <div class="era-header">
            <span class="era-range">{{ era.range }}</span>
            <h4 class="era-title">{{ era.title }}</h4>
          </div>

          <div class="timeline-items">
            <div v-for="(yearGroup, yIdx) in era.data" :key="yIdx" class="year-block">
              <div class="year-label">{{ yearGroup.year }}</div>
              <ul class="event-list">
                <li v-for="(event, eIdx) in yearGroup.events" :key="eIdx" class="event-item">
                  <span class="month">{{ event.month }}</span>
                  <div class="desc-box">
                    <p class="event-text">{{ event.text }}</p>
                    <p v-if="event.sub" class="event-sub">{{ event.sub }}</p>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 내용을 꽉 채우기 위한 확장 데이터
const expandedHistory = ref([
  {
    range: '2020 - 2026',
    title: '스마트 의료의 완성',
    data: [
      {
        year: '2026',
        events: [
          { month: '03', text: '서울에스병원 스마트 본관 그랜드 오픈', sub: '차세대 HIS 및 원격 협진 시스템 가동' },
          { month: '01', text: 'AI 기반 통합 관제 센터(Smart Command Center) 구축' }
        ]
      },
      {
        year: '2024',
        events: [
          { month: '11', text: '제4기 상급종합병원 재지정 (보건복지부)', sub: '환자 안전 및 의료 질 평가 최상위 등급 획득' },
          { month: '05', text: '로봇 수술 센터 누적 5,000례 달성' }
        ]
      }
    ]
  },
  {
    range: '2010 - 2019',
    title: '글로벌 의료 네트워크 도약',
    data: [
      {
        year: '2018',
        events: [
          { month: '09', text: '심혈관 질환 특성화 센터 개소', sub: '24시간 응급 심장 중재 시술 팀 운영' }
        ]
      },
      {
        year: '2012',
        events: [
          { month: '02', text: '서울에스병원 암센터 준공', sub: '최첨단 방사선 치료 장비(Linear Accelerator) 도입' }
        ]
      }
    ]
  },
  {
    range: '1976 - 2009',
    title: '인간 존중의 인술 실천',
    data: [
      {
        year: '1995',
        events: [
          { month: '05', text: '서울 S 의료재단 법인 전환 및 병원 증축' }
        ]
      },
      {
        year: '1976',
        events: [
          { month: '05', text: '에스 의원 개원', sub: '지역 사회 의료 기반 마련 및 봉사 정신 계승' }
        ]
      }
    ]
  }
]);
</script>

<style scoped>
/* [1] 전역 컨테이너 정렬 (1800px 동기화) */
.sub-page-container {
  max-width: 1200px; 
  margin: 0 auto;
  padding: 60px 20px 150px;
  font-family: 'Pretendard', sans-serif !important;
  color: #333;
}

/* [2] 상단 타이틀 영역 (인사말과 통일) */
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

/* [3] 메인 컨텐츠 레이아웃 */
.history-content {
  display: flex;
  gap: 100px;
  align-items: flex-start;
  padding: 0 20px;
}

/* 왼쪽 요약 카드 */
.history-summary {
  flex: 0 0 350px;
  position: sticky;
  top: 150px; /* 스크롤 시 따라오게 설정 */
}
.summary-card {
  background-color: #f8f9fb;
  padding: 50px 40px;
  border-radius: 4px;
  border-left: 6px solid #005baa;
}
.s-icon { margin-bottom: 25px; }
.s-title {
  font-size: 32px;
  font-weight: 800;
  color: #005baa;
  margin-bottom: 15px;
}
.s-desc {
  font-size: 18px;
  color: #444;
  line-height: 1.6;
  font-weight: 500;
}

/* [4] 타임라인 영역 */
.timeline-wrap {
  flex: 1;
  position: relative;
}

/* 시대별 그룹 */
.era-group {
  margin-bottom: 100px;
}
.era-header {
  border-bottom: 2px solid #111;
  padding-bottom: 20px;
  margin-bottom: 50px;
}
.era-range {
  font-size: 16px;
  color: #005baa;
  font-weight: 700;
  display: block;
  margin-bottom: 10px;
}
.era-title {
  font-size: 30px;
  font-weight: 800;
  color: #1a1a1a;
}

/* 연도별 블록 */
.year-block {
  display: flex;
  margin-bottom: 60px;
  position: relative;
}
.year-block:last-child { margin-bottom: 0; }

.year-label {
  flex: 0 0 150px;
  font-size: 28px;
  font-weight: 800;
  color: #1a1a1a;
  padding-top: 5px;
}

/* 이벤트 리스트 */
.event-list {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  border-left: 1px solid #ddd;
  padding-left: 40px;
}

.event-item {
  display: flex;
  margin-bottom: 30px;
  position: relative;
}
.event-item::before {
  content: "";
  position: absolute;
  left: -44.5px;
  top: 10px;
  width: 8px;
  height: 8px;
  background-color: #fff;
  border: 2px solid #005baa;
  border-radius: 50%;
}
.event-item:last-child { margin-bottom: 0; }

.month {
  font-size: 18px;
  font-weight: 700;
  color: #005baa;
  margin-right: 25px;
  padding-top: 2px;
}
.desc-box { flex: 1; }
.event-text {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}
.event-sub {
  font-size: 16px;
  color: #777;
  line-height: 1.5;
}

/* 반응형 배율 대응 */
@media (max-width: 1200px) {
  .history-content { flex-direction: column; gap: 60px; }
  .history-summary { position: static; width: 100%; }
}
</style>