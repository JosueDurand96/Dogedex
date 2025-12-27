package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListarCanResponse(
    @SerializedName("id")
    var id: Long,
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
    var tamanio: String,
    @SerializedName("caracter")
    var caracter: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("pelaje")
    var pelaje: String,
    @SerializedName("esterelizado")
    var esterelizado: String,
    @SerializedName("distrito")
    var distrito: String,
    @SerializedName("obtencion")
    var obtencion: String,
    @SerializedName("tenencia")
    var tenencia: String,
    @SerializedName("foto")
    var foto: String,
    @SerializedName("idUsuario")
    var idUsuario: Long,
    @SerializedName("nombreUsuario")
    var nombreUsuario: String,
    @SerializedName("apellidoUsuario")
    var apellidoUsuario: String,
    @SerializedName("latitud")
    var latitud: Double? = null,
    @SerializedName("longitud")
    var longitud: Double? = null,
): Serializable