<template>
    <v-row v-if="votings && votings.length > 0" class="dark pa-2 rounded-lg">
        <v-col style="padding: 0;"
            v-for="(voting,index) in paginatedVotings"
            :key="index"
            cols="12"
            sm="3">

            <v-sheet style="max-width: 100%; height: 100%;" class="ma-2 pa-2 center dark">
                <VotingCard :voting="voting"/>
            </v-sheet>
        </v-col>
        <div class="mt-4 right width" v-if="votings.length > itemsPerPage">
            <v-pagination
                v-model="page"
                :length="pageCount"
                :total-visible="4"
                @input="onPageChange"
            />
        </div>
    </v-row>
    <!--No caso de não haver votações disponíveis-->
    <div v-else>
        <v-row>
            <v-col cols="12">
                <v-alert
                    outlined
                    type="info"
                    elevation="2"
                    icon="mdi-information"
                    variant="tonal"
                >
                    <p>Não existem votações disponíveis no momento.</p>
                    <p>Por favor, volte mais tarde.</p>
                </v-alert>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import VotingCard from '@/components/HomePage/VotingCard.vue'
export default {
    components: {
        VotingCard
    },
    data() {
        return {
            page: 1,
            itemsPerPage: 8
        }
    },
    props: {
        votings: { type: Array, required: true }
    },
    computed: {
        pageCount() {
            return Math.ceil(this.votings.length / this.itemsPerPage);
        },
        paginatedVotings() {
            const start = (this.page - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.votings.slice(start, end);
        }
    },
    methods: {
        onPageChange(page) {
            this.page = page;
        }
    }
}
</script>

<style scoped>
.center {
    display: flex;
    justify-content: center;
}
.width {
    width: 100%;
}
.right {
    display: flex;
    justify-content: end;
}
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
</style>