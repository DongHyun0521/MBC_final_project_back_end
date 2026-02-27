import Faq from "@/views/customer/Faq.vue";
import Notice from "@/views/customer/Notice.vue";
import Voc from "@/views/customer/Voc.vue";

export default [
    // 공지사항 (목록 + 상세 + 쓰기/수정 모두 Notice.vue에서 처리)
    {
        path: '/notice',
        name: 'notice',
        component: Notice
    },
    {
        path: '/notice/:id', // 주소에 ID가 붙어도 Notice.vue로 보냄
        name: 'notice-detail',
        component: Notice
    },
    {
        path: '/faq',
        name: 'faq',
        component: Faq
    },
    {
        path: '/voc',
        name: 'voc',
        component: Voc,
        meta: { requiresAuth: true, title: '고객의소리' }
    }
]