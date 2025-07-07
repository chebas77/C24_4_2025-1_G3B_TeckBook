package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName

/**
 * Modelo para registro de usuario
 * Basado en el endpoint /api/usuarios/register
 */
data class RegisterRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("correoInstitucional")
    val correoInstitucional: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("rol")
    val rol: String = "estudiante",

    @SerializedName("cicloActual")
    val cicloActual: Int,

    @SerializedName("departamentoId")
    val departamentoId: Long,

    @SerializedName("carreraId")
    val carreraId: Long,

    @SerializedName("seccionId")
    val seccionId: Long? = null,

    @SerializedName("telefono")
    val telefono: String? = null
)
