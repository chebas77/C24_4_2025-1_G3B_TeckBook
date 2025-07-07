package com.rodriguez.manuel.teckbookmovil.core.config

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkConfig {
    object NetworkUtils {
        fun isNetworkAvailable(): Boolean {
            // Aquí puedes implementar la verificación real
            // o devolver true como placeholder:
            return true
        }

        fun isSuccessful(code: Int): Boolean {
            return code in 200..299
        }

        fun isRetryableError(code: Int): Boolean {
            return when (code) {
                Constants.HttpCodes.INTERNAL_SERVER_ERROR,
                Constants.HttpCodes.SERVICE_UNAVAILABLE,
                408, 429 -> true
                else -> false
            }
        }

        fun getErrorMessage(code: Int): String {
            return when (code) {
                Constants.HttpCodes.BAD_REQUEST -> Constants.ErrorMessages.VALIDATION_ERROR
                Constants.HttpCodes.UNAUTHORIZED -> Constants.ErrorMessages.UNAUTHORIZED_ERROR
                Constants.HttpCodes.FORBIDDEN -> Constants.ErrorMessages.FORBIDDEN_ERROR
                Constants.HttpCodes.NOT_FOUND -> Constants.ErrorMessages.NOT_FOUND_ERROR
                Constants.HttpCodes.INTERNAL_SERVER_ERROR,
                Constants.HttpCodes.SERVICE_UNAVAILABLE -> Constants.ErrorMessages.SERVER_ERROR
                else -> Constants.ErrorMessages.UNKNOWN_ERROR
            }
        }
    }

    fun createOkHttpClient(
        cacheDir: File? = null,
        authInterceptor: Interceptor? = null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(AppConfig.Network.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConfig.Network.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.Network.WRITE_TIMEOUT, TimeUnit.SECONDS)

        cacheDir?.let { dir ->
            val cacheSize = AppConfig.App.CACHE_SIZE
            val cache = Cache(dir, cacheSize)
            builder.cache(cache)
        }

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

        builder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", AppConfig.Auth.CONTENT_TYPE_JSON)
                .header("User-Agent", "TecBook-Mobile/${AppConfig.App.VERSION}")
                .header("X-Platform", "Android")

            chain.proceed(requestBuilder.build())
        }

        authInterceptor?.let { builder.addInterceptor(it) }

        builder.addInterceptor(createCacheInterceptor())
        builder.addNetworkInterceptor(createNetworkCacheInterceptor())

        return builder.build()
    }

    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }

    fun createPublicRetrofit(cacheDir: File? = null): Retrofit {
        val okHttpClient = createOkHttpClient(cacheDir = cacheDir)
        return createRetrofit(okHttpClient)
    }

    fun createAuthenticatedRetrofit(
        cacheDir: File? = null,
        authInterceptor: Interceptor
    ): Retrofit {
        val okHttpClient = createOkHttpClient(cacheDir = cacheDir, authInterceptor = authInterceptor)
        return createRetrofit(okHttpClient)
    }

    fun createUploadRetrofit(authInterceptor: Interceptor): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }

    private fun createCacheInterceptor() = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtils.isNetworkAvailable()) {
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=${AppConfig.App.CACHE_MAX_STALE}")
                .build()
        }
        chain.proceed(request)
    }

    private fun createNetworkCacheInterceptor() = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        response.newBuilder()
            .header("Cache-Control", "public, max-age=${AppConfig.App.CACHE_MAX_AGE}")
            .build()
    }

    private fun createGson() = com.google.gson.GsonBuilder()
        .setDateFormat(Constants.Format.SERVER_DATETIME_PATTERN)
        .setLenient()
        .create()
}
