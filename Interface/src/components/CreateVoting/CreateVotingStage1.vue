<template>
    <v-card flat>
        <v-card-title style="padding: 15px;">
            <v-icon large class="mr-4">mdi-plus-circle</v-icon>
            Criar Votação - Informações Gerais (1/2)
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
                    :rules="[rules.required, rules.maxlength100]"
                ></v-text-field>
                <v-textarea
                    id="description"
                    prepend-icon="mdi-text"
                    name="description"
                    label="Descrição"
                    type="text"
                    v-model="useVotingInfoStore().description"
                    :rules="[rules.required, rules.maxlength500]"
                ></v-textarea>
                <v-date-input
                    v-model="useVotingInfoStore().enddate"
                    label="Data do fim da votação"
                    :min="new Date().toISOString().slice(0, 10)"
                    :rules="[rules.required]"
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
                <v-checkbox
                id="privatevoting"
                name="privatevoting"
                label="Votação Privada"
                v-model="useVotingInfoStore().privatevoting"
                ></v-checkbox>
                <v-row class="mt-4">
                    <v-col cols="6">
                        <v-btn color="secondary" @click="leave" > Voltar </v-btn>
                    </v-col>
                    <v-col cols="6" class="text-right">
                        <v-btn color="primary" type="submit" > Seguinte </v-btn>
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
        return {
            rules: {
                required: value => !!value || 'Campo obrigatório.',
                maxlength100: value => (value && value.length <= 100) || 'Máximo de 100 caracteres.',
                maxlength500: value => (value && value.length <= 500) || 'Máximo de 500 caracteres.',
            },
            useVotingInfoStore
        };
    },
    methods: {

        goNext() {
            this.$emit('data');
        },
        leave() {
            this.$emit('leave');
        },
    },
};
</script>