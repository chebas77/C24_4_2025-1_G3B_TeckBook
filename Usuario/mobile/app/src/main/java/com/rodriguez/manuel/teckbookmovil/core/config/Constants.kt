package com.rodriguez.manuel.teckbookmovil.core.config

object Constants {
        enum class AnuncioType {
            INFORMATIVO, TAREA, EXAMEN, MATERIAL, EVENTO;

            companion object {
                fun fromString(value: String?): AnuncioType {
                    return when (value?.uppercase()) {
                        "INFORMATIVO" -> INFORMATIVO
                        "TAREA" -> TAREA
                        "EXAMEN" -> EXAMEN
                        "MATERIAL" -> MATERIAL
                        "EVENTO" -> EVENTO
                        else -> INFORMATIVO
                    }
                }
            }
        }

    // ========= VALIDATION =========
    object Validation {
        const val EMAIL_DOMAIN = "@tecsup.edu.pe"
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_FILE_SIZE_MB = 5
        val ALLOWED_IMAGE_TYPES = listOf("image/jpeg", "image/png", "image/jpg")
    }

    // ========= PATTERNS =========
    object Patterns {
        const val EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@tecsup\\.edu\\.pe$"
        const val PHONE_PATTERN = "^9\\d{8}$"
        const val CODIGO_AULA_PATTERN = "^[A-Z0-9]{6}\$"
    }

    // ========= ERROR MESSAGES =========
    object ErrorMessages {
        const val UNAUTHORIZED_ERROR = "No autorizado"
        const val FORBIDDEN_ERROR = "Acceso prohibido"
        const val NOT_FOUND_ERROR = "Recurso no encontrado"
        const val SERVER_ERROR = "Error del servidor"
        const val UNKNOWN_ERROR = "Error desconocido"
        const val INVALID_EMAIL_DOMAIN = "El correo debe ser institucional @tecsup.edu.pe"
        const val WEAK_PASSWORD = "La contraseña es demasiado corta"
        const val IMAGE_TOO_LARGE = "La imagen excede el tamaño máximo permitido"
        const val INVALID_IMAGE_FORMAT = "Formato de imagen no permitido"
        const val TIMEOUT_ERROR = "Tiempo de espera agotado. Intenta nuevamente."
        const val VALIDATION_ERROR = "Datos inválidos. Por favor revisa e intenta de nuevo."
        const val NETWORK_ERROR = "Error de red. Verifica tu conexión."
    }

    // ========= FORMAT =========
    object Format {
        const val DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val FILE_SIZE_FORMAT = "%.2f MB"
    }

    // ========= ACADEMIC =========
    object AcademicCycles {
        const val MIN_CYCLE = 1
        const val MAX_CYCLE = 10
    }

    // ========= LIMITS =========
    object Limits {
        const val MAX_ANUNCIO_TITLE_LENGTH = 100
        const val MAX_ANUNCIO_CONTENT_LENGTH = 1000
        const val MAX_MESSAGE_LENGTH = 250
    }
    object Animation {
        const val SPLASH_DELAY = 1500L // O el valor que prefieras
    }
}
