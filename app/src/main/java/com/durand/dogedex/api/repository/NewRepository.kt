package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.newApiService
import com.durand.dogedex.api.dto.AddBreedDTO
import com.durand.dogedex.api.dto.AddPetDTO
import com.durand.dogedex.api.dto.AddUserDTO
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.AddBreedResponse
import com.durand.dogedex.api.response.AddDogResponse
import com.durand.dogedex.api.response.AddUserResponse
import com.durand.dogedex.api.response.dangerousdogs.PetProfileResponse
import com.durand.dogedex.api.response.list_mascotas.ListaMascotas
import com.durand.dogedex.api.response.lostpetslist.LostPetDetailResponse

class NewRepository {


    suspend fun getConsultarListaMascotas(): ApiResponseStatus<List<ListaMascotas>> = makeNetworkCall {
        newApiService.getConsultarListaMascotas().listaMascotas
    }

    suspend fun getDangerousDogs(): ApiResponseStatus<List<PetProfileResponse>> = makeNetworkCall {
        newApiService.getDangerousPets().list
    }

    suspend fun addPet(addPetDTO: AddPetDTO): ApiResponseStatus<AddDogResponse> = makeNetworkCall {
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

}