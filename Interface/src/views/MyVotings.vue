<template scoped>
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
            <v-card flat style="padding: 10px;" class="dark" >
                <v-row>
                    <v-col>
                        <div class="flex">
                            <v-text-field class="mr-5" v-model="search" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                            <button @click="modalFiltering.opened = true" class="filterbutton">
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
                <v-data-table v-else class="dark"
                :headers="headers"
                :items="processedVotings"
                :search="search"
                @click:row="rowClicked"
                hover>
                <template v-slot:[`item.title`]="{ item }">
                    <p :title="'Descrição: '+item.description" style="max-width: 200px; font-weight: 500; ">
                        {{ item.title }}
                    </p>
                </template>
                    <template v-slot:[`item.status`]="{ item }">
                        <p v-if="item.active" style="color: green">Activa</p>
                        <p v-else style="color: red">Terminada</p>
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
import router from '@/router'
import ModalFiltering from '@/components/Modais/ModalFiltering.vue'
import { API_PATHS } from '@/apiPaths'

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação' },
    // {                 key: 'description',   title: 'Descrição' },
    {                 key: 'creationdate',  title: 'Data' },
    {                 key: 'status',        title: 'Estado'},
    {                 key: 'votes',         title: 'Votos' },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert,
        ModalFiltering
    },
    data() {
        return {
            search: '',
            loadingVotings: true,
            votings: [],
            headers: table_headers,
            modal: {
				opened: false,
				title: '',
				message: ''
			},
            modalFiltering: {
                opened: false
            },
            filters: null
        }
    },
    methods: {
        onClickCreateVoting(){
            router.push({name: 'createvoting'});
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
                const response = await axios.get(API_PATHS.userVotings)
                return response.data
            } catch (error) {
                let response = error.response
                //this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
                console.log(error);
                return []
            }
        },
        rowClicked(event, item) { // Para usar se quisermos clicar na linha inteira da tabela e levar para a votação específica
            // Access the item here using item.item retorna o objeto inputed
            // console.log(item.item.id);
            // Aceder à pagina do item clicado.
            // this.$router.push(`/voting/${item.item.id}`)
            this.$router.push({name: 'voting', params: {id: item.item.id}})
        },
        votingClicked(id){
            // console.log(id)
            // this.$router.push(`/voting/${id}`)
            this.$router.push({name: 'voting', params: {id: id}})
        },
        formatDateTime(dateTimeString) {
            // 2024-06-04 16:18:07
            let parts = dateTimeString.split(" ");

            let datePart = parts[0];
            let timePart = parts[1].split(":");

            let formattedTime = timePart[0] + "h" + timePart[1] + "m";

            let formattedDateTime = datePart + " " + formattedTime;

            return formattedDateTime;
        },
        onFilter(filters) {
            if(Object.values(filters).every(v => v === null)) {
                this.filters = null
            } else {
                this.filters = filters
            }
            this.modalFiltering.opened = false
        }
    },
    computed: {
        processedVotings() {
            let now = new Date().toISOString().replace('T', ' ').slice(0,19)
            let processedVotings = this.votings.map(voting => {
                let active = voting.enddate > now || voting.enddate === null
                // console.log(voting.creationdate)
                return {
                    ...voting,
                    creationdate: this.formatDateTime(voting.creationdate),
                    privatevoting: voting.privatevoting ? 'mdi-lock' : 'mdi-lock-open-variant',
                    active: active,
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
        this.getVotings()
        .then(votings => {
            this.votings = votings
            this.loadingVotings = false
        }).catch(error => {
            console.error(error)
        })
    }
}
</script>
<style scoped>
.clickable-button {
    padding: 5px 16px;
    margin-top: 10px;
    margin-bottom: 10px;
    border-radius: 4px;
    transition: color 0.3s ease;
    text-align: left; /* Align text to the left */
    background-color: rgb(231, 244, 255);
}

.clickable-button:hover {
    color: #1976d2; /* Change color on hover */
    text-decoration: underline;
    background-color: rgb(196, 221, 241);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
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