package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName

/**
 * Modelo de Estudiante en Aula
 * Basado en la entidad AulaEstudiante del backend
 */
data class AulaEstudiante(
    @SerializedName("id")
    val id: Long,

    @SerializedName("aulaId")
    val aulaId: Long,

    @SerializedName("estudianteId")
    val estudianteId: Long,

    @SerializedName("estado")
    val estado: String = "activo",

    @SerializedName("fechaUnion")
    val fechaUnion: String? = null,

    @SerializedName("fechaSalida")
    val fechaSalida: String? = null,

    // Campos adicionales del controller
    @SerializedName("nombre")
    val nombre: String? = null,

    @SerializedName("apellidos")
    val apellidos: String? = null,

    @SerializedName("email")
    val email: String? = null
) {
    /**
     * Estados posibles del estudiante en aula
     */
    enum class EstadoEstudiante(val value: String) {
        INVITADO("invitado"),
        ACTIVO("activo"),
        INACTIVO("inactivo");

        companion object {
            fun fromString(value: String?): EstadoEstudiante {
                return values().find { it.value.equals(value, ignoreCase = true) } ?: ACTIVO
            }
        }
    }

    /**
     * Convierte estado string a enum
     */
    fun getEstadoEnum(): EstadoEstudiante {
        return EstadoEstudiante.fromString(estado)
    }

    /**
     * Verifica si está activo en el aula
     */
    fun isActivo(): Boolean {
        return getEstadoEnum() == EstadoEstudiante.ACTIVO
    }

    /**
     * Obtiene el nombre completo del estudiante
     */
    fun getNombreCompleto(): String {
        return if (!nombre.isNullOrBlank() && !apellidos.isNullOrBlank()) {
            "$nombre $apellidos"
        } else {
            email ?: "Estudiante"
        }
    }
}