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
import com.rodriguez.manuel.teckbookmovil.data.models.common.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Servicio para gestión de aulas virtuales
 * Maneja operaciones CRUD de aulas, estudiantes, anuncios e invitaciones
 */
class AulaService(
    private val apiService: ApiService,
    private val uploadApiService: UploadApiService,
    private val tokenManager: TokenManager
) {

    // ========== GESTIÓN DE AULAS ==========

    /**
     * Obtiene todas las aulas del usuario autenticado
     */
    suspend fun getMyAulas(): ApiResponse<AulasResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo aulas del usuario")

                if (!tokenManager.isLoggedIn()) {
                    return@withContext ApiResponseFactory.unauthorizedError()
                }

                val response = apiService.getMyAulas()

                if (response.isSuccessful && response.body() != null) {
                    val aulasResponse = response.body()!!
                    Logger.d("AulaService", "Aulas obtenidas: ${aulasResponse.totalAulas}")

                    return@withContext ApiResponseFactory.success(
                        data = aulasResponse,
                        message = aulasResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo aulas: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("AulaService", "Error de conexión obteniendo aulas", e)
                return@withContext ApiResponseFactory.networkError()
            } catch (e: Exception) {
                Logger.e("AulaService", "Error inesperado obteniendo aulas", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene detalles de un aula específica
     */
    suspend fun getAulaById(aulaId: Long): ApiResponse<AulaDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo detalles del aula: $aulaId")

                val response = apiService.getAulaById(aulaId)

                if (response.isSuccessful && response.body() != null) {
                    val aulaDetail = response.body()!!
                    Logger.d("AulaService", "Detalles del aula obtenidos: ${aulaDetail.aula.nombre}")

                    return@withContext ApiResponseFactory.success(aulaDetail)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo aula $aulaId: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo aula $aulaId", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Crea una nueva aula virtual
     */
    suspend fun createAula(
        nombre: String,
        titulo: String? = null,
        descripcion: String? = null,
        seccionId: Long? = null
    ): ApiResponse<AulaVirtual> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Creando nueva aula: $nombre")

                // Validar datos
                if (nombre.trim().isEmpty()) {
                    return@withContext ApiResponseFactory.error("El nombre del aula es requerido")
                }

                if (nombre.length > 100) {
                    return@withContext ApiResponseFactory.error("El nombre no puede exceder 100 caracteres")
                }

                val request = CreateAulaRequest(
                    nombre = nombre.trim(),
                    titulo = titulo?.trim(),
                    descripcion = descripcion?.trim(),
                    seccionId = seccionId
                )

                val response = apiService.createAula(request)

                if (response.isSuccessful && response.body() != null) {
                    val aulaCreada = response.body()!!
                    Logger.d("AulaService", "Aula creada exitosamente: ${aulaCreada.id}")

                    return@withContext ApiResponseFactory.success(
                        data = aulaCreada,
                        message = "Aula creada exitosamente"
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error creando aula: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error creando aula", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Busca aulas por nombre
     */
    suspend fun searchAulas(query: String): ApiResponse<BuscarAulasResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Buscando aulas: $query")

                if (query.trim().length < 2) {
                    return@withContext ApiResponseFactory.error("El término de búsqueda debe tener al menos 2 caracteres")
                }

                val response = apiService.searchAulas(query.trim())

                if (response.isSuccessful && response.body() != null) {
                    val searchResponse = response.body()!!
                    Logger.d("AulaService", "Búsqueda completada: ${searchResponse.totalResultados} resultados")

                    return@withContext ApiResponseFactory.success(searchResponse)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error en búsqueda: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error en búsqueda de aulas", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE PARTICIPANTES ==========

    /**
     * Obtiene participantes de un aula
     */
    suspend fun getParticipantes(aulaId: Long): ApiResponse<ParticipantesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo participantes del aula: $aulaId")

                val response = apiService.getAulaParticipantes(aulaId)

                if (response.isSuccessful && response.body() != null) {
                    val participantes = response.body()!!
                    Logger.d("AulaService", "Participantes obtenidos: ${participantes.participantes.size}")

                    return@withContext ApiResponseFactory.success(participantes)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo participantes: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo participantes", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Agrega un estudiante al aula (solo profesores)
     */
    suspend fun addEstudianteToAula(aulaId: Long, estudianteId: Long): ApiResponse<MessageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Agregando estudiante $estudianteId al aula $aulaId")

                if (!tokenManager.isProfesor()) {
                    return@withContext ApiResponseFactory.error("Solo los profesores pueden agregar estudiantes")
                }

                val response = apiService.addEstudianteToAula(aulaId, estudianteId)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    Logger.d("AulaService", "Estudiante agregado exitosamente")

                    return@withContext ApiResponseFactory.success(result, result.message)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error agregando estudiante: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error agregando estudiante", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Elimina un estudiante del aula (solo profesores)
     */
    suspend fun removeEstudianteFromAula(aulaId: Long, estudianteId: Long): ApiResponse<MessageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Eliminando estudiante $estudianteId del aula $aulaId")

                if (!tokenManager.isProfesor()) {
                    return@withContext ApiResponseFactory.error("Solo los profesores pueden eliminar estudiantes")
                }

                val response = apiService.removeEstudianteFromAula(aulaId, estudianteId)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    Logger.d("AulaService", "Estudiante eliminado exitosamente")

                    return@withContext ApiResponseFactory.success(result, result.message)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error eliminando estudiante: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error eliminando estudiante", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE ANUNCIOS ==========

    /**
     * Obtiene anuncios de un aula
     */
    suspend fun getAnunciosDeAula(aulaId: Long): ApiResponse<List<Anuncio>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo anuncios del aula: $aulaId")

                val response = apiService.getAnunciosDeAula(aulaId)

                if (response.isSuccessful && response.body() != null) {
                    val anuncios = response.body()!!
                    Logger.d("AulaService", "Anuncios obtenidos: ${anuncios.size}")

                    return@withContext ApiResponseFactory.success(anuncios)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo anuncios: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo anuncios", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Crea un nuevo anuncio en el aula
     */
    suspend fun createAnuncio(
        aulaId: Long,
        titulo: String,
        contenido: String,
        tipo: String,
        archivoFile: File? = null
    ): ApiResponse<Anuncio> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Creando anuncio en aula $aulaId: $titulo")

                // Validar datos
                val tituloValidation = ValidationUtils.validateAnuncioTitle(titulo)
                if (!tituloValidation.isValid) {
                    return@withContext ApiResponseFactory.error(tituloValidation.message!!)
                }

                val contenidoValidation = ValidationUtils.validateAnuncioContent(contenido)
                if (!contenidoValidation.isValid) {
                    return@withContext ApiResponseFactory.error(contenidoValidation.message!!)
                }

                // Preparar datos multipart
                val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
                val contenidoBody = contenido.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipoBody = tipo.toRequestBody("text/plain".toMediaTypeOrNull())

                // Preparar archivo si existe
                val archivoPart = archivoFile?.let { file ->
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("archivo", file.name, requestFile)
                }

                val response = apiService.createAnuncioEnAula(
                    aulaId = aulaId,
                    titulo = tituloBody,
                    contenido = contenidoBody,
                    tipo = tipoBody,
                    archivo = archivoPart
                )

                if (response.isSuccessful && response.body() != null) {
                    val anuncio = response.body()!!
                    Logger.d("AulaService", "Anuncio creado exitosamente: ${anuncio.id}")

                    return@withContext ApiResponseFactory.success(
                        data = anuncio,
                        message = "Anuncio creado exitosamente"
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error creando anuncio: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: SocketTimeoutException) {
                Logger.e("AulaService", "Timeout creando anuncio", e)
                return@withContext ApiResponseFactory.timeoutError("La creación del anuncio tardó demasiado")
            } catch (e: Exception) {
                Logger.e("AulaService", "Error creando anuncio", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene anuncios generales
     */
    suspend fun getAnunciosGenerales(): ApiResponse<List<Anuncio>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo anuncios generales")

                val response = apiService.getAnunciosGenerales()

                if (response.isSuccessful && response.body() != null) {
                    val anuncios = response.body()!!
                    Logger.d("AulaService", "Anuncios generales obtenidos: ${anuncios.size}")

                    return@withContext ApiResponseFactory.success(anuncios)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo anuncios generales: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo anuncios generales", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE INVITACIONES ==========

    /**
     * Envía una invitación para unirse al aula
     */
    suspend fun enviarInvitacion(
        aulaId: Long,
        correoInvitado: String,
        mensaje: String? = null
    ): ApiResponse<EnviarInvitacionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Enviando invitación para aula $aulaId a $correoInvitado")

                // Validar email
                val emailValidation = ValidationUtils.validateInstitutionalEmail(correoInvitado)
                if (!emailValidation.isValid) {
                    return@withContext ApiResponseFactory.error(emailValidation.message!!)
                }

                val request = EnviarInvitacionRequest(
                    aulaId = aulaId,
                    correoInvitado = correoInvitado.trim(),
                    mensaje = mensaje?.trim()
                )

                val response = apiService.enviarInvitacion(request)

                if (response.isSuccessful && response.body() != null) {
                    val invitationResponse = response.body()!!
                    Logger.d("AulaService", "Invitación enviada exitosamente")

                    return@withContext ApiResponseFactory.success(
                        data = invitationResponse,
                        message = invitationResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error enviando invitación: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error enviando invitación", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Acepta una invitación usando el código
     */
    suspend fun aceptarInvitacion(codigoInvitacion: String): ApiResponse<AceptarInvitacionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Aceptando invitación: $codigoInvitacion")

                if (codigoInvitacion.trim().isEmpty()) {
                    return@withContext ApiResponseFactory.error("Código de invitación requerido")
                }

                val response = apiService.aceptarInvitacion(codigoInvitacion.trim())

                if (response.isSuccessful && response.body() != null) {
                    val acceptResponse = response.body()!!
                    Logger.d("AulaService", "Invitación aceptada, unido al aula: ${acceptResponse.aulaId}")

                    return@withContext ApiResponseFactory.success(
                        data = acceptResponse,
                        message = acceptResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error aceptando invitación: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error aceptando invitación", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene mis invitaciones
     */
    suspend fun getMisInvitaciones(): ApiResponse<MisInvitacionesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo mis invitaciones")

                val response = apiService.getMisInvitaciones()

                if (response.isSuccessful && response.body() != null) {
                    val invitations = response.body()!!
                    Logger.d("AulaService", "Invitaciones obtenidas: ${invitations.total}")

                    return@withContext ApiResponseFactory.success(invitations)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo invitaciones: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo invitaciones", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene invitaciones pendientes
     */
    suspend fun getInvitacionesPendientes(): ApiResponse<InvitacionesPendientesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("AulaService", "Obteniendo invitaciones pendientes")

                val response = apiService.getInvitacionesPendientes()

                if (response.isSuccessful && response.body() != null) {
                    val pendingInvitations = response.body()!!
                    Logger.d("AulaService", "Invitaciones pendientes: ${pendingInvitations.total}")

                    return@withContext ApiResponseFactory.success(pendingInvitations)
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("AulaService", "Error obteniendo invitaciones pendientes: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo invitaciones pendientes", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== MÉTODOS UTILITARIOS ==========

    /**
     * Verifica si el usuario actual puede gestionar un aula (es el profesor)
     */
    fun canManageAula(aula: AulaVirtual): Boolean {
        val currentUserId = tokenManager.getUserId()
        return tokenManager.isProfesor() && aula.profesorId == currentUserId
    }

    /**
     * Verifica si el usuario actual puede crear anuncios en un aula
     */
    fun canCreateAnuncios(aula: AulaVirtual): Boolean {
        return canManageAula(aula) || tokenManager.isAdmin()
    }

    /**
     * Obtiene estadísticas básicas de un aula
     */
    suspend fun getAulaStats(aulaId: Long): ApiResponse<AulaStats> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtener detalles del aula y anuncios en paralelo
                val aulaDetailResponse = getAulaById(aulaId)
                val anunciosResponse = getAnunciosDeAula(aulaId)

                if (aulaDetailResponse.isSuccess() && anunciosResponse.isSuccess()) {
                    val aulaDetail = aulaDetailResponse.getDataOrNull()!!
                    val anuncios = anunciosResponse.getDataOrNull()!!

                    val stats = AulaStats(
                        totalEstudiantes = aulaDetail.totalEstudiantes.toInt(),
                        totalAnuncios = anuncios.size,
                        anunciosPendientes = anuncios.count { !it.activo },
                        ultimaActividad = anuncios.maxByOrNull { it.fechaPublicacion ?: "" }?.fechaPublicacion
                    )

                    return@withContext ApiResponseFactory.success(stats)
                } else {
                    return@withContext ApiResponseFactory.error("Error obteniendo estadísticas del aula")
                }

            } catch (e: Exception) {
                Logger.e("AulaService", "Error obteniendo estadísticas", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Extrae mensaje de error de una respuesta
     */
    private fun getErrorMessage(response: retrofit2.Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val gson = com.google.gson.Gson()
                val errorResponse = gson.fromJson(errorBody, com.rodriguez.manuel.teckbookmovil.data.models.common.ErrorResponse::class.java)
                errorResponse.getErrorMessage()
            } else {
                when (response.code()) {
                    400 -> "Solicitud inválida"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "Aula no encontrada"
                    500 -> "Error del servidor"
                    else -> "Error de conexión"
                }
            }
        } catch (e: Exception) {
            "Error de conexión"
        }
    }
}