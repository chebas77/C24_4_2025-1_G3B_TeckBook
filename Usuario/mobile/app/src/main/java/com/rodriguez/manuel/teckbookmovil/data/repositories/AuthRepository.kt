package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.data.models.auth.GoogleLoginRequest
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginRequest
import com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        tokenManager.saveToken(loginResponse.token)
                        loginResponse.user?.let { user ->
                            tokenManager.saveUserInfo(
                                email = user.correoInstitucional,
                                userId = user.id,
                                role = user.rol,
                                name = user.nombre,
                                lastName = user.apellidos,
                                profileImageUrl = user.profileImageUrl
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
     * Login con Google usando ID Token OAuth2
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
                                name = user.nombre,
                                lastName = user.apellidos,
                                profileImageUrl = user.profileImageUrl
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
}
