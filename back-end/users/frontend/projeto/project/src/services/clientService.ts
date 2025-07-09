import { api, ApiResponse } from './api';

export interface Client {
  id: string;
  name: string;
  email: string;
  phone: string;
  document: string; // CNPJ ou CPF
  address: {
    street: string;
    number: string;
    complement?: string;
    neighborhood: string;
    city: string;
    state: string;
    zipCode: string;
  };
  contactPerson: string;
  status: 'Ativo' | 'Inativo' | 'Pendente';
  createdDate: string;
  lastOrderDate?: string;
}

export interface ClientStats {
  totalClients: number;
  activeClients: number;
  newClientsThisMonth: number;
  retentionRate: number;
}

export const clientService = {
  // Clientes
  async getClients(): Promise<Client[]> {
    const response = await api.get<ApiResponse<Client[]>>('/clients');
    return response.data.data;
  },

  async getClient(id: string): Promise<Client> {
    const response = await api.get<ApiResponse<Client>>(`/clients/${id}`);
    return response.data.data;
  },

  async createClient(client: Omit<Client, 'id' | 'createdDate'>): Promise<Client> {
    const response = await api.post<ApiResponse<Client>>('/clients', client);
    return response.data.data;
  },

  async updateClient(id: string, client: Partial<Client>): Promise<Client> {
    const response = await api.put<ApiResponse<Client>>(`/clients/${id}`, client);
    return response.data.data;
  },

  async deleteClient(id: string): Promise<void> {
    await api.delete(`/clients/${id}`);
  },

  async searchClients(query: string): Promise<Client[]> {
    const response = await api.get<ApiResponse<Client[]>>(`/clients/search?q=${encodeURIComponent(query)}`);
    return response.data.data;
  },

  // Estatísticas
  async getClientStats(): Promise<ClientStats> {
    const response = await api.get<ApiResponse<ClientStats>>('/clients/stats');
    return response.data.data;
  }
};