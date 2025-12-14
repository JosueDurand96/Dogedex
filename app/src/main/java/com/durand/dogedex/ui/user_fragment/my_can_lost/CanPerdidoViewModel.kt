@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.data.response.oficial.RegisterCanPerdidoResponse
import kotlinx.coroutines.launch

class CanPerdidoViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private var latitude = ""
    private var longitude = ""

    private val _list = MutableLiveData<RegisterCanPerdidoResponse>()
    val list: LiveData<RegisterCanPerdidoResponse> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error


    fun listar(registerCanRequest: RegisterCanPerdidoRequest) = viewModelScope.launch{
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<RegisterCanPerdidoResponse> = repository.registerCanPerdido(registerCanRequest)) {
                is ApiResponseStatus.Error -> {
                    Log.e("CanPerdidoViewModel", "Error al registrar mascota perdida: ${res.message}")
                    _error.postValue(res.message)
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("CanPerdidoViewModel", "Registrando mascota perdida...")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("CanPerdidoViewModel", "Mascota perdida registrada exitosamente")
                    _list.postValue(res.data)
                    _error.postValue(null) // Limpiar errores previos
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("CanPerdidoViewModel", "Excepción al registrar mascota perdida: ${e.localizedMessage}", e)
            // Para excepciones, usar un mensaje genérico o null
            _error.postValue(null)
            _isLoading.postValue(false)
        }
    }

}