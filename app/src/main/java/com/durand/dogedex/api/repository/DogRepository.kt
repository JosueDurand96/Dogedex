package com.durand.dogedex.api.repository

import com.durand.dogedex.R
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.DogsApi.retrofitService
import com.durand.dogedex.api.dto.AddDogToUserDTO
import com.durand.dogedex.api.dto.DogDTOMapper
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DogRepository {

    suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
        return withContext(Dispatchers.IO) {
            val allDogsListDeferred = async { downloadDogs() }
            val userDogsListDeferred = async { getUserDogs() }

            val allDogsListResponse = allDogsListDeferred.await()
            val userDogsListResponse = userDogsListDeferred.await()

            when {
                allDogsListResponse is ApiResponseStatus.Error -> {
                    allDogsListResponse
                }
                userDogsListResponse is ApiResponseStatus.Error -> {
                    userDogsListResponse
                }
                allDogsListResponse is ApiResponseStatus.Success && userDogsListResponse is ApiResponseStatus.Success -> {
                    ApiResponseStatus.Success(listOf<Dog>())
                    ApiResponseStatus.Success(
                        getCollectionList(
                            allDogsListResponse.data,
                            userDogsListResponse.data
                        )
                    )
                }
                else -> {
                    ApiResponseStatus.Error(R.string.error)
                }
            }
        }
    }

    private fun getCollectionList(allDogsList: List<Dog>, userDogList: List<Dog>): List<Dog> =
        allDogsList.map {
            if (userDogList.contains(it)) {
                it
            } else {
                Dog(
                    it.id, it.index, "", "", "", "", "",
                    "", "", "", "", inCollection = false
                )
            }
        }.sorted()


    suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }

    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val defaultResponse = retrofitService.addDogToUser(addDogToUserDTO)

        if (!defaultResponse.isSuccess) {
            throw Exception(defaultResponse.message)
        }
    }

    suspend fun getUserDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getUserDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }

    suspend fun getDogByMlId(mlDogId: String){
        val response = retrofitService.getDogByMlId(mlDogId)
        if (!response.isSuccess) {
            throw Exception(response.message)
        }
    }
}