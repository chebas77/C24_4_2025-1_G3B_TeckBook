package com.rodriguez.manuel.teckbookmovil.data.models

import kotlinx.serialization.Serializable

// ===============================================
// 🏥 HEALTH CHECK MODELS
// ===============================================

@Serializable
data class HealthResponse(
    val service: String,
    val status: String,
    val timestamp: Long,
    val endpoints: Map<String, String>? = null
)