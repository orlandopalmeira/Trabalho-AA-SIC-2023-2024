<template>
    <v-select
        label="Ordenar por"
        :items="fields"
        variant="outlined"
        density="compact"
        v-model="selectedField"
        hide-details />
</template>

<script>
export default {
    props: {
        onSortFromParent: {type: Function, required: true}, // função executada ao seleccionar um valor
        fields: {type: Object, required: true} // opções disponibilizadas no select
    },
    data() {
        return {
            selectedField: null
        };
    },
    watch: {
        selectedField() {
            this.onSortFromParent(this.selectedField);
        }
    }
}
</script>

<style scoped>
.bar {
    border: 2px solid #e0e0e0;
    border-radius: 5px;
    margin-right: 5px;
    padding: 5px;
}
</style>