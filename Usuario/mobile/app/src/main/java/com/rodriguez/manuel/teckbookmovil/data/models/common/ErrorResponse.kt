package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de error estructurada
 * Compatible con tus controladores REST y tu flujo AuthInterceptor.
 */
data class ErrorResponse(
    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("requiresLogin")
    val requiresLogin: Boolean = false,

    @SerializedName("details")
    val details: Map<String, String>? = null,

    @SerializedName("code")
    val code: Int? = null
) {
    /**
     * Devuelve el mensaje de error más descriptivo.
     */
    fun getErrorMessage(): String {
        return message ?: error
    }

    /**
     * Indica si se debe forzar reautenticación.
     */
    fun requiresReauth(): Boolean {
        return requiresLogin || code == 401
    }
}
