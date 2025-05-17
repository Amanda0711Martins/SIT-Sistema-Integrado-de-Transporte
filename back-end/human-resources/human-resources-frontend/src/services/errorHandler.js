// src/services/errorHandler.js
import { toast } from 'react-toastify';

/**
 * Handles API errors consistently
 * @param {Error} error - The error object from the API call
 * @returns {Promise<never>} - Rejects with the error
 */
export const handleError = (error) => {
    // Extract error information
    let errorMessage = 'An unexpected error occurred';
    let statusCode = 500;

    if (error.response) {
        // The request was made and the server responded with an error status
        statusCode = error.response.status;

        if (error.response.data) {
            if (error.response.data.message) {
                errorMessage = error.response.data.message;
            } else if (error.response.data.errors) {
                // Handle validation errors
                errorMessage = Object.entries(error.response.data.errors)
                    .map(([field, msg]) => `${field}: ${msg}`)
                    .join(', ');
            }
        }
    } else if (error.request) {
        // The request was made but no response was received
        errorMessage = 'No response received from server. Please check your connection.';
    } else {
        // Something happened in setting up the request
        errorMessage = error.message;
    }

    // Show toast notification for user feedback
    toast.error(errorMessage);

    // Return a rejected promise with detailed error information
    return Promise.reject({
        message: errorMessage,
        statusCode,
        originalError: error
    });
};