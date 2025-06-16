import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { authService, LoginCredentials } from '../services/authService';

interface User {
  uuid: string;
  name: string;
  email: string;
  roles: string[];
}

interface AuthContextType {
  user: User | null;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  /**
   * Função central que verifica com o backend se o cookie é válido
   * e obtém os dados do usuário.
   */
  const checkUserStatus = async () => {
    if (!loading) setLoading(true);

    try {
      const { isAuthenticated, user: userData } = await authService.checkAuthStatus();
      if (isAuthenticated && userData) {
        setUser(userData);
      } else {
        setUser(null);
      }
    } catch (error) {
      console.error('Falha ao verificar o status da sessão.', error);
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  // Verifica o status do usuário quando a aplicação carrega pela primeira vez.
  useEffect(() => {
    checkUserStatus();
  }, []);

  const login = async (credentials: LoginCredentials): Promise<void> => {
    await authService.login(credentials);

    await checkUserStatus();
  };

  const logout = async (): Promise<void> => {
    await authService.logout(); // Chama o backend para invalidar o cookie
    setUser(null); // Limpa o estado do usuário no frontend
  };

  const value = {
    user,
    login,
    logout,
    isAuthenticated: !!user, // A autenticação é simplesmente baseada na existência do usuário
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};