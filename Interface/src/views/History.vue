<template>  
 <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <ModalFiltering 
            :isVisible="modalFiltering.opened"
            @cancel="modalFiltering.opened = false"
            @filter="onFilter"/>
        <v-container>
            <v-card flat style="padding: 10px;" class="dark">
                <v-row>
                    <v-col cols="6">
                        <div class="flex">
                            <v-text-field class="mr-5" v-model="search" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                            <button @click="modalFiltering.opened = true" class="filterbutton">
                                <v-icon>mdi-filter-outline</v-icon> Filtrar
                            </button>
                        </div>
                    </v-col>
                </v-row>
                <LoadingAlert v-if="loadingHistory" message="A carregar as votações, por favor aguarde." />
                <v-data-table v-else class="dark"
                    :headers="headers"
                    :items="processedVotings"
                    :search="search"
                    v-model:sort-by="sortBy"
                    @click:row="rowClicked"
                    hover
                >
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
import ModalFiltering from '@/components/Modais/ModalFiltering.vue'
import ToastManager from '@/components/Toast/ToastManager'
import { API_PATHS } from '@/apiPaths'

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação', sortable: true },
    {                 key: 'description',   title: 'Descrição', sortable: true },
    {                 key: 'creationdate',  title: 'Data', sortable: true },
    {                 key: 'votes',         title: 'Votos', sortable: true },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    name: 'History',

    components: {
        AuthenticatedLayout,
        ModalOk,
        ModalFiltering,
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
            modalFiltering: {
                opened: false
            },
            filters: null,
            loadingHistory: true,
            headers: table_headers,
            historyVotings: [],
            // Default sort configuration
            sortBy: [{ key: 'creationdate', order:'desc' }],
        }
    },

    methods: {
        openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		},
        onFilter(filters) {
            if(Object.values(filters).every(v => v === null)) {
                this.filters = null
            } else {
                this.filters = filters
            }
            this.modalFiltering.opened = false
        },
        rowClicked(event, item) { // Para usar se quisermos clicar na linha inteira da tabela e levar para a votação específica
            // Access the item here using item.item retorna o objeto inputed
            this.$router.push({name: 'voting', params: {id: item.item.id}})
        },
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
            const isPrivateVoting = v => (v.privatevoting === 'mdi-lock')
            if(this.filters) {
                if(this.filters.creationdate) {
                    processedVotings = processedVotings.filter(v => {
                        return v.creationdate >= this.filters.creationdate[0] && v.creationdate <= this.filters.creationdate[1]
                    })
                }
                if(this.filters.enddate) {
                    processedVotings = processedVotings.filter(v => {
                        return v.enddate >= this.filters.enddate[0] && v.enddate <= this.filters.enddate[1]
                    })
                }
                if(this.filters.privatevoting !== null) {
                    processedVotings = processedVotings.filter(v => {
                        return isPrivateVoting(v) === this.filters.privatevoting
                    })
                }
            }
            return processedVotings
        }
    }, 
    created() {
        // lógica do Toast
        let toast_message = this.$route.query.toast_message // mensagem de voto submetido com sucesso vinda da Voting.vue
        if(toast_message){
            ToastManager.show(toast_message, 'success', 3000)
            this.$router.replace({ path: this.$route.path }); // para limpar a rota e não ter aquela query string feia ("?toast_message=...")
        }
        axios.get(API_PATHS.votings)
        .then(response => {
            // filtrar por aqueles em que o user votou
            this.historyVotings = response.data.filter(voting => voting.useralreadyvoted);
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
.filterbutton:hover {
    background-color: rgb(156, 156, 156);
}
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
</style>