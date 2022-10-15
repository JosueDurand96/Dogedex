package com.durand.dogedex.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseStatus<T> = withContext(Dispatchers.IO) {
    try {
        ApiResponseStatus.Success(call())
    } catch (e: Exception) {
        ApiResponseStatus.Error("Error al descargar los datos!")
    } catch (e: UnknownHostException) {
        ApiResponseStatus.Error("No hay Internet!")
    }
}
