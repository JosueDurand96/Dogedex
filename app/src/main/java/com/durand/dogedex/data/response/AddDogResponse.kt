@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.response


import com.squareup.moshi.Json

data class AddDogResponse(
    @field:Json(name = "caracter")
    val character: String,
    @field:Json(name = "color")
    val color: String,
    @field:Json(name = "distrito")
    val district: String,
    @field:Json(name = "especie")
    val species: String,
    @field:Json(name = "estado")
    val state: String,
    @field:Json(name = "esterilizado")
    val sterilized: String,
    @field:Json(name = "fechaHoraCreacion")
    val createdAt: String?,
    @field:Json(name = "fechaHoraModificacion")
    val updatedAt: String?,
    @field:Json(name = "fechaNacimiento")
    val dateOfBirth: String,
    @field:Json(name = "genero")
    val gender: String,
    @field:Json(name = "idFoto")
    val photoId: Int,
    @field:Json(name = "idMascota")
    val petId: Int?,
    @field:Json(name = "idRaza")
    val idRaza: Int,
    @field:Json(name = "idUsuario")
    val userId: Int,
    @field:Json(name = "modoObtencion")
    val obtainMode: String,
    @field:Json(name = "nombre")
    val name: String,
    @field:Json(name = "pelaje")
    val coat: String,
    @field:Json(name = "razonTenencia")
    val tenancyReason: String,
    @field:Json(name = "tamano")
    val size: String
)