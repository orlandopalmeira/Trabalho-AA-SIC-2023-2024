<template scoped>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container>
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
                        [botoes]
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="createVoting" :disabled="title === '' || description === ''">Criar</v-btn>
                </v-card-actions>
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
            privatevoting: false
        }
    },

    methods: {

        createVoting(){
            // TODO: confirmar todos os campos obrigatorios
            console.log('TODO: method createVoting')
        }
    }
}
</script>