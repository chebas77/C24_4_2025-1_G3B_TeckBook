package com.rodriguez.manuel.teckbookmovil.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.rodriguez.manuel.teckbookmovil.data.models.Resource
import com.rodriguez.manuel.teckbookmovil.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🔐 REPOSITORIO DE AUTENTICACIÓN
 * Maneja login, registro y persistencia de tokens
 */
class AuthRepository(private val context: Context) {

    private val apiService = NetworkModule.apiService

    // 🔒 SharedPreferences encriptadas
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        "auth_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // ========== AUTENTICACIÓN ==========

    /**
     * 🔐 Iniciar sesión
     */
    suspend fun login(email: String, password: String): Resource<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val loginRequest = LoginRequest(email, password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // Guardar token y datos del usuario
                    saveAuthData(loginResponse)
                    Resource.Success(loginResponse)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorMessage = parseErrorMessage(response.code(), response.errorBody()?.string())
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    /**
     * 📝 Registrar nuevo usuario
     */
    suspend fun register(usuario: Usuario): Resource<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.registerUser(usuario)

            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorMessage = parseErrorMessage(response.code(), response.errorBody()?.string())
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    /**
     * 👤 Obtener información del usuario
     */
    suspend fun getUserInfo(): Resource<Usuario> = withContext(Dispatchers.IO) {
        try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                return@withContext Resource.Error("No hay token de autenticación")
            }

            val response = apiService.getUserInfo("Bearer $token")

            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Resource.Success(user)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                if (response.code() == 401) {
                    // Token expirado, cerrar sesión
                    logout()
                }
                val errorMessage = parseErrorMessage(response.code(), response.errorBody()?.string())
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    /**
     * 🚪 Cerrar sesión
     */
    suspend fun logout(): Resource<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val token = getToken()
            if (!token.isNullOrEmpty()) {
                // Intentar invalidar token en el servidor
                try {
                    apiService.logout("Bearer $token")
                } catch (e: Exception) {
                    // Si falla, continuar con logout local
                }
            }

            // Limpiar datos locales
            clearAuthData()
            Resource.Success(mapOf("message" to "Sesión cerrada correctamente"))
        } catch (e: Exception) {
            // Aunque falle, limpiar datos locales
            clearAuthData()
            Resource.Error("Error al cerrar sesión, pero datos locales limpiados")
        }
    }

    // ========== GESTIÓN DE TOKENS ==========

    /**
     * 💾 Guardar datos de autenticación
     */
    private fun saveAuthData(loginResponse: LoginResponse) {
        with(encryptedPrefs.edit()) {
            putString(KEY_TOKEN, loginResponse.token)
            putBoolean(KEY_IS_LOGGED_IN, true)

            loginResponse.user?.let { user ->
                putString(KEY_USER_EMAIL, user.correoInstitucional)
            }

            apply()
        }
    }

    /**
     * 🧹 Limpiar datos de autenticación
     */
    private fun clearAuthData() {
        with(encryptedPrefs.edit()) {
            remove(KEY_TOKEN)
            remove(KEY_USER_EMAIL)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    // ========== GETTERS PÚBLICOS ==========

    /**
     * 🔑 Obtener token actual
     */
    fun getToken(): String? = encryptedPrefs.getString(KEY_TOKEN, null)

    /**
     * ✅ Verificar si el usuario está logueado
     */
    fun isLoggedIn(): Boolean = encryptedPrefs.getBoolean(KEY_IS_LOGGED_IN, false) && !getToken().isNullOrEmpty()

    /**
     * 📧 Obtener email del usuario actual
     */
    fun getUserEmail(): String? = encryptedPrefs.getString(KEY_USER_EMAIL, null)

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * ⚠️ Parsear mensajes de error
     */
    private fun parseErrorMessage(code: Int, errorBody: String?): String {
        return when {
            errorBody?.contains("Credenciales inválidas") == true -> "Email o contraseña incorrectos"
            errorBody?.contains("invalid_domain") == true -> "Solo se permiten correos institucionales (@tecsup.edu.pe)"
            errorBody?.contains("User not found") == true -> "Usuario no encontrado"
            code == 401 -> "Credenciales inválidas"
            code == 403 -> "Acceso denegado"
            code == 404 -> "Servicio no encontrado"
            code == 500 -> "Error del servidor"
            else -> "Error de conexión (código $code)"
        }
    }

    /**
     * 🏥 Verificar conectividad con el backend
     */
    suspend fun healthCheck(): Boolean = withContext(Dispatchers.IO) {
        NetworkModule.healthCheck()
    }
}