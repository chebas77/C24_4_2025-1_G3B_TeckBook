package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para listar participantes de un aula.
 */
data class ParticipantesResponse(
    @SerializedName("participantes")
    val participantes: List<AulaEstudiante>
)
