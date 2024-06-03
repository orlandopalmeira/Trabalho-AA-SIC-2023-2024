<template>
    <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
    {{ voting }}
</template>

<script>
import ModalOk from '@/components/Modais/ModalOk.vue'

import axios from '@/axios'

export default {
    name: 'Voting',

    components: {
        ModalOk
    },

    data() {
        return {
            loadingVoting: true,
            voting: null,
            modal: {
				opened: false,
				title: '',
				message: ''
			}
        }
    },

    methods: {

        async getVoting() {
            try {
                let response = await axios.get(`/votings/${this.$route.params.id}`)
                this.loadingVoting = false 
                return response.data
            } catch (error) {
                let response = error.response
                console.error(error)
                this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
                return null
            }
        }
    },

    created() {
        this.getVoting()
        .then(voting => {
            this.voting = voting
            this.loadingVoting = false
        }).catch(error => {
            console.error(error)
        })
    }
}
</script>