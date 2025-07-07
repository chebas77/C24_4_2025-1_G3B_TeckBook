package com.rodriguez.manuel.teckbookmovil.core.network

import com.rodriguez.manuel.teckbookmovil.data.models.auth.*
import com.rodriguez.manuel.teckbookmovil.data.models.aula.*
import com.rodriguez.manuel.teckbookmovil.data.models.carrera.*
import com.rodriguez.manuel.teckbookmovil.data.models.common.*
import com.rodriguez.manuel.teckbookmovil.core.config.AppConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface principal para todos los endpoints de la API TecBook
 * Basada en los controllers del backend Spring Boot
 */
interface ApiService {

    // ========== ENDPOINTS DE AUTENTICACIÓN ==========

    // Login con Google
    @POST("/api/auth/google") // o usa AppConfig.Endpoints.GOOGLE_LOGIN si el path es '/google-login'
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<LoginResponse>

    /**
     * Login tradicional con email y contraseña
     * POST /api/auth/login
     */
    @POST(AppConfig.Endpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Obtiene URL de redirección para Google OAuth2
     * GET /api/auth/google-login
     */
    @GET(AppConfig.Endpoints.GOOGLE_LOGIN)
    suspend fun getGoogleLoginUrl(): Response<GoogleLoginResponse>

    /**
     * Obtiene información del usuario autenticado
     * GET /api/auth/user
     */
    @GET(AppConfig.Endpoints.USER_INFO)
    suspend fun getUserInfo(): Response<UserInfo>

    /**
     * Cierra sesión e invalida el token
     * POST /api/auth/logout
     */
    @POST(AppConfig.Endpoints.LOGOUT)
    suspend fun logout(): Response<LogoutResponse>

    /**
     * Verifica el estado del token actual
     * GET /api/auth/token/status
     */
    @GET(AppConfig.Endpoints.TOKEN_STATUS)
    suspend fun getTokenStatus(): Response<TokenStatusResponse>

    // ========== ENDPOINTS DE USUARIO ==========

    /**
     * Obtiene información del usuario autenticado
     * GET /api/usuarios/me
     */
    @GET(AppConfig.Endpoints.USUARIO_ME)
    suspend fun getMyProfile(): Response<UserInfo>

    /**
     * Registra un nuevo usuario
     * POST /api/usuarios/register
     */
    @POST(AppConfig.Endpoints.USUARIO_REGISTER)
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    /**
     * Obtiene usuario por ID
     * GET /api/usuarios/{id}
     */
    @GET(AppConfig.Endpoints.USUARIO_BY_ID)
    suspend fun getUserById(@Path("id") userId: Long): Response<UserInfo>

    /**
     * Actualiza información del usuario
     * PUT /api/usuarios/{id}
     */
    @PUT(AppConfig.Endpoints.USUARIO_BY_ID)
    suspend fun updateUser(
        @Path("id") userId: Long,
        @Body request: UpdateProfileRequest
    ): Response<MessageResponse>

    // ========== ENDPOINTS DE AULAS VIRTUALES ==========

    /**
     * Obtiene todas las aulas del usuario autenticado
     * GET /api/aulas
     */
    @GET(AppConfig.Endpoints.AULAS)
    suspend fun getMyAulas(): Response<AulasResponse>

    /**
     * Obtiene detalles de un aula específica
     * GET /api/aulas/{aulaId}
     */
    @GET(AppConfig.Endpoints.AULA_BY_ID)
    suspend fun getAulaById(@Path("aulaId") aulaId: Long): Response<AulaDetailResponse>

    /**
     * Crea una nueva aula virtual
     * POST /api/aulas
     */
    @POST(AppConfig.Endpoints.AULAS)
    suspend fun createAula(@Body request: CreateAulaRequest): Response<AulaVirtual>

    /**
     * Busca aulas por nombre
     * GET /api/aulas/buscar
     */
    @GET(AppConfig.Endpoints.AULA_BUSCAR)
    suspend fun searchAulas(@Query("nombre") nombre: String): Response<BuscarAulasResponse>

    /**
     * Obtiene participantes de un aula
     * GET /api/aulas/{aulaId}/participantes
     */
    @GET(AppConfig.Endpoints.AULA_PARTICIPANTES)
    suspend fun getAulaParticipantes(@Path("aulaId") aulaId: Long): Response<ParticipantesResponse>

    /**
     * Agrega estudiante a un aula
     * POST /api/aulas/{aulaId}/estudiantes/{estudianteId}
     */
    @POST("/api/aulas/{aulaId}/estudiantes/{estudianteId}")
    suspend fun addEstudianteToAula(
        @Path("aulaId") aulaId: Long,
        @Path("estudianteId") estudianteId: Long
    ): Response<MessageResponse>

    /**
     * Elimina estudiante de un aula
     * DELETE /api/aulas/{aulaId}/participantes/{estudianteId}
     */
    @DELETE("/api/aulas/{aulaId}/participantes/{estudianteId}")
    suspend fun removeEstudianteFromAula(
        @Path("aulaId") aulaId: Long,
        @Path("estudianteId") estudianteId: Long
    ): Response<MessageResponse>

    // ========== ENDPOINTS DE ANUNCIOS ==========

    /**
     * Obtiene anuncios de un aula específica
     * GET /api/aulas/{aulaId}/anuncios
     */
    @GET(AppConfig.Endpoints.AULA_ANUNCIOS)
    suspend fun getAnunciosDeAula(@Path("aulaId") aulaId: Long): Response<List<Anuncio>>

    /**
     * Crea un nuevo anuncio en un aula
     * POST /api/aulas/{aulaId}/anuncios
     */
    @Multipart
    @POST(AppConfig.Endpoints.AULA_ANUNCIOS)
    suspend fun createAnuncioEnAula(
        @Path("aulaId") aulaId: Long,
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("tipo") tipo: RequestBody,
        @Part archivo: MultipartBody.Part? = null
    ): Response<Anuncio>

    /**
     * Obtiene anuncios generales
     * GET /api/anuncios/general
     */
    @GET(AppConfig.Endpoints.ANUNCIOS_GENERAL)
    suspend fun getAnunciosGenerales(): Response<List<Anuncio>>

    /**
     * Obtiene todos los anuncios (generales y de aulas)
     * GET /api/anuncios/general/todos
     */
    @GET(AppConfig.Endpoints.ANUNCIOS_TODOS)
    suspend fun getTodosLosAnuncios(): Response<List<Anuncio>>

    /**
     * Crea anuncio general
     * POST /api/anuncios/general
     */
    @Multipart
    @POST(AppConfig.Endpoints.ANUNCIOS_GENERAL)
    suspend fun createAnuncioGeneral(
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("tipo") tipo: RequestBody,
        @Part archivo: MultipartBody.Part? = null
    ): Response<Anuncio>

    // ========== ENDPOINTS DE INVITACIONES ==========

    /**
     * Envía una invitación para unirse a un aula
     * POST /api/invitaciones/enviar
     */
    @POST(AppConfig.Endpoints.INVITACIONES_ENVIAR)
    suspend fun enviarInvitacion(@Body request: EnviarInvitacionRequest): Response<EnviarInvitacionResponse>

    /**
     * Acepta una invitación usando el código
     * POST /api/invitaciones/aceptar/{codigoInvitacion}
     */
    @POST(AppConfig.Endpoints.INVITACIONES_ACEPTAR)
    suspend fun aceptarInvitacion(@Path("codigoInvitacion") codigoInvitacion: String): Response<AceptarInvitacionResponse>

    /**
     * Obtiene todas las invitaciones del usuario
     * GET /api/invitaciones/mis-invitaciones
     */
    @GET(AppConfig.Endpoints.INVITACIONES_MIS)
    suspend fun getMisInvitaciones(): Response<MisInvitacionesResponse>

    /**
     * Obtiene invitaciones pendientes del usuario
     * GET /api/invitaciones/pendientes
     */
    @GET(AppConfig.Endpoints.INVITACIONES_PENDIENTES)
    suspend fun getInvitacionesPendientes(): Response<InvitacionesPendientesResponse>

    /**
     * Obtiene invitaciones de un aula específica
     * GET /api/invitaciones/aula/{aulaId}
     */
    @GET("/api/invitaciones/aula/{aulaId}")
    suspend fun getInvitacionesDeAula(@Path("aulaId") aulaId: Long): Response<MisInvitacionesResponse>

    // ========== ENDPOINTS DE CARRERAS ==========

    /**
     * Obtiene todas las carreras activas
     * GET /api/carreras/activas (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.CARRERAS_ACTIVAS)
    suspend fun getCarrerasActivas(): Response<CarrerasActivasResponse>

    /**
     * Obtiene carrera por ID
     * GET /api/carreras/{id}
     */
    @GET("/api/carreras/{id}")
    suspend fun getCarreraById(@Path("id") carreraId: Long): Response<Carrera>

    /**
     * Obtiene carreras por departamento
     * GET /api/carreras/departamento/{departamentoId}/activas (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.CARRERAS_BY_DEPARTAMENTO)
    suspend fun getCarrerasByDepartamento(@Path("departamentoId") departamentoId: Long): Response<CarrerasByDepartamentoResponse>

    /**
     * Busca carreras por nombre
     * GET /api/carreras/buscar
     */
    @GET("/api/carreras/buscar")
    suspend fun buscarCarreras(@Query("nombre") nombre: String): Response<CarrerasActivasResponse>

    // ========== ENDPOINTS DE DEPARTAMENTOS ==========

    /**
     * Obtiene todos los departamentos activos
     * GET /api/departamentos/activos (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.DEPARTAMENTOS_ACTIVOS)
    suspend fun getDepartamentosActivos(): Response<DepartamentosActivosResponse>

    /**
     * Obtiene departamento por ID
     * GET /api/departamentos/{id}
     */
    @GET("/api/departamentos/{id}")
    suspend fun getDepartamentoById(@Path("id") departamentoId: Long): Response<Departamento>

    // ========== ENDPOINTS DE CICLOS ==========

    /**
     * Obtiene todos los ciclos disponibles
     * GET /api/ciclos/todos (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.CICLOS_TODOS)
    suspend fun getAllCiclos(): Response<CiclosResponse>

    /**
     * Obtiene ciclos para una carrera específica
     * GET /api/ciclos/carrera/{carreraId} (PÚBLICO)
     */
    @GET("/api/ciclos/carrera/{carreraId}")
    suspend fun getCiclosByCarrera(@Path("carreraId") carreraId: Long): Response<CiclosResponse>

    // ========== ENDPOINTS DE SECCIONES ==========

    /**
     * Obtiene secciones por carrera y ciclo
     * GET /api/secciones/carrera/{carreraId}/ciclo/{cicloId} (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.SECCIONES_BY_CARRERA_CICLO)
    suspend fun getSeccionesByCarreraAndCiclo(
        @Path("carreraId") carreraId: Long,
        @Path("cicloId") cicloId: Long
    ): Response<SeccionesResponse>

    /**
     * Obtiene todas las secciones de una carrera
     * GET /api/secciones/carrera/{carreraId} (PÚBLICO)
     */
    @GET("/api/secciones/carrera/{carreraId}")
    suspend fun getSeccionesByCarrera(@Path("carreraId") carreraId: Long): Response<SeccionesResponse>

    // ========== ENDPOINTS DE UPLOAD ==========

    /**
     * Sube imagen de perfil
     * POST /api/upload/profile-image
     */
    @Multipart
    @POST(AppConfig.Endpoints.UPLOAD_PROFILE_IMAGE)
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part): Response<UploadImageResponse>

    /**
     * Obtiene imagen de perfil actual
     * GET /api/upload/profile-image/current
     */
    @GET(AppConfig.Endpoints.CURRENT_PROFILE_IMAGE)
    suspend fun getCurrentProfileImage(): Response<CurrentImageResponse>

    /**
     * Elimina imagen de perfil
     * DELETE /api/upload/profile-image
     */
    @DELETE(AppConfig.Endpoints.UPLOAD_PROFILE_IMAGE)
    suspend fun removeProfileImage(): Response<MessageResponse>

    // ========== ENDPOINTS DE HEALTH CHECK ==========

    /**
     * Health check de carreras
     * GET /api/carreras/health (PÚBLICO)
     */
    @GET(AppConfig.Endpoints.HEALTH_CARRERAS)
    suspend fun getCarrerasHealth(): Response<HealthResponse>

    /**
     * Health check de aulas
     * GET /api/aulas/health
     */
    @GET(AppConfig.Endpoints.HEALTH_AULAS)
    suspend fun getAulasHealth(): Response<HealthResponse>

    /**
     * Health check de upload
     * GET /api/upload/health
     */
    @GET("/api/upload/health")
    suspend fun getUploadHealth(): Response<HealthResponse>
}

/**
 * Interface para endpoints que NO requieren autenticación
 * Usada con el cliente público de Retrofit
 */
interface PublicApiService {

    /**
     * Obtiene todas las carreras activas
     */
    @GET(AppConfig.Endpoints.CARRERAS_ACTIVAS)
    suspend fun getCarrerasActivas(): Response<CarrerasActivasResponse>

    /**
     * Obtiene carreras por departamento
     */
    @GET(AppConfig.Endpoints.CARRERAS_BY_DEPARTAMENTO)
    suspend fun getCarrerasByDepartamento(@Path("departamentoId") departamentoId: Long): Response<CarrerasByDepartamentoResponse>

    /**
     * Obtiene departamentos activos
     */
    @GET(AppConfig.Endpoints.DEPARTAMENTOS_ACTIVOS)
    suspend fun getDepartamentosActivos(): Response<DepartamentosActivosResponse>

    /**
     * Obtiene todos los ciclos
     */
    @GET(AppConfig.Endpoints.CICLOS_TODOS)
    suspend fun getAllCiclos(): Response<CiclosResponse>

    /**
     * Obtiene ciclos por carrera
     */
    @GET("/api/ciclos/carrera/{carreraId}")
    suspend fun getCiclosByCarrera(@Path("carreraId") carreraId: Long): Response<CiclosResponse>

    /**
     * Obtiene secciones por carrera y ciclo
     */
    @GET(AppConfig.Endpoints.SECCIONES_BY_CARRERA_CICLO)
    suspend fun getSeccionesByCarreraAndCiclo(
        @Path("carreraId") carreraId: Long,
        @Path("cicloId") cicloId: Long
    ): Response<SeccionesResponse>

    /**
     * Obtiene secciones por carrera
     */
    @GET("/api/secciones/carrera/{carreraId}")
    suspend fun getSeccionesByCarrera(@Path("carreraId") carreraId: Long): Response<SeccionesResponse>

    /**
     * Login tradicional
     */
    @POST(AppConfig.Endpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registro de usuario
     */
    @POST(AppConfig.Endpoints.USUARIO_REGISTER)
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    /**
     * Health checks
     */
    @GET(AppConfig.Endpoints.HEALTH_CARRERAS)
    suspend fun getCarrerasHealth(): Response<HealthResponse>

    /**
     * Anuncios generales
     */
    @GET(AppConfig.Endpoints.ANUNCIOS_GENERAL)
    suspend fun getAnunciosGenerales(): Response<List<Anuncio>>

    @GET(AppConfig.Endpoints.ANUNCIOS_TODOS)
    suspend fun getTodosLosAnuncios(): Response<List<Anuncio>>
}

/**
 * Interface específica para uploads de archivos
 * Configurada con timeouts más largos
 */
interface UploadApiService {

    /**
     * Sube imagen de perfil con timeout extendido
     */
    @Multipart
    @POST(AppConfig.Endpoints.UPLOAD_PROFILE_IMAGE)
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part): Response<UploadImageResponse>

    /**
     * Sube archivo para anuncio
     */
    @Multipart
    @POST("/api/upload/anuncio-file")
    suspend fun uploadAnuncioFile(@Part file: MultipartBody.Part): Response<UploadImageResponse>
}