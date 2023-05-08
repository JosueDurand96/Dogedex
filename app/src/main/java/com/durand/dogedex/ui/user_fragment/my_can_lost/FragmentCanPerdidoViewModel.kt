package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.dto.AddBreedDTO
import com.durand.dogedex.api.dto.AddPetDTO
import com.durand.dogedex.api.dto.AddUserDTO
import com.durand.dogedex.api.repository.NewRepository
import kotlinx.coroutines.launch

class FragmentCanPerdidoViewModel(
    private val repository: NewRepository = NewRepository()
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    init {
//        executeGetDangerousDogs()
//        executeAddPet()
//        executeAddUser()
//        executeGetLostPets()
        executeAddBreed()
    }

    fun executeGetDangerousDogs() = viewModelScope.launch {
        when (val res = repository.getDangerousDogs()) {
            is ApiResponseStatus.Error -> {}
            is ApiResponseStatus.Loading -> {}
            is ApiResponseStatus.Success -> {
                Log.e("XX", "${res.data}")
            }
        }
    }

    fun executeAddPet() = viewModelScope.launch {
        val pet = AddPetDTO(
            userId = 9,
            name = "Pepe",
            dateOfBirth = "2023-05-04",
            species = "perro",
            gender = "macho",
            idRaza = 28,
            size = "grande",
            character = "Docil",
            color = "blanco",
            coat = "corto",
            sterilized = "0",
            district = "San Borja",
            obtainMode = "Regalado",
            tenancyReason = "Mascota",
            photoId = 25,
            state = 1
        )
        when (val res = repository.addPet(pet)) {
            is ApiResponseStatus.Error -> {}
            is ApiResponseStatus.Loading -> {}
            is ApiResponseStatus.Success -> {
                Log.e("XX", res.data.toString())
            }
        }
    }

    fun executeAddUser() = viewModelScope.launch {
        val user = AddUserDTO(
            user = "croncal",
            password = "1104Bebe",
            userState = "1",
            name = "Carmen",
            lastname = "Roncal",
            email = "croncalcarrasco@hotmail.com",
            phone = "88888888",
            docType = "dni",
            dni = 22222222,
            district = "Comas",
            address = "Av. Tupac 342",
            typeHouse = "Casa",
            dataProcessing = "0"
        )
        when (val res = repository.addUser(user)) {
            is ApiResponseStatus.Error -> {}
            is ApiResponseStatus.Loading -> {}
            is ApiResponseStatus.Success -> {
                Log.e("XX", res.data.toString())
            }
        }
    }

    fun executeGetLostPets() = viewModelScope.launch {
        when (val res = repository.getLostPets()) {
            is ApiResponseStatus.Error -> {}
            is ApiResponseStatus.Loading -> {}
            is ApiResponseStatus.Success -> {
                Log.e("XX", res.data.toString())
            }
        }
    }

    fun executeAddBreed() = viewModelScope.launch {
        val breed = AddBreedDTO(
            name = "prueba 1",
            description = "mmmm1",
            origin = "aaaaa",
            size = "bb",
            character = "xx",
            isDangerous = "0",
            state = "1",
        )
        when (val res = repository.addBreed(breed)) {
            is ApiResponseStatus.Error -> {}
            is ApiResponseStatus.Loading -> {}
            is ApiResponseStatus.Success -> {
                Log.e("XX", res.data.toString())
            }
        }
    }

}