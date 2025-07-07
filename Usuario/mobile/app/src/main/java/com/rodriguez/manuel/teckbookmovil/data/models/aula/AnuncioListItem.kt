package com.rodriguez.manuel.teckbookmovil.data.models.aula

/**
 * Modelo para item de anuncio en lista
 * Mantiene estado de UI asociado a un anuncio.
 */
data class AnuncioListItem(
    val anuncio: Anuncio,
    val isRead: Boolean = false,
    val isExpanded: Boolean = false,
    val showActions: Boolean = true
) {

    /**
     * Obtiene preview del contenido del anuncio.
     * Usa extensión .truncate() si ya la tienes, o lo hace manualmente.
     */
    fun getContentPreview(maxLength: Int = 150): String {
        return anuncio.contenido.truncate(maxLength)
    }
}

/**
 * Extensión para truncar strings con "..." si excede maxLength.
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) {
        this
    } else {
        this.substring(0, maxLength).trimEnd() + "..."
    }
}
