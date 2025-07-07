package com.rodriguez.manuel.teckbookmovil.data.models.carrera

/**
 * Datos para filtros en cascada: departamentos, carreras, ciclos y secciones.
 * Usado en pantallas como registro de usuario, creación de aulas, etc.
 */
data class CascadeFilterData(
    val departamentos: List<Departamento> = emptyList(),
    val carreras: List<Carrera> = emptyList(),
    val ciclos: List<Ciclo> = emptyList(),
    val secciones: List<Seccion> = emptyList()
) {

    /**
     * Verifica si todos los datos mínimos están cargados.
     */
    fun isComplete(): Boolean {
        return departamentos.isNotEmpty() && carreras.isNotEmpty() && ciclos.isNotEmpty()
    }

    /**
     * Filtra carreras por departamento activo.
     */
    fun getCarrerasByDepartamento(departamentoId: Long): List<Carrera> {
        return carreras.filter { it.departamentoId == departamentoId && it.activo }
    }

    /**
     * Filtra secciones por carrera y ciclo.
     */
    fun getSeccionesByCarreraAndCiclo(carreraId: Long, ciclo: Int): List<Seccion> {
        return secciones.filter { it.carreraId == carreraId && it.ciclo == ciclo }
    }

    /**
     * Obtiene los ciclos válidos para una carrera.
     */
    fun getCiclosForCarrera(carreraId: Long): List<Ciclo> {
        val carrera = carreras.find { it.id == carreraId }
        val maxCiclos = carrera?.getDuracionCiclosOrDefault() ?: 6
        return ciclos.filter { it.numero <= maxCiclos }
    }
}
