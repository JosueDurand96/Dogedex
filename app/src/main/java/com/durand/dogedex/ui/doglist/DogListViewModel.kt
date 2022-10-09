package com.durand.dogedex.ui.doglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.api.repository.DogRepository
import kotlinx.coroutines.launch

class DogListViewModel: ViewModel() {

    private val _dogList = MutableLiveData<List<Dog>>()
    val dogList: LiveData<List<Dog>>
       get() = _dogList

    private val dogRepository =  DogRepository()

    init {
        downloadDogs()
    }

    private fun downloadDogs(){
        viewModelScope.launch {
          _dogList.value =   dogRepository.downloadDogs()
        }
    }
}