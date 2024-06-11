<template>
    <NavBar :logout_button="false" :routes="routes"/>
    <main>
        <slot></slot> <!--Conteúdo da página fica aqui-->
    </main>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
const routes = {
    'About': '/',
    'Login': '/login',
    'Criar conta': '/register',
}
</script>

