package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id")
    var id: Int,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("apellido")
    var apellido: String,
    @SerializedName("numeroDocumento")
    var numeroDocumento: String,
    @SerializedName("direccion")
    var direccion: String,
    @SerializedName("distrito")
    var distrito: String,
    @SerializedName("correo")
    var correo: String,
    @SerializedName("token")
    var token: String,
)