<template>
<AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <v-container v-if="stage == 1"> 
            <CreateVotingStage1 @data="this.stage = 2" @leave="leave"/>
        </v-container>
        <v-container v-else-if="stage == 2"> 
            <CreateVotingStage2 @data="createVoting" @leave="leave" />
        </v-container>
</AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import CreateVotingStage1 from '@/components/CreateVoting/CreateVotingStage1.vue';
import CreateVotingStage2 from '@/components/CreateVoting/CreateVotingStage2.vue';
import { useVotingInfoStore } from '@/stores/votingInfoStore';
import axios from '@/axios'
import { API_PATHS } from '@/apiPaths';

export default {
    components: {
        AuthenticatedLayout,
        ModalOk,
        CreateVotingStage1,
        CreateVotingStage2
    },
    data() {
        return {
            stage: 1,
            modal: { opened: false, title: '', message: '' },
            useVotingInfoStore
        }
    },
    methods: {
        openModal(title, message) {
            this.modal = { opened: true, title, message }
        },
        validateVoting(){
            // garantir que haja pelo menos uma pergunta
            if (!this.useVotingInfoStore().getQuestions.length) {
                this.openModal('Erro', 'Deverá adicionar pelo menos uma pergunta.');
                return false;
            }
            // garantir que haja pelo menos duas opções em cada pergunta
            if (this.useVotingInfoStore().questions.some(question => question.options.length < 2)) {
                this.openModal('Erro', 'Deverá adicionar pelo menos duas opções em cada pergunta.');
                return false;
            }
            return true;
        },
        leave() {
            if(this.stage == 1){
                this.$router.push({name: 'myvotings'})
            } else {
                this.stage = 1
            }
        },
        error(data) {
            this.openModal('Erro', data);
        },
        createVoting(){
            if(this.validateVoting()){
                let formData = new FormData();
                let images = this.extractImages();
                let dataObj = {
                    title: this.useVotingInfoStore().title,
                    description: this.useVotingInfoStore().description,
                    //enddate: this.useVotingInfoStore().enddate.toISOString().replace('T', ' ').slice(0,19),
                    enddate: this.useVotingInfoStore().enddate ? this.useVotingInfoStore().enddate.replace('T', ' ') + ':00' : null,
                    image: this.useVotingInfoStore().image,
                    privatevoting: this.useVotingInfoStore().privatevoting,
                    privateSelectedUsers: this.useVotingInfoStore().privateSelectedUsers,
                    showstats: this.useVotingInfoStore().isFinalResultPublic,
                    showstatsrealtime: this.useVotingInfoStore().isIntermediateResultPublic,
                    secretvotes: this.useVotingInfoStore().secretvotes,
                    questions: this.useVotingInfoStore().questions
                }
                // Dados da votação
                formData.append('voting', JSON.stringify(dataObj));
                // Imagens
                images.forEach(image => {
                    formData.append('images', image);
                });
                
                axios.post(API_PATHS.postVotings, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }).then((res) => {
                    console.log(res.data);
                    let voting_id = res.data.id;
                    useVotingInfoStore().reset();
                    this.$router.push({name: 'voting', params: { id: voting_id }, query: { toast_message: 'Votação criada com sucesso.' }});
                    // this.openModal('Sucesso', 'Votação criada com sucesso.', voting_url, 3);
                })
                .catch((error) => {
                    console.error(error);
                    this.openModal('Erro', 'Ocorreu um erro ao criar a votação.');
                });
            }
        },
        extractImages(){
            // Extrai todas as imagens da votação
            let images = [];
            if(this.useVotingInfoStore().image){
                let image_name = 'v_' + this.useVotingInfoStore().image.name; // tratamento do nome da imagem para lidar com repetições nesta votação
                images.push(new File([this.useVotingInfoStore().image], image_name, {type: this.useVotingInfoStore().image.type}));
                this.useVotingInfoStore().image = image_name;
            }

            this.useVotingInfoStore().questions.forEach((question, question_index) => {
                question.options.forEach((option, option_index) => {
                    if(option.image){
                        let image_name_op = 'q_' + question_index + 'o_' + option_index +  option.image.name; // tratamento do nome da imagem numa opção para lidar com repetições nesta votação
                        images.push(new File([option.image], image_name_op, {type: option.image.type}));
                        option.image = image_name_op;
                    }
                });
            });

            return images;
        },
    },
}
</script>