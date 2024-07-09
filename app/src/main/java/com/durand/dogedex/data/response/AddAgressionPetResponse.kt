package com.durand.dogedex.data.response


import com.google.gson.annotations.SerializedName

data class AddAgressionPetResponse(
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
    var idAgresion: Int,
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
)