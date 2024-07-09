package com.durand.dogedex.data.response.consultarmascotas


import com.squareup.moshi.Json

data class ConsultarMascotasResponse(
    @field:Json(name = "lista")
    val lista: List<DetalleMascota>
)