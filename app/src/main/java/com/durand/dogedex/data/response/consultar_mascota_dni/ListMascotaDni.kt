package com.durand.dogedex.data.response.consultar_mascota_dni


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListMascotaDni(
    @SerializedName("apellidos")
    var apellidos: String,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("nombreMascota")
    var nombreMascota: String,
    @SerializedName("numeroDocumento")
    var numeroDocumento: String
): Serializable