package com.durand.dogedex.ui.auth.oficial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.data.response.oficial.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(registerRequest: RegisterRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<RegisterResponse> = repository.registerUser(registerRequest)) {
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Login Error")
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Login Loading")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Login Success")
                    _register.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("josue", "Login Exception: ${e.localizedMessage}")
            _isLoading.postValue(false)
        }
    }
}