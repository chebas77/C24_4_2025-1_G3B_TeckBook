package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName
/**
 * Respuesta para upload de imagen de perfil
 */
data class UploadImageResponse(
    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("previousUrl")
    val previousUrl: String? = null,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("verified")
    val verified: Boolean = true
)