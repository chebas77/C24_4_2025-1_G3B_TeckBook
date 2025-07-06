package com.rodriguez.manuel.teckbookmovil.data.models.auth
import com.google.gson.annotations.SerializedName

/**
 * Información básica de carrera en respuestas
 */
data class CarreraInfo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("codigo")
    val codigo: String
)