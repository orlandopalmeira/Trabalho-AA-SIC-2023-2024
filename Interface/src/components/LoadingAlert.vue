<template lang="">
    <div class="flex">
        <v-progress-circular
        color="primary"
        indeterminate
        />
        <span class="ml-5">{{ message }}</span>
    </div>
</template>
<script>
export default {
    props: {
        message: { type: String, required: false, default: ''}
    }
}
</script>
<style scoped>
.flex {
    display: flex;
    align-items: center;
}
</style>