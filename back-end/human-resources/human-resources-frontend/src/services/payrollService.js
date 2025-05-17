// src/services/payrollService.js
import axios from 'axios';
import { handleError } from './errorHandler';

const API_URL = '/api/v1/payrolls';

export const payrollService = {
    /**
     * Get all payrolls
     * @returns {Promise<Array>} - List of payrolls
     */
    getAllPayrolls: async () => {
        try {
            const response = await axios.get(API_URL);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get payroll by ID
     * @param {number} id - Payroll ID
     * @returns {Promise<Object>} - Payroll data
     */
    getPayrollById: async (id) => {
        try {
            const response = await axios.get(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get payrolls by employee ID
     * @param {number} employeeId - Employee ID
     * @returns {Promise<Array>} - List of employee's payrolls
     */
    getPayrollsByEmployee: async (employeeId) => {
        try {
            const response = await axios.get(`${API_URL}/employee/${employeeId}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get payrolls by year and month
     * @param {number} year - Year
     * @param {number} month - Month (1-12)
     * @returns {Promise<Array>} - List of payrolls for the month
     */
    getPayrollsByMonth: async (year, month) => {
        try {
            const response = await axios.get(`${API_URL}/month/${year}/${month}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Calculate payroll for an employee
     * @param {number} employeeId - Employee ID
     * @param {number} year - Year
     * @param {number} month - Month (1-12)
     * @param {number} bonuses - Optional bonus amount
     * @param {number} otherDeductions - Optional additional deductions
     * @returns {Promise<Object>} - Calculated payroll
     */
    calculatePayroll: async (employeeId, year, month, bonuses = 0, otherDeductions = 0) => {
        try {
            const response = await axios.post(`${API_URL}/calculate`, null, {
                params: {
                    employeeId,
                    year,
                    month,
                    bonuses,
                    otherDeductions
                }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Update payroll status
     * @param {number} id - Payroll ID
     * @param {string} status - New status (DRAFT, PROCESSED, PAID, CANCELED)
     * @returns {Promise<Object>} - Updated payroll
     */
    updatePayrollStatus: async (id, status) => {
        try {
            const response = await axios.put(`${API_URL}/${id}/status`, null, {
                params: { status }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    }
};