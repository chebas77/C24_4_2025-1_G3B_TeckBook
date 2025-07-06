package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
* Respuesta de health check
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
     * Verifica si el servicio está funcionando
     */
    fun isHealthy(): Boolean {
        return status.equals("OK", ignoreCase = true)
    }
}