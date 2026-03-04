import { createRouter, createWebHistory } from 'vue-router'

// 1. 레이아웃 (틀) 불러오기
import MainLayout from '@/layouts/MainLayout.vue'   // 헤더+푸터 있는 일반 화면
// import KioskLayout from '@/layouts/KioskLayout.vue' // 헤더+푸터 없는 키오스크 화면
// import ParkingSystem from '@/layouts/ParkingSystem.vue'

// 페이지(View) 파일 불러오기
import MainHome from '@/views/MainHome.vue'             // 메인 홈페이지
import SearchResult from '@/components/SearchResult.vue' // 통합검색 결과창

// [병원소개] About 폴더
import Greeting from '@/views/about/Greeting.vue'
import Mission from '@/views/about/Mission.vue'
import History from '@/views/about/History.vue'

// [의료진/진료과] Medical 폴더
import CenterClinic from '@/views/medical/CenterClinic.vue'

// [건강정보] heath 폴더
import Disease from '@/views/health/Disease.vue'
import Checkup from '@/views/health/Checkup.vue'
import Story from '@/views/health/Story.vue'

// [오시는 길 / 주차안내]
import Location from '@/views/location/Location.vue'
import ParkingInfo from '@/views/location/ParkingInfo.vue'

// 기능별로 쪼개놓은 라우터 파일 합치기
import member from '@/router/member/member.js'
import vehicle from '@/router/vehicle/vehicle.js'
import reservation from '@/router/reservation/reservation.js'
import customer from '@/router/customer/customer.js'

// 담당자가 준 파일들 불러오기
import entry from '@/router/entry/entry.js'
import exit from '@/router/exit/exit.js'

// 라우터 설정 시작
const router = createRouter({
    history: createWebHistory(),
    routes: [

        // [A] 메인 홈페이지 그룹 (MainLayout 사용)
        {
            path: '/',
            component: MainLayout,
            children: [
                // 1. 메인화면
                {
                    path: '',
                    name: 'mainhome',
                    component: MainHome,
                },

                // 2. 검색결과 화면
                {
                    path: '/search',
                    name: 'searchresult',
                    component: SearchResult
                },

                // 3. 병원소개 메뉴 연결
                {
                    path: '/greeting',
                    name: 'greeting',
                    component: Greeting
                },
                {
                    path: '/mission',
                    name: 'mission',
                    component: Mission
                },
                {
                    path: '/history',
                    name: 'history',
                    component: History
                },

                // 4. 의료진/진료과 메뉴 연결
                {
                    path: '/centerclinic',
                    name: 'centerclinic',
                    component: CenterClinic
                },

                // 5. 건강정보 메뉴 연결
                {
                    path: '/disease',
                    name: 'disease',
                    component: Disease
                },
                {
                    path: '/checkup',
                    name: 'checkup',
                    component: Checkup
                },
                {
                    path: '/story',
                    name: 'story',
                    component: Story
                },
                {
                    path: '/story/:id',
                    name: 'story-detail',
                    component: Story
                },

                // 추가. 위치/주차 안내 연결
                {
                    path: '/location',
                    name: 'location',
                    component: Location
                },
                {
                    path: '/parkinginfo',
                    name: 'parkinginfo',
                    component: ParkingInfo
                },

                // 6. 쪼개놓은 파일들 여기서 합치지 (Spread Operator)
                ...member,
                ...vehicle,
                ...reservation,
                ...customer
            ]
        },

        // [B & C] 주차장/키오스크 파일들
        ...entry,
        ...exit,
    ]
})

// 5. 네비게이션 가드 (로그인 안 하면 못 들어가게 막기)
router.beforeEach((to, from, next) => {
    // 이동하려는 페이지에 '로그인 필요(requiresAuth)' 딱지가 붙어있는지 확인
    if (to.meta.requiresAuth) {

        // 세션 스토리지에서 로그인 정보 확인
        const isLoggedIn = sessionStorage.getItem('loginId');

        if (!isLoggedIn) {
            // 로그인 안 했으면 쫓아냄
            alert('로그인이 필요한 서비스입니다.');
            next('/login');
        } else {
            // 로그인 했으면 통과
            next();
        }
    } else {
        // 딱지 없는 페이지는 그냥 통과
        next();
    }
});

export default router;