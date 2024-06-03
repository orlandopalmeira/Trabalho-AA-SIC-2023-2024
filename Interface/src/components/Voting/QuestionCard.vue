<template lang="">
    <v-card style="background-color: lightgray;">
        <v-card-title style="padding: 15px; margin-bottom: 10px">
            <h4 style="font-weight: 600;">Pergunta {{ questionIndex + 1 }}: {{ question.description }}</h4>
        </v-card-title>
        <v-card-text style="background-color: white; margin: 10px;">
            <v-row>
                <v-col cols="12">
                    <v-radio-group v-model="selectedOption" @change="handleChange" row hide-details>
                        <div style="display: flex;" v-for="(option, index) in question.options">
                            <v-radio :key="index" :label="option.description" :value="option.id"/>
                            <img v-if="option.image != null" class="img-icon" alt="Option background" 
                                :src="'http://localhost:8080/images/' + option.image"/>
                        </div>
                    </v-radio-group>
                </v-col>
            </v-row>
        </v-card-text>
    </v-card>
</template>
<script>
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
        handleChange(event) {
            let optionId = event.target.value
            this.$emit('option-changed', this.questionIndex, parseInt(optionId))
        }
    }
}
</script>
<style>
.img-icon {
    max-height: 50px;
    width: auto;
}
</style>