<template>
    <nav>
        <!--Para ecrã de PC-->
        <div style="width: 100%; justify-content: space-between;" class="nav flex pa-2 bg-primary" v-if="!phonePage">
            <div class="button" @click="redirectToHome" :style="{ cursor: 'pointer', marginLeft: '20px' }">
                <h2 class="ml-5"> <img alt="Logótipo" :src="'../../favicon.ico'" :style="{ width: '1.2em', height: '1.2em', position: 'relative', top: '5px'}" /> {{ title }}</h2>
            </div>
            <ul class="flex">
                <li>
                    <v-switch class="center pr-5"
                        v-model="isDarkMode"
                        :title="`Dark Mode: ${isDarkMode ? 'On' : 'Off'}`"
                        @click="toggleDarkMode"
                        prepend-icon="mdi-theme-light-dark"
                    />
                </li>
                <li v-for="(route,title) in routes" 
                    :class="buttonClass(route)" 
                    @click="goTo(route)">{{ title }}
                </li>

                <v-menu v-if="useUserInfoStore().isAutenticated && useUserInfoStore().name != null" :title="useUserInfoStore().name">
                    <template v-slot:activator="{ props }">
                        <button class="ml-3 mr-3" v-bind="props">
                            <Avatar :avatar="useUserInfoStore().avatar" :name="useUserInfoStore().name" size="49px"/>
                        </button>
                    </template>
                    <div class="profile-box dark">
                        <div class="profile-info">
                        <h2 class="dark">{{ useUserInfoStore().name }}</h2>
                        <p class="dark">{{ useUserInfoStore().email }}</p>
                        <button class="logout-button" @click="logout">Logout</button>
                        </div>
                    </div>
                </v-menu>
            </ul>
        </div>
        <!--Para ecrã de telemóvel-->
        <div class="nav flex bg-primary" v-else>
            <div class="flex2">
                <div class="button" @click="redirectToHome" :style="{ cursor: 'pointer', marginLeft: '20px' }">
                    <h2 class="ml-5"> <img alt="Logótipo" :src="'../../favicon.ico'" :style="{ width: '1.2em', height: '1.2em', position: 'relative', top: '5px'}" /> {{ title }}</h2>
                </div>
                <button style="width: auto;" class="btn-style" @click="togglePhoneMenu">
                    <v-icon>mdi-menu</v-icon>
                </button>
            </div>
            <ul class="flex" v-if="phoneMenu">
                <li v-for="(route,title) in routes" 
                    :class="buttonClass(route)"
                    @click="goTo(route)">{{ title }}</li>
                <li v-if="logout_button" class="btn-logout-style" @click="logout">Logout</li>
            </ul>
        </div>
    </nav>
</template>

<script>
import Avatar from '@/components/Avatar.vue';

import axios from '../axios';
import { API_PATHS } from '@/apiPaths';
import { useUserInfoStore } from '@/stores/userInfoStore';
export default {
    name: 'NavBar',
    created() {
        // Storing darkmode preferences in LocalStorage
        this.isDarkMode = JSON.parse(localStorage.getItem('dark-mode') || 'false');
        
        // Resize event to determine size of the page
        window.addEventListener('resize', this.handleResize);
    },
    data() {
        return {
            phonePage: false,
            phoneMenu: false,
            useUserInfoStore, //! Talvez se possa eliminar isto
            isDarkMode: false
        }
    },
    components: {
        Avatar
    },
    props: {
        title: {type: String, required: false, default: 'VotaçãoApp'},
        logout_button: {type: Boolean, required: true},
        routes: {type: Object, required: true}
    },
    methods: {
        async logout() {
            try {
                // Alternativas para tentar remover o token, que servem como logout
                const response = await axios.get(API_PATHS.logout);
                
                this.$router.push({name: 'login'});
                this.useUserInfoStore().logout();
                if (response.status !== 200) {
                    throw new Error(`Error: ${response.statusText}`);
                }
            } catch (error) {
                console.error('Logout failed:', error);
            }
        },
        goTo(route){
            this.$router.push(route)
        },
        redirectToHome(){
            this.$router.push({name: 'home'})
        },
        handleResize() {
            this.phonePage = window.innerWidth < 768;
        },
        togglePhoneMenu(){
            this.phoneMenu = !this.phoneMenu
        },
        buttonClass(route){
            return {
                'btn-style': true,
                'btn-style-active': this.$route.path === route
            }
        },
        toggleDarkMode() {
            this.isDarkMode = !this.isDarkMode;
            localStorage.setItem('dark-mode', this.isDarkMode);
            // a Watch trata de adicionar ou remover a classe dark-mode
        },
        getImageUrl: API_PATHS.getImageUrl,
    },
    watch: {
        isDarkMode(newVal) {
            if (this.isDarkMode) {
                document.documentElement.classList.add('dark-mode');
                } else {
                    document.documentElement.classList.remove('dark-mode');
                }
        },
        $route() {
            this.phoneMenu = false;
        },
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.handleResize);
    },
};
</script>

<style scoped>
.nav {
    text-decoration: none;
}

.nav ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
}

.flex {
    display: flex;
    align-items: center;
}

.flex2 {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
}

.center {
    display: flex;
    justify-content: center;
    align-items: center;
}

.btn-style {
    padding: 10px 20px;
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 10px;
}

.btn-style-active {
    padding: 10px 20px; /* Add padding to the sides */
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 5px;
    color: white;    
    background-color: #7e82bb;
    box-shadow: 1px 6px 9px rgba(0, 0, 0, 0.2);
}

.btn-style:hover {
    background-color: #b4b4b4;
    transition: 0.5s ease;
}

.dark-mode .dark {
    background-color: #15202b !important;
    color: rgb(255, 255, 255);
}

.profile-box {
  width: 220px;
  padding: 20px;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.profile-info h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #333;
}

.profile-info p {
  margin: 0 0 20px;
  font-size: 16px;
  color: #666;
}

.logout-button {
  padding: 10px 20px;
  background-color: transparent;
  color: #ff4d4d;
  border: 2px solid #ff4d4d;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.logout-button:hover {
  background-color: #ff4d4d;
  color: #fff;
}
/* Responsive styles */
@media (max-width: 768px) {
    .nav {
        flex-direction: column;
        align-items: center;
    }
    .nav ul {
        flex-direction: column;
        width: 100%;
    }
    .btn-style {
        width: 100%;
        text-align: center;
    }
}

@media (max-width: 480px) {
    .btn-style {
        padding: 8px;
        margin: 4px 0;
    }
}
</style>
