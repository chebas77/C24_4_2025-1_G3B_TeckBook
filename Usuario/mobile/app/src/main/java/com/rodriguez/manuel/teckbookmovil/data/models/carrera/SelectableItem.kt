package com.rodriguez.manuel.teckbookmovil.data.models.carrera

/**
 * Modelo para elementos seleccionables en spinners/dropdowns.
 */
data class SelectableItem(
    val id: Long,
    val name: String,
    val description: String? = null,
    val isEnabled: Boolean = true
) {
    override fun toString(): String {
        return name
    }

    companion object {
        /**
         * Convierte Departamento a SelectableItem.
         */
        fun fromDepartamento(departamento: Departamento): SelectableItem {
            return SelectableItem(
                id = departamento.id,
                name = departamento.nombre,
                description = departamento.codigo,
                isEnabled = departamento.activo
            )
        }

        /**
         * Convierte Carrera a SelectableItem.
         */
        fun fromCarrera(carrera: Carrera): SelectableItem {
            return SelectableItem(
                id = carrera.id,
                name = carrera.getNombreCompleto(),
                description = carrera.descripcion,
                isEnabled = carrera.activo
            )
        }

        /**
         * Convierte Ciclo a SelectableItem.
         */
        fun fromCiclo(ciclo: Ciclo): SelectableItem {
            return SelectableItem(
                id = ciclo.id,
                name = ciclo.getNombreFormateado(),
                isEnabled = true
            )
        }

        /**
         * Convierte Seccion a SelectableItem.
         */
        fun fromSeccion(seccion: Seccion): SelectableItem {
            return SelectableItem(
                id = seccion.id,
                name = seccion.getNombreCompleto(),
                description = seccion.getCicloInfo(),
                isEnabled = true
            )
        }
    }
}
