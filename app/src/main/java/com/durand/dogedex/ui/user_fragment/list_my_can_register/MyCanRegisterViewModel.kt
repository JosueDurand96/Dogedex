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

class MyCanRegisterViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _list = MutableLiveData<List<ListarCanResponse>>(emptyList())
    val list: LiveData<List<ListarCanResponse>> = _list

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun listar(id: Long) = viewModelScope.launch {
        _isLoading.postValue(true)
        Log.d("MyCanRegisterViewModel", "=== listar INICIADO ===")
        Log.d("MyCanRegisterViewModel", "idUsuario: $id")
        try {
            Log.d("MyCanRegisterViewModel", "Llamando repository.listarMascota()")
            val res: ApiResponseStatus<List<ListarCanResponse>> = repository.listarMascota(id)
            Log.d("MyCanRegisterViewModel", "Respuesta recibida del repositorio: ${res.javaClass.simpleName}")
            
            when (res) {
                is ApiResponseStatus.Error -> {
                    Log.e("MyCanRegisterViewModel", "=== ERROR ===")
                    Log.e("MyCanRegisterViewModel", "Error al listar mascotas: ${res.message}")
                    Log.e("MyCanRegisterViewModel", "Tipo de mensaje: ${res.message.javaClass.simpleName}")
                    _list.postValue(emptyList())
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("MyCanRegisterViewModel", "=== LOADING ===")
                    Log.d("MyCanRegisterViewModel", "Cargando lista de mascotas...")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("MyCanRegisterViewModel", "=== SUCCESS ===")
                    Log.d("MyCanRegisterViewModel", "Lista obtenida exitosamente: ${res.data.size} elementos")
                    if (res.data.isNotEmpty()) {
                        Log.d("MyCanRegisterViewModel", "Primer elemento: ${res.data[0].nombre}, ID: ${res.data[0].id}")
                        Log.d("MyCanRegisterViewModel", "Todos los elementos:")
                        res.data.forEachIndexed { index, item ->
                            Log.d("MyCanRegisterViewModel", "  [$index] ${item.nombre} - ${item.raza} - ID: ${item.id}")
                        }
                    } else {
                        Log.w("MyCanRegisterViewModel", "La lista está vacía")
                    }
                    Log.d("MyCanRegisterViewModel", "Publicando lista en LiveData")
                    _list.postValue(res.data)
                    Log.d("MyCanRegisterViewModel", "Lista publicada, isLoading = false")
                    _isLoading.postValue(false)
                }
            }
            Log.d("MyCanRegisterViewModel", "=== listar COMPLETADO ===")
        } catch (e: Exception) {
            Log.e("MyCanRegisterViewModel", "=== EXCEPCIÓN ===")
            Log.e("MyCanRegisterViewModel", "Excepción al listar mascotas: ${e.message}", e)
            e.printStackTrace()
            _list.postValue(emptyList())
            _isLoading.postValue(false)
        }
    }
}
