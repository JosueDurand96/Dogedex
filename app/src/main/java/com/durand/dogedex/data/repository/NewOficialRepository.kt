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
            Log.d("RegisterCanFragment", "registerCanRequest: $registerCanRequest")
            newApiOficialService.registrarMascota(registerCanRequest, "application/json")
        }

    suspend fun registerCanAgresivo(registerCanRequest: RegisterCanRequest): ApiResponseStatus<RegisterCanResponse> =
        makeNetworkCall {
            Log.d("RegisterCanAgresorFragment", "registerCanAgresivoRequest: $registerCanRequest")
            newApiOficialService.registrarCanAgresivo(registerCanRequest, "application/json")
        }

    suspend fun registerCanPerdido(registerCanRequest: RegisterCanPerdidoRequest): ApiResponseStatus<RegisterCanPerdidoResponse> =
        makeNetworkCall {
            newApiOficialService.registrarMascotaPerdida(registerCanRequest, "application/json")
        }

    suspend fun listarMascota(idUsuario: Long): ApiResponseStatus<List<ListarCanResponse>> =
        makeNetworkCall {
            Log.d("NewOficialRepository", "=== listarMascota INICIADO ===")
            Log.d("NewOficialRepository", "idUsuario: $idUsuario")
            try {
                val response = newApiOficialService.listarMascota(
                    content_type = "application/json",
                    idUsuario = idUsuario
                )
                Log.d("NewOficialRepository", "Respuesta recibida: ${response.size} elementos")
                if (response.isNotEmpty()) {
                    Log.d("NewOficialRepository", "Primer elemento: ${response[0].nombre}, ID: ${response[0].id}")
                }
                Log.d("NewOficialRepository", "=== listarMascota EXITOSO ===")
                response
            } catch (e: Exception) {
                Log.e("NewOficialRepository", "ERROR en listarMascota: ${e.message}", e)
                e.printStackTrace()
                throw e
            }
        }

    suspend fun listarMascotaAgresiva(idUsuario: Long): ApiResponseStatus<List<ListarCanResponse>> =
        makeNetworkCall {
            Log.d("NewOficialRepository", "=== listarMascotaAgresiva INICIADO ===")
            Log.d("NewOficialRepository", "idUsuario: $idUsuario")
            try {
                val response = newApiOficialService.listarMascotaAgresiva(
                    content_type = "application/json",
                    idUsuario = idUsuario
                )
                Log.d("NewOficialRepository", "Respuesta recibida: ${response.size} elementos")
                if (response.isNotEmpty()) {
                    Log.d("NewOficialRepository", "Primer elemento: ${response[0].nombre}, ID: ${response[0].id}")
                }
                Log.d("NewOficialRepository", "=== listarMascotaAgresiva EXITOSO ===")
                response
            } catch (e: Exception) {
                Log.e("NewOficialRepository", "ERROR en listarMascotaAgresiva: ${e.message}", e)
                e.printStackTrace()
                throw e
            }
        }

    suspend fun listarMascotaPerdida(): ApiResponseStatus<List<ListarCanPerdidoResponse>> =
        makeNetworkCall {
            newApiOficialService.listarMascotaPerdida(
                content_type = "application/json",
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