package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Request para buscar aulas
 */
data class SearchAulasRequest(
    @SerializedName("nombre")
    val nombre: String
)