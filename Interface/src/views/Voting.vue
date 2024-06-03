<template>
<AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <LoadingAlert v-if="loadingVoting" message="A carregar a votação, por favor aguarde." />
        <v-card flat v-else>
            <v-card-title style="padding: 15px; text-align: center;">
                <h3 style="font-weight: 600;"><span style="color: gray; font-weight: 600;">Votação: </span> {{ voting.title }}</h3>
            </v-card-title>
            <v-card-text>
                <v-card style="background-color: lightgray;">
                    <v-card-title style="padding: 15px; margin-bottom: 10px">
                        <h4 style="font-weight: 600;">Pergunta {{ currentQuestionNumber }}: {{ voting.questions[currentQuestionNumber-1].description }}</h4>
                    </v-card-title>
                    <v-card-text style="background-color: white; margin: 10px;">
                        <v-row>
                            <v-col cols="12">
                                <v-radio-group v-model="voting.questions[currentQuestionNumber-1].selectedOption" row>
                                    <div style="display: flex;" v-for="(option, index) in voting.questions[currentQuestionNumber-1].options">
                                        <v-radio   
                                            :key="index"
                                            :label="option.description"
                                            :value="option.id"
                                        ></v-radio>
                                        <img 
                                            alt="Option background" 
                                            :src="option.image == null ? 
                                                defaultImage : 
                                                'http://localhost:8080/images/' + option.image"/>
                                    </div>
                                </v-radio-group>
                            </v-col>
                        </v-row>
                    </v-card-text>
                </v-card>
                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn v-if="currentQuestionNumber > 1"
                            color="secondary"
                            @click="goPrevious"
                        >
                            Questão anterior
                        </v-btn>
                        <v-btn v-else
                            color="secondary"
                            @click="leave"
                        >
                            Sair
                        </v-btn>
                    </v-col>
                    <v-col cols="6" class="text-right">
                        <v-btn v-if="currentQuestionNumber < voting.questions.length"
                            color="primary"
                            @click="goNext"
                        >
                            Questão seguinte
                        </v-btn>
                        <v-btn v-else
                            color="primary"
                            type="submit"
                        >
                            Submeter respostas
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
</AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'

import axios from '@/axios'

export default {
    name: 'Voting',

    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert
    },

    data() {
        return {
            loadingVoting: true,
            voting: null,
            modal: {
				opened: false,
				title: '',
				message: ''
			},
            currentQuestionNumber: 1,

            defaultImage: './kitten.png'
        }
    },

    methods: {

        async getVoting() {
            try {
                let response = await axios.get(`/votings/${this.$route.params.id}`)
                return response.data
            } catch (error) {
                let response = error.response
                console.error(error)
                this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
                return null
            }
        }
    },

    created() {
        this.getVoting()
        .then(voting => {
            this.voting = voting
            this.loadingVoting = false
        }).catch(error => {
            console.error(error)
        })
    }
}
</script>