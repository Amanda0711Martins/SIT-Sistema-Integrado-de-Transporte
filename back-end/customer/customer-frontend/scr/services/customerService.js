// src/services/customerService.js

import api from './api';

export const fetchCustomers = async (params = {}) => {
    const { page = 0, size = 10, sortBy = 'name', sortDirection = 'ASC', name, status } = params;

    try {
        let url;
        if (name || status) {
            url = '/customers/search';
            const response = await api.get(url, {
                params: { page, size, name, status }
            });
            return response.data;
        } else {
            url = '/customers';
            const response = await api.get(url, {
                params: { page, size, sortBy, sortDirection }
            });
            return response.data;
        }
    } catch (error) {
        throw error;
    }
};

export const getCustomerById = async (id) => {
    try {
        const response = await api.get(`/customers/${id}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getCustomerByCnpj = async (cnpj) => {
    try {
        const response = await api.get(`/customers/cnpj/${cnpj}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const createCustomer = async (customerData) => {
    try {
        const response = await api.post('/customers', customerData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const updateCustomer = async (id, customerData) => {
    try {
        const response = await api.put(`/customers/${id}`, customerData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const deleteCustomer = async (id) => {
    try {
        await api.delete(`/customers/${id}`);
    } catch (error) {
        throw error;
    }
};

export const changeCustomerStatus = async (id, status) => {
    try {
        const response = await api.patch(`/customers/${id}/status?status=${status}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};