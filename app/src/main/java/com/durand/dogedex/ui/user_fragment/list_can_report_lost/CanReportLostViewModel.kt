package com.durand.dogedex.ui.user_fragment.list_can_report_lost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import kotlinx.coroutines.launch

class CanReportLostViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _list = MutableLiveData<List<ListarCanPerdidoResponse>>()
    val list: LiveData<List<ListarCanPerdidoResponse>> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading


    fun listar() = viewModelScope.launch {
        _isLoading.postValue(true)
        Log.d("CanReportLostViewModel", "Iniciando listar mascotas perdidas")
        try {
            when (val res: ApiResponseStatus<List<ListarCanPerdidoResponse>> = repository.listarMascotaPerdida()) {
                is ApiResponseStatus.Error -> {
                    Log.e("CanReportLostViewModel", "Error al listar mascotas perdidas: ${res.message}")
                    _list.postValue(emptyList())
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("CanReportLostViewModel", "Cargando lista de mascotas perdidas...")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("CanReportLostViewModel", "Lista obtenida exitosamente: ${res.data.size} elementos")
                    if (res.data.isNotEmpty()) {
                        Log.d("CanReportLostViewModel", "Primer elemento: ${res.data[0].nombre}, Fecha pérdida: ${res.data[0].fechaPerdida}")
                    }
                    _list.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("CanReportLostViewModel", "Excepción al listar mascotas perdidas: ${e.message}", e)
            _list.postValue(emptyList())
            _isLoading.postValue(false)
        }
    }

}