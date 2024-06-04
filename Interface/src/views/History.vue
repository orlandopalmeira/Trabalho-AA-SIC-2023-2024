<template>  
    <AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <LoadingAlert v-if="loadingHistory" message="A carregar o histórico, por favor aguarde." />
    </AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'

import axios from '@/axios'

export default {
    name: 'History',

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

            loadingHistory: true,

            historyVotings: []
        }
    },

    created() {

        axios.get('/votings')
        .then(response => {
            
            // TODO: filtrar por aqueles que ja acabaram
            // TODO: filtrar por aqueles em que user votou

            this.loadingHistory = false
        })
        .catch(error => {
            console.log(error)
        })
    }
}
</script>