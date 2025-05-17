// src/services/api.js

import axios from 'axios';

const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8081/api/v1';

const api = axios.create({
    baseURL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor para adicionar token de autenticação
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor para tratamento de erros
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const { response } = error;

        // Se o erro for 401 (Não autorizado), redireciona para o login
        if (response && response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }

        // Formato padronizado de erro para facilitar o tratamento nos componentes
        const errorData = {
            status: response?.status || 500,
            message:
                response?.data?.message ||
                response?.statusText ||
                'Erro ao comunicar com o servidor',
            details: response?.data?.details || []
        };

        return Promise.reject(errorData);
    }
);

export default api;