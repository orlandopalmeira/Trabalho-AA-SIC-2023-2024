<template scoped>
    <div @click="onClick" class="card levitate pa-5" style="background-color: #eee;">
        <img 
            alt="Voting background" 
            :src="voting.image == null ? this.generateVotingImage(voting.title) : getImageUrl(voting.image)"/>
        <div style="display: flex; align-items: center; justify-content:center">
            <p class="title" :title="voting.title" >{{ voting.title }}</p>
        </div>
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <p :title=voting.creator.name class="creator" >{{ voting.creator.name }}</p>
            <div style="display: flex; flex-shrink: 0;">
                <v-icon class="ml-2" v-if="voting.useralreadyvoted" title="Votou nesta votação">mdi-vote</v-icon>
                <v-icon class="ml-2" color="red" v-if="voting.enddate && hasDatePassedToday(voting.enddate)" title="Período de votação acabou">mdi-calendar-remove</v-icon>
                <v-icon class="ml-2" color="orange" v-else-if="voting.enddate && isEndingSoon(voting.enddate)" :title="'Período de votação perto de acabar.\nFalta ' + calculateTimeRemaining(voting.enddate)">mdi-clock-fast</v-icon>
            </div>
        </div>
    </div>
</template>
<script>
import { API_PATHS } from '@/apiPaths';
import { GEN_IMAGES } from '@/genImages';
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
            let days_str = days > 0 ? days + 'd ' : '';
            let hours_str = hours > 0 ? hours + 'h ' : '';
            let minutes_str = minutes + 'min';
            if (days === 0 && hours === 0 && minutes < 2) return 'menos de um minuto';
            if (days === 0 && hours === 0) minutes_str = minutes + ' minutos';
            return `${days_str}${hours_str}${minutes_str}`;
        },
        getImageUrl: API_PATHS.getImageUrl,
        generateVotingImage: GEN_IMAGES.generateVotingImage
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
    font-size: 1.1rem;
    margin-top: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.creator {
    color: #707070;
    font-size: 0.8rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex-grow: 1;
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