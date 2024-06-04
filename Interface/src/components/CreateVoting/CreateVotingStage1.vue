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
                    v-model="title"
                    :rules="[rules.required]"
                ></v-text-field>
                <v-textarea
                    id="description"
                    prepend-icon="mdi-text"
                    name="description"
                    label="Descrição"
                    type="text"
                    v-model="description"
                    :rules="[rules.required]"
                ></v-textarea>
                <v-date-input
                    v-model="endDate"
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
                    v-model="image"
                    accept="image/*"
                ></v-file-input>
                <v-checkbox
                id="privatevoting"
                name="privatevoting"
                label="Votação Privada"
                v-model="privatevoting"
                ></v-checkbox>
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
                            Seguinte
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
        voting: {type: Object},
    },
    data() {
        return {
            title: this.voting.title,
            description: this.voting.description,
            endDate: this.voting.enddate,
            image: this.voting.image,
            privatevoting: this.voting.privatevoting,
            rules: {
                required: value => !!value || 'Campo obrigatório.',
            }
        };
    },
    methods: {

        validateAspectRatio(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = (event) => {
                    const img = new Image();
                    img.onload = () => {
                        const aspectRatio = img.width / img.height;
                        resolve(aspectRatio >= 1 && aspectRatio <= 2);
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

            if (this.image != null) {

                const isValid = await this.validateAspectRatio(this.image);

                if (!isValid) {

                    this.$emit('error', 'A imagem tem de ter uma proporção entre 1 e 2 (largura / altura).');

                    return;
                }
            }

            this.$emit('data', {
                title: this.title,
                description: this.description,
                enddate: this.endDate,
                image: this.image,
                privatevoting: this.privatevoting,
            });
        },
        leave() {
            this.$emit('leave');
        },
    },
};
</script>