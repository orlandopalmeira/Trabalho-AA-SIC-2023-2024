<template>
    <div class="flex">
        <v-btn class="button-rm-image" v-if="image" icon small @click.stop="removeImage" >
            <v-icon size="x-small" style="padding: 0; margin: 0">mdi-close</v-icon>
        </v-btn>
        <v-btn icon @click="triggerFileUpload" :style="buttonStyle">
            <v-icon v-if="!image">mdi-image</v-icon>
            <input type="file" ref="fileInput" accept="image/*" @change="handleImageUpload" style="display: none;" />
        </v-btn>
    </div>
</template>

<script>
export default {
    props: {
        imageprops: { type: File, default: null },
    },
    data() {
        return {
            image: null,
            imageUrl: '',
        };
    },
    computed: {
        buttonStyle() {
            return this.imageUrl ? {
                backgroundImage: `url(${this.imageUrl})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                position: 'relative',
            } : {};
        },
    },
    methods: {
        triggerFileUpload() {
            this.$refs.fileInput.click();
        },
        handleImageUpload(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.imageUrl = e.target.result;
                    this.image = file;
                };
                reader.readAsDataURL(file);
                this.$emit('image-uploaded', file);
            }
        },
        removeImage() {
            this.image = null;
            this.imageUrl = '';
            this.$refs.fileInput.value = '';
            this.$emit('image-removed');
        },
    },
    created() {
        if(this.imageprops) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.imageUrl = e.target.result;
                this.image = this.imageprops;
            };
            reader.readAsDataURL(this.imageprops);
        }
    }
};
</script>

<style scoped>
.v-btn {
    position: relative;
}
.button-rm-image {
    padding: 0;
    margin: 0;
    width: auto;
    height: auto;
    display: flex;
    align-items: center;
    position: absolute; /* Make the button absolute within the container */
    top: 0; /* Adjust these values as needed */
    right: 0; /* Adjust these values as needed */
    z-index: 1; /* Make sure the button is on top of the image */
}
.flex {
    display: flex;
    position: relative;
}
.align-center {
    align-items: center;
}
</style>
