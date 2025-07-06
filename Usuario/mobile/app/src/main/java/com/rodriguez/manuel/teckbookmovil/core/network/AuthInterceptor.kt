package com.rodriguez.manuel.teckbookmovil.core.network

import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor que agrega automáticamente el token JWT a las peticiones
 * que requieren autenticación
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url.toString()

        // Verificar si el endpoint requiere autenticación
        if (!requiresAuthentication(originalUrl)) {
            Logger.d("AuthInterceptor", "Endpoint público, sin token: $originalUrl")
            return chain.proceed(original)
        }

        // Obtener token
        val token = tokenManager.getBearerToken()

        if (token.isNullOrEmpty()) {
            Logger.w("AuthInterceptor", "No hay token disponible para endpoint protegido: $originalUrl")
            // Continuar sin token - el servidor debería responder 401
            return chain.proceed(original)
        }

        // Agregar token a la petición
        val requestBuilder = original.newBuilder()
            .header(AppConfig.Auth.HEADER_AUTHORIZATION, token)
            .method(original.method, original.body)

        val request = requestBuilder.build()
        Logger.d("AuthInterceptor", "Token agregado a petición: ${originalUrl}")

        try {
            val response = chain.proceed(request)

            // Verificar si el token ha expirado
            if (response.code == 401) {
                Logger.w("AuthInterceptor", "Token expirado o inválido (401) para: $originalUrl")
                handleTokenExpiration()
            }

            return response

        } catch (e: IOException) {
            Logger.e("AuthInterceptor", "Error en petición autenticada: $originalUrl", e)
            throw e
        }
    }

    /**
     * Determina si un endpoint requiere autenticación basado en la URL
     */
    private fun requiresAuthentication(url: String): Boolean {
        val publicEndpoints = listOf(
            AppConfig.Endpoints.LOGIN,
            AppConfig.Endpoints.GOOGLE_LOGIN,
            AppConfig.Endpoints.USUARIO_REGISTER,
            AppConfig.Endpoints.CARRERAS_ACTIVAS,
            AppConfig.Endpoints.DEPARTAMENTOS_ACTIVOS,
            AppConfig.Endpoints.CICLOS_TODOS,
            AppConfig.Endpoints.HEALTH_CARRERAS,
            AppConfig.Endpoints.HEALTH_AULAS,
            AppConfig.Endpoints.ANUNCIOS_GENERAL,
            AppConfig.Endpoints.ANUNCIOS_TODOS,
            "/oauth2/",
            "/api/debug/",
            "/api/public/"
        )

        // También verificar endpoints dinámicos públicos
        val publicPatterns = listOf(
            "/api/carreras/departamento/\\d+/activas",
            "/api/secciones/carrera/\\d+",
            "/api/secciones/carrera/\\d+/ciclo/\\d+"
        )

        // Verificar endpoints exactos
        for (endpoint in publicEndpoints) {
            if (url.contains(endpoint)) {
                return false
            }
        }

        // Verificar patrones dinámicos
        for (pattern in publicPatterns) {
            if (url.matches(".*$pattern.*".toRegex())) {
                return false
            }
        }

        // Por defecto, requiere autenticación
        return true
    }

    /**
     * Maneja la expiración del token
     */
    private fun handleTokenExpiration() {
        Logger.auth("AUTH", "Token expirado, limpiando sesión local")

        // Limpiar token local - esto forzará al usuario a hacer login nuevamente
        tokenManager.clearToken()

        // Aquí podrías emitir un evento para notificar a la UI que el token expiró
        // Por ejemplo, usando EventBus, LiveData, o un callback
        notifyTokenExpired()
    }

    /**
     * Notifica que el token ha expirado
     * Esta función puede ser extendida para enviar eventos a la UI
     */
    private fun notifyTokenExpired() {
        // TODO: Implementar notificación a la UI cuando se integre con Activities/ViewModels
        // Opciones:
        // 1. EventBus.getDefault().post(TokenExpiredEvent())
        // 2. Broadcast Intent
        // 3. Callback interface
        Logger.w("AuthInterceptor", "Token expirado - Usuario debe hacer login nuevamente")
    }

    /**
     * Interface para manejar eventos de token expirado
     */
    interface TokenExpirationListener {
        fun onTokenExpired()
    }

    companion object {
        private var tokenExpirationListener: TokenExpirationListener? = null

        /**
         * Establece un listener para eventos de token expirado
         */
        fun setTokenExpirationListener(listener: TokenExpirationListener?) {
            tokenExpirationListener = listener
        }

        /**
         * Notifica token expirado a través del listener
         */
        private fun notifyTokenExpiredToListener() {
            tokenExpirationListener?.onTokenExpired()
        }
    }
}