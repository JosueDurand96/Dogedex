package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class RegisterCanPerdidoRequest(
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("especie")
    var especie: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("raza")
    var raza: String,
    @SerializedName("tamanio")
    var tamanio: String,
    @SerializedName("caracter")
    var caracter: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("pelaje")
    var pelaje: String,
    @SerializedName("foto")
    var foto: String,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("nombreUsuario")
    var nombreUsuario: String,
    @SerializedName("apellidoUsuario")
    var apellidoUsuario: String,
    @SerializedName("fechaPerdida")
    var fechaPerdida: String,
    @SerializedName("lugarPerdida")
    var lugarPerdida: String,
    @SerializedName("comentario")
    var comentario: String,
)