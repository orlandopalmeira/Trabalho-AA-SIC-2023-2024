<template scoped>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container v-if="stage == 1"> 
            <v-card flat>
                <v-card-title style="padding: 15px;">
                    <v-icon large class="mr-4">mdi-plus-circle</v-icon>
                    Criar Votação - Informações Gerais (1/2)
                </v-card-title>
                <v-card-text>
                    <v-form @submit.prevent="goNext">
                        <v-text-field
                            id="title"
                            prepend-icon="mdi-format-title"
                            name="title"
                            label="Título"
                            type="text"
                            v-model="title"
                            :rules="[rules.required]"
                        ></v-text-field>
                        <v-textarea
                            id="description"
                            prepend-icon="mdi-text"
                            name="description"
                            label="Descrição"
                            type="text"
                            v-model="description"
                            :rules="[rules.required]"
                        ></v-textarea>
                        <v-file-input
                            id="image"
                            prepend-icon="mdi-image"
                            name="image"
                            label="Imagem (opcional)"
                            v-model="image"
                            accept="image/*"
                        ></v-file-input>
                        <v-checkbox
                        id="privatevoting"
                        name="privatevoting"
                        label="Votação Privada"
                        v-model="privatevoting"
                        ></v-checkbox>
                        <v-row class="mt-4">
                            <v-col cols="6">
                                <v-btn
                                    color="secondary"
                                    @click="leave"
                                >
                                    Voltar
                                </v-btn>
                            </v-col>
                            <v-col cols="6" class="text-right">
                                <v-btn
                                    color="primary"
                                    type="submit"
                                >
                                    Seguinte
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-form>
                </v-card-text>
            </v-card>
        </v-container>
        <v-container v-else-if="stage == 2"> 
            <v-card flat>
                <v-card-title style="padding: 15px;" >
                    <v-icon large class="mr-4">mdi-plus-circle</v-icon>
                    Criar Votação - Adicionar Perguntas (2/2)
                </v-card-title>
                <v-card-text>
                    <v-form>
                        <v-card style="background-color: #F2F2F2; margin-bottom: 20px;" v-for="(question,index) in questions">
                            <v-card-title style="padding: 10px;">
                                <v-icon large class="mr-4">mdi-comment-question</v-icon>
                                Pergunta {{ index+1 }}:
                            </v-card-title>
                            <v-card-text>
                                <v-text-field
                                prepend-icon="mdi-format-title"
                                label="Pergunta"
                                type="text"
                                v-model="questions[index]['title']"
                                ></v-text-field>
                                <v-card style="background-color: #F2F2F2;">
                                    <v-card-title>
                                        Opções:
                                    </v-card-title>
                                    <v-card-text style="padding: 20px;">
                                        <div v-for="(option, index2) in questions[index].options" :key="index2" style="padding: 10px;">
                                            <v-row>
                                                <v-text-field
                                                    :label="'Opção ' + (index2 + 1)"
                                                    v-model="questions[index].options[index2]['option']"
                                                    prepend-icon="mdi-form-textbox"
                                                ></v-text-field>
                                                <v-btn icon @click="addImg()" style="margin-left: 10px;">
                                                    <v-icon>mdi-image</v-icon>
                                                </v-btn>
                                                <v-btn icon color="error" @click="removeOption(index,index2)" style="margin-left: 10px;">
                                                    <v-icon>mdi-delete</v-icon>
                                                </v-btn>
                                            </v-row>
                                        </div>
                                        <v-btn color="secondary" @click="addOption(index)" style="margin-top: 10px;">                                        
                                            <v-icon left>mdi-plus</v-icon> Adicionar Opção
                                        </v-btn>
                                        <v-alert
                                            v-if="questions[index].options.length < 2"
                                            type="info"
                                            class="mt-4"
                                        >
                                            Você deve adicionar pelo menos duas opções.
                                        </v-alert>
                                    </v-card-text>
                                </v-card>
                                <v-btn color="error" @click="removeQuestion(index)" style="margin-top: 10px;">                                        
                                    <v-icon left>mdi-delete</v-icon> Remover Pergunta
                                </v-btn>
                            </v-card-text>
                        </v-card>

                        <v-btn color="primary" @click="addQuestion()" style="margin-bottom: 10px;">                                        
                            <v-icon left>mdi-plus</v-icon> Adicionar Pergunta
                        </v-btn>

                        <v-row class="mt-4">
                            <v-col cols="6">
                                <v-btn
                                    color="secondary"
                                    @click="leave"
                                >
                                    Voltar
                                </v-btn>
                            </v-col>
                            <v-col cols="6" class="text-right">
                                <v-btn
                                    color="primary"
                                    @click="goNext"
                                >
                                <span v-if="stage === 1">Seguinte</span>
                                <span v-else-if="stage === 2">Submeter</span>
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-form>
                </v-card-text>
            </v-card>
        </v-container>
    </AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import { useUserInfoStore } from '@/stores/userInfoStore'

export default {

    name: 'CreateVoting',

    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert
    },

    data() {
        return {
            modal: {
				opened: false,
				title: '',
				message: ''
			},
            useUserInfoStore,
            title: '',
            description: '',
            image: null,
            privatevoting: false,
            stage: 1,

            rules: {

                required: value => !!value || 'Campo obrigatório.',
            },

            questions: [{

                title: '',
                options: [{

                    option: '',
                    img: null,
                }, {

                    option: '',
                    img: null,
                }],
            }],
        }
    },

    methods: {

        leave() {
            if(this.stage == 1){
                this.$router.push('/myvotings')
            } else {
                this.stage = 1
            }
        },

        goNext() {
            if(this.stage == 1){
                
                if (this.title && this.description) {
                    this.stage = 2
                }

            } else {
                this.createVoting()
            }
        },

        addOption(indexQuestions) {
            this.questions[indexQuestions].options.push({
                option: '',
                img: null,
            });
        },

        removeOption(indexQuestion, indexOption) {
            this.questions[indexQuestion].options.splice(indexOption, 1);
        },
        
        addImg() {
            console.log('TODO: method addImg')
        },
        
        addQuestion() {
            this.questions.push({
                title: '',
                options: [{
                    option: '',
                    img: null,
                }, {
                    option: '',
                    img: null,
                }],
            });
        },

        removeQuestion(indexQuestion) {
            this.questions.splice(indexQuestion, 1);
        },

        createVoting(){
            // TODO: confirmar todos os campos obrigatorios
            // TODO: garantir que hajam pelo menos duas opçoes
            console.log('TODO: method createVoting')
        }
    }
}
</script>