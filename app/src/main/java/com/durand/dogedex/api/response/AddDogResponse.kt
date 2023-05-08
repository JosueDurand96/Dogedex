package com.durand.dogedex.api.response


import com.squareup.moshi.Json

data class AddDogResponse(
    @Json(name = "caracter")
    val character: String,
    @Json(name = "color")
    val color: String,
    @Json(name = "distrito")
    val district: String,
    @Json(name = "especie")
    val species: String,
    @Json(name = "estado")
    val state: String,
    @Json(name = "esterilizado")
    val sterilized: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String?,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String?,
    @Json(name = "fechaNacimiento")
    val dateOfBirth: String,
    @Json(name = "genero")
    val gender: String,
    @Json(name = "idFoto")
    val photoId: Int,
    @Json(name = "idMascota")
    val petId: Int?,
    @Json(name = "idRaza")
    val idRaza: Int,
    @Json(name = "idUsuario")
    val userId: Int,
    @Json(name = "modoObtencion")
    val obtainMode: String,
    @Json(name = "nombre")
    val name: String,
    @Json(name = "pelaje")
    val coat: String,
    @Json(name = "razonTenencia")
    val tenancyReason: String,
    @Json(name = "tamano")
    val size: String
)