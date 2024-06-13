<template>
    <v-card class="dark" flat>
        <v-card-title style="padding: 15px;">
            <v-icon large class="mr-4">mdi-plus-circle</v-icon>
            <b>Criar Votação</b> - Informações Gerais
        </v-card-title>
        <v-card-text>
            <v-form @submit.prevent="goNext">
                <v-text-field
                    id="title"
                    prepend-icon="mdi-format-title"
                    name="title"
                    label="Título"
                    type="text"
                    v-model="useVotingInfoStore().title"
                    :rules="getFieldRules('title')"
                ></v-text-field>
                <v-textarea
                    id="description"
                    prepend-icon="mdi-text"
                    name="description"
                    label="Descrição"
                    type="text"
                    v-model="useVotingInfoStore().description"
                    :rules="getFieldRules('description')"
                ></v-textarea>
                <v-text-field
                    type="datetime-local"
                    prepend-icon="mdi-calendar"
                    v-model="useVotingInfoStore().enddate"
                    label="Data do fim da votação"
                    :min="new Date().toISOString().slice(0,16)"
                    :rules="getFieldRules('enddate')"
                    required
                >
                </v-text-field>
                <div class="mb-4">
                    <v-file-input
                        id="image"
                        prepend-icon="mdi-image"
                        name="image"
                        label="Imagem (opcional)"
                        v-model="useVotingInfoStore().image"
                        accept="image/*"
                        @change="onImageChange"
                    />
                </div>
                <div v-if="useVotingInfoStore().imageUrl" class="img_container">
                    <div class="img_card">
                        <img
                            :src="useVotingInfoStore().imageUrl"
                            alt="Capa da votação"
                            class=""
                        />
                    </div>
                    <p class="title">{{ useVotingInfoStore().title ? useVotingInfoStore().title : "Título" }}</p>
                </div>
                <v-expansion-panels>
                    <v-expansion-panel title="Definições de Privacidade" class="dark-light">
                        <v-expansion-panel-text>
                            <div style="display: flex;">
                                <v-checkbox
                                    id="privatevoting"
                                    name="privatevoting"
                                    v-model="useVotingInfoStore().privatevoting"
                                    class="mr-4"
                                >
                                    <template v-slot:label>
                                        <div style="display: flex;">
                                            Votação Privada
                                            <v-icon :title="help.privatevoting" class="ml-2">mdi-information</v-icon>
                                        </div>
                                    </template>
                                </v-checkbox>
                                <v-autocomplete
                                    v-if="useVotingInfoStore().privatevoting"
                                    ref="autocomplete"
                                    label="Selecione os utilizadores que deseja permitir votar"
                                    v-model="useVotingInfoStore().privateSelectedUsers"
                                    :items="usersMatched"
                                    :rules="getFieldRules('privatevoting')"
                                    @update:search="onSearch"
                                    item-title="name"
                                    item-value="id"
                                    chips
                                    closable-chips
                                    multiple
                                    no-data-text="Introduza um nome válido"
                                    autofocus
                                    @update:modelValue="onUpdateModelValue"
                                >
                                    <template v-slot:chip="{ props, item }">
                                        <v-chip
                                        v-bind="props"
                                        :prepend-avatar="item.raw.avatar"
                                        :text="item.raw.name"
                                        />
                                    </template>
                                    <template v-slot:item="{ props, item }">
                                        <v-list-item
                                        v-bind="props"
                                        :prepend-avatar="item.raw.avatar"
                                        :subtitle="item.raw.email"
                                        :title="item.raw.name"
                                        />
                                    </template>
                                </v-autocomplete>
                            </div>
                            <v-checkbox
                                id="finalresultpublic"
                                name="finalresultpublic"
                                v-model="useVotingInfoStore().isFinalResultPublic"
                                hide-details
                            >
                                <template v-slot:label>
                                    <div style="display: flex;">
                                        Publicar resultados finais
                                        <v-icon :title="help.finalResultPublic" class="ml-2">mdi-information</v-icon>
                                    </div>
                                </template>
                            </v-checkbox>
                            <v-checkbox
                                id="intermediateresultpublic"
                                name="intermediateresultpublic"
                                v-model="useVotingInfoStore().isIntermediateResultPublic"
                                hide-details
                            >
                                <template v-slot:label>
                                    <div style="display: flex;">
                                        Publicar resultados intermédios
                                        <v-icon :title="help.intermediateResultPublic" class="ml-2">mdi-information</v-icon>
                                    </div>
                                </template>
                            </v-checkbox>
                            <v-checkbox
                                id="secretvotes"
                                name="secretvotes"
                                v-model="useVotingInfoStore().secretvotes"
                            >
                                <template v-slot:label>
                                    <div style="display: flex;">
                                        Votos secretos
                                        <v-icon :title="help.secretvotes" class="ml-2">mdi-information</v-icon>
                                    </div>
                                </template>
                            </v-checkbox>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>
                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn color="error" @click="leave"> Voltar </v-btn>
                    </v-col>
                    <v-col cols="6" class="text-right">
                        <v-btn color="primary" type="submit" :disabled="!areAllRulesMet"> Seguinte </v-btn>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
import axios from '@/axios';
import { API_PATHS } from '@/apiPaths';
import { GEN_IMAGES } from '@/genImages';
import { useVotingInfoStore } from '@/stores/votingInfoStore';
import { useUserInfoStore } from '@/stores/userInfoStore';

export default {
    emits: ['data', 'leave'],
    data() {
        return {
            useVotingInfoStore,
            usersMatched: [],
            std_image: './kitten.png',
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength50: value => (value && value.length <= 50) || 'Máximo de 50 caracteres.',
                maxlength500: value => (value && value.length <= 500) || 'Máximo de 500 caracteres.',
                atLeastOnePrivateVoter: value => {
                    const errorString = 'Tem de selecionar pelo menos um utilizador para aceder à votação privada.';
                    if (value && useVotingInfoStore().privateSelectedUsers.length === 0){
                        return errorString;
                    }
                    return true;
                },
            },
            help: {
                privatevoting: 'Se ativado, apenas os utilizadores selecionados poderão votar.',
                finalResultPublic: 'Se ativado, os resultados finais serão públicos.',
                intermediateResultPublic: 'Se ativado, os resultados intermédios serão públicos.',
                secretvotes: 'Se ativado, os votos serão secretos. Isto é, não será possível ver quem votou em quem/no quê.'
            },
        };
    },
    computed: {
        areAllRulesMet() {
            //* Caso seja necessário adicionar mais campos, basta adicionar o nome do campo aqui, para que seja tido em conta na verificação do disable do botao.
            let fields = ['title', 'description', 'enddate', 'privatevoting'];
            for (const field of fields) {
                if (!this.getFieldRules(field).every(rule => rule(this.useVotingInfoStore()[field])===true )) { // tem de se meter o "true" porque as regras em caso de dar false, retornam um string.
                    return false;
                }
            }
            return true;
            //* Verificação da imagem
            // let image = useVotingInfoStore().getImage;
            // if (image != null) {
            //     const isValid = this.validateAspectRatio(image, 1, 1.75).then(isValid => {
            //         if (!isValid) {
            //             return false;
            //         }
            //         else {
            //             return true;
            //         }
            //     });
            // }
        },
    },
    methods: {
        // getFieldRules: função que retorna as regras para cada campo
        getFieldRules(field) {
            // Criei esta função para o botão de submit ficar disabled caso as regras não sejam cumpridas. 
            // Com esta função, podemos definir aqui as regras para cada campo sem ter de alterar outros lados do código para alterar as condições do botão estar disabled.
            let rules = [];
            // caso venham a surgir mais fields, basta adicionar aqui as regras para esse field. E também adicionar o seu nome, nos fields da função areAllRulesMet.
            switch (field) {
                case 'title':
                rules = [this.rules.required, this.rules.maxlength50];
                break;
                case 'description':
                rules = [this.rules.required, this.rules.maxlength500];
                break;
                case 'enddate':
                rules = [];
                break;
                case 'privatevoting':
                rules = [this.rules.atLeastOnePrivateVoter];
                break;
                default:
                break;
            }
            return rules;
        },
        //* Image Methods
        onImageChange(event) {
            if (this.useVotingInfoStore().image) {
                const file = this.useVotingInfoStore().image;
                this.createImageUrl(file);
            } else {
                this.useVotingInfoStore().imageUrl = null;
            }
        },
        createImageUrl(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.useVotingInfoStore().imageUrl = e.target.result;
            };
            reader.readAsDataURL(file);
        },
        //* goNext: função que é chamada quando se clica no botão "Seguinte"
        goNext() {
            this.$emit('data');
        },
        leave() {
            this.$emit('leave');
        },
        //* onSearch: função que é chamada quando se pesquisa por utilizadores
        onSearch(val) {
            if (!val) {
                this.clearMatchedUsers();
                return;
            }
            axios.get(API_PATHS.usersByTerm(val)).then(res => {
                // Adiciona um avatar a cada utilizador, caso não o tenha.
                let own_id = parseInt(useUserInfoStore().getUserId);
                let users_matched = res.data;
                users_matched = users_matched.filter(user => user.id !== own_id);
                users_matched.forEach(user => {
                    if (!user.avatar) {
                        user.avatar = this.generateAvatar(user.name);
                    }
                    else {
                        user.avatar = API_PATHS.getImageUrl(user.avatar);
                    }
                });
                this.usersMatched = users_matched;
            }).catch(err => {
                this.usersMatched = [];
                console.log(err);
            });
        },
        //* onUpdateModelValue: função que é chamada quando se seleciona um utilizador privado que pode votar
        onUpdateModelValue(selectedItems) {
            this.$nextTick(() => {
                this.$refs.autocomplete.search = ''; // Limpa a pesquisa
            });
        },
        clearMatchedUsers() {
            this.usersMatched = [];
        },
        generateAvatar: GEN_IMAGES.generateAvatar
    },
};
</script>

<style scoped>
img {
    border-radius: 10px;
    width: 95%;
    height: 300px;
    object-fit: cover;
}
/* WIP */
.card { 
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 100%; /* Ensure this div has a height to position the inner div at the bottom */
    width: 25%;
    overflow: hidden;
}
.img_container {
    background-color: #eee;
    height: 350px; 
    width: 300px;
    align-items: center;
    border-radius: 10px;
    margin: 0 auto 30px auto;
}
.img_card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 300px; 
    width: 300px;
    overflow: hidden;
    margin: 0 auto;
    padding: 20px;
    border-radius: 10px;
    align-items: center;
}
.title {
    word-break: break-word;
    text-align: center;
    font-size: 1.35rem;
    margin-top: 1px;
}
.dark-mode .img_card{
    background-color: #121212 !important;
    color: white;
}
.dark-mode .img_container{
    background-color: #121212 !important;
    color: white;
}
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
.dark-mode .dark-light {
    background-color: #26334e !important;
    color: white;
}
</style>