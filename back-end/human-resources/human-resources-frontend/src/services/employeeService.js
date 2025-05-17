// src/services/employeeService.js
import axios from 'axios';
import { handleError } from './errorHandler';

const API_URL = '/api/v1/employees';

export const employeeService = {
    /**
     * Get all employees with pagination
     * @param {Object} pageable - Page options (page, size, sort)
     * @returns {Promise<Object>} - Page of employees
     */
    getAllEmployees: async (pageable = { page: 0, size: 10 }) => {
        try {
            const response = await axios.get(API_URL, {
                params: {
                    page: pageable.page,
                    size: pageable.size,
                    sort: pageable.sort || 'name,asc'
                }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get all active employees
     * @returns {Promise<Array>} - List of active employees
     */
    getActiveEmployees: async () => {
        try {
            const response = await axios.get(`${API_URL}/active`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get employee by ID
     * @param {number} id - Employee ID
     * @returns {Promise<Object>} - Employee data
     */
    getEmployeeById: async (id) => {
        try {
            const response = await axios.get(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Create a new employee
     * @param {Object} employee - Employee data
     * @returns {Promise<Object>} - Created employee
     */
    createEmployee: async (employee) => {
        try {
            const response = await axios.post(API_URL, employee);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Update an existing employee
     * @param {number} id - Employee ID
     * @param {Object} employee - Updated employee data
     * @returns {Promise<Object>} - Updated employee
     */
    updateEmployee: async (id, employee) => {
        try {
            const response = await axios.put(`${API_URL}/${id}`, employee);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Delete (soft-delete) an employee
     * @param {number} id - Employee ID
     * @returns {Promise<void>}
     */
    deleteEmployee: async (id) => {
        try {
            await axios.delete(`${API_URL}/${id}`);
            return true;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get employees by department
     * @param {string} department - Department name
     * @returns {Promise<Array>} - List of employees in department
     */
    getEmployeesByDepartment: async (department) => {
        try {
            const response = await axios.get(`${API_URL}/department/${department}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    }
};