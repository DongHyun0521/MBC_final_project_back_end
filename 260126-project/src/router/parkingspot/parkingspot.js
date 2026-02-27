import ParkingLayout from '@/layouts/ParkingLayout.vue'
import ParkingStatus from '@/views/parkingspot/ParkingStatus.vue'
import VehicleSearch from '@/views/parkingspot/VehicleSearch.vue'

// router/index.js의 routes 배열에 추가:
// import parkingspot from '@/router/parkingspot/parkingspot.js'
// routes: [ ...기존라우트, ...parkingspot ]

export default [
  {
    path: '/parking',
    component: ParkingLayout,
    children: [
      {
        path: '',
        name: 'ParkingStatus',
        component: ParkingStatus,
        meta: { title: '주차 현황판' }
      },
      {
        path: 'search',
        name: 'VehicleSearch',
        component: VehicleSearch,
        meta: { title: '차량 검색' }
      }
    ]
  }
]
