package com.durand.dogedex.data.dto


import com.squareup.moshi.Json

data class ConsultarMascotaDTO(
    @field:Json(name = "idUsuario")
    val idUsuario: Int
)