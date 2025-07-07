// ===============================================
// src/config/apiConfig.js - Backend API Config
// ===============================================
const API_CONFIG = {
  // URLs base
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'https://rival-terra-chebas77-e06d6aa9.koyeb.app',
FRONTEND_URL: import.meta.env.VITE_FRONTEND_URL || 'c24-4-2025-1-g3-b-teck-book.vercel.app',

  // Información de la app
  APP_NAME: import.meta.env.VITE_APP_NAME || 'TeckBook',
  APP_VERSION: import.meta.env.VITE_APP_VERSION || '1.0.0',
  ENVIRONMENT: import.meta.env.VITE_ENVIRONMENT || 'development',
  
  // Configuración de requests
  DEFAULT_TIMEOUT: 10000,
  RETRY_ATTEMPTS: 3,
  
  // Headers por defecto
  DEFAULT_HEADERS: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  
  // Configuración de archivos
  MAX_FILE_SIZE: 10 * 1024 * 1024, // 10MB
  ALLOWED_FILE_TYPES: [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'image/jpeg',
    'image/png',
    'image/gif',
    'text/plain'
  ]
};

// ===============================================
// ENDPOINTS DEL BACKEND
// ===============================================
const ENDPOINTS = {
  // Autenticación
  AUTH: {
    LOGIN: '/api/auth/login',
    GOOGLE_LOGIN: '/oauth2/authorize/google',
    GET_USER: '/api/auth/user',
    LOGOUT: '/api/auth/logout'
  },
  
  // Usuarios
  USERS: {
    BASE: '/api/usuarios',
    REGISTER: '/api/usuarios/register',
    ME: '/api/usuarios/me',
    BY_ID: (id) => `/api/usuarios/${id}`,
    UPDATE: (id) => `/api/usuarios/${id}`,
    DELETE: (id) => `/api/usuarios/${id}`
  },
  
  // Aulas
  AULAS: {
    BASE: '/api/aulas',
    BY_ID: (id) => `/api/aulas/${id}`,
    CREATE: '/api/aulas',
    UPDATE: (id) => `/api/aulas/${id}`,
    DELETE: (id) => `/api/aulas/${id}`,
    // Anuncios de aulas
    ANUNCIOS: {
      BY_AULA: (aulaId) => `/api/aulas/${aulaId}/anuncios`,
      CREATE: (aulaId) => `/api/aulas/${aulaId}/anuncios`,
      UPDATE: (aulaId, anuncioId) => `/api/aulas/${aulaId}/anuncios/${anuncioId}`,
      DELETE: (aulaId, anuncioId) => `/api/aulas/${aulaId}/anuncios/${anuncioId}`
    }
  },
  
  // Departamentos
  DEPARTAMENTOS: {
    BASE: '/api/departamentos',
    ACTIVOS: '/api/departamentos/activos',
    BY_ID: (id) => `/api/departamentos/${id}`
  },
  
  // Carreras
  CARRERAS: {
    BASE: '/api/carreras',
    ACTIVAS: '/api/carreras/activas',
    BY_DEPARTAMENTO: (deptoId) => `/api/carreras/departamento/${deptoId}/activas`,
    BY_ID: (id) => `/api/carreras/${id}`
  },
  
  // Ciclos
  CICLOS: {
    BASE: '/api/ciclos',
    TODOS: '/api/ciclos/todos',
    BY_CARRERA: (carreraId) => `/api/ciclos/carrera/${carreraId}`,
    BY_ID: (id) => `/api/ciclos/${id}`
  },
  
  // Secciones
  SECCIONES: {
    BASE: '/api/secciones',
    BY_CARRERA: (carreraId) => `/api/secciones/carrera/${carreraId}`,
    BY_CARRERA_CICLO: (carreraId, cicloId) => `/api/secciones/carrera/${carreraId}/ciclo/${cicloId}`
  },
  
  // Upload/Files
  UPLOAD: {
    BASE: '/api/upload',
    IMAGE: '/api/upload/profile-image'
  },
  
  // Health/Debug
  HEALTH: {
    BASE: '/api/health',
    DEBUG: '/api/debug'
  }
};

// ===============================================
// RUTAS DEL FRONTEND
// ===============================================
const ROUTES = {
  // Públicas
  PUBLIC: {
    HOME: '/',
    LOGIN: '/',
    REGISTER: '/register',
    RECOVER_PASSWORD: '/recuperar'
  },

  // Protegidas
  PROTECTED: {
    DASHBOARD: '/home',
    PROFILE: '/perfil',
    AULAS: '/aulas',
    AULA_DETAIL: (id) => `/aulas/${id}`,
    CREATE_AULA: '/crear-aula',
    ANNOUNCEMENTS: '/anuncios-general'
  }
};

export { API_CONFIG, ENDPOINTS, ROUTES };