@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.durand.dogedex.MyApplication
import com.durand.dogedex.data.request.oficial.ActualizarClaveRequest
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.request.oficial.RegistrarCodigoRequest
import com.durand.dogedex.data.request.oficial.ValidarCodigoRequest
import com.durand.dogedex.data.response.oficial.ActualizarClaveResponse
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.data.response.oficial.LoginResponse
import com.durand.dogedex.data.response.oficial.RegisterCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.RegisterResponse
import com.durand.dogedex.data.response.oficial.RegisterCanResponse
import com.durand.dogedex.data.response.oficial.RegistrarCodigoResponse
import com.durand.dogedex.data.response.oficial.ValidarCodigoResponse
import com.durand.dogedex.ui.ApiServiceInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private val okHttpClient = OkHttpClient
    .Builder()
    .addInterceptor(ChuckerInterceptor(MyApplication.appContext))
    .addInterceptor(ApiServiceInterceptor)
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    )
    .connectTimeout(180, TimeUnit.SECONDS)
    .readTimeout(180, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl("https://dogedex-backend-tesis-2025.onrender.com/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val newApiOficialService: NewApiOficialService by lazy {
    retrofit.create(NewApiOficialService::class.java)
}

interface NewApiOficialService {

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
        @Header("Content-Type") content_type: String
    ): LoginResponse

    @POST("auth/register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest,
        @Header("Content-Type") content_type: String
    ): RegisterResponse

    @POST("api/v1/Can/registrarCan")
    suspend fun registrarMascota(
        @Body registerCanRequest: RegisterCanRequest,
        @Header("Content-Type") content_type: String
    ): RegisterCanResponse

    @POST("api/v1/Mascota/registrarMascotaPerdida")
    suspend fun registrarMascotaPerdida(
        @Body registerCanPerdidaRequest: RegisterCanPerdidoRequest,
        @Header("Content-Type") content_type: String
    ): RegisterCanPerdidoResponse

    @GET("api/v1/Can/listarCan")
    suspend fun listarMascota(
        @Header("Content-Type") content_type: String,
        @Query("idUsuario") idUsuario: Long
    ): List<ListarCanResponse>

    @GET("api/v1/Mascota/obtenerMascotaPerdida")
    suspend fun listarMascotaPerdida(
        @Header("Content-Type") content_type: String
    ): List<ListarCanPerdidoResponse>

    @POST("api/v1/CodigoVerificacion/registrarCodigo")
    suspend fun postRegistrarCodigo(
        @Body registrarCodigoRequest: RegistrarCodigoRequest,
        @Header("Content-Type") content_type: String
    ): RegistrarCodigoResponse

    @POST("api/v1/CodigoVerificacion/validarCodigo")
    suspend fun postValidarCodigo(
        @Body registrarCodigoRequest: ValidarCodigoRequest,
        @Header("Content-Type") content_type: String
    ): ValidarCodigoResponse

    @POST("api/v1/Autenticacion/actualizarClave")
    suspend fun postActualizarClave(
        @Body actualizarClaveRequest: ActualizarClaveRequest,
        @Header("Content-Type") content_type: String
    ): ActualizarClaveResponse
}