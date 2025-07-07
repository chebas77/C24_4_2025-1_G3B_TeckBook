package com.rodriguez.manuel.teckbookmovil.data.services

import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponse
import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponseFactory
import com.rodriguez.manuel.teckbookmovil.core.network.ApiService
import com.rodriguez.manuel.teckbookmovil.core.network.UploadApiService
import com.rodriguez.manuel.teckbookmovil.core.network.getDataOrNull
import com.rodriguez.manuel.teckbookmovil.core.network.isSuccess
import com.rodriguez.manuel.teckbookmovil.core.storage.TokenManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.core.utils.ValidationUtils
import com.rodriguez.manuel.teckbookmovil.data.models.aula.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AulaService(
    private val apiService: ApiService,
    private val uploadApiService: UploadApiService,
    private val tokenManager: TokenManager
) {

    suspend fun getMyAulas(): ApiResponse<AulasResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Obteniendo aulas del usuario")
            val response = apiService.getMyAulas()
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error obteniendo aulas", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun getAulaById(aulaId: Long): ApiResponse<AulaDetailResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Obteniendo detalles del aula: $aulaId")
            val response = apiService.getAulaById(aulaId)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error obteniendo detalles", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun createAula(
        nombre: String,
        titulo: String? = null,
        descripcion: String? = null,
        seccionId: Long? = null
    ): ApiResponse<AulaVirtual> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Creando aula: $nombre")
            if (nombre.trim().isEmpty()) return@withContext ApiResponseFactory.error("El nombre es requerido")
            val request = CreateAulaRequest(
                nombre.trim(),
                titulo?.trim(),
                descripcion?.trim(),
                seccionId
            )
            val response = apiService.createAula(request)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error creando aula", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun searchAulas(query: String): ApiResponse<BuscarAulasResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Buscando aulas: $query")
            val response = apiService.searchAulas(query.trim())
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error buscando aulas", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun getParticipantes(aulaId: Long): ApiResponse<ParticipantesResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Obteniendo participantes del aula: $aulaId")
            val response = apiService.getAulaParticipantes(aulaId)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error obteniendo participantes", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun getAnunciosDeAula(aulaId: Long): ApiResponse<List<Anuncio>> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Obteniendo anuncios del aula: $aulaId")
            val response = apiService.getAnunciosDeAula(aulaId)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error obteniendo anuncios", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun createAnuncio(
        aulaId: Long,
        titulo: String,
        contenido: String,
        tipo: String,
        archivoFile: File? = null
    ): ApiResponse<Anuncio> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Creando anuncio en aula $aulaId: $titulo")
            val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
            val contenidoBody = contenido.toRequestBody("text/plain".toMediaTypeOrNull())
            val tipoBody = tipo.toRequestBody("text/plain".toMediaTypeOrNull())
            val archivoPart = archivoFile?.let {
                val requestFile = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("archivo", it.name, requestFile)
            }
            val response = apiService.createAnuncioEnAula(aulaId, tituloBody, contenidoBody, tipoBody, archivoPart)
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error creando anuncio", e)
            ApiResponseFactory.error(e)
        }
    }

    suspend fun getAnunciosGenerales(): ApiResponse<List<Anuncio>> = withContext(Dispatchers.IO) {
        try {
            Logger.d("AulaService", "Obteniendo anuncios generales")
            val response = apiService.getAnunciosGenerales()
            if (response.isSuccessful && response.body() != null) {
                ApiResponseFactory.success(response.body()!!)
            } else {
                ApiResponseFactory.error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("AulaService", "Error obteniendo anuncios generales", e)
            ApiResponseFactory.error(e)
        }
    }
}
