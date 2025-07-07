package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName

/**
 * Modelo para respuesta de registro exitoso
 */
data class RegisterResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("usuario")
    val usuario: UserInfo,

    @SerializedName("carrera")
    val carrera: CarreraInfo? = null
)
