<template>
    <NavBar :logout_button="false" :routes="routes"/>
    <RouterView />
    <main>
        <slot></slot> <!--Conteúdo da página fica aqui-->
    </main>
</template>

<script setup>
import { RouterView } from 'vue-router'
import NavBar from '../components/NavBar.vue'
const routes = {
    'Home': '/',
    'Login': '/login',
    'Criar conta': '/register'
}
</script>

