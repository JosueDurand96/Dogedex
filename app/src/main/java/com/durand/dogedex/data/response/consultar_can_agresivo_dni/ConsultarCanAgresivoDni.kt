package com.durand.dogedex.data.response.consultar_can_agresivo_dni


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsultarCanAgresivoDni(
    @SerializedName("apellidos")
    var apellidos: String,
    @SerializedName("caracter")
    var caracter: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("descripcionAgresion")
    var descripcionAgresion: String,
    @SerializedName("descripcionRaza")
    var descripcionRaza: String,
    @SerializedName("fechaHora")
    var fechaHora: String,
    @SerializedName("genero")
    var genero: String,
    @SerializedName("idAgresion")
    var idAgresion: Int,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idRaza")
    var idRaza: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("nombreMascota")
    var nombreMascota: String,
    @SerializedName("nombreRaza")
    var nombreRaza: Any,
    @SerializedName("numeroDocumento")
    var numeroDocumento: String,
    @SerializedName("pelaje")
    var pelaje: String,
    @SerializedName("tamano")
    var tamano: String
): Serializable