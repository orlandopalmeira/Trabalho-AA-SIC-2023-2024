<template>
    <default-layout>
        <v-container>
            <v-row class="my-5">
                <v-col cols="12" md="8" offset-md="2">
                    <v-card>
                        <v-card-title>
                            <v-icon large class="mr-4">mdi-information-outline</v-icon>
                            Sobre a Aplicação de Votações
                        </v-card-title>
                        <v-card-text>
                            <p>
                                Esta aplicação de votações foi criada para fornecer uma plataforma simples e eficaz para organizar e conduzir votações. Seja para eleições ou pesquisas. Esta ferramenta oferece uma interface amigável e recursos robustos para garantir a integridade e a transparência do processo de votação.
                            </p>
                            <v-divider class="my-4"></v-divider>
                            <p>
                                <strong>Funcionalidades principais incluem:</strong>
                            </p>
                            <div class="ml-4 mr-4">
                                <ul>
                                    <li>Criação de votações personalizadas</li>
                                    <li>Visualização de resultados mediante permissão do dono da votação</li>
                                    <li>Histórico de votações passadas</li>
                                    <li>...</li>
                                </ul>
                            </div>
                            <v-divider class="my-4"></v-divider>
                            <p>
                                Desenvolvido com <v-chip color="primary" text-color="white" label>Vue.js</v-chip> e <v-chip color="primary" text-color="white" label>Vuetify</v-chip>.
                            </p>
                        </v-card-text>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </default-layout>
</template>

<script>
import DefaultLayout  from '../layouts/DefaultLayout.vue'
import { useUserInfoStore } from '@/stores/userInfoStore'
import router from '@/router';
export default {
    name: 'About',
    components: {
        DefaultLayout
    },
    setup() {
        if (useUserInfoStore().isAutenticated === true) {
            router.push({name: 'home'});
        }
        return {}
    }
};
</script>

