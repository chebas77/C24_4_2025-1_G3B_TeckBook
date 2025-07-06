package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para participantes de aula
 */
data class ParticipantesResponse(
    @SerializedName("participantes")
    val participantes: List<AulaEstudiante>
)