<template>
    <div v-if="loadingStats" class="ma-5 pa-2 dark">
        <LoadingAlert message="A obter as estatísticas, por favor aguarde..." />
    </div>
    <div v-else class="pl-5 pr-5 pb-5 dark">
        <div class="background pa-2">
            <div style="margin: 10px 10px;" class="mb-5">
                <div class="flex space-between">
                    <div style="width: 25%">
                        <v-select
                            v-model="selected"
                            :items="selectItems"
                            density="compact"
                            class="select-question dark"
                            variant="outlined"
                            hide-details
                        />
                    </div>
                    <v-btn @click="downloadCSV" color="secondary" title="Descarregar votos em CSV">
                        <v-icon>mdi-file-excel</v-icon>
                    </v-btn>
                </div>
            </div>
            <div style="margin: 10px 10px;">
                <v-row>
                    <v-col class="vcol1 dark-light">
                        <p class="text-h6 pl-2" style="font-weight: bold;">Número de votos</p>
                        <p class="pl-2" style="font-size: 17pt;">{{ stats.numvotes }}</p>
                        <p class="pl-2">{{ privateParticipation }}</p>
                    </v-col>
                    <v-col class="vcol1 dark-light" cols="3.5">
                        <p class="text-h6 pl-2" style="font-weight: bold;">Mais votado</p>
                        <p v-if="winnerOptions.length === 0" class="pl-2" style="font-size: 14pt; word-break: break-word;">Nenhum voto registado</p>
                        <p v-else-if="winnerOptions.length === 1" class="pl-2" style="font-size: 17pt; word-break: break-word;">
                            {{ winnerOptions[0].description }}
                            <v-tooltip v-if="winnerOptions[0].image">
                                <template v-slot:activator="{ props }">
                                    <span v-bind="props" text>
                                        <v-icon>mdi-image</v-icon>
                                    </span>
                                </template>
                                <v-card class="dark-light" style="margin-left: -10px; margin-right: -10px;">
                                    <v-card-title>Opção: <b>{{ winnerOptions[0].description }}</b></v-card-title>
                                    <v-card-text>
                                        <img :src="getImageUrl(winnerOptions[0].image)" :alt="'Opção: '+winnerOptions[0].description" style="max-width: 100%;">
                                    </v-card-text>
                                </v-card>
                            </v-tooltip>
                        </p>
                        <v-tooltip v-else location="top">
                            <template v-slot:activator="{ props }">
                                <p class="pl-2" style="font-size: 17pt;" v-bind="props" text>
                                    <span class="hoverable" style="text-decoration: underline;">{{ winnerOptions.length }} opções empatadas</span>
                                </p>
                            </template>
                            <v-card class="dark-light" style="margin-left: -10px; margin-right: -10px;">
                                <v-card-title>Opções empatadas</v-card-title>
                                <v-card-text v-if="winnerOptions[0].count === 1">Com 1 voto</v-card-text>
                                <v-card-text v-else>Com {{ winnerOptions[0].count }} votos</v-card-text>
                                <v-list class="dark-lighter">
                                    <v-list-item v-for="(option, index) in winnerOptions" :key="index">
                                        <v-list-item-title class="font-weight-bold">{{ option.description }}</v-list-item-title>
                                        <v-divider v-if="index !== winnerOptions.length - 1" class="mt-4"></v-divider>
                                    </v-list-item>
                                </v-list>
                            </v-card>
                        </v-tooltip>
                        <p class="pl-2">{{ winnerOptions.countText }}</p>
                    </v-col>
                    <v-col class="vcol1 dark-light">
                        <p class="text-h6 pl-2" style="font-weight: bold;">Tempo decorrido</p>
                        <p class="pl-2" style="font-size: 17pt;">{{ timeElapsed }}</p>
                        <p class="pl-2"> {{ timeLeft }} </p>
                    </v-col>
                </v-row>
                <v-row>
                    <!----- Votantes Card ----->
                    <v-col class="vcol1 pl-5 pr-5 votantes dark-light" cols="4">
                        <p class="text-h6 mb-3" style="font-weight: bold;">Votantes</p>
                        <!-- {{ JSON.stringify(voting, null, 2) }} -->
                        <!-- Caso não seja um privateVoting e não há votantes -->
                        <p v-if="votersCardList.length===0">Sem votantes</p>

                        <!-- Caso seja um private voting, usa a privateVotersSorted vê os privateVoters que votaram, em primeiro -->
                        <v-row v-else-if="votersCardList" v-for="(voter, index) in paginatedVoters" :key="voter.id">
                            
                            <v-col class="center mr-2" cols="1">
                                <Avatar :avatar="getImageUrl(voter.avatar)" :name="voter.name" :size="'40px'"/>
                            </v-col>
                            <v-col class="pa-0 mr-2 mt-2">
                                <p>{{ voter.name }}</p>
                                <p class="gray" style="font-size: 0.8em">{{ voter.email }}</p>
                            </v-col>
                            <v-col class="pa-0 center-vertically justify-end options-style ml-2 mr-2" style="max-width: 38%;">
                                <div class="options-style">
                                    <p v-if="voting.secretvotes && stats.voters.some(vot => vot.id === voter.id)" class="green"> Votou </p>
                                    <p v-else-if="!voting.secretvotes && getOptionsStringOfVoter(voter)" :title="getOptionsStringOfVoter(voter)">{{ getOptionsStringOfVoter(voter) }}</p>
                                    <p v-else class="gray"> Não votou </p>
                                </div>
                            </v-col>
                            <v-divider class="mt-1 mb-1" v-if="index < stats.voters.length - 1"/>
                        </v-row>

                        <div class="mt-4 right width" v-if="votersCardList.length > votingPaging.itemsPerPage">
                            <v-pagination
                                v-model="votingPaging.page"
                                :length="pageCount"
                                :total-visible="4"
                                @input="onPageChange"
                            />
                        </div>
                        <!-- Caso padrão em que a votação não é privada, e apenas se preocupa com mostrar os que votaram -->
                        <!-- <v-row v-else v-for="(voter, index) in stats.voters" :key="index">
                            <v-col class="center mr-2" cols="1"><v-icon size="x-large">mdi-account-circle</v-icon></v-col>
                            <v-col class="pa-0 mr-2">
                                <p>{{ voter.name }}</p>
                                <p class="gray" style="font-size: 0.8em">{{ voter.email }}</p>
                            </v-col>
                            <v-col
                                class="pa-0 center-vertically justify-end options-style ml-2 mr-2" 
                                style="max-width: 38%;"
                                :title="getOptionsStringOfVoter(voter)">
                                <div class="options-style" >
                                    <p>{{ getOptionsStringOfVoter(voter) }}</p>   
                                </div>
                            </v-col>
                            <v-divider class="mt-1 mb-1" v-if="index < stats.voters.length - 1"/>
                        </v-row> -->

                    </v-col>
                    <!----- Gráfico de barras ----->
                    <v-col class="vcol1 dark-light" style="height: 400px" >
                        <Bar id="my-chart-id" :data="chart.data" :options="chart.options" />
                    </v-col>
                </v-row>
            </div>
        </div>
    </div>
</template>
<script>
import LoadingAlert from '../LoadingAlert.vue';
import SimpleAlert from '../SimpleAlert.vue';
import Avatar from '../Avatar.vue';
import axios from '@/axios';
import { API_PATHS } from '@/apiPaths';

import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
    components: {
        Bar,
        LoadingAlert,
        SimpleAlert,
        Avatar
    },
    props: {
        // votingId: { type: Number, required: true }
        voting: { type: Object, required: true }
    },
    data() {
        return {
            votingId: this.voting.id,
            loadingStats: true,
            selected: null,
            selectItems: null,
            
            stats: null,

            votersCardList: null,
            votingPaging: {
                page: 1,
                itemsPerPage: 6
            }
        }
    },
    created() {
        //* No privatevoters não vem o criador da votação, por isso adicionamos o criador à lista de votantes privados
        this.voting.privatevoters.push(this.voting.creator);
        axios.get(API_PATHS.votingStats(this.votingId))
            .then(response => {
                this.stats = response.data;
                this.selectItems = this.stats.questionsstats.map((qs,index) => ({
                    title: `Pergunta ${index+1}: "` + qs.description + '"',
                    value: index
                }));
                this.selected = 0;
                this.loadingStats = false;

                //* Voting card logic
                if (this.voting.privatevoting){
                    console.log("privateVOters")
                    this.votersCardList = this.privateVotersSorted(this.voting.privatevoters);
                }
                else{
                    console.log("voters")
                    this.votersCardList = this.stats.voters;
                }
            })
            .catch(error => {
                console.error(error);
                this.loadingStats = false;
            });
    },
    computed: {
        chart(){
            let question = this.stats.questionsstats[this.selected];
            return {
                data: {
                    labels: question.options.map(option => option.description),
                    datasets: [{
                        label: 'Quantidade de votos',
                        backgroundColor: '#f87979', // cor das barras do gráfico
                        data: question.options.map(option => option.count)
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                }
            }
        },
        privateParticipation(){
            if (!this.voting.privatevoting){
                return "";
            }
            else{
                let numRegistered = this.voting.privatevoters.length;
                // numRegistered++; // O utilizador que criou a votação não é contabilizado
                let numvotes = this.stats.numvotes;
                let percentage = (numvotes / numRegistered) * 100;
                let percentage_str = `${Math.round(percentage)}%`;
                return "Participação de " + percentage_str;
            }
        },
        winnerOptions(){
            let options = this.stats.questionsstats[this.selected].options.slice();
            //* Esta lista obtem as opções que têm o máximo de votos que todas as opções têm (Por exemplo, se o maior número de votos for 300 para duas opções, essas duas opções estarão nesta lista). Se não houver nenhum voto a lista ficará vazia.
            let winnerOptions = options.filter(option => option.count > 0 && option.count === Math.max(...options.map(option => option.count)));
            winnerOptions.forEach(winner => {
                if (winner.count === 0) {
                    return { description: 'Sem votos', countText: "" };
                } else {
                    let numVotes = winner.count;
                    if (numVotes === 1) {
                        winner.countText = "Com 1 voto";
                    }
                    else{
                        winner.countText = "Com " + numVotes + " votos";
                    }
                    
                    return winner;
                }
            });

            return winnerOptions;
             
        },
        timeElapsed(){
            let time = null;
            if (this.voting.enddate && (new Date(this.voting.enddate) - new Date()) < 0) { // Se a votação já terminou
                time = new Date(this.voting.enddate) - new Date(this.voting.creationdate); // Retorna o tempo que a votação durou
                // console.log("Votação terminada")
            }
            else{
                time = new Date() - new Date(this.voting.creationdate); // Retorna o tempo desde a criação da votação até agora
                // console.log("Votação não terminada")
            }
            // console.log("CreationDate: ", new Date(this.voting.creationdate))
            // console.log("Endate: ", new Date(this.voting.enddate))
            // console.log("Now: ", new Date())
            // console.log("Ret time: ", time);
            return this.timestampToFormatedDate(time);
        },
        timeLeft(){
            if (!this.voting.enddate) return 'Com duração indefinida';
            let time_left = new Date(this.voting.enddate) - new Date();
            if (time_left < 0) {
                return 'Votação terminada';
            }
            return this.timestampToFormatedDate(time_left) + " restantes";
        },
        pageCount() {
            return Math.ceil(this.votersCardList.length / this.votingPaging.itemsPerPage);
        },
        paginatedVoters() {
            const start = (this.votingPaging.page - 1) * this.votingPaging.itemsPerPage;
            const end = start + this.votingPaging.itemsPerPage;
            return this.votersCardList.slice(start, end);
        }
    },
    methods: {
        timestampToFormatedDate(time){
            let days = Math.floor(time / (1000 * 60 * 60 * 24));
            let hours = Math.floor((time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            let minutes = Math.floor((time % (1000 * 60 * 60)) / (1000 * 60));
            // let seconds = Math.floor((time % (1000 * 60)) / 1000);
            let days_str = days > 0 ? days + 'd ' : '';
            let hours_str = hours > 0 ? hours + 'h ' : '';
            let minutes_str = minutes + 'min';
            if (days === 0 && hours === 0 && minutes < 1) return 'Menos de um minuto';
            if (days === 0 && hours === 0 && minutes === 1) return '1 minuto';
            if (days === 0 && hours === 0) minutes_str = minutes + ' minutos';
            return `${days_str}${hours_str}${minutes_str}`;
        },
        getImageUrl(image){
            return API_PATHS.getImageUrl(image)
        },
        getQuestionFromId(questionId){
            return this.stats.questionsstats.find(question => question.id === questionId);
        },
        getOptionFromId(optionId) {
            let options = this.stats.questionsstats[this.selected].options;
            return options.find(option => option.id === optionId);
        },
        getOptionsFromIds(optionIds) {
            return optionIds.map(optionId => this.getOptionFromId(optionId));
        },
        getOptionsFromIdsString(optionIds) {
            return this.getOptionsFromIds(optionIds).map(option => option.description).join(', ');
        },
        getOptionsStringOfVoter(voter) {
            let question_id = this.stats.questionsstats[this.selected].id;
            let voter_data = this.stats.userselectedoptions.find(userQuestionOptions => {
                let userId = userQuestionOptions.userid;
                return userId === voter.id && userQuestionOptions.questionid === question_id;
            })
            if(!voter_data) return '';
            return this.getOptionsFromIdsString(voter_data.optionsids);
        },
        convertStatsToCSV() {
            let csv = 'Id utilizador,Nome,Email,Questão,Opção\n';
            this.stats.userselectedoptions.forEach(userQuestionOptions => {
                let userId = userQuestionOptions.userid;
                let name = userQuestionOptions.username;
                let email = userQuestionOptions.email;
                let question = this.getQuestionFromId(userQuestionOptions.questionid);
                let options = question.options.filter(option => userQuestionOptions.optionsids.includes(option.id));
                options.forEach(option => {
                    csv += `${userId},${name},${email},${question.description},${option.description}\n`;
                });
            });
            return csv;
        },
        downloadCSV() {
            let csv = this.convertStatsToCSV();
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a'); // cria um <a> invisível
            if (link.download !== undefined) { // feature detection
                const url = URL.createObjectURL(blob);
                link.setAttribute('href', url);
                link.setAttribute('download', 'voting_stats.csv');
                link.style.visibility = 'hidden';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            }
        },
        //* Voters logic
        privateVotersSorted(privVoters){
            return privVoters.sort((a, b) => {
                const userHasVotedA = this.stats.voters.some(voter => voter.id === a.id);
                const userHasVotedB = this.stats.voters.some(voter => voter.id === b.id);
                if (userHasVotedA && !userHasVotedB) return -1;
                if (!userHasVotedA && userHasVotedB) return 1;
                return 0;
            }).slice();
        },
        //* Voters paging logic
        onPageChange(page) {
            this.votingPaging.page = page;
        }
    },
}
</script>
<style scoped>
.flex {
    display: flex;
}
.space-between {
    justify-content: space-between;
}
.options-style{
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.center-vertically {
    display: flex;
    align-items: center;
}
.justify-end {
    justify-content: end;
}
.background {
    background-color: #D9D9D9;
    border-radius: 10px;
}
.vcol1 {
    background-color: #ffffff;
    border-radius: 10px;
    margin: 10px 10px;
}
.select-question{
    background-color: #ffffff;
    border-radius: 5px;
}
.center{
    display: flex;
    justify-content: center;
    align-items: center;
}
.votantes {
    max-height: calc(100vh - 250px);
    overflow-y: hidden;
}
.votantes:hover {
    overflow-y: auto; /* Show the scrollbar on hover */
}
.fill-height {
    flex-grow: 1;
}
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
.dark-mode .background {
    background-color: #15202b;
}

.hoverable {
  transition: color 0.3s ease;
}

.hoverable:hover {
  color: #42a5f5; /* Replace with your desired hover color */
}

.green {
    color: #4CAF50;
}

.red {
    color: #f44336;
}

.gray {
    color: #707070;
}
</style>