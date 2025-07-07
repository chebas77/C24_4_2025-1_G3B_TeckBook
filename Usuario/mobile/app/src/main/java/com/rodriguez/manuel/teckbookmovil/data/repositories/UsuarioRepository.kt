package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.data.models.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Login tradicional con correo y contraseña
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Guardar token y datos del usuario
                        tokenManager.saveToken(loginResponse.token)
                        loginResponse.user?.let { user ->
                            tokenManager.saveUserInfo(
                                email = user.correoInstitucional,
                                userId = user.id,
                                role = user.rol,
                            )
                        }
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Login con Google OAuth2 ID Token
     */
    suspend fun loginWithGoogle(idToken: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginWithGoogle(GoogleLoginRequest(idToken))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        tokenManager.saveToken(loginResponse.token)
                        loginResponse.user?.let { user ->
                            tokenManager.saveUserInfo(
                                email = user.correoInstitucional,
                                userId = user.id,
                                role = user.rol,
                            )
                        }
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Verifica el estado del token actual
     */
    suspend fun checkTokenStatus(): Result<TokenStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkTokenStatus()
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Cierra sesión (logout)
     */
    suspend fun logout(): Result<LogoutResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout()
                tokenManager.logout() // Limpia datos locales sin importar resultado
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                tokenManager.logout() // Asegurar limpieza
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene información del perfil del usuario autenticado
     */
    suspend fun getProfile(): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Actualiza datos de perfil
     */
    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
