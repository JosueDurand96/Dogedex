package com.durand.dogedex.ui.user_fragment.register_can

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.User
import com.durand.dogedex.api.repository.NewRepository
import kotlinx.coroutines.launch

class RegisterCanViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private var user: User? = null

    val formState: MutableLiveData<RegisterCanForm> = MutableLiveData(RegisterCanForm())

    val event = MutableLiveData<RegisterCanEvent>(RegisterCanEvent.None)


    fun setUserProfile(user: User?) {
        this.user = user
    }

    fun registerCan() = viewModelScope.launch {
        try {
            event.value = RegisterCanEvent.Loading
            val validate = formState.value?.validate() ?: false

            if (validate.not()) {
                event.value = RegisterCanEvent.ShowError("Completa todos los campos")
                return@launch
            }

            formState.value!!.toDto(user?.id!!.toInt()).let {
                when (repository.addPet(it)) {
                    is ApiResponseStatus.Error -> {
                        event.postValue(RegisterCanEvent.ShowError("Error el intentar registrar"))
                    }

                    is ApiResponseStatus.Success -> {
                       Log.d("JOSUEEEE","EXITOOOO")
                    }

                    else -> {
                        event.postValue(RegisterCanEvent.None)
                    }
                }
            }
        } finally {
            event.value = RegisterCanEvent.DismissLoading
        }

    }

}

sealed interface RegisterCanEvent {
    object Loading : RegisterCanEvent
    object DismissLoading : RegisterCanEvent
    class ShowError(val msg: String = "") : RegisterCanEvent
    object Success: RegisterCanEvent
    object None : RegisterCanEvent
}