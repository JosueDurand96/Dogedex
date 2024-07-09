package com.durand.dogedex.data.response.registar_can


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterCanResponse(
    @SerializedName("caracter")
    val caracter: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("distrito")
    val distrito: String,
    @SerializedName("especie")
    val especie: String,
    @SerializedName("estado")
    val estado: String,
    @SerializedName("esterilizado")
    val esterilizado: String,
    @SerializedName("fechaHoraCreacion")
    val fechaHoraCreacion: String,
    @SerializedName("fechaHoraModificacion")
    val fechaHoraModificacion: String,
    @SerializedName("fechaNacimiento")
    val fechaNacimiento: String,
    @SerializedName("foto")
    val foto: String,
    @SerializedName("genero")
    val genero: String,
    @SerializedName("idFoto")
    val idFoto: Any,
    @SerializedName("idMascota")
    val idMascota: Int,
    @SerializedName("idRaza")
    val idRaza: Int,
    @SerializedName("idUsuario")
    val idUsuario: Int,
    @SerializedName("latitud")
    val latitud: String,
    @SerializedName("longitud")
    val longitud: String,
    @SerializedName("modoObtencion")
    val modoObtencion: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("pelaje")
    val pelaje: String,
    @SerializedName("peligroso")
    val peligroso: String,
    @SerializedName("razonTenencia")
    val razonTenencia: String,
    @SerializedName("tamano")
    val tamano: String
): Serializable