package com.rodriguez.manuel.teckbookmovil.core.config

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Módulo obligatorio para Glide cuando usas kapt.
 * Esto permite que Glide genere la clase GlideApp automáticamente.
 */
@GlideModule
class TecBookGlideModule : AppGlideModule() {
    // Puedes dejarlo vacío, Glide se encarga de generar lo necesario.
}
