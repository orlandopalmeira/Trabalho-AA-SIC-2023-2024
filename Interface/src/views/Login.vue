<template>
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
</template>

<script>
import axios from '../axios';
export default {
	data() {
		return {
			email: '',
			password: ''
		};
	},
	methods: {
		async login() {
    	try {
    	  const response = await axios.post(`/auth/login`, {
  			  email: this.email,
  			  password: this.password
  			}, {
  			  headers: {
  			    'Content-Type': 'application/json'
  			  }
  			});
			
  			if (response.status !== 200) {
  			  throw new Error(`Error: ${response.statusText}`);
  			}
			
  			// console.log(response.data.message);
				console.log("Token: " + response.data.token);

			
  			this.$router.push('/');
    	} catch (error) {
    	  console.error('Login failed:', error);
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