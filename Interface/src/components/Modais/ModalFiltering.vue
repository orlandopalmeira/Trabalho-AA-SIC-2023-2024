<template>
    <div class="modal-overlay" v-if="isVisible">
        <div class="modal">
            <h3><v-icon>mdi-filter</v-icon> Filtrar</h3>
            <div class="padding20 dark">
                <v-date-input
                v-model="filters.creationdate"
                label="Definir intervalo de data de criação"
                width="400"
                multiple="range"
                />
                <v-date-input
                v-model="filters.enddate"
                label="Definir intervalo de data de fim da votação"
                width="400"
                multiple="range"
                />
                <v-radio-group v-model="filters.in_progress" inline label="Estado da votação">
                    <v-radio @click="toggleSelect('in_progress')" label="Em progresso" :value="true"></v-radio>
                    <v-radio @click="toggleSelect('finished')" label="Terminada" :value="false"></v-radio>
                </v-radio-group>
                <v-radio-group v-model="filters.privatevoting" inline label="Tipo de votação">
                    <v-radio @click="toggleSelect('public')" label="Pública" :value="false"></v-radio>
                    <v-radio @click="toggleSelect('private')" label="Privada" :value="true"></v-radio>
                </v-radio-group>
                <div class="flex space-between">
                    <v-btn variant="tonal" color="error"  @click="this.$emit('cancel')">Cancelar</v-btn>
                    <v-btn variant="tonal" color="primary" @click="this.$emit('filter', processedFilters)">Confirmar</v-btn>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    props: { 
        isVisible: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            filters: {
                creationdate: null,
                enddate: null,
                in_progress: null,
                privatevoting: null,
            }
        }
    },
    methods: {
        toggleSelect(type) {
            switch(type) {
                case 'in_progress':
                    this.filters.in_progress = this.filters.in_progress === true ? null : true
                    break
                case 'finished':
                    this.filters.in_progress = this.filters.in_progress === false ? null : false
                    break
                case 'public':
                    this.filters.privatevoting = this.filters.privatevoting === false ? null : false
                    break
                case 'private':
                    this.filters.privatevoting = this.filters.privatevoting === true ? null : true
                    break
            }
        }
    },
    computed: {
        processedFilters() {
            const dateToString = date => date ? date.toISOString().split('T')[0] : null // guarda apenas a data (sem horas)
            const firstLast = list => {
                if(list && list.length > 0 && list.every(e => e !== null)){
                    return [dateToString(list[1]), dateToString(list[list.length - 1])]
                } else { 
                    return null 
                }
            }
            return {
                creationdate: firstLast(this.filters.creationdate),
                enddate: firstLast(this.filters.enddate),
                in_progress: this.filters.in_progress,
                privatevoting: this.filters.privatevoting
            }
        }
    }
}
</script>
<style scoped>
h3 {
    margin: 0;
    text-align: left;
    padding: 10px;
    color: white;
    background-color: #0056b3; 
}
.flex {
    display: flex;
}
.space-between {
    justify-content: space-between;
}
.padding20 {
    padding: 20px;
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
    max-width: 750px;
    width: auto;
    z-index: 1000;
}
.dark-mode .modal {
    background-color: #15202b;
}
</style>