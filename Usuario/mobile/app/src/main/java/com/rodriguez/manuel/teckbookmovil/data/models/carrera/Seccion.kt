package com.rodriguez.manuel.teckbookmovil.data.models.carrera
import com.google.gson.annotations.SerializedName

/**
 * Modelo de Sección
 * Basado en la entidad Seccion del backend
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
     * Obtiene el nombre completo de la sección
     */
    fun getNombreCompleto(): String {
        return if (!codigo.isNullOrEmpty()) {
            "$nombre ($codigo)"
        } else {
            nombre
        }
    }

    /**
     * Obtiene información del ciclo
     */
    fun getCicloInfo(): String {
        return "Ciclo $ciclo"
    }
}