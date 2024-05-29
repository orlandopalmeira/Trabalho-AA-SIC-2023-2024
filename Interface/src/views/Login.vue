<template>
	<default-layout>
		<ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
		<v-container fluid fill-height>
			<v-card
			class="mx-auto"
			width="400"
			>
				<v-toolbar dark color="primary">
					<v-toolbar-title>Login</v-toolbar-title>
				</v-toolbar>
				<v-card-text>
					<v-form>
						<v-text-field
						id="email"
						prepend-icon="mdi-account"
						name="email"
						label="Email"
						type="text"
						v-model="email"
						></v-text-field>
						<v-text-field
						id="password"
						prepend-icon="mdi-lock"
						name="password"
						label="Password"
						type="password"
						v-model="password"
						></v-text-field>
					</v-form>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn @click="login" :disabled="email === '' || password === ''">Login</v-btn>
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
	}
};
</script>

<style scoped>
.fill-height {
	height: 100vh;
}
</style>