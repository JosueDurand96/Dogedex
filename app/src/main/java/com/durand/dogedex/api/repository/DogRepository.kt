package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.DogsApi.retrofitService
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
}