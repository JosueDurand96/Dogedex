package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName

data class ListarCanResponse(
    @SerializedName("id")
    var id: Int,
    @SerializedName("nombre")
    var nombre: Int,
    @SerializedName("fechaNacimiento")
    var fechaNacimiento: String,
    @SerializedName("especie")
    var especie: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("raza")
    var raza: Int,
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
    @SerializedName("obtencion")
    var obtencion: String,
    @SerializedName("tenencia")
    var tenencia: String,
    @SerializedName("foto")
    var foto: String,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("nombreUsuario")
    var nombreUsuario: String,
    @SerializedName("apellidoUsuario")
    var apellidoUsuario: String,
)