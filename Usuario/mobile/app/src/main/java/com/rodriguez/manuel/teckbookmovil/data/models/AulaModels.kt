package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🏫 AULAS VIRTUALES MODELS
// ===============================================

@Serializable
data class AulaVirtualData(
    val id: Long,
    val nombre: String,
    val titulo: String? = null,
    val descripcion: String? = null,
    val codigoAcceso: String,
    val profesorId: Long,
    val profesorNombreCompleto: String? = null,
    val seccionId: Long? = null,
    val estado: String,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class AulasResponse(
    val aulas: List<AulaVirtualData>,
    val totalAulas: Int,
    val rol: String,
    val usuarioId: Long,
    val message: String
)

@Serializable
data class AulaDetalleResponse(
    val aula: AulaVirtualData,
    val estudiantes: List<EstudianteAulaData>? = null,
    val totalEstudiantes: Long? = null,
    val esProfesor: Boolean = false
)

@Serializable
data class EstudianteAulaData(
    val id: Long,
    val aulaId: Long,
    val estudianteId: Long,
    val estado: String,
    val fechaUnion: String? = null,
    val fechaSalida: String? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val email: String? = null
)

@Serializable
data class AgregarEstudianteResponse(
    val message: String,
    val aulaId: Long,
    val estudianteId: Long,
    val estudianteNombre: String
)