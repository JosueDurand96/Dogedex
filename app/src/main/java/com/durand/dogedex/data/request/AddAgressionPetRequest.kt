package com.durand.dogedex.data.request


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddAgressionPetRequest(
    @SerializedName("color")
    var color: String,
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("fechaHora")
    var fechaHora: String,
    @SerializedName("fechaHoraCreacion")
    var fechaHoraCreacion: String,
    @SerializedName("fechaHoraModificacion")
    var fechaHoraModificacion: Any,
    @SerializedName("idAgresion")
    var idAgresion: Any,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("latitud")
    var latitud: String,
    @SerializedName("longitud")
    var longitud: String,
    @SerializedName("nombreApellido")
    var nombreApellido: String,
    @SerializedName("raza")
    var raza: Int,
    @SerializedName("tamano")
    var tamano: String
): Serializable