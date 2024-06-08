import { createApp } from 'vue';
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import Toast from '@/components/Toast/Toast.vue';

let instance;

const vuetify = createVuetify({
    components: {
        ...components
    },
    directives,
    icons: {
        defaultSet: 'mdi', // This sets Material Design Icons as the default icon set
    }
})


const initInstance = () => {
    const app = createApp(Toast);
    app.use(vuetify)
    const mountNode = document.createElement('div');
    document.body.appendChild(mountNode);
    instance = app.mount(mountNode);
};

const ToastManager = {
    show(message, type = 'info', duration = 2000) {
        if (!instance) {
            initInstance();
        }
        instance.showToast(message, type, duration);
    }
};

export default ToastManager;