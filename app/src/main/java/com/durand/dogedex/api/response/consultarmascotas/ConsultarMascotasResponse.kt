package com.durand.dogedex.api.response.consultarmascotas


import com.squareup.moshi.Json

data class ConsultarMascotasResponse(
    @field:Json(name = "lista")
    val lista: List<DetalleMascota>
)