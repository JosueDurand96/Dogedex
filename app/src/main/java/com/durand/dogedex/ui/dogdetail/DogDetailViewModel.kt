package com.durand.dogedex.ui.dogdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.DogRepository
import kotlinx.coroutines.launch

class DogDetailViewModel: ViewModel() {

    private val _status = MutableLiveData<ApiResponseStatus<Any>>()
    val status: LiveData<ApiResponseStatus<Any>>
        get() = _status

    private val dogRepository = DogRepository()


    fun addDogToUser(dogId: Long) {
        viewModelScope.launch {
            _status.value = ApiResponseStatus.Loading()
            handleAddDogToUser(dogRepository.addDogToUser(dogId))
        }
    }

    private fun handleAddDogToUser(apiResponseStatus: ApiResponseStatus<Any>) {
        _status.value = apiResponseStatus
    }
}