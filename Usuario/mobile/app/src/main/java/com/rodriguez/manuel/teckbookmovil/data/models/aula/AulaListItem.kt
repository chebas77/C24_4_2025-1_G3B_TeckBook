package com.rodriguez.manuel.teckbookmovil.data.models.aula

/**
 * Modelo para item de aula en lista/grid
 */
data class AulaListItem(
    val aula: AulaVirtual,
    val isSelected: Boolean = false,
    val showBadge: Boolean = false,
    val badgeText: String? = null,
    val badgeColor: String? = null
) {
    /**
     * Obtiene información resumida para mostrar en lista
     */
    fun getSummaryInfo(): String {
        val profesor = aula.getProfesorInfo()
        val estudiantes = if (aula.totalEstudiantes > 0) {
            "${aula.totalEstudiantes} estudiante${if (aula.totalEstudiantes != 1) "s" else ""}"
        } else {
            "Sin estudiantes"
        }
        return "$profesor • $estudiantes"
    }
}