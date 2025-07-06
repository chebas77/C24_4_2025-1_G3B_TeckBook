package com.rodriguez.manuel.teckbookmovil.data.models.carrera
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para departamentos activos
 */
data class DepartamentosActivosResponse(
    @SerializedName("departamentos")
    val departamentos: List<Departamento>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("message")
    val message: String
)
