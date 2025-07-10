package com.durand.dogedex.ui.auth.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.request.oficial.RegistrarCodigoRequest
import com.durand.dogedex.data.response.oficial.RegistrarCodigoResponse
import kotlinx.coroutines.launch

class RegistrarCodigoViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _list = MutableLiveData<RegistrarCodigoResponse>()
    val list: LiveData<RegistrarCodigoResponse> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun registrarCodigo(registrarCodigoRequest: RegistrarCodigoRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<RegistrarCodigoResponse> =
                repository.posRegistrarCodigo(
                    registrarCodigoRequest = registrarCodigoRequest
                )
            ) {
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
                    _list.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("josue", "Login Exception: ${e.localizedMessage}")
            _isLoading.postValue(false)
        }
    }
}