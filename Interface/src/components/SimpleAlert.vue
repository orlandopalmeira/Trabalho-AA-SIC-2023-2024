<template lang="">
    <v-row>
        <v-col cols="12">
            <v-alert
                outlined
                type="info"
                elevation="2"
                icon="mdi-information"
                variant="tonal"
            >
                {{ message }}
            </v-alert>
        </v-col>
    </v-row>
</template>
<script>
export default {
    props: {
        message: { type: String, required: true }
    }
}
</script>