package com.durand.dogedex.data.repository

import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.DogsApi
import com.durand.dogedex.data.dto.LoginDTO
import com.durand.dogedex.data.dto.SignUpDTO
import com.durand.dogedex.data.dto.UserDTOMapper
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.User

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): ApiResponseStatus<User> = makeNetworkCall {
        val loginDTO = LoginDTO(email,password)
        val loginApiResponse = DogsApi.retrofitService.login(loginDTO)
        if (!loginApiResponse.isSuccess) {
            throw Exception(loginApiResponse.message)
        }

        val userDTO = loginApiResponse.data.user
        val userDTOMapper = UserDTOMapper()
        userDTOMapper.fromUserDTOToUserDomain(userDTO)
    }

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