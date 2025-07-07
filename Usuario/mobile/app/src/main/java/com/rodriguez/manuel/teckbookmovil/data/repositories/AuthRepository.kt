package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginRequest
import com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Login con credenciales (tradicional)
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body() ?: throw Exception("Respuesta vacía del servidor")
                    tokenManager.saveToken(loginResponse.token)
                    loginResponse
                } else {
                    throw Exception("Error: ${response.code()} ${response.message()}")
                }
            }
        }
    }

    /**
     * NO se usa más login con Google desde móvil.
     * El flujo OAuth2 se hace vía navegador y backend.
     * Puedes borrar o comentar esta función.
     */
    @Deprecated("El flujo OAuth2 se maneja por navegador CustomTabs")
    suspend fun loginWithGoogle(idToken: String): Result<LoginResponse> {
        throw NotImplementedError("Este flujo se maneja con OAuth2 redirect")
    }
}
