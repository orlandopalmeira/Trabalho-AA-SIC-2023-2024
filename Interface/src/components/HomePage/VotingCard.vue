<template scoped>
    <div @click="onClick" class="card levitate pa-5">
        <img alt="Voting background" :src="voting.image == null ? defaultImage : voting.image"/>
        <p class="title">{{ voting.title }}</p>
    </div>
</template>
<script>
export default {
    props: {
        voting: { type: Object, required: true }
    },
    data() {
        return {
            defaultImage: './kitten.png'
        }
    },
    methods: {
        onClick() {
            //TODO: Redireccionar para a página da votação
            //this.$router.push({ name: 'Voting', params: { id: this.voting.id } });
            alert('Voting ' + this.voting.id + ' clicked');
        }
    }
}
</script>
<!-- 
Formato do JSON de uma votação
{
    "id": 1,
    "title": "Voting_866828589",
    "description": "voting_866828589",
    "image": null,
    "creationdate": "2024-05-27T21:55:48.265+00:00",
    "enddate": "2024-07-13 00:34:43",
    "privatevoting": true,
    "questions": [...] // lista que questions -> cada question com várias options
}
-->
<style scoped>
img {
    border-radius: 10px;
    max-width: 100%;
    height: auto;
}
.card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%; /* Ensure this div has a height to position the inner div at the bottom */
}
.title {
    word-break: break-word;
}
.levitate {
    border-radius: 10px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.levitate:hover {
    transform: translateY(-10px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
    cursor: pointer;
}
</style>