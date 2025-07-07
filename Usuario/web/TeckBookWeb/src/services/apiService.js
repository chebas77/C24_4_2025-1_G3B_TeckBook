import { API_CONFIG, ENDPOINTS } from '../config/apiConfig';
import { toast } from 'react-toastify'; // 👈 Importa el toast

class ApiService {
  constructor() {
    this.baseURL = API_CONFIG.API_BASE_URL;
    this.timeout = API_CONFIG.DEFAULT_TIMEOUT;
  }

  getAuthToken() {
    return localStorage.getItem('token');
  }

  buildHeaders(customHeaders = {}) {
    const headers = { ...API_CONFIG.DEFAULT_HEADERS };
    const token = this.getAuthToken();
    console.log("🔐 Token leído desde localStorage:", token);

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return { ...headers, ...customHeaders };
  }

  buildUrl(endpoint) {
    return `${this.baseURL}${endpoint}`;
  }

  async request(endpoint, options = {}) {
    const url = this.buildUrl(endpoint);
    const headers = this.buildHeaders(options.headers);

    const config = {
      ...options,
      headers,
      signal: options.signal || AbortSignal.timeout(this.timeout)
    };

    try {
      const response = await fetch(url, config);

      if (response.status === 401) {
        console.warn("⛔ Sesión expirada (401)");

        // 🔔 Mostrar toast antes de redirigir
        toast.warning('Tu sesión ha expirado. Inicia sesión nuevamente.');

        // Limpiar token y redirigir después de un breve retraso
        localStorage.removeItem('token');

        setTimeout(() => {
          window.location.href = '/';
        }, 2500);

        throw new Error('Sesión expirada');
      }

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`HTTP ${response.status}: ${errorData || response.statusText}`);
      }

      if (response.status === 204) {
        return null;
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }

      return await response.text();
    } catch (error) {
      console.error(`❌ Error en ${endpoint}:`, error);
      throw error;
    }
  }

  async get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  async post(endpoint, data, options = {}) {
    const body = data instanceof FormData ? data : JSON.stringify(data);
    const headers = data instanceof FormData ? {} : options.headers;

    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body,
      headers
    });
  }

  async put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }

  redirectToGoogleAuth() {
    window.location.href = this.buildUrl(ENDPOINTS.AUTH.GOOGLE_LOGIN);
  }
}

const apiService = new ApiService();
export default apiService;
