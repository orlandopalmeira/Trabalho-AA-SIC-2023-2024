<template>
    <DefaultLayout>
        <v-container>
            <h1 class="mb-5">Registo de conta</h1>
            <v-form ref="form" v-model="valid" lazy-validation>
                <v-text-field
                v-model="name"
                :rules="[rules.required]"
                label="Name"
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
                
                <div class="mt-5">
                    <v-btn
                    :disabled="!valid"
                    color="success"
                    class="mr-4"
                    @click="submit">
                    Submeter
                    </v-btn>
            
                    <v-btn
                        color="error"
                        @click="reset">
                        Reset
                    </v-btn>
                </div>
            </v-form>
        </v-container>
    </DefaultLayout>
</template>

<script>
import axios from '../axios';
import DefaultLayout from '../layouts/DefaultLayout.vue'
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
        showPassword: false,
        menu: false,
        rules: {
            required: value => !!value || 'Campo obrigatório',
            min: v => v.length >= 8 || 'Pelo menos 8 caracteres',
            email: value => {
                const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                return pattern.test(value) || 'Invalid e-mail.';
            },
        },
    }),
    methods: {
        submit() {
            if (this.$refs.form.validate()) {
                let user = {
                    name: this.name,
                    email: this.email,
                    birthdate: this.birthdate,
                    password: this.password
                };
                axios.post('/users', user)
                    .then(() => {
                        //TODO: Meter lógica de ir para a página inicial, por exemplo.
                        alert('Registo efectuado com sucesso!\nEfectue o login para aceder à sua conta na página seguinte.');
                        this.$router.push('/login');
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        },
        reset() {
            this.$refs.form.reset();
        },
    },
};
</script>

