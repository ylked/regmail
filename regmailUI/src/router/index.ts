/**
 * router/index.ts
 *
 * Automatic routes for `./src/pages/*.vue`
 */

// Composables
import { createRouter, createWebHistory } from 'vue-router/auto'
import index from "@/pages/index.vue";
import login from "@/pages/login.vue";
import register from "@/pages/register.vue";

type Route = {
  path: string;
  name: string;
  component: any;
  meta?: {
    title: string;
  };
}

const routes: Route[] = [
  {
    path: '/',
    name: 'Home',
    component: index,
    meta: {
      title: 'Home',
    },
  },
  {
    path: '/login',
    name: 'Login',
    component: login,
    meta: {
      title: 'Login',
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: register,
    meta: {
      title: 'Register',
    },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (!localStorage.getItem('vuetify:dynamic-reload')) {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    } else {
      console.error('Dynamic import error, reloading page did not fix it', err)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router
