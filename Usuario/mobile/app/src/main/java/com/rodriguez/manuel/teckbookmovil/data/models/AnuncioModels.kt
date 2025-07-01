package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 📢 ANUNCIOS MODELS
// ===============================================

@Serializable
data class AnuncioData(
    val id: Long,
    val titulo: String,
    val contenido: String,
    val aulaId: Long,
    val autorId: Long,
    val fechaPublicacion: String,
    val fechaEdicion: String? = null,
    val activo: Boolean = true
)

@Serializable
data class AnunciosResponse(
    val anuncios: List<AnuncioData>? = null,
    val total: Int? = null,
    val aulaId: Long? = null,
    val message: String? = null,
    val error: String? = null
)