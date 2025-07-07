package com.rodriguez.manuel.teckbookmovil.core.utils

import android.util.Log
import com.rodriguez.manuel.teckbookmovil.BuildConfig

/**
 * Logger centralizado para TecBook
 * Compatible con el flujo actual OAuth2 + token
 */
object Logger {

    private const val DEFAULT_TAG = "TecBook"
    private val isLoggingEnabled = BuildConfig.DEBUG

    /** ========== DEBUG ========== */
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
        }
    }

    /** ========== INFO ========== */
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Log.i(tag, message, throwable) else Log.i(tag, message)
        }
    }

    /** ========== WARNING ========== */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
        }
    }

    /** ========== ERROR (siempre) ========== */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
    }

    /** ========== VERBOSE ========== */
    fun v(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Log.v(tag, message, throwable) else Log.v(tag, message)
        }
    }

    /** ========== API / NETWORK HELPERS ========== */
    fun apiCall(endpoint: String, method: String = "GET") {
        d("API", "[$method] Calling: $endpoint")
    }

    fun apiResponse(endpoint: String, responseCode: Int, message: String? = null) {
        val status = if (responseCode in 200..299) "SUCCESS" else "ERROR"
        val logMessage = "[$status:$responseCode] $endpoint" + if (!message.isNullOrEmpty()) " - $message" else ""
        if (responseCode in 200..299) d("API", logMessage) else w("API", logMessage)
    }

    fun auth(message: String) = i("AUTH", message)
    fun networkError(error: String, throwable: Throwable? = null) = e("NETWORK", "Error: $error", throwable)
    fun userAction(action: String, details: String? = null) =
        i("USER", if (details != null) "$action - $details" else action)
    fun lifecycle(component: String, event: String) = v("LIFECYCLE", "$component: $event")

    /** ========== CRITICAL ========== */
    fun critical(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e("CRITICAL", message, throwable) else Log.e("CRITICAL", message)
    }
}
