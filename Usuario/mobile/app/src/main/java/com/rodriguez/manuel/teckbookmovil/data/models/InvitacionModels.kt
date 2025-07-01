package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 📨 INVITACIONES MODELS
// ===============================================

@Serializable
data class InvitacionData(
    val id: Long,
    val aulaVirtualId: Long,
    val invitadoPorId: Long,
    val correoInvitado: String,
    val codigoInvitacion: String,
    val estado: String,
    val mensaje: String? = null,
    val fechaInvitacion: String,
    val fechaExpiracion: String,
    val fechaRespuesta: String? = null
)

@Serializable
data class EnviarInvitacionRequest(
    val aulaId: Long,
    val correoInvitado: String,
    val mensaje: String? = null
)

@Serializable
data class EnviarInvitacionResponse(
    val message: String,
    val invitacion: Map<String, String>? = null
)

@Serializable
data class AceptarInvitacionResponse(
    val message: String,
    val aulaId: Long
)

@Serializable
data class InvitacionesResponse(
    val invitaciones: List<InvitacionData>,
    val total: Int
)