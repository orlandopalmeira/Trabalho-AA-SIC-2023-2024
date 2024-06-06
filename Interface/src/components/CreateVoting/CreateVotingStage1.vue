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
                <v-date-input
                    v-model="useVotingInfoStore().enddate"
                    label="Data do fim da votação"
                    :min="new Date().toISOString().slice(0, 10)"
                    :rules="getFieldRules('enddate')"
                    required
                ></v-date-input>
                <v-file-input
                    id="image"
                    prepend-icon="mdi-image"
                    name="image"
                    label="Imagem (opcional)"
                    v-model="useVotingInfoStore().image"
                    accept="image/*"
                ></v-file-input>
                <div style="display: flex; align-items: center;">
                    <v-checkbox
                        id="privatevoting"
                        name="privatevoting"
                        label="Votação Privada"
                        v-model="useVotingInfoStore().privatevoting"
                        class="mr-4"
                    ></v-checkbox>
                    <v-autocomplete
                        v-if="useVotingInfoStore().privatevoting"
                        id="selectPermitedUsers"
                        name="selectPermitedUsers"
                        label="Selecione os utilizadores que deseja permitir votar"
                        v-model="useVotingInfoStore().permitedUsers"
                        :items="users"
                        item-title="name"
                        item-value="id"
                        chips
                        closable-chips
                        multiple
                    >
                    <template v-slot:chip="{ props, item }">
                        <v-chip
                        v-bind="props"
                        :prepend-avatar="item.raw.avatar"
                        :text="item.raw.name"
                        ></v-chip>
                    </template>

                    <template v-slot:item="{ props, item }">
                        <v-list-item
                        v-bind="props"
                        :prepend-avatar="item.raw.avatar"
                        :subtitle="item.raw.email"
                        :title="item.raw.name"
                        ></v-list-item>
                    </template>
                    </v-autocomplete>
                </div>
                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn color="secondary" @click="leave" > Voltar </v-btn>
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
import { useVotingInfoStore } from '@/stores/votingInfoStore';

export default {
    // props: {
    //     voting: {type: Object},
    // },
    emits: ['data', 'leave'],
    data() {
        const srcs = {
                1: './kitten.png',
                2: './kitten.png',
                3: './kitten.png',
                4: './kitten.png',
                5: './kitten.png',
        }
        return {
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength100: value => (value && value.length <= 100) || 'Máximo de 100 caracteres.',
                maxlength500: value => (value && value.length <= 500) || 'Máximo de 500 caracteres.',
            },
            useVotingInfoStore,
            users: [
                { id: 1, name: 'Maria', email: 'maria@gmail.com', avatar: srcs[1] },
                { id: 2, name: 'António', email: 'antonio@gmail.com', avatar: srcs[2] },
                { id: 3, name: 'João', email: 'joao@gmail.com', avatar: srcs[3] }
            ],
            
        };
    },
    computed: {
        areAllRulesMet() {
            let fields = ['title', 'description', 'enddate'];
            for (const field of fields) {
                if (!this.getFieldRules(field).every(rule => rule(this.useVotingInfoStore()[field])===true )) { // tem de se meter o "true" porque as regras em caso de dar false, retornam um string.
                    return false;
                }
            }
            return true;
        },
    },
    methods: {
        getFieldRules(field) {
            // Criei esta função para o botão de submit ficar disabled caso as regras não sejam cumpridas. 
            // Com esta função, podemos definir aqui as regras para cada campo sem ter de alterar outros lados do código para alterar as condições do botão estar disabled.
            let rules = [];
            // caso venham a surgir mais fields, basta adicionar aqui as regras para esse field. E também adicionar o seu nome, nos fields da função areAllRulesMet.
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
        userProps (item) {
            return {
                title: item.name,
                subtitle: item.email,
            }
        }
    },
};
</script>
<style scoped>
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
</style>