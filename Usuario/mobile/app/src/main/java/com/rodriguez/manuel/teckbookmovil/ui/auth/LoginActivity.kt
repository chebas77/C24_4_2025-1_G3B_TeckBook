package com.rodriguez.manuel.teckbookmovil.ui.auth

import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.core.utils.showToast
import com.rodriguez.manuel.teckbookmovil.databinding.ActivityLoginBinding
import androidx.browser.customtabs.CustomTabsIntent

/**
 * LoginActivity ajustada para el flujo correcto OAuth2.
 * Se abre la URL de autorización directamente con Custom Tabs.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Logger.lifecycle("LoginActivity", "onCreate")

        setupUI()
    }

    private fun setupUI() {
        // Google Sign-In abre flujo OAuth2 (NO usa GoogleSignInClient local)
        binding.btnGoogleSignIn.setOnClickListener {
            Logger.userAction("LoginActivity", "Google Sign-In button clicked")
            launchOAuth2Flow()
        }

        // Botón de login tradicional (puedes dejarlo igual si lo usas)
        binding.btnTraditionalLogin.setOnClickListener {
            showToast("Login tradicional no disponible aún")
        }
    }

    /**
     * Abre el navegador integrado (Custom Tabs) con la URL OAuth2.
     */
    private fun launchOAuth2Flow() {
        Logger.auth("LoginActivity - Iniciando flujo OAuth2 Google")

        val oauth2Url = AppConfig.getOAuth2GoogleUrl()


        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(oauth2Url))
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.lifecycle("LoginActivity", "onDestroy")
    }
}
