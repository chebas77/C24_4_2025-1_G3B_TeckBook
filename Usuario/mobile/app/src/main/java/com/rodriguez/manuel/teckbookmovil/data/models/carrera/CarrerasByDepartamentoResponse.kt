package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para carreras por departamento
 */
data class CarrerasByDepartamentoResponse(
    @SerializedName("carreras")
    val carreras: List<Carrera>,

    @SerializedName("departamentoId")
    val departamentoId: Long,

    @SerializedName("count")
    val count: Int = carreras.size,

    @SerializedName("message")
    val message: String? = null
)
