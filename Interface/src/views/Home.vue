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
                <v-col>
                    <v-text-field style="width: 90%" v-model="searchQuery" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                </v-col>
                <v-col cols="3">
                    <div class="flex">
                        <v-select style="width: 60%"
                            label="Ordenar por"
                            :items="selectItems"
                            variant="outlined"
                            density="compact"
                            v-model="orderBy"
                            clearable
                            hide-details />
                        <v-btn :title="'Ordem ' + (reverseSort? 'descendente' : 'ascendente')" 
                            @click="reverseSort = !reverseSort" 
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
                v-else-if="!loadingVotings && this.votings && this.votings.length > 0 && filteredSortedList.length === 0"
                message="A sua pesquisa não retornou resultados."/>
            <VotingsContainer v-else :votings="filteredSortedList" />
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
            orderBy: 'enddate',
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
                { title: 'Número de votos', value: 'votes' }
            ],
            reverseSort: true,
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
                let response = await axios.get(API_PATHS.votings) // votações a que o user tem acesso
                this.loadingVotings = false 
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
        }
    }, 
    created() {
        this.getVotings()
        .then(votings => {
            this.votings = votings
            this.loadingVotings = false
        }).catch(error => {
            this.votings = []
            this.loadingVotings = false
            console.error(error)
        })
    },
    computed: {
        filteredSortedList(){
            let sortFactor = this.reverseSort ? -1 : 1;
            let filteredList = this.votings.slice();
            if (this.searchQuery && this.searchQuery !== '') {
                const query = this.searchQuery.toLowerCase();
                filteredList = filteredList.filter(voting => {
                    return Object.values(voting).some(fieldValue => 
                        fieldValue && fieldValue.toString().toLowerCase().includes(query)
                    );
                });
            }
            
            if (this.orderBy) {
                filteredList = filteredList.sort((a, b) => {
                    if (this.orderBy === 'enddate') {
                        if(a.enddate === null) { // "a" não tem fim definido
                            return 1*sortFactor;
                        } else if (b.enddate === null) { // "b" não tem fim definido
                            return -1*sortFactor;
                        }
                    }
                    if (a[this.orderBy] < b[this.orderBy]) {
                        return -1*sortFactor;
                    }
                    if (a[this.orderBy] > b[this.orderBy]) {
                        return 1*sortFactor;
                    }
                    return 0;
                });
            }

            return filteredList
        }	
    },
    watch: {
        orderBy(oldValue, newValue) {
            console.log('orderBy: ', oldValue, newValue)
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
</style>