@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.dto


import com.squareup.moshi.Json

data class AddDogDTO(
    @Json(name = "caracter")
    val character: String,
    @Json(name = "color")
    val color: String,
    @Json(name = "distrito")
    val district: String,
    @Json(name = "especie")
    val species: String,
    @Json(name = "estado")
    val state: Int,
    @Json(name = "esterilizado")
    val sterilized: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String? = null,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String? = null,
    @Json(name = "fechaNacimiento")
    val dateOfBirth: String,
    @Json(name = "genero")
    val gender: String,
    @Json(name = "idFoto")
    val photoId: Int,
    @Json(name = "idMascota")
    val petId: Int? = null,
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