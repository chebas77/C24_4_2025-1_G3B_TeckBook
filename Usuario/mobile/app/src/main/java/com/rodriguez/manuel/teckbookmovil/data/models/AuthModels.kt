package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🔐 AUTENTICACIÓN MODELS
// ===============================================

@Serializable
data class LoginRequest(
    val correoInstitucional: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val requiresCompletion: Boolean = false,
    val user: UserData? = null,
    val redirectTo: String? = null,
    val message: String? = null
)

@Serializable
data class GoogleLoginResponse(
    val redirectUrl: String,
    val message: String
)

@Serializable
data class LogoutResponse(
    val message: String,
    val userEmail: String? = null,
    val tokenInvalidated: Boolean = false,
    val timestamp: Long
)