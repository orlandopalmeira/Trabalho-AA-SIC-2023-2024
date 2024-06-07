<template scoped>
    <div @click="onClick" class="card levitate pa-5" style="background-color: #eee;">
        <!-- TODO: ver melhor como definir a URL para a imagem -->
        <img alt="Voting background" :src="voting.image == null ? defaultImage : 'http://localhost:8080/images/' + voting.image"/>
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
            this.$router.push({ name: 'Voting', params: { id: this.voting.id } });
        }
    }
}
</script>
<style scoped>
img {
    border-radius: 10px;
    width: 100%;
    height: 200px;
    object-fit: cover;
}
.card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%; /* Ensure this div has a height to position the inner div at the bottom */
    width: 100%;
    overflow: hidden;
}
.title {
    word-break: break-word;
    text-align: center;
    font-size: 1.25rem;
    margin-top: 15px;
}
.dark-mode .card {
    background-color: #121212 !important;
    color: white;
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