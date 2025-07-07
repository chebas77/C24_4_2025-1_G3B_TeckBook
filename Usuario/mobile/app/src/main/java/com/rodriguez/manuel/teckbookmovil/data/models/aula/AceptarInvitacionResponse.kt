package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para aceptar invitación
 */
data class AceptarInvitacionResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("aulaId")
    val aulaId: Long
)
