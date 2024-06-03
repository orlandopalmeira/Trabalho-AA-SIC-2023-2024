<template>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container>
            <div class="flex space-between mt-5 mb-5">
                <SearchBar :onSearchFromParent="onSearch" class="mr-5" />
                <OrderBy 
                    :fields="['Nenhum','Teste1', 'Teste2', 'Teste3']" 
                    :onSortFromParent="onSort"/>
            </div>
            <LoadingAlert v-if="loadingVotings" message="A carregar as votações, por favor aguarde." />
            <VotingsContainer v-else :votings="votings" />
        </v-container>
    </AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import SearchBar from '@/components/SearchBar.vue'
import OrderBy from '@/components/OrderBy.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import VotingsContainer from '@/components/HomePage/VotingsContainer.vue'

import axios from '@/axios'

export default {
    name: 'Home',
    components: {
        AuthenticatedLayout,
        SearchBar,
        OrderBy,
        VotingsContainer,
        ModalOk,
        LoadingAlert
    },
    data() {
        return {
            loadingVotings: true,
            votings: [],
            // votings: temp_votings
            modal: {
				opened: false,
				title: '',
				message: ''
			}
        }
    },
    methods: {
        onSearch(searchString) {
            //TODO: lógica de pesquisa
            alert(searchString)
        },
        onSort(field) {
            // TODO: lógica de ordenação
            alert(field)
        },
        openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		},
        async getVotings() {
            try {
                let response = await axios.get('/votings') // votações a que o user tem acesso
                this.loadingVotings = false 
                return response.data
            } catch (error) {
                let status = error.response.status
                if (status === 401) {
                    this.$router.push('/login')
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
            console.error(error)
        })
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