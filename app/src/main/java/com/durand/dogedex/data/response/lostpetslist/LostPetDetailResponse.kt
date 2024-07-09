@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.response.lostpetslist


import com.squareup.moshi.Json

data class LostPetDetailResponse(
    @field:Json(name = "descripcion")
    val description: String,
    @field:Json(name = "estado")
    val state: String,
    @field:Json(name = "fechaHoraCreacion")
    val createdAt: String,
    @field:Json(name = "fechaHoraModificacion")
    val updatedAt: String,
    @field:Json(name = "fechaPerdida")
    val lostDate: String,
    @field:Json(name = "idMascota")
    val petId: Int,
    @field:Json(name = "idPerdido")
    val lostId: Int,
    @field:Json(name = "idUsuario")
    val userId: Int,
    @field:Json(name = "latitud")
    val latitude: String,
    @field:Json(name = "longitud")
    val longitude: String
)