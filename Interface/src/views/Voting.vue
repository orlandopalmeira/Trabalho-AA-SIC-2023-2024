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
                                color="secondary"
                                @click="leave">
                                Sair
                            </v-btn>
                        </v-col>
                        <v-col cols="6" class="text-right">
                            <v-btn v-if="currentQuestionIndex < voting.questions.length-1"
                                color="primary"
                                @click="goNext"
                            >
                                Questão seguinte
                            </v-btn>
                            <v-btn v-else
                                color="primary"
                                @click="submitVote"
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
    import QuestionCard from '@/components/Voting/QuestionCard.vue'
    
    import axios from '@/axios'
    
    export default {
        name: 'Voting',
    
        components: {
            AuthenticatedLayout,
            ModalOk,
            LoadingAlert,
            QuestionCard
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
                currentQuestionIndex: 0,
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
                this.$router.push('/home')
            },
            submitVote() {
                const vote = this.validateVote()
                console.log(vote)
                if (vote) {
                    axios.post('/votes', vote)
                    .then(() => {
                        this.modal.title = 'Voto submetido'
                        this.modal.message = 'O seu voto foi submetido com sucesso.'
                        this.modal.opened = true
                        this.$router.push('/home')
                    }).catch(error => {
                        console.error(error)
                        this.modal.title = 'Erro ao submeter voto'
                        this.modal.message = 'Ocorreu um erro ao submeter o seu voto. Por favor tente novamente.'
                        this.modal.opened = true
                    })
                }
            },
        },
    
        created() {
            axios.get(`/votings/${this.$route.params.id}`)
            .then(response => {
                this.voting = response.data
                this.loadingVoting = false
            }).catch(error => {
                console.error(error)
            })
        }
    }
    </script>