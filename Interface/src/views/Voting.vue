<template>
    <AuthenticatedLayout>
            <ModalOk 
                :isVisible="modal.opened"
                :title="modal.title"
                :message="modal.message"
                :color="modal.color"
                @close-modal="modal.opened=false"/>
            <LoadingAlert v-if="loadingVoting" message="A carregar a votação, por favor aguarde." />
            <v-card flat v-else class="dark">
                <v-tabs v-model="tab" align-tabs="center" class="dark custom-tabs mt-4">
                    <v-tab class="dark-lighter" value="votar">Votar</v-tab>
                    <v-tab class="dark-lighter" v-if="allowedToViewStats()" value="estatisticas">Estatísticas</v-tab>
                    <v-tab class="dark-lighter" value="detalhes">Detalhes</v-tab>
                </v-tabs>
                <v-tabs-window v-model="tab" >
                    <!--Tab para votar-->
                    <v-tabs-window-item value="votar" class="dark"> 
                        <div style="display: flex; justify-content: center; align-items: center;">
                            <v-card class="mx-auto mb-2 dark-light" style="min-width: 500px; display: inline-block;">
                                <!--Título da votação-->
                                <v-card-title style="padding: 15px; text-align: center;" >
                                    <h3 style="font-weight: 600;"><span style="color: gray; font-weight: 600;">Votação: </span> {{ voting.title }}</h3>
                                </v-card-title>
                                <!-- Se a votação já terminou -->
                                <v-card-text v-if="!isVotingActive()">
                                    <SimpleAlert message="Esta votação já terminou"/>
                                </v-card-text>
                                <!--Se o utilizador já votou-->
                                <v-card-text v-else-if="voting.useralreadyvoted">
                                    <SimpleAlert message="Já submeteu o seu voto para esta votação"/>
                                </v-card-text>
                                <!--Se o utilizador ainda não votou => apresenta as perguntas-->
                                <v-card-text v-else-if="isVotingActive()">
                                    <v-alert closable type="info" class="mb-4 pa-2">
                                        <p> {{ voting.secretvotes ? 'O voto é secreto.' : 'O voto não é secreto.' }} </p>
                                    </v-alert>
                                    <QuestionCard :key="currentQuestionIndex"
                                        :questionIndex="currentQuestionIndex"
                                        :question="voting.questions[currentQuestionIndex]"
                                        @option-changed="optionChanged"/>
                                    <v-row class="mt-4">
                                        <v-col cols="6">
                                            <v-btn v-if="currentQuestionIndex > 0"
                                                color="secondary"
                                                @click="goPrevious">
                                                Questão anterior
                                            </v-btn>
                                            <v-btn v-else
                                                color="error"
                                                @click="leave">
                                                Sair
                                            </v-btn>
                                        </v-col>
                                        <v-col cols="6" class="text-right">
                                            <v-btn v-if="currentQuestionIndex < voting.questions.length-1"
                                                color="primary"
                                                @click="goNext"
                                                :disabled="!voting.questions[currentQuestionIndex].selectedOption">
                                                Questão seguinte
                                            </v-btn>
                                            <v-btn v-else
                                                color="primary"
                                                @click="submitVote"
                                                :disabled="!voting.questions[currentQuestionIndex].selectedOption">
                                                Submeter respostas
                                            </v-btn>
                                        </v-col>
                                    </v-row>
                                </v-card-text>
                            </v-card>
                        </div>
                    </v-tabs-window-item>
                    <!--Tab para estatísticas-->
                    <v-tabs-window-item v-if="allowedToViewStats()" value="estatisticas">
                        <StatsTab :voting="voting" />
                    </v-tabs-window-item>
                    <!--Tab para detalhes-->
                    <v-tabs-window-item value="detalhes">
                        <VotingDetails :voting="voting"/>
                    </v-tabs-window-item>
                  </v-tabs-window>
            </v-card>
    </AuthenticatedLayout>
    </template>
    
<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import QuestionCard from '@/components/Voting/QuestionCard.vue'
import VotingDetails from '@/components/Voting/VotingDetails.vue'
import StatsTab from '@/components/Voting/StatsTab.vue'
import SimpleAlert from '@/components/SimpleAlert.vue'
import ToastManager from '@/components/Toast/ToastManager'

import { useUserInfoStore } from '@/stores/userInfoStore';

import axios from '@/axios'
import { API_PATHS } from '@/apiPaths'

export default {
    name: 'Voting',

    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert,
        QuestionCard,
        StatsTab,
        VotingDetails,
        SimpleAlert
    },

    data() {
        return {
            loadingVoting: true,
            voting: null,
            modal: {
                opened: false,
                title: '',
                message: '',
                color: 'primary',
            },
            currentQuestionIndex: 0,
            tab: 'votar'
        }
    },

    methods: { 
        validateVote(){
            let vote = {
                votingid: this.voting.id,
                options: {}
            };
            this.voting.questions.forEach(question => {
                vote.options[`${question.id}`] = [`${question.selectedOption}`]
            });
            // Check if all questions have an option selected
            const indexes = this.voting.questions.map((question,index) => {
                if(!question.selectedOption) return index+1
                else return -1
            }).filter(index => index !== -1)

            if(indexes.length === 0) {
                return vote
            } else {
                this.modal.title = 'Perguntas por responder'
                this.modal.color = 'error'
                if (indexes.length === 1) {
                    this.modal.message = `A pergunta ${indexes[0]} não foi respondida.`
                } else{
                    this.modal.message = `As perguntas ${indexes.join(', ')} não foram respondidas.`
                }
                this.modal.opened = true
                this.currentQuestionIndex = indexes.reduce((a,b) => a < b ? a : b)-1 // Go to the first unanswered question
                return null
            }
        },
        optionChanged(questionIndex, optionId){
            this.voting.questions[questionIndex].selectedOption = optionId
        },
        goNext(){
            this.currentQuestionIndex++
        },
        goPrevious(){
            this.currentQuestionIndex--
        },
        leave() {
            this.$router.push({name: 'home'})
        },
        submitVote() {
            const vote = this.validateVote()
            if (vote) {
                axios.post(API_PATHS.votes, vote)
                .then(() => {
                    this.$router.push({name: 'history', query: {toast_message: 'Voto submetido com sucesso.'}})
                }).catch(error => {
                    console.error(error)
                    this.modal.title = 'Erro ao submeter voto'
                    this.modal.message = 'Ocorreu um erro ao submeter o seu voto. Por favor tente novamente.'
                    this.modal.opened = true
                })
            }
        },
        isVotingActive() {
            if (this.voting.enddate === null) return true;

            let now = new Date()
            let enddate = new Date(this.voting.enddate)
            return enddate === null || enddate > now 
        },
        allowedToViewStats(){
            if(this.voting.accesstype === "creator") return true

            if(this.voting.showstats){
                let now = new Date()
                let enddate = new Date(this.voting.enddate)
                let active = enddate === null || enddate > now 

                if (active) return this.voting.showstatsrealtime
                else return true
            }
            return false
        }
    },

    created() {
        const getAccessType = (voting) => {
            if(Number(voting.creator.id) === Number(useUserInfoStore().getUserId)){
                return "creator"
            } else if(voting.privatevoting){
                return "privatevoter"
            } else {
                return "public"
            }
        }

        axios.get(API_PATHS.votingId(this.$route.params.id))
        .then(response => {
            let voting = response.data
            this.voting = voting
            this.voting.accesstype = getAccessType(voting)
            this.loadingVoting = false
            //* Redireccionar para a tab vinda da URL se existir (querystring ?firstTab=...)
            let firstTab = this.$route.query.firstTab
            let availableTabs = ['votar', 'estatisticas', 'detalhes']
            if(firstTab && availableTabs.includes(firstTab)){
                if(firstTab === 'estatisticas' && !this.allowedToViewStats()){
                    firstTab = 'detalhes'
                } 
                this.tab = firstTab
            }
            //* Toast message logic
            let toast_message = this.$route.query.toast_message
            if(toast_message){
                ToastManager.show(toast_message, 'success', 3000)
                this.$router.replace({ path: this.$route.path }); // para limpar a rota e não ter aquela query string feia ("?toast_message=...")
            }
        }).catch(error => {
            console.error(error)
        })
    },
    computed: {
        
    }
}
</script>
<style scoped>
.custom-tabs .v-tab:first-child {
    border-top-left-radius: 15px;
}

.custom-tabs .v-tab:last-child {
    border-top-right-radius: 15px;
}

.dark-mode .dark {
    background-color: #181818;
    color: white;
}
</style>