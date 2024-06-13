<template>
    <v-img v-if="avatar" :src="getImageUrl(avatar)" :alt="'Avatar de ' + userName" />
    <v-img v-else :src="generateImages(userName)" :alt="'Avatar de ' + userName" />
</template>
<script>
export default {
    props: {
        avatar: {
            type: String,
            required: true,
            default: null
        },
        userName: {
            type: String,
            required: true,
        }
    },
    data() {
        return {
            defaultAvatar: '/path/to/static/image.jpg',

        }
    },
    methods: {
        generateImages(text) {
            const canvas = document.createElement('canvas');
            canvas.width = 100;
            canvas.height = 100;
            const context = canvas.getContext('2d');

            // Draw the background color
            // const fillColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
            const fillColor = "#0D4EC1";
            context.fillStyle = fillColor;
            context.fillRect(0, 0, canvas.width, canvas.height);

            // Set the font size and style
            context.font = '40px Arial';
            context.fillStyle = '#ffffff';
            context.textAlign = 'center';
            context.textBaseline = 'middle';

            // Check if the full title fits
            let displayText = text.charAt(0).toUpperCase();
            const maxWidth = canvas.width - 20; // Adjust max width to fit within the canvas

            // Calculate the starting Y position for the text to be vertically centered
            const startY = canvas.height / 2;

            // Draw the text
            context.fillText(displayText, canvas.width / 2, startY);

            const dataURL = canvas.toDataURL('image/png');
            return dataURL;
        }
    }
}
</script>