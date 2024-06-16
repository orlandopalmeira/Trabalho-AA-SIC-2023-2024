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
            <v-card flat class="dark pa-2 rounded-xl">
                <v-row class="pa-2">
                    <v-col cols="6">
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
                                <v-icon class="pr-1">mdi-plus</v-icon> Criar votação
                            </button>
                        </div>
                    </v-col>
                </v-row>
                <LoadingAlert v-if="loadingVotings" message="A carregar as votações, por favor aguarde." />
                <v-data-table v-else class="dark"
                    :headers="headers"
                    :items="processedVotings"
                    :search="search"
                    :items-per-page="itemsPerPage"
                    v-model:sort-by="sortInfo"
                    @click:row="rowClicked"
                    hover
                    no-data-text="Sem votações"
                >
                    <template v-slot:bottom>
                        <v-row class="mt-2" align="center" justify="center">
                            <v-col cols="4">
                                <v-pagination
                                    class="width mt-2"
                                    v-model="page"
                                    :length="totalPages"
                                    :total-visible="4"
                                />
                            </v-col>
                            <v-col cols="1" class="mt-2">
                                <v-select
                                    label="Votações por página"
                                    :items="[10, 20, 30, 40, 50]"
                                    variant="outlined"
                                    density="compact"
                                    v-model="itemsPerPage"
                                    hide-details
                                    style="width: 140px;"
                                />
                            </v-col>
                        </v-row>
                    </template>
                    <template v-slot:[`item.title`]="{ item }">
                        <p :title="'Descrição: '+item.description" style="max-width: 200px; font-weight: 500; ">
                            {{ item.title }}
                        </p>
                    </template>
                    <template v-slot:[`item.status`]="{ item }">
                        <p v-if="item.active" style="color: green">Em Progresso</p>
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
import ToastManager from '@/components/Toast/ToastManager'
import { useUserInfoStore } from '@/stores/userInfoStore'

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação', sortable: true  },
    // {                 key: 'description',   title: 'Descrição', sortable: true  },
    {                 key: 'creationdate',  title: 'Data de Criação', sortable: true },
    {                 key: 'status',        title: 'Estado', sortable: false },
    {                 key: 'votes',         title: 'Votos', sortable: true },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    props: {
        votingsRoute: {
            type: String,
            required: true,
            // default: API_PATHS.userVotings
        }
    },
    components: {
        AuthenticatedLayout,
        ModalOk,
        ModalFiltering,
        LoadingAlert,
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
            filters: null,

            // Default sort configuration
            sortInfo: [{ key: 'creationdate', order:'desc' }],
            totalPages: 0,
            page: 1,
            itemsPerPage: useUserInfoStore().historyItemsPerPage,
        }
    },
    created() {
        // lógica do Toast
        let toast_message = this.$route.query.toast_message // mensagem de voto submetido com sucesso vinda da Voting.vue
        if(toast_message){
            ToastManager.show(toast_message, 'success', 3000)
            this.$router.replace({ path: this.$route.path }); // para limpar a rota e não ter aquela query string feia ("?toast_message=...")
        }
        const params_ = {
            term: this.search,
            orderBy: this.sortInfo[0]?.key ?? "",
            order: this.sortInfo[0]?.order ?? "",
            page: this.page,
            votings_per_page: this.itemsPerPage,
        }

        // Obter as votações pedidas
        axios.get(this.votingsRoute, { params: params_ })
            .then(response => {
                this.votings = response.data.votings;
                this.totalPages = response.data.totalPages
                this.loadingVotings = false
            })
            .catch(error => {
                console.error(error)
            }
        )
    },
    watch: {
        search(){
            this.fetchData();
        },
        sortInfo() {
            this.fetchData();
        },
        page() {
            this.fetchData();
        },
        itemsPerPage() {
            useUserInfoStore().setHistoryItemsPerPage(this.itemsPerPage)
            this.fetchData();
        },
        filters(newFilters, oldFilters) { //Talvez fazer um if new === old, não fazer fetch
            this.fetchData();
        },
    },
    methods: {
        async fetchData() {
            try {
                const time_start = " 00:00:00"
                const time_end = " 23:59:59"
                const params_ = {
                    term: this.search,
                    orderBy: this.sortInfo[0]?.key ?? "",
                    order: this.sortInfo[0]?.order ?? "",
                    page: this.page,
                    votings_per_page: this.itemsPerPage,
                    creationdate_start: this.filters?.creationdate?.[0] != null ? this.filters.creationdate[0] + time_start : "",
                    creationdate_end: this.filters?.creationdate?.[1] != null ? this.filters.creationdate[1] + time_end : "",
                    enddate_start: this.filters?.enddate?.[0] != null ? this.filters.enddate[0] + time_start : "",
                    enddate_end: this.filters?.enddate?.[1] != null ? this.filters.enddate[1] + time_end : "",
                    in_progress: this.filters?.in_progress ?? "",
                    privatevoting: this.filters?.privatevoting ?? ""
                };
                const response = await axios.get(this.votingsRoute, { params: params_ });
                this.votings = response.data.votings;
                this.totalPages = response.data.totalPages
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
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
        rowClicked(event, item) { // Para usar se quisermos clicar na linha inteira da tabela e levar para a votação específica
            // Access the item here using item.item retorna o objeto inputed
            this.$router.push({name: 'voting', params: {id: item.item.id}})
        },
        // votingClicked(id){
        //     this.$router.push({name: 'voting', params: {id: id}})
        // },
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
            let now = new Date()
            let processedVotings = this.votings.map(voting => {
                let enddate = voting.enddate ? new Date(voting.enddate) : null
                let active = enddate > now || enddate === null
                return {
                    ...voting,
                    creationdate: this.formatDateTime(voting.creationdate),
                    privatevoting: voting.privatevoting ? 'mdi-lock' : 'mdi-lock-open-variant',
                    active: active,
                }
            })

            // const isPrivateVoting = v => (v.privatevoting === 'mdi-lock')
            // if(this.filters) {
            //     if(this.filters.creationdate) {
            //         processedVotings = processedVotings.filter(v => {
            //             return v.creationdate >= this.filters.creationdate[0] && v.creationdate <= this.filters.creationdate[1]
            //         })
            //     }
            //     if(this.filters.enddate) {
            //         processedVotings = processedVotings.filter(v => {
            //             return v.enddate >= this.filters.enddate[0] && v.enddate <= this.filters.enddate[1]
            //         })
            //     }
            //     if(this.filters.privatevoting !== null) {
            //         processedVotings = processedVotings.filter(v => {
            //             return isPrivateVoting(v) === this.filters.privatevoting
            //         })
            //     }
            // }
            return processedVotings
        }
    }, 
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