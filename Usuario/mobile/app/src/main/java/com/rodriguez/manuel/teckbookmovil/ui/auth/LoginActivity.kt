package com.rodriguez.manuel.teckbookmovil.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.rodriguez.manuel.teckbookmovil.TecBookApplication
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import com.rodriguez.manuel.teckbookmovil.core.config.Constants
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.core.utils.showToast
import com.rodriguez.manuel.teckbookmovil.core.utils.visible
import com.rodriguez.manuel.teckbookmovil.core.utils.gone
import com.rodriguez.manuel.teckbookmovil.data.repositories.AuthRepository
import com.rodriguez.manuel.teckbookmovil.databinding.ActivityLoginBinding
import com.rodriguez.manuel.teckbookmovil.ui.auth.viewmodels.AuthViewModel
import com.rodriguez.manuel.teckbookmovil.ui.main.MainActivity
import kotlinx.coroutines.launch
import com.rodriguez.manuel.teckbookmovil.core.network.ApiService

/**
 * Pantalla de login principal
 * - Login con Google OAuth2 (método principal)
 * - Login tradicional con email/password (fallback)
 * - Validación de dominio @tecsup.edu.pe
 * - Manejo de estados de carga y errores
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    // Launcher para Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            handleGoogleSignInResult(account)
        } catch (e: ApiException) {
            Logger.e("LoginActivity", "Google Sign-In falló", e)
            handleGoogleSignInError(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Logger.lifecycle("LoginActivity", "Activity", "onCreate")

        setupViewModel()
        setupGoogleSignIn()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val application = application as TecBookApplication

        val authRepository = AuthRepository(
            apiService = application.authenticatedRetrofit.create(ApiService::class.java),
            tokenManager = application.tokenManager
        )


        authViewModel = ViewModelProvider(
            this,
            AuthViewModel.Factory(authRepository) // 👈 Aquí debe coincidir con tu Factory
        )[AuthViewModel::class.java]
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(AppConfig.Google.WEB_CLIENT_ID)
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Logger.d("LoginActivity", "Google Sign-In configurado")
    }

    private fun setupUI() {
        // Configurar botón de Google Sign-In
        binding.btnGoogleSignIn.setOnClickListener {
            Logger.userAction("LoginActivity", "Google Sign-In button clicked")
            signInWithGoogle()
        }

        // Configurar login tradicional
        binding.btnTraditionalLogin.setOnClickListener {
            Logger.userAction("LoginActivity", "Traditional login button clicked")
            performTraditionalLogin()
        }

        // Configurar enlace de registro
        binding.tvRegister.setOnClickListener {
            Logger.userAction("LoginActivity", "Register link clicked")
            // TODO: Navegar a pantalla de registro
            showToast("Función de registro próximamente")
        }

        // Configurar validación en tiempo real del email
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmailField()
            }
        }

        Logger.d("LoginActivity", "UI configurada")
    }

    private fun observeViewModel() {
        // Observar estado de carga
        authViewModel.isLoading.observe(this) { isLoading ->
            updateLoadingState(isLoading)
        }

        // Observar resultado de login
        authViewModel.loginResult.observe(this) { result ->
            result?.let { response ->
                if (response.isSuccess) {
                    val loginResponse = response.getOrNull()
                    if (loginResponse != null) {
                        handleLoginSuccess(loginResponse)
                    }
                } else {
                    handleLoginError(response.exceptionOrNull()?.localizedMessage ?: "Error desconocido")
                }
            }
        }

        // Observar errores
        authViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                showToast(it)
                Logger.w("LoginActivity", "Error observado: $it")
            }
        }

        Logger.d("LoginActivity", "Observers configurados")
    }

    private fun signInWithGoogle() {
        Logger.auth("LoginActivity", "Iniciando Google Sign-In")

        // Limpiar cuenta anterior si existe
        googleSignInClient.signOut().addOnCompleteListener {
            // Lanzar intent de Google Sign-In
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            Logger.auth("LoginActivity", "Google Sign-In exitoso: ${account.email}")

            // Validar dominio institucional
            val email = account.email ?: ""
            if (!email.endsWith(AppConfig.Validation.EMAIL_DOMAIN)) {
                Logger.w("LoginActivity", "Email con dominio no válido: $email")
                showToast(Constants.ErrorMessages.INVALID_EMAIL_DOMAIN)
                googleSignInClient.signOut()
                return
            }

            // Procesar login con Google
            lifecycleScope.launch {
                try {
                    // Obtener ID Token
                    val idToken = account.idToken
                    if (idToken != null) {
                        // Procesar token con el backend
                        authViewModel.processGoogleAuth(idToken)
                    } else {
                        Logger.e("LoginActivity", "No se pudo obtener ID Token de Google")
                        showToast("Error obteniendo token de Google")
                    }
                } catch (e: Exception) {
                    Logger.e("LoginActivity", "Error procesando Google Sign-In", e)
                    showToast("Error procesando autenticación con Google")
                }
            }
        } else {
            Logger.w("LoginActivity", "Google Sign-In result null")
            showToast("Error en autenticación con Google")
        }
    }

    private fun handleGoogleSignInError(exception: ApiException) {
        val errorMessage = when (exception.statusCode) {
            12501 -> "Autenticación cancelada"
            12502 -> "Error de red. Verifica tu conexión"
            else -> "Error en autenticación con Google"
        }

        Logger.e("LoginActivity", "Google Sign-In error: ${exception.statusCode}")
        showToast(errorMessage)
    }

    private fun performTraditionalLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        Logger.auth("LoginActivity", "Intentando login tradicional para: $email")

        // Validar campos
        if (email.isEmpty()) {
            binding.etEmail.error = "Email requerido"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Contraseña requerida"
            return
        }

        // Validar dominio institucional
        if (!email.endsWith(AppConfig.Validation.EMAIL_DOMAIN)) {
            binding.etEmail.error = Constants.ErrorMessages.INVALID_EMAIL_DOMAIN
            return
        }

        // Realizar login
        authViewModel.login(email, password)
    }

    private fun validateEmailField() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isNotEmpty() && !email.endsWith(AppConfig.Validation.EMAIL_DOMAIN)) {
            binding.etEmail.error = Constants.ErrorMessages.INVALID_EMAIL_DOMAIN
        } else {
            binding.etEmail.error = null
        }
    }

    private fun handleLoginSuccess(loginResponse: com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginResponse) {
        Logger.auth("LoginActivity", "Login exitoso")

        showToast(Constants.SuccessMessages.LOGIN_SUCCESS)

        // Verificar si requiere completar datos
        if (loginResponse.requiresCompletion) {
            Logger.d("LoginActivity", "Usuario requiere completar datos")
            // TODO: Navegar a pantalla de completar perfil
            showToast("Redirigiendo para completar perfil...")
        }

        // Navegar a MainActivity
        navigateToMain()
    }

    private fun handleLoginError(errorMessage: String) {
        Logger.w("LoginActivity", "Login falló: $errorMessage")
        showToast(errorMessage)

        // Limpiar campos de contraseña en caso de error
        binding.etPassword.text?.clear()
    }

    private fun updateLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.btnGoogleSignIn.isEnabled = false
            binding.btnTraditionalLogin.isEnabled = false
        } else {
            binding.progressBar.gone()
            binding.btnGoogleSignIn.isEnabled = true
            binding.btnTraditionalLogin.isEnabled = true
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Logger.lifecycle("LoginActivity", "Acitivity","onStart")

        // Verificar si ya hay una cuenta de Google logueada
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            Logger.d("LoginActivity", "Cuenta de Google encontrada, verificando sesión")
            // Aquí podrías verificar si la sesión sigue siendo válida
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.lifecycle("LoginActivity", "Activity","onDestroy")
    }
}