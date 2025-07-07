package com.rodriguez.manuel.teckbookmovil.data.models.aula

import com.google.gson.annotations.SerializedName

/**
 * Respuesta para listar todas mis invitaciones.
 */
data class MisInvitacionesResponse(
    @SerializedName("invitaciones")
    val invitaciones: List<InvitacionAula>,

    @SerializedName("total")
    val total: Int
)
