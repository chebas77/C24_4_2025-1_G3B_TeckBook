package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 👤 USUARIO MODELS
// ===============================================

@Serializable
data class UserData(
    val id: Long,
    val nombre: String,
    val apellidos: String,
    val correoInstitucional: String,
    val rol: String,
    val cicloActual: Int? = null,
    val departamentoId: Long? = null,
    val carreraId: Long? = null,
    val seccionId: Long? = null,
    val telefono: String? = null,
    val profileImageUrl: String? = null,
    val requiresCompletion: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class UserResponse(
    val id: Long? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val correoInstitucional: String? = null,
    val rol: String? = null,
    val cicloActual: Int? = null,
    val departamentoId: Long? = null,
    val carreraId: Long? = null,
    val seccionId: Long? = null,
    val telefono: String? = null,
    val profileImageUrl: String? = null,
    val requiresCompletion: Boolean? = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    // Campos adicionales que puede retornar el API
    val user: UserData? = null,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class RegisterRequest(
    val nombre: String,
    val apellidos: String,
    val correoInstitucional: String,
    val password: String,
    val cicloActual: Int,
    val carreraId: Long,
    val departamentoId: Long? = 1L,
    val seccionId: Long? = null,
    val telefono: String? = null
)

@Serializable
data class RegisterResponse(
    val message: String,
    val usuario: UserData,
    val carrera: CarreraData? = null
)

@Serializable
data class UpdateUserRequest(
    val nombre: String? = null,
    val apellidos: String? = null,
    val cicloActual: Int? = null,
    val telefono: String? = null,
    val carreraId: Long? = null,
    val departamentoId: Long? = null,
    val seccionId: Long? = null
)

@Serializable
data class UpdateUserResponse(
    val message: String,
    val usuario: UserData
)