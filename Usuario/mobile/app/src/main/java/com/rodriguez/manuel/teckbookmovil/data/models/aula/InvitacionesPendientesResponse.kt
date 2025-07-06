package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para invitaciones pendientes
 */
data class InvitacionesPendientesResponse(
    @SerializedName("invitaciones")
    val invitaciones: List<InvitacionAula>,

    @SerializedName("total")
    val total: Int
)