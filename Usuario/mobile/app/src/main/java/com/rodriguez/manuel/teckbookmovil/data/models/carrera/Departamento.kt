package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Departamento
 * Basado en la entidad Departamento del backend.
 */
data class Departamento(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("codigo")
    val codigo: String? = null,

    @SerializedName("activo")
    val activo: Boolean = true
) {
    /**
     * Verifica si el departamento está activo.
     */
    fun isActivo(): Boolean {
        return activo
    }
}
