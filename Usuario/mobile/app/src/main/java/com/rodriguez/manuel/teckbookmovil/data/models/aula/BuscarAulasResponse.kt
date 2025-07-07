package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para búsqueda de aulas.
 * Compatible con el flujo actual: búsqueda por nombre y filtros.
 */
data class BuscarAulasResponse(
    @SerializedName("aulas")
    val aulas: List<AulaVirtual>,

    @SerializedName("totalResultados")
    val totalResultados: Int = aulas.size,  // Opcional: fallback automático

    @SerializedName("terminoBusqueda")
    val terminoBusqueda: String = "",

    @SerializedName("message")
    val message: String? = null
)
