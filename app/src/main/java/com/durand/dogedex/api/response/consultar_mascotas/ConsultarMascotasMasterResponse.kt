package com.durand.dogedex.api.response.consultar_mascotas


import com.google.gson.annotations.SerializedName

data class ConsultarMascotasMasterResponse(
    @SerializedName("lista")
    var lista: List<ConsultarDetalleMascota>
)