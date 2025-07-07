package com.rodriguez.manuel.teckbookmovil.core.network

import com.rodriguez.manuel.teckbookmovil.data.models.auth.*
import com.rodriguez.manuel.teckbookmovil.data.models.aula.*
import com.rodriguez.manuel.teckbookmovil.data.models.carrera.*
import com.rodriguez.manuel.teckbookmovil.data.models.common.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API principal para llamadas AUTENTICADAS (requiere JWT)
 */
interface ApiService {

    // ===================== AUTENTICACIÓN =====================
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/login/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @POST("/api/auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("/api/auth/token-status")
    suspend fun checkTokenStatus(): Response<TokenStatusResponse>

    // ===================== PERFIL DE USUARIO =====================
    @GET("/api/usuarios/profile")
    suspend fun getProfile(): Response<UserInfo>

    @PUT("/api/usuarios/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserInfo>

    // ===================== AULAS =====================
    @GET("/api/aulas/mis")
    suspend fun getMyAulas(): Response<AulasResponse>

    @GET("/api/aulas/{aulaId}")
    suspend fun getAulaById(@Path("aulaId") aulaId: Long): Response<AulaDetailResponse>

    @POST("/api/aulas")
    suspend fun createAula(@Body request: CreateAulaRequest): Response<AulaVirtual>

    @GET("/api/aulas/buscar")
    suspend fun searchAulas(@Query("nombre") nombre: String): Response<BuscarAulasResponse>

    @GET("/api/aulas/{aulaId}/participantes")
    suspend fun getAulaParticipantes(@Path("aulaId") aulaId: Long): Response<ParticipantesResponse>

    @GET("/api/aulas/{aulaId}/anuncios")
    suspend fun getAnunciosDeAula(@Path("aulaId") aulaId: Long): Response<List<Anuncio>>

    @Multipart
    @POST("/api/aulas/{aulaId}/anuncios")
    suspend fun createAnuncioEnAula(
        @Path("aulaId") aulaId: Long,
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("tipo") tipo: RequestBody,
        @Part archivo: MultipartBody.Part? = null
    ): Response<Anuncio>

    // ===================== ANUNCIOS GENERALES =====================
    @GET("/api/anuncios/general")
    suspend fun getAnunciosGenerales(): Response<List<Anuncio>>

    @GET("/api/anuncios/todos")
    suspend fun getTodosLosAnuncios(): Response<List<Anuncio>>

    // ===================== UPLOADS =====================
    @Multipart
    @POST("/api/uploads/profile-image")
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part): Response<UploadImageResponse>

    @GET("/api/uploads/profile-image")
    suspend fun getCurrentProfileImage(): Response<CurrentImageResponse>

    @DELETE("/api/uploads/profile-image")
    suspend fun removeProfileImage(): Response<MessageResponse>

    // ===================== CARRERAS Y FILTROS =====================
    @GET("/api/carreras/activas")
    suspend fun getCarrerasActivas(): Response<CarrerasActivasResponse>

    @GET("/api/carreras/departamento/{departamentoId}")
    suspend fun getCarrerasByDepartamento(@Path("departamentoId") departamentoId: Long): Response<CarrerasByDepartamentoResponse>

    @GET("/api/departamentos/activos")
    suspend fun getDepartamentosActivos(): Response<DepartamentosActivosResponse>

    @GET("/api/ciclos/todos")
    suspend fun getAllCiclos(): Response<CiclosResponse>

    @GET("/api/secciones/carrera/{carreraId}/ciclo/{cicloId}")
    suspend fun getSeccionesByCarreraAndCiclo(
        @Path("carreraId") carreraId: Long,
        @Path("cicloId") cicloId: Long
    ): Response<SeccionesResponse>
}


/**
 * API PÚBLICA para endpoints sin autenticación JWT.
 */
interface PublicApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/login/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @POST("/api/usuarios/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/carreras/activas")
    suspend fun getCarrerasActivas(): Response<CarrerasActivasResponse>

    @GET("/api/carreras/departamento/{departamentoId}")
    suspend fun getCarrerasByDepartamento(@Path("departamentoId") departamentoId: Long): Response<CarrerasByDepartamentoResponse>

    @GET("/api/departamentos/activos")
    suspend fun getDepartamentosActivos(): Response<DepartamentosActivosResponse>

    @GET("/api/ciclos/todos")
    suspend fun getAllCiclos(): Response<CiclosResponse>

    @GET("/api/secciones/carrera/{carreraId}/ciclo/{cicloId}")
    suspend fun getSeccionesByCarreraAndCiclo(
        @Path("carreraId") carreraId: Long,
        @Path("cicloId") cicloId: Long
    ): Response<SeccionesResponse>

    @GET("/api/anuncios/general")
    suspend fun getAnunciosGenerales(): Response<List<Anuncio>>
}


/**
 * API exclusiva para uploads grandes.
 */
interface UploadApiService {

    @Multipart
    @POST("/api/uploads/profile-image")
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part): Response<UploadImageResponse>

    @Multipart
    @POST("/api/uploads/anuncio-file")
    suspend fun uploadAnuncioFile(@Part file: MultipartBody.Part): Response<UploadImageResponse>
}
