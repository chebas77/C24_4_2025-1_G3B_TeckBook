package com.rodriguez.manuel.teckbookmovil.data.models.aula

/**
 * Modelo para estadísticas de aula
 */
data class AulaStats(
    val totalEstudiantes: Int,
    val totalAnuncios: Int,
    val anunciosPendientes: Int,
    val ultimaActividad: String?
) {
    /**
     * Verifica si hay actividad reciente
     */
    fun hasRecentActivity(): Boolean {
        return anunciosPendientes > 0 || ultimaActividad != null
    }
}