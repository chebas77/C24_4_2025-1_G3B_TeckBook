package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de error de validación con detalles por campo.
 */
data class ValidationErrorResponse(
    @SerializedName("error")
    val error: String,

    @SerializedName("details")
    val details: Map<String, String>,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Obtiene todos los errores por campo.
     */
    fun getFieldErrors(): Map<String, String> {
        return details
    }

    /**
     * Verifica si existe error para un campo específico.
     */
    fun hasFieldError(fieldName: String): Boolean {
        return details.containsKey(fieldName)
    }

    /**
     * Obtiene el error de un campo específico.
     */
    fun getFieldError(fieldName: String): String? {
        return details[fieldName]
    }
}
