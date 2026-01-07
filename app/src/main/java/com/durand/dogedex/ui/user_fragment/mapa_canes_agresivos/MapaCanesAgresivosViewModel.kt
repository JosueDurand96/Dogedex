package com.durand.dogedex.ui.user_fragment.mapa_canes_agresivos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import kotlinx.coroutines.launch

class MapaCanesAgresivosViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _allAggressiveDogs = MutableLiveData<List<ListarCanResponse>>()
    val allAggressiveDogs: LiveData<List<ListarCanResponse>> = _allAggressiveDogs

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadAggressiveDogs() = viewModelScope.launch {
        _isLoading.postValue(true)
        Log.d("MapaCanesAgresivosViewModel", "Iniciando carga de TODOS los canes agresivos (todos los usuarios)")
        try {
            // Pasar null para obtener todos los canes agresivos de todos los usuarios
            when (val res: ApiResponseStatus<List<ListarCanResponse>> = repository.listarCanAgresivo(null)) {
                is ApiResponseStatus.Error -> {
                    Log.e("MapaCanesAgresivosViewModel", "Error al listar canes agresivos: ${res.message}")
                    _allAggressiveDogs.postValue(emptyList())
                    _isLoading.postValue(false)
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("MapaCanesAgresivosViewModel", "Cargando lista de canes agresivos...")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("MapaCanesAgresivosViewModel", "Lista obtenida exitosamente: ${res.data.size} elementos")
                    // Log de coordenadas para debugging
                    res.data.forEachIndexed { index, dog ->
                        Log.d("MapaCanesAgresivosViewModel", "Can $index: ${dog.nombre}, lat=${dog.latitud}, lng=${dog.longitud}, usuario=${dog.idUsuario}")
                    }
                    _allAggressiveDogs.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("MapaCanesAgresivosViewModel", "Excepción al listar canes agresivos: ${e.message}", e)
            _allAggressiveDogs.postValue(emptyList())
            _isLoading.postValue(false)
        }
    }
}






