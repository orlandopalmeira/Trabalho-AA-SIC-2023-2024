<template>
    <NavBar :logout_button="false" :routes="routes"/>
    <main>
        <slot></slot> <!--Conteúdo da página fica aqui-->
    </main>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
const routes = {
    'About': {name: 'about'},
    'Login': {name: 'login'},
    'Criar conta': {name: 'register'},
}
</script>

