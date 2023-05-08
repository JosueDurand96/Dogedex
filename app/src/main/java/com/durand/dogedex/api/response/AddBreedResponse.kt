@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.response


import com.squareup.moshi.Json

data class AddBreedResponse(
    @field:Json(name = "caracter")
    val character: String,
    @field:Json(name = "descripcion")
    val description: String,
    @field:Json(name = "esPeligrosa")
    val isDangerous: String,
    @field:Json(name = "estado")
    val state: String,
    @field:Json(name = "fechaHoraCreacion")
    val createdAt: String,
    @field:Json(name = "fechaHoraModificacion")
    val updatedAt: String,
    @field:Json(name = "idRaza")
    val idRaza: Any,
    @field:Json(name = "nombre")
    val name: String,
    @field:Json(name = "procedencia")
    val origin: String,
    @field:Json(name = "tamano")
    val size: String
)