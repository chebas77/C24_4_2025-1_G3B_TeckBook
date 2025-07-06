package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

// ========== RESPUESTAS DE API ==========

/**
 * Respuesta para obtener aulas del usuario
 */
data class AulasResponse(
    @SerializedName("aulas")
    val aulas: List<AulaVirtual>,

    @SerializedName("totalAulas")
    val totalAulas: Int,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("usuarioId")
    val usuarioId: Long,

    @SerializedName("message")
    val message: String
)