package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Configuración de usuario para personalización de la app.
 */
data class UserSettings(
    @SerializedName("theme")
    val theme: String = "system", // Opciones: "light", "dark", "system"

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
    fun isDarkMode(): Boolean = theme == "dark"

    fun areNotificationsEnabled(): Boolean = notifications

    fun isDataSavingMode(): Boolean = dataUsage == "low"
}
