package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import java.time.LocalDate
import java.time.LocalDateTime

// ========== MODELOS DE AULAS VIRTUALES ==========

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
    /**
     * Convierte el estado string a enum
     */
    fun getEstadoEnum(): AppConfig.AulaState {
        return AppConfig.AulaState.fromString(estado)
    }

    /**
     * Verifica si el aula está activa
     */
    fun isActiva(): Boolean {
        return getEstadoEnum() == AppConfig.AulaState.ACTIVA
    }

    /**
     * Verifica si el aula está finalizada
     */
    fun isFinalizada(): Boolean {
        return getEstadoEnum() == AppConfig.AulaState.FINALIZADA
    }

    /**
     * Obtiene el título o nombre como fallback
     */
    fun getTituloOrNombre(): String {
        return titulo?.takeIf { it.isNotBlank() } ?: nombre
    }

    /**
     * Obtiene información del profesor
     */
    fun getProfesorInfo(): String {
        return profesorNombreCompleto ?: "Profesor"
    }

    /**
     * Obtiene información del estado para mostrar en UI
     */
    fun getEstadoDisplay(): String {
        return when (getEstadoEnum()) {
            AppConfig.AulaState.ACTIVA -> "Activa"
            AppConfig.AulaState.INACTIVA -> "Inactiva"
            AppConfig.AulaState.FINALIZADA -> "Finalizada"
        }
    }

    /**
     * Obtiene color del estado para UI
     */
    fun getEstadoColor(): String {
        return when (getEstadoEnum()) {
            AppConfig.AulaState.ACTIVA -> "#4CAF50"      // Verde
            AppConfig.AulaState.INACTIVA -> "#FF9800"    // Naranja
            AppConfig.AulaState.FINALIZADA -> "#757575"  // Gris
        }
    }
}
