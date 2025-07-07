package com.rodriguez.manuel.teckbookmovil.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rodriguez.manuel.teckbookmovil.R
import com.rodriguez.manuel.teckbookmovil.TecBookApplication
import com.rodriguez.manuel.teckbookmovil.core.config.Constants
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de splash que se muestra al iniciar la app
 * - Verifica si el usuario está logueado
 * - Redirige a Login o MainActivity según corresponda
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Logger.lifecycle("SplashActivity",  "onCreate")

        initializeApp()
    }

    private fun initializeApp() {
        lifecycleScope.launch {
            try {
                Logger.d("SplashActivity", "Inicializando aplicación")

                // Tiempo mínimo del splash
                delay(Constants.Animation.SPLASH_DELAY)

                val tokenManager = TecBookApplication.getTokenManager(this@SplashActivity)

                if (tokenManager.isLoggedIn()) {
                    Logger.auth("Usuario logueado, redirigiendo a MainActivity")
                    navigateToMain()
                } else {
                    Logger.auth("Usuario no logueado, redirigiendo a Login")
                    navigateToLogin()
                }

            } catch (e: Exception) {
                Logger.e("SplashActivity", "Error en inicialización", e)
                navigateToLogin()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.lifecycle("SplashActivity","onDestroy")
    }
}
