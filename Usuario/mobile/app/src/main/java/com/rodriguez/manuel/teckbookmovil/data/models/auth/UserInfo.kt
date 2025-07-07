package com.rodriguez.manuel.teckbookmovil.data.models.auth

import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

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
    fun getFullName(): String = "$nombre $apellidos".trim()

    fun getRoleEnum(): AppConfig.UserRole =
        AppConfig.UserRole.fromString(rol)

    fun isProfesor(): Boolean = getRoleEnum() == AppConfig.UserRole.PROFESOR

    fun isEstudiante(): Boolean = getRoleEnum() == AppConfig.UserRole.ESTUDIANTE

    fun isAdmin(): Boolean = getRoleEnum() == AppConfig.UserRole.ADMIN

    fun isProfileComplete(): Boolean =
        !requiresCompletion &&
                carreraId != null &&
                cicloActual != null &&
                departamentoId != null

    fun getInitials(): String {
        val nameInitial = nombre.firstOrNull()?.toString()?.uppercase() ?: ""
        val lastNameInitial = apellidos.split(" ").firstOrNull()?.firstOrNull()?.toString()?.uppercase() ?: ""
        return "$nameInitial$lastNameInitial"
    }
}
