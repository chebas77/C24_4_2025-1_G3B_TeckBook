package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Request para agregar estudiante a aula
 */
data class AddEstudianteRequest(
    @SerializedName("estudianteId")
    val estudianteId: Long
)