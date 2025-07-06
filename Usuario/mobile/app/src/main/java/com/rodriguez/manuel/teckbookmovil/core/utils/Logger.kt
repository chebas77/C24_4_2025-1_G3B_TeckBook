package com.rodriguez.manuel.teckbookmovil.core.utils

import android.util.Log
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

/**
 * Sistema de logging centralizado para TecBook
 * Proporciona diferentes niveles de log y configuración por entorno
 */
object Logger {

    private const val DEFAULT_TAG = AppConfig.App.LOG_TAG
    private val isLoggingEnabled = AppConfig.App.ENABLE_LOGGING
    private val isDebugMode = AppConfig.getCurrentEnvironment() != AppConfig.Environment.PRODUCTION

    /**
     * Log de debug - Solo en modo desarrollo
     */
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled && isDebugMode) {
            if (throwable != null) {
                Log.d(tag, message, throwable)
            } else {
                Log.d(tag, message)
            }
        }
    }

    /**
     * Log de información
     */
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) {
                Log.i(tag, message, throwable)
            } else {
                Log.i(tag, message)
            }
        }
    }

    /**
     * Log de advertencia
     */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) {
                Log.w(tag, message, throwable)
            } else {
                Log.w(tag, message)
            }
        }
    }

    /**
     * Log de error - Siempre se muestra
     */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    /**
     * Log verbose - Solo en debug
     */
    fun v(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled && isDebugMode) {
            if (throwable != null) {
                Log.v(tag, message, throwable)
            } else {
                Log.v(tag, message)
            }
        }
    }

    /**
     * Log de llamadas a la API
     */
    fun apiCall(tag: String = "API", endpoint: String, method: String = "GET") {
        d(tag, "[$method] Calling: $endpoint")
    }

    /**
     * Log de respuestas de la API
     */
    fun apiResponse(tag: String = "API", endpoint: String, responseCode: Int, message: String? = null) {
        val status = if (responseCode in 200..299) "SUCCESS" else "ERROR"
        val logMessage = "[$status:$responseCode] $endpoint" + if (message != null) " - $message" else ""

        if (responseCode in 200..299) {
            d(tag, logMessage)
        } else {
            w(tag, logMessage)
        }
    }

    /**
     * Log de errores de red
     */
    fun networkError(tag: String = "NETWORK", error: String, throwable: Throwable? = null) {
        e(tag, "Network Error: $error", throwable)
    }

    /**
     * Log de autenticación
     */
    fun auth(tag: String = "AUTH", message: String) {
        i(tag, message)
    }

    /**
     * Log de navegación
     */
    fun navigation(tag: String = "NAV", from: String, to: String) {
        d(tag, "Navigation: $from -> $to")
    }

    /**
     * Log de acciones del usuario
     */
    fun userAction(tag: String = "USER", action: String, details: String? = null) {
        val message = if (details != null) "$action - $details" else action
        i(tag, "User Action: $message")
    }

    /**
     * Log de eventos del ciclo de vida
     */
    fun lifecycle(tag: String = "LIFECYCLE", component: String, event: String) {
        v(tag, "$component: $event")
    }

    /**
     * Log de operaciones de base de datos/storage
     */
    fun storage(tag: String = "STORAGE", operation: String, key: String? = null) {
        val message = if (key != null) "$operation: $key" else operation
        v(tag, message)
    }

    /**
     * Log de métricas de rendimiento
     */
    fun performance(tag: String = "PERF", operation: String, duration: Long) {
        d(tag, "$operation completed in ${duration}ms")
    }

    /**
     * Log de eventos de UI
     */
    fun ui(tag: String = "UI", event: String, component: String? = null) {
        val message = if (component != null) "[$component] $event" else event
        v(tag, message)
    }

    /**
     * Log de eventos de sincronización
     */
    fun sync(tag: String = "SYNC", message: String) {
        i(tag, message)
    }

    /**
     * Log de eventos de cache
     */
    fun cache(tag: String = "CACHE", operation: String, key: String) {
        v(tag, "$operation: $key")
    }

    /**
     * Log de validaciones
     */
    fun validation(tag: String = "VALIDATION", field: String, isValid: Boolean, error: String? = null) {
        val status = if (isValid) "VALID" else "INVALID"
        val message = "[$status] $field" + if (error != null) " - $error" else ""

        if (isValid) {
            v(tag, message)
        } else {
            w(tag, message)
        }
    }

    /**
     * Log para debugging de objetos complejos
     */
    fun obj(tag: String = "OBJECT", objectName: String, obj: Any?) {
        if (isLoggingEnabled && isDebugMode) {
            d(tag, "$objectName: ${obj.toString()}")
        }
    }

    /**
     * Log para timing de operaciones
     */
    fun timing(tag: String = "TIMING", operation: String, block: () -> Unit) {
        if (isLoggingEnabled && isDebugMode) {
            val startTime = System.currentTimeMillis()
            d(tag, "Starting: $operation")

            try {
                block()
                val duration = System.currentTimeMillis() - startTime
                d(tag, "Completed: $operation in ${duration}ms")
            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - startTime
                e(tag, "Failed: $operation after ${duration}ms", e)
                throw e
            }
        } else {
            block()
        }
    }

    /**
     * Log para eventos críticos que siempre deben aparecer
     */
    fun critical(tag: String = "CRITICAL", message: String, throwable: Throwable? = null) {
        // Los eventos críticos siempre se logean, independientemente de la configuración
        if (throwable != null) {
            Log.e(tag, "CRITICAL: $message", throwable)
        } else {
            Log.e(tag, "CRITICAL: $message")
        }
    }

    /**
     * Configura el nivel de logging dinámicamente
     */
    fun setLoggingEnabled(enabled: Boolean) {
        // Esta función podría modificar una variable estática
        // Por ahora, el logging se controla desde AppConfig
    }

    /**
     * Obtiene información del entorno actual para logs
     */
    fun getEnvironmentInfo(): String {
        return "Environment: ${AppConfig.getCurrentEnvironment()}, " +
                "BaseURL: ${AppConfig.BASE_URL}, " +
                "Version: ${AppConfig.App.VERSION}"
    }
}