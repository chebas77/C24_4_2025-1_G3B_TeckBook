package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para carreras activas
 */
data class CarrerasActivasResponse(
    @SerializedName("carreras")
    val carreras: List<Carrera>,

    @SerializedName("count")
    val count: Int = carreras.size,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("isEmpty")
    val isEmpty: Boolean = carreras.isEmpty()
)
