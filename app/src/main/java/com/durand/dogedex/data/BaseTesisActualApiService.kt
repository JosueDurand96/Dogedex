@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.durand.dogedex.MyApplication
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.request.oficial.RegistrarCodigoRequest
import com.durand.dogedex.data.request.oficial.ValidarCodigoRequest
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
import retrofit2.http.POST
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
    .baseUrl("https://tesis2025appservices.azurewebsites.net/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val newApiOficialService: NewApiOficialService by lazy {
    retrofit.create(NewApiOficialService::class.java)
}

interface NewApiOficialService {

    @POST("api/v1/Autenticacion/iniciarSesion")
    suspend fun login(
        @Body loginRequest: LoginRequest,
        @Header("Content-Type") content_type: String
    ): LoginResponse

    @POST("api/v1/Autenticacion/registrar")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest,
        @Header("Content-Type") content_type: String
    ): RegisterResponse

    @POST("api/v1/Mascota/registrarMascota")
    suspend fun registrarMascota(
        @Body registerCanRequest: RegisterCanRequest,
        @Header("Content-Type") content_type: String
    ): RegisterCanResponse

    @POST("api/v1/Mascota/registrarMascotaPerdida")
    suspend fun registrarMascotaPerdida(
        @Body registerCanPerdidaRequest: RegisterCanPerdidoRequest,
        @Header("Content-Type") content_type: String
    ): RegisterCanPerdidoResponse

    @GET("api/v1/Mascota/obtenerMascota")
    suspend fun listarMascota(
        @Header("Content-Type") content_type: String
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

}