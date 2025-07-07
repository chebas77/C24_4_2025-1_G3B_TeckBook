package com.rodriguez.manuel.teckbookmovil.data.repositories

import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.data.models.carrera.CarrerasActivasResponse
import com.rodriguez.manuel.teckbookmovil.data.models.carrera.CarrerasByDepartamentoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarreraRepository(
    private val apiService: ApiService
) {

    /**
     * Obtiene todas las carreras activas
     */
    suspend fun getCarrerasActivas(): Result<CarrerasActivasResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCarrerasActivas()
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene carreras por departamento específico
     */
    suspend fun getCarrerasByDepartamento(departamentoId: Long): Result<CarrerasByDepartamentoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCarrerasByDepartamento(departamentoId)
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}
