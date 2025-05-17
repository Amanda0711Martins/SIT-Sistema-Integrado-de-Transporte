// src/services/expenseService.js
import api from '../utils/api';

const BASE_URL = '/api/expenses';

export const getExpenses = async (endpoint = '', params = {}) => {
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

export const getExpenseById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const createExpense = async (expenseData) => {
    const response = await api.post(BASE_URL, expenseData);
    return response.data;
};

export const updateExpense = async (id, expenseData) => {
    const response = await api.put(`${BASE_URL}/${id}`, expenseData);
    return response.data;
};

export const deleteExpense = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};

export const getExpensesByType = async (type, params = {}) => {
    return await getExpenses(`/type/${type}`, params);
};

export const getExpensesByCategory = async (category, params = {}) => {
    return await getExpenses(`/category/${category}`, params);
};

export const getExpensesByDateRange = async (startDate, endDate) => {
    const params = {
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString()
    };
    return await getExpenses('/date-range', params);
};