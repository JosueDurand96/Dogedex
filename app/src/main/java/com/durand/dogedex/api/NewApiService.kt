@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api

import com.durand.dogedex.api.dto.AddBreedDTO
import com.durand.dogedex.api.dto.AddPetDTO
import com.durand.dogedex.api.dto.AddUserDTO
import com.durand.dogedex.api.dto.AgregarMascotaPerdidaDTO
import com.durand.dogedex.api.dto.ConsultarMascotaDTO
import com.durand.dogedex.api.response.AddBreedResponse
import com.durand.dogedex.api.response.AddDogResponse
import com.durand.dogedex.api.response.AddUserResponse
import com.durand.dogedex.api.response.agregar_mascota_perdida.AgregarMascotaPerdidaResponse
import com.durand.dogedex.api.response.consultarmascotas.ConsultarMascotasResponse
import com.durand.dogedex.api.response.dangerousdogs.DangerousPetListResponse
import com.durand.dogedex.api.response.lostpetslist.LostPetsListResponse
import com.durand.dogedex.ui.ApiServiceInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private val okHttpClient = OkHttpClient
    .Builder()
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
    .baseUrl("https://app-patitas.azurewebsites.net/api/oper/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val newApiService: NewApiService by lazy {
    retrofit.create(NewApiService::class.java)
}

interface NewApiService {

    @POST("consultarListaRazasMascotasPeligrosas")
    suspend fun getDangerousPets(): DangerousPetListResponse

    @POST("agregarMascota")
    suspend fun addPet(@Body addPetDTO: AddPetDTO): AddDogResponse

    @POST("agregarUsuario")
    suspend fun addUser(@Body addUserDTO: AddUserDTO): AddUserResponse

    @POST("consultarListaMascotasPerdidas")
    suspend fun getLostPets(): LostPetsListResponse

    @POST("agregarRazaMascota")
    suspend fun addBreed(@Body addBreedDTO: AddBreedDTO): AddBreedResponse

    @POST("consultarMascotas")
    suspend fun consultarMascotas(@Body idUsuario: ConsultarMascotaDTO): ConsultarMascotasResponse

    @POST("agregarMascotaPerdida")
    suspend fun agregarMascotaPerdida(@Body agregarMascotaPerdidaDTO: AgregarMascotaPerdidaDTO): AgregarMascotaPerdidaResponse

}
