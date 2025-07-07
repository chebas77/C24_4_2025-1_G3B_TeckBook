package com.rodriguez.manuel.teckbookmovil.data.services

import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponse
import com.rodriguez.manuel.teckbookmovil.core.network.ApiResponseFactory
import com.rodriguez.manuel.teckbookmovil.core.network.PublicApiService
import com.rodriguez.manuel.teckbookmovil.core.network.getDataOrNull
import com.rodriguez.manuel.teckbookmovil.core.network.getErrorMessage
import com.rodriguez.manuel.teckbookmovil.core.network.isSuccess
import com.rodriguez.manuel.teckbookmovil.core.storage.PreferencesManager
import com.rodriguez.manuel.teckbookmovil.core.utils.Logger
import com.rodriguez.manuel.teckbookmovil.data.models.carrera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

/**
 * Servicio para gestión de datos académicos
 * Maneja carreras, departamentos, ciclos y secciones
 * Utiliza endpoints públicos para datos de filtros en cascada
 */
class CarreraService(
    private val publicApiService: PublicApiService,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        private const val CACHE_EXPIRY_TIME = 30 * 60 * 1000L // 30 minutos
        private const val CARRERAS_CACHE_KEY = "carreras_cache"
        private const val DEPARTAMENTOS_CACHE_KEY = "departamentos_cache"
        private const val CICLOS_CACHE_KEY = "ciclos_cache"
    }

    // ========== GESTIÓN DE CARRERAS ==========

    /**
     * Obtiene todas las carreras activas con cache
     */
    suspend fun getCarrerasActivas(forceRefresh: Boolean = false): ApiResponse<List<Carrera>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo carreras activas (forceRefresh: $forceRefresh)")

                // Verificar cache si no se fuerza refresh
                if (!forceRefresh) {
                    val cachedCarreras = getCachedCarreras()
                    if (cachedCarreras != null) {
                        Logger.d("CarreraService", "Carreras obtenidas desde cache: ${cachedCarreras.size}")
                        return@withContext ApiResponseFactory.success(cachedCarreras)
                    }
                }

                val response = publicApiService.getCarrerasActivas()

                if (response.isSuccessful && response.body() != null) {
                    val carrerasResponse = response.body()!!
                    val carreras = carrerasResponse.carreras

                    Logger.d("CarreraService", "Carreras activas obtenidas: ${carreras.size}")

                    // Guardar en cache
                    cacheCarreras(carreras)

                    return@withContext ApiResponseFactory.success(
                        data = carreras,
                        message = carrerasResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo carreras: $errorMessage")

                    // Intentar devolver cache en caso de error
                    val cachedCarreras = getCachedCarreras()
                    if (cachedCarreras != null) {
                        Logger.d("CarreraService", "Devolviendo carreras desde cache tras error")
                        return@withContext ApiResponseFactory.success(cachedCarreras)
                    }

                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("CarreraService", "Error de conexión obteniendo carreras", e)

                // Intentar devolver cache en caso de error de red
                val cachedCarreras = getCachedCarreras()
                if (cachedCarreras != null) {
                    Logger.d("CarreraService", "Devolviendo carreras desde cache tras error de red")
                    return@withContext ApiResponseFactory.success(cachedCarreras)
                }

                return@withContext ApiResponseFactory.networkError()
            } catch (e: Exception) {
                Logger.e("CarreraService", "Error inesperado obteniendo carreras", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene carreras por departamento
     */
    suspend fun getCarrerasByDepartamento(departamentoId: Long): ApiResponse<List<Carrera>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo carreras del departamento: $departamentoId")

                val response = publicApiService.getCarrerasByDepartamento(departamentoId)

                if (response.isSuccessful && response.body() != null) {
                    val carrerasResponse = response.body()!!
                    val carreras = carrerasResponse.carreras

                    Logger.d("CarreraService", "Carreras del departamento obtenidas: ${carreras.size}")

                    return@withContext ApiResponseFactory.success(
                        data = carreras,
                        message = carrerasResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo carreras del departamento: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("CarreraService", "Error obteniendo carreras del departamento", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE DEPARTAMENTOS ==========

    /**
     * Obtiene todos los departamentos activos con cache
     */
    suspend fun getDepartamentosActivos(forceRefresh: Boolean = false): ApiResponse<List<Departamento>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo departamentos activos (forceRefresh: $forceRefresh)")

                // Verificar cache si no se fuerza refresh
                if (!forceRefresh) {
                    val cachedDepartamentos = getCachedDepartamentos()
                    if (cachedDepartamentos != null) {
                        Logger.d("CarreraService", "Departamentos obtenidos desde cache: ${cachedDepartamentos.size}")
                        return@withContext ApiResponseFactory.success(cachedDepartamentos)
                    }
                }

                val response = publicApiService.getDepartamentosActivos()

                if (response.isSuccessful && response.body() != null) {
                    val departamentosResponse = response.body()!!
                    val departamentos = departamentosResponse.departamentos

                    Logger.d("CarreraService", "Departamentos activos obtenidos: ${departamentos.size}")

                    // Guardar en cache
                    cacheDepartamentos(departamentos)

                    return@withContext ApiResponseFactory.success(
                        data = departamentos,
                        message = departamentosResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo departamentos: $errorMessage")

                    // Intentar devolver cache en caso de error
                    val cachedDepartamentos = getCachedDepartamentos()
                    if (cachedDepartamentos != null) {
                        Logger.d("CarreraService", "Devolviendo departamentos desde cache tras error")
                        return@withContext ApiResponseFactory.success(cachedDepartamentos)
                    }

                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("CarreraService", "Error de conexión obteniendo departamentos", e)

                // Intentar devolver cache en caso de error de red
                val cachedDepartamentos = getCachedDepartamentos()
                if (cachedDepartamentos != null) {
                    Logger.d("CarreraService", "Devolviendo departamentos desde cache tras error de red")
                    return@withContext ApiResponseFactory.success(cachedDepartamentos)
                }

                return@withContext ApiResponseFactory.networkError()
            } catch (e: Exception) {
                Logger.e("CarreraService", "Error inesperado obteniendo departamentos", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE CICLOS ==========

    /**
     * Obtiene todos los ciclos disponibles con cache
     */
    suspend fun getAllCiclos(forceRefresh: Boolean = false): ApiResponse<List<Ciclo>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo todos los ciclos (forceRefresh: $forceRefresh)")

                // Verificar cache si no se fuerza refresh
                if (!forceRefresh) {
                    val cachedCiclos = getCachedCiclos()
                    if (cachedCiclos != null) {
                        Logger.d("CarreraService", "Ciclos obtenidos desde cache: ${cachedCiclos.size}")
                        return@withContext ApiResponseFactory.success(cachedCiclos)
                    }
                }

                val response = publicApiService.getAllCiclos()

                if (response.isSuccessful && response.body() != null) {
                    val ciclosResponse = response.body()!!
                    val ciclos = ciclosResponse.ciclos

                    Logger.d("CarreraService", "Ciclos obtenidos: ${ciclos.size}")

                    // Guardar en cache
                    cacheCiclos(ciclos)

                    return@withContext ApiResponseFactory.success(
                        data = ciclos,
                        message = ciclosResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo ciclos: $errorMessage")

                    // Intentar devolver cache en caso de error
                    val cachedCiclos = getCachedCiclos()
                    if (cachedCiclos != null) {
                        Logger.d("CarreraService", "Devolviendo ciclos desde cache tras error")
                        return@withContext ApiResponseFactory.success(cachedCiclos)
                    }

                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: UnknownHostException) {
                Logger.e("CarreraService", "Error de conexión obteniendo ciclos", e)

                // Intentar devolver cache en caso de error de red
                val cachedCiclos = getCachedCiclos()
                if (cachedCiclos != null) {
                    Logger.d("CarreraService", "Devolviendo ciclos desde cache tras error de red")
                    return@withContext ApiResponseFactory.success(cachedCiclos)
                }

                return@withContext ApiResponseFactory.networkError()
            } catch (e: Exception) {
                Logger.e("CarreraService", "Error inesperado obteniendo ciclos", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene ciclos para una carrera específica
     */
    suspend fun getCiclosByCarrera(carreraId: Long): ApiResponse<List<Ciclo>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo ciclos para carrera: $carreraId")

                val response = publicApiService.getCiclosByCarrera(carreraId)

                if (response.isSuccessful && response.body() != null) {
                    val ciclosResponse = response.body()!!
                    val ciclos = ciclosResponse.ciclos

                    Logger.d("CarreraService", "Ciclos para carrera obtenidos: ${ciclos.size}")

                    return@withContext ApiResponseFactory.success(
                        data = ciclos,
                        message = ciclosResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo ciclos para carrera: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("CarreraService", "Error obteniendo ciclos para carrera", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== GESTIÓN DE SECCIONES ==========

    /**
     * Obtiene secciones por carrera y ciclo
     */
    suspend fun getSeccionesByCarreraAndCiclo(carreraId: Long, cicloId: Long): ApiResponse<List<Seccion>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo secciones para carrera $carreraId, ciclo $cicloId")

                val response = publicApiService.getSeccionesByCarreraAndCiclo(carreraId, cicloId)

                if (response.isSuccessful && response.body() != null) {
                    val seccionesResponse = response.body()!!
                    val secciones = seccionesResponse.secciones

                    Logger.d("CarreraService", "Secciones obtenidas: ${secciones.size}")

                    return@withContext ApiResponseFactory.success(
                        data = secciones,
                        message = seccionesResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo secciones: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("CarreraService", "Error obteniendo secciones", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    /**
     * Obtiene todas las secciones de una carrera
     */
    suspend fun getSeccionesByCarrera(carreraId: Long): ApiResponse<List<Seccion>> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo secciones para carrera: $carreraId")

                val response = publicApiService.getSeccionesByCarrera(carreraId)

                if (response.isSuccessful && response.body() != null) {
                    val seccionesResponse = response.body()!!
                    val secciones = seccionesResponse.secciones

                    Logger.d("CarreraService", "Secciones de carrera obtenidas: ${secciones.size}")

                    return@withContext ApiResponseFactory.success(
                        data = secciones,
                        message = seccionesResponse.message
                    )
                } else {
                    val errorMessage = getErrorMessage(response)
                    Logger.w("CarreraService", "Error obteniendo secciones de carrera: $errorMessage")
                    return@withContext ApiResponseFactory.error(errorMessage, response.code())
                }

            } catch (e: Exception) {
                Logger.e("CarreraService", "Error obteniendo secciones de carrera", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== FILTROS EN CASCADA ==========

    /**
     * Obtiene todos los datos necesarios para filtros en cascada
     * Útil para formularios de registro, crear aulas, etc.
     */
    suspend fun getCascadeFilterData(forceRefresh: Boolean = false): ApiResponse<CascadeFilterData> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("CarreraService", "Obteniendo datos para filtros en cascada")

                // Obtener todos los datos en paralelo (simulado con coroutines secuenciales por simplicidad)
                val departamentosResponse = getDepartamentosActivos(forceRefresh)
                val carrerasResponse = getCarrerasActivas(forceRefresh)
                val ciclosResponse = getAllCiclos(forceRefresh)

                if (departamentosResponse.isSuccess() && carrerasResponse.isSuccess() && ciclosResponse.isSuccess()) {
                    val cascadeData = CascadeFilterData(
                        departamentos = departamentosResponse.getDataOrNull() ?: emptyList(),
                        carreras = carrerasResponse.getDataOrNull() ?: emptyList(),
                        ciclos = ciclosResponse.getDataOrNull() ?: emptyList(),
                        secciones = emptyList() // Las secciones se cargan dinámicamente
                    )

                    Logger.d("CarreraService", "Datos de filtros en cascada obtenidos exitosamente")

                    return@withContext ApiResponseFactory.success(
                        data = cascadeData,
                        message = "Datos de filtros obtenidos exitosamente"
                    )
                } else {
                    // Encontrar el primer error
                    val firstError = listOf(departamentosResponse, carrerasResponse, ciclosResponse)
                        .find { !it.isSuccess() }

                    return@withContext ApiResponseFactory.error(
                        firstError?.getErrorMessage() ?: "Error obteniendo datos de filtros"
                    )
                }

            } catch (e: Exception) {
                Logger.e("CarreraService", "Error obteniendo datos para filtros en cascada", e)
                return@withContext ApiResponseFactory.error(e)
            }
        }
    }

    // ========== MÉTODOS DE CONVERSIÓN PARA UI ==========

    /**
     * Convierte departamentos a items seleccionables para Spinners
     */
    fun departamentosToSelectableItems(departamentos: List<Departamento>): List<SelectableItem> {
        return departamentos.map { SelectableItem.fromDepartamento(it) }
    }

    /**
     * Convierte carreras a items seleccionables para Spinners
     */
    fun carrerasToSelectableItems(carreras: List<Carrera>): List<SelectableItem> {
        return carreras.map { SelectableItem.fromCarrera(it) }
    }

    /**
     * Convierte ciclos a items seleccionables para Spinners
     */
    fun ciclosToSelectableItems(ciclos: List<Ciclo>): List<SelectableItem> {
        return ciclos.map { SelectableItem.fromCiclo(it) }
    }

    /**
     * Convierte secciones a items seleccionables para Spinners
     */
    fun seccionesToSelectableItems(secciones: List<Seccion>): List<SelectableItem> {
        return secciones.map { SelectableItem.fromSeccion(it) }
    }

    // ========== MÉTODOS DE CACHE ==========

    /**
     * Guarda carreras en cache
     */
    private fun cacheCarreras(carreras: List<Carrera>) {
        try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(carreras)
            preferencesManager.saveString("${CARRERAS_CACHE_KEY}_data", json)
            preferencesManager.saveLong("${CARRERAS_CACHE_KEY}_timestamp", System.currentTimeMillis())
            Logger.cache("CarreraService", "SAVE", CARRERAS_CACHE_KEY)
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error guardando carreras en cache", e)
        }
    }

    /**
     * Obtiene carreras desde cache si son válidas
     */
    private fun getCachedCarreras(): List<Carrera>? {
        return try {
            val timestamp = preferencesManager.getLong("${CARRERAS_CACHE_KEY}_timestamp", 0)
            if (System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME) {
                Logger.cache("CarreraService", "EXPIRED", CARRERAS_CACHE_KEY)
                return null
            }

            val json = preferencesManager.getString("${CARRERAS_CACHE_KEY}_data")
            if (json != null) {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<Carrera>>() {}.type
                val carreras: List<Carrera> = gson.fromJson(json, type)
                Logger.cache("CarreraService", "LOAD", CARRERAS_CACHE_KEY)
                carreras
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error obteniendo carreras desde cache", e)
            null
        }
    }

    /**
     * Guarda departamentos en cache
     */
    private fun cacheDepartamentos(departamentos: List<Departamento>) {
        try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(departamentos)
            preferencesManager.saveString("${DEPARTAMENTOS_CACHE_KEY}_data", json)
            preferencesManager.saveLong("${DEPARTAMENTOS_CACHE_KEY}_timestamp", System.currentTimeMillis())
            Logger.cache("CarreraService", "SAVE", DEPARTAMENTOS_CACHE_KEY)
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error guardando departamentos en cache", e)
        }
    }

    /**
     * Obtiene departamentos desde cache si son válidos
     */
    private fun getCachedDepartamentos(): List<Departamento>? {
        return try {
            val timestamp = preferencesManager.getLong("${DEPARTAMENTOS_CACHE_KEY}_timestamp", 0)
            if (System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME) {
                Logger.cache("CarreraService", "EXPIRED", DEPARTAMENTOS_CACHE_KEY)
                return null
            }

            val json = preferencesManager.getString("${DEPARTAMENTOS_CACHE_KEY}_data")
            if (json != null) {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<Departamento>>() {}.type
                val departamentos: List<Departamento> = gson.fromJson(json, type)
                Logger.cache("CarreraService", "LOAD", DEPARTAMENTOS_CACHE_KEY)
                departamentos
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error obteniendo departamentos desde cache", e)
            null
        }
    }

    /**
     * Guarda ciclos en cache
     */
    private fun cacheCiclos(ciclos: List<Ciclo>) {
        try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(ciclos)
            preferencesManager.saveString("${CICLOS_CACHE_KEY}_data", json)
            preferencesManager.saveLong("${CICLOS_CACHE_KEY}_timestamp", System.currentTimeMillis())
            Logger.cache("CarreraService", "SAVE", CICLOS_CACHE_KEY)
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error guardando ciclos en cache", e)
        }
    }

    /**
     * Obtiene ciclos desde cache si son válidos
     */
    private fun getCachedCiclos(): List<Ciclo>? {
        return try {
            val timestamp = preferencesManager.getLong("${CICLOS_CACHE_KEY}_timestamp", 0)
            if (System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME) {
                Logger.cache("CarreraService", "EXPIRED", CICLOS_CACHE_KEY)
                return null
            }



            val json = preferencesManager.getString("${CICLOS_CACHE_KEY}_data")
            if (json != null) {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<Ciclo>>() {}.type
                val ciclos: List<Ciclo> = gson.fromJson(json, type)
                Logger.cache("CarreraService", "LOAD", CICLOS_CACHE_KEY)
                ciclos
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error obteniendo ciclos desde cache", e)
            null
        }
    }

    /**
     * Limpia todos los caches
     */
    fun clearCache() {
        try {
            preferencesManager.removeKey("${CARRERAS_CACHE_KEY}_data")
            preferencesManager.removeKey("${CARRERAS_CACHE_KEY}_timestamp")
            preferencesManager.removeKey("${DEPARTAMENTOS_CACHE_KEY}_data")
            preferencesManager.removeKey("${DEPARTAMENTOS_CACHE_KEY}_timestamp")
            preferencesManager.removeKey("${CICLOS_CACHE_KEY}_data")
            preferencesManager.removeKey("${CICLOS_CACHE_KEY}_timestamp")
            Logger.cache("CarreraService", "CLEAR", "ALL")
        } catch (e: Exception) {
            Logger.w("CarreraService", "Error limpiando cache", e)
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
                    404 -> "Datos no encontrados"
                    500 -> "Error del servidor"
                    503 -> "Servicio no disponible"
                    else -> "Error de conexión"
                }
            }
        } catch (e: Exception) {
            "Error de conexión"
        }
    }
}



