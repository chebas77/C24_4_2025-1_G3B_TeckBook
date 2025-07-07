import { API_CONFIG, ENDPOINTS } from '../config/apiConfig';
import { toast } from 'react-toastify';

class ApiService {
  constructor() {
    this.baseURL = API_CONFIG.API_BASE_URL;
    this.timeout = API_CONFIG.DEFAULT_TIMEOUT;
    
    // 🔥 Cache para evitar múltiples peticiones del token
    this._tokenCache = null;
    this._tokenCacheTime = null;
    
    // 🔥 Debounce para logs repetitivos
    this._lastLogTime = 0;
    this._logCooldown = 2000; // 2 segundos entre logs del mismo token
  }

  getAuthToken() {
    const now = Date.now();
    
    // Si el token está en cache y es reciente, usarlo
    if (this._tokenCache && this._tokenCacheTime && (now - this._tokenCacheTime) < 1000) {
      return this._tokenCache;
    }
    
    // Obtener token fresco
    const token = localStorage.getItem('token');
    
    // Solo loggear si ha pasado el cooldown para evitar spam
    if (token && (now - this._lastLogTime) > this._logCooldown) {
      console.log("🔐 Token leído desde localStorage:", token.substring(0, 50) + "...");
      this._lastLogTime = now;
    }
    
    // Cachear token
    this._tokenCache = token;
    this._tokenCacheTime = now;
    
    return token;
  }

  buildHeaders(customHeaders = {}) {
    const headers = { ...API_CONFIG.DEFAULT_HEADERS };
    const token = this.getAuthToken();

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
        
        // Limpiar cache del token
        this._tokenCache = null;
        this._tokenCacheTime = null;

        toast.warning('Tu sesión ha expirado. Inicia sesión nuevamente.');
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
      // Solo loggear errores únicos para evitar spam
      if (error.message !== 'Sesión expirada') {
        console.error(`❌ Error en ${endpoint}:`, error.message);
      }
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

  // 🔥 Método para limpiar cache manualmente
  clearTokenCache() {
    this._tokenCache = null;
    this._tokenCacheTime = null;
  }
}

const apiService = new ApiService();
export default apiService;