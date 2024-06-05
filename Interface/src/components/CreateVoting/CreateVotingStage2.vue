<template>
    <PreviewVoting
        :isVisible="modal.opened"
        @close-modal="closePreview"/>
    <v-card class="dark" flat>
        <v-card-title style="padding: 15px;" >
            <v-icon large class="mr-4">mdi-plus-circle</v-icon>
            <b>{{ useVotingInfoStore().title }}</b> - Adicionar Perguntas
        </v-card-title>
        <v-card-text>
            <v-form @submit.prevent="goNext">
                <v-card class="dark-light mb-5"  style="background-color: #F2F2F2;" v-for="(question,indexQuestion) in useVotingInfoStore().questions" :key="indexQuestion">
                    <v-card-title class="dark">
                        <div style="display: flex; align-items: center;">
                            <v-icon large class="mb-3 ml-3 mr-2">mdi-comment-question</v-icon> 
                            <v-text-field 
                                :label="'Pergunta ' + (indexQuestion + 1)"
                                type="text"
                                v-model="useVotingInfoStore().questions[indexQuestion]['description']"
                                :rules="getFieldRules('question')"
                                style="padding: 10px;"
                            ></v-text-field>
                            <v-btn color="error" @click="removeQuestion(indexQuestion)" size="small" class="mb-15 ml-15" title="Eliminar Pergunta">
                                <v-icon left>mdi-delete</v-icon>
                            </v-btn>
                        </div>
                    </v-card-title>
                    <v-card-text class="dark">
                        <v-card class="dark" style="background-color: #F2F2F2;">
                            <v-card-title>
                                Opções:
                            </v-card-title>
                            <v-card-text style="padding: 20px;">
                                <div v-for="(option, indexOption) in useVotingInfoStore().questions[indexQuestion].options" :key="indexOption" style="padding: 10px;">
                                    <v-row>
                                        <v-text-field
                                            :label="'Opção ' + (indexOption + 1)"
                                            v-model="useVotingInfoStore().questions[indexQuestion].options[indexOption]['description']"
                                            prepend-icon="mdi-form-textbox"
                                            :rules="getFieldRules('option')"
                                        ></v-text-field>
                                        <UploadIconButton 
                                            class="ml-2" 
                                            @image-uploaded="(image) => addImg(indexQuestion, indexOption, image)" 
                                            @image-removed="() => {removeOptionImg(indexQuestion, indexOption)}"
                                            :imageprops="getOptionImage(indexQuestion,indexOption)"/>
                                        <v-btn icon color="error" @click="removeOption(indexQuestion,indexOption)" style="margin-left: 10px;">
                                            <v-icon>mdi-delete</v-icon>
                                        </v-btn>
                                    </v-row>
                                </div>
                                <v-btn color="secondary" @click="addOption(indexQuestion)" style="margin-top: 10px;">                                        
                                    <v-icon left>mdi-plus</v-icon>Opção
                                </v-btn>
                                <v-alert
                                    v-if="useVotingInfoStore().questions[indexQuestion].options.length < 2"
                                    type="info"
                                    class="mt-4"
                                >
                                    Deverá adicionar pelo menos duas opções.
                                </v-alert>
                            </v-card-text>
                        </v-card>
                    </v-card-text>
                </v-card>

                <v-btn color="primary" @click="addQuestion()" style="margin-bottom: 10px;">                                        
                    <v-icon left>mdi-plus</v-icon>Pergunta
                </v-btn>
                <v-alert
                    v-if="useVotingInfoStore().questions.length < 1"
                    type="info"
                    class="mt-4"
                >
                    Deverá adicionar pelo menos uma pergunta.
                </v-alert>

                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn
                            color="red"
                            @click="leave"
                        >
                            Voltar
                        </v-btn>
                    </v-col>
                    <v-col cols="6" class="text-right">
                        <v-btn density="comfortable" icon="mdi-eye" :disabled="!areAllRulesMet" @click="openPreview" style="margin-right: 5px;"/>
                        <v-btn color="primary" type="submit" :disabled="!areAllRulesMet" > 
                            <span>Submeter</span>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
import { useVotingInfoStore } from '@/stores/votingInfoStore';
import UploadIconButton from '@/components/CreateVoting/UploadIconButton.vue';
import PreviewVoting from '@/components/Modais/PreviewVoting.vue';
export default {
    components: {
        UploadIconButton,
        PreviewVoting
    },
    emits: ['data', 'leave'],
    data() {
        return {
            useVotingInfoStore,
            // useVotingInfoStore().questions: useVotingInfoStore().questions,
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength100: value => (value || '').length <= 100 || 'Máximo de 100 caracteres.'
            },
            modal: {
                opened: false
            }
        }
    },
    methods: {
        addQuestion() {
            this.useVotingInfoStore().questions.push({
                description: '',
                options: [
                    { description: '', image: null },
                    { description: '', image: null }
                ]
            });
        },
        removeQuestion(index) {
            this.useVotingInfoStore().questions.splice(index, 1);
        },
        addOption(indexQuestion) {
            this.useVotingInfoStore().questions[indexQuestion].options.push({ description: '', image: null });
        },
        removeOption(indexQuestion, indexOption) {
            this.useVotingInfoStore().questions[indexQuestion].options.splice(indexOption, 1);
        },
        addImg(indexQuestion, indexOption, image) {
            this.useVotingInfoStore().questions[indexQuestion].options[indexOption].image = image;
        },
        removeOptionImg(indexQuestion, indexOption) {
            this.useVotingInfoStore().questions[indexQuestion].options[indexOption].image = null;
        },
        getOptionImage(indexQuestion, indexOption){
            return this.useVotingInfoStore().questions[indexQuestion].options[indexOption].image;
        },
        validateAspectRatio(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = (event) => {
                    const img = new Image();
                    img.onload = () => {
                        const aspectRatio = img.width / img.height;
                        resolve(aspectRatio >= 0.8 && aspectRatio <= 1.2);
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
        getFieldRules(field) {
            // Criei esta função para o botão de submit ficar disabled caso as regras não sejam cumpridas. 
            // Com esta função, podemos definir aqui as regras para cada campo sem ter de alterar outros lados do código para alterar as condições do botão estar disabled.
            let rules = [];
            // caso venham a surgir mais fields, basta adicionar aqui as regras para esse field. E também adicionar o seu nome, nos fields da função areAllRulesMet.
            switch (field) {
                case 'question':
                rules = [this.rules.required];
                break;
                case 'option':
                rules = [this.rules.required, this.rules.maxlength100];
                break;
                default:
                break;
            }
            return rules;
        },
        goNext() {
            this.$emit('data');
        },
        leave() {
            this.$emit('leave');
        },
        openPreview() {
            this.modal.opened = true;
        },
        closePreview() {
            this.modal.opened = false;
        }
    },
    computed: {
        areAllRulesMet() {
            let questions = this.useVotingInfoStore().questions;
            console.log(questions)

            for (let i = 0; i < questions.length; i++) {
                for (let j = 0; j < questions[i].options.length; j++) {

                    if (!this.getFieldRules("option").every(rule => rule(questions[i].options[j].description)===true )) {
                        return false;
                    }
                    
                    if (questions[i].options[j].image){
                        this.validateAspectRatio(questions[i].options[j].image, 0.8, 1.2).then((res) => {
                            if (!res) {
                                // this.openModal('Erro', 'Erro na Opção ' + (j+1) + ' da Pergunta ' + (i+1) + ': A imagem deve ter uma proporção entre 0.8 e 1.2 (largura / altura).');
                                return False;
                            }
                        })
                    }
                }
            }
            return true;
        },
    },
}
</script>
<style scoped>
.dark-mode .dark {
    background-color: #15202b !important;
    color: white;
}
.dark-mode .dark-light {
    background-color: #3f4f6e;
    color: white;
}
</style>