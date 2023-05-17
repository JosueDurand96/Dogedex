package com.durand.dogedex.api.response.registar_can


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterCanResponse(
    @SerializedName("caracter")
    var caracter: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("distrito")
    var distrito: String,
    @SerializedName("especie")
    var especie: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("esterilizado")
    var esterilizado: String,
    @SerializedName("fechaHoraCreacion")
    var fechaHoraCreacion: Any,
    @SerializedName("fechaHoraModificacion")
    var fechaHoraModificacion: Any,
    @SerializedName("fechaNacimiento")
    var fechaNacimiento: String,
    @SerializedName("foto")
    var foto: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("idFoto")
    var idFoto: Int,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idRaza")
    var idRaza: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("modoObtencion")
    var modoObtencion: String,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("pelaje")
    var pelaje: String,
    @SerializedName("razonTenencia")
    var razonTenencia: String,
    @SerializedName("tamano")
    var tamano: String
) : Serializable