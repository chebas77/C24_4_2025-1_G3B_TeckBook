import api from '../utils/api';

const authService = {
  // Login de administrador
  login: async (credentials) => {
    try {
      const response = await api.post('/auth/login/', {
        correo_institucional: credentials.email,
        password: credentials.password
      });
      return response;
    } catch (error) {
      throw error;
    }
  },

  // Logout
  logout: async (refreshToken) => {
    try {
      const response = await api.post('/auth/logout/', {
        refresh: refreshToken
      });
      return response;
    } catch (error) {
      console.error('Error en logout:', error);
      // No lanzar error para permitir logout local
    }
  },

  // Renovar token
  refreshToken: async () => {
    try {
      const refreshToken = localStorage.getItem('refresh_token');
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await api.post('/auth/refresh/', {
        refresh: refreshToken
      });

      const newAccessToken = response.data.access;
      localStorage.setItem('access_token', newAccessToken);
      
      return response;
    } catch (error) {
      // Si falla la renovación, limpiar tokens
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('user_data');
      throw error;
    }
  },

  // Verificar si el token es válido
  verifyToken: async (token) => {
    try {
      // Hacer una petición simple para verificar el token
      const response = await api.get('/usuarios/', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      return response.status === 200;
    } catch (error) {
      return false;
    }
  },

  // Obtener usuario actual
  getCurrentUser: () => {
    try {
      const userData = localStorage.getItem('user_data');
      return userData ? JSON.parse(userData) : null;
    } catch (error) {
      return null;
    }
  },

  // Verificar si está autenticado
  isAuthenticated: () => {
    const token = localStorage.getItem('access_token');
    const userData = localStorage.getItem('user_data');
    return !!(token && userData);
  },

  // Obtener token de acceso
  getAccessToken: () => {
    return localStorage.getItem('access_token');
  },

  // Limpiar datos de autenticación
  clearAuthData: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_data');
  }
};

export default authService;