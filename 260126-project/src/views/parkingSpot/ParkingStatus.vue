<template>
  <div class="parking-status">
    <!-- 더미입차 버튼 -->
    <div class="dummy-bar">
      <button class="btn bp" @click="handleDummyPark" :disabled="loading">🚗 더미 입차</button>
      <button class="btn br" v-if="devMode" @click="handleExitAll" :disabled="loading">🔄 전체 출차</button>
    </div>

    <!-- 범례 + 층버튼 -->
    <div class="control-row">
      <div class="legend">
        <div class="legend-item"><div class="dot avail"></div><span>주차 가능</span></div>
        <div class="legend-item"><div class="dot occupied"></div><span>주차 중</span></div>
        <div class="legend-item"><div class="dot recommend"></div><span>추천 위치</span></div>
        <div class="legend-item"><div class="dot highlight"></div><span>검색 결과</span></div>
      </div>
      <div class="floor-btns">
        <button v-for="f in 3" :key="f" :class="{ active: floor === f }" @click="changeFloor(f)">B{{ f }}</button>
      </div>
    </div>

    <!-- SVG 맵 -->
    <svg class="parking-map" :viewBox="`0 0 ${SVG.width} ${SVG.height}`" preserveAspectRatio="xMidYMid meet">
      <!-- 배경 -->
      <rect x="0" y="0" :width="SVG.width" :height="SVG.height" fill="#eeeeee" rx="12"/>
      <rect x="10" y="10" :width="SVG.width-20" :height="SVG.height-20" fill="none" stroke="#1a1a1a" stroke-width="3" rx="10"/>

      <!-- 입구 (왼쪽, A와 간격) -->
      <rect x="15" y="15" width="55" height="50" fill="#fff" stroke="#1a1a1a" stroke-width="2" rx="5"/>
      <text x="42" y="45" text-anchor="middle" font-size="11" font-weight="600">{{ topLabel }}</text>

      <!-- 층이동 (왼쪽, D와 간격) -->
      <template v-if="bottomLabel">
        <rect x="15" y="538" width="55" height="50" fill="#fff" stroke="#1a1a1a" stroke-width="2" rx="5"/>
        <text x="42" y="568" text-anchor="middle" font-size="11" font-weight="600">{{ bottomLabel }}</text>
      </template>

      <!-- 엘베 상단 -->
      <rect :x="Z.elev.top.x" :y="Z.elev.top.y" :width="Z.elev.w" :height="Z.elev.h"
        fill="#d0d0d0" stroke="#1a1a1a" stroke-width="2" rx="4"/>
      <text :x="Z.elev.top.x+Z.elev.w/2" :y="Z.elev.top.y+16" text-anchor="middle" font-size="9" font-weight="600">건물입구</text>
      <text :x="Z.elev.top.x+Z.elev.w/2" :y="Z.elev.top.y+30" text-anchor="middle" font-size="9" font-weight="600">(엘베)</text>

      <!-- 엘베 하단 -->
      <rect :x="Z.elev.bottom.x" :y="Z.elev.bottom.y" :width="Z.elev.w" :height="Z.elev.h"
        fill="#d0d0d0" stroke="#1a1a1a" stroke-width="2" rx="4"/>
      <text :x="Z.elev.bottom.x+Z.elev.w/2" :y="Z.elev.bottom.y+16" text-anchor="middle" font-size="9" font-weight="600">건물입구</text>
      <text :x="Z.elev.bottom.x+Z.elev.w/2" :y="Z.elev.bottom.y+30" text-anchor="middle" font-size="9" font-weight="600">(엘베)</text>

      <!-- A구역 -->
      <template v-for="i in 8" :key="'A'+i">
        <rect :x="getX('A',i)" :y="Z.A.sy" :width="Z.A.w" :height="Z.A.h"
          :fill="color('A',i)" :stroke="stk('A',i)" :stroke-width="sw('A',i)" rx="3" class="spot" @click="clickSpot('A',i)"/>
        <text :x="getX('A',i)+Z.A.w/2" :y="Z.A.sy+Z.A.h/2+4" text-anchor="middle" font-size="10" font-weight="600" :fill="textC('A',i)" style="pointer-events:none">A{{ i }}</text>
      </template>

      <!-- B구역 + 기둥 -->
      <template v-for="(p,i) in getPillarsBC('B')" :key="'pB'+i">
        <rect :x="p.x" :y="Z.B.sy" :width="p.w" :height="Z.B.h" fill="#555" stroke="#333" stroke-width="1"/>
        <rect :x="p.x" :y="Z.B.sy+Z.B.h+Z.B.rg" :width="p.w" :height="Z.B.h" fill="#555" stroke="#333" stroke-width="1"/>
      </template>
      <template v-for="i in 8" :key="'B'+i">
        <rect :x="getX('B',i)" :y="Z.B.sy" :width="Z.B.w" :height="Z.B.h"
          :fill="color('B',i)" :stroke="stk('B',i)" :stroke-width="sw('B',i)" rx="3" class="spot" @click="clickSpot('B',i)"/>
        <text :x="getX('B',i)+Z.B.w/2" :y="Z.B.sy+Z.B.h/2+4" text-anchor="middle" font-size="9" font-weight="600" :fill="textC('B',i)" style="pointer-events:none">B{{ i }}</text>
      </template>
      <template v-for="i in 8" :key="'B2-'+i">
        <rect :x="getX('B',i)" :y="Z.B.sy+Z.B.h+Z.B.rg" :width="Z.B.w" :height="Z.B.h"
          :fill="color('B',i+8)" :stroke="stk('B',i+8)" :stroke-width="sw('B',i+8)" rx="3" class="spot" @click="clickSpot('B',i+8)"/>
        <text :x="getX('B',i)+Z.B.w/2" :y="Z.B.sy+Z.B.h+Z.B.rg+Z.B.h/2+4" text-anchor="middle" font-size="9" font-weight="600" :fill="textC('B',i+8)" style="pointer-events:none">B{{ i+8 }}</text>
      </template>

      <!-- C구역 + 기둥 -->
      <template v-for="(p,i) in getPillarsBC('C')" :key="'pC'+i">
        <rect :x="p.x" :y="Z.C.sy" :width="p.w" :height="Z.C.h" fill="#555" stroke="#333" stroke-width="1"/>
        <rect :x="p.x" :y="Z.C.sy+Z.C.h+Z.C.rg" :width="p.w" :height="Z.C.h" fill="#555" stroke="#333" stroke-width="1"/>
      </template>
      <template v-for="i in 8" :key="'C'+i">
        <rect :x="getX('C',i)" :y="Z.C.sy" :width="Z.C.w" :height="Z.C.h"
          :fill="color('C',i)" :stroke="stk('C',i)" :stroke-width="sw('C',i)" rx="3" class="spot" @click="clickSpot('C',i)"/>
        <text :x="getX('C',i)+Z.C.w/2" :y="Z.C.sy+Z.C.h/2+4" text-anchor="middle" font-size="9" font-weight="600" :fill="textC('C',i)" style="pointer-events:none">C{{ i }}</text>
      </template>
      <template v-for="i in 8" :key="'C2-'+i">
        <rect :x="getX('C',i)" :y="Z.C.sy+Z.C.h+Z.C.rg" :width="Z.C.w" :height="Z.C.h"
          :fill="color('C',i+8)" :stroke="stk('C',i+8)" :stroke-width="sw('C',i+8)" rx="3" class="spot" @click="clickSpot('C',i+8)"/>
        <text :x="getX('C',i)+Z.C.w/2" :y="Z.C.sy+Z.C.h+Z.C.rg+Z.C.h/2+4" text-anchor="middle" font-size="9" font-weight="600" :fill="textC('C',i+8)" style="pointer-events:none">C{{ i+8 }}</text>
      </template>

      <!-- D구역 -->
      <template v-for="i in 8" :key="'D'+i">
        <rect :x="getX('D',i)" :y="Z.D.sy" :width="Z.D.w" :height="Z.D.h"
          :fill="color('D',i)" :stroke="stk('D',i)" :stroke-width="sw('D',i)" rx="3" class="spot" @click="clickSpot('D',i)"/>
        <text :x="getX('D',i)+Z.D.w/2" :y="Z.D.sy+Z.D.h/2+4" text-anchor="middle" font-size="10" font-weight="600" :fill="textC('D',i)" style="pointer-events:none">D{{ i }}</text>
      </template>

      <!-- E구역 + 기둥 (E3-E4 사이) -->
      <template v-for="(p,i) in getPillarsE()" :key="'pE'+i">
        <rect :x="p.x" :y="p.y" :width="p.w" :height="p.h" fill="#555" stroke="#333" stroke-width="1"/>
      </template>
      <template v-for="i in 6" :key="'E'+i">
        <rect :x="getX('E',i)" :y="getY('E',i)" :width="Z.E.w" :height="Z.E.h"
          :fill="color('E',i)" :stroke="stk('E',i)" :stroke-width="sw('E',i)" rx="3" class="spot" @click="clickSpot('E',i)"/>
        <text :x="getX('E',i)+Z.E.w/2" :y="getY('E',i)+Z.E.h/2+4" text-anchor="middle" font-size="10" font-weight="600" :fill="textC('E',i)" style="pointer-events:none">E{{ i }}</text>
      </template>

      <!-- 화살표 -->
      <defs>
        <marker id="ah" markerWidth="7" markerHeight="5" refX="6" refY="2.5" orient="auto">
          <polygon points="0 0, 7 2.5, 0 5" fill="#34C759"/>
        </marker>
      </defs>
      <path v-for="(ap, idx) in arrowPaths" :key="'arrow'+idx" :d="ap" stroke="#34C759" stroke-width="3" fill="none" marker-end="url(#ah)" stroke-dasharray="8 4"/>

      <!-- 만차 -->
      <text v-if="floorFullMsg" x="350" y="300" text-anchor="middle" font-size="16" font-weight="700" fill="#FF3B30">{{ floorFullMsg }}</text>
    </svg>

    <!-- 현황 카드 -->
    <div class="status-row">
      <div class="card cur"><div class="card-label">B{{ floor }}층</div><div class="card-value"><span class="g">{{ floorAvailable }}</span><span class="d">/</span><span>{{ floorTotal }}</span></div></div>
      <div class="card tot"><div class="card-label">전체</div><div class="card-value"><span class="g">{{ totalAvailable }}</span><span class="d">/</span><span>{{ totalAll }}</span></div></div>
    </div>

    <!-- 모달 -->
    <div class="modal-overlay" v-if="showModal" @click="closeModal">
      <div class="modal-body" @click.stop>
        <h3>{{ modalSpot?.zone }}{{ modalSpot?.spotNumber }}</h3>
        <div v-if="modalSpot?.isParked" class="modal-info">
          <p><strong>상태:</strong> 주차 중</p>
          <p><strong>차량:</strong> {{ modalSpot?.vehicleNum || '정보 없음' }}</p>
          <button class="btn-exit" @click="handleExit">출차</button>
        </div>
        <div v-else class="modal-info"><p>주차 가능 🅿️</p></div>
        <button class="btn-close" @click="closeModal">닫기</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated, watch } from 'vue';
import { useRoute } from 'vue-router';
import { SVG, ZONE, AISLE, spotX, spotY, buildArrowPath, buildSearchArrowPaths, pillarsBC, pillarsE, generateDummySpots } from '@/parking_utils/parkingCoords';
import api from '@/api/parkingApi';

export default {
  name: 'ParkingStatus',
  setup() {
    const route = useRoute();
    const floor = ref(1);
    const allSpots = ref([]);
    const loading = ref(false);
    const showModal = ref(false);
    const modalSpot = ref(null);
    const highlightSpot = ref(null);
    const devMode = computed(() => route.query.dev === 'true');

    if (route.query.hl) { try { highlightSpot.value = JSON.parse(route.query.hl); } catch {} }

    const topLabel = computed(() => floor.value === 1 ? '입구' : `B${floor.value - 1}↑`);
    const bottomLabel = computed(() => floor.value === 3 ? null : `B${floor.value + 1}↓`);

    const loadSpots = async () => {
      loading.value = true;
      try {
        const res = await api.getAllSpots();
        allSpots.value = Array.isArray(res?.data ?? res) ? (res?.data ?? res) : [];
      } catch { allSpots.value = generateDummySpots(); }
      finally { loading.value = false; }
    };

    onMounted(() => { loadSpots(); if (highlightSpot.value) floor.value = highlightSpot.value.floor; });
    onActivated(() => {
      if (route.query.hl) { try { highlightSpot.value = JSON.parse(route.query.hl); floor.value = highlightSpot.value.floor; } catch {} }
      else { highlightSpot.value = null; }
      loadSpots();
    });
    watch(() => route.query.hl, (v) => { if (v) { try { highlightSpot.value = JSON.parse(v); floor.value = highlightSpot.value.floor; } catch {} } else { highlightSpot.value = null; } });

    const floorSpots = computed(() => allSpots.value.filter(s => s.floor === floor.value));
    const findSpot = (z, n) => floorSpots.value.find(s => s.zone === z && s.spotNumber === n);
    const nearest = computed(() => floorSpots.value.filter(s => !s.isParked).sort((a, b) => a.distanceFromEntrance - b.distanceFromEntrance)[0] || null);
    const floorAvailable = computed(() => floorSpots.value.filter(s => !s.isParked).length);
    const floorTotal = computed(() => floorSpots.value.length);
    const totalAvailable = computed(() => allSpots.value.filter(s => !s.isParked).length);
    const totalAll = computed(() => allSpots.value.length);

    const isHL = (z, n) => { const h = highlightSpot.value; return h && h.zone === z && h.spotNumber === n && h.floor === floor.value; };
    const color = (z, n) => { const s = findSpot(z, n); if (!s) return '#fff'; if (isHL(z, n)) return '#FF9500'; if (nearest.value?.spotId === s.spotId) return '#34C759'; return s.isParked ? '#007AFF' : '#fff'; };
    const textC = (z, n) => { const s = findSpot(z, n); if (!s) return '#1a1a1a'; if (isHL(z, n) || nearest.value?.spotId === s.spotId || s.isParked) return '#fff'; return '#1a1a1a'; };
    const stk = (z, n) => isHL(z, n) ? '#FF6600' : '#1a1a1a';
    const sw = (z, n) => isHL(z, n) ? 3 : 1;

    const arrowPaths = computed(() => {
      const hl = highlightSpot.value;
      if (hl && hl.floor === floor.value) return buildSearchArrowPaths(hl.zone, hl.spotNumber);
      const tgt = nearest.value;
      if (tgt) return [buildArrowPath(tgt.zone, tgt.spotNumber)];
      if (floorSpots.value.length > 0 && floorAvailable.value === 0) return [`M${ZONE.entrance.x} ${ZONE.entrance.y} L${ZONE.entrance.x} 560`];
      return [];
    });

    const floorFullMsg = computed(() => totalAvailable.value === 0 && totalAll.value > 0 ? '전층 만차!' : '');
    const changeFloor = (f) => { floor.value = f; };
    const clickSpot = (z, n) => { const s = findSpot(z, n); if (s) { modalSpot.value = s; showModal.value = true; } };
    const handleExit = async () => { if (!modalSpot.value) return; try { await api.exit(modalSpot.value.spotId); await loadSpots(); } catch { modalSpot.value.isParked = false; } closeModal(); };
    const closeModal = () => { showModal.value = false; modalSpot.value = null; };

    const handleDummyPark = async () => {
      loading.value = true;
      try { const res = await api.dummyEntry(); const data = res?.data ?? res; if (data.floor !== floor.value) floor.value = data.floor; await loadSpots(); alert(`${data.zone}${data.spotNumber} (B${data.floor}층) 입차!`); }
      catch { const empty = allSpots.value.filter(s => !s.isParked).sort((a, b) => a.floor - b.floor || a.distanceFromEntrance - b.distanceFromEntrance); if (!empty.length) { alert('만차!'); loading.value = false; return; } const s = empty[0]; s.isParked = true; s.vehicleNum = `${Math.floor(Math.random()*90)+10}${'가나다라마바사아자차'[Math.floor(Math.random()*10)]}${Math.floor(Math.random()*9000)+1000}`; if (s.floor !== floor.value) floor.value = s.floor; alert(`${s.zone}${s.spotNumber} 입차!`); }
      finally { loading.value = false; }
    };

    const handleExitAll = async () => {
      if (!confirm('전체 출차?')) return;
      loading.value = true;
      try { await api.exitAll(); await loadSpots(); alert('전체 출차 완료!'); }
      catch { allSpots.value.forEach(s => { s.isParked = false; s.vehicleNum = null; }); alert('전체 출차 완료! (로컬)'); }
      finally { loading.value = false; }
    };

    return { SVG, Z: ZONE, floor, loading, showModal, modalSpot, floorAvailable, floorTotal, totalAvailable, totalAll, floorFullMsg, topLabel, bottomLabel, changeFloor, clickSpot, handleDummyPark, handleExitAll, devMode, handleExit, closeModal, getX: spotX, getY: spotY, getPillarsBC: pillarsBC, getPillarsE: pillarsE, color, textC, stk, sw, arrowPaths };
  }
};
</script>

<style scoped>
.parking-status { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 720px; margin: 0 auto; }
.dummy-bar { display: flex; justify-content: center; gap: 10px; margin-bottom: 12px; }
.control-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; flex-wrap: wrap; gap: 10px; }
.legend { display: flex; gap: 12px; flex-wrap: wrap; }
.legend-item { display: flex; align-items: center; gap: 5px; font-size: 12px; color: #666; }
.dot { width: 12px; height: 12px; border-radius: 3px; }
.dot.avail { background: #fff; border: 2px solid #1a1a1a; }
.dot.occupied { background: #007AFF; }
.dot.recommend { background: #34C759; }
.dot.highlight { background: #FF9500; border: 2px solid #FF6600; }
.floor-btns { display: flex; gap: 6px; }
.floor-btns button { padding: 8px 16px; border: 2px solid #e5e5e5; border-radius: 8px; background: #fff; font-size: 14px; font-weight: 600; color: #86868b; cursor: pointer; }
.floor-btns button:hover { border-color: #007AFF; color: #007AFF; }
.floor-btns button.active { background: #007AFF; border-color: #007AFF; color: #fff; }
.btn { padding: 10px 22px; border: none; border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; }
.bp { background: #007AFF; color: #fff; } .bp:hover { background: #0056b3; } .bp:disabled { background: #86868b; cursor: not-allowed; }
.br { background: #FF3B30; color: #fff; } .br:hover { background: #cc2d25; } .br:disabled { background: #86868b; cursor: not-allowed; }
.parking-map { width: 100%; height: auto; border: 2px solid #1a1a1a; border-radius: 10px; }
.spot { cursor: pointer; transition: opacity .15s; } .spot:hover { opacity: .7; }
.status-row { display: flex; gap: 12px; margin-top: 16px; justify-content: center; }
.card { flex: 1; max-width: 280px; padding: 14px 20px; border-radius: 12px; text-align: center; }
.card.cur { background: #e3f2fd; } .card.tot { background: #f5f5f7; }
.card-label { font-size: 14px; color: #86868b; margin-bottom: 4px; }
.card-value { font-size: 24px; font-weight: 700; } .card-value .g { color: #34C759; } .card-value .d { color: #86868b; margin: 0 4px; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-body { background: #fff; border-radius: 16px; padding: 24px; min-width: 280px; max-width: 90%; box-shadow: 0 10px 40px rgba(0,0,0,0.2); }
.modal-body h3 { font-size: 24px; font-weight: 700; text-align: center; margin-bottom: 16px; }
.modal-info p { font-size: 14px; margin-bottom: 8px; color: #666; } .modal-info p strong { color: #1a1a1a; }
.btn-exit { width: 100%; padding: 12px; background: #FF3B30; color: #fff; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; margin: 10px 0; }
.btn-close { width: 100%; padding: 12px; background: #f5f5f7; color: #86868b; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; }
</style>
