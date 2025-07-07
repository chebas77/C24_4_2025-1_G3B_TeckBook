package com.rodriguez.manuel.teckbookmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            // No importa lo que pongas, siempre te lleva al Home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Evita volver atrás
        }
    }
}

