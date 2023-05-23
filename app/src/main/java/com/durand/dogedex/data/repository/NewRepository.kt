@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.repository

import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.dto.*
import com.durand.dogedex.data.newApiService
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.response.AddBreedResponse
import com.durand.dogedex.data.response.AddUserResponse
import com.durand.dogedex.data.response.LoginMasterResponse
import com.durand.dogedex.data.response.agregar_mascota_perdida.AgregarMascotaPerdidaResponse
import com.durand.dogedex.data.response.can_perdido.ListCanPerdido
import com.durand.dogedex.data.response.can_perdido.ListCanPerdidoMasterResponse
import com.durand.dogedex.data.response.consultar_mascotas.ConsultarDetalleMascota
import com.durand.dogedex.data.response.dangerousdogs.PetProfileResponse
import com.durand.dogedex.data.response.list_mascotas.ListaMascotas
import com.durand.dogedex.data.response.list_mascotas.MascotaResponse
import com.durand.dogedex.data.response.lostpetslist.LostPetDetailResponse
import com.durand.dogedex.data.response.registar_can.RegisterCanResponse

class NewRepository {

    suspend fun getListCanLost(): ApiResponseStatus<List<ListCanPerdido>> = makeNetworkCall {
        newApiService.getListCanLost( "application/json").lista
    }
    suspend fun getLogin(addLoginDTO: AddLoginDTO): ApiResponseStatus<LoginMasterResponse> = makeNetworkCall {
        newApiService.getLogin(addLoginDTO)
    }
    suspend fun getConsultarListaMascotas(): ApiResponseStatus<List<MascotaResponse>> = makeNetworkCall {
        newApiService.getConsultarListaMascotas().listaMascotas
    }

    suspend fun getDangerousDogs(): ApiResponseStatus<List<PetProfileResponse>> = makeNetworkCall {
        newApiService.getDangerousPets().list
    }

    suspend fun addPet(addPetDTO: RegisterCanRequest): ApiResponseStatus<RegisterCanResponse> = makeNetworkCall {
        newApiService.addPet(addPetDTO, "application/json")
    }

    suspend fun addUser(addUserDTO: AddUserDTO): ApiResponseStatus<AddUserResponse> = makeNetworkCall {
        newApiService.addUser(addUserDTO)
    }

    suspend fun getLostPets(): ApiResponseStatus<List<LostPetDetailResponse>> = makeNetworkCall {
        newApiService.getLostPets().lostPetDetailResponse
    }

    suspend fun addBreed(addBreedDTO: AddBreedDTO): ApiResponseStatus<AddBreedResponse> = makeNetworkCall {
        newApiService.addBreed(addBreedDTO)
    }

    suspend fun consultarMascotasPorId(idUsuario: Int): ApiResponseStatus<List<ConsultarDetalleMascota>> = makeNetworkCall {
        newApiService.consultarMascotas(ConsultarMascotaDTO(idUsuario)).lista
    }

    suspend fun agregarMascotaPerdida(body: AgregarMascotaPerdidaDTO): ApiResponseStatus<AgregarMascotaPerdidaResponse> = makeNetworkCall {
        newApiService.agregarMascotaPerdida(body)
    }

}