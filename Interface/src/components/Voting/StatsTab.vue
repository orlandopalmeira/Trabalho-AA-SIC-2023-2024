<template>
    <div v-if="loadingStats" class="ma-5 pa-2 dark">
        <LoadingAlert message="A obter as estatísticas, por favor aguarde..." />
    </div>
    <div v-else-if="stats.numvotes === 0" class="pa-2 dark">
        <SimpleAlert message="Esta votação ainda não tem votos." class="dark"/>
    </div>
    <div v-else class="pa-5 dark">
        <div class="background pa-2">
            <div style="width: 25%;margin: 10px 10px;" class="mb-5">
                <v-select
                    v-model="selected"
                    :items="selectItems"
                    density="compact"
                    class="select-question dark"
                    variant="outlined"
                    hide-details
                />
            </div>
            <div style="margin: 10px 10px;">
                <v-row>
                    <v-col class="vcol1 dark">
                        <p class="text-h6" style="font-weight: bold;">Número de votos</p>
                        <p style="font-size: 20pt;">{{ stats.numvotes }}</p>
                    </v-col>
                    <v-col class="vcol1 dark" cols="3.5">
                        <p class="text-h6" style="font-weight: bold;">Mais votado</p>
                        <p style="font-size: 20pt; word-break: break-word;">{{ winner.description }}</p>
                        <p>Com {{ winner.count }} votos</p>
                    </v-col>
                    <v-col class="vcol1 dark">
                        <p class="text-h6" style="font-weight: bold;">???</p>
                        <p style="font-size: 20pt;">???</p>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="vcol1 pl-5 votantes dark" cols="4">
                        <p class="text-h6" style="font-weight: bold;">Votantes</p>
                        <v-row v-for="(voter, index) in stats.voters" :key="index">
                            <v-col class="center" cols="1"><v-icon size="x-large">mdi-account-circle</v-icon></v-col>
                            <v-col>
                                <p>{{ voter.name }}</p>
                                <p style="color: #454545">{{ voter.email }}</p>
                            </v-col>
                        </v-row>
                    </v-col>
                    <v-col class="vcol1 dark" style="height: 400px" >
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
import axios from '@/axios';

import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
    components: {
        Bar,
        LoadingAlert,
        SimpleAlert
    },
    props: {
        votingId: { type: String, required: true }
    },
    data() {
        return {
            loadingStats: true,
            selected: null,
            selectItems: null,
            stats: null,
        }
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
        winner(){
            let options = this.stats.questionsstats[this.selected].options;
            let winner = options.reduce((op1, op2) => op1.count > op2.count ? op1 : op2); // lidamos com empates?
            return winner;
        }
    },
    created() {
        axios.get(`/votings/${this.votingId}/stats`)
            .then(response => {
                this.stats = response.data;
                this.selectItems = this.stats.questionsstats.map((qs,index) => ({
                    title: `Pergunta ${index+1}: "` + qs.description + '"',
                    value: index
                }));
                this.selected = 0;
                this.loadingStats = false;
            })
            .catch(error => {
                console.error(error);
                this.loadingStats = false;
            });
    },

}
</script>
<style>
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
</style>