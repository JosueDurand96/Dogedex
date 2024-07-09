package com.durand.dogedex.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.AuthRepository
import com.durand.dogedex.data.User
import com.durand.dogedex.data.dto.AddLoginDTO
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.LoginMasterResponse
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _status = MutableLiveData<ApiResponseStatus<User>>()
    val status: LiveData<ApiResponseStatus<User>>
        get() = _status

    private val authRepository = AuthRepository()


    private val _login = MutableLiveData<LoginMasterResponse>()

    val login: LiveData<LoginMasterResponse> = _login

    fun onLogin(addLoginDTO: AddLoginDTO) = viewModelScope.launch {
        try {
            when (val res: ApiResponseStatus<LoginMasterResponse> = repository.getLogin(addLoginDTO)) {
                is ApiResponseStatus.Error -> {
                    Log.d("josue", "Error")
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("josue", "Loading")
                }
                is ApiResponseStatus.Success -> {
                    Log.d("josue", "Success")
                    _login.postValue(res.data!!)
                    Log.d("josue", "list ${res.data}")
                }
            }
        } catch (e: Exception) {

        }
    }

    fun login(email: String, password: String) {
        Log.d("josue", "email: $email")
        Log.d("josue", "password: $password")
        viewModelScope.launch {
            _status.value = ApiResponseStatus.Loading()
            handleResponseStatus(authRepository.login(email, password))
        }
    }

    fun onSignUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModelScope.launch {
            _status.value = ApiResponseStatus.Loading()
            handleResponseStatus(authRepository.signUp(email, password, passwordConfirmation))
        }
    }

    private fun handleResponseStatus(apiResponseStatus: ApiResponseStatus<User>) {
        if (apiResponseStatus is ApiResponseStatus.Success) {
            _user.value = apiResponseStatus.data!!
        }
        _status.value = apiResponseStatus
    }


}