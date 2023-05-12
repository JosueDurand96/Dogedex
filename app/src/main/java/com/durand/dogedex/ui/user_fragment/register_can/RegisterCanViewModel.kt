package com.durand.dogedex.ui.user_fragment.register_can

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.durand.dogedex.api.repository.NewRepository

class HomeViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun test() {
    }
    
}