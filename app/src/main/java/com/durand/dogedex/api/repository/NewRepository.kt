@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.dto.*
import com.durand.dogedex.api.newApiService
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.AddBreedResponse
import com.durand.dogedex.api.response.AddDogResponse
import com.durand.dogedex.api.response.AddUserResponse
import com.durand.dogedex.api.response.LoginMasterResponse
import com.durand.dogedex.api.response.agregar_mascota_perdida.AgregarMascotaPerdidaResponse
import com.durand.dogedex.api.response.consultar_mascotas.ConsultarDetalleMascota
import com.durand.dogedex.api.response.consultarmascotas.DetalleMascota
import com.durand.dogedex.api.response.dangerousdogs.PetProfileResponse
import com.durand.dogedex.api.response.list_mascotas.ListaMascotas
import com.durand.dogedex.api.response.lostpetslist.LostPetDetailResponse
import com.durand.dogedex.api.response.registar_can.RegisterCanResponse

class NewRepository {


    suspend fun getLogin(addLoginDTO: AddLoginDTO): ApiResponseStatus<LoginMasterResponse> = makeNetworkCall {
        newApiService.getLogin(addLoginDTO)
    }
    suspend fun getConsultarListaMascotas(): ApiResponseStatus<List<ListaMascotas>> = makeNetworkCall {
        newApiService.getConsultarListaMascotas().listaMascotas
    }

    suspend fun getDangerousDogs(): ApiResponseStatus<List<PetProfileResponse>> = makeNetworkCall {
        newApiService.getDangerousPets().list
    }

    suspend fun addPet(addPetDTO: RegisterCanRequest): ApiResponseStatus<RegisterCanResponse> = makeNetworkCall {
        newApiService.addPet(addPetDTO)
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