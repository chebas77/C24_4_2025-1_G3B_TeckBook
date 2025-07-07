
import apiService from './apiService';
import { ENDPOINTS } from '../config/apiConfig';

class AuthService {
  // Login tradicional
  async login(credentials) {
    try {
      const response = await apiService.post(ENDPOINTS.AUTH.LOGIN, credentials);
      
      if (response?.token) {
        localStorage.setItem('token', response.token);
        return response;
      }
      
      throw new Error('Respuesta inválida del servidor');
    } catch (error) {
      throw new Error(error.message || 'Error al iniciar sesión');
    }
  }

  // Login con Google
  loginWithGoogle() {
    apiService.redirectToGoogleAuth();
  }

  // Obtener información del usuario
  async getUser() {
    try {
      return await apiService.get(ENDPOINTS.AUTH.GET_USER);
    } catch (error) {
      throw new Error('Error al obtener información del usuario');
    }
  }

  // Logout
  logout() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }

  // Verificar si está autenticado
  isAuthenticated() {
    return !!localStorage.getItem('token');
  }

  // Obtener token
  getToken() {
    return localStorage.getItem('token');
  }
}

const authService = new AuthService();
export default authService;