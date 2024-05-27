import axios from 'axios';

// Set base URL
axios.defaults.baseURL = import.meta.env.VITE_BACKEND_SERVER_URL;

axios.defaults.withCredentials = true;
export default axios;