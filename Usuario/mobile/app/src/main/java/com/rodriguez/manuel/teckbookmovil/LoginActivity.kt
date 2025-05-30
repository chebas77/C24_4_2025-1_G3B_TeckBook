package com.rodriguez.manuel.teckbookmovil;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    // Declaración de elementos de la UI usando lateinit
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnCreateAccount: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar elementos de la UI
        initViews()

        // Configurar listeners (solo para mostrar toasts por ahora)
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

        // Botón de iniciar sesión
        btnLogin.setOnClickListener {
            // Por ahora solo mostramos un mensaje
            Toast.makeText(this, "Funcionalidad de login en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Botón de crear cuenta
        btnCreateAccount.setOnClickListener {
            // Navegar a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Botón de Google Sign In
        btnGoogleSignIn.setOnClickListener {
            Toast.makeText(this, "Ingreso con Google en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Enlace de "¿Olvidaste tu contraseña?"
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperar contraseña en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para obtener los valores de los campos (para futuras implementaciones)
    private fun getEmailText(): String {
        return etEmail.text.toString().trim()
    }

    private fun getPasswordText(): String {
        return etPassword.text.toString().trim()
    }

    // Función para validar campos (preparada para futuras implementaciones)
    private fun validateFields(): Boolean {
        val email = getEmailText()
        val password = getPasswordText()

        return when {
            email.isEmpty() -> {
                etEmail.error = "El email es requerido"
                false
            }
            password.isEmpty() -> {
                etPassword.error = "La contraseña es requerida"
                false
            }
            password.length < 6 -> {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            else -> true
        }
    }
}