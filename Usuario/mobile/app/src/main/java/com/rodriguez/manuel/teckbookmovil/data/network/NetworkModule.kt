package com.rodriguez.manuel.teckbookmovil.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 🌐 CONFIGURACIÓN DE RED
 * Centraliza la configuración de Retrofit y OkHttp
 */
object NetworkModule {

    private const val TAG = "NetworkModule"

    // 🔧 CONFIGURACIÓN DE URLs
    private object Config {
        const val BASE_URL_EMULATOR = "http://10.0.2.2:8085/api/"
        const val BASE_URL_DEVICE = "http://192.168.1.100:8085/api/"  // Cambiar por tu IP
        const val BASE_URL_PRODUCTION = "https://tu-servidor.com/api/"

        const val IS_DEBUG = true // Cambiar a false para release
        const val IS_EMULATOR = true // Cambiar a false si usas dispositivo físico
    }

    // 🎯 URL ACTUAL SEGÚN CONFIGURACIÓN
    private val BASE_URL = if (Config.IS_DEBUG) {
        if (Config.IS_EMULATOR) Config.BASE_URL_EMULATOR else Config.BASE_URL_DEVICE
    } else {
        Config.BASE_URL_PRODUCTION
    }

    // 📝 LOGGING para debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (Config.IS_DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    // 🔐 INTERCEPTOR para headers comunes
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // Agregar headers comunes
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        // Log de la petición (solo en debug)
        if (Config.IS_DEBUG) {
            Log.d(TAG, "🌐 API Request: ${request.method} ${request.url}")
        }

        val response = chain.proceed(requestBuilder.build())

        // Log de la respuesta (solo en debug)
        if (Config.IS_DEBUG) {
            Log.d(TAG, "📡 API Response: ${response.code} ${response.message}")
        }

        response
    }

    // 🔐 INTERCEPTOR para autenticación JWT
    class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val token = tokenProvider()

            return if (token != null) {
                val authenticatedRequest = request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(authenticatedRequest)
            } else {
                chain.proceed(request)
            }
        }
    }

    // 🌐 CLIENTE HTTP CONFIGURADO
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // 🏗️ RETROFIT INSTANCE
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 📡 API SERVICE INSTANCE
    val apiService: ApiService = retrofit.create(ApiService::class.java)

    /**
     * 🔐 CREAR CLIENTE AUTENTICADO
     * Para endpoints que requieren JWT token
     */
    fun createAuthenticatedApiService(tokenProvider: () -> String?): ApiService {
        val authenticatedClient = okHttpClient.newBuilder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()

        val authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return authenticatedRetrofit.create(ApiService::class.java)
    }

    /**
     * 🏥 HEALTH CHECK - Verifica conectividad con el backend
     */
    suspend fun healthCheck(): Boolean {
        return try {
            val response = apiService.healthCheckCarreras()
            val isSuccessful = response.isSuccessful

            if (Config.IS_DEBUG) {
                Log.d(TAG, "Health check result: ${if (isSuccessful) "✅ OK" else "❌ Failed"}")
            }

            isSuccessful
        } catch (e: Exception) {
            if (Config.IS_DEBUG) {
                Log.e(TAG, "❌ Health check failed: ${e.message}", e)
            }
            false
        }
    }

    /**
     * 🔧 OBTENER INFORMACIÓN DE CONFIGURACIÓN
     */
    fun getNetworkInfo(): Map<String, String> {
        return mapOf(
            "baseUrl" to BASE_URL,
            "isDebug" to Config.IS_DEBUG.toString(),
            "isEmulator" to Config.IS_EMULATOR.toString(),
            "timeout" to "30s"
        )
    }

    /**
     * 🛠️ CAMBIAR CONFIGURACIÓN EN RUNTIME (para testing)
     */
    fun getUrlForEnvironment(isEmulator: Boolean = true): String {
        return if (Config.IS_DEBUG) {
            if (isEmulator) Config.BASE_URL_EMULATOR else Config.BASE_URL_DEVICE
        } else {
            Config.BASE_URL_PRODUCTION
        }
    }

    /**
     * 📊 INFORMACIÓN DE DEBUG
     */
    fun debugInfo(): String {
        return """
            🌐 NETWORK CONFIGURATION:
            - Base URL: $BASE_URL
            - Is Debug: ${Config.IS_DEBUG}
            - Is Emulator: ${Config.IS_EMULATOR}
            - Timeout: 30s
            - Logging: ${if (Config.IS_DEBUG) "ENABLED" else "DISABLED"}
        """.trimIndent()
    }
}