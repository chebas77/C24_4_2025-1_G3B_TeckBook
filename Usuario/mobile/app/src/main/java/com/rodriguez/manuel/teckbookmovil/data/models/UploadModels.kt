package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🖼️ UPLOAD DE IMÁGENES MODELS
// ===============================================

@Serializable
data class UploadImageResponse(
    val imageUrl: String,
    val message: String,
    val previousUrl: String? = null,
    val userId: Long? = null,
    val verified: Boolean? = null
)

@Serializable
data class CurrentImageResponse(
    val imageUrl: String,
    val hasImage: Boolean,
    val userId: Long,
    val timestamp: Long
)

@Serializable
data class RemoveImageResponse(
    val message: String,
    val imageUrl: String,
    val previousImageUrl: String? = null,
    val timestamp: Long
)