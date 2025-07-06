package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
* Respuesta simple con solo mensaje
*/
data class MessageResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("details")
    val details: Map<String, Any>? = null
)