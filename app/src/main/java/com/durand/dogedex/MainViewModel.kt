package com.durand.dogedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.repository.DogRepository
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {


    private val dogRepository = DogRepository()

    fun getDogByMlId(mlDogId: String){
        viewModelScope.launch {
            dogRepository.getDogByMlId(mlDogId)
        }
    }
}