package com.durand.dogedex.api

import com.durand.dogedex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseStatus<T> = withContext(Dispatchers.IO) {
    try {
        ApiResponseStatus.Success(call())
    } catch (e: Exception) {
        ApiResponseStatus.Error(R.string.error)
    } catch (e: UnknownHostException) {
        ApiResponseStatus.Error(R.string.error_internet)
    } catch (e: Exception) {
        val errorMessage = when (e.message) {
            "sign_up_error" -> R.string.sign_up_error
            "sign_ip_error" -> R.string.sign_in_error
            "user_already_exists" -> R.string.user_already_exists
            else -> R.string.error_know
        }
        ApiResponseStatus.Error(errorMessage)
    }
}
