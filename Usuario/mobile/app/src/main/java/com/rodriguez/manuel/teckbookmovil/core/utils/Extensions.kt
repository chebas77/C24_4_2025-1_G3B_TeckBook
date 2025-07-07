package com.rodriguez.manuel.teckbookmovil.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.rodriguez.manuel.teckbookmovil.R
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import com.rodriguez.manuel.teckbookmovil.core.config.Constants
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// ========== EXTENSIONES PARA STRING ==========

/**
 * Verifica si es un email institucional válido
 */
fun String.isValidInstitutionalEmail(): Boolean {
    return this.endsWith(AppConfig.Validation.EMAIL_DOMAIN) &&
            this.matches(Constants.Patterns.EMAIL_PATTERN.toRegex())
}

/**
 * Extrae el nombre del usuario del email institucional
 */
fun String.extractNameFromEmail(): String {
    return if (this.contains("@")) {
        this.substringBefore("@").replace(".", " ").split(" ")
            .joinToString(" ") { it.capitalize() }
    } else {
        this
    }
}

/**
 * Capitaliza la primera letra de cada palabra
 */
fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

/**
 * Verifica si la string no es nula ni vacía
 */
fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * Limita la longitud de un string y agrega "..." si es necesario
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) {
        this
    } else {
        this.substring(0, maxLength - 3) + "..."
    }
}

/**
 * Formatea un tamaño de archivo
 */
fun Long.formatFileSize(): String {
    val sizeInMB = this / (1024.0 * 1024.0)
    return String.format(Constants.Format.FILE_SIZE_FORMAT, sizeInMB)
}

// ========== EXTENSIONES PARA DATE ==========

/**
 * Formatea una fecha a string legible
 */
fun Date.toFormattedString(pattern: String = Constants.Format.DATETIME_PATTERN): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Verifica si una fecha es de hoy
 */
fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = this

    return today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
}

/**
 * Obtiene tiempo relativo (hace 2 horas, ayer, etc.)
 */
fun Date.getRelativeTime(): String {
    val now = System.currentTimeMillis()
    val time = this.time
    val diff = now - time

    return when {
        diff < 60 * 1000 -> "Hace un momento"
        diff < 60 * 60 * 1000 -> "Hace ${diff / (60 * 1000)} minutos"
        diff < 24 * 60 * 60 * 1000 -> "Hace ${diff / (60 * 60 * 1000)} horas"
        diff < 7 * 24 * 60 * 60 * 1000 -> "Hace ${diff / (24 * 60 * 60 * 1000)} días"
        else -> this.toFormattedString(Constants.Format.DATE_PATTERN)
    }
}

// ========== EXTENSIONES PARA VIEW ==========

/**
 * Hace visible una vista
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Hace invisible una vista (ocupa espacio)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Oculta una vista (no ocupa espacio)
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Cambia visibilidad basado en condición
 */
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * Establece un click listener con debounce para evitar clicks múltiples
 */
fun View.setOnSingleClickListener(delayMs: Long = 1000L, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delayMs) {
            lastClickTime = currentTime
            action(view)
        }
    }
}

// ========== EXTENSIONES PARA EDITTEXT ==========

/**
 * Obtiene el texto como String (nunca null)
 */
fun EditText.textString(): String {
    return this.text.toString()
}

/**
 * Verifica si el EditText está vacío
 */
fun EditText.isEmpty(): Boolean {
    return this.textString().trim().isEmpty()
}

/**
 * Verifica si el EditText no está vacío
 */
fun EditText.isNotEmpty(): Boolean {
    return !isEmpty()
}

/**
 * Agrega un TextWatcher simple para cambios de texto
 */
fun EditText.onTextChanged(action: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            action(s.toString())
        }
    })
}

/**
 * Valida email institucional en tiempo real
 */
fun EditText.validateInstitutionalEmail(onValidation: (Boolean) -> Unit) {
    onTextChanged { text ->
        val isValid = text.isValidInstitutionalEmail()
        onValidation(isValid)
    }
}

// ========== EXTENSIONES PARA IMAGEVIEW ==========

/**
 * Carga imagen desde URL usando Glide
 */
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_profile_placeholder,
    error: Int = R.drawable.ic_profile_placeholder
) {
    Glide.with(this.context)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

/**
 * Carga imagen circular desde URL
 */
fun ImageView.loadCircularImage(
    url: String?,
    placeholder: Int = R.drawable.ic_profile_placeholder,
    error: Int = R.drawable.ic_profile_placeholder
) {
    Glide.with(this.context)
        .load(url)
        .transform(CircleCrop())
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

/**
 * Carga imagen de perfil desde URL o usa placeholder
 */
fun ImageView.loadProfileImage(url: String?) {
    loadCircularImage(
        url = url,
        placeholder = R.drawable.ic_profile_placeholder,
        error = R.drawable.ic_profile_placeholder
    )
}

// ========== EXTENSIONES PARA RETROFIT RESPONSE ==========

/**
 * Verifica si la respuesta es exitosa
 */
fun <T> Response<T>.isSuccessful(): Boolean {
    return this.isSuccessful && this.body() != null
}

/**
 * Obtiene el mensaje de error de una respuesta fallida
 */
fun <T> Response<T>.getErrorMessage(): String {
    return when (this.code()) {
        401 -> Constants.ErrorMessages.UNAUTHORIZED_ERROR
        403 -> Constants.ErrorMessages.FORBIDDEN_ERROR
        404 -> Constants.ErrorMessages.NOT_FOUND_ERROR
        500 -> Constants.ErrorMessages.SERVER_ERROR
        else -> this.message() ?: Constants.ErrorMessages.UNKNOWN_ERROR
    }
}

// ========== EXTENSIONES PARA COLLECTIONS ==========

/**
 * Encuentra un elemento de forma segura
 */
fun <T> List<T>.findSafe(predicate: (T) -> Boolean): T? {
    return try {
        this.find(predicate)
    } catch (e: Exception) {
        Logger.e("Extensions", "Error finding element in list", e)
        null
    }
}

/**
 * Filtra de forma segura
 */
fun <T> List<T>.filterSafe(predicate: (T) -> Boolean): List<T> {
    return try {
        this.filter(predicate)
    } catch (e: Exception) {
        Logger.e("Extensions", "Error filtering list", e)
        emptyList()
    }
}

// ========== EXTENSIONES PARA ENUM ==========

/**
 * Convierte enum a string de forma segura
 */
fun <T : Enum<T>> T.toStringValue(): String {
    return this.name.lowercase()
}

// ========== EXTENSIONES PARA VALIDACIÓN ==========

/**
 * Valida que un campo no esté vacío
 */
fun String?.validateNotEmpty(fieldName: String): String {
    if (this.isNullOrBlank()) {
        throw ValidationException("$fieldName no puede estar vacío")
    }
    return this.trim()
}

/**
 * Valida longitud mínima
 */
fun String.validateMinLength(minLength: Int, fieldName: String): String {
    if (this.length < minLength) {
        throw ValidationException("$fieldName debe tener al menos $minLength caracteres")
    }
    return this
}

/**
 * Valida email institucional
 */
fun String.validateInstitutionalEmail(): String {
    if (!this.isValidInstitutionalEmail()) {
        throw ValidationException(Constants.ErrorMessages.INVALID_EMAIL_DOMAIN)
    }
    return this
}

/**
 * Excepción personalizada para validaciones
 */
class ValidationException(message: String) : Exception(message)

// ========== EXTENSIONES PARA NÚMERO ==========

/**
 * Convierte milisegundos a formato legible
 */
fun Long.toReadableDuration(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}

/**
 * Convierte bytes a formato legible
 */
fun Long.toReadableBytes(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> "%.2f GB".format(gb)
        mb >= 1 -> "%.2f MB".format(mb)
        kb >= 1 -> "%.2f KB".format(kb)
        else -> "$this B"
    }
}

/**
 * Muestra un Toast corto
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Muestra un Toast largo
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Muestra un Toast desde un string resource
 */
fun Context.showToast(@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}

/**
 * Verifica si hay conexión a internet
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }
}

//