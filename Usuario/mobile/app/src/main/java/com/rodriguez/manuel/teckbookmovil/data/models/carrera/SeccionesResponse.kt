package com.rodriguez.manuel.teckbookmovil.data.models.carrera
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para secciones por carrera y ciclo
 */
data class SeccionesResponse(
    @SerializedName("secciones")
    val secciones: List<Seccion>,

    @SerializedName("carreraId")
    val carreraId: Long? = null,

    @SerializedName("cicloId")
    val cicloId: Long? = null,

    @SerializedName("count")
    val count: Int,

    @SerializedName("message")
    val message: String
)