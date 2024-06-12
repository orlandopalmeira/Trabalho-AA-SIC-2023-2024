<template scoped>
    <div @click="onClick" class="card levitate pa-5" style="background-color: #eee;">
        <img 
            alt="Voting background" 
            :src="voting.image == null ? this.generateImages(this.voting.title) : getImageUrl(voting.image)"/>
        <div style="display: flex; align-items: center; justify-content:center">
            <p class="title" :title="voting.title" >{{ voting.title }}</p>
        </div>
        <div>
            <v-icon class="ml-2" v-if="voting.useralreadyvoted" title="Votou nesta votação">mdi-vote</v-icon>
            <v-icon class="ml-2" color="red" v-if="voting.enddate && hasDatePassedToday(voting.enddate)" title="Período de votação acabou">mdi-calendar-remove</v-icon>
            <v-icon class="ml-2" color="orange" v-else-if="voting.enddate && isEndingSoon(voting.enddate)" :title="'Período de votação perto de acabar.\nFalta ' + calculateTimeRemaining(voting.enddate)">mdi-clock-fast</v-icon>
        </div>
    </div>
</template>
<script>
import { API_PATHS } from '@/apiPaths';
export default {
    props: {
        voting: { type: Object, required: true }
    },
    methods: {
        onClick() {
            this.$router.push({ name: 'voting', params: { id: this.voting.id } });
        },
        hasDatePassedToday(date) {
            return new Date(date) < new Date();
        },
        isEndingSoon(date){
            let near_hours_interval = 48 // 2 days
            return new Date(date) < new Date(new Date().getTime() + 1000*60*60*near_hours_interval);
        },
        calculateTimeRemaining(date){
            let now = new Date();
            let end = new Date(date);
            let diff = end - now;
            return this.timestampToFormatedDate(diff);
        },
        timestampToFormatedDate(time){
            let days = Math.floor(time / (1000 * 60 * 60 * 24));
            let hours = Math.floor((time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            let minutes = Math.floor((time % (1000 * 60 * 60)) / (1000 * 60));
            // let seconds = Math.floor((time % (1000 * 60)) / 1000);
            let days_str = days > 0 ? days + 'd' : '';
            let hours_str = hours > 0 ? hours + 'h' : '';
            let minutes_str = minutes + 'min';
            if (days === 0 && hours === 0 && minutes < 2) return 'menos de um minuto';
            if (days === 0 && hours === 0) minutes_str = minutes + ' minutos';
            return `${days_str} ${hours_str} ${minutes_str}`;
        },
        getImageUrl: API_PATHS.getImageUrl,
        generateImages(text) {

            const canvas = document.createElement('canvas');
            canvas.width = 400;
            canvas.height = 200;
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
            let displayText = text;
            const maxWidth = canvas.width - 20; // Adjust max width to fit within the canvas

            if (context.measureText(displayText).width > maxWidth) {
                // If text is too wide, use only the first letters of each word
                displayText = text.split(' ').map(word => word.charAt(0).toUpperCase()).join('');
            }

            if (context.measureText(displayText).width > maxWidth) {
                // If text is still too wide, use only the first letter
                displayText = text.charAt(0).toUpperCase();
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
    font-size: 1.35rem;
    margin-top: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
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