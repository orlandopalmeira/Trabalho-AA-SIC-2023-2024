export const API_PATHS = {
    logout: "/auth/logout",
    login: "/auth/login",
    register: "/auth/register",
    usersByTerm: (term) => `/users?term=${term}`,
    votingId: (votingId) => `/votings/${votingId}`,
    votingStats: (votingId) => `/votings/${votingId}/stats`,
    votings: (term, orderBy, reverseSort, page, itemsPerPage) => `votings?term=${term}&orderBy=${orderBy}&order=${reverseSort}&page=${page}&votings_per_page=${itemsPerPage}`,
    postVotings: "/votings",
    historyVotings: "/votings/history",
    userVotings: "/votings/user",
    votes: "/votes",
    imageUrl: (image_name) => `/images/${image_name}`,
    getImageUrl(imageName){
        if (imageName == "" || imageName == null){
            return null;
        }
        let serverUrl = import.meta.env.VITE_BACKEND_SERVER_URL;
        let imageUrlPath = API_PATHS.imageUrl(imageName);
        return serverUrl + imageUrlPath;
    }
};
