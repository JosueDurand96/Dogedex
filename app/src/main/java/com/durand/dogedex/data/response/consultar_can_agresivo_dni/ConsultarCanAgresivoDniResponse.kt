package com.durand.dogedex.data.response.consultar_can_agresivo_dni


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsultarCanAgresivoDniResponse(
    @SerializedName("lista")
    var lista: List<ConsultarCanAgresivoDni>
): Serializable