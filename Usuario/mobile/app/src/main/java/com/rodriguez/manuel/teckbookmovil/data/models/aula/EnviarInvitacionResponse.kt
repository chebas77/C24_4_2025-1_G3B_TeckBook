package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para enviar invitación de aula.
 * Compatible con flujo actual.
 */
data class EnviarInvitacionResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("invitacion")
    val invitacion: InvitacionInfo
)
