// ==========================================
// 주차장 좌표 설정
// SVG 700x600
// A,D: 엘베 양쪽 4+4칸 (w=41, h=69)
// B,C: 중간기둥 4+4칸 × 2열 (w=41, h=69)
// E: 우측 세로 1열, 가로 방향 칸 (w=69, h=40), 3+3 중간기둥
// ==========================================

export const SVG = { width: 700, height: 600 };

// ═══════════════════════════════════════════
// 통로 좌표 (화살표 경로용)
// ═══════════════════════════════════════════
export const AISLE = {
  leftX:  100,
  topY:   15,
  abY:    112,
  bcY:    304,
  cdY:    494,
  rightX: 610,
};

// ═══════════════════════════════════════════
// 구역 설정
// ═══════════════════════════════════════════
export const ZONE = {
  pw: 17,
  mpw: 17,
  pgap: 4,
  
  elev: {
    w: 70, h: 55,
    top:    { x: 324, y: 20 },
    bottom: { x: 324, y: 525 }
  },
  
  entrance: { x: 65, y: 50 },

  A: { sx: 105, sy: 20,  w: 41, h: 69, gap: 4, cnt: 8,  cols: 4, hasElev: true },
  B: { sx: 143, sy: 135, w: 41, h: 69, gap: 4, cnt: 16, cols: 8, rg: 10 },
  C: { sx: 143, sy: 325, w: 41, h: 69, gap: 4, cnt: 16, cols: 8, rg: 10 },
  D: { sx: 105, sy: 515, w: 41, h: 69, gap: 4, cnt: 8,  cols: 4, hasElev: true },
  E: { 
    sx: 615, sy: 140, w: 69, h: 40, gap: 4, cnt: 6, cols: 1, rg: 14, rowSize: 3,
    pillarH: 17,
    pgap: 4
  }
};

// ═══════════════════════════════════════════
// 거리 데이터 (입구 기준)
// ═══════════════════════════════════════════
export const DIST = {
  A: [242, 283, 324, 365, 526, 567, 608, 649],
  B: [295, 336, 377, 418, 473, 514, 555, 596, 465, 506, 547, 588, 643, 684, 725, 766],
  C: [465, 506, 547, 588, 643, 684, 725, 766, 635, 676, 717, 758, 813, 854, 895, 936],
  D: [582, 623, 664, 705, 866, 907, 948, 989],
  E: [763, 804, 845, 900, 941, 982]
};

// ═══════════════════════════════════════════
// 좌표 계산 함수
// ═══════════════════════════════════════════

export function spotX(zone, n) {
  const c = ZONE[zone];
  const pw = ZONE.pw;
  const pgap = ZONE.pgap;

  if (zone === 'E') return c.sx;

  if (c.hasElev) {
    const fourW = 4 * c.w + 3 * c.gap;
    
    if (n <= 4) {
      return c.sx + pw + pgap + (n - 1) * (c.w + c.gap);
    }
    const leftEnd = c.sx + pw + pgap + fourW + pgap + pw;
    const rightStart = leftEnd + ZONE.elev.w + pw + pgap;
    return rightStart + (n - 5) * (c.w + c.gap);
  }

  const mpw = ZONE.mpw;
  const fourW = 4 * c.w + 3 * c.gap;
  const col = (n - 1) % c.cols;
  
  if (col < 4) {
    return c.sx + pw + pgap + col * (c.w + c.gap);
  }
  const midPillarEnd = c.sx + pw + pgap + fourW + pgap + mpw + pgap;
  return midPillarEnd + (col - 4) * (c.w + c.gap);
}

export function spotY(zone, n) {
  const c = ZONE[zone];

  if (zone === 'E') {
    const pgap = c.pgap || 0;
    const ph = c.pillarH || ZONE.pw;
    
    if (n <= 3) {
      return c.sy + ph + pgap + (n - 1) * (c.h + c.gap);
    }
    const midPillarY = c.sy + ph + pgap + 3 * (c.h + c.gap) + pgap;
    return midPillarY + ph + pgap + (n - 4) * (c.h + c.gap);
  }

  if ((zone === 'B' || zone === 'C') && n > 8) return c.sy + c.h + c.rg;
  return c.sy;
}

export function spotCenter(zone, n) {
  const c = ZONE[zone];
  return {
    x: spotX(zone, n) + c.w / 2,
    y: spotY(zone, n) + c.h / 2
  };
}

// 화살표 끝점 (칸 앞에서 멈춤)
function arrowEnd(zone, n) {
  const c = ZONE[zone];
  const x = spotX(zone, n) + c.w / 2;
  const y = spotY(zone, n);
  
  return {
    x,
    topY: y - 8,          // 칸 위쪽 8px 앞 (위에서 진입)
    bottomY: y + c.h + 8  // 칸 아래쪽 8px 앞 (아래서 진입)
  };
}

// ═══════════════════════════════════════════
// 기둥 위치
// ═══════════════════════════════════════════

export function pillarsAD(zone) {
  const c = ZONE[zone];
  const pw = ZONE.pw;
  const pgap = ZONE.pgap;
  const fourW = 4 * c.w + 3 * c.gap;
  
  const leftEnd = c.sx + pw + pgap + fourW + pgap;
  const rightStart = leftEnd + pw + ZONE.elev.w;
  const rightEnd = rightStart + pw + pgap + fourW + pgap;
  
  return [
    { x: c.sx,       w: pw },
    { x: leftEnd,    w: pw },
    { x: rightStart, w: pw },
    { x: rightEnd,   w: pw }
  ];
}

export function pillarsBC(zone) {
  const c = ZONE[zone];
  const pw = ZONE.pw;
  const mpw = ZONE.mpw;
  const pgap = ZONE.pgap;
  const fourW = 4 * c.w + 3 * c.gap;
  
  const midStart = c.sx + pw + pgap + fourW + pgap;
  const rightStart = midStart + mpw + pgap + fourW + pgap;
  
  return [
    { x: c.sx,       w: pw  },
    { x: midStart,   w: mpw },
    { x: rightStart, w: pw  }
  ];
}

export function pillarsE() {
  const c = ZONE.E;
  const ph = c.pillarH || ZONE.pw;
  const pgap = c.pgap || 0;
  
  const topY = c.sy;
  const midY = topY + ph + pgap + 3 * (c.h + c.gap) + pgap;
  const bottomY = midY + ph + pgap + 3 * (c.h + c.gap) + pgap;
  
  return [
    { x: c.sx, y: topY,    w: c.w, h: ph },
    { x: c.sx, y: midY,    w: c.w, h: ph },
    { x: c.sx, y: bottomY, w: c.w, h: ph }
  ];
}

// ═══════════════════════════════════════════
// 입구 → 추천칸 화살표
// ═══════════════════════════════════════════
export function buildArrowPath(zone, n) {
  const end = arrowEnd(zone, n);
  const ent = ZONE.entrance;
  const A = AISLE;

  // A구역: 위에서 진입
  if (zone === 'A') {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`;
  }
  // B 1열: 위에서 진입
  if (zone === 'B' && n <= 8) {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`;
  }
  // B 2열: 아래에서 진입
  if (zone === 'B' && n > 8) {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.bottomY}`;
  }
  // C 1열: 위에서 진입
  if (zone === 'C' && n <= 8) {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.topY}`;
  }
  // C 2열: 아래에서 진입
  if (zone === 'C' && n > 8) {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.cdY} L${end.x} ${A.cdY} L${end.x} ${end.bottomY}`;
  }
  // D구역: 위에서 진입
  if (zone === 'D') {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.cdY} L${end.x} ${A.cdY} L${end.x} ${end.topY}`;
  }
  // E구역: 왼쪽에서 진입
  if (zone === 'E') {
    return `M${ent.x} ${ent.y} L${ent.x} ${A.abY} L${A.rightX} ${A.abY} L${A.rightX} ${end.topY} L${end.x} ${end.topY}`;
  }

  return '';
}

// ═══════════════════════════════════════════
// 검색 화살표 (양쪽 엘베 → 해당 칸)
// ═══════════════════════════════════════════
export function buildSearchArrowPaths(zone, n) {
  const end = arrowEnd(zone, n);
  const A = AISLE;
  const evT = ZONE.elev.top;
  const evB = ZONE.elev.bottom;
  
  const txc = evT.x + ZONE.elev.w / 2;
  const tyc = evT.y + ZONE.elev.h / 2;
  const bxc = evB.x + ZONE.elev.w / 2;
  const byc = evB.y + ZONE.elev.h / 2;

  const paths = [];

  // 상단 엘베 → 칸
  if (zone === 'A') {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`);
  } else if (zone === 'B' && n <= 8) {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`);
  } else if (zone === 'B' && n > 8) {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${A.leftX} ${A.abY} L${A.leftX} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.bottomY}`);
  } else if (zone === 'C' && n <= 8) {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${A.leftX} ${A.abY} L${A.leftX} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.topY}`);
  } else if (zone === 'C' && n > 8) {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${A.leftX} ${A.abY} L${A.leftX} ${A.cdY} L${end.x} ${A.cdY} L${end.x} ${end.bottomY}`);
  } else if (zone === 'D') {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${A.leftX} ${A.abY} L${A.leftX} ${A.cdY} L${end.x} ${A.cdY} L${end.x} ${end.topY}`);
  } else if (zone === 'E') {
    paths.push(`M${txc} ${tyc} L${txc} ${A.abY} L${A.rightX} ${A.abY} L${A.rightX} ${end.topY} L${end.x} ${end.topY}`);
  }

  // 하단 엘베 → 칸
  const col = (n - 1) % 8;
  const isRight = col >= 4;
  const sideX = isRight ? A.rightX : A.leftX;

  if (zone === 'D') {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${end.x} ${A.cdY} L${end.x} ${end.bottomY}`);
  } else if (zone === 'C' && n > 8) {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${sideX} ${A.cdY} L${sideX} ${end.bottomY} L${end.x} ${end.bottomY}`);
  } else if (zone === 'C' && n <= 8) {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${sideX} ${A.cdY} L${sideX} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.topY}`);
  } else if (zone === 'B' && n > 8) {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${sideX} ${A.cdY} L${sideX} ${A.bcY} L${end.x} ${A.bcY} L${end.x} ${end.bottomY}`);
  } else if (zone === 'B' && n <= 8) {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${sideX} ${A.cdY} L${sideX} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`);
  } else if (zone === 'A') {
    const aSideX = (n <= 4) ? A.leftX : A.rightX;
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${aSideX} ${A.cdY} L${aSideX} ${A.abY} L${end.x} ${A.abY} L${end.x} ${end.topY}`);
  } else if (zone === 'E') {
    paths.push(`M${bxc} ${byc} L${bxc} ${A.cdY} L${A.rightX} ${A.cdY} L${A.rightX} ${end.bottomY} L${end.x} ${end.bottomY}`);
  }

  return paths;
}

// ═══════════════════════════════════════════
// 더미 데이터 생성
// ═══════════════════════════════════════════
export function generateDummySpots() {
  const spots = [];
  for (let f = 1; f <= 3; f++) {
    let id = (f - 1) * 54 + 1;
    for (const z of ['A', 'B', 'C', 'D', 'E']) {
      for (let i = 1; i <= ZONE[z].cnt; i++) {
        spots.push({
          spotId: id++,
          floor: f,
          zone: z,
          spotNumber: i,
          distanceFromEntrance: DIST[z][i - 1],
          isParked: false,
          parkingLogId: null,
          vehicleNum: null
        });
      }
    }
  }
  return spots;
}
