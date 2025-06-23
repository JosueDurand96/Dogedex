package com.durand.dogedex.ui.user_fragment.register_can

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.AgregarMascotaRequest
import com.durand.dogedex.data.User
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.registar_can.RegisterCanResponse
import kotlinx.coroutines.launch

class RegisterCanViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private var user: User? = null

    val formState: MutableLiveData<RegisterCanForm> = MutableLiveData(RegisterCanForm())

    val event = MutableLiveData<RegisterCanEvent>(RegisterCanEvent.None)

    private val _list = MutableLiveData<RegisterCanResponse>()
    val list: LiveData<RegisterCanResponse> = _list
    fun setUserProfile(user: User?) {
        this.user = user
    }

     fun registerCan(add: AgregarMascotaRequest) = viewModelScope.launch {
        try {
            when (val res = repository.addPet(add)) {
                is ApiResponseStatus.Success -> {
                    _list.postValue(res.data!!)
                }

                else -> {}
            }
        } finally { }
    }


}

sealed interface RegisterCanEvent {
    object Loading : RegisterCanEvent
    object DismissLoading : RegisterCanEvent
    class ShowError(val msg: String = "") : RegisterCanEvent
    object Success: RegisterCanEvent
    object None : RegisterCanEvent
}