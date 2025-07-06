package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

// ========== MODELOS DE INVITACIONES ==========

/**
 * Modelo de Invitación a Aula
 * Basado en la entidad InvitacionAula del backend
 */
data class InvitacionAula(
    @SerializedName("id")
    val id: Long,

    @SerializedName("aulaVirtualId")
    val aulaVirtualId: Long,

    @SerializedName("invitadoPorId")
    val invitadoPorId: Long,

    @SerializedName("correoInvitado")
    val correoInvitado: String,

    @SerializedName("codigoInvitacion")
    val codigoInvitacion: String,

    @SerializedName("estado")
    val estado: String = "pendiente",

    @SerializedName("mensaje")
    val mensaje: String? = null,

    @SerializedName("fechaInvitacion")
    val fechaInvitacion: String? = null,

    @SerializedName("fechaExpiracion")
    val fechaExpiracion: String? = null,

    @SerializedName("fechaRespuesta")
    val fechaRespuesta: String? = null
) {
    /**
     * Estados posibles de la invitación
     */
    enum class EstadoInvitacion(val value: String) {
        PENDIENTE("pendiente"),
        ACEPTADA("aceptada"),
        RECHAZADA("rechazada"),
        EXPIRADA("expirada");

        companion object {
            fun fromString(value: String?): EstadoInvitacion {
                return values().find { it.value.equals(value, ignoreCase = true) } ?: PENDIENTE
            }
        }
    }

    /**
     * Convierte estado string a enum
     */
    fun getEstadoEnum(): EstadoInvitacion {
        return EstadoInvitacion.fromString(estado)
    }

    /**
     * Verifica si está pendiente
     */
    fun isPendiente(): Boolean {
        return getEstadoEnum() == EstadoInvitacion.PENDIENTE
    }

    /**
     * Verifica si fue aceptada
     */
    fun isAceptada(): Boolean {
        return getEstadoEnum() == EstadoInvitacion.ACEPTADA
    }

    /**
     * Verifica si fue rechazada
     */
    fun isRechazada(): Boolean {
        return getEstadoEnum() == EstadoInvitacion.RECHAZADA
    }

    /**
     * Verifica si expiró
     */
    fun isExpirada(): Boolean {
        return getEstadoEnum() == EstadoInvitacion.EXPIRADA
    }

    /**
     * Obtiene color del estado para UI
     */
    fun getEstadoColor(): String {
        return when (getEstadoEnum()) {
            EstadoInvitacion.PENDIENTE -> "#FF9800"    // Naranja
            EstadoInvitacion.ACEPTADA -> "#4CAF50"     // Verde
            EstadoInvitacion.RECHAZADA -> "#F44336"    // Rojo
            EstadoInvitacion.EXPIRADA -> "#757575"     // Gris
        }
    }

    /**
     * Obtiene texto del estado para mostrar
     */
    fun getEstadoDisplay(): String {
        return when (getEstadoEnum()) {
            EstadoInvitacion.PENDIENTE -> "Pendiente"
            EstadoInvitacion.ACEPTADA -> "Aceptada"
            EstadoInvitacion.RECHAZADA -> "Rechazada"
            EstadoInvitacion.EXPIRADA -> "Expirada"
        }
    }
}