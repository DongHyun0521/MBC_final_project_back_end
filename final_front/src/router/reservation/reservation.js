import Reservation from "@/views/reservation/Reservation.vue";
import DoctorSearch from "@/views/reservation/DoctorSearch.vue";
import DeptSearch from "@/views/reservation/DeptSearch.vue";
import CheckReservation from "@/views/reservation/CheckReservation.vue";
import Guide from "@/views/reservation/Guide.vue";
import Process from "@/views/reservation/Process.vue";

export default [
    {
        path: '/reservation',
        name: 'reservation',
        component: Reservation,
        // 예약 신청 로그인 필수
        meta: { requiresAuth: true, title: '진료 예약' } 
    },
    {
        path: '/doctorsearch',
        name: 'doctorsearch',
        component: DoctorSearch,
        // 비로그인 허용은 meta를 안 쓰거나 false로 두면 됨!(비회원도 조회 가능)
        meta: { requiresAuth: false, title: '의료진 찾기' } 
    },
    {
        path: '/deptsearch',
        name: 'deptsearch',
        component: DeptSearch,
        // 비회원도 조회 가능
        meta: { requiresAuth: false, title: '진료과 찾기' }
    },
    {
        path: '/checkreservation',
        name: 'checkreservation',
        component: CheckReservation,
        // 내 예약 확인 로그인 필수
        meta: { requiresAuth: true, title: '예약 확인' }
    },
    {
        path: '/guide',
        name: 'guide',
        component: Guide,
    },
    {
        path: '/process',
        name: 'proces',
        component: Process,
    }
]