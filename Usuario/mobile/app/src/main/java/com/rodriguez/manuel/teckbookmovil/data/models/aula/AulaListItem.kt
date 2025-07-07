package com.rodriguez.manuel.teckbookmovil.data.models.aula

/**
 * Modelo para item de aula en listas o grids.
 * Es una capa de presentación para mostrar aulas con badges o estados de selección.
 */
data class AulaListItem(
    val aula: AulaVirtual,
    val isSelected: Boolean = false,
    val showBadge: Boolean = false,
    val badgeText: String? = null,
    val badgeColor: String? = null
) {
    /**
     * Devuelve información resumida del aula para mostrar en la lista.
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
