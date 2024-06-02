<template>
	<default-layout>
		<ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
		<v-container class="mt-5" fill-height>
			<v-card class="mx-auto" width="500">
				<v-toolbar class="mb-8" dark color="primary">
					<v-toolbar-title class="pl-6">Login</v-toolbar-title>
				</v-toolbar>
				<v-card-text>
					<v-form @keydown.enter="login">
						<v-text-field class="pl-2 pr-5 pb-2"
							id="email"
							prepend-icon="mdi-account"
							name="email"
							label="Email"
							type="text"
							v-model="email"
						></v-text-field>
						<v-text-field class="pl-2 pr-5"
							id="password"
							prepend-icon="mdi-lock"
							:append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                			:type="showPassword ? 'text' : 'password'"
							@click:append="showPassword = !showPassword"
							name="password"
							label="Password"
							type="password"
							v-model="password"
						></v-text-field>
					</v-form>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn :class="buttonClass" @click="login" :disabled="email === '' || password === ''">Login</v-btn>
				</v-card-actions>
			</v-card>
		</v-container>
	</default-layout>
</template>

<script>
import { useUserInfoStore } from '@/stores/userInfoStore';
import axios from '../axios';
import DefaultLayout  from '../layouts/DefaultLayout.vue'
import ModalOk from '../components/Modais/ModalOk.vue';
export default {
	components: {
		DefaultLayout,
		ModalOk
	},
	data() {
		return {
			email: '',
			password: '',
			showPassword: false,
			modal: {
				opened: false,
				title: 'Falha no login',
				message: 'As credenciais inseridas são inválidas, por favor tente novamente.'
			},
			useUserInfoStore
		};
	},
	methods: {
		async login() {
			try {
				const response = await axios.post(`/auth/login`, {
					email: this.email,
					password: this.password
				});
				// Se a response for diferente de 200, dá throw de um erro que tem de ser tratado no catch
				console.log("Token: " + response.data.token);
				useUserInfoStore().setUserId(response.data.id);
				this.$router.push('/home');
			} catch (error) {
				let response = error.response;
				if (response.status == 401) {// Unauthorized
					this.openModal('Falha no login', 'As credenciais inseridas são inválidas, por favor tente novamente.')
				} else if(response.status != 200) {// Unexpected error
					this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
				}
				console.error('Login failed:', error);
			}
		},
		openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		}
	},
	computed: {
		buttonClass() {
			return this.email === '' || this.password === '' ? 'disabled-button' : 'active-button';
		}
	}
};
</script>

<style scoped>
.fill-height {
	height: 100vh;
}

.active-button {
  background-color: #1976d2;
  color: white;
}
.disabled-button {
  background-color: #000000;
  color: #ffffff;
}
</style>