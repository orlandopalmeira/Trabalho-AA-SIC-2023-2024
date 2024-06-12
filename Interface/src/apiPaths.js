// src/apiPaths.js
// export const API_BASE_URL = 'https://localhost:5173/';

export const API_PATHS = {
    logout: "/auth/logout",
    login: "/auth/login",
    register: "/auth/register",
    usersByTerm: (term) => `/users?term=${term}`,
    votingId: (votingId) => `/votings/${votingId}`,
    votingStats: (votingId) => `/votings/${votingId}/stats`,
    votings: "/votings",
    votingsAlreadyVoted: "/votings?alreadyvotedonly=true",
    userVotings: "/votings/user",
    votes: "/votes",
    imageUrl: (image_name) => `/images/${image_name}`,
    getImageUrl(imageName){
        let serverUrl = import.meta.env.VITE_BACKEND_SERVER_URL;
        let imageUrlPath = API_PATHS.imageUrl(imageName);
        return serverUrl + imageUrlPath;
    }
};
