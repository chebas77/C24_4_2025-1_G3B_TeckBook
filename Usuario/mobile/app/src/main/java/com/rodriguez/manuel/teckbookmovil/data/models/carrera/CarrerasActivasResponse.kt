package com.rodriguez.manuel.teckbookmovil.data.models.carrera
import com.google.gson.annotations.SerializedName

// ========== RESPUESTAS DE API ==========

/**
 * Respuesta para carreras activas
 */
data class CarrerasActivasResponse(
    @SerializedName("carreras")
    val carreras: List<Carrera>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("isEmpty")
    val isEmpty: Boolean = false
)