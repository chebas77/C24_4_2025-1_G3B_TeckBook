package com.rodriguez.manuel.teckbookmovil.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

/**
 * Maneja todas las operaciones de SharedPreferences de manera centralizada
 * Proporciona una interfaz simple y segura para el almacenamiento local
 */
class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        AppConfig.Storage.PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val editor: SharedPreferences.Editor
        get() = preferences.edit()

    // ========== MÉTODOS PARA STRING ==========

    /**
     * Guarda un valor String
     */
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    /**
     * Obtiene un valor String
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return preferences.getString(key, defaultValue)
    }

    // ========== MÉTODOS PARA BOOLEAN ==========

    /**
     * Guarda un valor Boolean
     */
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * Obtiene un valor Boolean
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    // ========== MÉTODOS PARA INT ==========

    /**
     * Guarda un valor Int
     */
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * Obtiene un valor Int
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    // ========== MÉTODOS PARA LONG ==========

    /**
     * Guarda un valor Long
     */
    fun saveLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    /**
     * Obtiene un valor Long
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(key, defaultValue)
    }

    // ========== MÉTODOS PARA FLOAT ==========

    /**
     * Guarda un valor Float
     */
    fun saveFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    /**
     * Obtiene un valor Float
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return preferences.getFloat(key, defaultValue)
    }

    // ========== MÉTODOS PARA SET<STRING> ==========

    /**
     * Guarda un Set de Strings
     */
    fun saveStringSet(key: String, value: Set<String>) {
        editor.putStringSet(key, value).apply()
    }

    /**
     * Obtiene un Set de Strings
     */
    fun getStringSet(key: String, defaultValue: Set<String>? = null): Set<String>? {
        return preferences.getStringSet(key, defaultValue)
    }

    // ========== MÉTODOS UTILITARIOS ==========

    /**
     * Verifica si existe una clave
     */
    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    /**
     * Elimina una clave específica
     */
    fun removeKey(key: String) {
        editor.remove(key).apply()
    }

    /**
     * Limpia todas las preferencias
     */
    fun clearAll() {
        editor.clear().apply()
    }

    /**
     * Obtiene todas las claves almacenadas
     */
    fun getAllKeys(): Set<String> {
        return preferences.all.keys
    }

    /**
     * Obtiene el número total de elementos almacenados
     */
    fun getCount(): Int {
        return preferences.all.size
    }

    // ========== MÉTODOS ESPECÍFICOS PARA TECBOOK ==========

    /**
     * Guarda configuración de tema
     */
    fun saveThemeMode(mode: String) {
        saveString("theme_mode", mode)
    }

    /**
     * Obtiene configuración de tema
     */
    fun getThemeMode(): String {
        return getString("theme_mode", AppConfig.App.DEFAULT_THEME_MODE) ?: AppConfig.App.DEFAULT_THEME_MODE
    }

    /**
     * Guarda configuración de notificaciones
     */
    fun saveNotificationsEnabled(enabled: Boolean) {
        saveBoolean("notifications_enabled", enabled)
    }

    /**
     * Obtiene configuración de notificaciones
     */
    fun areNotificationsEnabled(): Boolean {
        return getBoolean("notifications_enabled", true)
    }

    /**
     * Guarda la última vez que se sincronizaron datos
     */
    fun saveLastSyncTime(timestamp: Long = System.currentTimeMillis()) {
        saveLong("last_sync_time", timestamp)
    }

    /**
     * Obtiene la última vez que se sincronizaron datos
     */
    fun getLastSyncTime(): Long {
        return getLong("last_sync_time", 0L)
    }

    /**
     * Verifica si los datos necesitan sincronización
     */
    fun needsSync(maxAgeMs: Long = AppConfig.App.CACHE_VALIDITY_TIME): Boolean {
        val lastSync = getLastSyncTime()
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastSync) > maxAgeMs
    }

    /**
     * Guarda la versión de la app para detectar actualizaciones
     */
    fun saveAppVersion(version: String) {
        saveString("app_version", version)
    }

    /**
     * Obtiene la versión guardada de la app
     */
    fun getAppVersion(): String? {
        return getString("app_version")
    }

    /**
     * Verifica si es la primera vez que se abre la app
     */
    fun isFirstLaunch(): Boolean {
        return !contains("first_launch_completed")
    }

    /**
     * Marca que la app ya se abrió por primera vez
     */
    fun markFirstLaunchCompleted() {
        saveBoolean("first_launch_completed", true)
    }

    /**
     * Guarda búsquedas recientes
     */
    fun saveRecentSearch(query: String) {
        val recentSearches = getStringSet("recent_searches", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Eliminar si ya existe para evitar duplicados
        recentSearches.remove(query)

        // Agregar al inicio
        recentSearches.add(query)

        // Mantener solo las últimas 5 búsquedas
        if (recentSearches.size > 5) {
            recentSearches.remove(recentSearches.first())
        }

        saveStringSet("recent_searches", recentSearches)
    }

    /**
     * Obtiene búsquedas recientes
     */
    fun getRecentSearches(): List<String> {
        val searches = getStringSet("recent_searches", emptySet()) ?: emptySet()
        return searches.toList().reversed() // Más recientes primero
    }

    /**
     * Limpia búsquedas recientes
     */
    fun clearRecentSearches() {
        removeKey("recent_searches")
    }

    /**
     * Guarda configuración de idioma
     */
    fun saveLanguage(languageCode: String) {
        saveString("language", languageCode)
    }

    /**
     * Obtiene configuración de idioma
     */
    fun getLanguage(): String {
        return getString("language", "es") ?: "es"
    }

    /**
     * Guarda si se deben mostrar tutoriales
     */
    fun saveTutorialShown(tutorialKey: String, shown: Boolean = true) {
        saveBoolean("tutorial_$tutorialKey", shown)
    }

    /**
     * Verifica si ya se mostró un tutorial específico
     */
    fun isTutorialShown(tutorialKey: String): Boolean {
        return getBoolean("tutorial_$tutorialKey", false)
    }
}