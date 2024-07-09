package com.durand.dogedex.ui.user_fragment.can_report_lost

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewRepository
import com.durand.dogedex.data.response.lostpetslist.LostPetDetailResponse
import kotlinx.coroutines.launch

class CanReportLostViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private val _list = MutableLiveData<List<LostPetDetailResponse>>()
    val list: LiveData<List<LostPetDetailResponse>> = _list

    val loading = MutableLiveData(false)

    init {
        executeGetLostPets()
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun executeGetLostPets() = viewModelScope.launch {
        try {
            loading.postValue(true)
            when (val res = repository.getLostPets()) {
                is ApiResponseStatus.Success -> {
                    _list.postValue(res.data)
                }

                else -> {}
            }
        } finally {
            loading.postValue(false)
        }
    }

}