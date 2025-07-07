package com.rodriguez.manuel.teckbookmovil.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.rodriguez.manuel.teckbookmovil.R
import com.rodriguez.manuel.teckbookmovil.ui.main.fragments.AulasFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate de tener este layout con un FragmentContainerView

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container_view, AulasFragment())
            }
        }
    }
}
