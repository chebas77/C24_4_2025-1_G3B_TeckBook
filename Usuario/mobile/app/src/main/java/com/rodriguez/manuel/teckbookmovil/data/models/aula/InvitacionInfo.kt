package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Información básica de invitación en respuestas.
 */
data class InvitacionInfo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("correoInvitado")
    val correoInvitado: String,

    @SerializedName("codigoInvitacion")
    val codigoInvitacion: String,

    @SerializedName("fechaInvitacion")
    val fechaInvitacion: String? = null,

    @SerializedName("fechaExpiracion")
    val fechaExpiracion: String? = null
)
