<template>
    <AuthenticatedLayout>

    </AuthenticatedLayout>
</template>


<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
const getVotings = async () => {
    try {
        const response = await axios.get('/votings')
        console.log(response.data)
    } catch (error) {
        console.error(error)
    }
}
</script>