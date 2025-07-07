package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Invitación a Aula
 * Basado en la entidad InvitacionAula del backend.
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

    fun getEstadoEnum(): EstadoInvitacion {
        return EstadoInvitacion.fromString(estado)
    }

    fun isPendiente() = getEstadoEnum() == EstadoInvitacion.PENDIENTE
    fun isAceptada() = getEstadoEnum() == EstadoInvitacion.ACEPTADA
    fun isRechazada() = getEstadoEnum() == EstadoInvitacion.RECHAZADA
    fun isExpirada() = getEstadoEnum() == EstadoInvitacion.EXPIRADA

    fun getEstadoColor(): String {
        return when (getEstadoEnum()) {
            EstadoInvitacion.PENDIENTE -> "#FF9800"
            EstadoInvitacion.ACEPTADA -> "#4CAF50"
            EstadoInvitacion.RECHAZADA -> "#F44336"
            EstadoInvitacion.EXPIRADA -> "#757575"
        }
    }

    fun getEstadoDisplay(): String {
        return when (getEstadoEnum()) {
            EstadoInvitacion.PENDIENTE -> "Pendiente"
            EstadoInvitacion.ACEPTADA -> "Aceptada"
            EstadoInvitacion.RECHAZADA -> "Rechazada"
            EstadoInvitacion.EXPIRADA -> "Expirada"
        }
    }
}
