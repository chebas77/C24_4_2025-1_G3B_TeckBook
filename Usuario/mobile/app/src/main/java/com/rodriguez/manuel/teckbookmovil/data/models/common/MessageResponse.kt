package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta simple que contiene solo un mensaje y estado de éxito.
 * Ideal para operaciones que no devuelven datos complejos.
 */
data class MessageResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("details")
    val details: Map<String, Any>? = null
) {
    /**
     * Verifica si la operación fue exitosa.
     */
    fun isSuccessful(): Boolean = success

    /**
     * Devuelve mensaje legible para mostrar en UI.
     */
    fun getDisplayMessage(): String = message
}
