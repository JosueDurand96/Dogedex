package com.durand.dogedex.data.response.consultar_agresiones_por_mascota


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsultarAgresionesPorMascota(
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("fechaHora")
    var fechaHora: String,
    @SerializedName("fechaHoraCreacion")
    var fechaHoraCreacion: String,
    @SerializedName("fechaHoraModificacion")
    var fechaHoraModificacion: String,
    @SerializedName("idAgresion")
    var idAgresion: Int,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int
): Serializable