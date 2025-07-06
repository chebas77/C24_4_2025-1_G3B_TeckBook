package com.rodriguez.manuel.teckbookmovil.core.config

/**
 * Configuración global de la aplicación TecBook
 * Aquí se manejan las variables de entorno y configuraciones principales
 */
object AppConfig {

    // ========== URLs DEL BACKEND ==========

    /**
     * URL base del backend - CAMBIAR SEGÚN ENTORNO
     * - Desarrollo local: "http://10.0.2.2:8080" (para emulador Android)
     * - Desarrollo local: "http://192.168.1.XXX:8080" (para dispositivo físico)
     * - Producción: "https://tu-backend-desplegado.com"
     */
    const val BASE_URL = "http://10.0.2.2:8080"

    /**
     * URLs específicas basadas en tu backend
     */
    object Endpoints {
        // Auth endpoints (públicos)
        const val LOGIN = "/api/auth/login"
        const val GOOGLE_LOGIN = "/api/auth/google-login"
        const val LOGOUT = "/api/auth/logout"
        const val USER_INFO = "/api/auth/user"
        const val TOKEN_STATUS = "/api/auth/token/status"

        // Usuario endpoints
        const val USUARIO_ME = "/api/usuarios/me"
        const val USUARIO_REGISTER = "/api/usuarios/register"
        const val USUARIO_BY_ID = "/api/usuarios/{id}"

        // Aulas endpoints (protegidos)
        const val AULAS = "/api/aulas"
        const val AULA_BY_ID = "/api/aulas/{aulaId}"
        const val AULA_PARTICIPANTES = "/api/aulas/{aulaId}/participantes"
        const val AULA_ANUNCIOS = "/api/aulas/{aulaId}/anuncios"
        const val AULA_BUSCAR = "/api/aulas/buscar"

        // Carreras endpoints (públicos para filtros)
        const val CARRERAS_ACTIVAS = "/api/carreras/activas"
        const val CARRERAS_BY_DEPARTAMENTO = "/api/carreras/departamento/{departamentoId}/activas"
        const val DEPARTAMENTOS_ACTIVOS = "/api/departamentos/activos"
        const val CICLOS_TODOS = "/api/ciclos/todos"
        const val SECCIONES_BY_CARRERA_CICLO = "/api/secciones/carrera/{carreraId}/ciclo/{cicloId}"

        // Upload endpoints (protegidos)
        const val UPLOAD_PROFILE_IMAGE = "/api/upload/profile-image"
        const val CURRENT_PROFILE_IMAGE = "/api/upload/profile-image/current"

        // Invitaciones endpoints (protegidos)
        const val INVITACIONES_ENVIAR = "/api/invitaciones/enviar"
        const val INVITACIONES_ACEPTAR = "/api/invitaciones/aceptar/{codigoInvitacion}"
        const val INVITACIONES_MIS = "/api/invitaciones/mis-invitaciones"
        const val INVITACIONES_PENDIENTES = "/api/invitaciones/pendientes"

        // Anuncios generales (algunos públicos)
        const val ANUNCIOS_GENERAL = "/api/anuncios/general"
        const val ANUNCIOS_TODOS = "/api/anuncios/general/todos"

        // Health checks (públicos)
        const val HEALTH_CARRERAS = "/api/carreras/health"
        const val HEALTH_AULAS = "/api/aulas/health"
    }

    // ========== CONFIGURACIÓN DE RED ==========

    /**
     * Timeouts para las peticiones de red
     */
    object Network {
        const val CONNECT_TIMEOUT = 30L // segundos
        const val READ_TIMEOUT = 30L    // segundos
        const val WRITE_TIMEOUT = 30L   // segundos
    }

    // ========== CONFIGURACIÓN DE AUTENTICACIÓN ==========

    object Auth {
        const val TOKEN_PREFIX = "Bearer "
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_MULTIPART = "multipart/form-data"
    }

    // ========== CONFIGURACIÓN DE GOOGLE OAUTH ==========

    object Google {
        // Web client ID de tu configuración OAuth2
        const val WEB_CLIENT_ID = "YOUR_GOOGLE_WEB_CLIENT_ID"
        const val OAUTH_SCOPES = "email profile"
        const val REDIRECT_URI = "${BASE_URL}/oauth2/callback/google"
    }

    // ========== CONFIGURACIÓN DE ALMACENAMIENTO LOCAL ==========

    object Storage {
        const val PREFERENCES_NAME = "tecbook_prefs"
        const val KEY_JWT_TOKEN = "jwt_token"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_PROFILE_IMAGE_URL = "profile_image_url"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_LAST_NAME = "user_last_name"
    }

    // ========== CONFIGURACIÓN DE LA APP ==========

    object App {
        const val APP_NAME = "TecBook"
        const val VERSION = "1.0.0"
        const val DATABASE_VERSION = 1

        // Configuración de logging
        const val ENABLE_LOGGING = true // Cambiar a false en producción
        const val LOG_TAG = "TecBook"

        // Configuración de cache
        const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
        const val CACHE_MAX_AGE = 5 * 60 // 5 minutos
        const val CACHE_MAX_STALE = 7 * 24 * 60 * 60 // 7 días
    }

    // ========== VALIDACIONES ==========

    object Validation {
        const val EMAIL_DOMAIN = "@tecsup.edu.pe"
        const val MIN_PASSWORD_LENGTH = 6
        const val MAX_FILE_SIZE_MB = 5
        val ALLOWED_IMAGE_TYPES = listOf("image/jpeg", "image/png", "image/webp")
    }

    // ========== ROLES DE USUARIO ==========

    enum class UserRole(val value: String) {
        ESTUDIANTE("estudiante"),
        PROFESOR("profesor"),
        ADMIN("admin");

        companion object {
            fun fromString(value: String?): UserRole {
                return values().find { it.value.equals(value, ignoreCase = true) } ?: ESTUDIANTE
            }
        }
    }

    // ========== ESTADOS DE AULA ==========

    enum class AulaState(val value: String) {
        ACTIVA("activa"),
        INACTIVA("inactiva"),
        FINALIZADA("finalizada");

        companion object {
            fun fromString(value: String?): AulaState {
                return values().find { it.value.equals(value, ignoreCase = true) } ?: ACTIVA
            }
        }
    }

    // ========== TIPOS DE ANUNCIO ==========

    enum class AnuncioType(val value: String) {
        INFORMATIVO("informativo"),
        TAREA("tarea"),
        EXAMEN("examen"),
        MATERIAL("material"),
        EVENTO("evento");

        companion object {
            fun fromString(value: String?): AnuncioType {
                return values().find { it.value.equals(value, ignoreCase = true) } ?: INFORMATIVO
            }
        }
    }

    // ========== MÉTODOS UTILITARIOS ==========

    /**
     * Obtiene la URL completa para un endpoint
     */
    fun getFullUrl(endpoint: String): String {
        return BASE_URL + endpoint
    }

    /**
     * Verifica si un email es válido para la institución
     */
    fun isValidInstitutionalEmail(email: String): Boolean {
        return email.endsWith(Validation.EMAIL_DOMAIN)
    }

    /**
     * Obtiene el entorno actual basado en la URL
     */
    fun getCurrentEnvironment(): Environment {
        return when {
            BASE_URL.contains("localhost") || BASE_URL.contains("10.0.2.2") || BASE_URL.contains("192.168") -> Environment.DEVELOPMENT
            BASE_URL.contains("staging") || BASE_URL.contains("test") -> Environment.STAGING
            else -> Environment.PRODUCTION
        }
    }

    enum class Environment {
        DEVELOPMENT,
        STAGING,
        PRODUCTION
    }
}