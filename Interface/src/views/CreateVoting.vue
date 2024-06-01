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
                    <v-form>
                        <v-text-field
                        id="title"
                        prepend-icon="mdi-format-title"
                        name="title"
                        label="Título"
                        type="text"
                        v-model="title"
                        ></v-text-field>
                        <v-textarea
                        id="description"
                        prepend-icon="mdi-text"
                        name="description"
                        label="Descrição"
                        type="text"
                        v-model="description"
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
                                    @click="goNext"
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
                        
                        <v-card style="background-color: #F2F2F2;">

                            <v-card-title style="padding: 10px;">
                                <v-icon large class="mr-4">mdi-plus-circle</v-icon>
                                Adicionar Pergunta
                            </v-card-title>
                            <v-card-text>
                                <v-text-field
                                id="title"
                                prepend-icon="mdi-format-title"
                                name="title"
                                label="Pergunta"
                                type="text"
                                v-model="title"
                                ></v-text-field>

                                <v-card style="background-color: #F2F2F2;">

                                    <v-card-title>
                                        Opções:
                                    </v-card-title>

                                    <v-card-text style="padding: 20px;">
                                        <div v-for="(option, index) in options" :key="index" style="padding: 10px;">
                                            <v-row>
                                                <v-text-field
                                                    :label="'Opção ' + (index + 1)"
                                                    v-model="options[index]['option']"
                                                    prepend-icon="mdi-form-textbox"
                                                ></v-text-field>
                                                [TODO: add imagem]
                                                [TODO: remove]
                                            </v-row>
                                        </div>

                                    <v-btn color="primary" @click="addOption" style="margin-top: 10px;">                                        
                                        <v-icon left>mdi-plus</v-icon> Adicionar Opção
                                    </v-btn>

                                    <v-alert
                                        v-if="options.length < 2"
                                        type="info"
                                        class="mt-4"
                                    >
                                        Você deve adicionar pelo menos duas opções.
                                    </v-alert>

                                    </v-card-text>

                                </v-card>

                            </v-card-text>

                        </v-card>

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
// TODO: garantir que hajam pelo menos duas opçoes
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
            privatevoting: false,
            stage: 1,

            options: [{

                option: '',
                img: null,
            }, {

                option: '',
                img: null,
            }],


            image: null,
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
                this.stage = 2
            } else {
                this.createVoting()
            }
        },

        addOption() {
            console.log('TODO: method addOption');
        },

        createVoting(){
            // TODO: confirmar todos os campos obrigatorios
            console.log('TODO: method createVoting')
        }
    }
}
</script>