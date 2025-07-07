package com.rodriguez.manuel.teckbookmovil.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rodriguez.manuel.teckbookmovil.TecBookApplication
import com.rodriguez.manuel.teckbookmovil.ui.main.MainActivity
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger

/**
 * Maneja el deep link tuapp://oauth/callback?token=XYZ
 */
class OAuthCallbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data != null) {
            val token = data.getQueryParameter("token")
            val isNew = data.getQueryParameter("new")
            val incomplete = data.getQueryParameter("incomplete")

            Logger.d("OAuthCallback", "Token recibido: $token")
            Logger.d("OAuthCallback", "Nuevo usuario: $isNew | Incompleto: $incomplete")

            if (!token.isNullOrBlank()) {
                val app = application as TecBookApplication
                app.tokenManager.saveToken(token)

                // TODO: Manejar flags isNew y incomplete si quieres ir a una pantalla de completar perfil
            }
        }

        // Redirige al MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}
