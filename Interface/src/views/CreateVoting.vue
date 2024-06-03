<template>
<AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <v-container v-if="stage == 1"> 
            <CreateVotingStage1 @data="goNext" @leave="leave" :voting="this.voting"/>
        </v-container>
        <v-container v-else-if="stage == 2"> 
            <CreateVotingStage2 @data="goNext" @leave="leave" :questions="this.voting.questions"/>
        </v-container>
</AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import CreateVotingStage1 from '@/components/CreateVoting/CreateVotingStage1.vue';
import CreateVotingStage2 from '@/components/CreateVoting/CreateVotingStage2.vue';
import axios from '@/axios'
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
            voting: {
                title: '',
                description: '',
                enddate: null,
                image: null,
                privatevoting: false,
                questions: []
            }
        }
    },
    methods: {
        openModal(title, message) {
            this.modal = { opened: true, title, message }
        },
        validateVoting(){
            // garantir que haja pelo menos uma pergunta
            if (!this.voting.questions.length) {
                this.openModal('Erro', 'Deverá adicionar pelo menos uma pergunta.');
                return false;
            }
            // garantir que haja pelo menos duas opções em cada pergunta
            if (this.voting.questions.some(question => question.options.length < 2)) {
                this.openModal('Erro', 'Deverá adicionar pelo menos duas opções em cada pergunta.');
                return false;
            }
            return true;
        },
        goNext(data) {
            if(this.stage == 1){
                if (data.title && data.description) {
                    this.voting = {...this.voting, ...data};
                    this.stage = 2
                }
            } else {
                this.voting = {...this.voting, ...data};
                this.createVoting()
            }
        },
        leave(data) {
            if(this.stage == 1){
                this.$router.push('/myvotings')
            } else {
                this.stage = 1
                this.voting.questions = data;
            }
        },
        createVoting(){
            if(this.validateVoting()){
                let formData = new FormData();
                let images = this.extractImages();
                let dataObj = {
                    title: this.voting.title,
                    description: this.voting.description,
                    enddate: this.voting.enddate.toISOString().replace('T', ' ').slice(0,19),
                    image: this.voting.image,
                    privatevoting: this.voting.privatevoting,
                    questions: this.voting.questions
                }
                // Dados da votação
                formData.append('voting', JSON.stringify(dataObj));
                // Imagens
                images.forEach(image => {
                    formData.append('images', image);
                });
                
                axios.post('/votings', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }).then(() => {
                    this.openModal('Sucesso', 'Votação criada com sucesso.');
                    this.$router.push('/myvotings');
                    //TODO: Implementar lógica de redireccionamento para a página de MyVotings ou outra que seja adequada
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
            if(this.voting.image){
                let image_name = 'v_' + this.voting.image.name; // tratamento do nome da imagem para lidar com repetições nesta votação
                images.push(new File([this.voting.image], image_name, {type: this.voting.image.type}));
                this.voting.image = image_name;
            }

            this.voting.questions.forEach((question, question_index) => {
                question.options.forEach((option, option_index) => {
                    if(option.image){
                        let image_name_op = 'q_' + question_index + 'o_' + option_index +  option.image.name; // tratamento do nome da imagem numa opção para lidar com repetições nesta votação
                        images.push(new File([option.image], image_name_op, {type: option.image.type}));
                        option.image = image_name_op;
                    }
                });
            });

            return images;
        }
    },
}
</script>