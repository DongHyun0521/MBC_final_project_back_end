import KioskLayout from "@/layouts/KioskLayout.vue"
import Entry from '@/views/parkingLog/Entry.vue'

export default [
    {
        path: '/kiosk',
        component: KioskLayout,
        children: [
            {
                path: 'entry',      // 주소창에 localhost:5173/entry 라고 치면
                name: 'entry',
                component: Entry     // Entry.vue 화면을 보여줘라
            }
        ]
    }
]