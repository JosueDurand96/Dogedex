package com.durand.dogedex.data

import com.durand.dogedex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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
    }catch (e: HttpException){
        val errorMessage = if (e.code() == 401){
            R.string.password_user_incorrect
        }else{
            R.string.error_know
        }
        ApiResponseStatus.Error(errorMessage)
    } catch (e: Exception) {
        val errorMessage = when (e.message) {
            "sign_up_error" -> R.string.sign_up_error
            "sign_ip_error" -> R.string.sign_in_error
            "user_already_exists" -> R.string.user_already_exists
            "error_adding_dog" -> R.string.error_adding_dog
            else -> R.string.error_know
        }
        ApiResponseStatus.Error(errorMessage)
    }
}
