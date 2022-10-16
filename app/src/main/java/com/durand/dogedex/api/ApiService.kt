package com.durand.dogedex.api

import com.durand.dogedex.ApiServiceInterceptor
import com.durand.dogedex.api.dto.AddDogToUserDTO
import com.durand.dogedex.api.dto.LoginDTO
import com.durand.dogedex.api.dto.SignUpDTO
import com.durand.dogedex.api.response.DefaultResponse
import com.durand.dogedex.api.response.DogListApiResponse
import com.durand.dogedex.api.response.SignUpApiResponse
import com.durand.dogedex.util.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private val okHttpClient = OkHttpClient
    .Builder()
    .addInterceptor(ApiServiceInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
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

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}:true")
    @POST("add_dog_to_user")
    suspend fun addDogToUser(@Body addDogToUserDTO: AddDogToUserDTO): DefaultResponse
}

object DogsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}