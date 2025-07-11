package com.durand.dogedex.data.repository

import android.util.Log
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.makeNetworkCall
import com.durand.dogedex.data.newApiOficialService
import com.durand.dogedex.data.request.oficial.ActualizarClaveRequest
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.request.oficial.RegistrarCodigoRequest
import com.durand.dogedex.data.request.oficial.ValidarCodigoRequest
import com.durand.dogedex.data.response.oficial.ActualizarClaveResponse
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.data.response.oficial.LoginResponse
import com.durand.dogedex.data.response.oficial.RegisterCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.RegisterCanResponse
import com.durand.dogedex.data.response.oficial.RegisterResponse
import com.durand.dogedex.data.response.oficial.RegistrarCodigoResponse
import com.durand.dogedex.data.response.oficial.ValidarCodigoResponse

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

    suspend fun listarMascota(id: Int): ApiResponseStatus<List<ListarCanResponse>> =
        makeNetworkCall {
            Log.d("josue", "makeNetworkCall")
            Log.d("josue", "id: $id")
            newApiOficialService.listarMascota(
                content_type = "application/json",
                mascotaId = id,
            )
        }

    suspend fun listarMascotaPerdida(id: Int): ApiResponseStatus<List<ListarCanPerdidoResponse>> =
        makeNetworkCall {
            newApiOficialService.listarMascotaPerdida(
                content_type = "application/json",
                mascotaId = id,
            )
        }

    suspend fun posRegistrarCodigo(registrarCodigoRequest: RegistrarCodigoRequest): ApiResponseStatus<RegistrarCodigoResponse> =
        makeNetworkCall {
            newApiOficialService.postRegistrarCodigo(registrarCodigoRequest, "application/json")
        }

    suspend fun postValidarCodigo(validarCodigoRequest: ValidarCodigoRequest): ApiResponseStatus<ValidarCodigoResponse> =
        makeNetworkCall {
            newApiOficialService.postValidarCodigo(validarCodigoRequest, "application/json")
        }

    suspend fun postActualizarClave(actualizarClaveRequest: ActualizarClaveRequest): ApiResponseStatus<ActualizarClaveResponse> =
        makeNetworkCall {
            newApiOficialService.postActualizarClave(actualizarClaveRequest, "application/json")
        }
}