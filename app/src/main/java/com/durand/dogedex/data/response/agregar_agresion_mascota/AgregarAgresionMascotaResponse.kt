package com.durand.dogedex.data.response.agregar_agresion_mascota


import com.google.gson.annotations.SerializedName

data class AgregarAgresionMascotaResponse(
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("fechaHora")
    var fechaHora: String,
    @SerializedName("fechaHoraCreacion")
    var fechaHoraCreacion: Any,
    @SerializedName("fechaHoraModificacion")
    var fechaHoraModificacion: Any,
    @SerializedName("idAgresion")
    var idAgresion: Int,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int
)