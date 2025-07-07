package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta paginada genérica.
 * Compatible con backends que siguen la convención Spring Data Page.
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
    /** Verifica si hay más páginas disponibles */
    fun hasNext(): Boolean = !last

    /** Verifica si hay una página anterior */
    fun hasPrevious(): Boolean = !first

    /** Obtiene el número de la siguiente página, o null si no hay */
    fun getNextPage(): Int? = if (hasNext()) page + 1 else null

    /** Obtiene el número de la página anterior, o null si no hay */
    fun getPreviousPage(): Int? = if (hasPrevious()) page - 1 else null
}
