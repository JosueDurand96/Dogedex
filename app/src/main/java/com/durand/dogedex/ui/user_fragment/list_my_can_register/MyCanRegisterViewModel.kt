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

    fun listar(id: Int) = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            when (val res: ApiResponseStatus<List<ListarCanResponse>> = repository.listarMascota(id)) {
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Login Error")
                    _isLoading.postValue(false)
                }

                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Login Loading")
                    // Ya se setea en true arriba
                }

                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Login Success")
                    _list.postValue(res.data)
                    _isLoading.postValue(false)
                }
            }
        } catch (e: Exception) {
            Log.e("josue", "Login Exception: ${e.localizedMessage}")
            _isLoading.postValue(false)
        }
    }
}
