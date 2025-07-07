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

        if (!requiresAuthentication(originalUrl)) {
            Logger.d("AuthInterceptor", "📢 Endpoint público, sin token: $originalUrl")
            return chain.proceed(original)
        }

        val token = tokenManager.getBearerToken()

        if (token.isNullOrEmpty()) {
            Logger.w("AuthInterceptor", "❌ No hay token para endpoint protegido: $originalUrl")
            return chain.proceed(original)
        }

        val request = original.newBuilder()
            .header(AppConfig.Auth.HEADER_AUTHORIZATION, token)
            .method(original.method, original.body)
            .build()

        Logger.d("AuthInterceptor", "✅ Token agregado: $originalUrl")

        val response = chain.proceed(request)

        if (response.code == 401) {
            Logger.w("AuthInterceptor", "⚠️ Token expirado o inválido (401) para: $originalUrl")
            handleTokenExpiration()
        }

        return response
    }

    /**
     * Determina si un endpoint requiere autenticación
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
            "/oauth2/"
        )

        val publicPatterns = listOf(
            "/api/carreras/departamento/\\d+/activas",
            "/api/secciones/carrera/\\d+",
            "/api/secciones/carrera/\\d+/ciclo/\\d+"
        )

        if (publicEndpoints.any { url.contains(it) }) return false
        if (publicPatterns.any { url.matches(".*$it.*".toRegex()) }) return false

        return true // Todo lo demás requiere auth
    }

    /**
     * Maneja la expiración del token
     */
    private fun handleTokenExpiration() {
        Logger.auth("🚫 Token expirado - limpiando sesión local")
        tokenManager.logout()
    }
}
