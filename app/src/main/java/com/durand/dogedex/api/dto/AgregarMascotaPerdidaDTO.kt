@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.dto


import com.squareup.moshi.Json

data class AgregarMascotaPerdidaDTO(
    @field:Json(name = "descripcion")
    val descripcion: String,
    @field:Json(name = "estado")
    val estado: String,
    @field:Json(name = "fechaPerdida")
    val fechaPerdida: String,
    @field:Json(name = "idMascota")
    val idMascota: Long,
    @field:Json(name = "idUsuario")
    val idUsuario: Long,
    @field:Json(name = "latitud")
    val latitud: String,
    @field:Json(name = "longitud")
    val longitud: String
)