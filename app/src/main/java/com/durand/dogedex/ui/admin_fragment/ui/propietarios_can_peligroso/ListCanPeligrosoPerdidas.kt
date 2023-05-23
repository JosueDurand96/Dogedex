package com.durand.dogedex.ui.admin_fragment.ui.propietarios_can_peligroso

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.can_perdido.ListCanPerdido
import kotlinx.coroutines.launch

class ListCanPeligrosoPerdidas(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {


    private val _listCanes = MutableLiveData<List<ListCanPerdido>>()

    val listCanes: LiveData<List<ListCanPerdido>> = _listCanes

    fun startReportCanesRegistrados() = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<List<ListCanPerdido>> = repository.getListCanLost()){
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