package com.rodriguez.manuel.teckbookmovil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav = findViewById(R.id.bottom_navigation)

        // ✅ Ahora el fragmento inicial es INICIO (Feed)
        loadFragment(InicioFragment())

        // ✅ Y marcamos Inicio como seleccionado
        bottomNav.selectedItemId = R.id.nav_inicio

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    loadFragment(InicioFragment())
                    true
                }
                R.id.nav_perfil -> {
                    loadFragment(PerfilFragment())
                    true
                }
                R.id.nav_aulas -> {
                    loadFragment(AulasFragment())
                    true
                }
                R.id.nav_notificaciones -> {
                    loadFragment(NotificacionesFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}



