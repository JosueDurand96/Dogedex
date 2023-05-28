package com.durand.dogedex.data.Request


import com.google.gson.annotations.SerializedName

data class AddAggressionPetRequest(
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("estado")
    var estado: String,
    @SerializedName("fechaHora")
    var fechaHora: String,
    @SerializedName("idMascota")
    var idMascota: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int
)