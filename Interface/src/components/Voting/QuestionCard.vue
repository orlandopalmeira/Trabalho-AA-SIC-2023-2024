<template lang="">
    <v-card class="card dark">
        <v-card-title class="mb-5">
            <h4 style="font-weight: 600;">Pergunta {{ questionIndex + 1 }}: {{ question.description }}</h4>
        </v-card-title>
        <v-card-text class="card-text dark">
            <v-row>
                <v-col cols="12">
                    <v-radio-group v-model="selectedOption" @change="handleChange" row hide-details>
                        <div v-for="(option, index) in question.options">
                            <div style="display: flex; align-items: center;">
                                <v-radio :key="index" :label="option.description" :value="option.id"/>
                                <img v-if="option.imageUrl != null" class="img-icon" alt="Option background" 
                                    :src="option.imageUrl"/>
                                <img v-else-if="option.image != null" class="img-icon" alt="Option background" 
                                    :src="getImageUrl(option.image)"/>
                            </div>
                            <v-divider v-if="index < question.options.length - 1" style="margin: 5px"></v-divider>
                        </div>
                    </v-radio-group>
                </v-col>
            </v-row>
        </v-card-text>
    </v-card>
</template>
<script>
import { API_PATHS } from '@/apiPaths';

export default {
    props: {
        questionIndex: { type: Number, required: true },
        question: { type: Object, required: true }
    },
    data() {
        return {
            selectedOption: this.question.selectedOption
        }
    },
    methods: {
        getImageUrl: API_PATHS.getImageUrl,
        handleChange(event) {
            let optionId = event.target.value
            this.$emit('option-changed', this.questionIndex, parseInt(optionId))
        }
    }
}
</script>
<style scoped>
.img-icon {
    max-height: 50px;
    width: auto;
}
.card {
    background-color: lightgray;
}
.card-text{
    background-color: white;
    margin: 10px;
}
.dark-mode .dark {
    background-color: #15202b;
    color: white;
}
</style>