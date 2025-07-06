package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Request para enviar invitación
 */
data class EnviarInvitacionRequest(
    @SerializedName("aulaId")
    val aulaId: Long,

    @SerializedName("correoInvitado")
    val correoInvitado: String,

    @SerializedName("mensaje")
    val mensaje: String? = null
)