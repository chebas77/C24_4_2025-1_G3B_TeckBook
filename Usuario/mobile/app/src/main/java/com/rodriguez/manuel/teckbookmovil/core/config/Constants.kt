package com.rodriguez.manuel.teckbookmovil.core.config

/**
 * Constantes utilizadas en toda la aplicación TecBook
 */
object Constants {

    // ========== CÓDIGOS DE RESULTADO ==========

    object RequestCodes {
        const val GOOGLE_SIGN_IN = 1001
        const val CAMERA_PERMISSION = 1002
        const val STORAGE_PERMISSION = 1003
        const val PICK_IMAGE = 1004
        const val CAMERA_CAPTURE = 1005
    }

    // ========== CÓDIGOS DE ERROR HTTP ==========

    object HttpCodes {
        const val OK = 200
        const val CREATED = 201
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val INTERNAL_SERVER_ERROR = 500
        const val SERVICE_UNAVAILABLE = 503
    }

    // ========== MENSAJES DE ERROR ==========

    object ErrorMessages {
        const val NETWORK_ERROR = "Error de conexión. Verifica tu internet."
        const val TIMEOUT_ERROR = "La conexión tardó demasiado. Inténtalo de nuevo."
        const val SERVER_ERROR = "Error del servidor. Inténtalo más tarde."
        const val UNAUTHORIZED_ERROR = "Sesión expirada. Vuelve a iniciar sesión."
        const val FORBIDDEN_ERROR = "No tienes permisos para realizar esta acción."
        const val NOT_FOUND_ERROR = "Recurso no encontrado."
        const val VALIDATION_ERROR = "Por favor verifica los datos ingresados."
        const val UNKNOWN_ERROR = "Ocurrió un error inesperado."

        // Errores específicos de TecBook
        const val INVALID_EMAIL_DOMAIN = "Debes usar tu correo institucional @tecsup.edu.pe"
        const val WEAK_PASSWORD = "La contraseña debe tener al menos 6 caracteres"
        const val PROFILE_INCOMPLETE = "Tu perfil está incompleto. Completa tus datos."
        const val NO_AULAS_FOUND = "No tienes aulas asignadas aún."
        const val TOKEN_EXPIRED = "Tu sesión ha expirado. Inicia sesión nuevamente."
        const val IMAGE_TOO_LARGE = "La imagen es demasiado grande. Máximo 5MB."
        const val INVALID_IMAGE_FORMAT = "Formato de imagen no válido. Usa JPG, PNG o WebP."
    }

    // ========== MENSAJES DE ÉXITO ==========

    object SuccessMessages {
        const val LOGIN_SUCCESS = "Bienvenido a TecBook"
        const val LOGOUT_SUCCESS = "Sesión cerrada correctamente"
        const val PROFILE_UPDATE_SUCCESS = "Perfil actualizado correctamente"
        const val IMAGE_UPLOAD_SUCCESS = "Imagen de perfil actualizada"
        const val INVITATION_SENT = "Invitación enviada correctamente"
        const val INVITATION_ACCEPTED = "Te has unido al aula exitosamente"
        const val ANUNCIO_CREATED = "Anuncio publicado correctamente"
    }

    // ========== ANIMATION DURATIONS ==========

    object Animation {
        const val FADE_DURATION = 300L
        const val SLIDE_DURATION = 250L
        const val SCALE_DURATION = 200L
        const val SPLASH_DELAY = 2000L
    }

    // ========== CONFIGURACIÓN DE UI ==========

    object UI {
        const val GRID_SPAN_COUNT = 2
        const val LIST_ITEM_ANIMATION_DELAY = 50L
        const val REFRESH_TIMEOUT = 5000L
        const val SEARCH_DELAY = 500L
        const val MAX_LINES_PREVIEW = 3
        const val PROFILE_IMAGE_SIZE = 300 // pixels
    }

    // ========== INTENTS Y EXTRAS ==========

    object Intent {
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_AULA_ID = "extra_aula_id"
        const val EXTRA_ANUNCIO_ID = "extra_anuncio_id"
        const val EXTRA_INVITATION_CODE = "extra_invitation_code"
        const val EXTRA_IS_NEW_USER = "extra_is_new_user"
        const val EXTRA_REQUIRES_COMPLETION = "extra_requires_completion"
        const val EXTRA_TOKEN = "extra_token"
    }

    // ========== NAVEGACIÓN ==========

    object Navigation {
        const val FRAGMENT_HOME = "home"
        const val FRAGMENT_AULAS = "aulas"
        const val FRAGMENT_PROFILE = "profile"
        const val FRAGMENT_INVITATIONS = "invitations"
    }

    // ========== CONFIGURACIÓN DE FORMATO ==========

    object Format {
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val TIME_PATTERN = "HH:mm"
        const val DATETIME_PATTERN = "dd/MM/yyyy HH:mm"
        const val SERVER_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
        const val FILE_SIZE_FORMAT = "%.2f MB"
    }

    // ========== LÍMITES Y RESTRICCIONES ==========

    object Limits {
        const val MAX_ANUNCIO_TITLE_LENGTH = 255
        const val MAX_ANUNCIO_CONTENT_LENGTH = 5000
        const val MAX_MESSAGE_LENGTH = 500
        const val MAX_SEARCH_RESULTS = 50
        const val PAGINATION_PAGE_SIZE = 20
        const val MAX_RECENT_SEARCHES = 5
    }

    // ========== CONFIGURACIÓN DE CACHE ==========

    object Cache {
        const val AULAS_CACHE_KEY = "aulas_cache"
        const val USER_INFO_CACHE_KEY = "user_info_cache"
        const val CARRERAS_CACHE_KEY = "carreras_cache"
        const val CACHE_VALIDITY_TIME = 5 * 60 * 1000L // 5 minutos
    }

    // ========== NOTIFICACIONES ==========

    object Notifications {
        const val CHANNEL_ID = "tecbook_notifications"
        const val CHANNEL_NAME = "TecBook Notificaciones"
        const val CHANNEL_DESCRIPTION = "Notificaciones de anuncios y actividades"
        const val NEW_ANUNCIO_ID = 1
        const val INVITATION_ID = 2
        const val REMINDER_ID = 3
    }

    // ========== MIME TYPES ==========

    object MimeTypes {
        const val IMAGE_JPEG = "image/jpeg"
        const val IMAGE_PNG = "image/png"
        const val IMAGE_WEBP = "image/webp"
        const val APPLICATION_PDF = "application/pdf"
        const val TEXT_PLAIN = "text/plain"
        const val ALL_IMAGES = "image/*"
        const val ALL_FILES = "*/*"
    }

    // ========== PATRONES DE REGEX ==========

    object Patterns {
        const val EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@tecsup\\.edu\\.pe$"
        const val PHONE_PATTERN = "^[0-9]{9}$"
        const val CODIGO_AULA_PATTERN = "^[A-Z0-9]{6,8}$"
    }

    // ========== CONFIGURACIÓN DE DEBUG ==========

    object Debug {
        const val LOG_NETWORK_REQUESTS = true
        const val LOG_USER_ACTIONS = true
        const val SHOW_DEBUG_INFO = true
        const val MOCK_RESPONSES = false // Para testing
    }

    // ========== CICLOS ACADÉMICOS ==========

    object AcademicCycles {
        val CYCLE_NAMES = mapOf(
            1 to "Primer Ciclo",
            2 to "Segundo Ciclo",
            3 to "Tercer Ciclo",
            4 to "Cuarto Ciclo",
            5 to "Quinto Ciclo",
            6 to "Sexto Ciclo"
        )

        const val MIN_CYCLE = 1
        const val MAX_CYCLE = 6
    }

    // ========== URLs EXTERNAS ==========

    object ExternalUrls {
        const val TECSUP_WEBSITE = "https://www.tecsup.edu.pe"
        const val TECSUP_SUPPORT = "https://soporte.tecsup.edu.pe"
        const val PRIVACY_POLICY = "https://www.tecsup.edu.pe/privacidad"
        const val TERMS_OF_SERVICE = "https://www.tecsup.edu.pe/terminos"
    }

    // ========== CONFIGURACIÓN DE TEMA ==========

    object Theme {
        const val LIGHT_MODE = "light"
        const val DARK_MODE = "dark"
        const val SYSTEM_MODE = "system"
        const val DEFAULT_MODE = SYSTEM_MODE
    }
}