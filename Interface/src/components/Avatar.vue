<template>
    <v-avatar :size=size>
        <v-img v-if="avatar" :src="avatar" :alt="'Avatar de ' + name" />
        <v-img v-else :src="generateAvatar(name)" :alt="'Avatar de ' + name" />
    </v-avatar>
</template>
<script>
import { GEN_IMAGES } from '@/genImages';
export default {
    props: {
        avatar: {
            type: String,
            required: false,
            default: null
        },
        name: {
            type: String,
            required: true,
        },
        size: {
            type: String,
            required: false,
            default: '55px'
        }
    },
    methods: {
        generateAvatar: GEN_IMAGES.generateAvatar
    }
}
</script>