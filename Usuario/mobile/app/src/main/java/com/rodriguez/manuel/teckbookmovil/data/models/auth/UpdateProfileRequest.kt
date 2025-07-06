package com.rodriguez.manuel.teckbookmovil.data.models.auth
import com.google.gson.annotations.SerializedName

/**
 * Modelo para actualización de perfil de usuario
 */
data class UpdateProfileRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("cicloActual")
    val cicloActual: Int?,

    @SerializedName("carreraId")
    val carreraId: Long?,

    @SerializedName("seccionId")
    val seccionId: Long?,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("password")
    val password: String? = null
)