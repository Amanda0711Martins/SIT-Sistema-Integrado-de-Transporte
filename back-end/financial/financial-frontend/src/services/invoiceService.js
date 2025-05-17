// src/services/invoiceService.js
import api from '../utils/api';

const BASE_URL = '/api/invoices';

export const getInvoices = async (endpoint = '', params = {}) => {
    const queryParams = new URLSearchParams();

    // Add pagination parameters if provided
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);

    // Add any additional parameters
    Object.keys(params).forEach(key => {
        if (!['page', 'size'].includes(key)) {
            queryParams.append(key, params[key]);
        }
    });

    const queryString = queryParams.toString();
    const url = `${BASE_URL}${endpoint}${queryString ? `?${queryString}` : ''}`;

    const response = await api.get(url);
    return response.data;
};

export const getInvoiceById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const getInvoiceByNumber = async (invoiceNumber) => {
    const response = await api.get(`${BASE_URL}/number/${invoiceNumber}`);
    return response.data;
};

export const createInvoice = async (invoiceData) => {
    const response = await api.post(BASE_URL, invoiceData);
    return response.data;
};

export const updateInvoice = async (id, invoiceData) => {
    const response = await api.put(`${BASE_URL}/${id}`, invoiceData);
    return response.data;
};

export const deleteInvoice = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};

export const getInvoicesByClientId = async (clientId, params = {}) => {
    return await getInvoices(`/client/${clientId}`, params);
};

export const getInvoicesByStatus = async (status, params = {}) => {
    return await getInvoices(`/status/${status}`, params);
};

export const getInvoicesByDateRange = async (startDate, endDate) => {
    const params = {
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString()
    };
    return await getInvoices('/date-range', params);
};

export const issueInvoice = async (id) => {
    const response = await api.post(`${BASE_URL}/${id}/issue`);
    return response.data;
};

export const cancelInvoice = async (id) => {
    const response = await api.post(`${BASE_URL}/${id}/cancel`);
    return response.data;
};