import Login from '@/views/member/Login.vue'
import Regi from '@/views/member/Regi.vue'
import Mypage from '@/views/member/Mypage.vue'

export default [
    { 
        path: '/login',                     // 브라우저 주소창에 입력될 실제 경로
        name: 'login',                      // 이 경로에 붙이는 고유 별명 -> 이동시킬 때 /login 대신 login이라고 편리하게 쓸 수 있음(주소 바껴도 일일이 수정할 필요 없음)
        component: Login,                   // 해당 주소로 들어왔을 때 <router-view /> 자리에 꽂아줄 Vue 파일
    },
    { 
        path: '/regi', 
        name: 'regi', 
        component: Regi,
    },
    {
        path: '/mypage',
        name: 'mypage',
        component: Mypage,
        meta: { requiresAuth: true, title: '마이페이지' } // 로그인 필수
    }
]