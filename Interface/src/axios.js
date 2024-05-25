import axios from 'axios';
import * as dotenv from 'dotenv'
dotenv.config()

// Set base URL
axios.defaults.baseURL = process.env.BACKEND_SERVER_URL;

export default axios;