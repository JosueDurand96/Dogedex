package com.durand.dogedex.data.response.can_perdido


import com.google.gson.annotations.SerializedName

data class ListCanPerdido(
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("fechaHoraCreacion")
    var fechaHoraCreacion: String,
    @SerializedName("fechaHoraModificacion")
    var fechaHoraModificacion: String,
    @SerializedName("fechaPerdida")
    var fechaPerdida: String,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idPerdido")
    var idPerdido: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("latitud")
    var latitud: String,
    @SerializedName("longitud")
    var longitud: String
)