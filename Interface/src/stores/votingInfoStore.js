import { defineStore } from 'pinia';

export const useVotingInfoStore = defineStore('votinginfo', {
    // estado inicial da store
    state: () => ({
        title: '',
        description: '',
        enddate: null,
        image: null,
        privatevoting: false,
        privateSelectedUsers: [],
        usersMatched: [],
        questions: []
    }),
    // getters, equivalentes aos computed properties
    getters: {
        getTitle() {
            return this.title;
        },
        getDescription() {
            return this.description;
        },
        getEndDate() {
            return this.enddate;
        },
        getImage() {
            return this.image;
        },
        getPrivateVoting() {
            return this.privatevoting;
        },
        getPrivateSelectedUsers() {
            return this.privateSelectedUsers;
        },
        getUsersMatched() {
            return this.usersMatched;
        },
        getQuestions() {
            return this.questions;
        }
    },
    // acções, equivalentes aos métodos
    actions: {
        setTitle(title) {
            this.title = title;
        },
        setDescription(description) {
            this.description = description;
        },
        setEndDate(enddate) {
            this.enddate = enddate;
        },
        setImage(image) {
            this.image = image;
        },
        setPrivateVoting(privatevoting) {
            this.privatevoting = privatevoting;
        },
        setUsersMatcheds(permitedUsers) {
            this.usersMatched = permitedUsers;
        },
        addUsersMatched(user) {
            this.usersMatched.push(user);
        },
        setQuestions(questions) {
            this.questions = questions;
        },
        addQuestion(question) {
            this.questions.push(question);
        },
        reset() {
            this.$reset();
        }
    }
});