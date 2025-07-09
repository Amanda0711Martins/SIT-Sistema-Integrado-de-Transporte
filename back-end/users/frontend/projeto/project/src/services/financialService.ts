import { api, ApiResponse } from './api';

export interface Invoice {
  id: string;
  clientId: string;
  clientName: string;
  number: string;
  amount: number;
  issueDate: string;
  dueDate: string;
  status: 'Pendente' | 'Paga' | 'Vencida' | 'Cancelada';
  description: string;
  paymentDate?: string;
}

export interface Transaction {
  id: string;
  type: 'Receita' | 'Despesa';
  description: string;
  amount: number;
  date: string;
  category: string;
  invoiceId?: string;
}

export interface FinancialStats {
  monthlyRevenue: number;
  accountsReceivable: number;
  accountsPayable: number;
  netProfit: number;
}

export interface PaymentData {
  invoiceId: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  notes?: string;
}

export const financialService = {
  // Faturas/Cobranças
  async getInvoices(): Promise<Invoice[]> {
    const response = await api.get<ApiResponse<Invoice[]>>('/financial/invoices');
    return response.data.data;
  },

  async getInvoice(id: string): Promise<Invoice> {
    const response = await api.get<ApiResponse<Invoice>>(`/financial/invoices/${id}`);
    return response.data.data;
  },

  async createInvoice(invoice: Omit<Invoice, 'id'>): Promise<Invoice> {
    const response = await api.post<ApiResponse<Invoice>>('/financial/invoices', invoice);
    return response.data.data;
  },

  async updateInvoice(id: string, invoice: Partial<Invoice>): Promise<Invoice> {
    const response = await api.put<ApiResponse<Invoice>>(`/financial/invoices/${id}`, invoice);
    return response.data.data;
  },

  async processPayment(paymentData: PaymentData): Promise<Invoice> {
    const response = await api.post<ApiResponse<Invoice>>('/financial/invoices/payment', paymentData);
    return response.data.data;
  },

  // Transações
  async getTransactions(startDate?: string, endDate?: string): Promise<Transaction[]> {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    const response = await api.get<ApiResponse<Transaction[]>>(`/financial/transactions?${params}`);
    return response.data.data;
  },

  async createTransaction(transaction: Omit<Transaction, 'id'>): Promise<Transaction> {
    const response = await api.post<ApiResponse<Transaction>>('/financial/transactions', transaction);
    return response.data.data;
  },

  // Estatísticas Financeiras
  async getFinancialStats(): Promise<FinancialStats> {
    const response = await api.get<ApiResponse<FinancialStats>>('/financial/stats');
    return response.data.data;
  },

  // Relatórios
  async generateReport(type: 'revenue' | 'expenses' | 'profit', startDate: string, endDate: string): Promise<Blob> {
    const response = await api.get(`/financial/reports/${type}`, {
      params: { startDate, endDate },
      responseType: 'blob'
    });
    return response.data;
  }
};