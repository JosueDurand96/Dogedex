package com.durand.dogedex.ui.user_fragment.register_can

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.AgregarMascotaRequest
import com.durand.dogedex.data.User
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.data.response.oficial.RegisterCanResponse
import kotlinx.coroutines.launch

class RegisterCanViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    val formState: MutableLiveData<RegisterCanForm> = MutableLiveData(RegisterCanForm())

    val event = MutableLiveData<RegisterCanEvent>(RegisterCanEvent.None)

    private val _list = MutableLiveData<RegisterCanResponse>()
    val list: LiveData<RegisterCanResponse> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun listar(registerCanRequest: RegisterCanRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<RegisterCanResponse> = repository.registerCan(registerCanRequest)) {
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

    fun listarAgresivo(registerCanRequest: RegisterCanRequest) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<RegisterCanResponse> = repository.registerCanAgresivo(registerCanRequest)) {
                is ApiResponseStatus.Error -> {
                    Log.d("RegisterCanAgresorFragment", "Error al registrar can agresivo")
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("RegisterCanAgresorFragment", "Registrando can agresivo...")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("RegisterCanAgresorFragment", "Can agresivo registrado exitosamente")
                    _list.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("RegisterCanAgresorFragment", "Exception al registrar can agresivo: ${e.localizedMessage}")
            _isLoading.postValue(false)
        }
    }

}

sealed interface RegisterCanEvent {
    object Loading : RegisterCanEvent
    object DismissLoading : RegisterCanEvent
    class ShowError(val msg: String = "") : RegisterCanEvent
    object Success: RegisterCanEvent
    object None : RegisterCanEvent
}