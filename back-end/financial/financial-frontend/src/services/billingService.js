// src/services/billingService.js
import api from '../utils/api';

const BASE_URL = '/api/billings';

export const getBillings = async (endpoint = '', params = {}) => {
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

export const getBillingById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const getBillingByNumber = async (billingNumber) => {
    const response = await api.get(`${BASE_URL}/number/${billingNumber}`);
    return response.data;
};

export const createBilling = async (billingData) => {
    const response = await api.post(BASE_URL, billingData);
    return response.data;
};

export const updateBilling = async (id, billingData) => {
    const response = await api.put(`${BASE_URL}/${id}`, billingData);
    return response.data;
};

export const deleteBilling = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};

export const getBillingsByClientId = async (clientId, params = {}) => {
    return await getBillings(`/client/${clientId}`, params);
};

export const getBillingsByStatus = async (status, params = {}) => {
    return await getBillings(`/status/${status}`, params);
};

export const getOverdueBillings = async () => {
    const response = await api.get(`${BASE_URL}/overdue`);
    return response.data;
};

export const markBillingAsPaid = async (id, paymentMethod) => {
    const response = await api.post(`${BASE_URL}/${id}/pay`, { paymentMethod });
    return response.data;
};

export const cancelBilling = async (id) => {
    const response = await api.post(`${BASE_URL}/${id}/cancel`);
    return response.data;
};

export const getBillingsByDateRange = async (startDate, endDate) => {
    const params = {
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString()
    };
    return await getBillings('/date-range', params);
};