
<template>
  <v-tabs>
    <v-tab>
      <RouterLink to="/">Home</RouterLink>
    </v-tab>
    <v-tab>
      <RouterLink to="/login">Login</RouterLink>
    </v-tab>
    <v-tab>
      <RouterLink to="/register">Register</RouterLink>
    </v-tab>
    <v-spacer></v-spacer> <!-- Isto coloca a tab de Logout no fim do componente-->
    <v-tab @click="logout">
      Logout
    </v-tab>
  </v-tabs>
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
      async logout() {
        try {
    	    const response = await axios.get(`http://localhost:8080/auth/logout`);

          this.$router.push('/login');
  		  	if (response.status !== 200) {
  		  	  throw new Error(`Error: ${response.statusText}`);
  		  	}
    	  } catch (error) {
    	    console.error('Logout failed:', error);
    	  }
      }
    }
  };
</script>

<style scoped>
</style>
