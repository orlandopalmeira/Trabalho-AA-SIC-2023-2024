import { defineStore } from 'pinia';

export const useUserInfoStore = defineStore('userinfo', {
    // estado inicial da store
    state: () => ({
        user_id: localStorage.getItem('user_id') || null,
        avatar: localStorage.getItem('avatar') || null,
    }),
    // getters, equivalentes aos computed properties
    getters: {
        getUserId() {
            return this.user_id;
        },
        getAvatar() {
            return this.avatar;
        },
        isAutenticated() {
            return this.user_id !== null;
        }
    },
    // acções, equivalentes aos métodos
    actions: {
        setUserId(id){
            this.user_id = id;
            localStorage.setItem('user_id', id);
        },
        setAvatar(avatar){
            this.avatar = avatar;
            localStorage.setItem('avatar', avatar);
        },
        logout() {
            this.user_id = null;
            this.avatar = null;
            localStorage.removeItem('user_id');
            localStorage.removeItem('avatar');
        }
    }
});