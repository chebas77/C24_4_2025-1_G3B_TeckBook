package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
 * Error de validación con detalles
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
     * Obtiene errores por campo
     */
    fun getFieldErrors(): Map<String, String> {
        return details
    }

    /**
     * Verifica si hay error en un campo específico
     */
    fun hasFieldError(fieldName: String): Boolean {
        return details.containsKey(fieldName)
    }

    /**
     * Obtiene error de un campo específico
     */
    fun getFieldError(fieldName: String): String? {
        return details[fieldName]
    }
}