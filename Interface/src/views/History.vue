<template>  
 <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container>
            <v-card flat style="padding: 10px;" >
                <v-row>
                    <v-col>
                        <div class="flex">
                            <v-text-field class="mr-5" v-model="search" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                            <button class="filterbutton">
                                <v-icon>mdi-filter-outline</v-icon> Filtrar
                            </button>
                        </div>
                    </v-col>
                </v-row>
                <LoadingAlert v-if="loadingHistory" message="A carregar as votações, por favor aguarde." />
                <v-data-table v-else
                :headers="headers"
                :items="processedVotings"
                :search="search"
                hover>
                <template v-slot:[`item.title`]="{ item }">
                    <p>{{ item.title }}</p>
                </template>
                    <template v-slot:[`item.privatevoting`]="{ item }">
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
import axios from '@/axios'

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação' },
    {                 key: 'description',   title: 'Descrição' },
    {                 key: 'creationdate',  title: 'Data' },
    {                 key: 'votes',         title: 'Votos' },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    name: 'History',

    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert
    },

    data() {

        return {
            search: '',
            modal: {
                opened: false,
                title: '',
                message: ''
            },
            loadingHistory: true,
            headers: table_headers,
            historyVotings: []
        }
    },

    methods: {

        openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		}
    },

    computed: {
        processedVotings() {
            let now = new Date().toISOString().replace('T', ' ').slice(0,19)
            let processedVotings = this.historyVotings.map(voting => {
                let active = voting.enddate > now || voting.enddate === null
                return {
                    ...voting,
                    privatevoting: voting.privatevoting ? 'mdi-lock' : 'mdi-lock-open-variant', // Ícone de cadeado fechado ou aberto
                    active: active
                }
            })
            return processedVotings
        }
    }, 

    created() {

        axios.get('/votings')
        .then(response => {
            
            // filtrar por aqueles que ja acabaram e que user votou

            let now = new Date().toISOString().replace('T', ' ').slice(0,19);

            this.historyVotings = response.data.filter(voting => (voting.enddate < now || voting.enddate === null) && voting.useralreadyvoted);

            this.loadingHistory = false
        })
        .catch(error => {
            console.log(error)
        })
    }
}
</script>

<style scoped>
.clickable-button {
  padding: 5px 16px;
  border-radius: 4px;
  transition: color 0.3s ease;
  text-decoration: underline; /* Underline text */
}

.clickable-button:hover {
    color: #1976d2; /* Change color on hover */
    background-color: aliceblue;
    /* box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); */
}
.highlighted-row {
  border-bottom: 2px solid #868686; /* Grey line under the row */
}
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