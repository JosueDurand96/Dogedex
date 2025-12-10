package com.durand.dogedex.data.request


import com.google.gson.annotations.SerializedName

data class AgregarMascotaRequest(
    @SerializedName("caracter")
    val caracter: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("distrito")
    val distrito: String,
    @SerializedName("especie")
    val especie: String,
    @SerializedName("estado")
    val estado: Int,
    @SerializedName("esterilizado")
    val esterilizado: String,
    @SerializedName("fechaNacimiento")
    val fechaNacimiento: String,
    @SerializedName("foto")
    val foto: String,
    @SerializedName("genero")
    val genero: String,
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
    val peligroso: Int,
    @SerializedName("razonTenencia")
    val razonTenencia: String,
    @SerializedName("tamanio")
    val tamanio: String
)