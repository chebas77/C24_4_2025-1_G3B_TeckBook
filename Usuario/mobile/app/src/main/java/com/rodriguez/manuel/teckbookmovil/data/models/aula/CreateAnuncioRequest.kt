package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Request para crear un anuncio.
 * Compatible con multipart si se adjunta archivo.
 */
data class CreateAnuncioRequest(
    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("contenido")
    val contenido: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("permiteComentarios")
    val permiteComentarios: Boolean = true
)
