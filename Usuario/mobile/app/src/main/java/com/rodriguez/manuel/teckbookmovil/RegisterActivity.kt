package com.rodriguez.manuel.teckbookmovil;


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progress1: View
    private lateinit var progress2: View
    private lateinit var progress3: View

    // Contenedores de pasos
    private lateinit var step1Container: LinearLayout
    private lateinit var step2Container: LinearLayout
    private lateinit var step3Container: LinearLayout

    // Botones de navegación
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var tvBackToLogin: TextView

    // Campos del formulario - Paso 1
    private lateinit var etNombres: TextInputEditText
    private lateinit var etApellidos: TextInputEditText
    private lateinit var etCodigo: TextInputEditText
    private lateinit var etCorreoInstitucional: TextInputEditText

    // Campos del formulario - Paso 2
    private lateinit var etContraseña: TextInputEditText
    private lateinit var etCiclo: TextInputEditText
    private lateinit var etRol: TextInputEditText
    private lateinit var etDepartamentoId: TextInputEditText

    // Campos del formulario - Paso 3
    private lateinit var etCarreraId: TextInputEditText
    private lateinit var etSeccionId: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    // Control de pasos
    private var currentStep = 1
    private val totalSteps = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
        updateStepUI()
    }

    private fun initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvProgress = findViewById(R.id.tvProgress)
        progress1 = findViewById(R.id.progress1)
        progress2 = findViewById(R.id.progress2)
        progress3 = findViewById(R.id.progress3)

        // Contenedores
        step1Container = findViewById(R.id.step1Container)
        step2Container = findViewById(R.id.step2Container)
        step3Container = findViewById(R.id.step3Container)

        // Botones
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        // Campos Paso 1
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCodigo = findViewById(R.id.etCodigo)
        etCorreoInstitucional = findViewById(R.id.etCorreoInstitucional)

        // Campos Paso 2
        etContraseña = findViewById(R.id.etContraseña)
        etCiclo = findViewById(R.id.etCiclo)
        etRol = findViewById(R.id.etRol)
        etDepartamentoId = findViewById(R.id.etDepartamentoId)

        // Campos Paso 3
        etCarreraId = findViewById(R.id.etCarreraId)
        etSeccionId = findViewById(R.id.etSeccionId)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish() // Cierra la actividad y regresa
        }

        btnPrevious.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateStepUI()
            }
        }

        btnNext.setOnClickListener {
            if (validateCurrentStep()) {
                if (currentStep < totalSteps) {
                    currentStep++
                    updateStepUI()
                } else {
                    // Último paso - registrar usuario
                    registerUser()
                }
            }
        }

        tvBackToLogin.setOnClickListener {
            finish() // Regresa al login
        }
    }

    private fun updateStepUI() {
        // Actualizar indicador de progreso
        tvProgress.text = "Paso $currentStep de $totalSteps"

        // Actualizar barras de progreso
        updateProgressBars()

        // Mostrar/ocultar contenedores
        step1Container.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        step2Container.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        step3Container.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        // Actualizar títulos
        when (currentStep) {
            1 -> tvTitle.text = "Información Personal"
            2 -> tvTitle.text = "Información Académica"
            3 -> tvTitle.text = "Información de Contacto"
        }

        // Actualizar botones
        btnPrevious.visibility = if (currentStep == 1) View.GONE else View.VISIBLE
        btnNext.text = if (currentStep == totalSteps) "Registrar" else "Siguiente"

        // Ajustar el weight de los botones
        val layoutParams = btnNext.layoutParams as LinearLayout.LayoutParams
        if (currentStep == 1) {
            layoutParams.weight = 1f
            btnNext.layoutParams = layoutParams
        } else {
            layoutParams.weight = 1f
            btnNext.layoutParams = layoutParams
        }
    }

    private fun updateProgressBars() {
        val activeColor = getColor(R.color.primary_blue)
        val inactiveColor = getColor(R.color.gray_light)

        progress1.setBackgroundColor(if (currentStep >= 1) activeColor else inactiveColor)
        progress2.setBackgroundColor(if (currentStep >= 2) activeColor else inactiveColor)
        progress3.setBackgroundColor(if (currentStep >= 3) activeColor else inactiveColor)
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> validateStep1()
            2 -> validateStep2()
            3 -> validateStep3()
            else -> false
        }
    }

    private fun validateStep1(): Boolean {
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val codigo = etCodigo.text.toString().trim()
        val correoInstitucional = etCorreoInstitucional.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        return when {
            nombres.isEmpty() -> {
                etNombres.error = "Los nombres son requeridos"
                etNombres.requestFocus()
                false
            }
            apellidos.isEmpty() -> {
                etApellidos.error = "Los apellidos son requeridos"
                etApellidos.requestFocus()
                false
            }
            codigo.isEmpty() -> {
                etCodigo.error = "El código es requerido"
                etCodigo.requestFocus()
                false
            }
            correoInstitucional.isEmpty() -> {
                etCorreoInstitucional.error = "El correo institucional es requerido"
                etCorreoInstitucional.requestFocus()
                false
            }
            !isValidEmail(correoInstitucional) -> {
                etCorreoInstitucional.error = "Ingresa un correo válido"
                etCorreoInstitucional.requestFocus()
                false
            }
            contraseña.isEmpty() -> {
                etContraseña.error = "La contraseña es requerida"
                etContraseña.requestFocus()
                false
            }
            contraseña.length < 6 -> {
                etContraseña.error = "La contraseña debe tener al menos 6 caracteres"
                etContraseña.requestFocus()
                false
            }
            else -> {
                clearErrors(1)
                true
            }
        }
    }

    private fun validateStep2(): Boolean {
        val ciclo = etCiclo.text.toString().trim()
        val rol = etRol.text.toString().trim()
        val departamentoId = etDepartamentoId.text.toString().trim()
        val carreraId = etCarreraId.text.toString().trim()
        val seccionId = etSeccionId.text.toString().trim()

        return when {
            ciclo.isEmpty() -> {
                etCiclo.error = "El ciclo es requerido"
                etCiclo.requestFocus()
                false
            }
            rol.isEmpty() -> {
                etRol.error = "El rol es requerido"
                etRol.requestFocus()
                false
            }
            departamentoId.isEmpty() -> {
                etDepartamentoId.error = "El ID del departamento es requerido"
                etDepartamentoId.requestFocus()
                false
            }
            carreraId.isEmpty() -> {
                etCarreraId.error = "El ID de carrera es requerido"
                etCarreraId.requestFocus()
                false
            }
            seccionId.isEmpty() -> {
                etSeccionId.error = "El ID de sección es requerido"
                etSeccionId.requestFocus()
                false
            }
            else -> {
                clearErrors(2)
                true
            }
        }
    }

    private fun validateStep3(): Boolean {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val contraseñaOriginal = etContraseña.text.toString().trim()

        return when {
            email.isEmpty() -> {
                etEmail.error = "El email personal es requerido"
                etEmail.requestFocus()
                false
            }
            !isValidEmail(email) -> {
                etEmail.error = "Ingresa un email válido"
                etEmail.requestFocus()
                false
            }
            password.isEmpty() -> {
                etPassword.error = "Debes confirmar la contraseña"
                etPassword.requestFocus()
                false
            }
            password != contraseñaOriginal -> {
                etPassword.error = "Las contraseñas no coinciden"
                etPassword.requestFocus()
                false
            }
            else -> {
                clearErrors(3)
                true
            }
        }
    }

    private fun clearErrors(step: Int) {
        when (step) {
            1 -> {
                etNombres.error = null
                etApellidos.error = null
                etCodigo.error = null
                etCorreoInstitucional.error = null
                etContraseña.error = null
            }
            2 -> {
                etCiclo.error = null
                etRol.error = null
                etDepartamentoId.error = null
                etCarreraId.error = null
                etSeccionId.error = null
            }
            3 -> {
                etEmail.error = null
                etPassword.error = null
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser() {
        // Aquí iría la lógica de registro real
        Toast.makeText(this, "¡Registro exitoso! Implementar lógica de backend", Toast.LENGTH_LONG).show()

        // Por ahora, regresamos al login
        finish()
    }

    // Función para obtener todos los datos del formulario
    private fun getUserData(): Map<String, String> {
        return mapOf(
            "nombres" to etNombres.text.toString().trim(),
            "apellidos" to etApellidos.text.toString().trim(),
            "codigo" to etCodigo.text.toString().trim(),
            "correoInstitucional" to etCorreoInstitucional.text.toString().trim(),
            "contraseña" to etContraseña.text.toString().trim(),
            "ciclo" to etCiclo.text.toString().trim(),
            "rol" to etRol.text.toString().trim(),
            "departamentoId" to etDepartamentoId.text.toString().trim(),
            "carreraId" to etCarreraId.text.toString().trim(),
            "seccionId" to etSeccionId.text.toString().trim(),
            "email" to etEmail.text.toString().trim(),
            "password" to etPassword.text.toString().trim()
        )
    }
}