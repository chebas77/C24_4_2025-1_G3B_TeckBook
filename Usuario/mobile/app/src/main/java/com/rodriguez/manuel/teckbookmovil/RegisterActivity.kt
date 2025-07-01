package com.rodriguez.manuel.teckbookmovil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.rodriguez.manuel.teckbookmovil.data.models.*
import com.rodriguez.manuel.teckbookmovil.data.network.NetworkModule
import com.rodriguez.manuel.teckbookmovil.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Elementos de la UI - mismos que antes
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progress1: View
    private lateinit var progress2: View
    private lateinit var progress3: View

    private lateinit var step1Container: LinearLayout
    private lateinit var step2Container: LinearLayout
    private lateinit var step3Container: LinearLayout

    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var tvBackToLogin: TextView

    // Campos del formulario
    private lateinit var etNombres: TextInputEditText
    private lateinit var etApellidos: TextInputEditText
    private lateinit var etCodigo: TextInputEditText
    private lateinit var etCorreoInstitucional: TextInputEditText
    private lateinit var etContraseña: TextInputEditText
    private lateinit var etCiclo: TextInputEditText
    private lateinit var etRol: TextInputEditText
    private lateinit var etDepartamentoId: TextInputEditText
    private lateinit var etCarreraId: TextInputEditText
    private lateinit var etSeccionId: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    // Control de pasos
    private var currentStep = 1
    private val totalSteps = 3

    // 🔐 Repositorio de autenticación
    private lateinit var authRepository: AuthRepository

    // 📋 Datos para cargar dinámicamente
    private var carreras: List<Carrera> = emptyList()
    private var departamentos: List<Departamento> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository(this)

        initViews()
        setupClickListeners()
        updateStepUI()

        // 🔥 CARGAR DATOS DINÁMICOS DESDE LA API
        loadInitialData()
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
        etContraseña = findViewById(R.id.etContraseña)

        // Campos Paso 2
        etCiclo = findViewById(R.id.etCiclo)
        etRol = findViewById(R.id.etRol)
        etDepartamentoId = findViewById(R.id.etDepartamentoId)
        etCarreraId = findViewById(R.id.etCarreraId)
        etSeccionId = findViewById(R.id.etSeccionId)

        // Campos Paso 3
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

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
                    // Último paso - registrar usuario CON API
                    registerUserWithAPI()
                }
            }
        }

        tvBackToLogin.setOnClickListener { finish() }
    }

    /**
     * 🔥 CARGAR DATOS INICIALES DESDE LA API
     */
    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                // Cargar carreras activas
                val carrerasResponse = NetworkModule.apiService.getCarrerasActivas()
                if (carrerasResponse.isSuccessful) {
                    carreras = carrerasResponse.body()?.carreras ?: emptyList()
                    setupCarrerasHints()
                }

                // Cargar departamentos activos
                val departamentosResponse = NetworkModule.apiService.getDepartamentosActivos()
                if (departamentosResponse.isSuccessful) {
                    departamentos = departamentosResponse.body()?.departamentos ?: emptyList()
                    setupDepartamentosHints()
                }

            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity,
                    "Error al cargar datos: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 🎯 CONFIGURAR HINTS DINÁMICOS
     */
    private fun setupCarrerasHints() {
        if (carreras.isNotEmpty()) {
            val primerasCarreras = carreras.take(3).joinToString(", ") { "${it.id}-${it.codigo ?: it.nombre.take(10)}" }
            etCarreraId.hint = "ID Carrera (ej: $primerasCarreras)"
        }
    }

    private fun setupDepartamentosHints() {
        if (departamentos.isNotEmpty()) {
            val primerosDep = departamentos.take(2).joinToString(", ") { "${it.id}-${it.codigo ?: it.nombre.take(10)}" }
            etDepartamentoId.hint = "ID Depto (ej: $primerosDep)"
        }
    }

    /**
     * 📝 REGISTRAR USUARIO CON LA API
     */
    private fun registerUserWithAPI() {
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                // Crear objeto Usuario con todos los datos
                val usuario = createUsuarioFromForm()

                // Llamar a la API de registro
                val result = authRepository.register(usuario)

                when (result) {
                    is Resource.Success -> {
                        setLoadingState(false)

                        // Registro exitoso
                        Toast.makeText(this@RegisterActivity,
                            "¡Registro exitoso! Ahora puedes iniciar sesión",
                            Toast.LENGTH_LONG).show()

                        // Regresar al login
                        finish()
                    }

                    is Resource.Error -> {
                        setLoadingState(false)
                        showError(result.message)
                    }

                    is Resource.Loading -> {
                        // Loading ya manejado
                    }
                }

            } catch (e: Exception) {
                setLoadingState(false)
                showError("Error inesperado: ${e.message}")
            }
        }
    }

    /**
     * 👤 CREAR OBJETO USUARIO DESDE EL FORMULARIO
     */
    private fun createUsuarioFromForm(): Usuario {
        return Usuario(
            id = 0, // Se asigna automáticamente en el backend
            nombre = etNombres.text.toString().trim(),
            apellidos = etApellidos.text.toString().trim(),
            correoInstitucional = etCorreoInstitucional.text.toString().trim(),
            rol = etRol.text.toString().trim().lowercase(),
            cicloActual = etCiclo.text.toString().trim().toIntOrNull(),
            departamentoId = etDepartamentoId.text.toString().trim().toLongOrNull(),
            carreraId = etCarreraId.text.toString().trim().toLongOrNull(),
            seccionId = if (etSeccionId.text.toString().trim().isEmpty()) null else etSeccionId.text.toString().trim().toLongOrNull(),
            telefono = null, // Se puede agregar después
            profileImageUrl = null
        )
    }

    /**
     * 🔄 Gestionar estado de loading
     */
    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            btnNext.isEnabled = false
            btnNext.text = "Registrando..."
            btnPrevious.isEnabled = false
        } else {
            btnNext.isEnabled = true
            btnNext.text = if (currentStep == totalSteps) "Registrar" else "Siguiente"
            btnPrevious.isEnabled = true
        }
    }

    /**
     * ⚠️ Mostrar mensaje de error
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // ========== RESTO DE MÉTODOS (UI LOGIC) - MISMOS QUE ANTES ==========

    private fun updateStepUI() {
        tvProgress.text = "Paso $currentStep de $totalSteps"
        updateProgressBars()

        step1Container.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        step2Container.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        step3Container.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        when (currentStep) {
            1 -> tvTitle.text = "Información Personal"
            2 -> tvTitle.text = "Información Académica"
            3 -> tvTitle.text = "Información de Contacto"
        }

        btnPrevious.visibility = if (currentStep == 1) View.GONE else View.VISIBLE
        btnNext.text = if (currentStep == totalSteps) "Registrar" else "Siguiente"
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
            !correoInstitucional.endsWith("@tecsup.edu.pe") -> {
                etCorreoInstitucional.error = "Debe ser un correo institucional (@tecsup.edu.pe)"
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

        return when {
            ciclo.isEmpty() -> {
                etCiclo.error = "El ciclo es requerido"
                etCiclo.requestFocus()
                false
            }
            rol.isEmpty() -> {
                etRol.error = "El rol es requerido (estudiante/profesor)"
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
}