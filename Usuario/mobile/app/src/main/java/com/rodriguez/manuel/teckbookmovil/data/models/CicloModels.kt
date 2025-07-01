package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 📖 CICLOS MODELS
// ===============================================

@Serializable
data class CicloData(
    val id: Long,
    val numero: Int,
    val nombre: String
)

@Serializable
data class CiclosResponse(
    val ciclos: List<CicloData>,
    val count: Int,
    val message: String,
    val carreraId: Long? = null
)