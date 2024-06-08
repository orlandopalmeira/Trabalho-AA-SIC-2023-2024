<template>
    <div class="app-container">
        <RouterView />
        <footer class="footer">
            <p style="font-size: smaller;">
                &#169; Projeto de Arquiteturas Aplicacionais & Sistemas Interativos Confiáveis
                &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;
                Engenharia de Aplicações 23/24
                &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;
                Universidade do Minho
            </p>
        </footer>
    </div>
</template>

<style>
.app-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh; /* A altura mínima é 100% da altura da janela */
}

footer.footer {
    background-color: rgb(108, 117, 125);
    padding: 5px;
    color: white;
    text-align: center;
    margin-top: auto; /* Empurra o footer para o final do container */
}

</style>

<script>
import { RouterView } from 'vue-router'
export default {
    name: 'App',
    components: {
        RouterView
    }
};
</script>

