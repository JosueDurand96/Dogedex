package com.durand.dogedex.ui.auth.oficial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.Request.oficial.LoginRequest
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login


    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<LoginResponse> = repository.login(loginRequest)){
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Login Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Login Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Login Success ")
                    _login.postValue(res.data)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){
            Log.d("josue", "list ${e}")
        }
    }
}