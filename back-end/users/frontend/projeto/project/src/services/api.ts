import axios from 'axios';

// Configuração base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';

// Instância do Axios
export const api = axios.create({
  baseURL: API_BASE_URL,
    withCredentials:true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (window.location.pathname !== 'login' && error.response?.status === 401) {
            console.error("Não autorizado. Redirecionando para o login");
            window.location.href = '/login'
        }
        return Promise.reject(error);
    }
)

// Tipos para as respostas da API
export interface LoginResponse {
  token: string;
  user: {
    id: string;
    name: string;
    email: string;
    role: string;
  };
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}