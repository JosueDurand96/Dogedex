package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.DogsApi.retrofitService
import com.durand.dogedex.api.dto.AddDogToUserDTO
import com.durand.dogedex.api.dto.DogDTOMapper
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.Dog

class DogRepository {
    suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }

    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val defaultResponse = retrofitService.addDogToUser(addDogToUserDTO)

        if (!defaultResponse.isSuccess){
            throw Exception(defaultResponse.message)
        }
    }

    suspend fun getUserDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getUserDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }
}