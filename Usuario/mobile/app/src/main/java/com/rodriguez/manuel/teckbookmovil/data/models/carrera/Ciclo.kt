package com.rodriguez.manuel.teckbookmovil.data.models.carrera
import com.google.gson.annotations.SerializedName

/**
 * Modelo de Ciclo
 * Basado en la entidad Ciclo del backend
 */
data class Ciclo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("numero")
    val numero: Int,

    @SerializedName("nombre")
    val nombre: String
) {
    /**
     * Obtiene el nombre formateado del ciclo
     */
    fun getNombreFormateado(): String {
        return "$numero - $nombre"
    }

    /**
     * Verifica si es un ciclo válido
     */
    fun isValid(): Boolean {
        return numero in 1..6
    }
}
