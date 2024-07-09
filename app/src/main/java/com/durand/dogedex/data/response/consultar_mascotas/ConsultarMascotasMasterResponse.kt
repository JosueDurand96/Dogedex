package com.durand.dogedex.data.response.consultar_mascotas


import com.durand.dogedex.data.response.consultar_mascotas.ConsultarDetalleMascota
import com.google.gson.annotations.SerializedName

data class ConsultarMascotasMasterResponse(
    @SerializedName("lista")
    var lista: List<ConsultarDetalleMascota>
)