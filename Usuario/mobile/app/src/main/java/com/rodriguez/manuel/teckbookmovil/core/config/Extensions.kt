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
import com.rodriguez.manuel.teckbookmovil.core.config.Constants
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// ===============================
// 📧 EXTENSIONES PARA STRINGS
// ===============================

fun String.isValidInstitutionalEmail(): Boolean {
    return this.endsWith(Constants.Validation.EMAIL_DOMAIN) &&
            this.matches(Constants.Patterns.EMAIL_PATTERN.toRegex())
}

fun String.extractNameFromEmail(): String {
    return if (this.contains("@")) {
        this.substringBefore("@")
            .replace(".", " ")
            .split(" ")
            .joinToString(" ") { it.capitalize() }
    } else this
}

fun String.toTitleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun String?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) this else this.substring(0, maxLength - 3) + "..."
}

// ===============================
// 📅 EXTENSIONES PARA DATES Y LONG
// ===============================

fun Date.toFormattedString(pattern: String = Constants.Format.DATETIME_PATTERN): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = this
    return today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
}

fun Date.getRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this.time

    return when {
        diff < 60 * 1000 -> "Hace un momento"
        diff < 60 * 60 * 1000 -> "Hace ${diff / (60 * 1000)} minutos"
        diff < 24 * 60 * 60 * 1000 -> "Hace ${diff / (60 * 60 * 1000)} horas"
        diff < 7 * 24 * 60 * 60 * 1000 -> "Hace ${diff / (24 * 60 * 60 * 1000)} días"
        else -> this.toFormattedString(Constants.Format.DATE_PATTERN)
    }
}

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

// ===============================
// 👀 EXTENSIONES PARA VIEW
// ===============================

fun View.visible() = run { visibility = View.VISIBLE }
fun View.invisible() = run { visibility = View.INVISIBLE }
fun View.gone() = run { visibility = View.GONE }
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.setOnSingleClickListener(delayMs: Long = 1000L, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delayMs) {
            lastClickTime = currentTime
            action(it)
        }
    }
}

// ===============================
// ✏️ EXTENSIONES PARA EDITTEXT
// ===============================

fun EditText.textString(): String = text.toString()

fun EditText.isEmpty(): Boolean = textString().trim().isEmpty()

fun EditText.isNotEmpty(): Boolean = !isEmpty()

fun EditText.onTextChanged(action: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            action(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.validateInstitutionalEmail(onValidation: (Boolean) -> Unit) {
    onTextChanged { text -> onValidation(text.isValidInstitutionalEmail()) }
}

// ===============================
// 🖼️ EXTENSIONES PARA IMAGEVIEW (Glide)
// ===============================

fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_profile_placeholder,
    error: Int = R.drawable.ic_profile_placeholder
) {
    Glide.with(context).load(url).placeholder(placeholder).error(error).into(this)
}

fun ImageView.loadCircularImage(
    url: String?,
    placeholder: Int = R.drawable.ic_profile_placeholder,
    error: Int = R.drawable.ic_profile_placeholder
) {
    Glide.with(context).load(url).transform(CircleCrop()).placeholder(placeholder).error(error).into(this)
}

fun ImageView.loadProfileImage(url: String?) {
    loadCircularImage(url)
}

// ===============================
// ✅ EXTENSIONES PARA RESPONSE<>
// ===============================

fun <T> Response<T>.isSuccessful(): Boolean {
    return isSuccessful && body() != null
}

fun <T> Response<T>.getErrorMessage(): String {
    return when (code()) {
        401 -> Constants.ErrorMessages.UNAUTHORIZED_ERROR
        403 -> Constants.ErrorMessages.FORBIDDEN_ERROR
        404 -> Constants.ErrorMessages.NOT_FOUND_ERROR
        500 -> Constants.ErrorMessages.SERVER_ERROR
        else -> message() ?: Constants.ErrorMessages.UNKNOWN_ERROR
    }
}

// ===============================
// 📡 NETWORK Y TOASTS
// ===============================

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

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}
