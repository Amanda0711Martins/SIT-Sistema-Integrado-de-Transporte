import { api, ApiResponse } from './api';

export interface SystemUser {
  id: string;
  name: string;
  email: string;
  role: string;
  status: 'Ativo' | 'Inativo';
  lastLogin?: string;
  createdDate: string;
}

export interface Microservice {
  name: string;
  status: 'Online' | 'Offline' | 'Manutenção';
  uptime: string;
  version: string;
  lastUpdate: string;
  url: string;
}

export interface SystemLog {
  id: string;
  level: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS';
  message: string;
  timestamp: string;
  service: string;
  userId?: string;
}

export interface SystemStats {
  totalUsers: number;
  activeMicroservices: number;
  systemUptime: string;
  securityLogs: number;
}

export const adminService = {
  // Usuários do Sistema
  async getSystemUsers(): Promise<SystemUser[]> {
    const response = await api.get<ApiResponse<SystemUser[]>>('/admin/users');
    return response.data.data;
  },

  async createSystemUser(user: Omit<SystemUser, 'id' | 'createdDate'>): Promise<SystemUser> {
    const response = await api.post<ApiResponse<SystemUser>>('/admin/users', user);
    return response.data.data;
  },

  async updateSystemUser(id: string, user: Partial<SystemUser>): Promise<SystemUser> {
    const response = await api.put<ApiResponse<SystemUser>>(`/admin/users/${id}`, user);
    return response.data.data;
  },

  async deleteSystemUser(id: string): Promise<void> {
    await api.delete(`/admin/users/${id}`);
  },

  // Microserviços
  async getMicroservices(): Promise<Microservice[]> {
    const response = await api.get<ApiResponse<Microservice[]>>('/admin/microservices');
    return response.data.data;
  },

  async restartMicroservice(serviceName: string): Promise<void> {
    await api.post(`/admin/microservices/${serviceName}/restart`);
  },

  // Logs do Sistema
  async getSystemLogs(level?: string, service?: string, limit?: number): Promise<SystemLog[]> {
    const params = new URLSearchParams();
    if (level) params.append('level', level);
    if (service) params.append('service', service);
    if (limit) params.append('limit', limit.toString());
    
    const response = await api.get<ApiResponse<SystemLog[]>>(`/admin/logs?${params}`);
    return response.data.data;
  },

  // Estatísticas do Sistema
  async getSystemStats(): Promise<SystemStats> {
    const response = await api.get<ApiResponse<SystemStats>>('/admin/stats');
    return response.data.data;
  },

  // Configurações
  async getSystemConfig(): Promise<Record<string, any>> {
    const response = await api.get<ApiResponse<Record<string, any>>>('/admin/config');
    return response.data.data;
  },

  async updateSystemConfig(config: Record<string, any>): Promise<void> {
    await api.put('/admin/config', config);
  }
};