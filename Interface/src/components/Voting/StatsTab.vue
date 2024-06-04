<template>
    <div class="background ma-5 pa-2">
        <div style="width: 25%;margin: 10px 10px;" class="mb-5">
            <v-select
                v-model="selected"
                :items="['Pergunta 1', 'Pergunta 2', 'Pergunta 3']"
                density="compact"
                class="select-question"
                variant="outlined"
                hide-details
            />
        </div>
        <div style="margin: 10px 10px;">
            <v-row>
                <v-col class="vcol1">
                    <p class="text-h6" style="font-weight: bold;">Número de votos</p>
                    <p style="font-size: 20pt;">293847</p>
                </v-col>
                <v-col class="vcol1">
                    <p class="text-h6" style="font-weight: bold;">Mais votado</p>
                    <p style="font-size: 20pt;">AD</p>
                    <p>Com 12345 votos</p>
                </v-col>
                <v-col class="vcol1">
                    <p class="text-h6" style="font-weight: bold;">???</p>
                    <p style="font-size: 20pt;">???</p>
                </v-col>
            </v-row>
            <v-row>
                <v-col style="max-height: calc(100vh - 250px); overflow-y: auto;" class="vcol1 pl-5" cols="4">
                    <p class="text-h6" style="font-weight: bold;">Votantes</p>
                    <!--Isto é para ser um v-for-->
                    <v-row v-for="i in 10">
                        <v-col class="center" cols="1"><v-icon size="x-large">mdi-account-circle</v-icon></v-col>
                        <v-col>
                            <p>Nome do votante</p>
                            <p style="color: #454545">Email do votante</p>
                        </v-col>
                    </v-row>
                </v-col>
                <v-col class="vcol1" >
                    <Bar
                        id="my-chart-id"
                        :data="chart.data"
                        :options="chart.options"
                    />
                </v-col>
            </v-row>
        </div>
    </div>
</template>
<script>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
    components: {
        Bar
    },
    data() {
        return {
            selected: "Pergunta 1",
            chart: {
                data: {
                    labels: [ 'January', 'February', 'March', 'April', 'May' ],
                    datasets: [{
                        label: 'Quantidade de votos',
                        backgroundColor: '#000000',
                        data: [40, 20, 12, 26, 32] 
                    }]
                },
                options: {
                    responsive: true
                }
            }
        }
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
</style>