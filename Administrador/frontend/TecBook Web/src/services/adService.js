import api from '../utils/api';

const adService = {
  // Obtener todos los anuncios (puedes agregar filtros si lo necesitas)
  getAds: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.estado) params.append('estado', filters.estado); // visible, oculto, etc
      if (filters.buscar) params.append('buscar', filters.buscar);
      if (filters.page) params.append('page', filters.page);
      if (filters.page_size) params.append('page_size', filters.page_size);
      const response = await api.get(`/moderacion/?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener anuncios:', error);
      throw error;
    }
  },

  // Censurar (ocultar) un anuncio
  censurarAd: async (adId, comentario) => {
    try {
      const response = await api.post(`/moderacion/${adId}/censurar_anuncio/`, { motivo: comentario });
      return response.data;
    } catch (error) {
      console.error('Error al censurar anuncio:', error);
      throw error;
    }
  },

  // Descensurar (hacer visible) un anuncio
  descensurarAd: async (adId) => {
    try {
      const response = await api.post(`/moderacion/${adId}/descensurar_anuncio/`);
      return response.data;
    } catch (error) {
      console.error('Error al descensurar anuncio:', error);
      throw error;
    }
  },

  // Obtener historial de moderación de un anuncio
  getAdModerationHistory: async (adId) => {
    try {
      const response = await api.get(`/moderacion/${adId}/historial_moderacion/`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener historial de moderación:', error);
      throw error;
    }
  }
};

export default adService;
