<template>
    <div class="modal-overlay" v-if="isVisible">
        <div class="modal">
            <h3 style="padding: 10px; color: white; background-color: #0056b3; "><v-icon>{{ icon }}</v-icon> {{ title }}</h3>
            <hr class="dark"/>
            <div class="padding20 dark">
                <div class="dark" style="color: black;">
                    {{ message }}
                </div>
            </div>
            <div class="padding20 dark">
                <div class="modal-buttons dark" style="margin-top: 10px">
                    <button class="button" type="submit" @click="this.$emit('close-modal');">{{ buttonText }}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        icon : {
            type: String,
            required: false,
            default: 'mdi-information'
        },
        isVisible: {
            type: Boolean,
            required: true
        },
        title: {
            type: String,
            required: true
        },
        message: {
            type: String,
            required: true
        },
        buttonText: {
            type: String,
            required: false,
            default: 'Ok'
        },
        buttonTextAlt:{
            type: String,
            required: false,
            default: ''
        }
    },
    emits: [
        'close-modal',
        'close-modal-alt'
    ]
};
</script>
<style scoped>
.margin10 {
    margin: 10px;
}
.margin20 {
    margin: 20px;
}
.padding20 {
    padding: 20px;
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
    max-width: 500px;
    width: auto;
    z-index: 1000;
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
.dark-mode .dark {
    background-color: #15202b !important;
    color: white !important;
}
hr {
    border: none; /* Remove the default border */
    border-top: 2px solid gray; /* Add a new top border with desired color and thickness */
}
</style>
