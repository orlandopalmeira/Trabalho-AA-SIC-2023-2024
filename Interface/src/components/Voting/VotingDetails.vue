<template>
    <ModalOk 
        :isVisible="modal.opened" 
        :title="modal.title" 
        :message="modal.message"
        @close-modal="modal.onclose"/>
    <ModalYesNo
        :isVisible="modalConfirm.opened"
        :title="modalConfirm.title"
        :message="modalConfirm.message"
        @yes="modalConfirm.onyes"
        @no="modalConfirm.onno"/>
    <div class="dark" style="padding-left: 10%; padding-right: 10%">
        <v-card class="dark">
            <v-card-title class="mb-5">
                <div class="flex space-between">
                    <h4 style="font-weight: 600;">Detalhes da votação</h4>
                    <v-btn color="error" @click="deleteVoting" v-if="updatedVoting.accesstype === 'creator'">Eliminar esta votação</v-btn>
                </div>
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
                        readonly/>
                    <v-date-input v-if="updatedVoting.accesstype === 'creator'"
                        v-model="updatedVoting.enddate"
                        label="Data do fim da votação"
                        :min="new Date().toISOString().slice(0, 10)"
                        :rules="getFieldRules('enddate')"
                        required readonly/> <!--readonly evita que o input seja feito pelo teclado-->
                    <v-text-field v-else
                        v-model="formattedEndDate"
                        prepend-icon="mdi-calendar"
                        label="Data do fim da votação"
                        readonly/>
                    <v-checkbox
                        id="privatevoting"
                        name="privatevoting"
                        label="Votação Privada"
                        v-model="updatedVoting.privatevoting"
                        style="margin-bottom: -30px;"
                        readonly/>
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
                        v-model="updatedVoting.showstatsrealtime" 
                        hide-details
                        :readonly="updatedVoting.accesstype !== 'creator'">
                        <template v-slot:label>
                            <div style="display: flex;">
                                Publicar resultados intermédios
                                <v-icon :title="help.intermediateResultPublic" class="ml-2">mdi-information</v-icon>
                            </div>
                        </template>
                    </v-checkbox>
                    <div v-if="updatedVoting.accesstype === 'creator'" class="flex right">
                        <v-btn color="primary" type="submit">Guardar alterações</v-btn>
                    </div>
                </form>
            </v-card-text>
        </v-card>
    </div>
</template>
<script>
import axios from 'axios';
import ModalOk from '../Modais/ModalOk.vue';
import ModalYesNo from '../Modais/ModalYesNo.vue';

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
    },
    data() {
        return {
            updatedVoting: JSON.parse(JSON.stringify(this.voting)), // deep copy do votação passada pelo props
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength100: value => (value && value.length <= 100) || 'Máximo de 100 caracteres.',
                maxlength500: value => (value && value.length <= 500) || 'Máximo de 500 caracteres.',
            },
            help: {
                finalResultPublic: 'Se ativado, os resultados finais serão públicos.',
                intermediateResultPublic: 'Se ativado, os resultados intermédios serão públicos.'
            },
            formattedEndDate: new Date(this.voting.enddate).toISOString().slice(0, 10),
            modal: {
                opened: false,
                title: '',
                message: '',
                onclose: null,
            },
            modalConfirm: {
                opened: false,
                title: 'Eliminar votação',
                message: 'Tem a certeza que deseja eliminar a votação?',
                onyes: null,
                onno: null
            }
        }
    },
    methods: {
        getFieldRules(field) {
            let rules = [];
            switch (field) {
                case 'title':
                    rules = [this.rules.required, this.rules.maxlength100];
                    break;
                case 'description':
                    rules = [this.rules.required, this.rules.maxlength500];
                    break;
                case 'enddate':
                    rules = [this.rules.required];
                    break;
                default: break;
            }
            return rules;
        },
        openModal(title,message, closemodal = () => this.modal.opened = false) {
            this.modal.title = title;
            this.modal.message = message;
            this.modal.opened = true;
            this.modal.onclose = closemodal;
        },
        openModalConfirm(yes = () => this.modalConfirm.opened = false, 
                         no = () => this.modalConfirm.opened = false) {
            this.modalConfirm.onyes = yes;
            this.modalConfirm.onno = no;
            this.modalConfirm.opened = true;
        },
        submitChanges() {
            let dataObj = {
                title: this.updatedVoting.title,
                description: this.updatedVoting.description,
                enddate: this.updatedVoting.enddate.toISOString().slice(0, 19).replace('T', ' '), // formato da data no backend
                showstats: this.updatedVoting.showstats,
                showstatsrealtime: this.updatedVoting.showstatsrealtime
            };
            
            axios.put(`/votings/${this.updatedVoting.id}`, dataObj)
                .then(() => {
                    this.openModal('Sucesso', 'Alterações guardadas com sucesso.');
                })
                .catch(error => {
                    this.openModal('Erro', 'Ocorreu um erro ao guardar as alterações.');
                    console.log(error);
                });
        },
        deleteVoting() {
            this.openModalConfirm(() => {// se o utilizador confirmar a eliminação
                this.modalConfirm.opened = false; // fecha o modal de confirmação
                axios.delete(`/votings/${this.updatedVoting.id}`) // eliminação da votação
                .then(() => {
                    this.openModal('Sucesso', 'Votação eliminada com sucesso.', () => {
                        this.modal.opened = false;
                        this.$router.push('/home');
                    });
                })
                .catch(error => {
                    this.openModal('Erro', 'Ocorreu um erro ao eliminar a votação.');
                    console.log(error);
                });
            });
        }
    },
    created() {
        this.updatedVoting.enddate = new Date(this.updatedVoting.enddate);
    }
}
</script>
<style scoped>
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