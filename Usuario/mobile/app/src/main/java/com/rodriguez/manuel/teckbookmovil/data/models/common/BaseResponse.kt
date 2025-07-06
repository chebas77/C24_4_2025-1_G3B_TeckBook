package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

// ========== RESPUESTAS BASE ==========

/**
 * Respuesta base para todos los endpoints
 */
data class BaseResponse<T>(
    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Verifica si la respuesta es exitosa
     */
    fun isSuccess(): Boolean {
        return success && error == null && data != null
    }

    /**
     * Verifica si hay error
     */
    fun hasError(): Boolean {
        return !success || error != null
    }

    /**
     * Obtiene el mensaje de error o success
     */
    fun getDisplayMessage(): String? {
        return error ?: message
    }
}
