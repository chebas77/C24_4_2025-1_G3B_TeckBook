package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para búsqueda de aulas
 */
data class BuscarAulasResponse(
    @SerializedName("aulas")
    val aulas: List<AulaVirtual>,

    @SerializedName("totalResultados")
    val totalResultados: Int,

    @SerializedName("terminoBusqueda")
    val terminoBusqueda: String,

    @SerializedName("message")
    val message: String
)