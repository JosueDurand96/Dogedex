package com.durand.dogedex.api.repository

import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.DogsApi
import com.durand.dogedex.api.dto.SignUpDTO
import com.durand.dogedex.api.dto.UserDTOMapper
import com.durand.dogedex.api.makeNetworkCall
import com.durand.dogedex.api.response.User

class AuthRepository {
    suspend fun signUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ): ApiResponseStatus<User> = makeNetworkCall {
        val signUpDTO = SignUpDTO(email,password, passwordConfirmation)
        val signUpApiResponse = DogsApi.retrofitService.signUp(signUpDTO)
        if (!signUpApiResponse.isSuccess) {
            throw Exception(signUpApiResponse.message)
        }

        val userDTO = signUpApiResponse.data.user
        val userDTOMapper = UserDTOMapper()
        userDTOMapper.fromUserDTOToUserDomain(userDTO)
    }
}