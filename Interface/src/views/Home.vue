<template>
    <AuthenticatedLayout>
        <v-container>
            <div class="flex space-between mt-5 mb-5">
                <SearchBar :onSearchFromParent="onSearch" class="mr-5" />
                <OrderBy 
                    :fields="['Nenhum','Teste1', 'Teste2', 'Teste3']" 
                    :onSortFromParent="onSort"/>
            </div>
            <VotingsContainer :votings="votings" />
        </v-container>
    </AuthenticatedLayout>
</template>

<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import SearchBar from '@/components/HomePage/SearchBar.vue'
import OrderBy from '@/components/HomePage/OrderBy.vue'
import VotingsContainer from '@/components/HomePage/VotingsContainer.vue'

import axios from '@/axios'

const temp_votings = [
    {
        "title": "Voting_607700258",
        "description": "voting_607700258",
        "enddate": "2025-04-11 01:50:50",
        "privatevoting": true,
        "id": 1,
        "image": "./default_voting_background.png"
    },
    {
        "title": "Voting_60459224304",
        "description": "voting_6045922434",
        "enddate": "2025-04-03 02:41:52",
        "privatevoting": false,
        "id": 2,
        "image": null
    },
    {
        "title": "Voting_1326890825",
        "description": "voting_1326890825",
        "enddate": "2024-06-07 14:34:42",
        "privatevoting": false,
        "id": 3,
        "image": null
    },
    {
        "title": "Voting_3282466265",
        "description": "voting_3282466265",
        "enddate": "2024-07-20 22:21:06",
        "privatevoting": true,
        "id": 4,
        "image": null
    },
    {
        "title": "Voting_9370418324",
        "description": "voting_9370418324",
        "enddate": "2024-10-05 15:38:02",
        "privatevoting": false,
        "id": 5,
        "image": null
    },
    {
        "title": "Voting_5751822265",
        "description": "voting_5751822265",
        "enddate": "2024-06-07 00:21:07",
        "privatevoting": true,
        "id": 6,
        "image": null
    },
    {
        "title": "Voting_6510329",
        "description": "voting_6510329",
        "enddate": "2025-05-02 21:39:04",
        "privatevoting": false,
        "id": 7,
        "image": null
    },
    {
        "title": "Voting_3682866128",
        "description": "voting_3682866128",
        "enddate": "2024-08-01 07:37:41",
        "privatevoting": true,
        "id": 8,
        "image": null
    },
    {
        "title": "Voting_3592748777",
        "description": "voting_3592748777",
        "enddate": "2024-11-29 13:48:34",
        "privatevoting": false,
        "id": 9,
        "image": null
    },
    {
        "title": "Voting_6726064569",
        "description": "voting_6726064569",
        "enddate": "2025-04-28 21:37:38",
        "privatevoting": false,
        "id": 10,
        "image": null
    },
    {
        "title": "Voting_1657939354",
        "description": "voting_1657939354",
        "enddate": "2024-11-02 13:10:01",
        "privatevoting": true,
        "id": 11,
        "image": null
    },
    {
        "title": "Voting_7390180204",
        "description": "voting_7390180204",
        "enddate": "2024-11-04 21:21:35",
        "privatevoting": false,
        "id": 12,
        "image": null
    },
    {
        "title": "Voting_6744663877",
        "description": "voting_6744663877",
        "enddate": "2024-10-15 22:51:55",
        "privatevoting": true,
        "id": 13,
        "image": null
    },
    {
        "title": "Voting_6916802034",
        "description": "voting_6916802034",
        "enddate": "2025-02-16 03:45:54",
        "privatevoting": false,
        "id": 14,
        "image": null
    },
    {
        "title": "Voting_287554346",
        "description": "voting_287554346",
        "enddate": "2024-06-16 08:16:48",
        "privatevoting": false,
        "id": 15,
        "image": null
    },
    {
        "title": "Voting_1513952763",
        "description": "voting_1513952763",
        "enddate": "2025-04-07 14:21:55",
        "privatevoting": false,
        "id": 16,
        "image": null
    },
    {
        "title": "Voting_1972856234",
        "description": "voting_1972856234",
        "enddate": "2024-06-30 04:38:02",
        "privatevoting": true,
        "id": 17,
        "image": null
    },
    {
        "title": "Voting_5731696547",
        "description": "voting_5731696547",
        "enddate": "2024-08-03 18:58:34",
        "privatevoting": false,
        "id": 18,
        "image": null
    },
    {
        "title": "Voting_7417332987",
        "description": "voting_7417332987",
        "enddate": "2025-04-04 21:59:15",
        "privatevoting": false,
        "id": 19,
        "image": null
    },
    {
        "title": "Voting_8127506513",
        "description": "voting_8127506513",
        "enddate": "2024-10-20 01:45:42",
        "privatevoting": false,
        "id": 20,
        "image": null
    },
    {
        "title": "Voting_2071027399",
        "description": "voting_2071027399",
        "enddate": "2025-02-27 07:00:46",
        "privatevoting": true,
        "id": 21,
        "image": null
    },
    {
        "title": "Voting_5563376064",
        "description": "voting_5563376064",
        "enddate": "2024-12-18 00:03:39",
        "privatevoting": true,
        "id": 22,
        "image": null
    },
    {
        "title": "Voting_4979680023",
        "description": "voting_4979680023",
        "enddate": "2024-06-22 18:11:41",
        "privatevoting": false,
        "id": 23,
        "image": null
    },
    {
        "title": "Voting_3943945843",
        "description": "voting_3943945843",
        "enddate": "2025-02-26 07:51:00",
        "privatevoting": true,
        "id": 24,
        "image": null
    },
    {
        "title": "Voting_6166581320",
        "description": "voting_6166581320",
        "enddate": "2024-06-11 01:44:55",
        "privatevoting": true,
        "id": 25,
        "image": null
    },
    {
        "title": "Voting_5827513331",
        "description": "voting_5827513331",
        "enddate": "2024-12-16 20:49:19",
        "privatevoting": false,
        "id": 26,
        "image": null
    },
    {
        "title": "Voting_1505350298",
        "description": "voting_1505350298",
        "enddate": "2024-08-17 08:04:11",
        "privatevoting": false,
        "id": 27,
        "image": null
    },
    {
        "title": "Voting_6554334329",
        "description": "voting_6554334329",
        "enddate": "2025-04-04 12:18:56",
        "privatevoting": true,
        "id": 28,
        "image": null
    },
    {
        "title": "Voting_1158103308",
        "description": "voting_1158103308",
        "enddate": "2024-12-29 02:47:30",
        "privatevoting": true,
        "id": 29,
        "image": null
    },
    {
        "title": "Voting_2329160384",
        "description": "voting_2329160384",
        "enddate": "2025-03-06 00:09:27",
        "privatevoting": false,
        "id": 30,
        "image": null
    },
    {
        "title": "Voting_313973394",
        "description": "voting_313973394",
        "enddate": "2025-02-21 07:31:42",
        "privatevoting": false,
        "id": 31,
        "image": null
    },
    {
        "title": "Voting_6196428984",
        "description": "voting_6196428984",
        "enddate": "2024-08-27 12:10:09",
        "privatevoting": true,
        "id": 32,
        "image": null
    },
    {
        "title": "Voting_7754300157",
        "description": "voting_7754300157",
        "enddate": "2024-07-02 10:59:20",
        "privatevoting": true,
        "id": 33,
        "image": null
    },
    {
        "title": "Voting_643257963",
        "description": "voting_643257963",
        "enddate": "2025-05-24 17:34:29",
        "privatevoting": false,
        "id": 34,
        "image": null
    },
    {
        "title": "Voting_9402951385",
        "description": "voting_9402951385",
        "enddate": "2024-07-02 04:38:51",
        "privatevoting": false,
        "id": 35,
        "image": null
    },
    {
        "title": "Voting_6839469307",
        "description": "voting_6839469307",
        "enddate": "2025-02-15 13:19:54",
        "privatevoting": true,
        "id": 36,
        "image": null
    },
    {
        "title": "Voting_4514736869",
        "description": "voting_4514736869",
        "enddate": "2024-05-29 15:18:49",
        "privatevoting": true,
        "id": 37,
        "image": null
    },
    {
        "title": "Voting_739265418",
        "description": "voting_739265418",
        "enddate": "2024-06-04 14:45:54",
        "privatevoting": true,
        "id": 38,
        "image": null
    },
    {
        "title": "Voting_3893948039",
        "description": "voting_3893948039",
        "enddate": "2025-02-08 12:38:53",
        "privatevoting": false,
        "id": 39,
        "image": null
    },
    {
        "title": "Voting_306918866",
        "description": "voting_306918866",
        "enddate": "2024-11-14 16:59:50",
        "privatevoting": true,
        "id": 40,
        "image": null
    },
    {
        "title": "Voting_8575215984",
        "description": "voting_8575215984",
        "enddate": "2024-12-08 07:03:46",
        "privatevoting": false,
        "id": 41,
        "image": null
    },
    {
        "title": "Voting_5830319138",
        "description": "voting_5830319138",
        "enddate": "2025-05-02 02:08:28",
        "privatevoting": true,
        "id": 42,
        "image": null
    },
    {
        "title": "Voting_628906966",
        "description": "voting_628906966",
        "enddate": "2025-03-16 20:44:24",
        "privatevoting": true,
        "id": 43,
        "image": null
    },
    {
        "title": "Voting_1249519921",
        "description": "voting_1249519921",
        "enddate": "2024-07-19 12:48:14",
        "privatevoting": true,
        "id": 44,
        "image": null
    },
    {
        "title": "Voting_5524474924",
        "description": "voting_5524474924",
        "enddate": "2024-11-12 05:17:21",
        "privatevoting": false,
        "id": 45,
        "image": null
    },
    {
        "title": "Voting_2785588101",
        "description": "voting_2785588101",
        "enddate": "2024-12-04 04:12:56",
        "privatevoting": true,
        "id": 46,
        "image": null
    },
    {
        "title": "Voting_2941178743",
        "description": "voting_2941178743",
        "enddate": "2024-11-03 02:45:57",
        "privatevoting": false,
        "id": 47,
        "image": null
    },
    {
        "title": "Voting_3665371074",
        "description": "voting_3665371074",
        "enddate": "2024-08-27 14:43:49",
        "privatevoting": false,
        "id": 48,
        "image": null
    },
    {
        "title": "Voting_4436093349",
        "description": "voting_4436093349",
        "enddate": "2025-01-01 13:41:25",
        "privatevoting": false,
        "id": 49,
        "image": null
    },
    {
        "title": "Voting_9138563551",
        "description": "voting_9138563551",
        "enddate": "2025-05-28 07:29:41",
        "privatevoting": false,
        "id": 50,
        "image": null
    }
]

export default {
    name: 'Home',
    components: {
        AuthenticatedLayout,
        SearchBar,
        OrderBy,
        VotingsContainer
    },
    data() {
        return {
            loadingVotings: true,
            votings: this.getVotings().then(votings => votings).catch(error => [])
            // votings: getVotings()
        }
    },
    methods: {
        onSearch(searchString) {
            alert(searchString)
        },
        onSort(field) {
            alert(field)
        },
        async getVotings() {
            try {
                let response = await axios.get('/votings') // votações a que o user tem acesso
                this.loadingVotings = false 
                return response.data
            } catch (error) {
                console.error(error)
                return []
            }
        }
    }
}
</script>

<style scoped>
.flex {
    display: flex;
}
.space-between {
    justify-content: space-between;
}
</style>