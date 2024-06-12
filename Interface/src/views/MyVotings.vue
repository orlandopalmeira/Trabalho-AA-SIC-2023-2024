<template>
    <VotingsList :votingsRoute="votingsRoute" />
</template>

<script>
import { API_PATHS } from '@/apiPaths';
import VotingsList from '@/components/VotingsList.vue'

export default {
    data() {
        return {
            votingsRoute: API_PATHS.userVotings
        }
    },
    components: {
        VotingsList
    }
}

</script>