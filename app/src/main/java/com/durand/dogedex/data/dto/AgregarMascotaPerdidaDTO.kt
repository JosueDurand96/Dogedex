@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.dto


import com.squareup.moshi.Json

data class AgregarMascotaPerdidaDTO(
    @field:Json(name = "descripcion")
    val descripcion: String,
    @field:Json(name = "estado")
    val estado: String,
    @field:Json(name = "fechaPerdida")
    val fechaPerdida: String,
    @field:Json(name = "idMascota")
    val idMascota: Int,
    @field:Json(name = "idUsuario")
    val idUsuario: Int,
    @field:Json(name = "latitud")
    val latitud: String,
    @field:Json(name = "longitud")
    val longitud: String
)