package com.durand.dogedex.data.repository

import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.newApiOficialService
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.data.response.oficial.LoginResponse
import com.durand.dogedex.data.response.oficial.RegisterCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.RegisterCanResponse
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

    suspend fun registerCan(registerCanRequest: RegisterCanRequest): ApiResponseStatus<RegisterCanResponse> =
        makeNetworkCall {
            newApiOficialService.registrarMascota(registerCanRequest, "application/json")
        }

    suspend fun registerCanPerdido(registerCanRequest: RegisterCanPerdidoRequest): ApiResponseStatus<RegisterCanPerdidoResponse> =
        makeNetworkCall {
            newApiOficialService.registrarMascotaPerdida(registerCanRequest, "application/json")
        }

    suspend fun listarMascota(): ApiResponseStatus<List<ListarCanResponse>> =
        makeNetworkCall {
            newApiOficialService.listarMascota( "application/json")
        }

    suspend fun listarMascotaPerdida(): ApiResponseStatus<List<ListarCanPerdidoResponse>> =
        makeNetworkCall {
            newApiOficialService.listarMascotaPerdida( "application/json")
        }
}