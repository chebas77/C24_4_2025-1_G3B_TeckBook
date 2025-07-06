package com.rodriguez.manuel.teckbookmovil

import android.app.Application
import android.content.Context
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import com.rodriguez.manuel.teckbookmovil.core.network.AuthInterceptor
import com.rodriguez.manuel.teckbookmovil.core.config.NetworkConfig
import com.rodriguez.manuel.teckbookmovil.core.storage.PreferencesManager
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import retrofit2.Retrofit
import java.io.File


/**
 * Clase Application principal de TecBook
 * Inicializa componentes globales y configuración de la aplicación
 */
class TecBookApplication : Application() {

    // ========== COMPONENTES GLOBALES ==========

    lateinit var tokenManager: TokenManager
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var authInterceptor: AuthInterceptor
        private set

    lateinit var publicRetrofit: Retrofit
        private set

    lateinit var authenticatedRetrofit: Retrofit
        private set

    // ========== CICLO DE VIDA ==========

    override fun onCreate() {
        super.onCreate()

        Logger.i("TecBookApp", "Iniciando aplicación TecBook v${AppConfig.App.VERSION}")
        Logger.i("TecBookApp", "Entorno: ${AppConfig.getCurrentEnvironment()}")
        Logger.i("TecBookApp", "Base URL: ${AppConfig.BASE_URL}")

        // Inicializar componentes
        initializeComponents()

        // Configurar aplicación
        setupApplication()

        Logger.i("TecBookApp", "Aplicación inicializada correctamente")
    }

    override fun onTerminate() {
        super.onTerminate()
        Logger.i("TecBookApp", "Terminando aplicación TecBook")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Logger.w("TecBookApp", "Memoria baja detectada")
        // Aquí podrías limpiar caches, imágenes, etc.
        clearMemoryCache()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logger.w("TecBookApp", "Trim memory llamado con nivel: $level")

        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {
                // La UI está oculta, podemos liberar recursos de UI
                Logger.d("TecBookApp", "UI oculta, liberando recursos de UI")
            }
            TRIM_MEMORY_RUNNING_MODERATE,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // La app está corriendo pero el sistema necesita memoria
                Logger.d("TecBookApp", "Sistema necesita memoria, liberando caches")
                clearMemoryCache()
            }
            TRIM_MEMORY_BACKGROUND,
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE -> {
                // La app está en background
                Logger.d("TecBookApp", "App en background, liberando todos los recursos posibles")
                clearMemoryCache()
            }
        }
    }

    // ========== INICIALIZACIÓN ==========

    private fun initializeComponents() {
        Logger.d("TecBookApp", "Inicializando componentes principales")

        // Inicializar gestores de almacenamiento
        preferencesManager = PreferencesManager(this)
        tokenManager = TokenManager(this)

        // Inicializar interceptor de autenticación
        authInterceptor = AuthInterceptor(tokenManager)

        // Inicializar clientes de red
        initializeNetworkComponents()

        // Configurar listeners
        setupTokenExpirationListener()

        Logger.d("TecBookApp", "Componentes inicializados")
    }

    private fun initializeNetworkComponents() {
        Logger.d("TecBookApp", "Inicializando componentes de red")

        // Directorio de cache
        val cacheDir = File(cacheDir, "http_cache")

        // Retrofit para endpoints públicos
        publicRetrofit = NetworkConfig.createPublicRetrofit(cacheDir)

        // Retrofit para endpoints autenticados
        authenticatedRetrofit = NetworkConfig.createAuthenticatedRetrofit(
            cacheDir = cacheDir,
            authInterceptor = authInterceptor
        )

        Logger.d("TecBookApp", "Componentes de red configurados")
    }

    private fun setupApplication() {
        Logger.d("TecBookApp", "Configurando aplicación")

        // Verificar primera ejecución
        if (preferencesManager.isFirstLaunch()) {
            Logger.i("TecBookApp", "Primera ejecución de la aplicación")
            handleFirstLaunch()
        }

        // Verificar actualización de versión
        checkAppUpdate()

        // Configurar modo debug
        if (AppConfig.getCurrentEnvironment() != AppConfig.Environment.PRODUCTION) {
            setupDebugMode()
        }

        Logger.d("TecBookApp", "Aplicación configurada")
    }

    private fun setupTokenExpirationListener() {
        AuthInterceptor.setTokenExpirationListener(object : AuthInterceptor.TokenExpirationListener {
            override fun onTokenExpired() {
                Logger.auth("TecBookApp", "Token expirado detectado")
                handleTokenExpiration()
            }
        })
    }

    // ========== MANEJO DE EVENTOS ==========

    private fun handleFirstLaunch() {
        Logger.i("TecBookApp", "Configurando primera ejecución")

        // Configurar valores por defecto
        preferencesManager.apply {
            saveThemeMode(AppConfig.Theme.DEFAULT_MODE)
            saveNotificationsEnabled(true)
            saveAppVersion(AppConfig.App.VERSION)
            markFirstLaunchCompleted()
        }

        Logger.i("TecBookApp", "Primera ejecución configurada")
    }

    private fun checkAppUpdate() {
        val savedVersion = preferencesManager.getAppVersion()
        val currentVersion = AppConfig.App.VERSION

        if (savedVersion != currentVersion) {
            Logger.i("TecBookApp", "Actualización detectada: $savedVersion -> $currentVersion")
            handleAppUpdate(savedVersion, currentVersion)
            preferencesManager.saveAppVersion(currentVersion)
        }
    }

    private fun handleAppUpdate(oldVersion: String?, newVersion: String) {
        Logger.i("TecBookApp", "Manejando actualización de app")

        // Aquí puedes manejar migraciones de datos, limpiar caches, etc.
        // Por ejemplo:
        // - Limpiar cache de imágenes obsoleto
        // - Migrar preferencias
        // - Actualizar esquemas de base de datos local

        Logger.i("TecBookApp", "Actualización procesada correctamente")
    }

    private fun handleTokenExpiration() {
        Logger.auth("TecBookApp", "Manejando expiración de token globalmente")

        // Limpiar toda la información de sesión
        tokenManager.logout()

        // Aquí podrías:
        // 1. Enviar broadcast para cerrar actividades
        // 2. Redirigir a login
        // 3. Mostrar notificación de sesión expirada

        // Por ahora solo logueamos
        Logger.auth("TecBookApp", "Token limpiado, usuario debe hacer login nuevamente")
    }

    private fun setupDebugMode() {
        Logger.d("TecBookApp", "Configurando modo debug")

        // Configuraciones específicas para debug
        if (AppConfig.Debug.LOG_NETWORK_REQUESTS) {
            Logger.d("TecBookApp", "Logging de red habilitado")
        }

        if (AppConfig.Debug.SHOW_DEBUG_INFO) {
            Logger.d("TecBookApp", "Información de debug habilitada")
        }

        Logger.d("TecBookApp", "Modo debug configurado")
    }

    // ========== GESTIÓN DE MEMORIA ==========

    private fun clearMemoryCache() {
        Logger.d("TecBookApp", "Liberando cache de memoria")

        try {
            // Limpiar cache de Glide
            com.bumptech.glide.Glide.get(this).clearMemory()

            // Limpiar cache de OkHttp
            publicRetrofit.callFactory().let { client ->
                if (client is okhttp3.OkHttpClient) {
                    client.cache?.evictAll()
                }
            }

            authenticatedRetrofit.callFactory().let { client ->
                if (client is okhttp3.OkHttpClient) {
                    client.cache?.evictAll()
                }
            }

            // Forzar garbage collection
            System.gc()

            Logger.d("TecBookApp", "Cache de memoria liberado")

        } catch (e: Exception) {
            Logger.e("TecBookApp", "Error liberando cache de memoria", e)
        }
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Obtiene información del estado de la aplicación
     */
    fun getAppInfo(): AppInfo {
        return AppInfo(
            version = AppConfig.App.VERSION,
            environment = AppConfig.getCurrentEnvironment(),
            baseUrl = AppConfig.BASE_URL,
            isLoggedIn = tokenManager.isLoggedIn(),
            userEmail = tokenManager.getUserEmail(),
            userRole = tokenManager.getUserRole(),
            lastSyncTime = preferencesManager.getLastSyncTime()
        )
    }

    /**
     * Realiza logout completo de la aplicación
     */
    fun performGlobalLogout() {
        Logger.auth("TecBookApp", "Realizando logout global")

        // Limpiar token y datos de usuario
        tokenManager.logout()

        // Limpiar búsquedas recientes
        preferencesManager.clearRecentSearches()

        // Limpiar cache
        clearMemoryCache()

        Logger.auth("TecBookApp", "Logout global completado")
    }

    /**
     * Data class para información de la aplicación
     */
    data class AppInfo(
        val version: String,
        val environment: AppConfig.Environment,
        val baseUrl: String,
        val isLoggedIn: Boolean,
        val userEmail: String?,
        val userRole: AppConfig.UserRole,
        val lastSyncTime: Long
    )

    // ========== COMPANION OBJECT ==========

    companion object {

        /**
         * Obtiene la instancia de TecBookApplication desde cualquier contexto
         */
        fun getInstance(context: Context): TecBookApplication {
            return context.applicationContext as TecBookApplication
        }

        /**
         * Acceso rápido a componentes desde Activities/Fragments
         */
        fun getTokenManager(context: Context): TokenManager {
            return getInstance(context).tokenManager
        }

        fun getPreferencesManager(context: Context): PreferencesManager {
            return getInstance(context).preferencesManager
        }

        fun getPublicRetrofit(context: Context): Retrofit {
            return getInstance(context).publicRetrofit
        }

        fun getAuthenticatedRetrofit(context: Context): Retrofit {
            return getInstance(context).authenticatedRetrofit
        }
    }
}

