package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
 * Estadísticas de usuario
 */
data class UserStats(
    @SerializedName("aulasInscritas")
    val aulasInscritas: Int,

    @SerializedName("anunciosLeidos")
    val anunciosLeidos: Int,

    @SerializedName("anunciosPendientes")
    val anunciosPendientes: Int,

    @SerializedName("invitacionesPendientes")
    val invitacionesPendientes: Int,

    @SerializedName("ultimaActividad")
    val ultimaActividad: String?,

    @SerializedName("notificacionesNoLeidas")
    val notificacionesNoLeidas: Int = 0
) {
    /**
     * Verifica si hay notificaciones pendientes
     */
    fun hasNotifications(): Boolean {
        return anunciosPendientes > 0 ||
                invitacionesPendientes > 0 ||
                notificacionesNoLeidas > 0
    }

    /**
     * Obtiene total de notificaciones
     */
    fun getTotalNotifications(): Int {
        return anunciosPendientes + invitacionesPendientes + notificacionesNoLeidas
    }
}