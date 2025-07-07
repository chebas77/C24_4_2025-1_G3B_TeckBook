package com.rodriguez.manuel.teckbookmovil.core.config

import com.rodriguez.manuel.teckbookmovil.BuildConfig

object AppConfig {

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

    enum class AulaState {
        ACTIVA, INACTIVA, FINALIZADA;

        companion object {
            fun fromString(value: String?): AulaState {
                return values().find { it.name.equals(value, ignoreCase = true) } ?: ACTIVA
            }
        }
    }

    fun getOAuth2GoogleUrl(): String {
        return "${BuildConfig.BASE_URL}/oauth2/authorization/google"
    }

    object Endpoints {
        // === AUTH ===
        const val LOGIN = "/api/auth/login"
        const val GOOGLE_LOGIN = "/oauth2/authorization/google"
        const val LOGOUT = "/api/auth/logout"
        const val USER_INFO = "/api/auth/user"
        const val TOKEN_STATUS = "/api/auth/token/status"

        // === USUARIOS ===
        const val USUARIO_ME = "/api/usuarios/me"
        const val USUARIO_REGISTER = "/api/usuarios/register"
        const val USUARIO_BY_ID = "/api/usuarios/{id}"

        // === AULAS ===
        const val HEALTH_AULAS = "/api/aulas/health"
        const val AULAS = "/api/aulas"
        const val AULA_BY_ID = "/api/aulas/{aulaId}"
        const val AULA_BUSCAR = "/api/aulas/buscar"
        const val AULA_PARTICIPANTES = "/api/aulas/{aulaId}/participantes"
        const val AULA_ANUNCIOS = "/api/aulas/{aulaId}/anuncios"

        // === ANUNCIOS ===
        const val ANUNCIOS_GENERAL = "/api/anuncios/general"
        const val ANUNCIOS_TODOS = "/api/anuncios/general/todos"

        // === UPLOAD ===
        const val UPLOAD_PROFILE_IMAGE = "/api/upload/profile-image"
        const val CURRENT_PROFILE_IMAGE = "/api/upload/profile-image/current"

        // === CARRERAS, DEPARTAMENTOS Y CICLOS ===
        const val HEALTH_CARRERAS = "/api/carreras/health"
        const val CARRERAS_ACTIVAS = "/api/carreras/activas"
        const val CARRERAS_BY_DEPARTAMENTO = "/api/carreras/departamento/{departamentoId}/activas"
        const val DEPARTAMENTOS_ACTIVOS = "/api/departamentos/activos"
        const val CICLOS_TODOS = "/api/ciclos/todos"
        const val SECCIONES_BY_CARRERA_CICLO = "/api/secciones/carrera/{carreraId}/ciclo/{cicloId}"
    }

    object Google {
        const val WEB_CLIENT_ID = "3435xxx.apps.googleusercontent.com"
        const val OAUTH_SCOPES = "openid email profile"
    }

    object Storage {
        const val KEY_JWT_TOKEN = "jwt_token"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_ROLE = "user_role"
        const val PREFERENCES_NAME = "tecbook_prefs"
        const val DEFAULT_THEME_MODE = "light"
        const val CACHE_VALIDITY_TIME = 3600000L  // 1 hora en ms (o ajusta a tu flujo)
    }

    object Auth {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE_JSON = "application/json"
        const val TOKEN_PREFIX = "Bearer "
    }

    // TODO: Agrega aquí Network, Theme, Debug si los tienes
}
