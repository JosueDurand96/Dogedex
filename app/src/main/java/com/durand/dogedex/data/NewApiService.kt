@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data

import com.durand.dogedex.data.dto.*
import com.durand.dogedex.data.response.AddBreedResponse
import com.durand.dogedex.data.response.AddUserResponse
import com.durand.dogedex.data.response.LoginMasterResponse
import com.durand.dogedex.data.response.agregar_mascota_perdida.AgregarMascotaPerdidaResponse
import com.durand.dogedex.data.response.can_perdido.ListCanPerdidoMasterResponse
import com.durand.dogedex.data.response.consultar_mascotas.ConsultarMascotasMasterResponse
import com.durand.dogedex.data.response.dangerousdogs.DangerousPetListResponse
import com.durand.dogedex.data.response.list_mascotas.ListMascotasMasterResponse
import com.durand.dogedex.data.response.lostpetslist.LostPetsListResponse
import com.durand.dogedex.data.response.registar_can.RegisterCanResponse
import com.durand.dogedex.ui.ApiServiceInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
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
    @POST("consultarListaMascotasPerdidas")
    suspend fun getListCanLost(@Header("Content-Type") content_type: String): ListCanPerdidoMasterResponse

    @POST("autenticarUsuario")
    suspend fun getLogin(@Body addLoginDTO: AddLoginDTO): LoginMasterResponse

    @POST("consultarListaMascotas")
    suspend fun getConsultarListaMascotas(): ListMascotasMasterResponse

    @POST("consultarListaRazasMascotasPeligrosas")
    suspend fun getDangerousPets(): DangerousPetListResponse

    @POST("agregarMascota")
    suspend fun addPet(
        @Body addPetDTO: RegisterCanRequest,
        @Header("Content-Type") content_type: String
    ): RegisterCanResponse

    @POST("agregarUsuario")
    suspend fun addUser(@Body addUserDTO: AddUserDTO): AddUserResponse

    @POST("consultarListaMascotasPerdidas")
    suspend fun getLostPets(): LostPetsListResponse

    @POST("agregarRazaMascota")
    suspend fun addBreed(@Body addBreedDTO: AddBreedDTO): AddBreedResponse

    @POST("consultarMascotas")
    suspend fun consultarMascotas(@Body idUsuario: ConsultarMascotaDTO): ConsultarMascotasMasterResponse

    @POST("agregarMascotaPerdida")
    suspend fun agregarMascotaPerdida(@Body agregarMascotaPerdidaDTO: AgregarMascotaPerdidaDTO): AgregarMascotaPerdidaResponse

}
