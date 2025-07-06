package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import java.time.LocalDateTime

/**
 * Modelo para información del usuario autenticado
 * Basado en buildUserResponse del AuthController
 */
data class UserInfo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("correoInstitucional")
    val correoInstitucional: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("cicloActual")
    val cicloActual: Int?,

    @SerializedName("departamentoId")
    val departamentoId: Long?,

    @SerializedName("carreraId")
    val carreraId: Long?,

    @SerializedName("seccionId")
    val seccionId: Long?,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,

    @SerializedName("requiresCompletion")
    val requiresCompletion: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    /**
     * Obtiene el nombre completo del usuario
     */
    fun getFullName(): String {
        return "$nombre $apellidos".trim()
    }

    /**
     * Convierte el rol string a enum
     */
    fun getRoleEnum(): AppConfig.UserRole {
        return AppConfig.UserRole.fromString(rol)
    }

    /**
     * Verifica si es profesor
     */
    fun isProfesor(): Boolean {
        return getRoleEnum() == AppConfig.UserRole.PROFESOR
    }

    /**
     * Verifica si es estudiante
     */
    fun isEstudiante(): Boolean {
        return getRoleEnum() == AppConfig.UserRole.ESTUDIANTE
    }

    /**
     * Verifica si es admin
     */
    fun isAdmin(): Boolean {
        return getRoleEnum() == AppConfig.UserRole.ADMIN
    }

    /**
     * Verifica si el perfil está completo
     */
    fun isProfileComplete(): Boolean {
        return !requiresCompletion &&
                carreraId != null &&
                cicloActual != null &&
                departamentoId != null
    }

    /**
     * Obtiene las iniciales del usuario para avatares
     */
    fun getInitials(): String {
        val nameInitial = nombre.firstOrNull()?.toString()?.uppercase() ?: ""
        val lastNameInitial = apellidos.split(" ").firstOrNull()?.firstOrNull()?.toString()?.uppercase() ?: ""
        return "$nameInitial$lastNameInitial"
    }
}
