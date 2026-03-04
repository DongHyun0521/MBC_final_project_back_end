import KioskLayout from "@/layouts/KioskLayout.vue"
import Exit from '@/views/parkingLog/Exit.vue'

export default [
    {
        path: '/kiosk',
        component: KioskLayout,
        children: [
            {
                path: 'exit',       // 주소창에 localhost:5173/exit 라고 치면
                name: 'exit',
                component: Exit      // Exit.vue 화면을 보여줘라
            }
        ]
    }
]