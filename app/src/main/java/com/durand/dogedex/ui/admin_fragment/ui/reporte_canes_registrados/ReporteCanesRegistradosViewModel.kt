package com.durand.dogedex.ui.admin_fragment.ui.reporte_canes_registrados

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.list_mascotas.ListaMascotas
import kotlinx.coroutines.launch

class ReporteCanesRegistradosViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    init {
        startReportCanesRegistrados()
    }

    private val _listCanes = MutableLiveData<List<ListaMascotas>>()

    val listCanes: LiveData<List<ListaMascotas>> = _listCanes

    fun startReportCanesRegistrados() = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<List<ListaMascotas>> = repository.getConsultarListaMascotas()){
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success")
                    _listCanes.postValue(res.data!!)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){ }
    }
}