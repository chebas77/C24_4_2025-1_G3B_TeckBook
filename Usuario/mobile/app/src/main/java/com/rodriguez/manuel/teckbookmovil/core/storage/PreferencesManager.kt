package com.rodriguez.manuel.teckbookmovil.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

/**
 * Maneja SharedPreferences de forma centralizada para TecBook.
 * Usado por TokenManager y configuración general.
 */
class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        AppConfig.Storage.PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val editor: SharedPreferences.Editor
        get() = preferences.edit()

    // ====================== OPERACIONES BÁSICAS ======================

    fun saveString(key: String, value: String) = editor.putString(key, value).apply()
    fun getString(key: String, defaultValue: String? = null) = preferences.getString(key, defaultValue)

    fun saveBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).apply()
    fun getBoolean(key: String, defaultValue: Boolean = false) = preferences.getBoolean(key, defaultValue)

    fun saveLong(key: String, value: Long) = editor.putLong(key, value).apply()
    fun getLong(key: String, defaultValue: Long = 0L) = preferences.getLong(key, defaultValue)

    fun removeKey(key: String) = editor.remove(key).apply()
    fun clearAll() = editor.clear().apply()

    fun contains(key: String) = preferences.contains(key)

    // ====================== CONFIGURACIÓN GENERAL ======================

    fun saveThemeMode(mode: String) = saveString("theme_mode", mode)
    fun getThemeMode(): String = getString("theme_mode", AppConfig.Storage.DEFAULT_THEME_MODE)
        ?: AppConfig.Storage.DEFAULT_THEME_MODE

    fun saveNotificationsEnabled(enabled: Boolean) = saveBoolean("notifications_enabled", enabled)
    fun areNotificationsEnabled(): Boolean = getBoolean("notifications_enabled", true)

    fun saveLanguage(languageCode: String) = saveString("language", languageCode)
    fun getLanguage(): String = getString("language", "es") ?: "es"

    fun saveLastSyncTime(timestamp: Long = System.currentTimeMillis()) = saveLong("last_sync_time", timestamp)
    fun getLastSyncTime(): Long = getLong("last_sync_time", 0L)
    fun needsSync(maxAgeMs: Long = AppConfig.Storage.CACHE_VALIDITY_TIME): Boolean {
        val lastSync = getLastSyncTime()
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastSync) > maxAgeMs
    }

    fun saveAppVersion(version: String) = saveString("app_version", version)
    fun getAppVersion(): String? = getString("app_version")
}
