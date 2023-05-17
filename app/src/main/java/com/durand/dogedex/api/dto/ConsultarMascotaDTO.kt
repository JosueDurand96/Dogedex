package com.durand.dogedex.api.dto


import com.squareup.moshi.Json

data class ConsultarMascotaDTO(
    @field:Json(name = "idUsuario")
    val idUsuario: Int
)