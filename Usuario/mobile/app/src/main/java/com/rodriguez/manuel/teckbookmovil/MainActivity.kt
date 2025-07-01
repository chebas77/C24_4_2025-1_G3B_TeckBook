package com.rodriguez.manuel.teckbookmovil

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.rodriguez.manuel.teckbookmovil.data.network.NetworkModule
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🧪 PROBAR SOLO ENDPOINTS PÚBLICOS
        testPublicEndpoints()
    }

    /**
     * 🌐 Prueba SOLO endpoints públicos (que no requieren autenticación)
     */
    private fun testPublicEndpoints() {
        Log.d(TAG, "=== 🌐 PROBANDO ENDPOINTS PÚBLICOS ===")
        Log.d(TAG, NetworkModule.debugInfo())

        lifecycleScope.launch {
            var publicTestsPassed = 0
            var totalPublicTests = 0

            try {
                // 1. ✅ Health Check Carreras (PÚBLICO)
                totalPublicTests++
                Log.d(TAG, "🏥 Test 1: Health check carreras (PÚBLICO)...")
                try {
                    val healthCarreras = NetworkModule.apiService.healthCheckCarreras()
                    Log.d(TAG, "Response code: ${healthCarreras.code()}")
                    Log.d(TAG, "Response message: ${healthCarreras.message()}")

                    if (healthCarreras.isSuccessful) {
                        publicTestsPassed++
                        val body = healthCarreras.body()
                        Log.d(TAG, "✅ Health carreras: OK - ${body?.service}")
                    } else {
                        Log.e(TAG, "❌ Health carreras: ${healthCarreras.code()} - ${healthCarreras.message()}")
                        Log.e(TAG, "Error body: ${healthCarreras.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Health carreras exception: ${e.message}", e)
                }

                // 2. ✅ Health Check Departamentos (PÚBLICO)
                totalPublicTests++
                Log.d(TAG, "🏥 Test 2: Health check departamentos (PÚBLICO)...")
                try {
                    val healthDepartamentos = NetworkModule.apiService.healthCheckDepartamentos()
                    Log.d(TAG, "Response code: ${healthDepartamentos.code()}")

                    if (healthDepartamentos.isSuccessful) {
                        publicTestsPassed++
                        Log.d(TAG, "✅ Health departamentos: OK")
                    } else {
                        Log.e(TAG, "❌ Health departamentos: ${healthDepartamentos.code()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Health departamentos exception: ${e.message}", e)
                }

                // 3. ✅ Carreras Activas (PÚBLICO)
                totalPublicTests++
                Log.d(TAG, "📚 Test 3: Carreras activas (PÚBLICO)...")
                try {
                    val carrerasResponse = NetworkModule.apiService.getCarrerasActivas()
                    Log.d(TAG, "Response code: ${carrerasResponse.code()}")

                    if (carrerasResponse.isSuccessful) {
                        publicTestsPassed++
                        val carreras = carrerasResponse.body()
                        Log.d(TAG, "✅ Carreras: ${carreras?.count ?: 0} obtenidas")
                        carreras?.carreras?.take(3)?.forEach { carrera ->
                            Log.d(TAG, "   - ${carrera.nombre} (${carrera.codigo})")
                        }
                    } else {
                        Log.e(TAG, "❌ Carreras: ${carrerasResponse.code()} - ${carrerasResponse.message()}")
                        if (carrerasResponse.code() == 401) {
                            Log.e(TAG, "🚨 ESTE ENDPOINT NO DEBERÍA REQUERIR AUTH!")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Carreras exception: ${e.message}", e)
                }

                // 4. ✅ Departamentos Activos (PÚBLICO)
                totalPublicTests++
                Log.d(TAG, "🏢 Test 4: Departamentos activos (PÚBLICO)...")
                try {
                    val deptosResponse = NetworkModule.apiService.getDepartamentosActivos()
                    Log.d(TAG, "Response code: ${deptosResponse.code()}")

                    if (deptosResponse.isSuccessful) {
                        publicTestsPassed++
                        val departamentos = deptosResponse.body()
                        Log.d(TAG, "✅ Departamentos: ${departamentos?.count ?: 0} obtenidos")
                        departamentos?.departamentos?.forEach { depto ->
                            Log.d(TAG, "   - ${depto.nombre} (${depto.codigo})")
                        }
                    } else {
                        Log.e(TAG, "❌ Departamentos: ${deptosResponse.code()} - ${deptosResponse.message()}")
                        if (deptosResponse.code() == 401) {
                            Log.e(TAG, "🚨 ESTE ENDPOINT NO DEBERÍA REQUERIR AUTH!")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Departamentos exception: ${e.message}", e)
                }

                // 5. ✅ Ciclos (PÚBLICO)
                totalPublicTests++
                Log.d(TAG, "📖 Test 5: Ciclos (PÚBLICO)...")
                try {
                    val ciclosResponse = NetworkModule.apiService.getAllCiclos()
                    Log.d(TAG, "Response code: ${ciclosResponse.code()}")

                    if (ciclosResponse.isSuccessful) {
                        publicTestsPassed++
                        val ciclos = ciclosResponse.body()
                        Log.d(TAG, "✅ Ciclos: ${ciclos?.count ?: 0} obtenidos")
                        ciclos?.ciclos?.forEach { ciclo ->
                            Log.d(TAG, "   - ${ciclo.nombre} (${ciclo.numero})")
                        }
                    } else {
                        Log.e(TAG, "❌ Ciclos: ${ciclosResponse.code()} - ${ciclosResponse.message()}")
                        if (ciclosResponse.code() == 401) {
                            Log.e(TAG, "🚨 ESTE ENDPOINT NO DEBERÍA REQUERIR AUTH!")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Ciclos exception: ${e.message}", e)
                }

                // Resumen de endpoints públicos
                Log.d(TAG, "=== 📊 RESUMEN ENDPOINTS PÚBLICOS ===")
                Log.d(TAG, "Tests públicos pasados: $publicTestsPassed/$totalPublicTests")

                if (publicTestsPassed > 0) {
                    Log.d(TAG, "🎉 ¡Conexión básica al backend funciona!")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,
                            "✅ Conexión OK: $publicTestsPassed/$totalPublicTests endpoints públicos",
                            Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(TAG, "❌ Ningún endpoint público funciona - problema de conectividad")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,
                            "❌ Error de conexión al backend",
                            Toast.LENGTH_LONG).show()
                    }
                }

                // ⚠️ Mostrar qué endpoints requieren auth (es normal que fallen)
                Log.d(TAG, "=== ⚠️ ENDPOINTS QUE REQUIEREN AUTH (normal que fallen) ===")
                Log.d(TAG, "- /api/auth/user")
                Log.d(TAG, "- /api/usuarios/me")
                Log.d(TAG, "- /api/aulas")
                Log.d(TAG, "- /api/invitaciones/*")
                Log.d(TAG, "- /api/upload/*")
                Log.d(TAG, "Para estos necesitas hacer login primero")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error general: ${e.message}", e)
                handleConnectionError(e)
            }
        }
    }

    /**
     * 🚨 Diagnóstico de errores de conexión
     */
    private fun handleConnectionError(error: Exception) {
        Log.e(TAG, "=== 🚨 DIAGNÓSTICO DE ERROR ===")

        val errorMessage = error.message ?: "Error desconocido"

        when {
            errorMessage.contains("ECONNREFUSED") -> {
                Log.e(TAG, "🔴 Backend no está corriendo o puerto incorrecto")
                Log.e(TAG, "💡 Verificar: http://localhost:8085/api/carreras/health en navegador")
            }

            errorMessage.contains("UnknownHostException") -> {
                Log.e(TAG, "🔴 No se puede resolver la IP")
                Log.e(TAG, "💡 Verificar IP en NetworkModule.Config")
            }

            errorMessage.contains("ConnectException") -> {
                Log.e(TAG, "🔴 No se puede conectar")
                Log.e(TAG, "💡 Para emulador: 10.0.2.2:8085")
                Log.e(TAG, "💡 Para dispositivo: IP_DE_TU_PC:8085")
            }

            else -> {
                Log.e(TAG, "🔴 Error: $errorMessage")
            }
        }

        Log.e(TAG, "📱 Configuración actual: ${NetworkModule.getNetworkInfo()}")
    }
}