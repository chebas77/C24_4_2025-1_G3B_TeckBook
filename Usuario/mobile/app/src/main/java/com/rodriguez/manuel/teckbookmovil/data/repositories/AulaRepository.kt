package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.data.models.aula.AulasResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AulaRepository(
    private val apiService: ApiService
) {

    /**
     * Obtiene mis aulas desde la API.
     */
    suspend fun getMyAulas(): Result<AulasResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.getMyAulas()

                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Respuesta vacía")
                } else {
                    throw Exception("Error: ${response.code()} ${response.message()}")
                }
            }
        }
    }
}
