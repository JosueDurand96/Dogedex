package com.durand.dogedex.ui.user_fragment.mapa_canes_perdidos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import kotlinx.coroutines.launch
import java.util.*

data class MapFilters(
    var last7Days: Boolean = false,
    var last30Days: Boolean = false,
    var raza: String = "",
    var distrito: String = ""
)

class MapaCanesPerdidosViewModel(
    private val repository: NewOficialRepository = NewOficialRepository()
) : ViewModel() {

    private val _allLostDogs = MutableLiveData<List<ListarCanPerdidoResponse>>()
    val allLostDogs: LiveData<List<ListarCanPerdidoResponse>> = _allLostDogs

    private val _filteredLostDogs = MutableLiveData<List<ListarCanPerdidoResponse>>()
    val filteredLostDogs: LiveData<List<ListarCanPerdidoResponse>> = _filteredLostDogs

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isGeocoding = MutableLiveData(false)
    val isGeocoding: LiveData<Boolean> = _isGeocoding

    private val _availableRazas = MutableLiveData<List<String>>()
    val availableRazas: LiveData<List<String>> = _availableRazas

    private val _availableDistritos = MutableLiveData<List<String>>()
    val availableDistritos: LiveData<List<String>> = _availableDistritos

    val filters = MapFilters()

    fun loadLostDogs() = viewModelScope.launch {
        _isLoading.postValue(true)
        Log.d("MapaCanesPerdidosViewModel", "Iniciando carga de mascotas perdidas")
        try {
            when (val res: ApiResponseStatus<List<ListarCanPerdidoResponse>> = repository.listarMascotaPerdida()) {
                is ApiResponseStatus.Error -> {
                    Log.e("MapaCanesPerdidosViewModel", "Error al listar mascotas perdidas: ${res.message}")
                    _allLostDogs.postValue(emptyList())
                    _filteredLostDogs.postValue(emptyList())
                    _isLoading.postValue(false)
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("MapaCanesPerdidosViewModel", "Cargando lista de mascotas perdidas...")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("MapaCanesPerdidosViewModel", "Lista obtenida exitosamente: ${res.data.size} elementos")
                    _allLostDogs.postValue(res.data)
                    extractFilterOptions(res.data)
                    applyFilters(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("MapaCanesPerdidosViewModel", "Excepción al listar mascotas perdidas: ${e.message}", e)
            _allLostDogs.postValue(emptyList())
            _filteredLostDogs.postValue(emptyList())
            _isLoading.postValue(false)
        }
    }

    private fun extractFilterOptions(dogs: List<ListarCanPerdidoResponse>) {
        val razas = mutableSetOf<String>()
        val distritos = mutableSetOf<String>()

        dogs.forEach { dog ->
            dog.raza?.let { if (it.trim().isNotEmpty()) razas.add(it) }
            dog.distrito?.let { if (it.trim().isNotEmpty()) distritos.add(it) }
        }

        _availableRazas.postValue(razas.sorted())
        _availableDistritos.postValue(distritos.sorted())
    }

    fun applyFilters(dogs: List<ListarCanPerdidoResponse>? = null) {
        val dogsToFilter = dogs ?: _allLostDogs.value ?: emptyList()
        val filtered = mutableListOf<ListarCanPerdidoResponse>()

        dogsToFilter.forEach { dog ->
            var matches = true

            // Filter by date
            if (filters.last7Days || filters.last30Days) {
                try {
                    val fechaPerdida = dog.fechaPerdida?.let { 
                        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(it)
                            ?: java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                    } ?: return@forEach

                    val now = Calendar.getInstance()
                    val cutoffDate = Calendar.getInstance()
                    
                    if (filters.last7Days) {
                        cutoffDate.add(Calendar.DAY_OF_MONTH, -7)
                    } else if (filters.last30Days) {
                        cutoffDate.add(Calendar.DAY_OF_MONTH, -30)
                    }

                    if (fechaPerdida.before(cutoffDate.time)) {
                        matches = false
                    }
                } catch (e: Exception) {
                    Log.e("MapaCanesPerdidosViewModel", "Error parsing date: ${e.message}")
                }
            }

            // Filter by raza
            if (matches && filters.raza.isNotEmpty()) {
                matches = dog.raza == filters.raza
            }

            // Filter by distrito
            if (matches && filters.distrito.isNotEmpty()) {
                matches = dog.distrito == filters.distrito
            }

            if (matches) {
                filtered.add(dog)
            }
        }

        _filteredLostDogs.postValue(filtered)
    }

    fun resetFilters() {
        filters.last7Days = false
        filters.last30Days = false
        filters.raza = ""
        filters.distrito = ""
        applyFilters()
    }
}


