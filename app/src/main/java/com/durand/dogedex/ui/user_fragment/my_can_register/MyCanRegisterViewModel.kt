@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.ui.user_fragment.my_can_register

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.User
import com.durand.dogedex.api.repository.NewRepository
import com.durand.dogedex.api.response.consultar_mascotas.ConsultarDetalleMascota
import com.durand.dogedex.api.response.consultarmascotas.DetalleMascota
import kotlinx.coroutines.launch

class MyCanRegisterViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private val _list = MutableLiveData<List<ConsultarDetalleMascota>>(emptyList())
    val list: LiveData<List<ConsultarDetalleMascota>> = _list

    val loading = MutableLiveData(false)

    private var user: User? = null

    fun setUserProfile(user: User?) {
        this.user = user
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun executeConsultarMascotasPorId() = viewModelScope.launch {
        try {
            loading.postValue(true)
            when (val res: ApiResponseStatus<List<ConsultarDetalleMascota>> = repository.consultarMascotasPorId(user?.id ?: -1)) {
                is ApiResponseStatus.Success -> {
                    _list.postValue(res.data)
                }

                else -> {}
            }
        } finally {
            loading.postValue(false)
        }
    }

}
