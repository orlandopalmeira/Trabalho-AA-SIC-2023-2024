import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia';
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { VDateInput } from 'vuetify/labs/VDateInput'
import '@mdi/font/css/materialdesignicons.css';

const vuetify = createVuetify({
    components: {
        VDateInput, 
        ...components
    },
    directives,
    icons: {
        defaultSet: 'mdi', // This sets Material Design Icons as the default icon set
    }
})

const app = createApp(App)
const pinia = createPinia();


app.use(vuetify)
app.use(router)
app.use(pinia)

app.mount('#app')
