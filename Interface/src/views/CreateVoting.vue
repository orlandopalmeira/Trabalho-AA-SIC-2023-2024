<template scoped>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container v-if="stage == 1"> 
            <v-card flat>
                <v-card-title>
                    <v-icon large class="mr-4">mdi-plus-circle</v-icon>
                    Criar Votação
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
        <v-container v-if="stage == 2"> 
            <v-card flat>
                <v-card-title>
                    <v-icon large class="mr-4">mdi-plus-circle</v-icon>
                    Criar Votação - Adicionar Perguntas
                </v-card-title>
                <v-card-text>
                    <v-form>

                        [TODO]

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
            privatevoting: false,
            stage: 1,


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

        createVoting(){
            // TODO: confirmar todos os campos obrigatorios
            console.log('TODO: method createVoting')
        }
    }
}
</script>