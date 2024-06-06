<template>
    <div class="modal-overlay" v-if="isVisible" @click="this.$emit('close-modal');">
        <div class="modal dark-parent" @click.stop>
            <v-card class="mx-auto mt-2 dark-parent" width="500">
                <v-card-title style="padding: 15px;">
                    <h3 style="font-weight: 600; text-align: center;"><span style="color: gray; font-weight: 600;">Votação: </span> {{ useVotingInfoStore().title }}</h3>
                </v-card-title>
                <v-card-text>
                    <div v-for="(question, index) in useVotingInfoStore().questions">
                        <ul class="mb-5">
                            <QuestionCard :key="index"
                                :questionIndex="index"
                                :question="question"
                                :disableVote="true"/>
                        </ul>
                    </div>
                </v-card-text>
            </v-card>
            <div class="margin20 modal-buttons" style="margin-top: 10px">
                <v-btn class="button" type="submit" @click="this.$emit('close-modal');">Fechar</v-btn>
            </div>
        </div>
    </div>
</template>

<script>
import { useVotingInfoStore } from '@/stores/votingInfoStore';
import QuestionCard from '@/components/Voting/QuestionCard.vue';
export default {
    name: 'PreviewVoting',
    data() {
        return {
            useVotingInfoStore,
        }
    },
    components: {
        QuestionCard
    },
    props: {
        isVisible: {
            type: Boolean,
            required: true
        }
    },
    emits: ['close-modal']
}
</script>

<style scoped>
.margin10 {
    margin: 10px;
}
.margin20 {
    margin: 20px;
}
label{
    margin-right: 10px;
}
h3 {
    margin: 0;
    text-align: left;
}
hr{
    margin: 0;
}
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
}
.modal {
    background:white;
    border-radius: 5px;
    max-height: 600px;
    height: 100%;
    max-width: 600px;
    width: 100%;
    z-index: 1000;
    overflow-y: auto;
}
.modal-buttons {
    display: flex;
    justify-content: end;
}
.button {
    padding: 5px 15px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
.button:hover {
    background: #0056b3;
}

.dark-mode .dark-parent {
    background-color: #181818;
    color: white;
}
</style>