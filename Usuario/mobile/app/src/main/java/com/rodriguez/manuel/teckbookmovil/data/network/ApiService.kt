package com.rodriguez.manuel.teckbookmovil.data.network

import com.rodriguez.manuel.teckbookmovil.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 📡 API SERVICE - Interface para comunicación con el backend
 * Versión simplificada sin dependencias externas
 */
interface ApiService {

    // ===============================================
    // 🏥 HEALTH CHECKS
    // ===============================================

    @GET("carreras/health")
    suspend fun healthCheckCarreras(): Response<HealthResponse>

    @GET("departamentos/health")
    suspend fun healthCheckDepartamentos(): Response<HealthResponse>

    // ===============================================
    // 🔐 AUTENTICACIÓN
    // ===============================================

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("auth/google-login")
    suspend fun getGoogleLoginUrl(): Response<GoogleLoginResponse>

    @GET("auth/user")
    suspend fun getUserInfo(@Header("Authorization") token: String): Response<UserResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>

    // ===============================================
    // 👤 USUARIOS
    // ===============================================

    @POST("usuarios/register")
    suspend fun registerUser(@Body user: RegisterRequest): Response<RegisterResponse>

    @GET("usuarios/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>

    // ===============================================
    // 🏢 DEPARTAMENTOS (PÚBLICOS)
    // ===============================================

    @GET("departamentos/activos")
    suspend fun getDepartamentosActivos(): Response<DepartamentosResponse>

    // ===============================================
    // 📚 CARRERAS (PÚBLICOS)
    // ===============================================

    @GET("carreras/activas")
    suspend fun getCarrerasActivas(): Response<CarrerasResponse>

    @GET("carreras/departamento/{departamentoId}/activas")
    suspend fun getCarrerasActivasByDepartamento(@Path("departamentoId") departamentoId: Long): Response<CarrerasResponse>

    // ===============================================
    // 📖 CICLOS (PÚBLICOS)
    // ===============================================

    @GET("ciclos/todos")
    suspend fun getAllCiclos(): Response<CiclosResponse>

    // ===============================================
    // 🏫 SECCIONES (PÚBLICOS)
    // ===============================================

    @GET("secciones/carrera/{carreraId}")
    suspend fun getSeccionesByCarrera(@Path("carreraId") carreraId: Long): Response<SeccionesResponse>

    // ===============================================
    // 🏫 AULAS VIRTUALES (REQUIEREN AUTH)
    // ===============================================

    @GET("aulas")
    suspend fun getAulasDelUsuario(@Header("Authorization") token: String): Response<AulasResponse>

    @GET("aulas/{aulaId}")
    suspend fun getAulaById(
        @Path("aulaId") aulaId: Long,
        @Header("Authorization") token: String
    ): Response<AulaDetalleResponse>

    // ===============================================
    // 📢 ANUNCIOS (REQUIEREN AUTH)
    // ===============================================

    @GET("aulas/{aulaId}/anuncios")
    suspend fun getAnunciosDeAula(
        @Path("aulaId") aulaId: Long,
        @Header("Authorization") token: String
    ): Response<AnunciosResponse>

    // ===============================================
    // 📨 INVITACIONES (REQUIEREN AUTH)
    // ===============================================

    @POST("invitaciones/enviar")
    suspend fun enviarInvitacion(
        @Body invitacion: EnviarInvitacionRequest,
        @Header("Authorization") token: String
    ): Response<EnviarInvitacionResponse>

    @POST("invitaciones/aceptar/{codigoInvitacion}")
    suspend fun aceptarInvitacion(
        @Path("codigoInvitacion") codigo: String,
        @Header("Authorization") token: String
    ): Response<AceptarInvitacionResponse>

    @GET("invitaciones/pendientes")
    suspend fun getInvitacionesPendientes(@Header("Authorization") token: String): Response<InvitacionesResponse>
}