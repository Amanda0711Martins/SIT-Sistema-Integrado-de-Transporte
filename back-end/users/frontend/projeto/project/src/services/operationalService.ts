import { api, ApiResponse } from './api';

export interface Vehicle {
  id: string;
  plate: string;
  model: string;
  year: number;
  driver?: string;
  status: 'Disponível' | 'Em Rota' | 'Manutenção';
  location: string;
  fuelLevel: number;
  mileage: number;
}

export interface Route {
  id: string;
  vehicleId: string;
  driverId: string;
  origin: string;
  destination: string;
  status: 'Planejada' | 'Em Andamento' | 'Concluída';
  startDate: string;
  endDate?: string;
  distance: number;
}

export interface Pickup {
  id: string;
  clientId: string;
  clientName: string;
  origin: string;
  destination: string;
  scheduledDate: string;
  status: 'Pendente' | 'Agendada' | 'Coletada' | 'Entregue';
  weight: number;
  value: number;
}

export interface Quote {
  id: string;
  clientId: string;
  origin: string;
  destination: string;
  weight: number;
  dimensions: string;
  value: number;
  status: 'Pendente' | 'Aprovada' | 'Rejeitada';
  createdDate: string;
  validUntil: string;
}

export const operationalService = {
  // Veículos
  async getVehicles(): Promise<Vehicle[]> {
    const response = await api.get<ApiResponse<Vehicle[]>>('/operational/vehicles');
    return response.data.data;
  },

  async getVehicle(id: string): Promise<Vehicle> {
    const response = await api.get<ApiResponse<Vehicle>>(`/operational/vehicles/${id}`);
    return response.data.data;
  },

  async createVehicle(vehicle: Omit<Vehicle, 'id'>): Promise<Vehicle> {
    const response = await api.post<ApiResponse<Vehicle>>('/operational/vehicles', vehicle);
    return response.data.data;
  },

  async updateVehicle(id: string, vehicle: Partial<Vehicle>): Promise<Vehicle> {
    const response = await api.put<ApiResponse<Vehicle>>(`/operational/vehicles/${id}`, vehicle);
    return response.data.data;
  },

  // Rotas
  async getRoutes(): Promise<Route[]> {
    const response = await api.get<ApiResponse<Route[]>>('/operational/routes');
    return response.data.data;
  },

  async createRoute(route: Omit<Route, 'id'>): Promise<Route> {
    const response = await api.post<ApiResponse<Route>>('/operational/routes', route);
    return response.data.data;
  },

  async updateRouteStatus(id: string, status: Route['status']): Promise<Route> {
    const response = await api.patch<ApiResponse<Route>>(`/operational/routes/${id}/status`, { status });
    return response.data.data;
  },

  // Coletas
  async getPickups(): Promise<Pickup[]> {
    const response = await api.get<ApiResponse<Pickup[]>>('/operational/pickups');
    return response.data.data;
  },

  async createPickup(pickup: Omit<Pickup, 'id'>): Promise<Pickup> {
    const response = await api.post<ApiResponse<Pickup>>('/operational/pickups', pickup);
    return response.data.data;
  },

  async updatePickupStatus(id: string, status: Pickup['status']): Promise<Pickup> {
    const response = await api.patch<ApiResponse<Pickup>>(`/operational/pickups/${id}/status`, { status });
    return response.data.data;
  },

  // Cotações
  async getQuotes(): Promise<Quote[]> {
    const response = await api.get<ApiResponse<Quote[]>>('/operational/quotes');
    return response.data.data;
  },

  async createQuote(quote: Omit<Quote, 'id'>): Promise<Quote> {
    const response = await api.post<ApiResponse<Quote>>('/operational/quotes', quote);
    return response.data.data;
  },

  async updateQuoteStatus(id: string, status: Quote['status']): Promise<Quote> {
    const response = await api.patch<ApiResponse<Quote>>(`/operational/quotes/${id}/status`, { status });
    return response.data.data;
  }
};