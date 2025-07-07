package com.rodriguez.manuel.teckbookmovil.core.utils

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

        if (!email.endsWith(Constants.Validation.EMAIL_DOMAIN)) {
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

        val emailPattern = Pattern.compile(Constants.Patterns.EMAIL_PATTERN)

        if (!emailPattern.matcher(email).matches()) {
            return ValidationResult.error("Formato de correo inválido")
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES DE PASSWORD ==========

    fun validatePassword(password: String?): ValidationResult {
        if (password.isNullOrBlank()) {
            return ValidationResult.error("La contraseña es requerida")
        }

        if (password.length < Constants.Validation.MIN_PASSWORD_LENGTH) {
            return ValidationResult.error(Constants.ErrorMessages.WEAK_PASSWORD)
        }

        return ValidationResult.success()
    }

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

        val namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s\\-']+$")
        if (!namePattern.matcher(name).matches()) {
            return ValidationResult.error("$fieldName contiene caracteres no válidos")
        }

        return ValidationResult.success()
    }

    fun validateLastName(lastName: String?): ValidationResult {
        return validateName(lastName, "Apellidos")
    }

    // ========== VALIDACIONES DE TELÉFONO ==========

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

    fun validateOptionalPhone(phone: String?): ValidationResult {
        if (phone.isNullOrBlank()) {
            return ValidationResult.success()
        }

        return validatePhone(phone)
    }

    // ========== VALIDACIONES ACADÉMICAS ==========

    fun validateCycle(cycle: Int?): ValidationResult {
        if (cycle == null) {
            return ValidationResult.error("El ciclo es requerido")
        }

        if (cycle < Constants.AcademicCycles.MIN_CYCLE || cycle > Constants.AcademicCycles.MAX_CYCLE) {
            return ValidationResult.error("El ciclo debe estar entre ${Constants.AcademicCycles.MIN_CYCLE} y ${Constants.AcademicCycles.MAX_CYCLE}")
        }

        return ValidationResult.success()
    }

    fun validateCarreraId(carreraId: Long?): ValidationResult {
        if (carreraId == null || carreraId <= 0) {
            return ValidationResult.error("Debe seleccionar una carrera")
        }

        return ValidationResult.success()
    }

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

    fun validateAnuncioTitle(title: String?): ValidationResult {
        if (title.isNullOrBlank()) {
            return ValidationResult.error("El título es requerido")
        }

        if (title.length > Constants.Limits.MAX_ANUNCIO_TITLE_LENGTH) {
            return ValidationResult.error("El título no puede exceder ${Constants.Limits.MAX_ANUNCIO_TITLE_LENGTH} caracteres")
        }

        return ValidationResult.success()
    }

    fun validateAnuncioContent(content: String?): ValidationResult {
        if (content.isNullOrBlank()) {
            return ValidationResult.error("El contenido es requerido")
        }

        if (content.length > Constants.Limits.MAX_ANUNCIO_CONTENT_LENGTH) {
            return ValidationResult.error("El contenido no puede exceder ${Constants.Limits.MAX_ANUNCIO_CONTENT_LENGTH} caracteres")
        }

        return ValidationResult.success()
    }

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

    fun validateFileSize(sizeBytes: Long): ValidationResult {
        val maxSizeBytes = Constants.Validation.MAX_FILE_SIZE_MB * 1024 * 1024

        if (sizeBytes > maxSizeBytes) {
            return ValidationResult.error(Constants.ErrorMessages.IMAGE_TOO_LARGE)
        }

        return ValidationResult.success()
    }

    fun validateImageType(mimeType: String?): ValidationResult {
        if (mimeType.isNullOrBlank()) {
            return ValidationResult.error("Tipo de archivo no detectado")
        }

        if (!Constants.Validation.ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            return ValidationResult.error(Constants.ErrorMessages.INVALID_IMAGE_FORMAT)
        }

        return ValidationResult.success()
    }

    // ========== VALIDACIONES COMPUESTAS ==========

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

    fun validateLoginData(email: String?, password: String?): ValidationResult {
        validateInstitutionalEmail(email).takeIf { !it.isValid }?.let { return it }
        validatePassword(password).takeIf { !it.isValid }?.let { return it }

        return ValidationResult.success()
    }

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

    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null
    ) {
        companion object {
            fun success(message: String? = null) = ValidationResult(true, message)
            fun error(message: String) = ValidationResult(false, message)
        }
    }

    fun validateAll(vararg validations: ValidationResult): ValidationResult {
        validations.forEach { validation ->
            if (!validation.isValid) {
                return validation
            }
        }
        return ValidationResult.success()
    }

    inline fun validateCustom(condition: Boolean, lazyMessage: () -> String): ValidationResult {
        return if (condition) {
            ValidationResult.success()
        } else {
            ValidationResult.error(lazyMessage())
        }
    }
}
