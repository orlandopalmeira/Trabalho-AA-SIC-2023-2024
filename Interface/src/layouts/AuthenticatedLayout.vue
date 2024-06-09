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
    'Home': {name: 'home'},
    'Histórico': {name: 'history'},
    'Minhas votações': {name: 'myvotings'},
}
</script>

