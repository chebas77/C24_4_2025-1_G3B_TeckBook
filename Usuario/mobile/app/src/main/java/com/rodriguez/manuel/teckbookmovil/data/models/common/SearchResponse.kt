package com.rodriguez.manuel.teckbookmovil.data.models.common

import com.google.gson.annotations.SerializedName

/**
 * Respuesta genérica para operaciones de búsqueda.
 */
data class SearchResponse<T>(
    @SerializedName("results")
    val results: List<T>,

    @SerializedName("query")
    val query: String,

    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("searchTime")
    val searchTime: Long? = null,

    @SerializedName("suggestions")
    val suggestions: List<String>? = null,

    @SerializedName("filters")
    val filters: Map<String, String>? = null
) {
    /** Verifica si hay resultados */
    fun hasResults(): Boolean = results.isNotEmpty()

    /** Verifica si hay sugerencias */
    fun hasSuggestions(): Boolean = !suggestions.isNullOrEmpty()
}
