<template scoped>
    <div @click="onClick" class="card levitate pa-5" style="background-color: #eee;">
        <!-- TODO: ver melhor como definir a URL para a imagem -->
        <img alt="Voting background" :src="voting.image == null ? defaultImage : 'http://localhost:8080/images/' + voting.image"/>
        <p class="title">{{ voting.title }}</p>
    </div>
</template>
<script>
export default {
    props: {
        voting: { type: Object, required: true }
    },
    data() {
        return {
            defaultImage: this.generateImages(this.voting.title)
        }
    },
    methods: {
        onClick() {
            this.$router.push({ name: 'voting', params: { id: this.voting.id } });
        },
        generateImages(text) {
            const randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16);

            const canvas = document.createElement('canvas');
            canvas.width = 400;
            canvas.height = 200;
            const context = canvas.getContext('2d');

            // Draw the background color
            context.fillStyle = randomColor;
            context.fillRect(0, 0, canvas.width, canvas.height);
            
            // Set the font size and style
            context.font = '40px Arial';
            context.fillStyle = '#ffffff';
            context.textAlign = 'center';
            context.textBaseline = 'middle';

            // Check if the full title fits
            let displayText = text;
            const maxWidth = canvas.width - 20; // Adjust max width to fit within the canvas

            if (context.measureText(displayText).width > maxWidth) {
                // If text is too wide, use only the first letters of each word
                displayText = text.split(' ').map(word => word.charAt(0).toUpperCase()).join('');
            }

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
<style scoped>
img {
    border-radius: 10px;
    width: 100%;
    height: 140px;
    object-fit: cover;
}
.card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%; /* Ensure this div has a height to position the inner div at the bottom */
    width: 100%;
    overflow: hidden;
}
.title {
    word-break: break-word;
    text-align: center;
    font-size: 1.25rem;
    margin-top: 15px;
}
.dark-mode .card {
    background-color: #121212 !important;
    color: white;
}
.levitate {
    border-radius: 10px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.levitate:hover {
    transform: translateY(-10px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
    cursor: pointer;
}
</style>