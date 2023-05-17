@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.User
import com.durand.dogedex.api.dto.AgregarMascotaPerdidaDTO
import com.durand.dogedex.api.repository.NewRepository
import com.durand.dogedex.api.response.consultar_mascotas.ConsultarDetalleMascota
import com.durand.dogedex.api.response.consultarmascotas.DetalleMascota
import kotlinx.coroutines.launch

class CanPerdidoViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private var latitude = ""
    private var longitude = ""

    private val _list = MutableLiveData<List<ConsultarDetalleMascota>>(emptyList())
    val list: LiveData<List<ConsultarDetalleMascota>> = _list

    val viewState = MutableLiveData<CanPerdidoEvent>()
    val loading = MutableLiveData(false)

    val estado = MutableLiveData("")
    val fecha = MutableLiveData("")
    val comentarios = MutableLiveData("")
    val nombreMascota = MutableLiveData("")

    private var user: User? = null

    init {
        executeConsultarMascotasPorId()
    }

    fun setUserProfile(user: User?) {
        this.user = user
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun executeConsultarMascotasPorId() = viewModelScope.launch {
        try {
            loading.postValue(true)
            when (val res: ApiResponseStatus<List<ConsultarDetalleMascota>> = repository.consultarMascotasPorId(user?.id ?: -1)) {
                is ApiResponseStatus.Error -> {
                    viewState.postValue(
                        CanPerdidoEvent.Error(
                            "No tiene mascotas registradas."
                        )
                    )
                }

                is ApiResponseStatus.Success -> {
                    _list.postValue(res.data)
                }

                else -> {
                    viewState.postValue(CanPerdidoEvent.None)
                }
            }
        } finally {
            loading.postValue(false)
        }
    }

    fun setCoordinates(latitude: String, longitude: String) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun executeAgregarMascotaPerdida() = viewModelScope.launch {
        try {
            loading.postValue(true)
            val param = buildAgregarMascotaPerdidaDTO()
            when (repository.agregarMascotaPerdida(param)) {
                is ApiResponseStatus.Error -> {
                    viewState.postValue(
                        CanPerdidoEvent.Error(
                            "Error al intentar agregar mascota"
                        )
                    )
                }

                is ApiResponseStatus.Success -> {
                    cleanValues()
                    viewState.postValue(
                        CanPerdidoEvent.SuccessAgregarMascotaPerdida(
                            "Se agregó la mascota perdida"
                        )
                    )
                }

                else -> {
                    viewState.postValue(CanPerdidoEvent.None)
                }
            }
        } finally {
            loading.postValue(false)
        }
    }

    private fun cleanValues() {
        comentarios.value = ""
        estado.value = ""
        fecha.value = ""
        nombreMascota.value = ""
    }

    private fun buildAgregarMascotaPerdidaDTO(): AgregarMascotaPerdidaDTO {
        return AgregarMascotaPerdidaDTO(
            descripcion = comentarios.value!!,
            estado = estado.value!!,
            fechaPerdida = fecha.value!!,
            idMascota = _list.value!!.firstOrNull { it.nombre == nombreMascota.value!! }?.idMascota ?: -1,
            idUsuario = user?.id ?: -1,
            latitud = latitude,
            longitud = longitude
        )
    }

    sealed interface CanPerdidoEvent {
        data class Error(val msg: String) : CanPerdidoEvent
        data class SuccessAgregarMascotaPerdida(val msg: String) : CanPerdidoEvent
        object None : CanPerdidoEvent
    }


}