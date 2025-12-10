package com.durand.dogedex.ui.auth.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.request.oficial.ValidarCodigoRequest
import com.durand.dogedex.data.response.oficial.ValidarCodigoResponse
import kotlinx.coroutines.launch

class ValidarCodigoViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _list = MutableLiveData<ValidarCodigoResponse>()
    val list: LiveData<ValidarCodigoResponse> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun registrarCodigo(validarCodigoRequest: ValidarCodigoRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<ValidarCodigoResponse> =
                repository.postValidarCodigo(
                    validarCodigoRequest = validarCodigoRequest
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