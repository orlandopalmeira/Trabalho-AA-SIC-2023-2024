<template scoped>
    <AuthenticatedLayout>
        <ModalOk 
			:isVisible="modal.opened"
			:title="modal.title"
			:message="modal.message"
			@close-modal="modal.opened=false"/>
        <v-container>
            <v-card flat>
                <v-row>
                    <v-col>
                        <div class="flex">
                            <v-text-field class="mr-5" v-model="search" label="Pesquisar" prepend-icon="mdi-magnify" density="compact" hide-details/>
                            <button class="filterbutton">
                                <v-icon>mdi-filter-outline</v-icon> Filtrar
                            </button>
                        </div>
                    </v-col>
                    <v-col>
                        <div class="flex justify-end" style="width: 100%">
                            <button class="buttoncreatevoting" @click="onClickCreateVoting">
                                <v-icon class="plus-icon">mdi-plus</v-icon> Criar votação
                            </button>
                        </div>
                    </v-col>
                </v-row>
                
                <LoadingAlert v-if="loadingVotings" message="A carregar as votações, por favor aguarde." />
                <v-data-table v-else
                :headers="headers"
                :items="processedVotings"
                :search="search">
                    <template v-slot:item.status="{ item }">
                        <p v-if="item.status==='active'" style="color: green">Activa</p>
                        <p v-else style="color: red">Terminada</p>
                    </template>

                    <template v-slot:item.privatevoting="{ item }">
                        <v-icon>{{ item.privatevoting }}</v-icon>
                    </template>
                </v-data-table>

            </v-card>
        </v-container>
    </AuthenticatedLayout>
</template>
<script>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ModalOk from '@/components/Modais/ModalOk.vue'
import LoadingAlert from '@/components/LoadingAlert.vue'
import { useUserInfoStore } from '@/stores/userInfoStore'
import axios from '@/axios'

const temp_votings = [
    {
        "title": "Voting_9724003055",
        "description": "voting_9724003055",
        "enddate": "2025-04-09 14:44:09",
        "privatevoting": false,
        "id": 1,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3447630015",
        "description": "voting_3447630015",
        "enddate": "2025-04-16 15:17:15",
        "privatevoting": false,
        "id": 2,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3227323706",
        "description": "voting_3227323706",
        "enddate": "2024-12-04 10:20:01",
        "privatevoting": true,
        "id": 3,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2643561192",
        "description": "voting_2643561192",
        "enddate": "2025-01-24 18:54:05",
        "privatevoting": false,
        "id": 4,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3260977040",
        "description": "voting_3260977040",
        "enddate": "2025-02-26 03:46:13",
        "privatevoting": true,
        "id": 5,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2942110097",
        "description": "voting_2942110097",
        "enddate": "2025-01-24 06:12:08",
        "privatevoting": true,
        "id": 6,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7811593140",
        "description": "voting_7811593140",
        "enddate": "2024-08-24 00:45:57",
        "privatevoting": false,
        "id": 7,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_5996653147",
        "description": "voting_5996653147",
        "enddate": "2024-12-27 19:21:21",
        "privatevoting": true,
        "id": 8,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7092011177",
        "description": "voting_7092011177",
        "enddate": "2024-07-09 00:34:52",
        "privatevoting": false,
        "id": 9,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_428827453",
        "description": "voting_428827453",
        "enddate": "2025-04-08 21:30:16",
        "privatevoting": true,
        "id": 10,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_6036814053",
        "description": "voting_6036814053",
        "enddate": "2024-07-26 01:29:32",
        "privatevoting": true,
        "id": 11,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2648155786",
        "description": "voting_2648155786",
        "enddate": "2024-06-05 23:33:53",
        "privatevoting": false,
        "id": 12,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3058268539",
        "description": "voting_3058268539",
        "enddate": "2024-07-27 00:35:44",
        "privatevoting": false,
        "id": 13,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2850191670",
        "description": "voting_2850191670",
        "enddate": "2024-08-22 20:26:37",
        "privatevoting": true,
        "id": 14,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_6451049675",
        "description": "voting_6451049675",
        "enddate": "2024-11-17 19:14:47",
        "privatevoting": true,
        "id": 15,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7817697540",
        "description": "voting_7817697540",
        "enddate": "2025-02-05 01:53:54",
        "privatevoting": true,
        "id": 16,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_9663080346",
        "description": "voting_9663080346",
        "enddate": "2025-03-18 03:18:54",
        "privatevoting": false,
        "id": 17,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_4706612081",
        "description": "voting_4706612081",
        "enddate": "2025-01-03 19:38:08",
        "privatevoting": false,
        "id": 18,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_8827813709",
        "description": "voting_8827813709",
        "enddate": "2024-09-06 08:51:34",
        "privatevoting": false,
        "id": 19,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_6056689056",
        "description": "voting_6056689056",
        "enddate": "2025-05-25 11:43:12",
        "privatevoting": true,
        "id": 20,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_9618082599",
        "description": "voting_9618082599",
        "enddate": "2025-05-17 07:18:59",
        "privatevoting": false,
        "id": 21,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2676568886",
        "description": "voting_2676568886",
        "enddate": "2024-08-08 20:12:39",
        "privatevoting": true,
        "id": 22,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3518641185",
        "description": "voting_3518641185",
        "enddate": "2024-10-23 05:51:12",
        "privatevoting": true,
        "id": 23,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_1852263538",
        "description": "voting_1852263538",
        "enddate": "2025-04-07 09:19:43",
        "privatevoting": false,
        "id": 24,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_1656641889",
        "description": "voting_1656641889",
        "enddate": "2024-08-02 07:22:16",
        "privatevoting": true,
        "id": 25,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_1700296813",
        "description": "voting_1700296813",
        "enddate": "2024-06-16 21:38:26",
        "privatevoting": false,
        "id": 26,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_4761353212",
        "description": "voting_4761353212",
        "enddate": "2025-04-17 22:48:48",
        "privatevoting": true,
        "id": 27,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_1338002708",
        "description": "voting_1338002708",
        "enddate": "2024-09-29 01:49:55",
        "privatevoting": false,
        "id": 28,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_3254253661",
        "description": "voting_3254253661",
        "enddate": "2024-06-11 03:34:18",
        "privatevoting": false,
        "id": 29,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7288805830",
        "description": "voting_7288805830",
        "enddate": "2024-11-07 03:52:01",
        "privatevoting": true,
        "id": 30,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_1002795022",
        "description": "voting_1002795022",
        "enddate": "2024-12-11 03:53:01",
        "privatevoting": true,
        "id": 31,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_9639428824",
        "description": "voting_9639428824",
        "enddate": "2025-03-31 06:23:24",
        "privatevoting": true,
        "id": 32,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_6519987665",
        "description": "voting_6519987665",
        "enddate": "2025-03-22 11:28:06",
        "privatevoting": false,
        "id": 33,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_6751507660",
        "description": "voting_6751507660",
        "enddate": "2024-08-17 03:11:01",
        "privatevoting": true,
        "id": 34,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7278797889",
        "description": "voting_7278797889",
        "enddate": "2024-10-20 00:49:13",
        "privatevoting": false,
        "id": 35,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_8079546673",
        "description": "voting_8079546673",
        "enddate": "2024-06-14 22:21:41",
        "privatevoting": true,
        "id": 36,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_5747979",
        "description": "voting_5747979",
        "enddate": "2025-04-22 09:08:05",
        "privatevoting": false,
        "id": 37,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_5323328408",
        "description": "voting_5323328408",
        "enddate": "2024-09-12 15:41:37",
        "privatevoting": false,
        "id": 38,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7168364336",
        "description": "voting_7168364336",
        "enddate": "2024-07-23 00:41:34",
        "privatevoting": false,
        "id": 39,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_8317678281",
        "description": "voting_8317678281",
        "enddate": "2024-09-22 11:56:24",
        "privatevoting": false,
        "id": 40,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2513553903",
        "description": "voting_2513553903",
        "enddate": "2024-10-12 12:50:19",
        "privatevoting": true,
        "id": 41,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2096409723",
        "description": "voting_2096409723",
        "enddate": "2025-03-06 16:47:35",
        "privatevoting": false,
        "id": 42,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7630302005",
        "description": "voting_7630302005",
        "enddate": "2024-11-01 02:50:13",
        "privatevoting": true,
        "id": 43,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_8731226277",
        "description": "voting_8731226277",
        "enddate": "2025-01-05 08:14:59",
        "privatevoting": true,
        "id": 44,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7984232304",
        "description": "voting_7984232304",
        "enddate": "2024-06-25 02:22:10",
        "privatevoting": true,
        "id": 45,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_8256236211",
        "description": "voting_8256236211",
        "enddate": "2024-10-13 01:07:35",
        "privatevoting": false,
        "id": 46,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_9102452473",
        "description": "voting_9102452473",
        "enddate": "2024-12-30 06:56:46",
        "privatevoting": false,
        "id": 47,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_5423679767",
        "description": "voting_5423679767",
        "enddate": "2024-08-14 20:39:01",
        "privatevoting": true,
        "id": 48,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_2924109030",
        "description": "voting_2924109030",
        "enddate": "2025-01-10 02:51:17",
        "privatevoting": true,
        "id": 49,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    },
    {
        "title": "Voting_7926239451",
        "description": "voting_7926239451",
        "enddate": "2024-06-23 02:29:54",
        "privatevoting": true,
        "id": 50,
        "image": null,
        "creationdate": "2024-05-29 21:18:39",
        "status": "active"
    }
]

const table_headers = [
    { align: 'start', key: 'title',         title: 'Votação' },
    {                 key: 'description',   title: 'Descrição' },
    {                 key: 'creationdate',  title: 'Data' },
    {                 key: 'status',        title: 'Estado'},
    {                 key: 'votes',         title: 'Votos' },
    {                 key: 'privatevoting', title: 'Visibilidade', sortable: false },
]

export default {
    components: {
        AuthenticatedLayout,
        ModalOk,
        LoadingAlert
    },
    data() {
        return {
            search: '',
            loadingVotings: true,
            votings: [],//temp_votings,
            headers: table_headers,
            modal: {
				opened: false,
				title: '',
				message: ''
			},
            useUserInfoStore
        }
    },
    methods: {
        onClickCreateVoting(){
            //TODO: redirecionar para a página de criação de votação
            alert('Not implemented yet')
        },
        openModal(title,message) {
			this.modal = {
				opened: true,
				title: title,
				message: message
			}
		},
        async getVotings(){
            this.loadingVotings = false;
            try {
                let user_id = this.useUserInfoStore().getUserId
                const response = await axios.get(`/votings/user/${user_id}`)
                return response.data
            } catch (error) {
                console.log(error);
                return []
            }
        }
    },
    computed: {
        processedVotings() {
            return this.votings.map(voting => {
                return {
                    ...voting,
                    privatevoting: voting.privatevoting ? 'mdi-lock' : 'mdi-lock-open-variant', // Ícone de cadeado fechado ou aberto
                    votes: 0 //TODO: Esta contagem tem de ser feita no servidor aplicacional
                }
            })
        }
    }, 
    created() {
        this.getVotings()
        .then(votings => {
            this.votings = votings
            this.loadingVotings = false
        }).catch(error => {
            let response = error.response
            this.openModal('Erro inesperado','Resposta do servidor "' + response.data.message + '"')
            console.error(error)
        })
    }
}
</script>
<style scoped>
.flex {
    display: flex;
    
}
.plus-icon {
    color: rgb(0, 162, 255);
    background-color: white;
    border-radius: 50%;
    margin-right: 5px;
}
.justify-end {
    justify-content:end;
}
.buttoncreatevoting {
    background-color: #4CAF50; /* Green */
    border: none;
    color: white;
    padding: 10px 10px;
    text-align: center;
    text-decoration: none;
    font-size: 16px;
    cursor: pointer;
    border-radius: 12px;
    display: flex;
    align-items: center;
}
.buttoncreatevoting:hover {
    background-color: #45a049;
}
.filterbutton {
    border: 2px solid rgb(156, 156, 156);
    border-radius: 12px;
    padding: 8px 8px;
}
</style>