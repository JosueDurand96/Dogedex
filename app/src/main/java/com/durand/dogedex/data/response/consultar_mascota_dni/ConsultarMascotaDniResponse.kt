package com.durand.dogedex.data.response.consultar_mascota_dni


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsultarMascotaDniResponse(
    @SerializedName("lista")
    var lista: List<ListMascotaDni>
): Serializable