// src/stores/userInfoStore.js
import { defineStore } from 'pinia';

export const useUserInfoStore = defineStore('userinfo', {
    // estado inicial da store
    state: () => ({
        user_id: null
    }),
    // getters, equivalentes aos computed properties
    getters: {
        getUserId() {
            return this.user_id;
        }
    },
    // acções, equivalentes aos métodos
    actions: {
        setUserId(id){
            this.user_id = id;
        },
        logout() {
            this.user_id = null;
        }
    }
});
