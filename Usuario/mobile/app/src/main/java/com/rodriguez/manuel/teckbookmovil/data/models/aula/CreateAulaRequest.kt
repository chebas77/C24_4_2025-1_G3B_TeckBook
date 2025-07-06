package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Request para crear aula virtual
 */
data class CreateAulaRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("titulo")
    val titulo: String? = null,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("seccionId")
    val seccionId: Long? = null,

    @SerializedName("fechaInicio")
    val fechaInicio: String? = null,

    @SerializedName("fechaFin")
    val fechaFin: String? = null
)