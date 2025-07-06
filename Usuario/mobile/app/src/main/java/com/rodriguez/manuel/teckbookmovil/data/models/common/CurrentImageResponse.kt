package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
 * Respuesta para obtener imagen actual
 */
data class CurrentImageResponse(
    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("hasImage")
    val hasImage: Boolean,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("timestamp")
    val timestamp: Long
)