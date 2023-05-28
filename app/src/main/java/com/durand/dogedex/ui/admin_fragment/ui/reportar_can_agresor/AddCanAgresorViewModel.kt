package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.Request.AddAggressionPetRequest
import com.durand.dogedex.data.Request.DniRequest
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.agregar_agresion_mascota.AgregarAgresionMascotaResponse
import com.durand.dogedex.data.response.can_perdido.ListCanPerdido
import com.durand.dogedex.data.response.consultar_mascota_dni.ListMascotaDni
import kotlinx.coroutines.launch

class AddCanAgresorViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private val _listCanes = MutableLiveData<List<ListMascotaDni>>()
    val listCanes: LiveData<List<ListMascotaDni>> = _listCanes

    private val _addCan = MutableLiveData<Boolean>()
    val addCan: LiveData<Boolean> = _addCan

    fun startReportCanesRegistrados(dniRequest: DniRequest) = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<List<ListMascotaDni>> = repository.getConsultarMascotaDni(dniRequest)){
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success ListMascotaDni")
                    _listCanes.postValue(res.data!!)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){
            Log.d("josue", "list ${e}")
        }
    }


    fun addPerroPeligroso(dniRequest: AddAggressionPetRequest) = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<AgregarAgresionMascotaResponse> = repository.getAgregarAgresionMascota(dniRequest)){
                is ApiResponseStatus.Error -> {
                    _addCan.postValue(false)
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success ListMascotaDni")
                    _addCan.postValue(true)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){
            Log.d("josue", "list ${e}")
        }
    }

}