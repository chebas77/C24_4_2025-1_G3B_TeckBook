package com.rodriguez.manuel.teckbookmovil.core.config

import android.content.Context
import com.rodriguez.manuel.teckbookmovil.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkConfig {

    /**
     * Utilidades comunes de red.
     * Puedes adaptarlas a tus necesidades.
     */
    object NetworkUtils {
        fun isNetworkAvailable(): Boolean {
            // TODO: Implementar verificación real de red
            return true
        }

        fun isSuccessful(code: Int): Boolean = code in 200..299

        fun isRetryableError(code: Int): Boolean = code in listOf(408, 429, 500, 503)
    }

    /**
     * Crea un cliente HTTP con logging, caché opcional y autenticación opcional.
     */
    fun createOkHttpClient(
        context: Context? = null,
        authInterceptor: Interceptor? = null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)

        // Si quieres caché de red
        context?.let {
            val cacheDir = File(it.cacheDir, "http_cache")
            val cacheSize = 10 * 1024 * 1024L // 10 MB
            val cache = Cache(cacheDir, cacheSize)
            builder.cache(cache)
        }

        // Logging solo en DEBUG
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }
        builder.addInterceptor(logging)

        // Interceptor para headers básicos
        builder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", "TecBook-Mobile/${BuildConfig.VERSION_NAME}")
                .header("X-Platform", "Android")
            chain.proceed(requestBuilder.build())
        }

        // Interceptor de autenticación (token JWT)
        authInterceptor?.let { builder.addInterceptor(it) }

        return builder.build()
    }

    /**
     * Crea un Retrofit público sin token.
     */
    fun createPublicRetrofit(context: Context? = null): Retrofit {
        val okHttpClient = createOkHttpClient(context = context)
        return createRetrofit(okHttpClient)
    }

    /**
     * Crea un Retrofit protegido con token.
     */
    fun createAuthenticatedRetrofit(
        context: Context? = null,
        authInterceptor: Interceptor
    ): Retrofit {
        val okHttpClient = createOkHttpClient(context = context, authInterceptor = authInterceptor)
        return createRetrofit(okHttpClient)
    }

    /**
     * Crea una instancia de Retrofit con configuración común.
     */
    private fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL) // ✅ Usa siempre BuildConfig para evitar inconsistencias
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Interceptor que aplica reglas de caché para offline si quieres usarlo.
     */
    fun createCacheInterceptor() = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtils.isNetworkAvailable()) {
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=604800") // 7 días
                .build()
        }
        chain.proceed(request)
    }

    fun createNetworkCacheInterceptor() = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        response.newBuilder()
            .header("Cache-Control", "public, max-age=300") // 5 minutos
            .build()
    }
}
