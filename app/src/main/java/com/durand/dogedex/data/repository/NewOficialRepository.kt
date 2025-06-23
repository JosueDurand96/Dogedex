package com.durand.dogedex.data.repository

import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.newApiOficialService
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.response.oficial.LoginResponse
import com.durand.dogedex.data.response.oficial.RegisterResponse

class NewOficialRepository {

    suspend fun login(loginRequest: LoginRequest): ApiResponseStatus<LoginResponse> =
        makeNetworkCall {
            newApiOficialService.login(loginRequest, "application/json")
        }

    suspend fun registerUser(registerRequest: RegisterRequest): ApiResponseStatus<RegisterResponse> =
        makeNetworkCall {
            newApiOficialService.registerUser(registerRequest, "application/json")
        }
}