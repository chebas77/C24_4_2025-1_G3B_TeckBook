package com.rodriguez.manuel.teckbookmovil.core.network

import com.rodriguez.manuel.teckbookmovil.core.config.Constants

/**
 * Wrapper genérico para todas las respuestas de la API
 * Proporciona un manejo consistente de éxito, error y loading
 */
sealed class ApiResponse<out T> {

    /**
     * Estado de carga
     */
    object Loading : ApiResponse<Nothing>()

    /**
     * Respuesta exitosa con datos
     */
    data class Success<T>(
        val data: T,
        val message: String? = null
    ) : ApiResponse<T>()

    /**
     * Respuesta de error
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: Constants.ErrorMessages.UNKNOWN_ERROR,
        val code: Int? = null
    ) : ApiResponse<Nothing>()

    /**
     * Respuesta de error de red
     */
    data class NetworkError(
        val message: String = Constants.ErrorMessages.NETWORK_ERROR
    ) : ApiResponse<Nothing>()

    /**
     * Respuesta de timeout
     */
    data class TimeoutError(
        val message: String = Constants.ErrorMessages.TIMEOUT_ERROR
    ) : ApiResponse<Nothing>()

    /**
     * Respuesta de token expirado
     */
    data class UnauthorizedError(
        val message: String = Constants.ErrorMessages.UNAUTHORIZED_ERROR
    ) : ApiResponse<Nothing>()
}

/**
 * Extensiones útiles para ApiResponse
 */

/**
 * Verifica si la respuesta es exitosa
 */
fun <T> ApiResponse<T>.isSuccess(): Boolean {
    return this is ApiResponse.Success
}

/**
 * Verifica si la respuesta es un error
 */
fun <T> ApiResponse<T>.isError(): Boolean {
    return this is ApiResponse.Error ||
            this is ApiResponse.NetworkError ||
            this is ApiResponse.TimeoutError ||
            this is ApiResponse.UnauthorizedError
}

/**
 * Verifica si está en estado de carga
 */
fun <T> ApiResponse<T>.isLoading(): Boolean {
    return this is ApiResponse.Loading
}

/**
 * Obtiene los datos si la respuesta es exitosa, null en caso contrario
 */
fun <T> ApiResponse<T>.getDataOrNull(): T? {
    return if (this is ApiResponse.Success) {
        this.data
    } else {
        null
    }
}

/**
 * Obtiene el mensaje de error si es un error, null en caso contrario
 */
fun <T> ApiResponse<T>.getErrorMessage(): String? {
    return when (this) {
        is ApiResponse.Error -> this.message
        is ApiResponse.NetworkError -> this.message
        is ApiResponse.TimeoutError -> this.message
        is ApiResponse.UnauthorizedError -> this.message
        else -> null
    }
}

/**
 * Ejecuta una acción si la respuesta es exitosa
 */
inline fun <T> ApiResponse<T>.onSuccess(action: (data: T) -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        action(this.data)
    }
    return this
}

/**
 * Ejecuta una acción si la respuesta es un error
 */
inline fun <T> ApiResponse<T>.onError(action: (message: String) -> Unit): ApiResponse<T> {
    val errorMessage = getErrorMessage()
    if (errorMessage != null) {
        action(errorMessage)
    }
    return this
}

/**
 * Ejecuta una acción si está en estado de carga
 */
inline fun <T> ApiResponse<T>.onLoading(action: () -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Loading) {
        action()
    }
    return this
}

/**
 * Transforma los datos de una respuesta exitosa
 */
inline fun <T, R> ApiResponse<T>.map(transform: (T) -> R): ApiResponse<R> {
    return when (this) {
        is ApiResponse.Success -> ApiResponse.Success(transform(this.data), this.message)
        is ApiResponse.Error -> this
        is ApiResponse.NetworkError -> this
        is ApiResponse.TimeoutError -> this
        is ApiResponse.UnauthorizedError -> this
        is ApiResponse.Loading -> this
    }
}

/**
 * Combina múltiples respuestas API
 */
fun <T1, T2, R> combineApiResponses(
    response1: ApiResponse<T1>,
    response2: ApiResponse<T2>,
    transform: (T1, T2) -> R
): ApiResponse<R> {
    return when {
        response1 is ApiResponse.Loading || response2 is ApiResponse.Loading -> {
            ApiResponse.Loading
        }
        response1 is ApiResponse.Success && response2 is ApiResponse.Success -> {
            ApiResponse.Success(transform(response1.data, response2.data))
        }
        response1.isError() -> {
            response1 as ApiResponse<R>
        }
        response2.isError() -> {
            response2 as ApiResponse<R>
        }
        else -> {
            ApiResponse.Error(Exception("Unknown error in combineApiResponses"))
        }
    }
}

/**
 * Factory para crear respuestas ApiResponse
 */
object ApiResponseFactory {

    fun <T> success(data: T, message: String? = null): ApiResponse<T> {
        return ApiResponse.Success(data, message)
    }

    fun error(throwable: Throwable, code: Int? = null): ApiResponse<Nothing> {
        return ApiResponse.Error(throwable, throwable.message ?: Constants.ErrorMessages.UNKNOWN_ERROR, code)
    }

    fun error(message: String, code: Int? = null): ApiResponse<Nothing> {
        return ApiResponse.Error(Exception(message), message, code)
    }

    fun networkError(message: String = Constants.ErrorMessages.NETWORK_ERROR): ApiResponse<Nothing> {
        return ApiResponse.NetworkError(message)
    }

    fun timeoutError(message: String = Constants.ErrorMessages.TIMEOUT_ERROR): ApiResponse<Nothing> {
        return ApiResponse.TimeoutError(message)
    }

    fun unauthorizedError(message: String = Constants.ErrorMessages.UNAUTHORIZED_ERROR): ApiResponse<Nothing> {
        return ApiResponse.UnauthorizedError(message)
    }

    fun loading(): ApiResponse<Nothing> {
        return ApiResponse.Loading
    }
}

/**
 * Manejo de errores específicos por código HTTP
 */
fun createErrorFromHttpCode(code: Int, message: String? = null): ApiResponse<Nothing> {
    val errorMessage = message ?: when (code) {
        400 -> Constants.ErrorMessages.VALIDATION_ERROR
        401 -> Constants.ErrorMessages.UNAUTHORIZED_ERROR
        403 -> Constants.ErrorMessages.FORBIDDEN_ERROR
        404 -> Constants.ErrorMessages.NOT_FOUND_ERROR
        500 -> Constants.ErrorMessages.SERVER_ERROR
        503 -> Constants.ErrorMessages.SERVER_ERROR
        else -> Constants.ErrorMessages.UNKNOWN_ERROR
    }

    return when (code) {
        401 -> ApiResponseFactory.unauthorizedError(errorMessage)
        in 500..599 -> ApiResponseFactory.error(errorMessage, code)
        else -> ApiResponseFactory.error(errorMessage, code)
    }
}