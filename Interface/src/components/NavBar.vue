<template>
    <nav>
        <!--Para ecrã de PC-->
        <div style="width: 100%; justify-content: space-between;" class="nav flex pa-2 bg-primary" v-if="!phonePage">
            <h2 class="ml-5">VotaçãoApp</h2>
            <ul class="flex">
                <li v-for="(route,title) in routes" 
                    :class="buttonClass(route)" 
                    @click="goTo(route)">{{ title }}</li>
                <li v-if="logout_button" class="btn-logout-style" @click="logout">Logout</li>
            </ul>
        </div>
        <!--Para ecrã de telemóvel-->
        <div class="nav flex" v-else>
            <div class="flex2">
                <h2 class="ml-5" style="width: 100%;">VotaçãoApp</h2>
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
import { useUserInfoStore } from '@/stores/userInfoStore';
export default {
    name: 'NavBar',
    data() {
        return {
            phonePage: false,
            phoneMenu: false,
            useUserInfoStore
        }
    },
    props: {
        logout_button: {type: Boolean,required: true},
        routes: {type: Object, required: true}
    },
    methods: {
        async logout() {
            try {
                const response = await axios.get('/auth/logout');
                
                this.$router.push('/login');
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
        togglePhoneMenu(){
            this.phoneMenu = !this.phoneMenu
        },
        buttonClass(route){
            return {
                'btn-style': true,
                'btn-style-active': this.$route.path === route
            }
        }
    },
    created(){
        window.addEventListener('resize', () => {
            this.phonePage = window.innerWidth < 768
        })
    }
};
</script>

<style scoped>
.nav {
    background-color: #C3C0C0;
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

.btn-style {
    padding: 10px;
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 10px;
}

.btn-style-active {
    padding: 10px;
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 10px;
    color: white;    
    background-color: #a09f9f;
}

.btn-style:hover {
    background-color: #E0E0E0;
}

.btn-logout-style {
    padding: 10px;
    margin: 5px 10px;
    cursor: pointer;
    border-radius: 10px;
    background-color: #e12222;
    color: white;
}

.btn-logout-style:hover {
    background-color: #ff4d4d;
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
