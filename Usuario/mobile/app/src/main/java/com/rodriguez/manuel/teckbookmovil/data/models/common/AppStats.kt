package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Estadísticas generales de la app.
 */
data class AppStats(
    @SerializedName("totalUsuarios")
    val totalUsuarios: Long,

    @SerializedName("totalAulas")
    val totalAulas: Long,

    @SerializedName("totalAnuncios")
    val totalAnuncios: Long,

    @SerializedName("usuariosActivos")
    val usuariosActivos: Long,

    @SerializedName("aulasActivas")
    val aulasActivas: Long,

    @SerializedName("timestamp")
    val timestamp: Long
)
