<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>센터/클리닉</h2>
      <p>S-HOSPITAL의 전문 센터와 클리닉을 소개합니다.</p>
    </div>

    <section class="group-sec">
      <h3 class="sec-title">전문 센터</h3>
      <div class="grid-box">
        <div class="item-card" v-for="center in centerList" :key="center">{{ center }}</div>
      </div>
    </section>

    <section class="group-sec">
      <h3 class="sec-title">특수 클리닉</h3>
      <div class="grid-box">
        <div class="item-card" v-for="clinic in clinicList" :key="clinic">{{ clinic }}</div>
      </div>
    </section>
  </div>
</template>

<script setup>
const centerList = [
  '암센터', '심혈관센터', '뇌신경센터', '소화기병센터', 
  '척추센터', '관절센터', '재활센터', '응급의료센터', 
  '건강증진센터', '국제진료센터', '여성의학센터', '소아청소년센터'
];

const clinicList = [
  '통증클리닉', '비만클리닉', '수면클리닉', '금연클리닉',
  '성장클리닉', '난임클리닉', '스트레스클리닉', '노화방지클리닉'
];
</script>

<style scoped>
.sub-page-container { max-width: 1100px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 60px; }
.page-header h2 { font-size: 32px; font-weight: 700; color: #111; margin-bottom: 10px; }
.page-header p { font-size: 16px; color: #666; }

.group-sec { margin-bottom: 80px; }
.sec-title { 
  font-size: 22px; font-weight: 700; color: #005baa; 
  border-left: 4px solid #005baa; padding-left: 15px; margin-bottom: 20px; 
}

.grid-box { 
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; 
}

.item-card { 
  border: 1px solid #ddd; background: #fff; padding: 20px; 
  text-align: center; border-radius: 6px; font-weight: 600; color: #444; 
  cursor: pointer; transition: 0.2s;
}

.item-card:hover { 
  border-color: #005baa; color: #005baa; box-shadow: 0 5px 15px rgba(1, 113, 233, 0.1); transform: translateY(-3px); 
}
</style>