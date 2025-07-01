package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🚨 ERROR RESPONSES
// ===============================================

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String? = null,
    val timestamp: Long? = null,
    val details: Map<String, String>? = null,
    val requiresLogin: Boolean? = null
)

@Serializable
data class ApiError(
    val code: Int,
    val message: String,
    val details: String? = null
)