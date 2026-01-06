@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import kotlinx.coroutines.launch

class CanAgresivoReportViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _list = MutableLiveData<List<ListarCanResponse>>(emptyList())
    val list: LiveData<List<ListarCanResponse>> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun listar(id: Long) = viewModelScope.launch {
        _isLoading.postValue(true)
        Log.d("CanAgresivoReportViewModel", "=== listar INICIADO ===")
        Log.d("CanAgresivoReportViewModel", "idUsuario: $id")
        try {
            Log.d("CanAgresivoReportViewModel", "Llamando repository.listarCanAgresivo()")
            val res: ApiResponseStatus<List<ListarCanResponse>> = repository.listarCanAgresivo(id)
            Log.d("CanAgresivoReportViewModel", "Respuesta recibida del repositorio: ${res.javaClass.simpleName}")
            
            when (res) {
                is ApiResponseStatus.Error -> {
                    Log.e("CanAgresivoReportViewModel", "=== ERROR ===")
                    Log.e("CanAgresivoReportViewModel", "Error al listar mascotas agresivas: ${res.message}")
                    Log.e("CanAgresivoReportViewModel", "Tipo de mensaje: ${res.message.javaClass.simpleName}")
                    _list.postValue(emptyList())
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("CanAgresivoReportViewModel", "=== LOADING ===")
                    Log.d("CanAgresivoReportViewModel", "Cargando lista de mascotas agresivas...")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("CanAgresivoReportViewModel", "=== SUCCESS ===")
                    Log.d("CanAgresivoReportViewModel", "Lista obtenida exitosamente: ${res.data.size} elementos")
                    if (res.data.isNotEmpty()) {
                        Log.d("CanAgresivoReportViewModel", "Primer elemento: ${res.data[0].nombre}, ID: ${res.data[0].id}")
                        Log.d("CanAgresivoReportViewModel", "Todos los elementos:")
                        res.data.forEachIndexed { index, item ->
                            Log.d("CanAgresivoReportViewModel", "  [$index] ${item.nombre} - ${item.raza} - ID: ${item.id}")
                        }
                    } else {
                        Log.w("CanAgresivoReportViewModel", "La lista está vacía")
                    }
                    Log.d("CanAgresivoReportViewModel", "Publicando lista en LiveData")
                    _list.postValue(res.data)
                    Log.d("CanAgresivoReportViewModel", "Lista publicada, isLoading = false")
                    _isLoading.postValue(false)
                }
            }
            Log.d("CanAgresivoReportViewModel", "=== listar COMPLETADO ===")
        } catch (e: Exception) {
            Log.e("CanAgresivoReportViewModel", "=== EXCEPCIÓN ===")
            Log.e("CanAgresivoReportViewModel", "Excepción al listar mascotas agresivas: ${e.message}", e)
            e.printStackTrace()
            _list.postValue(emptyList())
            _isLoading.postValue(false)
        }
    }
}
