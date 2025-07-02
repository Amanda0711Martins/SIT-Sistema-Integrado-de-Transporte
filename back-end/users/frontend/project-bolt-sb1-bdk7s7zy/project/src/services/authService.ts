import { api } from './api';

// Tipos
export interface LoginCredentials {
  email: string;
  password: string;
}

interface User {
  uuid: string;
  name: string;
  email: string;
  roles: string[];
}

interface AuthStatusResponse {
  isAuthenticated: boolean;
  user: User | null;
}

export const authService = {
  /**
   * Envia as credenciais. O backend responderá com um cabeçalho Set-Cookie.
   * A função não precisa mais retornar dados.
   */
  async login(credentials: LoginCredentials): Promise<void> {
    await api.post('/auth/login', credentials);
  },

  /**
   * Chama o endpoint de logout para que o backend invalide o cookie.
   */
  async logout(): Promise<void> {
    await api.post('/auth/logout');
  },

  /**
   * Verifica com o backend se o cookie enviado pelo navegador é válido.
   * Retorna os dados do usuário se a sessão estiver ativa.
   * **Lembre-se: você precisa criar o endpoint GET /api/auth/status no seu Spring Boot.**
   */
  async checkAuthStatus(): Promise<AuthStatusResponse> {
    try {
      const { data } = await api.get<AuthStatusResponse>('/auth/status');
      return data;
    } catch (error) {
      return { isAuthenticated: false, user: null };
    }
  },
};