<!-- Página onde são apresentadas as votações -->
<template>
    <NavBar :logout_button="true" :routes="routes"/>
    <main>
        <slot></slot> <!--Conteúdo da página fica aqui-->
    </main>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
const routes = {
    'Home': '/home',
    'Histórico': '/history',
    'Minhas votações': '/myvotings',
}
</script>

