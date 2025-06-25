package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class RegisterCanRequest(
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("fechaNacimiento")
    var fechaNacimiento: String,
    @SerializedName("especie")
    var especie: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("raza")
    var raza: String,
    @SerializedName("tamanio")
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
    @SerializedName("foto")
    var foto: String,
    @SerializedName("idUsuario")
    var idUsuario: Int,
)