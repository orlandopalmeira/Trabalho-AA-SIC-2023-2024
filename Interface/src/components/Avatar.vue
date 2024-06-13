<template>
    <v-avatar size="55px">
        <v-img v-if="avatar" :src="getImageUrl(avatar)" :alt="'Avatar de ' + name" />
        <v-img v-else :src="localGenAvatar" :alt="'Avatar de ' + name" />
    </v-avatar>
</template>
<script>
import { API_PATHS } from '@/apiPaths';
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
        genAvatar: {
            type: String,
            required: false,
            default: null,
        }
    },
    data() {
        return {
            localGenAvatar: this.genAvatar
        };
    },
    methods : {
        getImageUrl: API_PATHS.getImageUrl,
    },
    created() {
        if (this.avatar == null && this.localGenAvatar == null) {
            this.localGenAvatar = GEN_IMAGES.generateAvatar(this.name);
        }
    }
}
</script>