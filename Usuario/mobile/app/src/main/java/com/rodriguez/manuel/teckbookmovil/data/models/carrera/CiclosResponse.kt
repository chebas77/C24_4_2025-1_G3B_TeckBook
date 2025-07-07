package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para todos los ciclos.
 */
data class CiclosResponse(
    @SerializedName("ciclos")
    val ciclos: List<Ciclo>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("message")
    val message: String
)
