package com.durand.dogedex.ui.auth.oficial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        _error.postValue(null) // Limpiar error anterior
        try {
            when (val res: ApiResponseStatus<LoginResponse> = repository.login(loginRequest)) {
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Login Error: ${res.message}")
                    _error.postValue(res.message)
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Login Loading")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Login Success")
                    _login.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("josue", "Login Exception: ${e.localizedMessage}")
            _isLoading.postValue(false)
        }
    }

}