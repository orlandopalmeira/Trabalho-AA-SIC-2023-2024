<template>
    <nav>
        <!--Para ecrã de PC-->
        <div style="width: 100%; justify-content: space-between;" class="nav flex pa-2 bg-primary" v-if="!phonePage">
            <div class="button" @click="redirectToHome" :style="{ cursor: 'pointer', marginLeft: '20px' }">
                <h2 class="ml-5"> <img :src="'../../favicon.ico'" :style="{ width: '1.2em', height: '1.2em', position: 'relative', top: '5px'}" /> {{ title }}</h2>
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
                <v-avatar v-if="useUserInfoStore().avatar" size="55px" class="mr-5">
                    <v-img :src="getImageUrl(useUserInfoStore().avatar)"/>
                </v-avatar>
                <v-avatar v-else-if="useUserInfoStore().isAutenticated" size="55px" class="mr-5">
                    <v-img :src="generateImages('User')"/>
                </v-avatar>
                <li v-if="logout_button" class="btn-logout-style" @click="logout">Logout</li>
            </ul>
        </div>
        <!--Para ecrã de telemóvel-->
        <div class="nav flex bg-primary" v-else>
            <div class="flex2">
                <!-- <h2 class="ml-5" style="width: 100%;">VotaçãoApp</h2> -->
                <div class="button" @click="redirectToHome" :style="{ cursor: 'pointer', marginLeft: '20px' }">
                    <h2 class="ml-5"> <img :src="'../../favicon.ico'" :style="{ width: '1.2em', height: '1.2em', position: 'relative', top: '5px'}" /> {{ title }}</h2>
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
import axios from '../axios';
import { API_PATHS } from '@/apiPaths';
// import Cookies from 'js-cookie'; // If using cookies
import { useUserInfoStore } from '@/stores/userInfoStore';
export default {
    name: 'NavBar',
    created() {
        // Storing darkmode preferences in LocalStorage
        this.isDarkMode = JSON.parse(localStorage.getItem('dark-mode') || 'false');
        /* this.isDarkMode = document.documentElement.classList.contains('dark-mode'); */
        
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
                // Cookies.remove('token');
                // Cookies.remove('token', { path: '/' })
                // document.cookie = "token" +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                
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
        generateImages(text) {
            const canvas = document.createElement('canvas');
            canvas.width = 100;
            canvas.height = 100;
            const context = canvas.getContext('2d');

            // Draw the background color
            // const fillColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
            const fillColor = "#0D4EC1";
            context.fillStyle = fillColor;
            context.fillRect(0, 0, canvas.width, canvas.height);

            // Set the font size and style
            context.font = '40px Arial';
            context.fillStyle = '#ffffff';
            context.textAlign = 'center';
            context.textBaseline = 'middle';

            // Check if the full title fits
            let displayText = text.charAt(0).toUpperCase();
            const maxWidth = canvas.width - 20; // Adjust max width to fit within the canvas

            // Calculate the starting Y position for the text to be vertically centered
            const startY = canvas.height / 2;

            // Draw the text
            context.fillText(displayText, canvas.width / 2, startY);

            const dataURL = canvas.toDataURL('image/png');
            return dataURL;
            }
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

.btn-logout-style {
    padding: 15px;
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 10px;
    background-color: #e12222;
    color: white;
}

.btn-logout-style:hover {
    background-color: #ff4d4d;
    transition: 0.5s ease;
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
