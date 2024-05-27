<template>
  <NavBar :logout_button="true" :routes="routes"/>
  <RouterView />
  
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
import NavBar from './components/NavBar.vue'
const routes = {
  'Home': '/',
  'Login': '/login',
  'Register': '/register'
}
</script>

