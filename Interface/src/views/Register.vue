<template>
    <DefaultLayout>
        <v-container>
            <v-card class="mx-auto dark" width="500">
                <v-toolbar class="mb-8" dark color="primary">
					<v-toolbar-title class="pl-6">Criar conta</v-toolbar-title>
				</v-toolbar>
                <v-card-text>
                    {{ valid }}
                    <v-form @submit.prevent="submit" ref="form" v-model="valid" lazy-validation>
                        <v-text-field
                        v-model="name"
                        :rules="[rules.required]"
                        label="Nome"
                        prepend-icon="mdi-account"
                        required
                        ></v-text-field>
                        
                        <v-text-field
                        v-model="email"
                        :rules="[rules.required, rules.email]"
                        label="Email"
                        prepend-icon="mdi-email"
                        required
                        ></v-text-field>
                        
                        <v-date-input
                        v-model="birthdate"
                        label="Data de nascimento"
                        :max="new Date().toISOString().substr(0, 10)"
                        :rules="[rules.required]"
                        required
                        readonly
                        ></v-date-input>
                    
                        <v-text-field
                        v-model="password"
                        :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        :type="showPassword ? 'text' : 'password'"
                        @click:append="showPassword = !showPassword"
                        :rules="[rules.required, rules.min]"
                        label="Password"
                        hint="Insira pelo menos 8 caracteres"
                        prepend-icon="mdi-lock"
                        counter
                        required
                        ></v-text-field>
        
                        <v-text-field
                        v-model="password_confirm"
                        :append-icon="showPassword_confirm ? 'mdi-eye' : 'mdi-eye-off'"
                        :type="showPassword_confirm ? 'text' : 'password'"
                        @click:append="showPassword_confirm = !showPassword_confirm"
                        :rules="[rules.required, rules.min]"
                        :error-messages="passwordConfirmed"
                        label="Confirme a Password"
                        hint="Insira pelo menos 8 caracteres"
                        prepend-icon="mdi-lock"
                        counter
                        required
                        ></v-text-field>

                        <div style="display: flex;">
                            <v-file-input
                            id="image"
                            prepend-icon="mdi-image"
                            name="image"
                            label="Imagem (opcional)"
                            v-model="this.avatar"
                            accept="image/*"
                            @change="onAvatarChange"
                            />
                            
                            <v-avatar v-if="this.avatarURL" size="55px" class="ml-4">
                                <v-img :src="avatarURL" />
                            </v-avatar>
                        </div>
                        
                            <div class="mt-5 d-flex justify-end">
                                <v-btn color="error" class="mr-4" @click="reset"> Limpar </v-btn>
                                <v-btn :disabled="!valid" type="submit" color="success"> Criar conta </v-btn>
                            </div>
                    </v-form>
                </v-card-text>
            </v-card>
        </v-container>
    </DefaultLayout>
</template>

<script>
import { useUserInfoStore } from '@/stores/userInfoStore';
import axios from '../axios';
import DefaultLayout from '../layouts/DefaultLayout.vue'
import { API_PATHS } from '@/apiPaths';
export default {
    name: 'Register',
    components: {
        DefaultLayout
    },
    data: () => ({
        valid: false,
        name: '',
        email: '',
        birthdate: null,
        password: '',
        password_confirm: '',
        avatar: null,
        avatarURL: null,
        showPassword: false,
        showPassword_confirm: false,
        menu: false,
        rules: {
            required: value => !!value || 'Campo obrigatório',
            min: v => v.length >= 8 || 'Pelo menos 8 caracteres',
            email: value => {
                const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                return pattern.test(value) || 'E-mail inválido';
            },
        },
        useUserInfoStore
    }),
    computed: {
        passwordConfirmed() {
            return this.password === this.password_confirm ? [] : 'Passwords não coincidem';
        }
    },
    methods: {
        submit() {
            if (this.$refs.form.validate() && this.valid) {
                let formData = new FormData();
                let user = {
                    name: this.name,
                    email: this.email,
                    birthdate: this.birthdate,
                    password: this.password,
                };
                formData.append('user', JSON.stringify(user));
                formData.append('avatar', this.avatar);

                axios.post(API_PATHS.register, formData, { 
                    headers: { 
                        'Content-Type': 'multipart/form-data' 
                    }
                }).then((response) => {
                    useUserInfoStore().setUserId(response.data.id);
                    useUserInfoStore().setAvatar(response.data.avatar);
                    useUserInfoStore().setName(response.data.name);
				    this.$router.push({name: 'home'});
                    })
                    .catch((error) => {
                        let response = error.response;
                        if (response.status === 409) {
                            alert('Já existe uma conta com o email \'' + this.email + '\'.');
                        } else {
                            alert('Ocorreu um erro desconhecido ao registar a conta.');
                        }
                        console.log(error);
                    });
            }
        },
        reset() {
            this.$refs.form.reset();
        },
        onAvatarChange(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.avatarURL = e.target.result;
                    this.avatar = file;
                };
                reader.readAsDataURL(file);
            }
        }
    },
};
</script>
<style scoped>
.dark-mode .dark {
    background-color: #15202b !important;
    color: white;
}
</style>

