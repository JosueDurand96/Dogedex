package com.durand.dogedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    fun getRecognizedDog(mlDogId: String){
        viewModelScope.launch {

        }
    }
}