package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import java.time.LocalDateTime

// ========== MODELOS DE AUTENTICACIÓN ==========

/**
 * Modelo para solicitud de login tradicional
 */
data class LoginRequest(
    @SerializedName("correoInstitucional")
    val correoInstitucional: String,

    @SerializedName("password")
    val password: String
)
