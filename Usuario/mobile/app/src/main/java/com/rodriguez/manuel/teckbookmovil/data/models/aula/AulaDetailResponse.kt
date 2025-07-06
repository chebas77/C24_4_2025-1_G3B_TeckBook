package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para detalle de aula específica
 */
data class AulaDetailResponse(
    @SerializedName("aula")
    val aula: AulaVirtual,

    @SerializedName("estudiantes")
    val estudiantes: List<AulaEstudiante>,

    @SerializedName("totalEstudiantes")
    val totalEstudiantes: Long,

    @SerializedName("esProfesor")
    val esProfesor: Boolean
)