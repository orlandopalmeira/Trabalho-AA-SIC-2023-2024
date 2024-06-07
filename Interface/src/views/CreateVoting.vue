<template>
<AuthenticatedLayout>
        <ModalOk 
            :isVisible="modal.opened"
            :title="modal.title"
            :message="modal.message"
            @close-modal="modal.opened=false"/>
        <v-container v-if="stage == 1"> 
            <CreateVotingStage1 @data="goNext" @leave="leave"/>
        </v-container>
        <v-container v-else-if="stage == 2"> 
            <CreateVotingStage2 @data="goNext" @leave="leave" />
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
        validateAspectRatio(file, min, max, stage2info = null) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = (event) => {
                    const img = new Image();
                    img.onload = () => {
                        const aspectRatio = img.width / img.height;
                        if (aspectRatio < min) {
                            let title = 'Erro'
                            if (stage2info) {
                                title += ' na Pergunta ' + (stage2info.question + 1) + ', Opção ' + (stage2info.option + 1);
                            }
                            this.openModal(title, 'Imagem demasiado comprida. Por favor submeta uma imagem mais larga.');
                        }
                        else if (aspectRatio > max) {
                            let title = 'Erro'
                            if (stage2info) {
                                title += ' na Pergunta ' + (stage2info.question + 1) + ', Opção ' + (stage2info.option + 1);
                            }
                            this.openModal(title, 'Imagem demasiado larga. Por favor submeta uma imagem mais comprida.');
                        }
                        resolve(aspectRatio >= min && aspectRatio <= max);
                    };
                    img.onerror = () => {
                        reject(false);
                    };
                    img.src = event.target.result;
                };
                reader.onerror = () => {
                    reject(false);
                };
                reader.readAsDataURL(file);
            });
        },
        async goNext() {
            if(this.stage == 1){
                if (useVotingInfoStore().getTitle && useVotingInfoStore().getDescription) {

                    let image = useVotingInfoStore().getImage;

                    if (image != null) {

                        const isValid = await this.validateAspectRatio(image, 1, 2);

                        if (!isValid) {

                            return;
                        }
                    }

                    this.stage = 2
                }
                // else - Não faz nada, e a interface indica os campos em falta.
            } else {
                let questions = this.useVotingInfoStore().questions;

                for (let i = 0; i < questions.length; i++) {
                    for (let j = 0; j < questions[i].options.length; j++) {

                        if (!questions[i].options[j].image) continue;

                        let imgValid = await this.validateAspectRatio(questions[i].options[j].image, 0.8, 1.2, { question: i, option: j }); 

                        if (!imgValid) {
                            
                            return;
                        }
                    }
                }

                this.createVoting()
            }
        },
        leave() {
            if(this.stage == 1){
                this.$router.push('/myvotings')
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
                    enddate: this.useVotingInfoStore().enddate.toISOString().replace('T', ' ').slice(0,19),
                    image: this.useVotingInfoStore().image,
                    privatevoting: this.useVotingInfoStore().privatevoting,
                    privateSelectedUsers: this.useVotingInfoStore().privateSelectedUsers,
                    showstats: this.useVotingInfoStore().isFinalResultPublic,
                    showstatsrealtime: this.useVotingInfoStore().isIntermediateResultPublic,
                    questions: this.useVotingInfoStore().questions
                }
                // console.log(dataObj.enddate);
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
                }).then((res) => {
                    console.log(res.data);
                    this.openModal('Sucesso', 'Votação criada com sucesso.');
                    setTimeout(() => {
                        useVotingInfoStore().reset(); // Para que os dados saiam e não fiquem na próxima votação
                        let voting_id = res.data.id;
                        console.log("Voting ID: " + voting_id);

                        this.$router.push('/voting/' + voting_id);
                    }, 2000); // Delay in milliseconds (2 seconds in this example)
                    //TODO: Implementar lógica de redireccionamento para a página de MyVotings ou outra que seja adequada
                })
                .catch((error) => {
                    console.log("AQUI ESTÁ O RES");
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
        }
    },
}
</script>