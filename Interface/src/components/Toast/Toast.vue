<template>
    <transition name="slide">
        <div v-if="visible" :class="['toast', type]" @click="close">
            <v-icon>{{ getIcon() }}</v-icon> {{ message }}
        </div>
    </transition>
</template>

<script>
export default {
    data() {
        return {
            visible: false,
            message: '',
            type: ''
        };
    },
    methods: {
        getIcon() {
            switch (this.type) {
                case 'info':
                    return 'mdi-information';
                case 'success':
                    return 'mdi-check-circle';
                case 'error':
                    return 'mdi-close-circle';
                case 'warning':
                    return 'mdi-alert';
                default:
                    return 'mdi-information';
            }
        },
        showToast(message, type = 'info', duration = 2000) {
            this.message = message;
            this.type = type;
            this.visible = true;
            
            setTimeout(() => {
                this.visible = false;
            }, duration);
        },
        close() {
            this.visible = false;
        }
    }
};
</script>

<style>
.toast {
    position: fixed;
    top: 80px;
    right: 20px;
    padding: 10px 10px;
    color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    cursor: pointer;
    z-index: 1000;
}

.toast.info {
    background-color: #2196f3;
}

.toast.success {
    background-color: #4caf50;
}

.toast.error {
    background-color: #f44336;
}

.toast.warning {
    background-color: #ff9800;
}

.slide-enter-active, .slide-leave-active {
    transition: all 0.5s ease;
}

.slide-enter, .slide-leave-to /* .slide-leave-active in <2.1.8 */ {
    transform: translateX(100%);
    opacity: 0;
}
</style>
