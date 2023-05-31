package com.durand.dogedex.data.response.consultar_agresiones_por_mascota


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsultarAgresionesPorMascotaResponse(
    @SerializedName("lista")
    var lista: List<ConsultarAgresionesPorMascota>
): Serializable