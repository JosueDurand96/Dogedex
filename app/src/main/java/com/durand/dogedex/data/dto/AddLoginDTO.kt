package com.durand.dogedex.data.dto

import com.squareup.moshi.Json

data class AddLoginDTO(
    @field:Json(name = "usuario")
    val usuario: String,
    @field:Json(name = "contrasena")
    val contrasena: String
)