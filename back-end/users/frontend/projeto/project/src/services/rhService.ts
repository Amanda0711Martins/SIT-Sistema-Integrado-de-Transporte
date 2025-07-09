import { api, ApiResponse } from './api';

export interface Employee {
  id: string;
  name: string;
  email: string;
  role: string;
  department: string;
  status: 'Ativo' | 'Inativo';
  joinDate: string;
  salary?: number;
  phone?: string;
}

export interface PayrollData {
  employeeId: string;
  month: string;
  year: number;
  baseSalary: number;
  overtime: number;
  deductions: number;
  netSalary: number;
}

export interface TimeRecord {
  id: string;
  employeeId: string;
  date: string;
  clockIn: string;
  clockOut?: string;
  totalHours: number;
  overtime: number;
}

export const rhService = {
  // Funcionários
  async getEmployees(): Promise<Employee[]> {
    const response = await api.get<ApiResponse<Employee[]>>('/rh/employees');
    return response.data.data;
  },

  async getEmployee(id: string): Promise<Employee> {
    const response = await api.get<ApiResponse<Employee>>(`/rh/employees/${id}`);
    return response.data.data;
  },

  async createEmployee(employee: Omit<Employee, 'id'>): Promise<Employee> {
    const response = await api.post<ApiResponse<Employee>>('/rh/employees', employee);
    return response.data.data;
  },

  async updateEmployee(id: string, employee: Partial<Employee>): Promise<Employee> {
    const response = await api.put<ApiResponse<Employee>>(`/rh/employees/${id}`, employee);
    return response.data.data;
  },

  async deleteEmployee(id: string): Promise<void> {
    await api.delete(`/rh/employees/${id}`);
  },

  // Folha de Pagamento
  async getPayroll(month: string, year: number): Promise<PayrollData[]> {
    const response = await api.get<ApiResponse<PayrollData[]>>(`/rh/payroll?month=${month}&year=${year}`);
    return response.data.data;
  },

  async generatePayroll(month: string, year: number): Promise<PayrollData[]> {
    const response = await api.post<ApiResponse<PayrollData[]>>('/rh/payroll/generate', { month, year });
    return response.data.data;
  },

  // Controle de Ponto
  async getTimeRecords(employeeId?: string, startDate?: string, endDate?: string): Promise<TimeRecord[]> {
    const params = new URLSearchParams();
    if (employeeId) params.append('employeeId', employeeId);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    const response = await api.get<ApiResponse<TimeRecord[]>>(`/rh/timerecords?${params}`);
    return response.data.data;
  },

  async clockIn(employeeId: string): Promise<TimeRecord> {
    const response = await api.post<ApiResponse<TimeRecord>>('/rh/timerecords/clockin', { employeeId });
    return response.data.data;
  },

  async clockOut(employeeId: string): Promise<TimeRecord> {
    const response = await api.post<ApiResponse<TimeRecord>>('/rh/timerecords/clockout', { employeeId });
    return response.data.data;
  }
};