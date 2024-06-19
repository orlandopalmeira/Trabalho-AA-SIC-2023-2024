<template>
    <ModalOk 
        :isVisible="modal.opened" 
        :title="modal.title" 
        :message="modal.message"
        @close-modal="modal.onclose"/>
    <ModalYesNo
        :isVisible="modalConfirmDelete.opened"
        :title="modalConfirmDelete.title"
        :message="modalConfirmDelete.message"
        @yes="modalConfirmDelete.onyes"
        @no="modalConfirmDelete.onno"/>
    <ModalYesNo
        :isVisible="modalConfirmCloseVoting.opened"
        :title="modalConfirmCloseVoting.title"
        :message="modalConfirmCloseVoting.message"
        @yes="modalConfirmCloseVoting.onyes"
        @no="modalConfirmCloseVoting.onno"/>
    <div class="dark" style="padding-left: 10%; padding-right: 10%; display: flex; justify-content: center; align-items: center;" >
        <v-card class="dark" width="700px">
            <v-card-title class="mb-5">
                <div class="flex space-between">
                    <h4 v-if="updatedVoting.accesstype === 'creator'" style="font-weight: 600;">Detalhes da votação - Editar</h4>
                    <h4 v-else style="font-weight: 600;">Detalhes da votação</h4>
                    <div class="flex">
                        <v-btn @click="shareVoting" title="Partilhar esta votação" color="secondary" style="z-index: 11"><v-icon>mdi-share-variant</v-icon></v-btn>
                        <v-btn class="ml-2" color="error" @click="deleteVoting" title="Eliminar esta votação" v-if="updatedVoting.accesstype === 'creator'"><v-icon>mdi-delete</v-icon></v-btn>
                    </div>
                </div>
                <v-row>
                    <v-col align="right">
                        <p style="font-size: 1em;">Criador:</p>
                    </v-col>
                    <v-col align="center" cols="1">
                        <Avatar :avatar="getImageUrl(creator.avatar)" :name="creator.name" :size="'35px'"/>
                    </v-col>
                    <v-col align="left">
                        <p class="creator-name" :title="creator.name">{{ creator.name }}</p>
                        <p class="creator-email" :title="creator.email">{{ creator.email }}</p>
                    </v-col>
                </v-row>
            </v-card-title>
            <v-card-text>
                <form @submit.prevent="submitChanges">
                    <v-text-field
                        id="title"
                        prepend-icon="mdi-format-title"
                        name="title"
                        label="Título"
                        type="text"
                        v-model="updatedVoting.title"
                        :rules="getFieldRules('title')"
                        :readonly="updatedVoting.accesstype !== 'creator'"
                        required/>
                    <v-textarea
                        id="description"
                        prepend-icon="mdi-text"
                        name="description"
                        label="Descrição"
                        type="text"
                        v-model="updatedVoting.description"
                        :rules="getFieldRules('description')"
                        :readonly="updatedVoting.accesstype !== 'creator'"
                        required/>
                    <v-text-field
                        v-model="updatedVoting.creationdate"
                        prepend-icon="mdi-calendar"
                        label="Data de início da votação"
                        :disabled="updatedVoting.accesstype === 'creator'"/>
                    <div class="flex align-center">
                        <v-text-field v-if="updatedVoting.accesstype === 'creator'"
                            type="datetime-local"
                            prepend-icon="mdi-calendar"
                            v-model="updatedVoting.enddate"
                            label="Data do fim da votação"
                            :min="myISOString(new Date())"/>
                        <v-text-field v-else-if="updatedVoting.enddate"
                            v-model="updatedVoting.enddate"
                            prepend-icon="mdi-calendar"
                            label="Data do fim da votação"
                            readonly/>
                        <v-text-field v-else
                            prepend-icon="mdi-calendar"
                            label="Fim da votação indefinido"
                            readonly/>
                        <v-btn v-if="updatedVoting.accesstype === 'creator' && !isVotingTerminated" @click="closeVoting" color="error" class="ml-5 mb-5" style="display: flex; padding-top: 25px; padding-bottom: 25px" >Terminar<br>Votação</v-btn>
                    </div>
                    <v-checkbox
                        id="privatevoting"
                        name="privatevoting"
                        label="Votação Privada"
                        v-model="updatedVoting.privatevoting"
                        style="margin-bottom: -30px;"
                        :disabled="updatedVoting.accesstype === 'creator'"/>
                    <v-checkbox
                        id="finalresultpublic"
                        name="finalresultpublic"
                        v-model="updatedVoting.showstats"
                        style="margin-bottom: -30px;"
                        :readonly="updatedVoting.accesstype !== 'creator'">
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
                        style="margin-bottom: -30px;"
                        v-model="updatedVoting.showstatsrealtime" 
                        :readonly="updatedVoting.accesstype !== 'creator'">
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
                        label="Votos secretos"
                        v-model="updatedVoting.secretvotes"
                        :disabled="updatedVoting.accesstype === 'creator'"/>
                    <div v-if="updatedVoting.accesstype === 'creator'" class="flex right">
                        <v-btn color="primary" type="submit" :disabled="!hasChanges">Guardar alterações</v-btn>
                    </div>
                </form>
                <div v-if="updatedVoting.accesstype !== 'creator'" class="unclickable"></div>
            </v-card-text>
        </v-card>
    </div>
</template>
<script>
import axios from 'axios';
import { API_PATHS } from '@/apiPaths';
import ModalOk from '../Modais/ModalOk.vue';
import ModalYesNo from '../Modais/ModalYesNo.vue';
import ToastManager from '../Toast/ToastManager';
import Avatar from '../Avatar.vue';

export default {
    props: {
        voting: {
            type: Object,
            required: true
        }
    },
    components: {
        ModalOk,
        ModalYesNo,
        Avatar
    },
    data() {
        return {
            updatedVoting: JSON.parse(JSON.stringify(this.voting)), // deep copy do votação passada pelo props
            creator: null,
            initialVoting: null,
            isVotingTerminated: false,
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength100: value => (value && value.length <= 100) || 'Máximo de 100 caracteres.',
                maxlength500: value => (value && value.length <= 500) || 'Máximo de 500 caracteres.',
            },
            help: {
                finalResultPublic: 'Se ativado, os resultados finais serão públicos.',
                intermediateResultPublic: 'Se ativado, os resultados intermédios serão públicos.'
            },
            modal: {
                opened: false,
                title: '',
                message: '',
                onclose: null,
            },
            modalConfirmDelete: {
                opened: false,
                title: 'Eliminar votação',
                message: 'Tem a certeza que deseja eliminar a votação?',
                onyes: null,
                onno: null
            },
            modalConfirmCloseVoting: {
                opened: false,
                title: 'Terminar votação',
                message: 'Tem a certeza que deseja terminar a votação?',
                onyes: null,
                onno: null
            }
        }
    },
    methods: {
        myISOString(date) {
            const pad = (num) => num.toString().padStart(2, '0');

            const year = date.getFullYear();
            const month = pad(date.getMonth() + 1); // Months are zero-based
            const day = pad(date.getDate());
            const hours = pad(date.getHours());
            const minutes = pad(date.getMinutes());
            const seconds = pad(date.getSeconds());

            return `${year}-${month}-${day}T${hours}:${minutes}`;
        },
        getFieldRules(field) {
            let rules = [];
            switch (field) {
                case 'title':
                    rules = [this.rules.required, this.rules.maxlength100];
                    break;
                case 'description':
                    rules = [this.rules.required, this.rules.maxlength500];
                    break;
                default: break;
            }
            return rules;
        },
        openModal(title,message, closemodal = () => {
                this.modal.opened = false

                // redireciona para a página de detalhes da votação
                const routePath = this.$router.resolve({
                name: 'voting',
                params: { id: this.updatedVoting.id },
                query: { firstTab: 'detalhes' }
                }).href;
                window.location.href = window.location.origin + routePath;
            }) {
            this.modal.title = title;
            this.modal.message = message;
            this.modal.opened = true;
            this.modal.onclose = closemodal;
        },
        openModalConfirmDelete(yes = () => this.modalConfirmDelete.opened = false, 
                               no = () => this.modalConfirmDelete.opened = false) {
            this.modalConfirmDelete.onyes = yes;
            this.modalConfirmDelete.onno = no;
            this.modalConfirmDelete.opened = true;
        },
        openModalConfirmCloseVoting(yes = () => this.modalConfirmCloseVoting.opened = false, 
                                    no = () => this.modalConfirmCloseVoting.opened = false) {
            this.modalConfirmCloseVoting.onyes = yes;
            this.modalConfirmCloseVoting.onno = no;
            this.modalConfirmCloseVoting.opened = true;
        },
        submitChanges() {
            let dataObj = {
                title: this.updatedVoting.title,
                description: this.updatedVoting.description,
                enddate: this.updatedVoting.enddate ? this.updatedVoting.enddate.replace('T',' ').slice(0, 16) + ':00' : null, // formato da data no backend
                showstats: this.updatedVoting.showstats,
                showstatsrealtime: this.updatedVoting.showstatsrealtime
            };
            axios.put(API_PATHS.votingId(this.updatedVoting.id), dataObj)
                .then(() => {
                    this.openModal('Sucesso', 'Alterações guardadas com sucesso.');
                })
                .catch(error => {
                    this.openModal('Erro', 'Ocorreu um erro ao guardar as alterações.');
                    console.log(error);
                });
        },
        deleteVoting() {
            this.openModalConfirmDelete(() => {// se o utilizador confirmar a eliminação
                this.modalConfirmDelete.opened = false; // fecha o modal de confirmação
                axios.delete(API_PATHS.votingId(this.updatedVoting.id)) // eliminação da votação
                .then(() => {
                    this.openModal('Sucesso', 'Votação eliminada com sucesso.', () => {
                        this.modal.opened = false;
                            this.$router.back();
                    });
                })
                .catch(error => {
                    this.openModal('Erro', 'Ocorreu um erro ao eliminar a votação.');
                    console.log(error);
                });
            });
        },
        shareVoting(){
            let url = window.location.href;
            navigator.clipboard.writeText(url)
                .then(() => {
                    ToastManager.show('Link copiado para a área de transferência.', 'success');
                })
                .catch(() => {
                    ToastManager.show('Erro ao copiar o link para a área de transferência.', 'error');
                });
        },
        closeVoting(){
            this.openModalConfirmCloseVoting(() => {
                this.modalConfirmCloseVoting.opened = false;
                this.updatedVoting.enddate = this.myISOString(new Date());
                const dataObj = {
                    title: this.updatedVoting.title,
                    description: this.updatedVoting.description,
                    enddate: this.updatedVoting.enddate.replace('T',' ').slice(0, 16) + ':00',
                    showstats: this.updatedVoting.showstats,
                    showstatsrealtime: this.updatedVoting.showstatsrealtime
                };
                axios.put(API_PATHS.votingId(this.updatedVoting.id), dataObj)
                .then(() => {
                    this.openModal('Sucesso', 'Votação terminada com sucesso.');
                })
                .catch(error => {
                    this.openModal('Erro', 'Ocorreu um erro ao terminar as votação.');
                    console.log(error);
                });
            });
        },
        getImageUrl(image){
            return API_PATHS.getImageUrl(image)
        }
    },
    created() {
        this.updatedVoting.enddate = this.updatedVoting.enddate ? this.updatedVoting.enddate.slice(0, 16) : null;
        this.updatedVoting.creationdate = this.updatedVoting.creationdate.slice(0, 16);
        this.creator = this.updatedVoting.creator;
        if(this.updatedVoting.enddate){
            this.isVotingTerminated = new Date(this.updatedVoting.enddate.replace(' ', 'T')) < new Date();
        }
    },
    computed: {
        hasChanges() {
            for (let key in this.initialVoting) {
                if (this.initialVoting[key] !== this.updatedVoting[key]) {
                return true;
                }
            }
            return false;
        }
    },
    mounted() {
        this.initialVoting = { ...this.updatedVoting };
    }
}
</script>
<style scoped>
.creator-name {
    font-size: 0.75em;
    max-width: 150px; /* Define a largura máxima desejada */
    overflow: hidden; /* Esconde o texto que ultrapassa a largura máxima */
    text-overflow: ellipsis; /* Adiciona "..." ao final do texto que ultrapassa a largura máxima */
    white-space: nowrap; /* Impede que o texto quebre para a próxima linha */
}
.creator-email {
    font-size: 0.55em;
    max-width: 150px; /* Define a largura máxima desejada */
    overflow: hidden; /* Esconde o texto que ultrapassa a largura máxima */
    text-overflow: ellipsis; /* Adiciona "..." ao final do texto que ultrapassa a largura máxima */
    white-space: nowrap; /* Impede que o texto quebre para a próxima linha */
}
.unclickable {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 10;
}
.flex {
    display: flex;
}
.right {
    justify-content: end;
}
.space-between {
    justify-content: space-between;
}
.dark-mode .dark {
    background-color: #1e1e1e;
    color: white;
}
</style>