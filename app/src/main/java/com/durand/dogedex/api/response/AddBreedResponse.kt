package com.durand.dogedex.api.response


import com.squareup.moshi.Json

data class AddBreedResponse(
    @Json(name = "caracter")
    val character: String,
    @Json(name = "descripcion")
    val description: String,
    @Json(name = "esPeligrosa")
    val isDangerous: String,
    @Json(name = "estado")
    val state: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String,
    @Json(name = "idRaza")
    val idRaza: Any,
    @Json(name = "nombre")
    val name: String,
    @Json(name = "procedencia")
    val origin: String,
    @Json(name = "tamano")
    val size: String
)