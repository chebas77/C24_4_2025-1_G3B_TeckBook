package com.rodriguez.manuel.teckbookmovil.data.models.auth
import com.google.gson.annotations.SerializedName

/**
 * Modelo para respuesta de Google OAuth login
 */
data class GoogleLoginResponse(
    @SerializedName("redirectUrl")
    val redirectUrl: String,

    @SerializedName("message")
    val message: String
)