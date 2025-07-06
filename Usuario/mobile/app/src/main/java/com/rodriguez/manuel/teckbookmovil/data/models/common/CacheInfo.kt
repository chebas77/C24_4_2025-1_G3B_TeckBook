package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName
/**
 * Información de cache
 */
data class CacheInfo(
    @SerializedName("key")
    val key: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("expiresAt")
    val expiresAt: Long,

    @SerializedName("size")
    val size: Long? = null
) {
    /**
     * Verifica si el cache ha expirado
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    /**
     * Obtiene tiempo restante en milisegundos
     */
    fun getTimeToLive(): Long {
        return maxOf(0, expiresAt - System.currentTimeMillis())
    }

    /**
     * Verifica si es válido
     */
    fun isValid(): Boolean {
        return !isExpired()
    }
}