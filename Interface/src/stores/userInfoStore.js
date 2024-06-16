import { defineStore } from 'pinia';

export const useUserInfoStore = defineStore('userinfo', {
    // estado inicial da store
    state: () => ({
        user_id: localStorage.getItem('user_id') || null,
        avatar: localStorage.getItem('avatar') || null,
        name: localStorage.getItem('name') || null,
        email: localStorage.getItem('email') || null,
        homeOrderBy: localStorage.getItem('homeOrderBy') || 'creationdate',
        homeReverseSort: localStorage.getItem('homeReverseSort') === 'true',
        homeItemsPerPage: localStorage.getItem('homeItemsPerPage') || 8,
        historyItemsPerPage: localStorage.getItem('historyItemsPerPage') || 10
    }),
    // getters, equivalentes aos computed properties
    getters: {
        getUserId() {
            return this.user_id;
        },
        getAvatar() {
            return this.avatar;
        },
        getName() {
            return this.name;
        },
        getEmail() {
            return this.email;
        },
        isAutenticated() {
            return this.user_id !== null;
        },
        getHomeOrderBy() {
            return this.homeOrderBy;
        },
        getHomeReverseSort() {
            return this.homeReverseSort;
        },
        getHomeItemsPerPage() {
            return this.homeItemsPerPage;
        },
        getHistoryItemsPerPage() {
            return this.historyItemsPerPage;
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
        setName(name){
            this.name = name;
            localStorage.setItem('name', name);
        },
        setEmail(email){
            this.email = email;
            localStorage.setItem('email', email);
        },
        logout() {
            this.user_id = null;
            this.avatar = null;
            this.name = null;
            this.email = null;
            localStorage.removeItem('user_id');
            localStorage.removeItem('avatar');
            localStorage.removeItem('name');
            localStorage.removeItem('email');
        },
        setHomeOrderBy(orderBy) {
            this.homeOrderBy = orderBy;
            localStorage.setItem('homeOrderBy', orderBy);
        },
        setHomeReverseSort(reverseSort) {
            this.homeReverseSort = reverseSort;
            localStorage.setItem('homeReverseSort', reverseSort);
        },
        setHomeItemsPerPage(itemsPerPage) {
            this.homeItemsPerPage = itemsPerPage;
            localStorage.setItem('homeItemsPerPage', itemsPerPage);
        },
        setHistoryItemsPerPage(itemsPerPage) {
            this.historyItemsPerPage = itemsPerPage;
            localStorage.setItem('historyItemsPerPage', itemsPerPage);
        }
    }
});