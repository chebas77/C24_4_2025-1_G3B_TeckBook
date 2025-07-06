package com.rodriguez.manuel.teckbookmovil.core.config

import com.rodriguez.manuel.teckbookmovil.core.config.NetworkConfig.createOkHttpClient
import com.rodriguez.manuel.teckbookmovil.core.config.NetworkConfig.createRetrofit
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit



/**
 * Configuración centralizada para todos los componentes de red
 * Aquí se configuran Retrofit, OkHttp, interceptors, etc.
 */
object NetworkConfig {

    /**
     * Crea y configura el cliente OkHttp
     */
    fun createOkHttpClient(
        cacheDir: File? = null,
        authInterceptor: Interceptor? = null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(AppConfig.Network.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConfig.Network.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.Network.WRITE_TIMEOUT, TimeUnit.SECONDS)

        // ========== CACHE ==========
        cacheDir?.let { dir ->
            val cacheSize = AppConfig.App.CACHE_SIZE
            val cache = Cache(dir, cacheSize)
            builder.cache(cache)
        }

        // ========== LOGGING INTERCEPTOR ==========
        if (AppConfig.App.ENABLE_LOGGING) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (AppConfig.getCurrentEnvironment() == AppConfig.Environment.PRODUCTION) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.BODY
                }
            }
            builder.addInterceptor(loggingInterceptor)
        }

        // ========== HEADERS COMUNES ==========
        builder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", AppConfig.Auth.CONTENT_TYPE_JSON)
                .header("User-Agent", "TecBook-Mobile/${AppConfig.App.VERSION}")
                .header("X-Platform", "Android")

            chain.proceed(requestBuilder.build())
        }

        // ========== AUTH INTERCEPTOR ==========
        authInterceptor?.let { interceptor ->
            builder.addInterceptor(interceptor)
        }

        // ========== CACHE INTERCEPTOR ==========
        builder.addInterceptor(createCacheInterceptor())

        // ========== NETWORK INTERCEPTOR (para cache) ==========
        builder.addNetworkInterceptor(createNetworkCacheInterceptor())

        return builder.build()
    }

    /**
     * Crea y configura Retrofit
     */
    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }
}

/**
 * Utilidades para verificación de red
 */
object NetworkUtils {

    /**
     * Verifica si hay conexión a internet disponible
     * Nota: Esta función debe ser implementada con el contexto de Android
     */
    fun isNetworkAvailable(): Boolean {
        // Esta implementación será completada cuando creemos la clase con Context
        // Por ahora retornamos true para evitar errores de compilación
        return true
    }

    /**
     * Determina si una respuesta HTTP indica éxito
     */
    fun isSuccessful(code: Int): Boolean {
        return code in 200..299
    }

    /**
     * Determina si un error es recuperable
     */
    fun isRetryableError(code: Int): Boolean {
        return when (code) {
            Constants.HttpCodes.INTERNAL_SERVER_ERROR,
            Constants.HttpCodes.SERVICE_UNAVAILABLE,
            408, // Request Timeout
            429  // Too Many Requests
                -> true

            else -> false
        }
    }

    /**
     * Obtiene mensaje de error amigable basado en código HTTP
     */
    fun getErrorMessage(code: Int): String {
        return when (code) {
            Constants.HttpCodes.BAD_REQUEST -> Constants.ErrorMessages.VALIDATION_ERROR
            Constants.HttpCodes.UNAUTHORIZED -> Constants.ErrorMessages.UNAUTHORIZED_ERROR
            Constants.HttpCodes.FORBIDDEN -> Constants.ErrorMessages.FORBIDDEN_ERROR
            Constants.HttpCodes.NOT_FOUND -> Constants.ErrorMessages.NOT_FOUND_ERROR
            Constants.HttpCodes.INTERNAL_SERVER_ERROR -> Constants.ErrorMessages.SERVER_ERROR
            Constants.HttpCodes.SERVICE_UNAVAILABLE -> Constants.ErrorMessages.SERVER_ERROR
            else -> Constants.ErrorMessages.UNKNOWN_ERROR
        }
    }
}


/**
 * Configuración personalizada de Gson
 */
private fun createGson() = com.google.gson.GsonBuilder()
    .setDateFormat(Constants.Format.SERVER_DATETIME_PATTERN)
    .setLenient()
    .create()

/**
 * Interceptor para manejo de cache offline
 */
private fun createCacheInterceptor() = Interceptor { chain ->
    var request = chain.request()

    // Si no hay internet, usar cache
    if (!NetworkUtils.isNetworkAvailable()) {
        request = request.newBuilder()
            .header("Cache-Control", "public, only-if-cached, max-stale=${AppConfig.App.CACHE_MAX_STALE}")
            .build()
    }

    chain.proceed(request)
}

/**
 * Network interceptor para configurar cache
 */
private fun createNetworkCacheInterceptor() = Interceptor { chain ->
    val response = chain.proceed(chain.request())

    response.newBuilder()
        .header("Cache-Control", "public, max-age=${AppConfig.App.CACHE_MAX_AGE}")
        .build()
}

/**
 * Configuración específica para endpoints públicos (sin auth)
 */
fun createPublicRetrofit(cacheDir: File? = null): Retrofit {
    val okHttpClient = createOkHttpClient(cacheDir = cacheDir)
    return createRetrofit(okHttpClient)
}

/**
 * Configuración específica para endpoints protegidos (con auth)
 */
fun createAuthenticatedRetrofit(
    cacheDir: File? = null,
    authInterceptor: Interceptor
): Retrofit {
    val okHttpClient = createOkHttpClient(
        cacheDir = cacheDir,
        authInterceptor = authInterceptor
    )
    return createRetrofit(okHttpClient)
}

/**
 * Configuración para uploads de archivos
 */
fun createUploadRetrofit(authInterceptor: Interceptor): Retrofit.Builder {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Más tiempo para uploads
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .build()

    return Retrofit.Builder()
}