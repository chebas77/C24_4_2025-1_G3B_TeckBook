package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
 * Configuración de usuario
 */
data class UserSettings(
    @SerializedName("theme")
    val theme: String = "system",

    @SerializedName("notifications")
    val notifications: Boolean = true,

    @SerializedName("language")
    val language: String = "es",

    @SerializedName("autoSync")
    val autoSync: Boolean = true,

    @SerializedName("cacheImages")
    val cacheImages: Boolean = true,

    @SerializedName("dataUsage")
    val dataUsage: String = "normal" // "low", "normal", "high"
) {
    /**
     * Verifica si está en modo oscuro
     */
    fun isDarkMode(): Boolean {
        return theme == "dark"
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return notifications
    }

    /**
     * Verifica si está en modo de ahorro de datos
     */
    fun isDataSavingMode(): Boolean {
        return dataUsage == "low"
    }
}