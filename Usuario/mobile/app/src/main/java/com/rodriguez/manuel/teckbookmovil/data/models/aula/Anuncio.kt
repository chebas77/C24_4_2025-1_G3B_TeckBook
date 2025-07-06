package com.rodriguez.manuel.teckbookmovil.data.models.aula
import com.google.gson.annotations.SerializedName
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig

/**
 * Modelo de Anuncio
 * Basado en la entidad Anuncio del backend
 */
data class Anuncio(
    @SerializedName("id")
    val id: Long,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("contenido")
    val contenido: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("archivoUrl")
    val archivoUrl: String? = null,

    @SerializedName("archivoNombre")
    val archivoNombre: String? = null,

    @SerializedName("archivoTipo")
    val archivoTipo: String? = null,

    @SerializedName("archivoTamaño")
    val archivoTamaño: Long? = null,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("etiquetas")
    val etiquetas: String? = null,

    @SerializedName("permiteComentarios")
    val permiteComentarios: Boolean = true,

    @SerializedName("totalLikes")
    val totalLikes: Int = 0,

    @SerializedName("totalComentarios")
    val totalComentarios: Int = 0,

    @SerializedName("aulaId")
    val aulaId: Int? = null,

    @SerializedName("autorId")
    val autorId: Int,

    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String? = null,

    @SerializedName("fechaEdicion")
    val fechaEdicion: String? = null,

    @SerializedName("activo")
    val activo: Boolean = true,

    @SerializedName("fijado")
    val fijado: Boolean = false,

    @SerializedName("esGeneral")
    val esGeneral: Boolean = false
) {
    /**
     * Convierte tipo string a enum
     */
    fun getTipoEnum(): AppConfig.AnuncioType {
        return AppConfig.AnuncioType.fromString(tipo)
    }

    /**
     * Verifica si tiene archivo adjunto
     */
    fun hasAttachment(): Boolean {
        return !archivoUrl.isNullOrBlank()
    }

    /**
     * Verifica si es anuncio general
     */
    fun isGeneral(): Boolean {
        return esGeneral || aulaId == null
    }

    /**
     * Verifica si está fijado
     */
    fun isPinned(): Boolean {
        return fijado
    }

    /**
     * Obtiene el tamaño del archivo formateado
     */
    fun getFormattedFileSize(): String? {
        return archivoTamaño?.let { size ->
            when {
                size < 1024 -> "${size} B"
                size < 1024 * 1024 -> "${size / 1024} KB"
                else -> "${"%.2f".format(size / (1024.0 * 1024.0))} MB"
            }
        }
    }

    /**
     * Obtiene el ícono del tipo de anuncio
     */
    fun getTipoIcon(): String {
        return when (getTipoEnum()) {
            AppConfig.AnuncioType.INFORMATIVO -> "📢"
            AppConfig.AnuncioType.TAREA -> "📝"
            AppConfig.AnuncioType.EXAMEN -> "📊"
            AppConfig.AnuncioType.MATERIAL -> "📚"
            AppConfig.AnuncioType.EVENTO -> "📅"
        }
    }
}