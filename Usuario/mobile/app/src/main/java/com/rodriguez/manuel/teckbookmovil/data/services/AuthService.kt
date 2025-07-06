package com.rodriguez.manuel.teckbookmovil.data.services

import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponse
import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponseFactory
import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.core.network.PublicApiService
import com.rodriguez.manuel.teckbookmovil.core.network.getDataOrNull
import com.rodriguez.manuel.teckbookmovil.core.network.isSuccess
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.core.utils.ValidationUtils
import com.rodriguez.manuel.teckbookmovil.data.models.auth.*
import com.rodriguez.manuel.teckbookmovil.data.models.common.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Servicio de autenticación que maneja login, logout, registro y gestión de sesiones
 * Integrado con el backend Spring Boot con OAuth2 y JWT
 */
class AuthService(
    private val publicApiService: PublicApiService,
    private val authenticatedApiService: ApiService,
    private val tokenManager: TokenManager
) {

    // ========== LOGIN TRADICIONAL ==========

    /**
     * Realiza login con email y contraseña
     */
    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Iniciando login para: $email")

                // Validar datos de entrada
                val validation = ValidationUtils.validateLoginData(email, password)
                if (!validation.isValid) {
                    Logger.w("AuthService", "Datos de login inválidos: ${validation.message}")
                    return@withContext ApiResponseFactory.error(validation.message ?: "Datos inválidos")
                }

                // Crear request
                val request = LoginRequest(
                    correoInstitucional = email.trim(),
                    password = password
                )

                // Realizar petición
                val response = publicApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Logger.auth("AuthService", "Login exitoso para: $email")

                    // Guardar token y datos de usuario
                    saveUserSession(loginResponse, email)

                    return@withContext ApiResponseFactory.success(
                        data = loginResponse,
                        message = loginResponse.message ?: "Login exitoso"
                    )
                } else {
                    val errorMessage = getErrorMessageFromResponse(response)
                    Logger.w("AuthService", "Login fallido para $email: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("AuthService", "Error de conexión en login", e)
                return@withContext ApiResponseFactory.networkError()
            } catch (e: SocketTimeoutException) {
                Logger.e("AuthService", "Timeout en login", e)
                return@withContext ApiResponseFactory.timeoutError()
            } catch (e: Exception) {
                Logger.e("AuthService", "Error inesperado en login", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GOOGLE OAUTH LOGIN ==========

    /**
     * Obtiene URL para iniciar Google OAuth2
     */
    suspend fun getGoogleLoginUrl(): ApiResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Obteniendo URL de Google OAuth2")

                val response = publicApiService.login(LoginRequest("", "")) // Placeholder para obtener redirect

                // En el contexto real, esto redirigiría a Google OAuth
                // Por ahora simulamos la URL
                val googleAuthUrl = "/oauth2/authorization/google"

                Logger.auth("AuthService", "URL de Google OAuth obtenida")
                return@withContext ApiResponseFactory.success(googleAuthUrl)

            } catch (e: Exception) {
                Logger.e("AuthService", "Error obteniendo URL de Google", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Procesa el resultado del login de Google OAuth2
     * Este método sería llamado después de recibir el token del callback
     */
    suspend fun processGoogleAuthCallback(token: String): ApiResponse<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Procesando callback de Google OAuth2")

                // Guardar token recibido
                tokenManager.saveToken(token)

                // Obtener información del usuario
                val userInfoResponse = getUserInfo()

                if (userInfoResponse.isSuccess()) {
                    val userInfo = userInfoResponse.getDataOrNull()!!

                    // Guardar información completa del usuario
                    saveUserInfo(userInfo)

                    Logger.auth("AuthService", "Google OAuth procesado exitosamente para: ${userInfo.correoInstitucional}")
                    return@withContext ApiResponseFactory.success(userInfo, "Login con Google exitoso")
                } else {
                    Logger.e("AuthService", "Error obteniendo info de usuario después de OAuth")
                    return@withContext userInfoResponse
                }

            } catch (e: Exception) {
                Logger.e("AuthService", "Error procesando Google callback", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== REGISTRO DE USUARIO ==========

    /**
     * Registra un nuevo usuario
     */
    suspend fun registerUser(
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        confirmPassword: String,
        carreraId: Long,
        cicloActual: Int,
        departamentoId: Long,
        seccionId: Long? = null,
        telefono: String? = null
    ): ApiResponse<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Registrando nuevo usuario: $email")

                // Validar todos los datos
                val validation = ValidationUtils.validateRegistrationData(
                    nombre, apellidos, email, password, confirmPassword, carreraId, cicloActual
                )

                if (!validation.isValid) {
                    Logger.w("AuthService", "Datos de registro inválidos: ${validation.message}")
                    return@withContext ApiResponseFactory.error(validation.message ?: "Datos inválidos")
                }

                // Crear request
                val request = RegisterRequest(
                    nombre = nombre.trim(),
                    apellidos = apellidos.trim(),
                    correoInstitucional = email.trim(),
                    password = password,
                    cicloActual = cicloActual,
                    departamentoId = departamentoId,
                    carreraId = carreraId,
                    seccionId = seccionId,
                    telefono = telefono?.takeIf { it.isNotBlank() }
                )

                // Realizar petición
                val response = publicApiService.registerUser(request)

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    Logger.auth("AuthService", "Registro exitoso para: $email")

                    return@withContext ApiResponseFactory.success(
                        data = registerResponse,
                        message = registerResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessageFromResponse(response)
                    Logger.w("AuthService", "Registro fallido para $email: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("AuthService", "Error de conexión en registro", e)
                return@withContext ApiResponseFactory.networkError()
            } catch (e: SocketTimeoutException) {
                Logger.e("AuthService", "Timeout en registro", e)
                return@withContext ApiResponseFactory.timeoutError()
            } catch (e: Exception) {
                Logger.e("AuthService", "Error inesperado en registro", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE SESIÓN ==========

    /**
     * Obtiene información del usuario autenticado
     */
    suspend fun getUserInfo(): ApiResponse<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Obteniendo información del usuario")

                if (!tokenManager.isLoggedIn()) {
                    Logger.w("AuthService", "Usuario no autenticado")
                    return@withContext ApiResponseFactory.unauthorizedError()
                }

                val response = authenticatedApiService.getUserInfo()

                if (response.isSuccessful && response.body() != null) {
                    val userInfo = response.body()!!
                    Logger.auth("AuthService", "Información de usuario obtenida: ${userInfo.correoInstitucional}")

                    // Actualizar información local
                    saveUserInfo(userInfo)

                    return@withContext ApiResponseFactory.success(userInfo)
                } else if (response.code() == 401) {
                    Logger.w("AuthService", "Token expirado al obtener info de usuario")
                    handleTokenExpiration()
                    return@withContext ApiResponseFactory.unauthorizedError()
                } else {
                    val errorMessage = getErrorMessageFromResponse(response)
                    Logger.w("AuthService", "Error obteniendo info de usuario: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AuthService", "Error inesperado obteniendo info de usuario", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Actualiza el perfil del usuario
     */
    suspend fun updateProfile(
        userId: Long,
        nombre: String,
        apellidos: String,
        cicloActual: Int?,
        carreraId: Long?,
        seccionId: Long?,
        telefono: String?,
        password: String? = null
    ): ApiResponse<MessageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Actualizando perfil de usuario: $userId")

                // Validar datos de actualización
                val validation = ValidationUtils.validateProfileUpdate(nombre, apellidos, telefono, cicloActual)
                if (!validation.isValid) {
                    return@withContext ApiResponseFactory.error(validation.message ?: "Datos inválidos")
                }

                val request = UpdateProfileRequest(
                    nombre = nombre.trim(),
                    apellidos = apellidos.trim(),
                    cicloActual = cicloActual,
                    carreraId = carreraId,
                    seccionId = seccionId,
                    telefono = telefono?.takeIf { it.isNotBlank() },
                    password = password?.takeIf { it.isNotBlank() }
                )

                val response = authenticatedApiService.updateUser(userId, request)

                if (response.isSuccessful && response.body() != null) {
                    val updateResponse = response.body()!!
                    Logger.auth("AuthService", "Perfil actualizado exitosamente")

                    // Actualizar información local del usuario
                    refreshUserInfo()

                    return@withContext ApiResponseFactory.success(updateResponse, updateResponse.message)
                } else {
                    val errorMessage = getErrorMessageFromResponse(response)
                    Logger.w("AuthService", "Error actualizando perfil: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AuthService", "Error inesperado actualizando perfil", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Verifica el estado del token actual
     */
    suspend fun checkTokenStatus(): ApiResponse<TokenStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Verificando estado del token")

                if (!tokenManager.hasToken()) {
                    return@withContext ApiResponseFactory.unauthorizedError("No hay token")
                }

                val response = authenticatedApiService.getTokenStatus()

                if (response.isSuccessful && response.body() != null) {
                    val tokenStatus = response.body()!!
                    Logger.auth("AuthService", "Estado del token: ${if (tokenStatus.isValid) "válido" else "inválido"}")

                    if (!tokenStatus.isValid) {
                        handleTokenExpiration()
                    }

                    return@withContext ApiResponseFactory.success(tokenStatus)
                } else {
                    Logger.w("AuthService", "Error verificando token: ${response.code()}")
                    return@withContext ApiResponseFactory.error("Error verificando token", response.code())
                }

            } catch (e: Exception) {
                Logger.e("AuthService", "Error verificando estado del token", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Cierra sesión del usuario
     */
    suspend fun logout(): ApiResponse<LogoutResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.auth("AuthService", "Cerrando sesión de usuario")

                val response = try {
                    authenticatedApiService.logout()
                } catch (e: Exception) {
                    Logger.w("AuthService", "Error en logout remoto, continuando con logout local", e)
                    null
                }

                // Limpiar sesión local independientemente del resultado del servidor
                val userEmail = tokenManager.getUserEmail()
                tokenManager.logout()

                Logger.auth("AuthService", "Sesión local cerrada para: $userEmail")

                val logoutResponse = response?.body() ?: LogoutResponse(
                    message = "Sesión cerrada localmente",
                    userEmail = userEmail,
                    tokenInvalidated = true,
                    timestamp = System.currentTimeMillis()
                )

                return@withContext ApiResponseFactory.success(logoutResponse, "Sesión cerrada correctamente")

            } catch (e: Exception) {
                Logger.e("AuthService", "Error en logout", e)

                // Forzar logout local aunque falle el remoto
                tokenManager.logout()

                return@withContext ApiResponseFactory.success(
                    LogoutResponse(
                        message = "Sesión cerrada localmente",
                        userEmail = null,
                        tokenInvalidated = true,
                        timestamp = System.currentTimeMillis()
                    ),
                    "Sesión cerrada"
                )
            }
        }
    }

    // ========== MÉTODOS DE ESTADO ==========

    /**
     * Verifica si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * Obtiene información básica del usuario logueado
     */
    fun getCurrentUser(): TokenManager.SessionInfo {
        return tokenManager.getSessionSummary()
    }

    /**
     * Verifica si el usuario actual es profesor
     */
    fun isCurrentUserProfesor(): Boolean {
        return tokenManager.isProfesor()
    }

    /**
     * Verifica si el usuario actual es estudiante
     */
    fun isCurrentUserEstudiante(): Boolean {
        return tokenManager.isEstudiante()
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Guarda la sesión del usuario después del login
     */
    private fun saveUserSession(loginResponse: LoginResponse, email: String) {
        // Guardar token
        tokenManager.saveToken(loginResponse.token)

        // Guardar información del usuario si está disponible
        loginResponse.user?.let { userInfo ->
            saveUserInfo(userInfo)
        }

        Logger.auth("AuthService", "Sesión guardada para: $email")
    }

    /**
     * Guarda información del usuario en storage local
     */
    private fun saveUserInfo(userInfo: UserInfo) {
        tokenManager.saveUserInfo(
            email = userInfo.correoInstitucional,
            userId = userInfo.id,
            role = userInfo.rol,
            name = userInfo.nombre,
            lastName = userInfo.apellidos,
            profileImageUrl = userInfo.profileImageUrl
        )
    }

    /**
     * Maneja la expiración del token
     */
    private fun handleTokenExpiration() {
        Logger.auth("AuthService", "Token expirado, limpiando sesión")
        tokenManager.logout()
    }

    /**
     * Refresca la información del usuario
     */
    private suspend fun refreshUserInfo() {
        try {
            val userInfoResponse = getUserInfo()
            if (userInfoResponse.isSuccess()) {
                Logger.auth("AuthService", "Información de usuario refrescada")
            }
        } catch (e: Exception) {
            Logger.w("AuthService", "Error refrescando info de usuario", e)
        }
    }

    /**
     * Extrae mensaje de error de una respuesta HTTP
     */
    private fun getErrorMessageFromResponse(response: retrofit2.Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                // Intentar parsear JSON de error
                val gson = com.google.gson.Gson()
                val errorResponse = gson.fromJson(errorBody, com.rodriguez.manuel.teckbookmovil.data.models.common.ErrorResponse::class.java)
                errorResponse.getErrorMessage()
            } else {
                "Error ${response.code()}: ${response.message()}"
            }
        } catch (e: Exception) {
            Logger.w("AuthService", "Error parseando respuesta de error", e)
            when (response.code()) {
                400 -> "Datos incorrectos"
                401 -> "Credenciales inválidas"
                403 -> "Acceso denegado"
                404 -> "Servicio no encontrado"
                500 -> "Error del servidor"
                else -> "Error de conexión"
            }
        }
    }

    /**
     * Valida que el email sea institucional
     */
    fun validateInstitutionalEmail(email: String): ValidationUtils.ValidationResult {
        return ValidationUtils.validateInstitutionalEmail(email)
    }

    /**
     * Valida fortaleza de contraseña
     */
    fun validatePassword(password: String): ValidationUtils.ValidationResult {
        return ValidationUtils.validatePassword(password)
    }

    /**
     * Valida datos de registro completos
     */
    fun validateRegistrationData(
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        confirmPassword: String,
        carreraId: Long?,
        cicloActual: Int?
    ): ValidationUtils.ValidationResult {
        return ValidationUtils.validateRegistrationData(
            nombre, apellidos, email, password, confirmPassword, carreraId, cicloActual
        )
    }
}