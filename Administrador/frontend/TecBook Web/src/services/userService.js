import api from '../utils/api';

const userService = {
  // Obtener lista de usuarios con filtros
  getUsuarios: async (filters = {}) => {
    try {
      const params = new URLSearchParams();

      // Agregar filtros solo si tienen valor válido
      if (filters.rol) params.append('rol', filters.rol);
      if (filters.activo !== undefined && filters.activo !== '') params.append('activo', filters.activo);
      if (filters.strikes !== undefined && filters.strikes !== '') params.append('strikes', filters.strikes);
      if (filters.buscar) params.append('buscar', filters.buscar);
      if (filters.page) params.append('page', filters.page);
      if (filters.page_size) params.append('page_size', filters.page_size);

      const response = await api.get(`/usuarios/?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuarios:', error);
      throw error;
    }
  },

  // Obtener usuario específico
  getUsuario: async (id) => {
    try {
      const response = await api.get(`/usuarios/${id}/`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuario:', error);
      throw error;
    }
  },

  // Aplicar strike a usuario
  aplicarStrike: async (userId, motivo) => {
    try {
      const response = await api.post(`/usuarios/${userId}/aplicar_strike/`, {
        motivo
      });
      return response.data;
    } catch (error) {
      console.error('Error al aplicar strike:', error);
      throw error;
    }
  },

  // Suspender usuario
  suspenderUsuario: async (userId, motivo, dias = 7) => {
    try {
      const response = await api.post(`/usuarios/${userId}/suspender_usuario/`, {
        motivo,
        dias
      });
      return response.data;
    } catch (error) {
      console.error('Error al suspender usuario:', error);
      throw error;
    }
  },

  // Reactivar usuario
  reactivarUsuario: async (userId) => {
    try {
      const response = await api.post(`/usuarios/${userId}/reactivar_usuario/`);
      return response.data;
    } catch (error) {
      console.error('Error al reactivar usuario:', error);
      throw error;
    }
  }
};

export default userService;