<template scoped>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container>
            <v-card flat>
                <v-row>
                    <v-col>
                        <div class="flex">
                            <v-text-field class="mr-5" v-model="search" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                            <button class="filterbutton">
                                <v-icon>mdi-filter-outline</v-icon> Filtrar
                            </button>
                        </div>
                    </v-col>
                    <v-col>
                        <div class="flex justify-end" style="width: 100%">
                            <button class="buttoncreatevoting" @click="onClickCreateVoting">
                                <v-icon class="plus-icon">mdi-plus</v-icon> Criar votação
                            </button>
                        </div>
                    </v-col>
                </v-row>
                
                <LoadingAlert v-if="loadingVotings" message="A carregar as votações, por favor aguarde." />
                <v-data-table v-else
                :headers="headers"
                :items="processedVotings"
                :search="search">
                    <template v-slot:item.status="{ item }">
                        <p v-if="item.status==='active'" style="color: green">Activa</p>
                        <p v-else style="color: red">Terminada</p>
                    </template>

                    <template v-slot:item.privatevoting="{ item }">
                        <v-icon>{{ item.privatevoting }}</v-icon>
                    </template>
                </v-data-table>

            </v-card>
        </v-container>
    </AuthenticatedLayout>
</template>
<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import { useUserInfoStore } from '@/stores/userInfoStore'
import axios from '@/axios'

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação' },
    {                 key: 'description',   title: 'Descrição' },
    {                 key: 'creationdate',  title: 'Data' },
    {                 key: 'status',        title: 'Estado'},
    {                 key: 'votes',         title: 'Votos' },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert
    },
    data() {
        return {
            search: '',
            loadingVotings: true,
            votings: [],//temp_votings,
            headers: table_headers,
            modal: {
				opened: false,
				title: '',
				message: ''
			},
            useUserInfoStore
        }
    },
    methods: {
        onClickCreateVoting(){
            //TODO: redirecionar para a página de criação de votação
            alert('Not implemented yet')
        },
        openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		},
        async getVotings(){
            this.loadingVotings = false;
            try {
                let user_id = this.useUserInfoStore().getUserId
                const response = await axios.get(`/votings/user/${user_id}`)
                return response.data
            } catch (error) {
                console.log(error);
                return []
            }
        }
    },
    computed: {
        processedVotings() {
            return this.votings.map(voting => {
                return {
                    ...voting,
                    privatevoting: voting.privatevoting ? 'mdi-lock' : 'mdi-lock-open-variant', // Ícone de cadeado fechado ou aberto
                    votes: 0 //TODO: Esta contagem tem de ser feita no servidor aplicacional
                }
            })
        }
    }, 
    created() {
        this.getVotings()
        .then(votings => {
            this.votings = votings
            this.loadingVotings = false
        }).catch(error => {
            let response = error.response
            this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
            console.error(error)
        })
    }
}
</script>
<style scoped>
.flex {
    display: flex;
    
}
.plus-icon {
    color: rgb(0, 162, 255);
    background-color: white;
    border-radius: 50%;
    margin-right: 5px;
}
.justify-end {
    justify-content:end;
}
.buttoncreatevoting {
    background-color: #4CAF50; /* Green */
    border: none;
    color: white;
    padding: 10px 10px;
    text-align: center;
    text-decoration: none;
    font-size: 16px;
    cursor: pointer;
    border-radius: 12px;
    display: flex;
    align-items: center;
}
.buttoncreatevoting:hover {
    background-color: #45a049;
}
.filterbutton {
    border: 2px solid rgb(156, 156, 156);
    border-radius: 12px;
    padding: 8px 8px;
}
</style>