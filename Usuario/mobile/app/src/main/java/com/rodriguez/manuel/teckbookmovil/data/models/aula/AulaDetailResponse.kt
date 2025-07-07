package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend para obtener el detalle de un aula específica,
 * incluyendo lista de estudiantes y rol del usuario.
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
