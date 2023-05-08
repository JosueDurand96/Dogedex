package com.durand.dogedex.api

import com.durand.dogedex.ApiServiceInterceptor
import com.durand.dogedex.api.dto.AddBreedDTO
import com.durand.dogedex.api.dto.AddDogDTO
import com.durand.dogedex.api.dto.AddUserDTO
import com.durand.dogedex.api.response.AddBreedResponse
import com.durand.dogedex.api.response.AddDogResponse
import com.durand.dogedex.api.response.AddUserResponse
import com.durand.dogedex.api.response.dangerousdogs.DangerousDogListResponse
import com.durand.dogedex.api.response.lostpetslist.LostPetsListResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private val okHttpClient = OkHttpClient
    .Builder()
    .addInterceptor(ApiServiceInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl("https://app-patitas.azurewebsites.net/api/oper/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val newApiService: NewApiService by lazy {
    retrofit.create(NewApiService::class.java)
}

interface NewApiService {

    @POST("consultarListaRazasMascotasPeligrosas")
    suspend fun getDangerousDogs(): DangerousDogListResponse

    @POST("agregarMascota")
    suspend fun addPet(@Body addDogDTO: AddDogDTO): AddDogResponse

    @POST("agregarUsuario")
    suspend fun addUser(@Body addUserDTO: AddUserDTO): AddUserResponse

    @POST("consultarListaMascotasPerdidas")
    suspend fun getLostPets(): LostPetsListResponse

    @POST("agregarRazaMascota")
    suspend fun addBreed(@Body addBreedDTO: AddBreedDTO): AddBreedResponse

}
