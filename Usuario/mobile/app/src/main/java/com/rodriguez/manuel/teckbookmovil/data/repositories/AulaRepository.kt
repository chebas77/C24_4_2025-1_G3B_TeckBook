package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.data.models.aula.AulasResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AulaRepository(private val apiService: ApiService) {

    suspend fun getMyAulas(): Result<AulasResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyAulas()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
