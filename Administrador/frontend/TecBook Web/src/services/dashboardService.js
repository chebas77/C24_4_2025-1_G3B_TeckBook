import api from '../utils/api';

const dashboardService = {
  // Obtener datos del dashboard
  getDashboardData: async () => {
    try {
      const response = await api.get('/dashboard-data/');
      return response.data;
    } catch (error) {
      console.error('Error al obtener datos del dashboard:', error);
      throw error;
    }
  },

  // Obtener estadísticas generales
  getEstadisticasGenerales: async (periodo = '30') => {
    try {
      const response = await api.get(`/estadisticas/generales/?periodo=${periodo}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener estadísticas:', error);
      throw error;
    }
  }
};

export default dashboardService;