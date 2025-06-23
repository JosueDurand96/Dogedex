@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.durand.dogedex.MyApplication
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.response.oficial.LoginResponse
import com.durand.dogedex.data.response.oficial.RegisterResponse
import com.durand.dogedex.ui.ApiServiceInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
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

}