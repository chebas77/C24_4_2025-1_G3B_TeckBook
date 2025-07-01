package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 📚 CARRERAS MODELS
// ===============================================

@Serializable
data class CarreraData(
    val id: Long,
    val nombre: String,
    val codigo: String,
    val departamentoId: Long,
    val activo: Boolean = true,
    val descripcion: String? = null,
    val duracionCiclos: Int? = null,
    val modalidad: String? = null
)

@Serializable
data class CarrerasResponse(
    val carreras: List<CarreraData>,
    val count: Int,
    val message: String,
    val isEmpty: Boolean = false,
    val departamentoId: Long? = null
)

@Serializable
data class CarreraResponse(
    val carrera: CarreraData? = null,
    val message: String? = null,
    val error: String? = null,
    val id: Long? = null
)