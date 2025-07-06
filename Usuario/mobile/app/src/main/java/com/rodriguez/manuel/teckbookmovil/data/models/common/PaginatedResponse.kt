package com.rodriguez.manuel.teckbookmovil.data.models.common
import com.google.gson.annotations.SerializedName

/**
 * Respuesta paginada genérica
 */
data class PaginatedResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("totalElements")
    val totalElements: Long,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("first")
    val first: Boolean,

    @SerializedName("last")
    val last: Boolean,

    @SerializedName("empty")
    val empty: Boolean
) {
    /**
     * Verifica si hay más páginas
     */
    fun hasNext(): Boolean {
        return !last
    }

    /**
     * Verifica si hay página anterior
     */
    fun hasPrevious(): Boolean {
        return !first
    }

    /**
     * Obtiene el número de la siguiente página
     */
    fun getNextPage(): Int? {
        return if (hasNext()) page + 1 else null
    }

    /**
     * Obtiene el número de la página anterior
     */
    fun getPreviousPage(): Int? {
        return if (hasPrevious()) page - 1 else null
    }
}