<template>
    <v-col class="vcol1 pl-5 pr-5 votantes dark-light" cols="4">
        <p class="text-h6 mb-3" style="font-weight: bold;">Votantes</p>
        <!-- Caso não seja um privateVoting e não há votantes -->
        <p v-if="!voting.privatevoting && stats.voters.length===0">Sem votantes</p>
        <!-- Caso seja um private voting, usa a privateVotersSorted vê os privateVoters que votaram, em primeiro -->
        <v-row v-else-if="voting.privatevoting" v-for="(voter, index) in privateVotersSorted" :key="voter.id">
            <v-col class="center mr-2" cols="1"><v-icon size="x-large">mdi-account-circle</v-icon></v-col>
            <v-col class="pa-0 mr-2">
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
        <!-- Caso padrão em que a votação não é privada, e apenas se preocupa com mostrar os que votaram -->
        <v-row v-else v-for="(voter, index) in stats.voters" :key="index">
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
        </v-row>
    </v-col>
</template>

<script>
export default {
    name: 'VotersList',
    props: {
        voting: {
            type: Object,
            required: true
        },
        stats: {
            type: Object,
            required: true
        },
        selected: {
            type: Number,
            required: true
        }
    },
    computed: {
        privateVotersSorted() {
            return this.voting.privatevoters.sort((a, b) => {
                const userHasVotedA = this.stats.voters.some(voter => voter.id === a.id);
                const userHasVotedB = this.stats.voters.some(voter => voter.id === b.id);
                if (userHasVotedA && !userHasVotedB) return -1;
                if (!userHasVotedA && userHasVotedB) return 1;
                return 0;
            });
        }
    },
    methods: {
        getOptionsStringOfVoter(voter) {
            let question_id = this.stats.questionsstats[this.selected].id;
            let voter_data = this.stats.userselectedoptions.find(userQuestionOptions => {
                let userId = userQuestionOptions.userid;
                return userId === voter.id && userQuestionOptions.questionid === question_id;
            });
            if (!voter_data) return '';
            return this.getOptionsFromIdsString(voter_data.optionsids);
        },
        getOptionsFromIdsString(optionIds) {
            return this.getOptionsFromIds(optionIds).map(option => option.description).join(', ');
        },
        getOptionsFromIds(optionIds) {
            return optionIds.map(optionId => this.getOptionFromId(optionId));
        },
        getOptionFromId(optionId) {
            let options = this.stats.questionsstats[this.selected].options;
            return options.find(option => option.id === optionId);
        }
    }
}
</script>
