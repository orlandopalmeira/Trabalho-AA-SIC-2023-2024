import axios from 'axios';

// Set base URL
let baseURL = import.meta.env.VITE_BACKEND_SERVER_URL
console.log("Env value of VITE_BACKEND_SERVER_URL: ", baseURL)

axios.defaults.baseURL = baseURL || 'http://localhost:8080';
console.log("axios default base url debug: ",axios.defaults.baseURL)

axios.defaults.withCredentials = true;
export default axios;
