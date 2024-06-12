import { createRouter, createWebHistory } from 'vue-router'
import { API_PATHS } from '@/apiPaths'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'about',
      component: () => import('../views/About.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/myvotings',
      name: 'myvotings',
      component: () => import('../views/MyVotings.vue'),
    },
    {
      path: '/createvoting',
      name: 'createvoting',
      component: () => import('../views/CreateVoting.vue')
    },
    {
      path: '/voting/:id',
      name: 'voting',
      component: () => import('../views/Voting.vue')
    },
    {
      path: '/history',
      name: 'history',
      component: () => import('../views/History.vue'),
    }
  ]
})

export default router
