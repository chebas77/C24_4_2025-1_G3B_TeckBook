package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName

/**
 * Modelo para respuesta de login exitoso
 * Basado en el AuthController del backend
 */
data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("type")
    val type: String = "Bearer",

    @SerializedName("requiresCompletion")
    val requiresCompletion: Boolean = false,

    @SerializedName("redirectTo")
    val redirectTo: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("user")
    val user: UserInfo? = null
)
