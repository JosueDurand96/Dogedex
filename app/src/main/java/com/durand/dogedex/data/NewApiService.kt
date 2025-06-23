@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.durand.dogedex.MyApplication
import com.durand.dogedex.data.request.*
import com.durand.dogedex.data.dto.*
import com.durand.dogedex.data.response.AddAgressionPetResponse
import com.durand.dogedex.data.response.AddBreedResponse
import com.durand.dogedex.data.response.AddUserResponse
import com.durand.dogedex.data.response.LoginMasterResponse
import com.durand.dogedex.data.response.agregar_agresion_mascota.AgregarAgresionMascotaResponse
import com.durand.dogedex.data.response.agregar_mascota_perdida.AgregarMascotaPerdidaResponse
import com.durand.dogedex.data.response.can_perdido.ListCanPerdidoMasterResponse
import com.durand.dogedex.data.response.consultar_agresiones_por_mascota.ConsultarAgresionesPorMascotaResponse
import com.durand.dogedex.data.response.consultar_can_agresivo_dni.ConsultarCanAgresivoDniResponse
import com.durand.dogedex.data.response.consultar_mascota_dni.ConsultarMascotaDniResponse
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
    .baseUrl("https://be-conap.onrender.com/api/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val newApiService: NewApiService by lazy {
    retrofit.create(NewApiService::class.java)
}

interface NewApiService {
    @POST("agregarAgresionMascotaNV")
    suspend fun registerCanReporteAgresivo(
        @Body addAgressionPetRequest: AddAgressionPetRequest,
        @Header("Content-Type") content_type: String
    ): AddAgressionPetResponse

    @POST("consultarAgresionesPorMascota")
    suspend fun consultarAgresionesPorMascota(
        @Body idAgresion: IdAgresionRequest,
        @Header("Content-Type") content_type: String
    ): ConsultarAgresionesPorMascotaResponse

    @POST("ConsultaCanesAgresivoXDni")
    suspend fun consultarCanAgresivoDni(
        @Body dniRequest: DniRequest,
        @Header("Content-Type") content_type: String
    ): ConsultarCanAgresivoDniResponse

    @POST("agregarAgresionMascota")
    suspend fun getAgregarAgresionMascota(
        @Body addAggressionPetRequest: AddAggressionPetRequest,
        @Header("Content-Type") content_type: String
    ): AgregarAgresionMascotaResponse

    @POST("consultarMascotaPorDni")
    suspend fun getConsultarMascotaDni(
        @Body dniRequest: DniRequest,
        @Header("Content-Type") content_type: String
    ): ConsultarMascotaDniResponse

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
        @Body addPetDTO: AgregarMascotaRequest,
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
