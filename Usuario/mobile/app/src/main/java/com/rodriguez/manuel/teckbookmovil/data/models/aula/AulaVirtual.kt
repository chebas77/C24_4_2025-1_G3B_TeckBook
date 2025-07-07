package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

/**
 * Modelo de Aula Virtual
 * Basado en la entidad AulaVirtual del backend
 */
data class AulaVirtual(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("titulo")
    val titulo: String? = null,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("codigoAcceso")
    val codigoAcceso: String,

    @SerializedName("profesorId")
    val profesorId: Long,

    @SerializedName("seccionId")
    val seccionId: Long? = null,

    @SerializedName("estado")
    val estado: String = "activa",

    @SerializedName("fechaInicio")
    val fechaInicio: String? = null,

    @SerializedName("fechaFin")
    val fechaFin: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    // Campos adicionales que pueden venir del controller
    @SerializedName("profesorNombreCompleto")
    val profesorNombreCompleto: String? = null,

    @SerializedName("totalEstudiantes")
    val totalEstudiantes: Int = 0
) {
    fun getEstadoEnum(): AppConfig.AulaState {
        return AppConfig.AulaState.fromString(estado)
    }

    fun isActiva(): Boolean = getEstadoEnum() == AppConfig.AulaState.ACTIVA
    fun isFinalizada(): Boolean = getEstadoEnum() == AppConfig.AulaState.FINALIZADA

    fun getTituloOrNombre(): String = titulo?.takeIf { it.isNotBlank() } ?: nombre

    fun getProfesorInfo(): String = profesorNombreCompleto ?: "Profesor"

    fun getEstadoDisplay(): String = when (getEstadoEnum()) {
        AppConfig.AulaState.ACTIVA -> "Activa"
        AppConfig.AulaState.INACTIVA -> "Inactiva"
        AppConfig.AulaState.FINALIZADA -> "Finalizada"
    }

    fun getEstadoColor(): String = when (getEstadoEnum()) {
        AppConfig.AulaState.ACTIVA -> "#4CAF50"      // Verde
        AppConfig.AulaState.INACTIVA -> "#FF9800"    // Naranja
        AppConfig.AulaState.FINALIZADA -> "#757575"  // Gris
    }
}
