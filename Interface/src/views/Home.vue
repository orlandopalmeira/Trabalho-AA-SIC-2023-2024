<template>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
            :buttonText="modal.buttonText"
			@close-modal="redirectToLogin"/>
        <v-container>
            <v-row class="mt-3">
                <v-col cols="5" class="flex">
                    <v-btn @click="handleSearch" icon class="dark mr-2"> <v-icon>mdi-magnify</v-icon> </v-btn>
                    <v-text-field style="width: 90%" v-model="searchQuery" label="Pesquisar" density="compact" hide-details @keyup.enter="handleSearch"/>
                </v-col>
                <v-col cols="3">
                    <div class="flex">
                        <v-select style="width: 30%"
                            label="Ordenar por"
                            :items="selectItems"
                            variant="outlined"
                            density="compact"
                            v-model="orderBy"
                            hide-details 
                            />
                        <v-btn :title="'Ordem ' + (reverseSort? 'descendente' : 'ascendente')" 
                            @click="onclickReverseSort" 
                            class="bg-transparent" 
                            :icon="reverseSort ? 'mdi-arrow-down' : 'mdi-arrow-up'"
                            flat>
                        </v-btn>
                        
                    </div>
                </v-col>
                <v-col class="flex justify-end pr-5 mb-1">
                    <button class="buttoncreatevoting" @click="onClickCreateVoting">
                        <v-icon class="pr-1">mdi-plus</v-icon> Criar votação
                    </button>
                </v-col>
            </v-row>
            <LoadingAlert v-if="loadingVotings" message="A carregar as votações, por favor aguarde." />
            <SimpleAlert
                v-else-if="!loadingVotings && this.votings && this.votings.length > 0 && this.votings.length === 0"
                message="A sua pesquisa não retornou resultados."/>
            <VotingsContainer v-else :votings="this.votings"/>
            <v-row class="mt-4" align="center" justify="center">
                <v-col cols="4">
                    <v-pagination
                    v-model="page"
                    :length="totalPages"
                    :total-visible="4"
                    />
                </v-col>
                <v-col cols="1">
                    <v-select 
                    style="width: 140px;"
                    class="ml-5 mt-2 mb-2"
                    label="Votações por página"
                    :items="[8, 12, 16, 20, 24, 28]"
                    variant="outlined"
                    v-model="itemsPerPage"
                    hide-details
                    />
                </v-col>
            </v-row>
        </v-container>
    </AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import SearchBar from '@/components/SearchBar.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import VotingsContainer from '@/components/HomePage/VotingsContainer.vue'
import SimpleAlert from '@/components/SimpleAlert.vue'

import axios from '@/axios'
import { API_PATHS } from '@/apiPaths'
import { useUserInfoStore } from '@/stores/userInfoStore'

export default {
    name: 'Home',
    components: {
        AuthenticatedLayout,
        SearchBar,
        VotingsContainer,
        ModalOk,
        LoadingAlert,
        SimpleAlert
    },
    data() {
        return {
            loadingVotings: true,
            votings: [],
            searchQuery: '',
            orderBy: useUserInfoStore().homeOrderBy,
            modal: {
				opened: false,
				title: '',
				message: '',
                buttonText: 'daddas'
			},
            selectItems: [
                { title: 'Título', value: 'title' },
                { title: 'Descrição', value: 'description' },
                { title: 'Data de criação', value: 'creationdate' },
                { title: 'Data de fim', value: 'enddate' },
                { title: 'Populariade', value: 'votes' }
            ],
            reverseSort: useUserInfoStore().homeReverseSort,
            page: 1,
            itemsPerPage: parseInt(useUserInfoStore().homeItemsPerPage),
            totalPages: 0,
        }
    },
    methods: {
        openModal(title, message, buttonText = 'Ok') {
			this.modal = {
				opened: true,
				title: title,
				message: message,
                buttonText: buttonText
			}
		},
        redirectToLogin() {
            this.$router.push({name: 'login'})
        },
        onClickCreateVoting(){
            this.$router.push({name: 'createvoting'});
        },
        async getVotings() {
            try {
                let sort = this.reverseSort ? "desc" : "asc"
                let response = await axios.get(API_PATHS.votings(this.searchQuery, this.orderBy, sort, this.page, this.itemsPerPage)) // votações a que o user tem acesso
                return response.data
            } catch (error) {
                let status = error.response.status
                if (status === 401) {
                    // this.$router.push({name: 'login'})
                    this.openModal('Credenciais inválidas','Faça primeiro login para aceder às votações!', "Ir para login")
                }
                else{
                    console.error(error)
                    this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
                    return []
                }
            }
        },
        handleGetVotings(){
            this.loadingVotings = true
            this.getVotings()
            .then(response => {
                this.votings = response.votings
                this.totalPages = response.totalPages
                this.loadingVotings = false
            }).catch(error => {
                this.votings = []
                this.loadingVotings = false
                console.error(error)
            })
        },
        onclickReverseSort(){
            this.reverseSort = !this.reverseSort
            useUserInfoStore().setHomeReverseSort(this.reverseSort)
            this.handleGetVotings()
        },
        handlePageChanged(page) {
            this.page = page
            this.handleGetVotings()
        },
        handleSearch() {
            this.handleGetVotings()
        }
    }, 
    created() {
        useUserInfoStore().setHomeOrderBy(this.orderBy)
        useUserInfoStore().setHomeReverseSort(this.reverseSort)
        this.handleGetVotings()
    },
    watch: {
        orderBy(newValue, oldValue) {
            if (newValue != null) {
                useUserInfoStore().setHomeOrderBy(newValue)
                this.handleGetVotings()
            }
        },
        page(newValue) {
            this.handlePageChanged(newValue)
        },
        itemsPerPage(newValue) {
            if (newValue != null) {
                useUserInfoStore().setHomeItemsPerPage(newValue)
                this.handleGetVotings()
            }
        }
    }
}
</script>

<style scoped>
.flex {
    display: flex;
}
.space-between {
    justify-content: space-between;
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
</style>