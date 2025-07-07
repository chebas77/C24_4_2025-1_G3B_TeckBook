package com.rodriguez.manuel.teckbookmovil.data.services

import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponse
import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponseFactory
import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.core.network.PublicApiService
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.core.utils.ValidationUtils
import com.rodriguez.manuel.teckbookmovil.data.models.auth.*
import com.rodriguez.manuel.teckbookmovil.data.models.common.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService(
    private val publicApiService: PublicApiService,
    private val authenticatedApiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        return withContext(Dispatchers.IO) {
            val validation = ValidationUtils.validateLoginData(email, password)
            if (!validation.isValid) {
                return@withContext ApiResponseFactory.error(validation.message ?: "Datos inválidos")
            }

            val request = LoginRequest(email.trim(), password)
            val response = publicApiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.token)
                loginResponse.user?.let { user ->
                    tokenManager.saveUserInfo(
                        email = user.correoInstitucional,
                        userId = user.id,
                        role = user.rol
                    )
                }
                ApiResponseFactory.success(loginResponse, loginResponse.message ?: "Login exitoso")
            } else {
                ApiResponseFactory.error("Credenciales inválidas", response.code())
            }
        }
    }

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
            val validation = ValidationUtils.validateRegistrationData(
                nombre, apellidos, email, password, confirmPassword, carreraId, cicloActual
            )
            if (!validation.isValid) {
                return@withContext ApiResponseFactory.error(validation.message ?: "Datos inválidos")
            }

            val request = RegisterRequest(
                nombre.trim(), apellidos.trim(), email.trim(), password,
                cicloActual, departamentoId, carreraId, seccionId, telefono?.takeIf { it.isNotBlank() }
            )

            val response = publicApiService.registerUser(request)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!, "Registro exitoso")
            } else {
                ApiResponseFactory.error("Error en registro", response.code())
            }
        }
    }

    suspend fun getUserInfo(): ApiResponse<UserInfo> = withContext(Dispatchers.IO) {
        val response = authenticatedApiService.getUserInfo()
        if (response.isSuccessful && response.body() != null) {
            val user = response.body()!!
            tokenManager.saveUserInfo(
                email = user.correoInstitucional,
                userId = user.id,
                role = user.rol
            )
            ApiResponseFactory.success(user)
        } else {
            ApiResponseFactory.unauthorizedError("Token inválido o expirado")
        }
    }

    suspend fun updateProfile(userId: Long, request: UpdateProfileRequest): ApiResponse<MessageResponse> {
        return withContext(Dispatchers.IO) {
            val response = authenticatedApiService.updateProfile(userId, request)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error actualizando perfil", response.code())
            }
        }
    }

    suspend fun logout(): ApiResponse<LogoutResponse> = withContext(Dispatchers.IO) {
        val response = authenticatedApiService.logout()
        tokenManager.logout()
        if (response.isSuccessful && response.body() != null) {
            ApiResponseFactory.success(response.body()!!)
        } else {
            ApiResponseFactory.success(
                LogoutResponse(
                    message = "Sesión cerrada localmente",
                    userEmail = null,
                    tokenInvalidated = true,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun checkTokenStatus(): ApiResponse<TokenStatusResponse> = withContext(Dispatchers.IO) {
        val response = authenticatedApiService.getTokenStatus()
        if (response.isSuccessful && response.body() != null) {
            val status = response.body()!!
            if (!status.isValid) tokenManager.logout()
            ApiResponseFactory.success(status)
        } else {
            ApiResponseFactory.unauthorizedError("Token inválido")
        }
    }

    fun isLoggedIn() = tokenManager.isLoggedIn()
}
