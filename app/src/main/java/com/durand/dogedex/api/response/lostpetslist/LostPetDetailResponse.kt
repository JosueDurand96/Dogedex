package com.durand.dogedex.api.response.lostpetslist


import com.squareup.moshi.Json

data class LostPetDetailResponse(
    @Json(name = "descripcion")
    val description: String,
    @Json(name = "estado")
    val state: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String,
    @Json(name = "fechaPerdida")
    val lostDate: String,
    @Json(name = "idMascota")
    val petId: Int,
    @Json(name = "idPerdido")
    val lostId: Int,
    @Json(name = "idUsuario")
    val userId: Int,
    @Json(name = "latitud")
    val latitude: String,
    @Json(name = "longitud")
    val longitude: String
)