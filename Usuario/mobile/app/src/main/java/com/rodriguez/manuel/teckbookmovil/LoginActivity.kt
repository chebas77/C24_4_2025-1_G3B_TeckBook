package com.rodriguez.manuel.teckbookmovil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.compiler.processing.util.Resource
import com.bumptech.glide.load.engine.Resource
import com.google.android.material.textfield.TextInputEditText
import com.rodriguez.manuel.teckbookmovil.data.models.Resource
import com.rodriguez.manuel.teckbookmovil.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnCreateAccount: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var tvForgotPassword: TextView

    // 🔐 Repositorio de autenticación
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar repositorio
        authRepository = AuthRepository(this)

        // Verificar si ya hay una sesión activa
        checkExistingSession()

        // Inicializar UI
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
    }

    private fun setupClickListeners() {

        // 🔐 Botón de iniciar sesión - CONECTADO CON API
        btnLogin.setOnClickListener {
            if (validateFields()) {
                performLogin()
            }
        }

        // 📝 Botón de crear cuenta
        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 🌐 Botón de Google Sign In
        btnGoogleSignIn.setOnClickListener {
            Toast.makeText(this, "Google Sign-In: próximamente disponible", Toast.LENGTH_SHORT).show()
        }

        // 🔒 Enlace de "¿Olvidaste tu contraseña?"
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperar contraseña: próximamente disponible", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 🔐 REALIZAR LOGIN CON LA API
     */
    private fun performLogin() {
        val email = getEmailText()
        val password = getPasswordText()

        // Mostrar loading
        setLoadingState(true)

        // Llamar a la API usando corrutinas
        lifecycleScope.launch {
            try {
                val result = authRepository.login(email, password)

                when (result) {
                    is Resource.Success -> {
                        setLoadingState(false)

                        val loginResponse = result.data

                        // Verificar si requiere completar datos
                        if (loginResponse.requiresCompletion) {
                            Toast.makeText(this@LoginActivity,
                                "Perfil incompleto. Completa tus datos.",
                                Toast.LENGTH_LONG).show()
                            navigateToHome()
                        } else {
                            // Login exitoso, navegar al home
                            Toast.makeText(this@LoginActivity,
                                "¡Bienvenido ${loginResponse.user?.nombre}!",
                                Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
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
     * ✅ Verificar si ya existe una sesión activa
     */
    private fun checkExistingSession() {
        if (authRepository.isLoggedIn()) {
            // Ya hay una sesión activa, verificar validez del token
            lifecycleScope.launch {
                val result = authRepository.getUserInfo()
                when (result) {
                    is Resource.Success -> {
                        // Token válido, navegar directamente al home
                        navigateToHome()
                    }
                    is Resource.Error -> {
                        // Token inválido, permitir login normal
                        authRepository.logout()
                    }
                    is Resource.Loading -> {
                        setLoadingState(true)
                    }
                }
            }
        }
    }

    /**
     * 🏠 Navegar a la pantalla principal
     */
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * ⚠️ Mostrar mensaje de error
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        // Limpiar campos si es error de credenciales
        if (message.contains("credenciales", ignoreCase = true) ||
            message.contains("incorrectos", ignoreCase = true)) {
            etPassword.text?.clear()
            etPassword.requestFocus()
        }
    }

    /**
     * 🔄 Gestionar estado de loading
     */
    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            btnLogin.isEnabled = false
            btnLogin.text = "Iniciando sesión..."
            btnCreateAccount.isEnabled = false
            btnGoogleSignIn.isEnabled = false
        } else {
            btnLogin.isEnabled = true
            btnLogin.text = "Iniciar sesión"
            btnCreateAccount.isEnabled = true
            btnGoogleSignIn.isEnabled = true
        }
    }

    // ========== MÉTODOS DE VALIDACIÓN ==========

    private fun getEmailText(): String {
        return etEmail.text.toString().trim()
    }

    private fun getPasswordText(): String {
        return etPassword.text.toString().trim()
    }

    private fun validateFields(): Boolean {
        val email = getEmailText()
        val password = getPasswordText()

        // Limpiar errores previos
        etEmail.error = null
        etPassword.error = null

        return when {
            email.isEmpty() -> {
                etEmail.error = "El email es requerido"
                etEmail.requestFocus()
                false
            }
            !isValidEmail(email) -> {
                etEmail.error = "Ingresa un email válido"
                etEmail.requestFocus()
                false
            }
            !email.endsWith("@tecsup.edu.pe") -> {
                etEmail.error = "Usa tu correo institucional (@tecsup.edu.pe)"
                etEmail.requestFocus()
                false
            }
            password.isEmpty() -> {
                etPassword.error = "La contraseña es requerida"
                etPassword.requestFocus()
                false
            }
            password.length < 6 -> {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                etPassword.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}