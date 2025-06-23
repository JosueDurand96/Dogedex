package com.durand.dogedex.data.repository

import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.Request.oficial.LoginRequest
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.newApiOficialService
import com.durand.dogedex.data.response.oficial.LoginResponse

class NewOficialRepository {

    suspend fun login(loginRequest: LoginRequest): ApiResponseStatus<LoginResponse> =
        makeNetworkCall {
            newApiOficialService.login(loginRequest, "application/json")
        }
}