package com.durand.dogedex.api.dto


import com.squareup.moshi.Json

data class AddBreedDTO(
    @Json(name = "caracter")
    val character: String,
    @Json(name = "descripcion")
    val description: String,
    @Json(name = "esPeligrosa")
    val isDangerous: String,
    @Json(name = "estado")
    val state: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String? = null,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String? = null,
    @Json(name = "idRaza")
    val idRaza: Int? = null,
    @Json(name = "nombre")
    val name: String,
    @Json(name = "procedencia")
    val origin: String,
    @Json(name = "tamano")
    val size: String
)