package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🏢 DEPARTAMENTOS MODELS
// ===============================================

@Serializable
data class DepartamentoData(
    val id: Long,
    val nombre: String,
    val codigo: String? = null,
    val activo: Boolean = true
)

@Serializable
data class DepartamentosResponse(
    val departamentos: List<DepartamentoData>,
    val count: Int,
    val message: String
)

@Serializable
data class DepartamentoResponse(
    val id: Long? = null,
    val nombre: String? = null,
    val codigo: String? = null,
    val activo: Boolean? = null,
    val message: String? = null,
    val error: String? = null
)