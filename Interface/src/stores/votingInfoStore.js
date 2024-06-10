import { defineStore } from 'pinia';

export const useVotingInfoStore = defineStore('votinginfo', {
    // estado inicial da store
    state: () => ({
        title: '',
        description: '',
        enddate: null,
        image: null,
        imageUrl: null,
        privatevoting: false,
        privateSelectedUsers: [],
        isFinalResultPublic: true,
        isIntermediateResultPublic: true,
        secretvotes: false,
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
        getImageUrl() {
            return this.imageUrl;
        },
        getPrivateVoting() {
            return this.privatevoting;
        },
        getUsersMatched() {
            return this.usersMatched;
        },
        getQuestions() {
            return this.questions;
        },
        getIsFinalResultPublic() {
            return this.isFinalResultPublic;
        },
        getIsIntermediateResultPublic() {
            return this.isIntermediateResultPublic;
        },
        getSecretVotes() {
            return this.secretvotes;
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
        setImageUrl(imageUrl) {
            this.imageUrl = imageUrl;
        },
        setPrivateVoting(privatevoting) {
            this.privatevoting = privatevoting;
        },
        setPrivateSelectedUsers(privateSelectedUsers) {
            this.privateSelectedUsers = privateSelectedUsers;
        },
        setIsFinalResultPublic(isFinalResultPublic) {
            this.isFinalResultPublic = isFinalResultPublic;
        },
        setIsIntermediateResultPublic(isIntermediateResultPublic) {
            this.isIntermediateResultPublic = isIntermediateResultPublic;
        },
        setSecretVotes(secretvotes) {
            this.secretvotes = secretvotes;
        },
        setQuestions(questions) {
            this.questions = questions;
        },
        addQuestion(question) {
            this.questions.push(question);
        },
        reset() {
            this.$reset();
        },
        printContents() { //! Not sure se funciona
            console.log(this.$state);
        }
    }
});