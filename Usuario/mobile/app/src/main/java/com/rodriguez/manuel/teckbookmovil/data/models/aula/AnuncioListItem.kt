package com.rodriguez.manuel.teckbookmovil.data.models.aula

/**
 * Modelo para item de anuncio en lista
 */
data class AnuncioListItem(
    val anuncio: Anuncio,
    val isRead: Boolean = false,
    val isExpanded: Boolean = false,
    val showActions: Boolean = true
) {
    /**
     * Obtiene preview del contenido
     */
    fun getContentPreview(maxLength: Int = 150): String {
        return if (anuncio.contenido.length <= maxLength) {
            anuncio.contenido
        } else {
            anuncio.contenido.substring(0, maxLength) + "..."
        }
    }
}