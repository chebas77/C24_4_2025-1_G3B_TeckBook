package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🏫 SECCIONES MODELS
// ===============================================

@Serializable
data class SeccionData(
    val id: Long,
    val nombre: String,
    val codigo: String? = null,
    val ciclo: Int,
    val carreraId: Long? = null
)

@Serializable
data class SeccionesResponse(
    val secciones: List<SeccionData>,
    val count: Int,
    val message: String,
    val carreraId: Long? = null,
    val cicloId: Long? = null
)