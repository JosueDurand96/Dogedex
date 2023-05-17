package com.durand.dogedex.data.dto


import com.google.gson.annotations.SerializedName

data class RegisterCanRequest(
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("fechaNacimiento")
    var fechaNacimiento: String,
    @SerializedName("especie")
    var especie: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("idRaza")
    var idRaza: Int,
    @SerializedName("tamano")
    var tamano: String,
    @SerializedName("caracter")
    var caracter: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("pelaje")
    var pelaje: String,
    @SerializedName("esterilizado")
    var esterilizado: String,
    @SerializedName("distrito")
    var distrito: String,
    @SerializedName("modoObtencion")
    var modoObtencion: String,
    @SerializedName("razonTenencia")
    var razonTenencia: String,
)