<template>
<AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <LoadingAlert v-if="loadingVoting" message="A carregar a votação, por favor aguarde." />
        <v-card flat v-else style="max-width: 1000px; margin: 20px auto;">
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
                                <div 
                                    v-for="(option, index) in voting.questions[currentQuestionNumber-1].options" 
                                    :key="index" 
                                    style="margin-right: 20px; margin-bottom: 20px; display: flex;">
                                    <v-radio
                                        v-model="option.selected"
                                        :label="option.description"
                                        :value="option.id"
                                        @click="toggleOption(option)"
                                    ></v-radio>
                                    <img v-if="option.image != null"
					    alt="Option background" 
					    :src="'http://localhost:8080/images/' + option.image"
					/>
                                </div>
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
                            @click="submitAnswers"
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

            answers: {},

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
        },

        toggleOption(option) {
            option.selected = !option.selected;
        },

        goNext() {
            this.currentQuestionNumber++
        },

        goPrevious() {
            this.currentQuestionNumber--
        },

        leave() {
            this.$router.push('/')
        },

        submitAnswers() {
            alert('TODO: submit answers to server and redirect to home page')
        },
    },

    created() {
        this.getVoting()
        .then(voting => {
            this.voting = voting
            this.loadingVoting = false
        }).catch(error => {
            console.error(error)
        })

        this.answers['votingid'] = this.$route.params.id
        this.answers['options'] = {}
    }
}
</script>
