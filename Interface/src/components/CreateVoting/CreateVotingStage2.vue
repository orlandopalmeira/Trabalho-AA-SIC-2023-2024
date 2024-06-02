<template>
    <v-card flat>
        <v-card-title style="padding: 15px;" >
            <v-icon large class="mr-4">mdi-plus-circle</v-icon>
            Criar Votação - Adicionar Perguntas (2/2)
        </v-card-title>
        <v-card-text>
            <v-form @submit.prevent="goNext">
                <v-card style="background-color: #F2F2F2; margin-bottom: 20px;" v-for="(question,indexQuestion) in questions" :key="indexQuestion">
                    <v-card-title style="padding: 10px;">
                        <v-icon large class="mr-4">mdi-comment-question</v-icon>
                        Pergunta {{ indexQuestion+1 }}:
                    </v-card-title>
                    <v-card-text>
                        <v-text-field
                        prepend-icon="mdi-format-title"
                        label="Pergunta"
                        type="text"
                        v-model="questions[indexQuestion]['description']"
                        :rules="[rules.required]"
                        ></v-text-field>
                        <v-card style="background-color: #F2F2F2;">
                            <v-card-title>
                                Opções:
                            </v-card-title>
                            <v-card-text style="padding: 20px;">
                                <div v-for="(option, indexOption) in questions[indexQuestion].options" :key="indexOption" style="padding: 10px;">
                                    <v-row>
                                        <v-text-field
                                            :label="'Opção ' + (indexOption + 1)"
                                            v-model="questions[indexQuestion].options[indexOption]['description']"
                                            prepend-icon="mdi-form-textbox"
                                            :rules="[rules.required]"
                                        ></v-text-field>
                                        <v-btn icon @click="addImg()" style="margin-left: 10px;">
                                            <v-icon>mdi-image</v-icon>
                                        </v-btn>
                                        <v-btn icon color="error" @click="removeOption(indexQuestion,indexOption)" style="margin-left: 10px;">
                                            <v-icon>mdi-delete</v-icon>
                                        </v-btn>
                                    </v-row>
                                </div>
                                <v-btn color="secondary" @click="addOption(indexQuestion)" style="margin-top: 10px;">                                        
                                    <v-icon left>mdi-plus</v-icon> Adicionar Opção
                                </v-btn>
                                <v-alert
                                    v-if="questions[indexQuestion].options.length < 2"
                                    type="info"
                                    class="mt-4"
                                >
                                    Deverá adicionar pelo menos duas opções.
                                </v-alert>
                            </v-card-text>
                        </v-card>
                        <v-btn color="error" @click="removeQuestion(indexQuestion)" style="margin-top: 10px;">                                        
                            <v-icon left>mdi-delete</v-icon> Remover Pergunta
                        </v-btn>
                    </v-card-text>
                </v-card>

                <v-btn color="primary" @click="addQuestion()" style="margin-bottom: 10px;">                                        
                    <v-icon left>mdi-plus</v-icon> Adicionar Pergunta
                </v-btn>
                <v-alert
                    v-if="questions.length < 1"
                    type="info"
                    class="mt-4"
                >
                    Deverá adicionar pelo menos uma pergunta.
                </v-alert>

                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn
                            color="secondary"
                            @click="leave"
                        >
                            Voltar
                        </v-btn>
                    </v-col>
                    <v-col cols="6" class="text-right">
                        <v-btn
                            color="primary"
                            type="submit"
                        >
                        <span>Submeter</span>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
export default {
    props: {
        questions: {type: Array},
    },
    data() {
        return {
            questions: this.questions,
            rules: {
                required: value => !!value || 'Campo obrigatório.',
            },
        }
    },
    methods: {
        addQuestion() {
            this.questions.push({
                description: '',
                options: [
                    { description: '', image: null },
                    { description: '', image: null }
                ]
            });
        },
        removeQuestion(index) {
            this.questions.splice(index, 1);
        },
        addOption(indexQuestion) {
            this.questions[indexQuestion].options.push({ description: '', image: null });
        },
        removeOption(indexQuestion, indexOption) {
            this.questions[indexQuestion].options.splice(indexOption, 1);
        },
        addImg() {
            console.log('TODO: addImg');
        },
        goNext() {
            this.$emit('data', this.questions);
        },
        leave() {
            this.$emit('leave', this.questions);
        }
    }
}
</script>