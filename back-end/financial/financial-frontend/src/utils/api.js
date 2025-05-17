// src/utils/api.js
import axios from 'axios';

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8083'
});

// Add authentication token to all requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Handle API errors
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response) {
            // Server error response (4xx, 5xx)
            if (error.response.status === 401) {
                // Unauthorized, redirect to login
                localStorage.removeItem('authToken');
                window.location.href = '/login';
            }

            // Enhance error with response data
            error.message = error.response.data.message || error.message;
        } else if (error.request) {
            // No response received
            error.message = 'No response from server. Please check your network connection.';
        }

        return Promise.reject(error);
    }
);

export default api;