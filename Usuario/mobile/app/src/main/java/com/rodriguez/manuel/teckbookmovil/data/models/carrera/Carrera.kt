package com.rodriguez.manuel.teckbookmovil.data.models.carrera

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Carrera
 * Basado en la entidad Carrera del backend
 */
data class Carrera(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("departamentoId")
    val departamentoId: Long,

    @SerializedName("activo")
    val activo: Boolean = true,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("duracionCiclos")
    val duracionCiclos: Int? = null,

    @SerializedName("modalidad")
    val modalidad: String? = null
) {
    /** Devuelve nombre completo con código */
    fun getNombreCompleto(): String {
        return if (codigo.isNotEmpty()) "$nombre ($codigo)" else nombre
    }

    /** Verifica si la carrera está activa */
    fun isActiva(): Boolean = activo

    /** Obtiene duración en ciclos con fallback */
    fun getDuracionCiclosOrDefault(): Int = duracionCiclos ?: 6
}
