import api from './api';

export const freightService = {
    calculateFreight: async (freightData) => {
        try {
            const response = await api.post('/operational/freight/calculate', freightData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || 'Failed to calculate freight');
        }
    },

    getFreightHistory: async (clientId, page = 0, size = 10) => {
        try {
            const response = await api.get(`/operational/freight/history/${clientId}`, {
                params: { page, size }
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || 'Failed to fetch freight history');
        }
    },

    getFreightDetails: async (id) => {
        try {
            const response = await api.get(`/operational/freight/${id}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || 'Failed to fetch freight details');
        }
    },

    deleteFreightCalculation: async (id) => {
        try {
            await api.delete(`/operational/freight/${id}`);
            return { success: true };
        } catch (error) {
            throw new Error(error.response?.data?.message || 'Failed to delete freight calculation');
        }
    }
};

export default freightService;