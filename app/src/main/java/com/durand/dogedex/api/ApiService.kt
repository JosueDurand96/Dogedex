package com.durand.dogedex.api

import com.durand.dogedex.api.dto.LoginDTO
import com.durand.dogedex.api.dto.SignUpDTO
import com.durand.dogedex.api.response.DogListApiResponse
import com.durand.dogedex.api.response.SignUpApiResponse
import com.durand.dogedex.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService {
    @GET("dogs")
    suspend fun getAllDogs(): DogListApiResponse

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): SignUpApiResponse

    @POST("sign_in")
    suspend fun login(@Body loginDTO: LoginDTO): SignUpApiResponse
}

object DogsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}