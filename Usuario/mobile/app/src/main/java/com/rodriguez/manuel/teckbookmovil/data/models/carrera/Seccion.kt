package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Sección.
 * Basado en la entidad Seccion del backend.
 */
data class Seccion(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("codigo")
    val codigo: String? = null,

    @SerializedName("ciclo")
    val ciclo: Int,

    @SerializedName("carreraId")
    val carreraId: Long? = null
) {
    /**
     * Devuelve el nombre completo con código si aplica.
     */
    fun getNombreCompleto(): String {
        return if (!codigo.isNullOrEmpty()) {
            "$nombre ($codigo)"
        } else {
            nombre
        }
    }

    /**
     * Muestra información resumida del ciclo.
     */
    fun getCicloInfo(): String {
        return "Ciclo $ciclo"
    }
}
