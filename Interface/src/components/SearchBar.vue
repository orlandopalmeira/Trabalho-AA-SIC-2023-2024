<template>
    <div class="flex">
        <input class="bar"
            type="text" 
            placeholder="Pesquisar"
            v-model="searchQuery"/>
        <button @click="onSearch" class="button">
            <v-icon style="color:white">mdi-magnify</v-icon>
        </button>
    </div>
</template>

<script>
export default {
    props: {
        onSearchFromParent: {type: Function, required: true},
    },
    data() {
        return {
            searchQuery: '',
        };
    },
    methods: {
        onSearch() {
            this.onSearchFromParent(this.searchQuery);
        }
    }
};
</script>

<style scoped>
.flex {
    display: flex;
    align-items: center;
}
.bar {
    border: 2px solid #e0e0e0;
    border-radius: 5px;
    margin-right: 5px;
    padding: 5px;
}
.button{
    background-color: #000000;
    border-radius: 5px;
    padding: 5px;
}
</style>
