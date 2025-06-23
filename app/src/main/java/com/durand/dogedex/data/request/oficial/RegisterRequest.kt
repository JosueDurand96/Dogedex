package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("apellidos")
    var apellidos: String,
    @SerializedName("numeroDocumento")
    var numeroDocumento: String,
    @SerializedName("direccion")
    var direccion: String,
    @SerializedName("distrito")
    var distrito: String,
    @SerializedName("correo")
    var correo: String,
    @SerializedName("clave")
    var clave: String,
)