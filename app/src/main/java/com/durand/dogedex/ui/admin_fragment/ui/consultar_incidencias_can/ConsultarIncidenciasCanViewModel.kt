package com.durand.dogedex.ui.admin_fragment.ui.consultar_incidencias_can

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.DniRequest
import com.durand.dogedex.data.request.IdAgresionRequest
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.consultar_agresiones_por_mascota.ConsultarAgresionesPorMascota
import com.durand.dogedex.data.response.consultar_can_agresivo_dni.ConsultarCanAgresivoDni
import kotlinx.coroutines.launch

class ConsultarIncidenciasCanViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {


    private val _listCanesPorDni = MutableLiveData<List<ConsultarCanAgresivoDni>>()
    val listCanesPorDni: LiveData<List<ConsultarCanAgresivoDni>> = _listCanesPorDni

    private val _listAgresionesPorMascota = MutableLiveData<List<ConsultarAgresionesPorMascota>>()
    val listAgresionesPorMascota: LiveData<List<ConsultarAgresionesPorMascota>> = _listAgresionesPorMascota


    fun consultaCanesAgresivoXDni(dniRequest: DniRequest) = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<List<ConsultarCanAgresivoDni>> = repository.consultarCanAgresivoDni(dniRequest)){
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success consultaCanesAgresivoXDni")
                    _listCanesPorDni.postValue(res.data!!)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){
            Log.d("josue", "list ${e}")
        }
    }

    fun consultarAgresionesPorMascota(idAgresion: IdAgresionRequest) = viewModelScope.launch {
        try {
            when(val res: ApiResponseStatus<List<ConsultarAgresionesPorMascota>> = repository.consultarAgresionesPorMascota(idAgresion)){
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success consultarAgresionesPorMascota")
                    _listAgresionesPorMascota.postValue(res.data!!)
                    Log.d("josue", "list ${res.data}")
                }
            }
        }catch (e:Exception){
            Log.d("josue", "list ${e}")
        }
    }

}