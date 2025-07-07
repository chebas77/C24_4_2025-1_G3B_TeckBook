package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API para listar aulas.
 */
data class AulasResponse(
    @SerializedName("aulas")
    val aulas: List<AulaVirtual>
)
