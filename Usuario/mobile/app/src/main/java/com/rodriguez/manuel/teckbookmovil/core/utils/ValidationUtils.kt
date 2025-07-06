package com.rodriguez.manuel.teckbookmovil.core.utils

import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import com.rodriguez.manuel.teckbookmovil.core.config.Constants
import java.util.regex.Pattern

/**
 * Utilidades para validación de datos en TecBook
 * Centraliza todas las validaciones de la aplicación
 */
object ValidationUtils {

    // ========== VALIDACIONES DE EMAIL ==========

    /**
     * Valida que un email sea institucional de Tecsup
     */
    fun validateInstitutionalEmail(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.error("El correo electrónico es requerido")
        }

        if (!email.endsWith(AppConfig.Validation.EMAIL_DOMAIN)) {
            return ValidationResult.error(Constants.ErrorMessages.INVALID_EMAIL_DOMAIN)
        }

        val emailPattern = Pattern.compile(Constants.Patterns.EMAIL_PATTERN)
        if (!emailPattern.matcher(email).matches()) {
            return ValidationResult.error("Formato de correo inválido")
        }

        return ValidationResult.success()
    }

    /**
     * Valida formato básico de email
     */
    fun validateEmailFormat(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.error("El correo electrónico es requerido")
        }

        val emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )

        if (!emailPattern.matcher(email).matches()) {
            return ValidationResult.error("Formato de correo inválido")
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES DE PASSWORD ==========

    /**
     * Valida fortaleza de contraseña
     */
    fun validatePassword(password: String?): ValidationResult {
        if (password.isNullOrBlank()) {
            return ValidationResult.error("La contraseña es requerida")
        }

        if (password.length < AppConfig.Validation.MIN_PASSWORD_LENGTH) {
            return ValidationResult.error(Constants.ErrorMessages.WEAK_PASSWORD)
        }

        return ValidationResult.success()
    }

    /**
     * Valida que las contraseñas coincidan
     */
    fun validatePasswordConfirmation(password: String?, confirmPassword: String?): ValidationResult {
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return passwordValidation
        }

        if (password != confirmPassword) {
            return ValidationResult.error("Las contraseñas no coinciden")
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES DE NOMBRES ==========

    /**
     * Valida nombre de persona
     */
    fun validateName(name: String?, fieldName: String = "Nombre"): ValidationResult {
        if (name.isNullOrBlank()) {
            return ValidationResult.error("$fieldName es requerido")
        }

        if (name.length < 2) {
            return ValidationResult.error("$fieldName debe tener al menos 2 caracteres")
        }

        if (name.length > 50) {
            return ValidationResult.error("$fieldName no puede exceder 50 caracteres")
        }

        // Solo letras, espacios y algunos caracteres especiales
        val namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s\\-']+$")
        if (!namePattern.matcher(name).matches()) {
            return ValidationResult.error("$fieldName contiene caracteres no válidos")
        }

        return ValidationResult.success()
    }

    /**
     * Valida apellidos
     */
    fun validateLastName(lastName: String?): ValidationResult {
        return validateName(lastName, "Apellidos")
    }

    // ========== VALIDACIONES DE TELÉFONO ==========

    /**
     * Valida número de teléfono peruano
     */
    fun validatePhone(phone: String?): ValidationResult {
        if (phone.isNullOrBlank()) {
            return ValidationResult.error("El teléfono es requerido")
        }

        val phonePattern = Pattern.compile(Constants.Patterns.PHONE_PATTERN)
        if (!phonePattern.matcher(phone).matches()) {
            return ValidationResult.error("Formato de teléfono inválido (9 dígitos)")
        }

        return ValidationResult.success()
    }

    /**
     * Valida teléfono opcional
     */
    fun validateOptionalPhone(phone: String?): ValidationResult {
        if (phone.isNullOrBlank()) {
            return ValidationResult.success()
        }

        return validatePhone(phone)
    }

    // ========== VALIDACIONES ACADÉMICAS ==========

    /**
     * Valida ciclo académico
     */
    fun validateCycle(cycle: Int?): ValidationResult {
        if (cycle == null) {
            return ValidationResult.error("El ciclo es requerido")
        }

        if (cycle < Constants.AcademicCycles.MIN_CYCLE || cycle > Constants.AcademicCycles.MAX_CYCLE) {
            return ValidationResult.error("El ciclo debe estar entre ${Constants.AcademicCycles.MIN_CYCLE} y ${Constants.AcademicCycles.MAX_CYCLE}")
        }

        return ValidationResult.success()
    }

    /**
     * Valida ID de carrera
     */
    fun validateCarreraId(carreraId: Long?): ValidationResult {
        if (carreraId == null || carreraId <= 0) {
            return ValidationResult.error("Debe seleccionar una carrera")
        }

        return ValidationResult.success()
    }

    /**
     * Valida código de aula
     */
    fun validateAulaCode(code: String?): ValidationResult {
        if (code.isNullOrBlank()) {
            return ValidationResult.error("El código de aula es requerido")
        }

        val codePattern = Pattern.compile(Constants.Patterns.CODIGO_AULA_PATTERN)
        if (!codePattern.matcher(code).matches()) {
            return ValidationResult.error("Código de aula inválido")
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES DE CONTENIDO ==========

    /**
     * Valida título de anuncio
     */
    fun validateAnuncioTitle(title: String?): ValidationResult {
        if (title.isNullOrBlank()) {
            return ValidationResult.error("El título es requerido")
        }

        if (title.length > Constants.Limits.MAX_ANUNCIO_TITLE_LENGTH) {
            return ValidationResult.error("El título no puede exceder ${Constants.Limits.MAX_ANUNCIO_TITLE_LENGTH} caracteres")
        }

        return ValidationResult.success()
    }

    /**
     * Valida contenido de anuncio
     */
    fun validateAnuncioContent(content: String?): ValidationResult {
        if (content.isNullOrBlank()) {
            return ValidationResult.error("El contenido es requerido")
        }

        if (content.length > Constants.Limits.MAX_ANUNCIO_CONTENT_LENGTH) {
            return ValidationResult.error("El contenido no puede exceder ${Constants.Limits.MAX_ANUNCIO_CONTENT_LENGTH} caracteres")
        }

        return ValidationResult.success()
    }

    /**
     * Valida mensaje corto
     */
    fun validateMessage(message: String?): ValidationResult {
        if (message.isNullOrBlank()) {
            return ValidationResult.error("El mensaje es requerido")
        }

        if (message.length > Constants.Limits.MAX_MESSAGE_LENGTH) {
            return ValidationResult.error("El mensaje no puede exceder ${Constants.Limits.MAX_MESSAGE_LENGTH} caracteres")
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES DE ARCHIVOS ==========

    /**
     * Valida tamaño de archivo
     */
    fun validateFileSize(sizeBytes: Long): ValidationResult {
        val maxSizeBytes = AppConfig.Validation.MAX_FILE_SIZE_MB * 1024 * 1024

        if (sizeBytes > maxSizeBytes) {
            return ValidationResult.error(Constants.ErrorMessages.IMAGE_TOO_LARGE)
        }

        return ValidationResult.success()
    }

    /**
     * Valida tipo de imagen
     */
    fun validateImageType(mimeType: String?): ValidationResult {
        if (mimeType.isNullOrBlank()) {
            return ValidationResult.error("Tipo de archivo no detectado")
        }

        if (!AppConfig.Validation.ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            return ValidationResult.error(Constants.ErrorMessages.INVALID_IMAGE_FORMAT)
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES COMPUESTAS ==========

    /**
     * Valida datos completos de registro
     */
    fun validateRegistrationData(
        name: String?,
        lastName: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        carreraId: Long?,
        cycle: Int?
    ): ValidationResult {

        validateName(name).takeIf { !it.isValid }?.let { return it }
        validateLastName(lastName).takeIf { !it.isValid }?.let { return it }
        validateInstitutionalEmail(email).takeIf { !it.isValid }?.let { return it }
        validatePasswordConfirmation(password, confirmPassword).takeIf { !it.isValid }?.let { return it }
        validateCarreraId(carreraId).takeIf { !it.isValid }?.let { return it }
        validateCycle(cycle).takeIf { !it.isValid }?.let { return it }

        return ValidationResult.success("Datos válidos")
    }

    /**
     * Valida datos de login
     */
    fun validateLoginData(email: String?, password: String?): ValidationResult {
        validateInstitutionalEmail(email).takeIf { !it.isValid }?.let { return it }
        validatePassword(password).takeIf { !it.isValid }?.let { return it }

        return ValidationResult.success()
    }

    /**
     * Valida datos de perfil para actualización
     */
    fun validateProfileUpdate(
        name: String?,
        lastName: String?,
        phone: String?,
        cycle: Int?
    ): ValidationResult {

        validateName(name).takeIf { !it.isValid }?.let { return it }
        validateLastName(lastName).takeIf { !it.isValid }?.let { return it }
        validateOptionalPhone(phone).takeIf { !it.isValid }?.let { return it }
        cycle?.let { validateCycle(it).takeIf { !it.isValid }?.let { return it } }

        return ValidationResult.success("Datos de perfil válidos")
    }

    // ========== CLASE DE RESULTADO ==========

    /**
     * Resultado de validación
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null
    ) {
        companion object {
            fun success(message: String? = null) = ValidationResult(true, message)
            fun error(message: String) = ValidationResult(false, message)
        }
    }

    // ========== EXTENSIONES ÚTILES ==========

    /**
     * Extensión para validar múltiples campos
     */
    fun validateAll(vararg validations: ValidationResult): ValidationResult {
        validations.forEach { validation ->
            if (!validation.isValid) {
                return validation
            }
        }
        return ValidationResult.success()
    }

    /**
     * Extensión para validar con acción personalizada
     */
    inline fun validateCustom(condition: Boolean, lazyMessage: () -> String): ValidationResult {
        return if (condition) {
            ValidationResult.success()
        } else {
            ValidationResult.error(lazyMessage())
        }
    }
}