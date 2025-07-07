package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Información de sesión activa de usuario.
 */
data class SessionInfo(
    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("loginTime")
    val loginTime: Long,

    @SerializedName("lastActivity")
    val lastActivity: Long,

    @SerializedName("expiresAt")
    val expiresAt: Long,

    @SerializedName("deviceInfo")
    val deviceInfo: String? = null
) {
    /** Verifica si la sesión sigue activa y no ha expirado */
    fun isValid(): Boolean {
        return isActive && System.currentTimeMillis() < expiresAt
    }

    /** Tiempo restante en milisegundos */
    fun getTimeToExpire(): Long {
        return maxOf(0, expiresAt - System.currentTimeMillis())
    }

    /** ¿La sesión expira pronto? (< 5 minutos) */
    fun isExpiringSoon(): Boolean {
        return getTimeToExpire() < 5 * 60 * 1000L
    }
}
