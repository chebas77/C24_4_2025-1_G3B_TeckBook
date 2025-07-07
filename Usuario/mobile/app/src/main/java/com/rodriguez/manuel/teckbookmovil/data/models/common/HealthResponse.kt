package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de health check del backend.
 * Compatible con endpoints tipo /health o /actuator/health.
 */
data class HealthResponse(
    @SerializedName("service")
    val service: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("endpoints")
    val endpoints: Map<String, String>? = null,

    @SerializedName("carrerasActivas")
    val carrerasActivas: Long? = null,

    @SerializedName("totalAulas")
    val totalAulas: Long? = null
) {
    /**
     * Verifica si el servicio está en estado OK.
     */
    fun isHealthy(): Boolean {
        return status.equals("OK", ignoreCase = true)
    }

    /**
     * Devuelve estado legible para UI.
     */
    fun getStatusDisplay(): String {
        return if (isHealthy()) "Disponible" else "No disponible"
    }
}
