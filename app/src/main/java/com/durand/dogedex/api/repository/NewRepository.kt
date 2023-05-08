package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.newApiService
import com.durand.dogedex.api.dto.AddBreedDTO
import com.durand.dogedex.api.dto.AddDogDTO
import com.durand.dogedex.api.dto.AddUserDTO
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.AddBreedResponse
import com.durand.dogedex.api.response.AddDogResponse
import com.durand.dogedex.api.response.AddUserResponse
import com.durand.dogedex.api.response.dangerousdogs.DogProfileResponse
import com.durand.dogedex.api.response.lostpetslist.LostPetDetailResponse

class NewRepository {

    suspend fun getDangerousDogs(): ApiResponseStatus<List<DogProfileResponse>> = makeNetworkCall {
        newApiService.getDangerousDogs().list
    }

    suspend fun addPet(addDogDTO: AddDogDTO): ApiResponseStatus<AddDogResponse> = makeNetworkCall {
        newApiService.addPet(addDogDTO)
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