package com.rodriguez.manuel.teckbookmovil.data.models.auth
import com.google.gson.annotations.SerializedName

/**
 * Modelo para respuesta de logout
 */
data class LogoutResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("userEmail")
    val userEmail: String?,

    @SerializedName("tokenInvalidated")
    val tokenInvalidated: Boolean,

    @SerializedName("timestamp")
    val timestamp: Long
)