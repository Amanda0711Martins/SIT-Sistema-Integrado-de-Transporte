// src/services/timeTrackingService.js
import axios from 'axios';
import { handleError } from './errorHandler';

const API_URL = '/api/v1/time-tracking';

export const timeTrackingService = {
    /**
     * Get all time entries
     * @returns {Promise<Array>} - List of time entries
     */
    getAllTimeEntries: async () => {
        try {
            const response = await axios.get(API_URL);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get time entry by ID
     * @param {number} id - Time entry ID
     * @returns {Promise<Object>} - Time entry data
     */
    getTimeEntryById: async (id) => {
        try {
            const response = await axios.get(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get time entries by employee ID
     * @param {number} employeeId - Employee ID
     * @returns {Promise<Array>} - List of employee's time entries
     */
    getTimeEntriesByEmployeeId: async (employeeId) => {
        try {
            const response = await axios.get(`${API_URL}/employee/${employeeId}`);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Get time entries within a date range
     * @param {Date} start - Start date
     * @param {Date} end - End date
     * @returns {Promise<Array>} - List of time entries in the date range
     */
    getTimeEntriesByDateRange: async (start, end) => {
        try {
            const response = await axios.get(`${API_URL}/range`, {
                params: {
                    start: start.toISOString(),
                    end: end.toISOString()
                }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Record a new time entry
     * @param {Object} timeEntry - Time entry data
     * @returns {Promise<Object>} - Created time entry
     */
    recordEntry: async (timeEntry) => {
        try {
            const location = await getLocationString();
            const response = await axios.post(`${API_URL}/entry`, timeEntry, {
                headers: {
                    'X-Location': location
                }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Record exit time for a time entry
     * @param {number} id - Time entry ID
     * @param {string} notes - Optional notes about the work done
     * @returns {Promise<Object>} - Updated time entry
     */
    recordExit: async (id, notes = '') => {
        try {
            const location = await getLocationString();
            const response = await axios.put(`${API_URL}/${id}/exit`, null, {
                params: { notes },
                headers: {
                    'X-Location': location
                }
            });
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Update a time entry
     * @param {number} id - Time entry ID
     * @param {Object} timeEntry - Updated time entry data
     * @returns {Promise<Object>} - Updated time entry
     */
    updateTimeEntry: async (id, timeEntry) => {
        try {
            const response = await axios.put(`${API_URL}/${id}`, timeEntry);
            return response.data;
        } catch (error) {
            return handleError(error);
        }
    },

    /**
     * Delete a time entry
     * @param {number} id - Time entry ID
     * @returns {Promise<boolean>} - Success indicator
     */
    deleteTimeEntry: async (id) => {
        try {
            await axios.delete(`${API_URL}/${id}`);
            return true;
        } catch (error) {
            return handleError(error);
        }
    }
};

// Helper function to get location string if available
const getLocationString = async () => {
    try {
        if (navigator.geolocation) {
            return new Promise((resolve) => {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        resolve(`${position.coords.latitude},${position.coords.longitude}`);
                    },
                    () => {
                        resolve('location-unavailable');
                    }
                );
            });
        }
        return 'geolocation-unsupported';
    } catch (error) {
        console.error('Error getting location:', error);
        return 'location-error';
    }
};